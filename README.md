# BFHL API Challenge

This repository contains the backend implementation for the Bajaj Finserv Health Limited (BFHL) developer challenge.

## Developer Information

- **Name**: Harsh Patil
- **Roll Number**: 0827CS231100
- **Email ID**: harshpatil230509@acropolis.in

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Maven
- Docker

## API Endpoints

### 1. POST /bfhl
Processes the input array and groups elements into numbers, alphabets, and special characters.

- **Request Body**:
  ```json
  {
    "data": ["a", "1", "334", "4", "R", "$"]
  }
  ```
- **Response Body**:
  ```json
  {
    "is_success": true,
    "user_id": "harsh_patil_06042006",
    "email": "harshpatil230509@acropolis.in",
    "roll_number": "0827CS231100",
    "odd_numbers": ["1"],
    "even_numbers": ["334", "4"],
    "alphabets": ["A", "R"],
    "special_characters": ["$"],
    "sum": "339",
    "concat_string": "Ra"
  }
  ```

### 2. GET /bfhl/health
Returns the operation code.

- **Response Body**:
  ```json
  {
    "operation_code": 1
  }
  ```

## Running Locally

### Prerequisites
- JDK 21 installed

### Build and Run
Run the following command from the root directory:
```bash
./mvnw spring-boot:run
```

### Run Tests
```bash
./mvnw clean test
```

## Running with Docker

### Build the Image
```bash
docker build -t bfhl-api .
```

### Run the Container
```bash
docker run -p 8080:8080 bfhl-api
```
