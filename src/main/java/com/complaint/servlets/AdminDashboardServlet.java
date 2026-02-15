package com.complaint.servlets;

import com.complaint.util.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/adminDashboard")
public class AdminDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect("adminLogin.html");
            return;
        }

        Connection con = DBConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT c.complaint_id, u.name, u.email, c.category, c.description, c.status, c.created_at " +
                            "FROM complaints c JOIN users u ON c.user_id=u.user_id " +
                            "ORDER BY c.created_at DESC"
            );

            ResultSet rs = ps.executeQuery();

            List<Map<String, String>> complaints = new ArrayList<>();

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("complaint_id", rs.getString("complaint_id"));
                row.put("name", rs.getString("name"));
                row.put("email", rs.getString("email"));
                row.put("category", rs.getString("category"));
                row.put("description", rs.getString("description"));
                row.put("status", rs.getString("status"));
                row.put("created_at", rs.getString("created_at"));
                complaints.add(row);
            }

            request.setAttribute("complaints", complaints);

            RequestDispatcher rd = request.getRequestDispatcher("adminDashboard.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred.");
        }
    }
}