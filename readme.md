

# Online Complaint Registration and Tracking System

A full-stack web application that allows users to register complaints, track complaint status using a unique Complaint ID, and enables administrators to manage complaints through a structured workflow.

**Tech Stack:** HTML, CSS, JavaScript, Java Servlets (Jakarta), JSP, MySQL, JDBC, Apache Tomcat 11, Maven

---

## Features

### User

* Register complaint (name, email, category, description)
* Client-side + server-side validation
* Unique Complaint ID generation
* Track complaint status using Complaint ID

### Admin

* Secure admin login with session management
* View all complaints in dashboard
* Update complaint status and remarks
* Status workflow enforcement: Pending → In Progress → Resolved
* Audit logging of all updates

---

## Project Structure

```txt
ComplaintSystem/
├── pom.xml
|── src/data
|   |── source.sql
└── src/main/
    ├── java/com/complaint/
    │   ├── util/DBConnection.java
    │   └── servlets/
    │       ├── RegisterComplaintServlet.java
    │       ├── TrackComplaintServlet.java
    │       ├── AdminLoginServlet.java
    │       ├── AdminDashboardServlet.java
    │       ├── UpdateComplaintServlet.java
    │       └── LogoutServlet.java
    └── webapp/
        ├── *.html, *.jsp
        ├── css/style.css
        └── WEB-INF/web.xml
```

---

## Database Setup (MySQL)

Run the following script:

```sql
CREATE DATABASE complaint_db;
USE complaint_db;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admins (
  admin_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(64) NOT NULL
);

CREATE TABLE complaints (
  complaint_id VARCHAR(25) PRIMARY KEY,
  user_id INT NOT NULL,
  category VARCHAR(50) NOT NULL,
  description TEXT NOT NULL,
  status ENUM('Pending','In Progress','Resolved') DEFAULT 'Pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  resolved_at TIMESTAMP NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE complaint_updates (
  update_id INT AUTO_INCREMENT PRIMARY KEY,
  complaint_id VARCHAR(25) NOT NULL,
  admin_id INT NOT NULL,
  old_status VARCHAR(20),
  new_status VARCHAR(20),
  remarks VARCHAR(255),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id),
  FOREIGN KEY (admin_id) REFERENCES admins(admin_id)
);

-- default admin: admin / admin123
INSERT INTO admins(username, password_hash)
VALUES('admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
```

Update database credentials in:

`src/main/java/com/complaint/util/DBConnection.java`

---

## Build and Run

### 1. Build WAR

```bash
mvn clean package
```

### 2. Deploy to Tomcat

Copy:

```txt
target/ComplaintSystem.war
```

To:

```txt
C:\apache-tomcat-11.0.18\webapps\
```

Restart Tomcat.

---

## Application URLs

* Register Complaint:
  `http://localhost:8080/ComplaintSystem/registerComplaint.html`

* Track Complaint:
  `http://localhost:8080/ComplaintSystem/trackComplaint.html`

* Admin Login:
  `http://localhost:8080/ComplaintSystem/adminLogin.html`

* Admin Dashboard:
  `http://localhost:8080/ComplaintSystem/adminDashboard`

* Logout:
  `http://localhost:8080/ComplaintSystem/logout`

---

## Default Admin Credentials

* Username: `admin`
* Password: `admin123`

---

## Authors
* Name: Nithyasree K
* RegNo: 3122235001092
