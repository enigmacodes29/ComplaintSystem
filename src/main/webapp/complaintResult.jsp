<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>Complaint Submitted</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <h2>Complaint Submitted Successfully!</h2>

    <%
        String complaintId = (String) request.getAttribute("complaintId");
        if (complaintId == null) complaintId = "NOT GENERATED (Servlet failed)";
    %>

    <p><b>Your Complaint ID:</b> <%= complaintId %></p>
    <p><b>Status:</b> Pending</p>

    <p><a href="trackComplaint.html">Track Complaint</a></p>
    <p><a href="registerComplaint.html">Submit Another</a></p>
</div>

</body>
</html>