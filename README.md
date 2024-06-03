# E-Commerce Project

This project is a RESTful API that implements the basic functionalities of an e-commerce platform.

## Technologies Used

- Java: The main programming language.
- Spring Boot: The framework that forms the basis of the application.
- JUnit: The library used for unit tests.
- Mockito: The library used to create mock objects in unit tests.
- Maven: The project management and build tool.
- PostgreSQL: The database management system.

## Database Schema

### Customer Table

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | Long      | The primary key of the customer. |
| firstName   | String    | The first name of the customer. |
| lastName    | String    | The last name of the customer. |
| email       | String    | The email of the customer. |
| phoneNumber | String    | The phone number of the customer. |

### Product Table

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | Long      | The primary key of the product. |
| name        | String    | The name of the product. |
| price       | BigDecimal| The price of the product. |
| stock       | Integer   | The stock quantity of the product. |

### Cart Table

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | Long      | The primary key of the cart. |
| customer    | Customer  | The customer who owns the cart. |
| cartItems   | List<CartItem> | The items in the cart. |
| totalPrice  | BigDecimal| The total price of the items in the cart. |

### CartItem Table

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | Long      | The primary key of the cart item. |
| cart        | Cart      | The cart that the item belongs to. |
| product     | Product   | The product in the cart item. |
| quantity    | Integer   | The quantity of the product in the cart item. |

### Order Table

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | Long      | The primary key of the order. |
| orderCode   | String    | The unique code of the order. |
| customer    | Customer  | The customer who placed the order. |
| orderItems  | List<OrderItem> | The items in the order. |
| status      | OrderType | The status of the order. |
| totalPrice  | BigDecimal| The total price of the order. |
| orderDate   | LocalDateTime | The date and time when the order was placed. |

### OrderItem Table

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | Long      | The primary key of the order item. |
| order       | Order     | The order that the item belongs to. |
| product     | Product   | The product in the order item. |
| quantity    | Integer   | The quantity of the product in the order item. |
| priceAtPurchase | BigDecimal | The price of the product at the time of purchase. |


## API Endpoints

### Customer Endpoints
- `POST /api/v1/customer`: Creates a new customer.

### Cart Endpoints
- `GET /api/v1/cart/{cartId}`: Retrieves the details of a cart with a specific ID.
- `GET /api/v1/cart/customer/{customerId}`: Retrieves the details of a cart belonging to a specific customer ID.
- `POST /api/v1/cart/add-item/{customerId}/{productId}`: Adds a specific product to the cart of a specific customer.
- `PUT /api/v1/cart/{cartId}/{productId}/{quantity}`: Updates the quantity of a specific product in the cart.
- `DELETE /api/v1/cart/{cartId}/{productId}`: Removes a specific product from the cart.

### Order Endpoints
- `POST /api/v1/order/{cartId}`: Places an order for a specific cart ID.
- `GET /api/v1/order/{orderCode}`: Retrieves the details of an order with a specific order code.
- `GET /api/v1/order/customer/{customerId}`: Retrieves the list of all orders for a specific customer ID.

### Product Endpoints
- `GET /api/v1/products/{id}`: Retrieves the details of a product with a specific ID.
- `POST /api/v1/products`: Creates a new product.
- `PUT /api/v1/products/{id}`: Updates the information of a product with a specific ID.
- `DELETE /api/v1/products/{id}`: Deletes a product with a specific ID.

## CI/CD Pipeline

This project automates continuous integration and continuous deployment (CI/CD) processes using GitHub Actions. Below are the main steps of the CI/CD pipeline:

- When code is pushed to GitHub or a pull request is created, the CI/CD pipeline is triggered.
- The pipeline runs on the latest version of Ubuntu.
- A database service is started in PostgreSQL version 13.
- JDK 17 is installed and Maven dependencies are cached.
- The project is built and tests are run with Maven.
- Test reports are archived.
- If all tests are successful, the production artifact (JAR file) is archived.

For more information, see the `.github/workflows/ci-cd.yml` file.

## Installation

1. Clone the project: `git clone https://github.com/AliRizaAynaci/ecommerce-backend.git`
2. Build the project with Maven: `mvn clean install`
3. Run the application: `java -jar target/ecommerce-0.0.1-SNAPSHOT.jar`