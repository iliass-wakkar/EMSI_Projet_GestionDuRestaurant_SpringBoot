<div align="center">

# 🍽️ Restaurant Management: Enterprise Spring Boot 3 Backend

**Modular Hospitality Backend with Spring Data JPA, PostgreSQL & Order State Machine Workflows**

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot 3.4](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F.svg?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F.svg?logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![Spring Data JPA](https://img.shields.io/badge/Spring_Data-JPA%20%2F%20Hibernate-59666C.svg?logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1.svg?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Architecture](https://img.shields.io/badge/Pattern-Domain--Driven%20Repository-blue.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

*Architected & Engineered by **[Iliass Wakkar](https://www.wakkar.net)***

---

</div>

## 📌 Executive Summary

Modern high-volume restaurant operations require resilient backend architectures capable of coordinating real-time dining room table turnover, kitchen order routing, inventory reservation, and synchronized customer billing without race conditions or service bottlenecks.

**GestionDuRestaurant** is an enterprise-grade hospitality management backend engineered with **Java 17**, **Spring Boot 3.4**, **Spring Data JPA/Hibernate**, **Spring Security**, and **PostgreSQL**. Designed following Domain-Driven Design (DDD) principles and the decoupled Repository pattern, the platform orchestrates complete order lifecycles through deterministic finite-state machine workflows.

---

## 🏗️ Domain Architecture & State Machine

```
                           [ Dine-In Customer / Waitstaff POS ]
                                             │
                                             ▼
                             [ REST API Controller Layer ]
                                             │
                                             ▼
                        [ OrderService (Transactional Boundary) ]
                                             │
                 ┌───────────────────────────┴───────────────────────────┐
                 ▼                                                       ▼
      [ Table Availability Guard ]                           [ Order Finite State Machine ]
      - Atomically reserves dining tables                   ┌─────────────────────────────┐
      - Prevents double-booking via transactions            │  CREATED ➔ PENDING_PAYMENT  │
                                                            │             │               │
                                                            │             ▼               │
                                                            │     IN_PREPARATION (Kitchen)│
                                                            │             │               │
                                                            │             ▼               │
                                                            │      SERVED ➔ COMPLETED     │
                                                            │  (Releases Table to Free)   │
                                                            └─────────────────────────────┘
                                             │
                                             ▼
                        [ Spring Data JPA Persistence Layer ]
                                             │
                                             ▼
                         [ PostgreSQL Relational Database ]
```

### State Machine Transition Guard:
```java
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;

    public OrderResponse transitionOrderStatus(Long orderId, OrderStatus targetStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.canTransitionTo(targetStatus)) {
            throw new IllegalStateException("Invalid order state transition from " 
                + order.getStatus() + " to " + targetStatus);
        }

        order.setStatus(targetStatus);
        if (targetStatus == OrderStatus.COMPLETED) {
            order.getTable().setAvailable(true); // Atomically free dining table
        }

        return OrderResponse.fromEntity(orderRepository.save(order));
    }
}
```

---

## ⚡ Core Technical Highlights

- **Domain-Driven Repository Pattern**: Decouples business rules in service layers from persistence mechanics, enabling clean unit testing via Mockito.
- **Transactional Table Allocation**: Atomic database locks prevent race conditions when multiple waitstaff attempt to assign the same dining table concurrently.
- **JPA Auditing & Optimistic Locking**: Automatic entity auditing (`@CreatedDate`, `@LastModifiedDate`) and versioning fields (`@Version`) ensuring concurrency safety.
- **Strict DTO & Hibernate Validation**: Enforces input validation (`@NotNull`, `@Positive`, `@NotBlank`) prior to database transactions.
- **Spring Security Layer**: Stateless authentication securing kitchen display screens, waitstaff handhelds, and administrative financial reports.

---

## 📊 Relational Data Model (12 JPA Entities)

```
Key Relational Entities:
├── User & Role               : Waitstaff, Kitchen Chef, Shift Manager, Cashier
├── DiningTable & Zone        : Physical table numbering, seating capacity & floor zones
├── MenuItem & Category       : Culinary items, ingredients, pricing & active specials
├── Order & OrderItem         : Individual line items, quantity, notes & price snapshots
├── Bill & Payment            : Split bills, payment methods (Cash, Card) & fiscal records
└── Reservation               : Advance dining reservations with time slot conflicts
```

---

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 17+
- Maven 3.8+ (or bundled `mvnw`)
- PostgreSQL 14+

### Database Configuration
Update `Back-End/src/main/resources/application.properties` (or `.env`):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/iliass-wakkar/EMSI_Projet_GestionDuRestaurant_SpringBoot.git
   cd EMSI_Projet_GestionDuRestaurant_SpringBoot/Back-End
   ```

2. **Build with Maven**:
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Launch the Spring Boot Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Verify Endpoint Access**:
   The server boots on `http://localhost:8080`.

---

## 📂 Repository Structure

```
EMSI_Projet_GestionDuRestaurant_SpringBoot/
└── Back-End/
    ├── src/main/java/com/GestionDuRestaurant/
    │   ├── config/           # Spring Security, CORS & JPA auditing configuration
    │   ├── controller/       # 30+ REST endpoints (Orders, Tables, Menu, Bills)
    │   ├── dto/              # Request / Response transfer objects with validation
    │   ├── entity/           # 12 Relational JPA Entities (Order, Table, Item)
    │   ├── repository/       # Spring Data JPA repositories with custom queries
    │   ├── service/          # Business workflows & state transition machines
    │   └── exception/        # Global exception handler (@ControllerAdvice)
    ├── src/main/resources/   # Application properties & SQL seed scripts
    ├── pom.xml               # Maven dependencies (Spring Boot 3.4.5, PostgreSQL)
    └── mvnw / mvnw.cmd       # Maven wrapper scripts
```

---

## 👤 Author

**Iliass Wakkar**
- Portfolio: [wakkar.net](https://www.wakkar.net)
- GitHub: [@iliass-wakkar](https://github.com/iliass-wakkar)
- LinkedIn: [linkedin.com/in/iliass-wakkar-940233335](https://www.linkedin.com/in/iliass-wakkar-940233335/)
- Case Study: [Restaurant Management Spring Boot Case Study](https://www.wakkar.net/projects/restaurant-management-springboot)

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.