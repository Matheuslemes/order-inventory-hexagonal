# Inventory and Order Management Microservices

## Project Scope

This project consists of a microservices-based architecture designed to efficiently manage orders and inventory. The system comprises multiple services that communicate asynchronously using Apache Kafka as the event broker.

The primary goal is to validate stock availability before confirming an order, ensuring scalability, reliability, and data consistency throughout the process.

## Architecture Approach
This project follows an alternative version of the Hexagonal Architecture (Ports & Adapters), focusing on modularity, scalability, and separation of concerns. Unlike the strict Hexagonal Architecture, this approach adds flexibility while maintaining core architectural principles:

- Clear separation between business logic and infrastructure.
- Loosely coupled components, allowing for easy adaptability and replacement.
- Improved maintainability, enabling teams to work independently on different layers.
- Event-driven design, ensuring efficient communication between services.

## Technologies Used
The project leverages a robust technology stack to ensure efficiency, scalability, and maintainability:

- Java 17 – Main programming language
- Spring Boot – Application framework for dependency injection and microservices support
- Spring Kafka – Event-driven communication with Apache Kafka
- PostgreSQL – Relational database for data persistence
- JUnit & Mockito – Unit testing framework and mocking library
- Lombok – Simplified Java object modeling with annotations
- Docker – Containerization for deployment and scalability
- Mermaid.js – Diagramming tool for system flow representation
  
This architecture ensures that the core business logic remains independent of external concerns, making it easy to integrate with new frameworks or technologies in the future.

## System Workflow
Below is an overview of how the system processes orders and manages inventory.

1 - Order Service
- Handles order creation and produces events to Kafka.

2 - Inventory Service
- Listens to order events from Kafka.
- Validates stock availability for the requested order.
- Updates inventory accordingly and sends a response event.

3 - PostgreSQL Database
- Stores inventory and order information for persistence and tracking.

4 - Kafka
- Facilitates asynchronous communication between services.
- Ensures event-driven processing for order validation and stock updates.

## Order Validation Process
- The Inventory Service checks whether the order can be fulfilled based on available stock.
- If stock is available, the order status is updated to CONFIRMED, and the inventory is adjusted.
- If stock is insufficient, the order is CANCELLED, and a failure event is logged.
- A response event is sent back to the Order Service via Kafka to update the order status.

## Key Features
 Scalable and fault-tolerant architecture
 Event-driven communication using Kafka
 Reliable inventory management and order processing
 Microservices with PostgreSQL for data persistence

This microservices-based solution ensures seamless order processing, efficient inventory tracking, and fault tolerance for better scalability and system reliability. 

## Application Structure Diagram
![estrutura](https://github.com/user-attachments/assets/9443b53b-d817-403a-a19d-8de539e18a80)

## Processing Flow Diagram
![fluxo-geral](https://github.com/user-attachments/assets/ed6ffb9e-77dc-4104-be4b-34c7d1079596)

## Service Communication Diagram
![comunicação](https://github.com/user-attachments/assets/26e61326-774b-4bee-a71e-51b9cc2ca4c4)


## Author & Contact Information

Author: Matheus Silva Lemes
Email: matheuslemesmsl@gmail.com

LinkedIn: https://www.linkedin.com/in/matheus-silva-lemes-8a4628178/

GitHub: https://github.com/Matheuslemes

If you have any questions, suggestions, or feedback, feel free to reach out!
