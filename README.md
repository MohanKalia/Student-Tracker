# Student Tracker

A secured REST API for managing student records, built with Java 25, Spring Boot, and MySQL. It supports student CRUD, filtering by university or study level, request validation, and interactive API docs.

## Features

- Create, read, update, partially update, and delete student records
- Filter records by university or study level
- Persist data with Spring Data JPA and MySQL
- Protect endpoints with HTTP Basic authentication and role based permissions
- Explore the API through Swagger UI

## Tech stack

Java 25 · Spring Boot 4 · Spring WebMVC · Spring Data JPA · Spring Security · MySQL · Springdoc OpenAPI · Maven

## Run locally

You’ll need Java 25 and a MySQL server. Create a database:

```sql
CREATE DATABASE student_tracker;
```

Create a `.env` file in the project root and set your connection details:

```dotenv
DB_URL=jdbc:mysql://localhost:3306/student_tracker
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

Keep credentials private; don’t commit a `.env` containing real secrets.

Start the application with the Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`.

## API

All routes are under `/api` and require HTTP Basic authentication.

| Method | Endpoint | Access |
| --- | --- | --- |
| `GET` | `/api/students` | Student |
| `GET` | `/api/students/{id}` | Student |
| `GET` | `/api/students/university/{university}` | Student |
| `GET` | `/api/students/studylevel/{studyLevel}` | Student |
| `POST` | `/api/students` | Professor |
| `PUT` | `/api/students` | Professor; include the student ID in the request body |
| `PATCH` | `/api/students/{id}` | Professor |
| `DELETE` | `/api/students/{id}` | Dean |

The application defines in-memory accounts for development:

| Username | Password | Roles |
| --- | --- | --- |
| `john` | `test123` | Student |
| `mary` | `test123` | Student, Professor |
| `susan` | `test123` | Student, Professor, Dean |

These accounts are for local development only. Replace them before deploying the application.

## API documentation

With the app running, open [Swagger UI](http://localhost:8080/ui.html). The OpenAPI specification is at [http://localhost:8080/api-docs](http://localhost:8080/api-docs).

## Tests

```bash
./mvnw test
```

## Project status

Core student CRUD, DTOs, validation, role based access, and API documentation are implemented. Planned work includes global error handling, database migrations, stronger authentication, containerization, and deployment.
