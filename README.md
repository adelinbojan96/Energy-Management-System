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

---

## Technologies Used

### Backend
- Java 21
- Spring Boot 3
- Spring Web and Spring Security
- Spring Data JPA (PostgreSQL)
- JWT (JSON Web Token) Authentication
- Springdoc OpenAPI for Swagger documentation

### Frontend
- React.js (Vite)
- Axios for API communication
- Role-based routing (Admin and Client)

### Infrastructure
- Docker and Docker Compose
- Traefik (Reverse Proxy and Load Balancer)
- PostgreSQL Databases

---

## REST API Overview

### Authentication Service (`:8083`)
| Method | Endpoint | Description |
|---------|-----------|-------------|
| `POST` | `/auth/register` | Register a new user credential |
| `POST` | `/auth/login` | Authenticate user and return JWT token |
| `DELETE` | `/auth/credentials/{id}` | Delete a credential by ID |

### User Service (`:8081`)
| Method | Endpoint | Description |
|---------|-----------|-------------|
| `GET` | `/users` | Retrieve all users |
| `GET` | `/users/{id}` | Retrieve a user by ID |
| `POST` | `/users` | Create a new user |
| `PUT` | `/users/{id}` | Update user information |
| `DELETE` | `/users/{id}` | Delete a user |

### Device Service (`:8082`)
| Method | Endpoint | Description |
|---------|-----------|-------------|
| `GET` | `/device` | Retrieve all devices |
| `GET` | `/device/{id}` | Retrieve a device by ID |
| `POST` | `/device` | Create a new device |
| `PUT` | `/device/assign?deviceId=&userId=` | Assign a device to a user |
| `DELETE` | `/device/{id}` | Delete a device |

### Prerequisites
- Docker
- Docker Compose
- Node.js and Maven (only required if building manually)

---

![Untitled diagram-2025-10-28-212446.png](../../../../Downloads/Untitled%20diagram-2025-10-28-212446.png)
