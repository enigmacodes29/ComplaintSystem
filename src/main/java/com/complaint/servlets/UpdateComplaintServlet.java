package com.complaint.servlets;

import com.complaint.util.DBConnection;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/updateComplaint")
public class UpdateComplaintServlet extends HttpServlet {

    private boolean validTransition(String oldStatus, String newStatus) {
        if (oldStatus.equals("Pending") && newStatus.equals("In Progress")) return true;
        if (oldStatus.equals("In Progress") && newStatus.equals("Resolved")) return true;
        if (oldStatus.equals(newStatus)) return true; // allow no change
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect("adminLogin.html");
            return;
        }

        int adminId = (int) session.getAttribute("adminId");

        String complaintId = request.getParameter("complaintId");
        String newStatus = request.getParameter("newStatus");
        String remarks = request.getParameter("remarks");

        if (complaintId == null || newStatus == null || remarks == null ||
                remarks.trim().length() < 3) {
            response.getWriter().println("Invalid update.");
            return;
        }

        Connection con = DBConnection.getConnection();

        try {
            // 1) Get current status
            PreparedStatement psOld = con.prepareStatement(
                    "SELECT status FROM complaints WHERE complaint_id=?"
            );
            psOld.setString(1, complaintId);
            ResultSet rsOld = psOld.executeQuery();

            if (!rsOld.next()) {
                response.getWriter().println("Complaint not found.");
                return;
            }

            String oldStatus = rsOld.getString("status");

            // 2) Validate status transition
            if (!validTransition(oldStatus, newStatus)) {
                response.getWriter().println("Invalid status transition.");
                return;
            }

            // 3) Update complaints table
            PreparedStatement psUpdate;

            if (newStatus.equals("Resolved")) {
                psUpdate = con.prepareStatement(
                        "UPDATE complaints SET status=?, resolved_at=NOW() WHERE complaint_id=?"
                );
                psUpdate.setString(1, newStatus);
                psUpdate.setString(2, complaintId);
            } else {
                psUpdate = con.prepareStatement(
                        "UPDATE complaints SET status=? WHERE complaint_id=?"
                );
                psUpdate.setString(1, newStatus);
                psUpdate.setString(2, complaintId);
            }

            psUpdate.executeUpdate();

            // 4) Insert update log
            PreparedStatement psLog = con.prepareStatement(
                    "INSERT INTO complaint_updates(complaint_id, admin_id, old_status, new_status, remarks) " +
                            "VALUES(?,?,?,?,?)"
            );
            psLog.setString(1, complaintId);
            psLog.setInt(2, adminId);
            psLog.setString(3, oldStatus);
            psLog.setString(4, newStatus);
            psLog.setString(5, remarks);

            psLog.executeUpdate();

            response.sendRedirect("adminDashboard");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred.");
        }
    }
}