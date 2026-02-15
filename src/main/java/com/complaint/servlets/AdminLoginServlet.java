package com.complaint.servlets;

import com.complaint.util.DBConnection;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/adminLogin")
public class AdminLoginServlet extends HttpServlet {

    private String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            response.getWriter().println("Invalid login.");
            return;
        }

        try {
            String hashed = sha256(password);

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT admin_id FROM admins WHERE username=? AND password_hash=?"
            );
            ps.setString(1, username);
            ps.setString(2, hashed);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int adminId = rs.getInt("admin_id");

                HttpSession session = request.getSession(true);
                session.setAttribute("adminId", adminId);
                session.setAttribute("adminUser", username);

                response.sendRedirect("adminDashboard");
            } else {
                response.getWriter().println("Login failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred.");
        }
    }
}