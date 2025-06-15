# Ticket-System

## 1. Analysis:
### Scenario ideation
**Scenario 1 – Reporting a bug (User)**
Anna is a user who notices that her company portal crashes when uploading files. She logs into the ticket system, creates a new ticket with "High" priority, and describes the issue. The system assigns it to a support staff member.

**Scenario 2 – Responding to a ticket (Support)**
Jonas, a support staff, sees Anna’s ticket in his list. He opens it, reads the description, and sends a message asking for a screenshot. Anna replies with the screenshot, and Jonas continues the investigation.

**Scenario 3 – Ticket resolution and feedback (User)**
Once the issue is fixed, Jonas replies with a solution and marks the ticket status as Open for Anna. Anna confirms the issue is resolved and submits a 5-star rating with a short thank-you message. The ticket is closed.

### Use case analysis
The ticket system supports the following core use cases:

**Create Ticket**
The user logs in and submits a new support ticket. They enter a title, optional description, and select a priority. The system assigns the ticket to the support staff with the lowest workload based on ticket priority weights.

**View and Update Ticket**
The user can view their own tickets, including the status and related messages. They can update or delete their ticket within 30 minutes of creation, as long as no messages or ratings have been added.

**Send Message on Ticket**
Both users and support staff can post messages on a ticket. Messages allow for two-way communication. Depending on the input ("Keep" or "Switch"), the message may also change the ticket status for both sides.

**Assign Ticket Automatically**
When a ticket is created, the system calculates each support staff’s workload and assigns the new ticket to the one with the lowest score. Ticket status is set to Open for support and Pending for the user.

**Rate Ticket**
Once a ticket is resolved, the user can submit a rating (1–5) and optional feedback. Submitting a rating closes the ticket. Ratings are tied to the support staff and tracked for performance insights.

**Log In and Authenticate**
All actors (user, support, admin) log in using a username or email and password. Upon successful login, they receive a JWT token that grants access based on their role.

**Admin System Access**
Admins can view and manage all tickets, users, messages, and ratings. They have full access to the system, including administrative functions not available to users or support staff.

### User story writing
As a user, I want to create support tickets so that I can get help with issues.

As a user, I want to view and update my tickets so that I can track and manage them.

As support staff, I want tickets to be auto-assigned based on workload so that tasks are distributed fairly.

As a user or support staff, I want to send messages on tickets so that we can communicate during the support process.

As a user, I want to rate resolved tickets so that I can give feedback on the support I received.

As an admin, I want to access all tickets and users so that I can manage the system.

As a user, I want to log in securely so that I can access my personal ticket data.

## 2. Domain Design:

The domain model defines the core business entities and their relationships for the ticket management system. Each entity plays a specific role in tracking, managing, and interacting with support tickets and users. The domain model is saved as "Domain model.pdf" on main.

### Entities

- **User:** Represents a registered system user.  
  - Contains essential credentials: `username`, `email`, `password` (stored as a 60-character bcrypt hash), and a creation timestamp (`createdAt`).
  - Assigned a `role` via the `UserRole` enum to determine access privileges and visibility in the system (`User`, `Support`, `Admin`).
  - Can act as:
    - A ticket creator (`createdBy`)
    - A ticket assignee (`assignedTo`)
    - The author of a `Message`
    - The subject or creator of a `Rating`

- **Ticket:** Represents a support issue raised by a user.  
  - Fields include: `title`, optional `description`, `createdAt` timestamp, and `priority` level.
  - Relationships:
    - Created by a `User`
    - Assigned to a support `User`
    - Has a one-to-one link to its `latestStatusChange`
    - Contains a history of `statusChanges` via a one-to-many relation
  - All status changes are cascade-persisted and orphan-removable.

- **Message:** Represents a user comment or response in a ticket discussion thread.  
  - Linked to a specific `Ticket` and authored by a `User`.
  - Contains: `text` (as a large object), and `createdAt`.

- **Rating:** Captures user feedback after a ticket has been resolved.  
  - Fields include: numeric `rating` (1–5), optional `text`, and `createdAt`.
  - Relationships:
    - Linked to one `Ticket`
    - Created by one `User`
    - Directed at another `User` (support personnel)

