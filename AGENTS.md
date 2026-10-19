# AGENTS.md

## Project Overview

Build a simplified delivery order tracking system as part of the Foody Delivery Full-Stack Developer challenge.

The application must allow users to register, log in, create delivery orders, list orders, view order details, and update order statuses.

## Tech Stack

### Backend
- Java 25
- Spring Boot
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- React
- TypeScript
- Vite

## Requirements

### Authentication
- Register users with name, email, and password.
- Authenticate users with email and password.
- Hash passwords securely. Never store plain-text passwords.
- Protect application endpoints so only authenticated users can access them.

### Orders API
- Create orders with customer information, items, and delivery address.
- List orders.
- Retrieve an order by ID.
- Update order status.

Supported statuses:
- `RECEBIDO`
- `EM_PREPARO`
- `SAIU_PARA_ENTREGA`
- `ENTREGUE`
- `CANCELADO`

Validate incoming requests and prevent invalid status transitions.

### Frontend
- Provide a login and registration interface.
- List orders and display their current statuses.
- Allow authenticated users to create orders.
- Provide clear loading, success, and error states.
- Keep the interface simple and functional.

## Development Guidelines

- Keep the code simple, readable, and maintainable.
- Follow standard Spring Boot conventions, separating controllers, services, repositories, entities, and DTOs.
- Separate frontend components, API communication, and application logic.
- Use environment variables or configuration files for environment-specific settings.
- Never commit secrets, credentials, or sensitive data.
- Handle errors consistently and return appropriate HTTP status codes.
- Write tests for important business rules and authentication flows.
- Avoid unnecessary abstractions, dependencies, and architectural complexity.

## Git Workflow

- Keep the application runnable throughout development.
- Do not commit generated files, build artifacts, or local database files.

## Scope

Prioritize a working, well-structured MVP that satisfies the challenge requirements. Do not introduce microservices or other unnecessary infrastructure.