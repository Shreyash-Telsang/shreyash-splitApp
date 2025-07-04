# Split App Backend

A Spring Boot backend application for splitting expenses between groups of people. This system helps track shared expenses and automatically calculates who owes money to whom.

## Features

- Add, view, edit, and delete expenses
- Support for different split types (equal, percentage, exact amount)
- Automatic calculation of balances and settlements
- RESTful API endpoints for all operations
- MongoDB integration for data persistence

## Prerequisites

- Java 17 or higher
- Maven
- **MongoDB:**
    - For local development: Install MongoDB 4.4+ and run a local instance (default port 27017).
    - For deployed application: A MongoDB Atlas cluster (configured via environment variable).

## Setup Instructions (Local Development)

1. Clone the repository:
```bash
git clone https://github.com/Shreyash-Telsang/shreyash-splitApp.git
cd shreyash-splitApp
```

2. **Configure MongoDB URI:**
   For local development, you can either:
   a. Run a local MongoDB instance. Your application will attempt to connect to `mongodb://localhost:27017/splitapp` by default.
   b. Connect to your MongoDB Atlas cluster by setting the `SPRING_DATA_MONGODB_URI` environment variable in your terminal session before running the app:
   ```bash
   # For Windows PowerShell:
   $env:SPRING_DATA_MONGODB_URI="mongodb+srv://<YOUR_ATLAS_USER>:<YOUR_ATLAS_PASSWORD>@cluster0.iz3kfpd.mongodb.net/Shreyashsplitapp?retryWrites=true&w=majority&appName=Cluster0"

   # For Linux/macOS/Git Bash:
   export SPRING_DATA_MONGODB_URI="mongodb+srv://<YOUR_ATLAS_USER>:<YOUR_ATLAS_PASSWORD>@cluster0.iz3kfpd.mongodb.net/Shreyashsplitapp?retryWrites=true&w=majority&appName=Cluster0"
   ```
   *(Replace `<YOUR_ATLAS_USER>` and `<YOUR_ATLAS_PASSWORD>` with your actual MongoDB Atlas credentials.)*

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## Deployment

This backend is deployed on [Railway.app](https://railway.app/).

**Deployed API Base URL:** `https://shreyash-splitapp-production.up.railway.app`

Deployment relies on a `SPRING_DATA_MONGODB_URI` environment variable set on Railway, which points to the MongoDB Atlas cluster.

## API Documentation

### Expense Management

#### List All Expenses
`GET /api/expenses`

#### Add New Expense
`POST /api/expenses`

Request Body Example:
```json
{
    "amount": 60.00,
    "description": "Dinner at restaurant",
    "paidBy": "Shantanu",
    "participants": ["Shantanu", "Sanket", "Om"],
    "splitType": "EQUAL",
    "splitShares": [
        {"person": "Shantanu", "amount": 20.00}, 
        {"person": "Sanket", "percentage": 50.0}
    ] // Optional, based on splitType
}
```

#### Update Expense
`PUT /api/expenses/{id}`

Request Body Example (similar to POST, include fields to update):
```json
{
    "amount": 350.00,
    "description": "Petrol (updated to 350)",
    "paidBy": "Om",
    "participants": ["Om", "Shantanu", "Sanket"],
    "splitType": "EQUAL"
}
```

#### Delete Expense
`DELETE /api/expenses/{id}`

### Settlement Calculations

#### Get Balances
`GET /api/balances`

#### Get Settlements
`GET /api/settlements`

#### Get All People
`GET /api/people`

## Response Format

All API responses follow this consistent format:
```json
{
    "success": true,
    "data": {...},
    "message": "Operation successful"
}
```

## Error Handling

The API returns appropriate HTTP status codes and helpful error messages for invalid inputs and unexpected scenarios:
- 200: OK (Success)
- 400: Bad Request (e.g., validation errors, invalid input)
- 404: Not Found (e.g., expense ID not found)
- 500: Internal Server Error (unhandled exceptions)

## Settlement Calculation Logic

The `ExpenseService` handles the core logic for calculating balances and simplified settlements. 
1.  **Balances:** It iterates through all recorded expenses. For each expense, the `paidBy` person's balance increases by the expense amount, and each `participant`'s balance decreases by their calculated share of the expense. Shares are determined based on `splitType` (EQUAL, PERCENTAGE, or EXACT_AMOUNT).
2.  **Simplified Settlements:** After calculating all individual balances, the system identifies 'debtors' (people who owe money) and 'creditors' (people who are owed money). It then minimizes the number of transactions needed for settlement by efficiently transferring money from debtors to creditors until all balances are zero. This is done by iteratively settling the largest debts/credits first.

## Postman Collection

A public Postman collection is available for easy testing and demonstration of all API endpoints and pre-populated data.

**Postman Collection Link:** [Link to your GitHub Gist here] *(Please replace this placeholder with the actual public Gist URL after exporting your collection.)*

## Development

### Project Structure
```
src/main/java/com/splitapp/
├── controller/   # Handles API endpoints
├── model/        # Defines data structures (Expense, SplitShare)
├── repository/   # Provides data access (MongoDB)
├── service/      # Contains core business logic and calculations
└── SplitAppApplication.java # Main Spring Boot entry point
```

### Running Tests
```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

