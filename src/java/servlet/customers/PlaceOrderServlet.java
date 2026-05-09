package servlet.customers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import com.google.gson.Gson;

import DAO.OrderDAO;
import DAO.MenuDAO;
import model.Address;
import DAO.AddressDAO;
import DAO.PaymentDAO;
import DAO.UserDAO;
import jakarta.servlet.http.HttpSession;
import model.request.ItemReq;
import model.request.OrderReq;
import java.io.BufferedReader;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = {"/PlaceOrderServlet"})
public class PlaceOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            OrderReq json = gson.fromJson(reader, OrderReq.class);

            if (json == null || json.getItems() == null || json.getItems().isEmpty()) {
                response.setStatus(400);
                out.write("{\"error\":\"No items in order\"}");
                return;
            }

            String paymentMethod = json.getPaymentMethod();
            String notes         = json.getNotes();
            int    addressId     = json.getAddressId();

            // Get customer id from session
            HttpSession session    = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(401);
                out.write("{\"error\":\"Not logged in\"}");
                return;
            }
            int customerId = (int) session.getAttribute("userId");

            List<ItemReq> items   = json.getItems();
            MenuDAO       menuDAO = new MenuDAO();
            double        total   = 0;

            // Calculate total from DB prices (never trust client-side prices)
            for (ItemReq item : items) {
                double price = menuDAO.getItemPrice(item.getItemId());
                total += price * item.getQuantity();
            }

            // Wallet payment — check and deduct balance
            UserDAO userDAO = new UserDAO();
            if ("wallet".equals(paymentMethod)) {
                double currentBalance = userDAO.getBalance(customerId);
                if (currentBalance < total) {
                    response.setStatus(400);
                    out.write(String.format(
                        "{\"error\":\"Insufficient balance! You need $%.2f more.\"}",
                        (total - currentBalance)));
                    return;
                }
                userDAO.deductBalance(customerId, total);
            }

            // Get delivery address text
            AddressDAO adao    = new AddressDAO();
            Address    a       = adao.getAddressById(addressId);
            if (a == null) {
                response.setStatus(400);
                out.write("{\"error\":\"Invalid address\"}");
                return;
            }
            String address = a.getAddress();

            // Insert order
            OrderDAO orderDao = new OrderDAO();
            int      orderId  = orderDao.placeOrder(customerId, addressId, address, total, notes);

            if (orderId == 0) {
                response.setStatus(500);
                out.write("{\"error\":\"Failed to create order\"}");
                return;
            }

            // Insert order items
            orderDao.insertOrderItems(orderId, items);

            // Insert payment record
            PaymentDAO paymentDao = new PaymentDAO();
            paymentDao.insertPayment(orderId, paymentMethod, total);

            // For card/wallet, mark order as 'paid' so admin can assign a chef
            if (!"cash".equals(paymentMethod)) {
                paymentDao.markOrderPaid(orderId);
            }

            out.write(String.format("{\"message\":\"Order #%d placed successfully!\", \"orderId\":%d}", orderId, orderId));

        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(500);
            out.write("{\"error\":\"Order failed: " + ex.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