- **TicketStatusChange:** Tracks a status transition in a ticket's lifecycle.  
  - Allows distinct status values for the ticket `creator` and `assignee` (`statusForCreator`, `statusForAssigned`) to support role-specific ticket views.
  - Can be associated with a `Message` or `Rating`, enabling the tracking of how/why a ticket status changed.
  - Maintains a reference to the `previousChange`, enabling historical reconstruction. This is important when messages or ratings are deleted to trace-back the previous status.
  - Stores a `changedAt` timestamp to document when the change occurred.

### Enums

- **UserRole:** Defines a user's system role. Values:
  - `User` – Can create and track tickets, and rate resolved ones.
  - `Support` – Can be assigned tickets, reply via messages, and be rated.
  - `Admin` – Has full access including user and system configuration. Is mainly used for bug-fixxing.

- **TicketStatus:** Represents a ticket's lifecycle stage. Values:
  - `Open` – Shown to the user with the open task (e.g., needs to solve the issue, provide info, or send a screenshot).
  - `Pending` – Shown to the user waiting for the other party to act or respond.
  - `Closed` – The ticket is resolved or archived.

- **Priority:** Represents the urgency of the ticket. Values:
  - `High`, `Medium`, `Low`

## 3. Frontend implementation:
Through our problems with budibase we had to work with a pro code approach as we could not get budibase to work. With the late decision to switch to a pro code approach we unfortunatly had time difficulties to implement every function of the backend.

### Folder Structure and Key Concepts

**src/hooks.server.ts**
This file is used for security and explained in the security section.

