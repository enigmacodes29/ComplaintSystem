<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        table { width: 100%; border-collapse: collapse; font-size: 14px; }
        th, td { border: 1px solid #ddd; padding: 8px; }
        th { background: #007bff; color: white; }
        .small { width: 110px; }
    </style>
</head>
<body>

<div class="container" style="width: 95%;">
    <h2>Admin Dashboard</h2>

    <p>Welcome, <b>${sessionScope.adminUser}</b> |
        <a href="logout">Logout</a>
    </p>

    <table>
        <tr>
            <th>Complaint ID</th>
            <th>User</th>
            <th>Email</th>
            <th>Category</th>
            <th>Description</th>
            <th>Status</th>
            <th>Created</th>
            <th class="small">Update</th>
        </tr>

        <%
            List<Map<String, String>> complaints =
                (List<Map<String, String>>) request.getAttribute("complaints");

            if (complaints != null) {
                for (Map<String, String> c : complaints) {
        %>
        <tr>
            <td><%= c.get("complaint_id") %></td>
            <td><%= c.get("name") %></td>
            <td><%= c.get("email") %></td>
            <td><%= c.get("category") %></td>
            <td><%= c.get("description") %></td>
            <td><%= c.get("status") %></td>
            <td><%= c.get("created_at") %></td>

            <td>
                <form action="updateComplaint" method="post">
                    <input type="hidden" name="complaintId" value="<%= c.get("complaint_id") %>">

                    <select name="newStatus" required>
                        <option value="">Select</option>
                        <option>Pending</option>
                        <option>In Progress</option>
                        <option>Resolved</option>
                    </select>

                    <input type="text" name="remarks" placeholder="Remarks" required>

                    <button type="submit">Update</button>
                </form>
            </td>
        </tr>

        <%
                }
            }
        %>
    </table>

</div>

</body>
</html>