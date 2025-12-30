# Job Application Tracker API

A production-ready REST API for managing job applications, built with Spring Boot 3.5 and modern Java practices. This project demonstrates enterprise-level backend development with comprehensive security, validation, error handling, and API documentation.

## 🎯 Problem Statement

In today's competitive job market, candidates often struggle to keep track of multiple applications, while recruiters face challenges in managing job postings and candidate pipelines efficiently. Existing solutions can be overly complex or lack customization. This **Job Application Tracker API** provides a streamlined, secure, and robust backend to manage this process, ensuring data integrity and role-based access control.


## 🚀 Features

- **Multi-Role Authentication**: JWT-based authentication with role-based access control (Admin, Recruiter, Candidate)
- **Job Management**: Full CRUD operations for job postings
- **Application Tracking**: Apply to jobs, track application status, and manage candidates
- **Comprehensive Security**: BCrypt password hashing, JWT tokens, method-level security
- **Input Validation**: Bean Validation with detailed error messages
- **Global Exception Handling**: Standardized error responses across all endpoints
- **API Documentation**: Interactive Swagger UI with OpenAPI 3.0 specifications
- **Database Migrations**: Flyway for version-controlled database schema
- **Docker Support**: Docker Compose for easy local development

## 🛠️ Technology Stack

### Backend
- **Java 17** - Modern LTS version
- **Spring Boot 3.5.9** - Latest Spring framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **Hibernate** - ORM framework

### Database
- **PostgreSQL 16** - Production-grade relational database
- **Flyway** - Database migration tool

### Security & Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing

### Documentation
- **SpringDoc OpenAPI 3** - API documentation
- **Swagger UI** - Interactive API explorer

### Build & Development
- **Maven** - Dependency management and build tool
- **Lombok** - Reduce boilerplate code
- **Docker & Docker Compose** - Containerization


## 📋 Prerequisites

- Java 17 or higher
- Docker and Docker Compose (for database)
- Maven 3.6+ (or use included Maven wrapper)

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/jobtracker.git
cd jobtracker
```

### 2. Configure Environment Variables

Create a `.env` file in the project root (use `.env.example` as template):

```bash
cp .env.example .env
```

Edit `.env` with your database credentials:

```env
POSTGRES_DB=jobtracker
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password
```

### 3. Configure Application Properties

Create `src/main/resources/application.properties` (use `application.properties.example` as template):

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Update the database password and JWT secret in `application.properties`.

### 4. Start the Database

```bash
docker-compose up -d
```

This will start a PostgreSQL database on port 5431.

### 5. Run the Application

Using Maven wrapper (recommended):

```bash
./mvnw spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔐 Authentication

### Register a New User

```bash
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securepassword",
  "role": "ROLE_CANDIDATE"
}
```

Available roles:
- `ROLE_ADMIN` - Full system access
- `ROLE_RECRUITER` - Manage jobs and view applications
- `ROLE_CANDIDATE` - Apply to jobs and view own applications

### Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securepassword"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Using the Token

Include the JWT token in the Authorization header for protected endpoints:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📖 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Authenticate user

### Jobs
- `GET /api/jobs` - Get all jobs (public)
- `GET /api/jobs/{id}` - Get job by ID (public)
- `POST /api/jobs` - Create job (Recruiter only)
- `PUT /api/jobs/{id}` - Update job (Recruiter only)
- `DELETE /api/jobs/{id}` - Delete job (Recruiter only)

### Applications
- `POST /api/applications/apply/{jobId}` - Apply to job (Candidate only)
- `GET /api/applications/my` - Get my applications (Candidate only)
- `GET /api/applications` - Get all applications (Admin only)
- `GET /api/applications/job/{jobId}` - Get applications for job (Recruiter/Admin)
- `PUT /api/applications/{id}/status` - Update application status (Recruiter/Admin)

### Users
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID (Admin only)
- `GET /api/users/me` - Get current user (Authenticated users)

## 🧪 Running Tests

```bash
./mvnw test
```

## 🏗️ Project Structure

```
jobtracker/
├── src/
│   ├── main/
│   │   ├── java/com/jerdiys/jobtracker/
│   │   │   ├── application/      # Application management
│   │   │   ├── auth/              # Authentication
│   │   │   ├── config/            # Configuration classes
│   │   │   ├── dtos/              # Data Transfer Objects
│   │   │   ├── enums/             # Enumerations
│   │   │   ├── exception/         # Custom exceptions & handlers
│   │   │   ├── job/               # Job management
│   │   │   ├── security/          # Security configuration
│   │   │   └── user/              # User management
│   │   └── resources/
│   │       ├── db/migration/      # Flyway migrations
│   │       └── application.properties
│   └── test/                      # Test files
├── docker-compose.yml             # Docker configuration
├── pom.xml                        # Maven dependencies
└── README.md                      # This file
```

## 🔒 Security Features

- **Password Encryption**: BCrypt with salt
- **JWT Authentication**: Stateless, token-based auth
- **Role-Based Access Control**: Method-level security with `@PreAuthorize`
- **CORS Configuration**: Configurable cross-origin requests
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **Input Validation**: Bean Validation with custom constraints

## 🐛 Error Handling

The API uses standardized error responses:

```json
{
  "status": 404,
  "message": "Resource Not Found",
  "details": "Job not found with id: '123'",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/jobs/123"
}
```

Common HTTP status codes:
- `200 OK` - Success
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 🚀 Deployment

### Building for Production

```bash
./mvnw clean package
```

The executable JAR will be in `target/jobtracker-0.0.1-SNAPSHOT.jar`.

### Running the JAR

```bash
java -jar target/jobtracker-0.0.1-SNAPSHOT.jar
```

## 🤝 Contributing

This is a portfolio project, but suggestions and feedback are welcome!

## 📝 License

This project is open source and available under the MIT License.

## 👤 Author

**Your Name**
- GitHub: [@jerdiys](https://github.com/jerdiys)
- LinkedIn: [David John](https://linkedin.com/in/david-john-1a1362252)
- Email: seanjed82@gmail.com

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community
- All open-source contributors

---

**Note**: This is a portfolio project demonstrating modern Spring Boot development practices. It showcases enterprise-level features including security, validation, error handling, and comprehensive API documentation.
