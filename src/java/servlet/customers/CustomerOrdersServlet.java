/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet.customers;

import DAO.OrderDAO;
import DAO.UserDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Order;

/**
 *
 * @author yohan
 */
@WebServlet(name = "CustomerOrdersServlet", urlPatterns = {"/CustomerOrdersServlet"})
public class CustomerOrdersServlet extends HttpServlet {

    // GET: Load orders that are NOT delivered or cancelled
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");

        try {
            OrderDAO dao = new OrderDAO();
            List<Order> all = dao.getOrdersByCustomer(userId);
            // Filter list to only show active items
            List<Order> active = all.stream()
                .filter(o -> !o.getStatus().equals("delivered") && !o.getStatus().equals("cancelled"))
                .toList();
            response.getWriter().write(new Gson().toJson(active));
        } catch (Exception e) { response.setStatus(500); }
    }

    // POST: Handle the Cancel and Refund logic[cite: 5, 7]
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        int userId = (int) request.getSession().getAttribute("userId");

        OrderDAO oDao = new OrderDAO();
        UserDAO uDao = new UserDAO();

        try {
            Order order = oDao.getOrderById(orderId);
            String oldStatus = order.getStatus();

            if (oDao.cancelOrder(orderId)) {
                // If it was already paid (Wallet/Card), refund the user
                if (oldStatus.equals("paid")) {
                    uDao.refundBalance(userId, order.getTotalAmount());
                }
                response.getWriter().write("{\"message\":\"Order cancelled and refunded!\"}");
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Too late! Chef is already cooking.\"}");
            }
        } catch (Exception e) { response.setStatus(500); }
    }
}