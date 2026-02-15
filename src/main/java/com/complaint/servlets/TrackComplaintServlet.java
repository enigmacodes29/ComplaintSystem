package com.complaint.servlets;

import com.complaint.util.DBConnection;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/trackComplaint")
public class TrackComplaintServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String complaintId = request.getParameter("complaintId");

        if (complaintId == null || complaintId.trim().isEmpty()) {
            response.getWriter().println("Complaint ID required.");
            return;
        }

        Connection con = DBConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT c.*, u.name, u.email " +
                            "FROM complaints c JOIN users u ON c.user_id=u.user_id " +
                            "WHERE c.complaint_id=?"
            );
            ps.setString(1, complaintId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                request.setAttribute("complaintId", rs.getString("complaint_id"));
                request.setAttribute("name", rs.getString("name"));
                request.setAttribute("email", rs.getString("email"));
                request.setAttribute("category", rs.getString("category"));
                request.setAttribute("description", rs.getString("description"));
                request.setAttribute("status", rs.getString("status"));
                request.setAttribute("createdAt", rs.getString("created_at"));
                request.setAttribute("resolvedAt", rs.getString("resolved_at"));

                // Latest remarks
                PreparedStatement ps2 = con.prepareStatement(
                        "SELECT remarks FROM complaint_updates " +
                                "WHERE complaint_id=? ORDER BY updated_at DESC LIMIT 1"
                );
                ps2.setString(1, complaintId);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    request.setAttribute("remarks", rs2.getString("remarks"));
                } else {
                    request.setAttribute("remarks", "No remarks yet.");
                }

                RequestDispatcher rd = request.getRequestDispatcher("trackResult.jsp");
                rd.forward(request, response);

            } else {
                response.getWriter().println("Complaint ID not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred.");
        }
    }
}