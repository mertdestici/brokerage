# Brokerage API - Java Backend Developer Case

This is a Spring Boot application developed for a brokerage firm to manage customer stock orders. The application supports placing, listing, and canceling orders as well as tracking customer assets. It also includes authentication for both admin and customers.

---

## 📦 Features

- ✅ Create new stock orders (BUY or SELL) for a customer
- ✅ List orders by customer and date range
- ✅ Cancel pending orders only
- ✅ List all assets of a customer
- ✅ TRY is stored as an asset (not a separate table)
- ✅ Usable balance validation before placing orders
- ✅ Reclaim usable balance on cancellation
- ✅ Admin-authenticated endpoints
- ✅ JWT login for customers (Bonus 1)
- ❌ Admin endpoint for matching orders (Bonus 2 – not implemented)

---

## 🔐 Authentication

### Admin
- Admin user can access **all** customer data.
- Admin credentials are stored in the database (seeded on startup).

### Customer
- Customers can **only access and manage their own data** after login.
- JWT token must be included in `Authorization: Bearer <token>` header.

---

## 📌 Technologies Used

- Java 17
- Spring Boot
- Spring Security with JWT
- H2 in-memory database
- Gradle build tool
- JUnit 5 & MockMvc for testing
- Docker (optional)

---

## 🚀 How to Run the Project

### 🔧 Without Docker
1. **Clone the repository**
   ```bash
   git clone https://github.com/mertdestici/brokerage
   cd brokerage-api
    ```
2. **Build the project**
    ```bash
    ./gradlew build
    ```
3. Run the application
    ```bash
    ./gradlew bootrun
    ```
### 🐳 Run with Docker

1. **Build Docker image**
   ```bash
   docker build -t brokerage-api .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 brokerage-api
   ```

3. **Access the application**
    - API: `http://localhost:8080`
    - H2 Console: `http://localhost:8080/h2-console`

---

## 🧪 Running Tests

```bash
./gradlew test
```

📫 Postman collection: [Brokerage API.postman_collection.json](Brokerage%20API.postman_collection.json)
