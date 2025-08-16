# Healix Doctor Service

This project is a Spring Boot application integrated with MongoDB Atlas, providing a basic search functionality using MongoDB's text search.

## Features
- Spring Boot backend
- MongoDB Atlas integration
- REST API for searching documents

## Getting Started

### Prerequisites
- Java 17 or later
- Maven
- MongoDB Atlas account (with a cluster and connection string)

### Setup
1. Clone the repository.
2. Configure your MongoDB Atlas connection string in `src/main/resources/application.properties`.
3. Build and run the application:
   ```sh
   mvn spring-boot:run
   ```

### API Usage
- `GET /doctors/search?query=...` — Search doctors by name, specialty, etc.

## Notes
- Replace the MongoDB connection string placeholder with your actual Atlas URI.
- Extend the model and repository as needed for your use case.
