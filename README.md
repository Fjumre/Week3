The codebase follows a layered design:

* **Controllers**: Receive HTTP requests, validate/parse data, and return JSON responses.
* **DAOs (Data Access Objects)**: Handle database operations using JPA.
* **Models/DTOs**: Represent domain entities and transfer objects.
* **Endpoints**: Central place to register routes and apply role-based access control.

---

## Authentication & Roles

* Authentication handled by `SecurityController` (not shown fully here).
* Roles are defined in `RouteRoles`:

  * `ANYONE`: Public access
  * `USER`: Authenticated user
  * `ADMIN`: Admin-only actions
* Javalin's `before(securityController.authenticate())` middleware ensures users are authenticated for protected routes.
* Role-based access applied per-route in `Endpoints`.

---

## API Endpoints

### Authentication Routes (Public)

Base path: `/auth`

* **POST /auth/login** → Log in a user (returns token/session)
* **POST /auth/register** → Register a new user
* **POST /auth/resetpassword** → Request/reset password flow

Protected Test Routes

Base path: `/protected`

* **GET /protected/user** → Accessible by `USER` role
* **GET /protected/admin** → Accessible by `ADMIN` role

User Routes

Base path: `/user`

* **GET /user/all** → (ADMIN) Get all users
* **POST /user/create** → (ADMIN) Create a new user
* **GET /user/{id}** → (USER or ADMIN) Get user by ID
* **PUT /user/update/{id}** → (USER or ADMIN) Update user by ID
* **DELETE /user/delete/{id}** → (USER or ADMIN) Delete user by ID
* **POST /user/logout** → (USER or ADMIN) Log out
* *(Optional, disabled by default)*

  * **GET /user/u/{username}** → Find user by username
  * **PUT /user/u/{username}** → Update user by username

Product Routes

Base path: `/products`

* **GET /products** → (ANYONE) Get all products
* **GET /products/{id}** → (ANYONE) Get product by ID
* **GET /products/name/{name}** → (ANYONE) Get product by name
* **POST /products** → (ADMIN) Create a product
* **PUT /products** → (ADMIN) Update a product
* **DELETE /products/{id}** → (ADMIN) Delete product by ID
* **DELETE /products/name/{name}** → (ADMIN) Delete product by name

---

Design Decisions

1. Layered architecture:

   * Controllers are thin (parsing, validation, responses).
   * DAOs encapsulate persistence logic, ensuring transaction boundaries are properly managed.

2. Security enforcement:

   * `@RolesAllowed` annotations in DAOs (defense in depth).
   * Role checks applied at the route level in `Endpoints`.

3. DTO usage:

   * `UserDTO` ensures sensitive fields (like hashed password) are not exposed to clients.

4. Password handling:

   BCrypt hashing on creation and update.
   Verification delegated to User entity/DAO.

5. Centralized routing:

All routes are declared in `Endpoints.java`, making the API surface easy to audit.

6. Error handling:

   Controllers return JSON objects with a consistent `{ "msg": "..." }` structure.
   Global error handling can be extended in `ApplicationConfig`.

