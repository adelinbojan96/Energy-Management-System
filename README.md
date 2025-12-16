# Energy-Management-System

## Overview

The **Energy Management System** is a distributed web application that allows authenticated users to access, monitor, and manage smart energy metering devices.  
The system is implemented using a **microservice architecture**, where each service is independently deployable and containerized. It includes authentication, user management, and device management functionalities, as well as a role-based frontend interface for both administrators and clients.

---

## Architecture

The platform is composed of the following components:

| Component | Description | Port |
|------------|-------------|------|
| **Frontend (React)** | Web interface for administrators and clients | 5173 |
| **API Gateway** | Entry point that routes requests to the appropriate microservice | 8080 |
| **Auth Microservice** | Handles registration, login, and JWT-based authentication | 8083 |
| **User Microservice** | Manages user accounts and roles | 8081 |
| **Device Microservice** | Manages energy metering devices and their association with users | 8082 |
| **Traefik** | Reverse proxy and load balancer for internal routing between services | 80, 8080 (dashboard) |
| **PostgreSQL Databases** | Independent databases for Auth, User, and Device services | 5432–5434 |
| **RabbitMQ** | **Message Broker** for asynchronous communication (Assignment 2) | 5672 |
| **Monitoring Microservice** | Consumes measurement data and aggregates hourly consumption | 8084 |

All services communicate via REST APIs and are deployed on a shared Docker network.

---

## Features

### Administrator Role
- Create, read, update, and delete user accounts.
- Create, read, update, and delete devices.
- Assign devices to users.

### Client Role
- Log in using secure JWT authentication.
- View devices assigned to their account.
- **View energy consumption data on charts (Assignment 2).**

---

## Technologies Used

### Backend
- Java 21
- Spring Boot 3
- Spring Web and Spring Security
- Spring Data JPA (PostgreSQL)
- JWT (JSON Web Token) Authentication
- **Spring AMQP (RabbitMQ) for asynchronous messaging**
- Springdoc OpenAPI for Swagger documentation

### Frontend
- React.js (Vite)
- Axios for API communication
- Role-based routing (Admin and Client)
- **Recharts for data visualization**

### Infrastructure
- Docker and Docker Compose
- Traefik (Reverse Proxy and Load Balancer)
- **RabbitMQ (Message Broker)**
- PostgreSQL Databases

---

### Run device simulator

#### Commands:
    cd device_simulator
    mvn clean install exec:java -Dexec.mainClass="org.example.Simulator" -Dexec.args="--device.id=<YOUR_DEVICE_UUID>"

last device uuid = d56fc032-6c3d-463a-ad63-2da949a87425

---

### Testing the alert system using a local alert

#### Command
    curl -X GET http://localhost:8089/chat/trigger-alert

##  Deployment and Execution

### Prerequisites
- Docker
- Docker Compose
- Maven (Required for building Java microservices)