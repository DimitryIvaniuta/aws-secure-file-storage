# AWS Secure File Storage

This project is a secure file storage solution built with Java, Spring Boot, and AWS. It enables users to:
- Register and authenticate using a database (PostgreSQL).
- Upload files to AWS S3 with encryption using AWS KMS.
- Download files (as bytes or via file paths).
- List all files stored in the AWS S3 bucket.
- Delete files from AWS S3.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Setup and Configuration](#setup-and-configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing with Postman](#testing-with-postman)
- [Security Configuration](#security-configuration)
- [Docker and Database](#docker-and-database)
- [Future Improvements](#future-improvements)

## Features

- **User Management:**  
  Register, retrieve, and delete users. User details are stored in a PostgreSQL database with credentials managed by AWS Secrets Manager.
- **File Storage:**  
  Upload files to AWS S3 with optional AWS KMS encryption. Download files either as a byte array or as a resource.
- **List Files:**  
  Retrieve a list of all files stored in your S3 bucket.
- **Authentication:**  
  Secure endpoints using HTTP Basic authentication with user details loaded from the database.

## Technologies Used

- **Java 21**
- **Spring Boot 3.x**
- **Gradle**
- **AWS SDK (S3, KMS, Secrets Manager)**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Jackson (with jackson-datatype-jsr310 for Java 8 Date/Time types)**

## Project Structure

```aws-secure-file-storage/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker/
│   └── postgres/
│       └── docker-compose.yml
├── README.md
└── src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── securefilestorage/
│   │           ├── SecureFileStorageApplication.java
│   │           ├── config/
│   │           │   ├── DataSourceConfig.java
│   │           │   ├── JacksonConfiguration.java
│   │           │   └── SecurityConfig.java
│   │           ├── controller/
│   │           │   ├── FileStorageController.java
│   │           │   └── UserController.java
│   │           ├── dto/
│   │           │   ├── UserRequestDto.java
│   │           │   └── UserResponseDto.java
│   │           ├── exception/
│   │           │   └── GlobalExceptionHandler.java
│   │           ├── model/
│   │           │   └── User.java
│   │           ├── repository/
│   │           │   └── UserRepository.java
│   │           ├── security/
│   │           │   └── CustomUserDetailsService.java
│   │           └── service/
│   │               ├── FileStorageService.java
│   │               └── UserService.java
│   └── resources/
│       ├── application.yml
│       └── static/
│           └── favicon.ico
└── test/
└── java/
└── com/
└── securefilestorage/
└── SecureFileStorageApplicationTests.java
```


## Setup and Configuration

1. **AWS Configuration:**
  - Create the following secrets in AWS Secrets Manager:
    - `/secure-storage/aws-credentials` for AWS SDK credentials.
    - `/secure-storage-app/db-credentials` containing keys like `username`, `password`, `host`, `port`, and `dbname` for your PostgreSQL database.
  - Update the `application.yml` under the `aws.s3` section with your bucket name, region, and secret names.

2. **Database Configuration:**
  - The database connection details are fetched from AWS Secrets Manager via `DataSourceConfig`.
  - Ensure your PostgreSQL instance is running via Docker Compose (see next section).

3. **Jackson Configuration:**
  - The project uses Jackson’s JavaTimeModule (configured in `JacksonConfiguration.java`) to properly handle Java 8 date/time types (e.g., `ZonedDateTime`).

## Running the Application

### Without Dockerizing the Application

- **Database:**  
  Run your PostgreSQL container using Docker Compose:
  ```bash
  cd docker/postgres
  docker-compose up -d

---

## 📬 Contact

**Dzmitry Ivaniuta** — [diafter@gmail.com](mailto:diafter@gmail.com) — [GitHub](https://github.com/DimitryIvaniuta)
