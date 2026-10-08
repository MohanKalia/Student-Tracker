# Student-Tracker

A RESTful backend for managing student data, built with **Java** and **Spring Boot**.

Student Tracker is a backend-focused project for learning and applying modern Spring development practices, including REST API design, persistence with Spring Data JPA, database integration, and API documentation.

> 🚧 **Project status:** In development

## Tech Stack

* **Java 25**
* **Spring Boot 4.1.1**
* **Spring WebMVC**
* **Spring Data JPA**
* **MySQL**
* **Springdoc OpenAPI**
* **Maven**

## Overview

The goal of Student Tracker is to build a clean and maintainable REST API for working with student records.

The project is being developed with a layered backend architecture so that HTTP handling, business logic, and data access remain separated.

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
MySQL
```

## Features

Current development focuses on:

* Student data management
* RESTful API endpoints
* Persistent storage with MySQL
* Database access through Spring Data JPA
* Automatic API documentation with OpenAPI / Swagger UI

As the project develops, additional functionality will be added here.

## Project Structure

The project follows a conventional Spring Boot structure:

```text
Student-Tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── mohankalia/
│   │   │           └── ...
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

Make sure you have the following installed:

* Java 25
* MySQL
* Git

You can verify Java with:

```bash
java -version
```

### 1. Clone the repository

```bash
git clone https://github.com/MohanKalia/Student-Tracker.git
cd Student-Tracker
```

### 2. Configure MySQL

Create a MySQL database for the application.

For example:

```sql
CREATE DATABASE student_tracker;
```

Then configure the database connection in:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_tracker
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> Do not commit real database passwords or other secrets to Git.

### 3. Run the application

Using the Maven wrapper:

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

#### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

Or build the project first:

```bash
./mvnw clean package
```

Then run the generated JAR:

```bash
java -jar target/Student-Tracker-0.0.1-SNAPSHOT.jar
```

## API Documentation

The project uses **OpenAPI / Swagger UI** for API documentation.

Once the application is running, Swagger UI should be available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The generated OpenAPI specification is available at:

```text
http://localhost:8080/v3/api-docs
```

These endpoints make it possible to explore and test the REST API directly from the browser.

## API

As the API develops, endpoints will be documented here.

Example format:

| Method   | Endpoint         | Description      |
| -------- | ---------------- | ---------------- |
| `GET`    | `/students`      | Get all students |
| `GET`    | `/students/{id}` | Get a student    |
| `POST`   | `/students`      | Create a student |
| `PUT`    | `/students/{id}` | Update a student |
| `DELETE` | `/students/{id}` | Delete a student |

## Testing

Run the test suite with:

```bash
./mvnw test
```

The project uses Spring's testing infrastructure to verify application behaviour as the codebase grows.

## Development Goals

This project is also being used to practice backend engineering concepts such as:

* REST API design
* Dependency injection
* Layered architecture
* Spring Data JPA
* Hibernate
* Relational database design
* Exception handling
* Input validation
* API documentation
* Automated testing
* Clean and maintainable Java code

## Roadmap

Planned improvements include:

* [ ] Complete student CRUD operations
* [ ] Add request/response DTOs
* [ ] Add validation
* [ ] Add global exception handling
* [ ] Improve API documentation
* [ ] Add unit and integration tests
* [ ] Add database migrations
* [ ] Add authentication and authorization
* [ ] Add Docker support
* [ ] Add CI with GitHub Actions
* [ ] Deploy the API

## Contributing

This is primarily a personal learning project, but suggestions, issues, and pull requests are welcome.

If you find a bug or have an idea for improving the project, feel free to open an issue.

## License

This project is currently available for educational and portfolio purposes.

A formal open-source license can be added later if required.

---

Built with Java and Spring Boot.
