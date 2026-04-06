# 🚀 Quarkus Dành Cho Spring Boot Developer

> **Lời thầy:** Em đã biết Spring Boot rồi thì học Quarkus sẽ nhanh hơn nhiều. Thầy sẽ dạy theo kiểu so sánh — cái gì giống thì thầy nói ngắn, cái gì khác thì thầy giải thích kỹ. Let's go!

---

## 📋 Mục Lục

1. [Quarkus là gì? Tại sao lại có Quarkus?](#1-quarkus-là-gì)
2. [So sánh tổng quan với Spring Boot](#2-so-sánh-tổng-quan)
3. [Tạo Project đầu tiên](#3-tạo-project)
4. [Dependency Injection — CDI vs Spring DI](#4-dependency-injection)
5. [REST API — JAX-RS vs Spring MVC](#5-rest-api)
6. [Configuration — Giống nhưng khác chút](#6-configuration)
7. [JPA & Hibernate — Panache là điểm mới!](#7-jpa--panache)
8. [Reactive Programming](#8-reactive-programming)
9. [Dev Mode — Điểm mạnh cực lớn!](#9-dev-mode)
10. [Build Native Image — Khác hoàn toàn Spring!](#10-native-image)
11. [Testing](#11-testing)
12. [Bảng so sánh nhanh](#12-bảng-so-sánh-nhanh)
13. [Roadmap học tập](#13-roadmap)

---

## 1. Quarkus Là Gì?

Quarkus là một **Java framework** được tạo ra bởi Red Hat, thiết kế đặc biệt cho môi trường:
- ☁️ **Cloud-native** (Kubernetes, OpenShift)
- ⚡ **Serverless** (khởi động siêu nhanh)
- 🐳 **Container** (tối ưu bộ nhớ RAM)

### So sánh con số thực tế

| Chỉ số | Spring Boot (JVM) | Quarkus (JVM) | Quarkus (Native) |
|--------|-------------------|---------------|------------------|
| Thời gian khởi động | ~3-5 giây | ~0.8 giây | ~0.01 giây |
| RAM tiêu thụ (idle) | ~200MB | ~100MB | ~15MB |
| Kích thước JAR | ~20MB | ~15MB | Binary ~50MB |

> 💡 **Thầy nói thêm:** Spring Boot tối ưu cho **throughput cao** (chạy lâu dài). Quarkus tối ưu cho **startup time** và **RAM** — rất phù hợp khi deploy lên Kubernetes hay làm Serverless Function.

---

## 2. So Sánh Tổng Quan

```
Spring Boot                    Quarkus
─────────────────────────────────────────────────────
@SpringBootApplication    ↔    Không cần annotation main đặc biệt
Spring DI (Spring IoC)    ↔    CDI (Jakarta Contexts & Dependency Injection)
Spring MVC / WebFlux      ↔    JAX-RS (RESTEasy) / Vert.x Reactive
Spring Data JPA           ↔    Hibernate ORM với Panache
Spring Boot Test          ↔    @QuarkusTest
application.properties    ↔    application.properties (giống hệt!)
Maven / Gradle            ↔    Maven / Gradle (giống hệt!)
Actuator                  ↔    MicroProfile Health, Metrics
Spring Security           ↔    Quarkus Security / OIDC
@Scheduled                ↔    @Scheduled (giống hệt!)
```

---

## 3. Tạo Project

### Spring Boot — Em đã biết
```
https://start.spring.io
```

### Quarkus — Tương tự, có tool riêng

**Cách 1: Dùng trang web (giống start.spring.io)**
```
https://code.quarkus.io
```

**Cách 2: Dùng Maven CLI**
```bash
mvn io.quarkus.platform:quarkus-maven-plugin:3.8.0:create \
    -DprojectGroupId=com.example \
    -DprojectArtifactId=my-app \
    -Dextensions="resteasy-reactive,hibernate-orm-panache,jdbc-postgresql"
```

**Cách 3: Dùng Quarkus CLI**
```bash
# Cài CLI
brew install quarkusio/tap/quarkus   # MacOS
# hoặc: https://quarkus.io/guides/cli-tooling

# Tạo project
quarkus create app com.example:my-app \
    --extension="resteasy-reactive,hibernate-orm-panache"

# Chạy dev mode
quarkus dev
```

### Cấu trúc thư mục — Gần giống Spring Boot

```
my-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── GreetingResource.java   # Giống @RestController
│   │   │       └── GreetingService.java
│   │   └── resources/
│   │       ├── application.properties      # Quen thuộc rồi!
│   │       └── META-INF/resources/         # Static files
│   └── test/
│       └── java/
├── pom.xml
└── Dockerfile.native                       # Có sẵn!
```

---

## 4. Dependency Injection

> 🔑 **Đây là điểm KHÁC NHAU lớn nhất!** Spring dùng annotation của chính nó, còn Quarkus dùng **CDI (Jakarta EE standard)**.

### Spring Boot — Em quen rồi
```java
// Spring Boot
@Service
public class GreetingService {
    public String greet(String name) {
        return "Hello, " + name;
    }
}

@RestController
public class GreetingController {
    
    @Autowired  // hoặc constructor injection
    private GreetingService greetingService;
}
```

### Quarkus — CDI
```java
// Quarkus
@ApplicationScoped          // ← Thay cho @Service, @Component
public class GreetingService {
    public String greet(String name) {
        return "Hello, " + name;
    }
}

@Path("/hello")
public class GreetingResource {
    
    @Inject                 // ← Thay cho @Autowired
    GreetingService greetingService;
}
```

### Bảng so sánh Annotation DI

| Spring Boot | Quarkus (CDI) | Ý nghĩa |
|-------------|---------------|---------|
| `@Component` | `@ApplicationScoped` | Bean tồn tại suốt vòng đời app |
| `@Service` | `@ApplicationScoped` | (không có @Service riêng) |
| `@Repository` | `@ApplicationScoped` | (không có @Repository riêng) |
| `@Autowired` | `@Inject` | Inject dependency |
| `@Qualifier` | `@Named` | Phân biệt nhiều impl |
| `@Primary` | `@Default` | Impl mặc định |
| `@Scope("prototype")` | `@Dependent` | Tạo mới mỗi lần inject |
| `@RequestScope` | `@RequestScoped` | Sống theo HTTP request |
| `@SessionScope` | `@SessionScoped` | Sống theo HTTP session |

### ⚠️ Điểm khác quan trọng — CDI Scope

```java
// Spring Boot — @Service = Singleton mặc định
@Service
public class CounterService {
    private int count = 0;  // Shared toàn app
}

// Quarkus — Phải chọn scope rõ ràng!
@ApplicationScoped  // Singleton — dùng cái này thay @Service
public class CounterService {
    private int count = 0;  // Shared toàn app
}

@RequestScoped      // Mỗi HTTP request là 1 instance mới
public class RequestContext {
    private String requestId;
}

@Dependent          // Mỗi lần @Inject là instance mới (giống prototype)
public class TempProcessor {
}
```

---

## 5. REST API

### Spring Boot — Spring MVC
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<Product> getAll() { ... }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) { ... }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) { ... }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) { ... }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { ... }
}
```

### Quarkus — JAX-RS (RESTEasy)
```java
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GET
    public List<Product> getAll() { ... }

    @GET
    @Path("/{id}")
    public Product getById(@PathParam("id") Long id) { ... }

    @POST
    public Response create(Product product) {
        // Trả về Response để set status code
        return Response.status(Response.Status.CREATED)
                       .entity(product)
                       .build();
    }

    @PUT
    @Path("/{id}")
    public Product update(@PathParam("id") Long id, Product product) { ... }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return Response.noContent().build();
    }
}
```

### Bảng so sánh REST Annotation

| Spring MVC | JAX-RS (Quarkus) |
|------------|-----------------|
| `@RestController` | `@Path` (trên class) |
| `@RequestMapping("/path")` | `@Path("/path")` |
| `@GetMapping` | `@GET` |
| `@PostMapping` | `@POST` |
| `@PutMapping` | `@PUT` |
| `@DeleteMapping` | `@DELETE` |
| `@PathVariable` | `@PathParam` |
| `@RequestParam` | `@QueryParam` |
| `@RequestBody` | Không cần annotation (tự detect từ Content-Type) |
| `@RequestHeader` | `@HeaderParam` |
| `ResponseEntity<T>` | `Response` |
| `@ResponseStatus` | `Response.status(...)` |

### Exception Handling

```java
// Spring Boot
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}

// Quarkus — Dùng ExceptionMapper
@Provider
public class NotFoundExceptionMapper 
    implements ExceptionMapper<NotFoundException> {
    
    @Override
    public Response toResponse(NotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(new ErrorResponse(ex.getMessage()))
                       .build();
    }
}
```

---

## 6. Configuration

> 🎉 **Tin vui:** Phần này gần như **giống hệt** Spring Boot!

### application.properties
```properties
# Quarkus — Gần giống Spring Boot
quarkus.http.port=8080                          # server.port=8080
quarkus.datasource.db-kind=postgresql           # spring.datasource.url=...
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/mydb
quarkus.datasource.username=postgres
quarkus.datasource.password=secret
quarkus.hibernate-orm.database.generation=update  # spring.jpa.hibernate.ddl-auto=update
quarkus.log.level=INFO                          # logging.level.root=INFO
```

### Profile — Khác với Spring!

```properties
# Spring Boot dùng: application-dev.properties
# Quarkus dùng ký hiệu % trực tiếp trong 1 file!

# application.properties
quarkus.http.port=8080

# Override cho profile "dev"
%dev.quarkus.http.port=8081
%dev.quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb

# Override cho profile "prod"  
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://prod-server/mydb
```

```bash
# Chạy với profile cụ thể
./mvnw quarkus:dev -Dquarkus.profile=dev    # default khi dùng dev mode
./mvnw package -Pnative -Dquarkus.profile=prod
```

### @ConfigProperty — Thay cho @Value

```java
// Spring Boot
@Value("${app.greeting.message}")
private String message;

@Value("${app.max-retries:3}")  // Có default value
private int maxRetries;

// Quarkus
@ConfigProperty(name = "app.greeting.message")
String message;

@ConfigProperty(name = "app.max-retries", defaultValue = "3")
int maxRetries;

// Optional value
@ConfigProperty(name = "app.optional-feature")
Optional<String> optionalFeature;
```

### @ConfigMapping — Thay cho @ConfigurationProperties

```java
// Spring Boot
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private int timeout;
    // getters, setters...
}

// Quarkus — Dùng interface!
@ConfigMapping(prefix = "app")
public interface AppConfig {
    String name();
    int timeout();
    
    // Nested config
    DatabaseConfig database();
    
    interface DatabaseConfig {
        String url();
        int poolSize();
    }
}

// Inject và dùng
@Inject
AppConfig appConfig;

// Dùng
String name = appConfig.name();
String dbUrl = appConfig.database().url();
```

---

## 7. JPA & Panache

> 🌟 **Panache** là điểm nổi bật nhất của Quarkus — làm việc với JPA trở nên cực kỳ gọn gàng!

### Entity

```java
// Spring Boot — Entity bình thường
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price;
    
    // Phải có getters/setters
}

// Quarkus — Panache Entity (kế thừa PanacheEntity)
@Entity
@Table(name = "products")
public class Product extends PanacheEntity {
    // id được tự thêm bởi PanacheEntity!
    
    public String name;      // Public field — không cần getter/setter!
    public Double price;
    
    // Có thể thêm query methods ngay trong Entity (Active Record pattern)
    public static List<Product> findByPriceRange(Double min, Double max) {
        return list("price >= ?1 and price <= ?2", min, max);
    }
    
    public static Optional<Product> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
```

### Repository Pattern — Vẫn có nếu em thích!

```java
// Quarkus — Panache Repository (giống Spring Data JPA hơn)
@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    // Các method cơ bản đã có sẵn: findById, findAll, persist, delete...
    
    // Custom query
    public List<Product> findCheapProducts(Double maxPrice) {
        return list("price <= ?1", maxPrice);
    }
    
    public Optional<Product> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
```

### So sánh với Spring Data JPA

```java
// Spring Data JPA
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPriceLessThan(Double price);
    Optional<Product> findByName(String name);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") Double min, 
                                   @Param("max") Double max);
}

// Quarkus Panache Repository
@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    public List<Product> findByPriceLessThan(Double price) {
        return list("price < ?1", price);  // HQL ngắn hơn!
    }
    
    public Optional<Product> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
    
    public List<Product> findByPriceRange(Double min, Double max) {
        return list("price >= ?1 and price <= ?2", min, max);
    }
}
```

### Transaction

```java
// Spring Boot
@Service
@Transactional
public class ProductService { ... }

// Quarkus — Giống nhưng import khác!
@ApplicationScoped
public class ProductService {
    
    @Transactional  // import jakarta.transaction.Transactional
    public Product createProduct(Product product) {
        product.persist();  // Panache method
        return product;
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product.deleteById(id);  // Nếu dùng Active Record
    }
}
```

### CRUD Service hoàn chỉnh

```java
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductRepository productRepository;

    @GET
    public List<Product> getAll() {
        return productRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Product getById(@PathParam("id") Long id) {
        return productRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @POST
    @Transactional
    public Response create(Product product) {
        productRepository.persist(product);
        return Response.created(URI.create("/api/products/" + product.id))
                       .entity(product)
                       .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Product update(@PathParam("id") Long id, Product updated) {
        Product product = productRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
        product.name = updated.name;
        product.price = updated.price;
        return product;  // Tự động persist vì trong @Transactional
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        productRepository.deleteById(id);
        return Response.noContent().build();
    }
}
```

---

## 8. Reactive Programming

> ⚡ Đây là điểm Quarkus **mạnh hơn hẳn** Spring Boot. Quarkus được xây dựng trên **Vert.x** — reactive từ tầng lõi!

### RESTEasy Reactive

```java
// Thêm extension
// mvn quarkus:add-extension -Dextensions="resteasy-reactive"

@Path("/api/products")
public class ProductResource {

    @Inject
    ProductService productService;

    // Trả về Uni<T> — tương tự Mono<T> trong Spring WebFlux
    @GET
    @Path("/{id}")
    public Uni<Product> getById(@PathParam("id") Long id) {
        return productService.findById(id);
    }

    // Trả về Multi<T> — tương tự Flux<T> trong Spring WebFlux
    @GET
    public Multi<Product> getAll() {
        return productService.findAll();
    }
}
```

### So sánh Reactive Types

| Spring WebFlux | Quarkus Mutiny |
|----------------|----------------|
| `Mono<T>` | `Uni<T>` |
| `Flux<T>` | `Multi<T>` |
| `Publisher<T>` | `Publisher<T>` (tương thích) |

```java
// Spring WebFlux
Mono<Product> product = productService.findById(1L);
Flux<Product> products = productService.findAll();

// Quarkus Mutiny
Uni<Product> product = productService.findById(1L);
Multi<Product> products = productService.findAll();

// Chain operations — cú pháp gần giống
product
    .onItem().transform(p -> new ProductDTO(p))
    .onFailure().recoverWithItem(new ProductDTO())
    .subscribe().with(
        dto -> System.out.println("Got: " + dto),
        err -> System.err.println("Error: " + err)
    );
```

---

## 9. Dev Mode

> 🔥 **Dev Mode** của Quarkus là **feature killer** — không có gì tương đương trong Spring Boot!

### Chạy Dev Mode

```bash
./mvnw quarkus:dev
# hoặc
quarkus dev
```

### Tính năng Dev Mode

**1. Live Reload — Nhanh hơn Spring DevTools RẤT NHIỀU**
- Spring DevTools: reload ~2-3 giây (restart JVM)
- Quarkus Dev Mode: reload ~0.1-0.5 giây (hot swap thực sự!)

**2. Dev UI — Cổng thần kỳ tại `http://localhost:8080/q/dev`**
```
Truy cập: http://localhost:8080/q/dev

Bạn sẽ thấy:
✅ Quarkus Configuration (xem/sửa config realtime)
✅ Swagger UI (test API trực tiếp)
✅ Database management
✅ Scheduled jobs management
✅ Health checks
✅ Extensions marketplace
✅ Và hàng chục tính năng khác!
```

**3. Dev Services — KHÔNG CẦN CÀI DATABASE!**
```properties
# Chỉ cần khai báo db-kind, Quarkus tự khởi động Docker container!
quarkus.datasource.db-kind=postgresql

# Quarkus sẽ tự:
# 1. Pull Docker image postgresql
# 2. Khởi động container
# 3. Cấu hình connection cho app của bạn
# Tất cả tự động khi chạy dev mode!
```

> 💡 **Thầy khen:** Dev Services là lý do nhiều developer chuyển từ Spring Boot sang Quarkus. Không cần `docker-compose.yml`, không cần cấu hình, chỉ code!

**4. Continuous Testing**
```bash
# Bật continuous testing — test tự chạy khi code thay đổi!
# Nhấn 'r' trong terminal khi đang chạy dev mode
```

---

## 10. Native Image

> 🎯 Đây là điểm **KHÁC HOÀN TOÀN** với Spring Boot (Spring Native mới có, còn mới và kém ổn định hơn).

### Quarkus Native — Build binary thực sự

```bash
# Cần GraalVM, hoặc dùng Docker build (dễ hơn)

# Build native với Docker (không cần cài GraalVM)
./mvnw package -Pnative -Dquarkus.native.container-build=true

# Kết quả: file binary trong target/
# target/my-app-1.0-runner  (chạy trực tiếp, không cần JVM!)
```

### Chạy Native Binary

```bash
./target/my-app-1.0-runner
# Khởi động trong 0.01 giây!
# INFO  [io.quarkus] (main) my-app 1.0 native (powered by Quarkus) started in 0.012s
```

### Docker với Native Image

```dockerfile
# Dockerfile.native — Quarkus tạo sẵn!
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.9
WORKDIR /work/
COPY --chown=1001 target/*-runner /work/application

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

```bash
# Image size cực nhỏ!
docker build -f Dockerfile.native -t my-app-native .
docker images my-app-native
# REPOSITORY       TAG     IMAGE ID    SIZE
# my-app-native    latest  abc123      ~80MB  (so với 200-300MB của Spring Boot!)
```

### ⚠️ Lưu ý khi dùng Native Image

```
Một số thứ KHÔNG hoạt động với Native:
❌ Reflection không được hỗ trợ mặc định
❌ Dynamic class loading
❌ Runtime bytecode generation

Giải pháp:
✅ Quarkus tự xử lý 95% trường hợp
✅ Dùng @RegisterForReflection nếu cần
✅ Tránh dùng thư viện không tương thích native
```

```java
// Nếu cần reflection cho class nào đó
@RegisterForReflection
public class MyReflectionClass {
    // ...
}
```

---

## 11. Testing

### Giống Spring Boot về cơ bản

```java
// Spring Boot
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGetProduct() {
        ResponseEntity<Product> response = 
            restTemplate.getForEntity("/api/products/1", Product.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

// Quarkus
@QuarkusTest
class ProductResourceTest {
    
    @Test
    void testGetProduct() {
        given()
            .when().get("/api/products/1")
            .then()
                .statusCode(200)
                .body("name", is("Test Product"));
    }
}
```

### Mock — Khác annotation nhưng tương tự

```java
// Spring Boot
@MockBean
private ProductService productService;

// Quarkus — @InjectMock
@QuarkusTest
class ProductResourceTest {
    
    @InjectMock
    ProductService productService;
    
    @Test
    void testWithMock() {
        when(productService.findById(1L))
            .thenReturn(new Product("Test", 99.99));
        
        given()
            .when().get("/api/products/1")
            .then()
                .statusCode(200)
                .body("name", is("Test"));
    }
}
```

### Native Test — Chỉ có trong Quarkus!

```java
// Test với native binary thực sự!
@QuarkusIntegrationTest  // Chạy test với native binary
class ProductNativeIT extends ProductResourceTest {
    // Kế thừa tất cả test từ ProductResourceTest
    // Nhưng chạy trên native binary
}
```

---

## 12. Bảng So Sánh Nhanh

| Tính năng | Spring Boot | Quarkus | Giống/Khác |
|-----------|-------------|---------|------------|
| DI Framework | Spring IoC | CDI | ⚠️ Khác annotation |
| REST | Spring MVC | JAX-RS | ⚠️ Khác annotation |
| Config file | `application.properties` | `application.properties` | ✅ Giống |
| JPA | Spring Data JPA | Hibernate + Panache | ⚠️ Panache khác biệt |
| Transaction | `@Transactional` | `@Transactional` | ✅ Giống (import khác) |
| Reactive | Spring WebFlux | Mutiny + Vert.x | ⚠️ Khác type |
| Testing | `@SpringBootTest` | `@QuarkusTest` | ⚠️ Khác |
| Build tool | Maven/Gradle | Maven/Gradle | ✅ Giống |
| Dev reload | Spring DevTools | Dev Mode (nhanh hơn) | ✅ Có, nhanh hơn |
| Native build | Spring Native (beta) | GraalVM Native | ✅ Quarkus ổn định hơn |
| Profiles | file riêng | `%profile.` prefix | ⚠️ Khác cú pháp |
| Actuator/Health | Spring Actuator | MicroProfile Health | ⚠️ Khác |
| Scheduled | `@Scheduled` | `@Scheduled` | ✅ Giống |
| Events | `ApplicationEventPublisher` | CDI Events | ⚠️ Khác |
| Cache | `@Cacheable` | `@CacheResult` | ⚠️ Khác annotation |

---

## 13. Roadmap

### Tuần 1 — Làm quen
- [ ] Tạo project từ `code.quarkus.io`
- [ ] Chạy dev mode, khám phá Dev UI
- [ ] Viết REST API đơn giản (CRUD)
- [ ] Kết nối database với Panache

### Tuần 2 — Thực hành
- [ ] CDI scopes và lifecycle
- [ ] Configuration với `@ConfigProperty` và `@ConfigMapping`
- [ ] Exception handling với `ExceptionMapper`
- [ ] Unit test và Integration test

### Tuần 3 — Nâng cao
- [ ] Reactive với Mutiny
- [ ] Security (JWT / OIDC)
- [ ] Health checks và Metrics
- [ ] Build Native Image

### Tuần 4 — Production ready
- [ ] Containerize với Docker
- [ ] Deploy lên Kubernetes
- [ ] Tuning performance
- [ ] CI/CD pipeline

---

## 🎯 Extensions Hay Nhất Nên Biết

```bash
# Thêm extension
quarkus extension add <tên-extension>

# Các extension hay dùng:
resteasy-reactive              # REST API (reactive)
hibernate-orm-panache          # JPA với Panache
jdbc-postgresql                # PostgreSQL driver
jdbc-mysql                     # MySQL driver
smallrye-openapi               # Swagger UI tự động
quarkus-security-jwtm          # JWT Security
smallrye-health                # Health checks
micrometer-registry-prometheus # Metrics
scheduler                      # Cron jobs
mailer                         # Gửi email
redis-client                   # Redis
kafka-streams                  # Kafka
```

---

## 📚 Tài Liệu Tham Khảo

| Nguồn | Link |
|-------|------|
| Quarkus Official Guides | https://quarkus.io/guides/ |
| Quarkus Cheat Sheet | https://lordofthejars.github.io/quarkus-cheat-sheet/ |
| MicroProfile Spec | https://microprofile.io/ |
| Panache Docs | https://quarkus.io/guides/hibernate-orm-panache |
| Mutiny Docs | https://smallrye.io/smallrye-mutiny/ |

---

> 🏆 **Lời thầy cuối bài:** Em đã có nền Spring Boot, học Quarkus sẽ mất khoảng 1-2 tuần để thành thạo. Điểm mấu chốt cần nhớ: **CDI thay DI, JAX-RS thay Spring MVC, Panache thay Spring Data, Uni/Multi thay Mono/Flux**. Còn lại thì khái niệm gần như tương đương. Chúc em thành công với dự án!
