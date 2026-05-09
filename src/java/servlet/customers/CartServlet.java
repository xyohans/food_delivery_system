package servlet.customers;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;

@WebServlet(name = "CartServlet", urlPatterns = {"/CartServlet"})
public class CartServlet extends HttpServlet {

    static class CartItem {
        int    itemId;
        String name;
        double price;
        int    quantity;
    }

    static class CartReq {
        String action;   // "add", "remove", "clear"
        int    itemId;
        String name;
        double price;
        int    quantity;
    }

    // Prevent browser from caching cart data
    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setNoCacheHeaders(response);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.write("{\"error\":\"Not logged in\"}");
            return;
        }

        String cartJson = (String) session.getAttribute("cart");
        out.write(cartJson != null ? cartJson : "[]");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setNoCacheHeaders(response);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.write("{\"error\":\"Not logged in\"}");
            return;
        }

        try {
            BufferedReader reader = request.getReader();
            Gson    gson = new Gson();
            CartReq req  = gson.fromJson(reader, CartReq.class);

            String    cartJson = (String) session.getAttribute("cart");
            CartItem[] cart    = cartJson != null
                ? gson.fromJson(cartJson, CartItem[].class)
                : new CartItem[0];

            java.util.List<CartItem> list =
                new java.util.ArrayList<>(java.util.Arrays.asList(cart));

            switch (req.action == null ? "" : req.action) {
                case "add" -> {
                    CartItem found = list.stream()
                        .filter(i -> i.itemId == req.itemId).findFirst().orElse(null);
                    if (found != null) {
                        found.quantity += (req.quantity > 0 ? req.quantity : 1);
                    } else {
                        CartItem ni = new CartItem();
                        ni.itemId   = req.itemId;
                        ni.name     = req.name;
                        ni.price    = req.price;
                        ni.quantity = req.quantity > 0 ? req.quantity : 1;
                        list.add(ni);
                    }
                    out.write("{\"message\":\"Item added to cart\"}");
                }
                case "remove" -> {
                    list.removeIf(i -> i.itemId == req.itemId);
                    out.write("{\"message\":\"Item removed\"}");
                }
                case "clear" -> {
                    list.clear();
                    out.write("{\"message\":\"Cart cleared\"}");
                }
                default -> {
                    response.setStatus(400);
                    out.write("{\"error\":\"Unknown action\"}");
                    return;
                }
            }

            session.setAttribute("cart", gson.toJson(list));

        } catch (Exception e) {
            response.setStatus(500);
            out.write("{\"error\":\"Cart error\"}");
        }
    }
}