**src/routes/**
This folder defines the pages and URL structure of the app.
Every folder inside routes becomes part of the URL path.
For example:
src/routes/dashboard → your-site.com/dashboard
Inside each folder, the files have special roles:
+layout.svelte: Shared layout (e.g., navigation bar) for all child pages.
+page.svelte: The actual content shown for that route.
Layouts are inherited, so you can define common UI elements once and reuse them across pages.

**src/routes/api/**
This folder is used for API requests from the frontend to the backend.
Instead of making direct calls to the backend, all API logic is routed through here.
This provides a centralized and secure way to communicate with your backend services.

### Technologies Used
-  SvelteKit – Modern frontend framework
-  TypeScript – Typed JavaScript for better developer experience
-  TailwindCSS – Utility-first CSS framework
-  DaisyUI – UI component library based on Tailwind
-  HTML – Used within Svelte components


## 4. Business Logic and API design:
This section explains the core business rules governing the Ticket-System and details the API design to interact with the system.

### Business Logic
Beside the restrictions on the different endpoints (like Updates of tickets, messages and ratings are only allowed within 30min after creation) which are listed in the chapter API endpoints specification, the main business rule is the ticket assignment.

#### Ticket Assignment
Tickets are automatically assigned to the support member with the lowest workload.
The workload for each support member is calculated by summing the weights of their open or pending tickets:
  - High priority ticket = 3 points
  - Medium priority ticket = 2 points
  - Low priority ticket = 1 point

### API endpoints specification:
Here are all API endpoints from swagger listed.

#### Login and Registration Controller
- **POST `/api/login`**
  - Purpose:        User login
  - Input:          `login` (username/email), `password`
  - Details:        Authenticates user, returns JWT if valid, else 401
  - Returns:        JWT token

- **POST `/api/register`**
  - Purpose:        Register new user
  - Input:          `username`, `email`, `password`
  - Details:        Username and email must be both new, assigns the role `User`
  - Returns:        Success message or 400 on duplicates
 
- **Current User Controller '/api/user/current'**
-   Purpose:        Get information about the currently authenticated user
-   Input:          None (requires valid JWT in Authorization header)
-   Details:        Returns user details including userId, username, email, role, and account creation date. Returns 401 if not authenticated, 404 if user not found.
-   Returns:        JSON with user info or error status

#### Ticket Controller

- **POST `/api/tickets`**
  - Purpose:        Create ticket
  - Input:          `title`, `Details:`, `priority` (Low/Medium/High), `createdById`
  - Details:        Tickets can only be created by users (or admins), and must include either a title or Details:. If title is not provided, it defaults to Username of creator and date.
                    Priority can be "Low", "Medium" or "High" and defaults to 'Low'.
                    The tickets are automatically assigned to the support with the lowest workload based on the business logic.
                    The ticket status for the assigned user is set to 'Open' and the ticket status for the creator is set to 'Pending' upon creation.
  - Returns:        TicketInfoDTO containing all the relevant ticket info as well as the status for the user and support from the new ticket.

- **GET `/api/tickets/{id}`**
  - Purpose:        Get ticket by ID
  - Input:          `id` (TicketID)
  - Details:        Endpoint is used for Ticket view and is allowed for Admin, User and Support
  - Returns:        TicketInfoDTO containing all the relevant ticket info as well as the status for the user and support from the requested ticket.

- **GET `/api/tickets/all`**
  - Purpose:        Get all tickets
  - Input:          None
  - Details:        Is only used in swagger to get all the Tickets, only allowed for admins.
  - Returns:        List of TicketInfoDTO containing all the relevant ticket info as well as the status for the user and support from all existing tickets.

- **GET `/api/tickets/filter/{status}`**
  - Purpose:        Get tickets filtered by user role & status
  - Input:          `userID` (query param), `status` (path param: Open/Pending/Closed)
  - Details:        Users see all own tickets and supports see all assigned tickets filtered by the article status and userID.
  - Returns:        List of TicketInfoDTO containing all the relevant ticket info as well as the status for the user and support from all filtered tickets.

- **PUT `/api/tickets/{id}`**
  - Purpose:        Update ticket by ID
  - Input:          `id` (of the Ticket), `title`, `Details:`, `priority` (Low/Medium/High), `createdById`
  - Details:        Only ticket creator can update within 30 minutes after creation.
                    Null or empty fields keep the input from before and priority defaults to Low if invalid.
  - Returns:        TicketInfoDTO containing all the relevant ticket info as well as the status for the user and support from the updated ticket.

- **DELETE `/api/tickets/{id}`**
  - Purpose:        Delete ticket by ID
  - Input:          `id` (from Ticket), `userId`
  - Details:        Only ticket creator can delete their open or pending tickets as long as no messages or ratings are created for the ticket.
  - Returns:        No content (void)

#### Message Controller (all endpoints allowed for Admin, User and Support)

- **POST `/api/messages`**
  - Purpose:        Create message
  - Input:          `text`, `userId`, `ticketId`, `status` ("Keep"/"Switch")
  - Details:        Sender must be ticket creator or the assigned support. The text can not be empty.
                    When entering "Keep", the status for both the user and support will stay.
                    When entering "Switch" the status will switch for both from "Open" to "Pending and vice versa.
                    "Switch" only works for the person for which the status is "Open".
  - Returns:        List of all messages for the ticket including the newly created one.

- **GET `/api/messages/{id}`**
  - Purpose:        Get message by ID
  - Input:          `id` (of message)
  - Details:        Is only used in the backend.
  - Returns:        Single message info

- **GET `/api/messages/ticket/{ticketId}`**
  - Purpose:        Get all messages for a ticket
  - Input:          `ticketId`
  - Details:        Used to show the messages in the ticket.
  - Returns:        List of messages for ticket

- **PUT `/api/messages/{messageId}`**
  - Purpose:        Update message
  - Input:          `messageId`, updated `text`, `userId`, `ticketId`
  - Details:        Only original message creator can update the message within 30 minutes after creation as long as no newer messages from others exist. The text can not be empty!
  - Returns:        List of messages for ticket after update

- **DELETE `/api/messages/{messageId}`**
  - Purpose:        Delete message
  - Input:          `messageId`, `userId`
  - Details:        Only original message creator can delete the message within 30 minutes after creation as long as no newer messages from others exist.
                    The ticket status must be 'Open' or 'Pending' and it rollbacks the previous ticket status if changed.
  - Returns:        List of messages for ticket after deletion

#### Rating Controller

- **POST `/api/ratings`**
  - Purpose:        Create rating
  - Input:          `ticketId`, `text`, `rating` (1-5), `createdById`
  - Details:        Only Users (or Admins) can create ratings.
                    Ticket must be 'Open' or 'Pending' and only the ticket creator can create ratings. The rating automatically closes the ticket.
  - Returns:        RatingInfoDTO containing all the relevant info for the created rating.

- **GET `/api/ratings/support/{supportId}`**
  - Purpose:        Get all ratings for a support
  - Input:          `supportId`
  - Details:        Only Support user (and Admins) can use this endpoint.
  - Returns:        List of RatingInfoDTO containing all the relevant info for the rating assigned to the support.

- **GET `/api/ratings/support/{supportId}/average`**
  - Purpose:        Get average rating for support
  - Input:          `supportId`
  - Details:        Only Support user (and Admins) can use this endpoint.
  - Returns:        Average rating (double between 1-5) for the specified support user.

- **PUT `/api/ratings/{ratingId}`**
  - Purpose:        Update rating
  - Input:          `ratingId`, updated `text` and/or `rating`, `createdById`
  - Details:        Only original creator can update the rating within 30 minutes after creation.
                    All null or empty fields keep the existing values.
  - Returns:        RatingInfoDTO containing all the relevant info for the updated rating.

- **DELETE `/api/ratings/{ratingId}`**
  - Purpose:        Delete rating
  - Input:          `ratingId`, `userId`
  - Details:        Only original creator can delete the rating within 30 minutes after creation.
                    Deleting a rating reopens the associated ticket with the previous ticket status.
  - Returns:        No content (void)

## 5. Data and API Implementation:

Implementation of the data access, business logic, and API layers.

### Technology Stack
- **Spring Boot 3.5.0** – Framework for building the backend
- **Maven** – Build automation and dependency management tool
- **Spring Data JPA** – ORM for managing database interactions using annotated Java classes
- **H2 Database (in-memory)** – Lightweight, embedded database for development and testing
- **JWT (JSON Web Token)** – For secure authentication using the jjwt library
- **Spring Security** – For role-based access control and API-level protection
- **OpenAPI 3.0 + springdoc-openapi** – For auto-generating API documentation and providing a Swagger UI

### Application Startup
- The project is a standard Spring Boot application.
- It is bootstrapped via the `TicketSystemApplication` class located in the root package (`ch.fhnw.ticket_system`).
- The `main()` method invokes `SpringApplication.run(...)`, which starts the Spring context and auto-configures components.

### Data Persistence
- The entities (`User`, `Ticket`, `Message`, `Rating`, and `TicketStatusChange`) are defined using JPA annotations (e.g., `@Entity`, `@ManyToOne`, `@Id`).
- These entities are automatically mapped to relational tables by Spring Data JPA. The schema is generated at runtime based on the JPA definitions.
- A custom `data.sql` file is used to populate the database on startup with:
  - Example users (1 Admin, 4 Users, 2 Supporters)
  - 20 sample tickets with initial status changes
  - 43 messages linked to tickets
  - 6 ratings and 69 status changes
- This seeding facilitates quick testing and demonstration of system features.

### API Implementation
- **Controllers:** Expose REST endpoints to manage tickets, users, messages, ratings, and status updates.
- **Data Layer:** Includes:
  - **Domain Models:** Core business entities (e.g., `Ticket`, `User`, `Message`, `Rating`, `TicketStatusChange`) and supporting enums (`TicketStatus`, `Priority`, `UserRole`).
  - **DTOs (Data Transfer Objects):** Define structured request/response payloads to decouple internal domain models from external APIs.
  - **Repository Layer:** Uses Spring Data JPA for abstracted database access. Includes custom queries for filtering and sorting.
- **Service Layer:** Implements business rules, such as ticket assignment based on workload, and performs input validation with meaningful exceptions.
- **Security Layer:** Enforces authentication and role-based authorization using JWT and Spring Security. (Further details in the next chapter.)

## Security

This project implements a secure authentication and authorization system for the ticket system API using JWT (JSON Web Tokens) and Spring Security.

### JWT Token Provider

The `JwtTokenProvider` component is responsible for creating and verifying JWT tokens.

- Uses the HS256 algorithm with a securely generated secret key.
- Tokens contain the username as the subject and a custom claim for the user role.
- Tokens expire after 1 hour.
- The secret key is securely stored and used to sign and verify tokens.

### Security Configuration

The `SecurityConfig` class configures Spring Security with the following key aspects:

- **HTTP Security:**
  - CSRF protection is disabled for simplicity (adjust as needed).
  - CORS support enabled with default settings.
  - Frame options set to `sameOrigin` to allow the H2 database console.
  - Defines public endpoints such as login, registration, Swagger UI, and H2 console, which do not require authentication.
  - Protects all other endpoints, restricting access based on user roles:
    - `User`, `Support`, and `Admin` roles are supported.
    - Access to ticket, rating, and message endpoints is restricted according to user roles.
- **JWT Authentication:**
  - Uses OAuth2 resource server support for JWT tokens.
  - Configures a custom JWT authentication converter to extract roles from the token claim `"role"` and prefix them with `"ROLE_"` as required by Spring Security.
  - JWT tokens are decoded using the secret key from `JwtTokenProvider`.

### Password Encoding

- Passwords are hashed using BCrypt with the `PasswordEncoder` bean provided by `PasswordConfig`.
- All stored passwords are encoded before saving to the database.

### OpenAPI Security Integration

- The `OpenAPIConfig` class integrates JWT authentication with Swagger UI by:
  - Adding a security scheme named `bearerAuth` of type HTTP with scheme `bearer` and bearer format `JWT`.
  - Adding a global security requirement to secure the API documentation endpoints.
- This allows users to authenticate within the Swagger UI using JWT tokens.

### Authentication and Registration Endpoints

Implemented in the `LoginRegistrationController`:

- **Login (`POST /api/login`):**
  - Accepts login requests with either username or email and password.
  - Validates credentials and returns a JWT token in the `Authorization` header on success.
  - Returns HTTP 401 if authentication fails.
  
- **Registration (`POST /api/register`):**
  - Allows new users to register with a username, email, and password.
  - Checks for existing username or email conflicts.
  - Encodes the password using BCrypt before storing.
  - Assigns the default role `User`.
  - Returns success or appropriate error messages.
 
- **Get Current User Info (`GET /api/user/current`):**
  - This endpoint is used after the login to authenticate the user
  - Requires a valid JWT token in the Authorization header.
  - Returns detailed information about the currently authenticated user, including `userId`, `username`, `email`, `role`, and `createdAt`.
  - Returns HTTP 401 if the user is not authenticated.
  - Returns HTTP 404 if the user is not found in the database.
 
### Security in the frontend
**src/hooks.server.ts**
This file runs on the server for every incoming request. Its job is to:
Check if a Bearer token is included in the request.
If a token is found, it decodes it to extract the userId, role, and the token itself.
These values are stored in a user object, which becomes accessible throughout the app.
This means the user info is available globally without needing to decode the token again in each request.

## 7. Demonstrator:
Integration of frontend and backend to realize an end-to-end application consuming REST APIs from the web service

###Setup (Prerequisites)
  - Backend Spring Boot app running (default port 8080)
  - Frontend SvelteKit app running (e.g., port 5173)
  - Both connected and configured to communicate via REST API

### To start the app
  - A new codespace has to be opened
  - Click CTRL + SHIFT + P
  - Enter: "Tasks: run task"
  - Click: "run backend"
  - Wait until backend runs
  - Click again CTRL + SHIFT + P
  - Enter: "Tasks: run task"
  - Click: "run frontend"
  - Open http://localhost:5173/ outputed in the terminal

### Links to the different pages
After starting the app the port tab in codespace should show multiple ports. The two links for the port 8080 and 5173 are important.

**Port 8080**
Opens a website with the text "Hello, welcome to our TicketSytem!" if the backend is running. The only purpouse is to show if it runs.

**Port 5173**
Opens the frontend

**Swagger**
Shows all the endpoints from the backend. Can be opened by adding "/swagger-ui.html" after the link from port 8080.

**H2-Database**
Can be used to show all the entities. Can be opened by adding "/h2-console" after the link from port 8080.
For login the following settings and credentials are needed:
  - Driver      Class: org.h2.Driver
  - JDBC URL:   jdbc:h2:mem:testdb
  - User Name:  TicketSystem
  - Password:   pw

### Logins:
There are 7 users registered with the roles "User", "Support" and "Admin". For the login the username or email and the password are needed.

#### Users
  - The users are named user1, user2, user3 and user4.
  - They have the e-mail userX@example.com, where X represents their respective number.
  - They all have pass123 as password

#### Support
  - The support-users are named support1 and support2.
  - They have the e-mail supportX@example.com, where X represents their respective number.
  - They both have support123 as password

#### Admin
  - The admin is named admin1
  - The admins e-mail is admin1@example.com
  - The password for the admin is admin123

### Demonstration:

The demonstration was recorded. The video was uploaded on steam.

## 8. Project documentation

The project documentation is saved as "Project Documentation.pdf" on main.


