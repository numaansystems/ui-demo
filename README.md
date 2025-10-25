# UI Demo - Gateway Application

A Spring Boot 3 gateway application that serves as a central entry point for multiple internal organizational applications, supporting both modern and legacy tech stacks.

## Features

- **Modern Application Support**: Spring Boot 3, Angular, React, and other modern frameworks
- **Legacy Application Support**: Spring Framework 3, GWT, JSP, and Struts applications
- **Flexible Routing**: Multiple routing strategies for different application types
- **Minimal Integration Overhead**: Legacy applications can be integrated with minimal or no code changes

## Tech Stack

- **Spring Boot 3.3.2**: Core framework
- **Spring WebFlux**: Reactive web and routing
- **Spring Data JPA**: Data persistence layer
- **Thymeleaf**: Server-side template engine
- **H2 Database**: In-memory database for development
- **Angular 18**: Modern frontend UI (separate module)
- **Bootstrap 5**: UI styling
- **Java 17**: Programming language

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 18+ (for Angular UI)

### Building the Application

```bash
# Build the Spring Boot application
mvn clean install

# Build the Angular UI (optional)
cd ng-ui
npm install
ng build --configuration production
```

### Running the Application

```bash
# Run the Spring Boot application
mvn spring-boot:run

# Or run the compiled JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### Running Tests

```bash
mvn test
```

## Documentation

### [Route Configuration Guide](ROUTE_CONFIGURATION.md)

**Comprehensive documentation for configuring routes to support various application types:**

- **Step-by-step instructions** for adding new client routes to the gateway
- **Modern Spring Boot 3 applications** with examples using Angular, REST APIs, and SPAs
- **Legacy Spring Framework 3 applications** with GWT integration guidance
- **Best practices** for minimizing changes to legacy applications
- **Sample configurations** for common use cases
- **Troubleshooting guide** for common issues

**Key topics covered:**
- Controller-based routing
- Spring Cloud Gateway RouteLocator configuration
- WebClient-based proxying
- Session management for legacy apps
- GWT-specific routing requirements
- Static resource serving
- CORS configuration
- Performance optimization

➡️ **[Read the Full Route Configuration Guide](ROUTE_CONFIGURATION.md)**

## Project Structure

```
ui-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       ├── api/           # REST Controllers
│   │   │       ├── model/         # JPA Entities
│   │   │       ├── service/       # Business Logic
│   │   │       └── DemoApplication.java
│   │   └── resources/
│   │       ├── static/            # Static resources
│   │       ├── templates/         # Thymeleaf templates
│   │       └── application.properties
│   └── test/
│       └── java/                  # Test classes
├── ng-ui/                         # Angular frontend application
├── ROUTE_CONFIGURATION.md         # Route configuration documentation
├── HELP.md                        # Additional help and references
└── pom.xml                        # Maven configuration
```

## API Endpoints

The gateway exposes the following default endpoints:

### Records API
- `GET /api/records` - Get all records
- `GET /api/records/{id}` - Get record by ID
- `POST /api/records` - Create new record
- `DELETE /api/records/{id}` - Delete record

### Agencies API
- `GET /agencies` - Get all agencies
- `GET /agencies-categories` - Get agencies grouped by categories

### Home
- `GET /` - Home page (Thymeleaf template)

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Application name
spring.application.name=demo

# Thymeleaf configuration
spring.mvc.view.prefix=classpath:/templates/

# Vaadin configuration
vaadin.launch-browser=false
```

### Adding New Routes

See the [Route Configuration Guide](ROUTE_CONFIGURATION.md) for detailed instructions on adding routes for:
- Modern applications (Spring Boot, Angular, React, etc.)
- Legacy applications (Spring Framework 3, GWT, JSP, etc.)
- Microservices integration
- Static resource serving

## Angular UI

The Angular UI is located in the `ng-ui` directory. See [ng-ui/README.md](ng-ui/README.md) for Angular-specific documentation.

### Building Angular for Production

```bash
cd ng-ui
ng build --configuration production --output-path ../src/main/resources/static/ui
```

This builds the Angular app and places it in the gateway's static resources folder.

## Development

### Hot Reload (Spring Boot)

```bash
mvn spring-boot:run
```

Spring Boot DevTools is not included by default. To enable hot reload, add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Angular Development Server

```bash
cd ng-ui
ng serve
```

Access at `http://localhost:4200`

## Deployment

### Production Build

```bash
mvn clean package -DskipTests
```

The executable JAR will be created in `target/demo-0.0.1-SNAPSHOT.jar`

### Docker Deployment (Optional)

Create a `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
docker build -t ui-demo .
docker run -p 8080:8080 ui-demo
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-route`)
3. Commit your changes (`git commit -am 'Add new route configuration'`)
4. Push to the branch (`git push origin feature/new-route`)
5. Create a Pull Request

### Guidelines

- Follow existing code style and conventions
- Update documentation when adding new features
- Write tests for new functionality
- Update the [Route Configuration Guide](ROUTE_CONFIGURATION.md) when adding new routing patterns

## Troubleshooting

### Common Issues

**Application won't start**
- Check Java version (Java 17+ required)
- Verify port 8080 is not in use
- Check logs for specific error messages

**Routes not working**
- Verify route configuration in Java config classes
- Check application logs for routing errors
- See [Route Configuration Guide](ROUTE_CONFIGURATION.md) troubleshooting section

**Legacy app integration issues**
- Ensure session management is configured correctly
- Verify CORS settings if needed
- Check that backend services are accessible
- See [Legacy Applications section](ROUTE_CONFIGURATION.md#legacy-spring-framework-3gwt-applications) in Route Configuration Guide

## Additional Resources

- [ROUTE_CONFIGURATION.md](ROUTE_CONFIGURATION.md) - Comprehensive route configuration guide
- [HELP.md](HELP.md) - Spring Boot references and guides
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Angular Documentation](https://angular.dev/)

## License

This project is licensed under the terms specified in the parent project.

## Support

For questions or issues:
1. Check the [Route Configuration Guide](ROUTE_CONFIGURATION.md)
2. Review application logs
3. Consult Spring Boot and Spring Cloud Gateway documentation
4. Contact the platform team

---

**Version:** 0.0.1-SNAPSHOT  
**Last Updated:** 2025-10-25
