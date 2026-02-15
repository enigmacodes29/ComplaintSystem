package com.complaint.servlets;

import com.complaint.util.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/registerComplaint")
public class RegisterComplaintServlet extends HttpServlet {

    private String generateComplaintId() {
        Random r = new Random();
        int num = 1000 + r.nextInt(9000);
        return "CMP" + LocalDate.now().toString().replace("-", "") + "-" + num;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String category = request.getParameter("category");
        String description = request.getParameter("description");

        // SERVER SIDE VALIDATION
        if (name == null || email == null || category == null || description == null ||
                name.trim().length() < 3 || description.trim().length() < 10 ||
                !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            response.getWriter().println("Invalid input. Please go back.");
            return;
        }

        Connection con = DBConnection.getConnection();

        try {
            // 1) Check if user exists
            int userId = -1;
            PreparedStatement psUser = con.prepareStatement(
                    "SELECT user_id FROM users WHERE email=?"
            );
            psUser.setString(1, email);
            ResultSet rsUser = psUser.executeQuery();

            if (rsUser.next()) {
                userId = rsUser.getInt("user_id");
            } else {
                PreparedStatement psInsertUser = con.prepareStatement(
                        "INSERT INTO users(name,email) VALUES(?,?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                psInsertUser.setString(1, name);
                psInsertUser.setString(2, email);
                psInsertUser.executeUpdate();

                ResultSet keys = psInsertUser.getGeneratedKeys();
                if (keys.next()) {
                    userId = keys.getInt(1);
                }
            }

            // 2) Duplicate complaint prevention (same description + category within 24h)
            PreparedStatement psDup = con.prepareStatement(
                    "SELECT COUNT(*) AS cnt FROM complaints " +
                            "WHERE user_id=? AND category=? AND description=? " +
                            "AND created_at > NOW() - INTERVAL 1 DAY"
            );
            psDup.setInt(1, userId);
            psDup.setString(2, category);
            psDup.setString(3, description);

            ResultSet rsDup = psDup.executeQuery();
            if (rsDup.next() && rsDup.getInt("cnt") > 0) {
                response.getWriter().println("Duplicate complaint detected. Please track existing complaint.");
                return;
            }

            // 3) Insert complaint
            String complaintId = generateComplaintId();

            PreparedStatement psComplaint = con.prepareStatement(
                    "INSERT INTO complaints(complaint_id,user_id,category,description,status) VALUES(?,?,?,?,?)"
            );
            psComplaint.setString(1, complaintId);
            psComplaint.setInt(2, userId);
            psComplaint.setString(3, category);
            psComplaint.setString(4, description);
            psComplaint.setString(5, "Pending");

            psComplaint.executeUpdate();

            request.setAttribute("complaintId", complaintId);

            RequestDispatcher rd = request.getRequestDispatcher("complaintResult.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred.");
        }
    }
}