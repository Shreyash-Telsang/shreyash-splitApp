# Split App Backend

A Spring Boot backend application for splitting expenses between groups of people. This application helps track shared expenses and calculates who owes money to whom.

## Features

- Add, view, edit, and delete expenses
- Support for different split types (equal, percentage, exact amount)
- Automatic calculation of balances and settlements
- RESTful API endpoints for all operations
- MongoDB integration for data persistence

## Prerequisites

- Java 17 or higher
- Maven
- MongoDB 4.4 or higher

## Setup Instructions

1. Clone the repository:
```bash
git clone <repository-url>
cd split-app
```

2. Install MongoDB:
- Download and install MongoDB from [MongoDB website](https://www.mongodb.com/try/download/community)
- Start MongoDB service

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Expense Management

#### List All Expenses
```
GET /api/expenses
```

#### Add New Expense
```
POST /api/expenses
Content-Type: application/json

{
    "amount": 60.00,
    "description": "Dinner at restaurant",
    "paidBy": "Shantanu",
    "participants": ["Shantanu", "Sanket", "Om"],
    "splitType": "EQUAL"
}
```

#### Update Expense
```
PUT /api/expenses/{id}
Content-Type: application/json

{
    "amount": 60.00,
    "description": "Dinner at restaurant",
    "paidBy": "Shantanu",
    "participants": ["Shantanu", "Sanket", "Om"],
    "splitType": "EQUAL"
}
```

#### Delete Expense
```
DELETE /api/expenses/{id}
```

### Settlement Calculations

#### Get Balances
```
GET /api/balances
```

#### Get Settlements
```
GET /api/settlements
```

#### Get All People
```
GET /api/people
```

## Response Format

All API responses follow this format:
```json
{
    "success": true,
    "data": {...},
    "message": "Operation successful"
}
```

## Error Handling

The API returns appropriate HTTP status codes and error messages:
- 200: Success
- 400: Bad Request
- 404: Not Found
- 500: Internal Server Error

## Development

### Project Structure
```
src/main/java/com/splitapp/
├── controller/
│   └── ExpenseController.java
├── model/
│   └── Expense.java
├── repository/
│   └── ExpenseRepository.java
├── service/
│   └── ExpenseService.java
└── SplitAppApplication.java
```

### Running Tests
```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details. 