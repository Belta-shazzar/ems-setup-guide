# Employee Management System - Setup & Deployment Guide

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Repositories](#repositories)
- [Prerequisites](#prerequisites)
- [Local Development Setup](#local-development-setup)
- [Running the Services](#running-the-services)
- [Service Repositories](#service-repositories)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Monitoring & Health Checks](#monitoring--health-checks)
- [Troubleshooting](#troubleshooting)
- [Improvements & Recommendations](#improvements--recommendations)

## Overview

The Employee Management System (EMS) is a microservices-based application for managing employees, departments, and authentication. It consists of multiple services that communicate via REST APIs and Apache Kafka.

### Key Features
- **Employee Management**: CRUD operations for employees and departments
- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Event-Driven Architecture**: Kafka integration for asynchronous communication
- **Service Discovery**: Eureka for service registration and discovery
- **Centralized Configuration**: Spring Cloud Config Server
- **API Gateway**: Single entry point for all services
- **API Documentation**: Swagger/OpenAPI documentation

## Repositories

Below are the individual service repositories that make up this project:
PS: I have added the repositories here for easy access.

- [Config Service](https://github.com/Belta-shazzar/ems-config-service)
- [Discovery Service](https://github.com/Belta-shazzar/ems-discovery)
- [API Gateway](https://github.com/Belta-shazzar/ems-api-gateway)
- [Auth Service](https://github.com/Belta-shazzar/ems-auth-service)
- [Employee Service](https://github.com/Belta-shazzar/ems-employee-service)
- [Notification Service](https://github.com/Belta-shazzar/ems-notification-service)

## Architecture

```
┌─────────────┐
│  API Gateway│
│  (Port 8000)│
└──────┬──────┘
       │
       ├──────────────┬──────────────┬──────────────┐
       │              │              │              │
┌──────▼──────┐ ┌────▼─────┐ ┌──────▼──────┐ ┌────▼─────────┐
│Auth Service │ │Employee  │ │Notification │ │Config Server │
│ (Port 8010) │ │Service   │ │Service      │ │ (Port 8888)  │
│             │ │(Port 8020│ │(Port 8083)  │ │              │
└──────┬──────┘ └────┬─────┘ └──────┬──────┘ └──────────────┘
       │             │              │
       │             │              │
       └─────────────┴──────────────┘
                     │
              ┌──────▼──────┐
              │   Eureka    │
              │  Discovery  │
              │ (Port 8761) │
              └─────────────┘

Infrastructure:
- PostgreSQL (Port 5432)
- Apache Kafka (Port 29092)
- Zookeeper (Port 2181)
- Kafka UI (Port 8080)
```

## Prerequisites

### Required Software
- **Java 21** (JDK 21 or later)
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Git**
- **PostgreSQL 15** (or use Docker)

### Optional
- **IntelliJ IDEA** or **VS Code** for development
- **Postman** or **cURL** for API testing

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd employee-management-system
```

### 2. Start Infrastructure Services

Using Docker Compose:

```bash
docker-compose up -d postgres redis zookeeper kafka kafka-ui
```

PS: You can find a compose.yml file in the root of this repository containing all needed services.

Verify services are running:
```bash
docker-compose ps
```

### 3. Configure Environment Variables


### 4. Build All Services

```bash
# Build all services
mvn clean install -DskipTests

# Or build individual services
cd config-server && mvn clean install -DskipTests
cd discovery && mvn clean install -DskipTests
cd auth-service && mvn clean install -DskipTests
cd employee-service && mvn clean install -DskipTests
cd notification-service && mvn clean install -DskipTests
cd api-gateway && mvn clean install -DskipTests
```

## Running the Services

### Start Order (Important!)

Services must be started in the following order:

1. **Config Server** (Port 8888)
```bash
cd config-server
mvn spring-boot:run
```

2. **Discovery Service** (Port 8761)
```bash
cd discovery
mvn spring-boot:run
```

3. **Auth Service** (Port 8010)
```bash
cd auth-service
mvn spring-boot:run
```

4. **Employee Service** (Port 8020)
```bash
cd employee-service
mvn spring-boot:run
```

5. **Notification Service** (Port 8083)
```bash
cd notification-service
mvn spring-boot:run
```

6. **API Gateway** (Port 8080)
```bash
cd api-gateway
mvn spring-boot:run
```

### Verify Services

- **Eureka Dashboard**: http://localhost:8761
- **Kafka UI**: http://localhost:8080 (if using Docker)
- **Config Server**: http://localhost:8888/actuator/health
- **API Gateway**: http://localhost:8080/actuator/health

## API Documentation

### Swagger UI

Access Swagger documentation for each service:

- **Employee Service**: http://localhost:8020/swagger-ui.html
- **Auth Service**: http://localhost:8010/swagger-ui.html

## Testing

### Run Unit Tests

```bash
# All services
mvn test

# Specific service
cd employee-service
mvn test
```

### Run Integration Tests

```bash
# Employee Service
cd employee-service
mvn verify -P integration-tests

# Auth Service
cd auth-service
mvn verify -P integration-tests
```

## Monitoring & Health Checks

### Actuator Endpoints

All services expose actuator endpoints:

```bash
# Health check
GET /actuator/health

# Metrics
GET /actuator/metrics

# Circuit breaker status (Auth Service)
GET /actuator/circuitbreakers
GET /actuator/circuitbreakerevents
```

### Kafka Monitoring

Access Kafka UI at: http://localhost:8080 (when running via Docker)

### Eureka Dashboard

Access service registry at: http://localhost:8761

## Troubleshooting

### Common Issues

#### 1. Services Can't Connect to Config Server

**Problem**: Services fail to start with "Could not locate PropertySource"

**Solution**:
- Ensure Config Server is running first
- Retry mechanism is configured (max 6 attempts)

#### 2. Kafka Connection Issues

**Problem**: Producer/Consumer can't connect to Kafka

**Solution**:
- Verify Kafka is running: `docker-compose ps`
- Check Kafka configuration in `employee-service.yml` and `notification-service.yml`
- Ensure ports 9092 and 29092 are not in use
- Fixed configuration uses INTERNAL (29092) and EXTERNAL (9092) listeners

#### 3. Circuit Breaker Open

**Problem**: Auth service can't reach Employee service

**Solution**:
- Check Employee service is running
- View circuit breaker status: `/actuator/circuitbreakers`
- Circuit breaker will auto-recover after 10 seconds
- Fallback mechanism returns user-friendly error

#### 4. Database Connection Errors

**Problem**: Services can't connect to PostgreSQL

**Solution**:
```bash
# Check PostgreSQL is running
docker-compose ps postgres

# View logs
docker-compose logs postgres

# Recreate database
docker-compose down -v
docker-compose up -d postgres
```

## Improvements & Recommendations

### Short-term Improvements

1. **Enhanced Security**
   - Implement OAuth2/OIDC for authentication
   - Add API rate limiting
   - Implement request signing
   - Add CORS configuration

2. **Observability**
   - Integrate distributed tracing (Zipkin/Jaeger)
   - Add centralized logging (ELK Stack)
   - Implement custom metrics and dashboards (Grafana)
   - Add application performance monitoring (APM)

3. **Testing**
   - Increase test coverage to >80%
   - Add contract testing (Pact)
   - Implement chaos engineering tests
   - Add performance/load testing (JMeter/Gatling)

### Medium-term Improvements

1. **Scalability**
   - Implement database read replicas
   - Add caching layer (Redis) for frequently accessed data
   - Implement message queue for heavy operations
   - Add horizontal pod autoscaling (HPA) for Kubernetes

2. **Resilience**
   - Implement bulkhead pattern
   - Add timeout configurations
   - Implement graceful degradation
   - Add health check probes for Kubernetes

3. **DevOps**
   - Migrate to Kubernetes for orchestration
   - Implement blue-green deployments
   - Add canary deployments
   - Implement infrastructure as code (Terraform)

4. **API Management**
   - Add API versioning strategy
   - Implement GraphQL for flexible queries
   - Add webhook support for event notifications
   - Implement API analytics

### Long-term Improvements

1. **Architecture**
   - Consider CQRS pattern for complex queries
   - Implement event sourcing for audit trail
   - Add saga pattern for distributed transactions
   - Consider service mesh (Istio/Linkerd)

2. **Data**
   - Implement multi-tenancy support
   - Add data encryption at rest
   - Implement GDPR compliance features
   - Add data anonymization for testing

3. **Business Features**
   - Add employee self-service portal
   - Implement approval workflows
   - Add reporting and analytics
   - Implement notification preferences

4. **Integration**
   - Add SSO integration
   - Implement LDAP/Active Directory integration
   - Add third-party HR system integrations
   - Implement webhook for external systems

### Technical Debt

1. **Code Quality**
   - Refactor large service classes
   - Implement consistent error handling
   - Add comprehensive JavaDoc
   - Implement code quality gates (SonarQube)

2. **Configuration**
   - Externalize all configuration
   - Implement feature flags
   - Add environment-specific configurations
   - Implement secrets management (Vault)

3. **Documentation**
   - Add architecture decision records (ADRs)
   - Create runbooks for operations
   - Add troubleshooting guides
   - Create onboarding documentation

## Support & Contact

For issues, questions, or contributions:
- Create an issue in the repository
- Contact the development team
- Refer to the project wiki for detailed documentation

## License

MIT License

Copyright (c) 2025 Daniel Oguejiofor

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all  
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  
SOFTWARE.

---

**Last Updated**: October 2025
**Version**: 1.0.0
