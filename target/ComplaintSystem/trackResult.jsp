<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Complaint Status</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <h2>Complaint Status</h2>

    <p><b>Complaint ID:</b> ${complaintId}</p>
    <p><b>Name:</b> ${name}</p>
    <p><b>Email:</b> ${email}</p>
    <p><b>Category:</b> ${category}</p>
    <p><b>Description:</b> ${description}</p>
    <p><b>Status:</b> ${status}</p>
    <p><b>Created At:</b> ${createdAt}</p>
    <p><b>Resolved At:</b> ${resolvedAt}</p>
    <p><b>Latest Remarks:</b> ${remarks}</p>

    <p><a href="trackComplaint.html">Track Another</a></p>
</div>

</body>
</html>