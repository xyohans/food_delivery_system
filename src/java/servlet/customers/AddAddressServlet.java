/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet.customers;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

import DAO.AddressDAO;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AddAddressServlet", urlPatterns = {"/AddAddressServlet"})
public class AddAddressServlet extends HttpServlet {

    static class AddressReq {

        private String address;
        private String label;
        private boolean is_default;

        public String getAddress() {
            return address;
        }

        public String getLabel() {
            return label;
        }

        public boolean isIs_default() {
            return is_default;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // session
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            if (userId == null) {
                response.setStatus(401);
                out.write("{\"error\":\"Not logged in\"}");
                return;
            }

            // read JSON
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            AddressReq json = gson.fromJson(reader, AddressReq.class);

            if (json == null || json.getAddress() == null || json.getAddress().isEmpty()) {
                response.setStatus(400);
                out.write("{\"error\":\"Address required\"}");
                return;
            }

            // defaults
            String address = json.getAddress();
            String label = (json.getLabel() == null || json.getLabel().isEmpty())
                    ? "Other"
                    : json.getLabel();

            int isDefault = json.isIs_default() ? 1 : 0;

            // save
            AddressDAO dao = new AddressDAO();
            dao.addAddress(userId, label, address, isDefault);

            out.write("{\"message\":\"Address added\"}");

        } catch (Exception e) {
            response.setStatus(500);
            out.write("{\"error\":\"Server error\"}");
            e.printStackTrace();
        }
    }
}
