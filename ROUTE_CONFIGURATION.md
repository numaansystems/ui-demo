# Route Configuration Guide for Gateway Application

## Overview

This gateway application is built with Spring Boot 3 and serves as a central entry point for multiple internal organizational applications. It supports routing to both modern and legacy applications with minimal configuration changes required for client applications.

### Architecture

The gateway uses Spring Boot 3 with:
- **Spring WebFlux**: For reactive routing and proxying
- **Spring MVC**: For traditional controller-based routing
- **Thymeleaf**: For server-side template rendering
- **Spring Data JPA**: For data persistence

## Table of Contents

1. [Adding Routes for Modern Spring Boot 3 Applications](#modern-spring-boot-3-applications)
2. [Adding Routes for Legacy Spring Framework 3/GWT Applications](#legacy-spring-framework-3gwt-applications)
3. [Configuration Methods](#configuration-methods)
4. [Best Practices](#best-practices)
5. [Example Configurations](#example-configurations)

---

## Modern Spring Boot 3 Applications

### Overview
Modern Spring Boot 3 applications typically use REST APIs with JSON responses and may include modern frontend frameworks (Angular, React, Vue.js) or Thymeleaf templates.

### Step-by-Step Instructions

#### 1. Using Spring MVC Controller-Based Routing

For applications that serve HTML pages or REST APIs, create a controller to route requests:

**Example: Routing to a Modern Dashboard Application**

```java
package com.example.demo.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardRouteController {

    @GetMapping("/**")
    public String dashboardRoutes() {
        return "forward:/dashboard-app/index.html";
    }
}
```

#### 2. Using Spring WebFlux RouteLocator (Recommended for Proxying)

For applications that need to proxy requests to backend services, use RouteLocator:

**Create a Configuration Class:**

```java
package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModernAppRoutesConfig {

    @Bean
    public RouteLocator modernAppRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route for Analytics Application
            .route("analytics_app", r -> r
                .path("/analytics/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Gateway-Routed", "true")
                )
                .uri("http://analytics-service:8080"))
            
            // Route for Reporting Application
            .route("reporting_app", r -> r
                .path("/reports/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://reporting-service:8080"))
            
            .build();
    }
}
```

**Note:** To use Spring Cloud Gateway routing, add the following dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

And add the Spring Cloud BOM to your dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 3. Serving Static Resources

If your modern application is a Single Page Application (SPA), configure static resource serving:

**application.properties:**
```properties
# Modern App Static Resources
spring.web.resources.static-locations=classpath:/static/,classpath:/public/,classpath:/static/modern-app/
```

**Place your built SPA files in:**
```
src/main/resources/static/modern-app/
```

**Create a Controller to Handle SPA Routing:**
```java
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/modern-app")
public class ModernAppController {

    @GetMapping(value = {"", "/**"})
    public String forward(HttpServletRequest request) {
        // Forward to index.html for routes that don't end with a file extension
        // This allows the SPA router to handle all application routes
        String path = request.getRequestURI();
        // Check if path ends with a file extension (e.g., .js, .css, .png)
        if (!path.matches(".*\\.[a-zA-Z0-9]+$")) {
            return "forward:/modern-app/index.html";
        }
        return "forward:" + path;
    }
}
```

---

## Legacy Spring Framework 3/GWT Applications

### Overview
Legacy applications built with Spring Framework 3 and GWT require special consideration to minimize changes to the existing codebase while integrating with the gateway.

### Key Principles
1. **Preserve existing URLs**: Maintain the same URL structure the legacy app expects
2. **Minimal code changes**: Avoid modifying legacy application code
3. **Session compatibility**: Ensure session management works correctly
4. **Asset serving**: Properly route static assets (CSS, JS, GWT modules)

### Step-by-Step Instructions

#### 1. Proxy-Based Approach (Recommended)

This approach forwards all requests to the legacy application server without requiring changes to the legacy app.

**Configuration Class:**

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LegacyAppRoutesConfig {

    @Bean
    public WebClient legacyAppWebClient() {
        return WebClient.builder()
            .baseUrl("http://legacy-app-server:8080")
            .build();
    }
}
```

**Proxy Controller:**

```java
package com.example.demo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;

@Controller
@RequestMapping("/legacy")
public class LegacyAppProxyController {

    @Autowired
    private WebClient legacyAppWebClient;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<String>> proxyLegacyApp(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader HttpHeaders headers) {
        
        String path = request.getRequestURI().substring("/legacy".length());
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? path + "?" + queryString : path;
        
        return legacyAppWebClient
            .method(org.springframework.http.HttpMethod.valueOf(request.getMethod()))
            .uri(fullPath)
            .headers(h -> h.addAll(filterHeaders(headers)))
            .bodyValue(body != null ? body : "")
            .retrieve()
            .toEntity(String.class);
    }
    
    private HttpHeaders filterHeaders(HttpHeaders headers) {
        HttpHeaders filtered = new HttpHeaders();
        // Copy relevant headers, excluding hop-by-hop headers
        headers.forEach((name, values) -> {
            if (!name.equalsIgnoreCase("host") && 
                !name.equalsIgnoreCase("connection") &&
                !name.equalsIgnoreCase("transfer-encoding")) {
                filtered.addAll(name, values);
            }
        });
        return filtered;
    }
}
```

#### 2. Using Spring Cloud Gateway for Legacy Apps

**Configuration for Legacy GWT Application:**

```java
@Configuration
public class LegacyGwtRoutesConfig {

    @Bean
    public RouteLocator legacyGwtRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route for GWT Application Base
            .route("legacy_gwt_app", r -> r
                .path("/legacy-gwt/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .preserveHostHeader()
                    .addRequestHeader("X-Forwarded-Prefix", "/legacy-gwt")
                )
                .uri("http://legacy-gwt-server:8080"))
            
            // Route for GWT RPC Services
            .route("legacy_gwt_rpc", r -> r
                .path("/legacy-gwt/rpc/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .preserveHostHeader()
                )
                .uri("http://legacy-gwt-server:8080"))
            
            // Route for GWT Static Resources (JS, CSS, HTML, images)
            .route("legacy_gwt_static", r -> r
                .path("/legacy-gwt/**/*.js", "/legacy-gwt/**/*.css", 
                      "/legacy-gwt/**/*.html", "/legacy-gwt/**/*.png", 
                      "/legacy-gwt/**/*.jpg", "/legacy-gwt/**/*.gif")
                .filters(f -> f
                    .stripPrefix(1)
                    .setResponseHeader("Cache-Control", "public, max-age=3600")
                )
                .uri("http://legacy-gwt-server:8080"))
            
            .build();
    }
}
```

#### 3. Session Management for Legacy Apps

Ensure session cookies are properly configured:

**application.properties:**
```properties
# Session Configuration for Legacy Apps
server.servlet.session.cookie.name=JSESSIONID
server.servlet.session.cookie.path=/
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=false

# For production with HTTPS
# server.servlet.session.cookie.secure=true
```

#### 4. Handling GWT-Specific Requirements

GWT applications require special handling for:

**a. GWT RPC Requests**
- Preserve `Content-Type: text/x-gwt-rpc`
- Maintain session state
- Forward POST requests without modification

**b. GWT Nocache Files**
- Never cache `.nocache.js` files
- Set appropriate cache headers for `.cache.html` files

**Configuration Example:**

```java
@Configuration
public class GwtSpecificConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // GWT compiled resources (with caching)
        registry.addResourceHandler("/legacy-gwt/**/*.cache.html")
            .addResourceLocations("classpath:/static/legacy-gwt/")
            .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
        
        // GWT nocache resources (no caching)
        registry.addResourceHandler("/legacy-gwt/**/*.nocache.js")
            .addResourceLocations("classpath:/static/legacy-gwt/")
            .setCacheControl(CacheControl.noCache());
    }
}
```

---

## Configuration Methods

### Understanding Route Order

**Important:** Spring Cloud Gateway processes routes in the order they are defined, not by path specificity. When multiple routes could match a request, the first matching route in the definition order is used.

**Best Practice:**
- Define more specific routes **before** more general routes
- Example: `/reports/api/**` should be defined before `/reports/**`
- This ensures that API routes don't get caught by broader UI routes

### Method 1: Controller-Based Routing
**Best for:** Simple forwarding, serving static pages, or when you need custom logic

**Pros:**
- Simple and straightforward
- Full control over request handling
- Easy to debug

**Cons:**
- Manual implementation of proxy logic
- Less efficient for high-traffic scenarios

### Method 2: Spring Cloud Gateway RouteLocator
**Best for:** Microservices architecture, complex routing rules, high performance

**Pros:**
- Built-in load balancing
- Rich filter ecosystem
- High performance
- Declarative configuration

**Cons:**
- Requires additional dependencies
- Learning curve for complex configurations

### Method 3: WebClient-Based Proxy
**Best for:** Custom proxy logic, request/response transformation

**Pros:**
- Full control over HTTP client behavior
- Support for reactive programming
- Custom error handling

**Cons:**
- More code to maintain
- Manual header management

---

## Best Practices

### 1. URL Path Organization

Organize routes with clear prefixes:
```
/                          -> Gateway home page
/modern-app/**            -> Modern SPA application
/analytics/**             -> Analytics service
/reports/**               -> Reporting service
/legacy-gwt/**            -> Legacy GWT application
/legacy-spring/**         -> Legacy Spring Framework 3 app
```

### 2. Minimize Legacy Application Changes

**DO:**
- Use proxy-based routing to avoid code changes
- Preserve existing URL structures
- Maintain session compatibility
- Keep the same context path the legacy app expects

**DON'T:**
- Require legacy apps to change their URL patterns
- Modify authentication mechanisms
- Change session management without testing
- Force CORS configuration on legacy apps

### 3. Configuration Management

Store environment-specific configurations in separate files:

**application.properties (default):**
```properties
legacy.gwt.service.url=http://localhost:8080
modern.analytics.service.url=http://localhost:8081
```

**application-prod.properties:**
```properties
legacy.gwt.service.url=http://legacy-gwt-prod:8080
modern.analytics.service.url=http://analytics-prod:8081
```

### 4. Security Considerations

- **CORS Configuration:** Configure CORS appropriately for cross-origin requests
- **Authentication:** Implement centralized authentication at the gateway level
- **Authorization:** Validate permissions before forwarding requests
- **HTTPS:** Use HTTPS in production with proper SSL configuration

### 5. Performance Optimization

- **Caching:** Implement caching for static resources
- **Connection Pooling:** Configure connection pools for WebClient
- **Timeouts:** Set appropriate timeouts for backend services

```properties
# Connection timeout
spring.webflux.client.connect-timeout=5000

# Read timeout
spring.webflux.client.read-timeout=30000
```

### 6. Monitoring and Logging

Add logging for route matching and proxying:

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RouteController {
    
    @RequestMapping("/**")
    public String handleRequest(HttpServletRequest request) {
        log.info("Routing request: {} {}", request.getMethod(), request.getRequestURI());
        // routing logic
    }
}
```

---

## Example Configurations

### Example 1: Modern Angular Application

**Scenario:** Route an Angular SPA served from the gateway

**Step 1: Build Angular app**
```bash
cd ng-ui
ng build --configuration production --output-path ../src/main/resources/static/ui
```

**Step 2: Add .gitignore entry**
```
src/main/resources/static/ui
```

**Step 3: Create Controller**
```java
@Controller
@RequestMapping("/ui")
public class AngularAppController {

    @GetMapping(value = {"", "/{path:^(?!.*\\.).*$}/**"})
    public String forwardAngularRoutes() {
        return "forward:/ui/index.html";
    }
}
```

**Step 4: Configure in application.properties**
```properties
spring.web.resources.static-locations=classpath:/static/
```

### Example 2: Legacy GWT Inventory Management System

**Scenario:** Integrate a legacy GWT-based inventory system with minimal changes

**Requirements:**
- Legacy app runs on `http://inventory-legacy:9090`
- Uses context path `/inventory`
- Requires session persistence
- Has GWT RPC services at `/inventory/rpc/inventoryService`

**Implementation:**

```java
package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryLegacyRoutes {

    @Bean
    public RouteLocator inventoryRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Main application routes
            .route("inventory_main", r -> r
                .path("/inventory/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .preserveHostHeader()
                    .addRequestHeader("X-Forwarded-Proto", "http")
                    .addRequestHeader("X-Forwarded-Port", "8080")
                )
                .uri("http://inventory-legacy:9090"))
            .build();
    }
}
```

**No changes required to the legacy application!**

### Example 3: Modern Spring Boot 3 Reporting Service

**Scenario:** Route a modern microservice with REST APIs

**Service Details:**
- Service runs on `http://reporting-service:8082`
- Provides REST APIs at `/api/reports/**`
- Requires JWT authentication

**Implementation:**

```java
@Configuration
public class ReportingServiceRoutes {

    @Bean
    public RouteLocator reportingRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route for API requests (defined first to match before the general UI route)
            // Note: Routes are processed in definition order, not by path specificity
            .route("reporting_api", r -> r
                .path("/reports/api/**")
                .filters(f -> f
                    .stripPrefix(1)
                    // Add custom header - for dynamic values, use a custom filter
                    .addRequestHeader("X-Gateway-Request", "true")
                    // Add JWT token validation filter if needed
                )
                .uri("http://reporting-service:8082"))
            
            // Route for UI requests (defined after API route to avoid matching API paths)
            .route("reporting_ui", r -> r
                .path("/reports/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://reporting-service:8082"))
            .build();
    }
}
```

**Note:** For dynamic header values (like UUIDs or timestamps), you'll need to implement a custom GatewayFilterFactory. Static headers can be added directly as shown above.

### Example 4: Multiple Legacy Applications

**Scenario:** Gateway routes to 3 different legacy applications

```java
@Configuration
public class MultipleLegacyAppsConfig {

    @Bean
    public RouteLocator multipleLegacyRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // HR Management System (Legacy Spring 3 + JSP)
            .route("hr_legacy", r -> r
                .path("/hr/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .preserveHostHeader()
                )
                .uri("http://hr-legacy:8080"))
            
            // Finance System (Legacy Spring 3 + GWT)
            .route("finance_legacy", r -> r
                .path("/finance/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .preserveHostHeader()
                )
                .uri("http://finance-legacy:8081"))
            
            // Customer Portal (Legacy Struts)
            .route("customer_legacy", r -> r
                .path("/customer/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .preserveHostHeader()
                )
                .uri("http://customer-legacy:8082"))
            
            .build();
    }
}
```

---

## Testing Your Routes

### 1. Local Testing

Start your gateway application and test routes:

```bash
# Test modern app route
curl http://localhost:8080/modern-app/

# Test legacy app route
curl http://localhost:8080/legacy-gwt/

# Test API route
curl http://localhost:8080/api/reports
```

### 2. Integration Testing

Create integration tests for your routes:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouteIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testModernAppRoute() {
        ResponseEntity<String> response = restTemplate.getForEntity("/modern-app/", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLegacyAppRoute() {
        ResponseEntity<String> response = restTemplate.getForEntity("/legacy-gwt/", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: 404 Not Found on Legacy App Routes
**Solution:** Verify the `stripPrefix` filter is configured correctly and the backend service is accessible.

#### Issue 2: Session Lost After Routing
**Solution:** Ensure `preserveHostHeader()` is enabled and session cookies are configured properly.

#### Issue 3: GWT RPC Calls Failing
**Solution:** Check that `Content-Type` headers are preserved and POST request bodies are not modified.

#### Issue 4: Static Resources Not Loading
**Solution:** Verify resource handler configuration and check browser developer tools for correct paths.

#### Issue 5: CORS Errors
**Solution:** Add CORS configuration for cross-origin requests:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
```

---

## Maintenance and Updates

### Adding a New Application

1. **Identify the application type** (modern vs. legacy)
2. **Choose appropriate routing method** (Controller, RouteLocator, or WebClient)
3. **Create configuration class** following examples above
4. **Test locally** before deploying
5. **Update this documentation** with the new route
6. **Monitor logs** after deployment

### Removing an Application

1. Delete or comment out the route configuration
2. Remove any associated controllers or proxy classes
3. Update application.properties if needed
4. Restart the gateway
5. Update this documentation

---

## Additional Resources

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [GWT Documentation](http://www.gwtproject.org/doc/latest/DevGuide.html)
- [Spring Boot 3 Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

## Support

For questions or issues with route configuration:
1. Check this documentation first
2. Review application logs for routing errors
3. Consult the Spring Cloud Gateway documentation
4. Contact the platform team for assistance

---

**Last Updated:** 2025-10-25
**Version:** 1.0
