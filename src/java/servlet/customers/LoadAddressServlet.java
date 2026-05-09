package servlet.customers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Address;
import DAO.AddressDAO;
import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "LoadAddressServlet", urlPatterns = {"/LoadAddressServlet"})
public class LoadAddressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            if (userId == null) {
                response.setStatus(401);
                out.write("{\"error\":\"Not logged in\"}");
                return;
            }

            AddressDAO dao = new AddressDAO();
            List<Address> addressList = dao.getAddressesByUser(userId);

            Gson gson = new Gson();

            String json = gson.toJson(addressList);
            out.write(json);

        } catch (SQLException ex) {
            System.getLogger(LoadAddressServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }

}
