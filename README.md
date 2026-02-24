# ChuksKitchen Backend API

A complete **food ordering backend** built with **Spring Boot** for the Trueminds internship deliverable.  
It supports user registration (with OTP), role-based access (Customer & Admin), menu management, cart, orders, payments (using balance), ratings, and referral codes — all data stored in JSON files (no external database required).

## Features

- **User Management**
  - Signup with OTP verification (OTP printed to console + returned in response)
  - Login (email + password + OTP verification)
  - Role-based access: **Customer** (verified users) vs **Admin**
  - Unverified users can only browse food (no cart/order access)
  - Update profile, phone, address

- **Referral Code System**
  - Optional referral code during signup
  - Admin can create, list, and delete referral codes
  - Codes validated against admin-managed list

- **Food Management (Admin only)**
  - Add, update, delete food items
  - Set stock quantity → availability automatic (quantity > 0)

- **Cart**
  - Add/remove items (verified users only)
  - Real-time stock adjustment (decreases on add, restores on remove/cancel)

- **Orders**
  - Place order from cart
  - Pay using user balance (top-up with Luhn-validated card numbers)
  - Order lifecycle: PENDING → PAID → PREPARING → OUT_FOR_DELIVERY → DELIVERED or CANCELLED
  - Customer or Admin can cancel (refunds balance if paid)

- **Ratings**
  - Verified users can rate completed orders (stars + comment)
  - Admin can view all ratings and delete any

- **Admin Privileges**
  - Manage food, orders, referral codes, ratings
    - Update order status manually

- **Data Persistence**
  - All data saved in `./data/` folder as JSON files
  - `users.json`, `foods.json`, `orders.json`, `referralCodes.json`, `carts.json`,`ratings.json`.

- **Error Handling**
  - Global exception handler returns clean JSON errors
  - Handles: duplicate email/phone, invalid referral, insufficient stock/balance, etc.

## Tech Stack

- Java 17
- Spring Boot 3.2
- Jackson (for JSON file storage)
- Lombok
- Maven
- No external database — pure file-based persistence

## API Overview (Key Endpoints)

| Method | Endpoint                                   | Description                                      | Access          |
|--------|--------------------------------------------|--------------------------------------------------|-----------------|
| POST   | `/api/users/signup`                        | Register user (returns user + OTP)               | Public          |
| POST   | `/api/users/verify`                        | Verify OTP                                       | Public          |
| POST   | `/api/users/login`                         | Login (email + password + OTP)                   | Public          |
| GET    | `/api/foods`                               | List available food items                        | Public          |
| POST   | `/api/foods`                               | Create food item                                 | Admin only      |
| POST   | `/api/cart/{userId}/add`                   | Add item to cart                                 | Verified User   |
| POST   | `/api/orders/place/{userId}`               | Place order from cart                            | Verified User   |
| POST   | `/api/orders/{orderId}/pay?userId=...`     | Pay order with balance                           | Verified User   |
| PATCH  | `/api/orders/{orderId}/cancel?requesterId=...` | Cancel order (refund if paid)                | User/Admin      |
| POST   | `/api/ratings?userId=...`                  | Leave rating on completed order                  | Verified User   |
| GET    | `/api/referral-codes?requesterId=...`      | List referral codes                              | Admin only      |


## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/lukmanOye/ChuksKitchen-Backend-API.git
   cd ChuksKitchen-Backend-API
2. Ensure Java 17 and Maven are installed.
3. Run the application:
   ```Bash
   mvn spring-boot:run

4. API available at:
    http://localhost:8080/api

5. Data is automatically saved/loaded from the ./data/ folder.

     

![User & Relationships](./images/user-relationships.png)

![Order Status Lifecycle](./images/order-status-lifecycle.png)

![Status Flow](./images/status-flow.png)
   

##  Data Model Relationships
User → Orders: one user can have many orders

Order → User: each order belongs to one user

Order → OrderItems: one order contains many order items

FoodItem → OrderItems / CartItems: a food item can appear in many order items (as a snapshot) and many cart items

User → Ratings: one user can leave many ratings

Rating → Order: each rating may be linked to one order (optional)

ReferralCode → Users: one referral code can be used by many users

User → Cart: each user has one active cart

Cart → CartItems: one cart contains many cart items


