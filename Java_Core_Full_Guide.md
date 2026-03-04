# 📚 Java Core – Lộ Trình Học Thực Chiến cho Backend Developer

> **Giảng viên:** Senior Java Developer | **Mục tiêu:** Nắm vững Java Core để làm việc với Spring Boot
> **Phong cách:** Thực tế, đúng bản chất, không học vẹt

---

## 📋 Mục Lục

1. [PHẦN 1: Kiến Thức Nền Tảng](#phần-1-kiến-thức-nền-tảng)
   - [1.1 Biến, Kiểu Dữ Liệu, Toán Tử](#11-biến-kiểu-dữ-liệu-toán-tử)
   - [1.2 Cấu Trúc Điều Khiển](#12-cấu-trúc-điều-khiển)
   - [1.3 Mảng](#13-mảng)
2. [PHẦN 2: Lập Trình Hướng Đối Tượng (OOP)](#phần-2-lập-trình-hướng-đối-tượng-oop)
   - [2.1 Class & Object](#21-class--object)
   - [2.2 Constructor](#22-constructor)
   - [2.3 Encapsulation (Đóng Gói)](#23-encapsulation-đóng-gói)
   - [2.4 Inheritance (Kế Thừa)](#24-inheritance-kế-thừa)
   - [2.5 Polymorphism (Đa Hình)](#25-polymorphism-đa-hình)
   - [2.6 Abstraction (Trừu Tượng)](#26-abstraction-trừu-tượng)
   - [2.7 Interface vs Abstract Class](#27-interface-vs-abstract-class)
3. [PHẦN 3: Các Thành Phần Quan Trọng Trong Java Core](#phần-3-các-thành-phần-quan-trọng-trong-java-core)
   - [3.1 String & Xử Lý Chuỗi](#31-string--xử-lý-chuỗi)
   - [3.2 Wrapper Class](#32-wrapper-class)
   - [3.3 Exception Handling](#33-exception-handling)
   - [3.4 Collection Framework](#34-collection-framework)
   - [3.5 Generics](#35-generics)
4. [PHẦN 4: Kiến Thức Cần Cho Backend](#phần-4-kiến-thức-cần-cho-backend)
   - [4.1 equals() & hashCode()](#41-equals--hashcode)
   - [4.2 Comparable vs Comparator](#42-comparable-vs-comparator)
   - [4.3 Lambda Expression](#43-lambda-expression)
   - [4.4 Stream API](#44-stream-api)

---

# PHẦN 1: Kiến Thức Nền Tảng

## 1.1 Biến, Kiểu Dữ Liệu, Toán Tử

### 🧠 Bản Chất Vấn Đề

Java là **ngôn ngữ kiểu tĩnh (statically typed)** – nghĩa là mọi biến bắt buộc phải khai báo kiểu dữ liệu trước khi dùng. Compiler sẽ kiểm tra kiểu dữ liệu ngay lúc biên dịch, không phải lúc chạy.

Java chia kiểu dữ liệu làm 2 nhóm lớn:

```
Kiểu dữ liệu Java
├── Primitive (nguyên thủy) → Lưu giá trị trực tiếp trên Stack
└── Reference (tham chiếu) → Lưu địa chỉ trỏ đến Object trên Heap
```

### 📌 Các Kiểu Primitive Hay Dùng

| Kiểu | Kích thước | Giá trị mặc định | Ví dụ dùng |
|------|-----------|-----------------|------------|
| `int` | 4 bytes | 0 | ID, số lượng |
| `long` | 8 bytes | 0L | Timestamp, số lớn |
| `double` | 8 bytes | 0.0d | Tiền, tỷ lệ % |
| `boolean` | 1 bit | false | Trạng thái |
| `char` | 2 bytes | '\u0000' | Ký tự đơn |
| `String` | *(Reference)* | null | Văn bản |

> ⚠️ **Lưu ý quan trọng:** `String` KHÔNG phải primitive – đây là một class đặc biệt trong Java!

### 📝 Ví Dụ Code

```java
public class DataTypeDemo {
    public static void main(String[] args) {
        // Primitive types
        int age = 25;
        long userId = 1234567890L;   // Phải có 'L' ở cuối cho long
        double salary = 15000000.50;
        boolean isActive = true;

        // Reference type
        String name = "Nguyen Van A";
        String nullName = null;       // Reference có thể null, primitive thì không!

        // Khai báo hằng số với 'final'
        final int MAX_RETRY = 3;

        // Type casting – ép kiểu
        double price = 99.9;
        int priceInt = (int) price;   // = 99 (mất phần thập phân!)
        System.out.println(priceInt); // Output: 99

        // Integer overflow – lỗi hay gặp
        int maxInt = Integer.MAX_VALUE; // = 2,147,483,647
        System.out.println(maxInt + 1); // Output: -2147483648 (bị tràn số!)
    }
}
```

### ⚡ Toán Tử Quan Trọng

```java
// Toán tử số học
int a = 10, b = 3;
System.out.println(a / b);   // = 3 (chia nguyên, mất phần dư!)
System.out.println(a % b);   // = 1 (phần dư – dùng nhiều trong thực tế)
System.out.println(10.0 / 3);// = 3.333... (ít nhất 1 số phải là double)

// Toán tử so sánh
// == so sánh GIÁ TRỊ với primitive, so sánh ĐỊA CHỈ với reference
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");

System.out.println(s1 == s2);      // true  (String pool)
System.out.println(s1 == s3);      // false (khác địa chỉ!)
System.out.println(s1.equals(s3)); // true  ← LUÔN DÙNG equals() với String

// Toán tử logic
boolean result = (age > 18) && isActive;  // AND
boolean result2 = (age < 18) || isActive; // OR
boolean result3 = !isActive;              // NOT

// Ternary operator – hay dùng trong backend
String status = isActive ? "ACTIVE" : "INACTIVE";
```

### 🎯 Bài Tập

Viết một chương trình nhỏ:
1. Khai báo biến lưu thông tin một sản phẩm: `id` (long), `name` (String), `price` (double), `inStock` (boolean)
2. Tính tiền sau khi giảm giá 20%
3. In ra màn hình dòng: `"Sản phẩm [name] có giá [price] VND"`

---

## 1.2 Cấu Trúc Điều Khiển

### 🧠 Bản Chất Vấn Đề

Cấu trúc điều khiển quyết định **luồng thực thi** của chương trình. Trong backend, bạn sẽ dùng chúng rất nhiều để xử lý business logic.

### 📝 if – else

```java
// Cách đúng – xử lý null trước
public String getDiscount(String memberType) {
    if (memberType == null) {
        return "0%";
    }

    if (memberType.equals("GOLD")) {
        return "20%";
    } else if (memberType.equals("SILVER")) {
        return "10%";
    } else {
        return "0%";
    }
}
```

### 📝 switch – Khi Nào Dùng?

Dùng `switch` khi so sánh **một biến với nhiều giá trị cố định** – code đẹp hơn if-else lồng nhau.

```java
// Switch truyền thống
public double getShippingFee(String province) {
    switch (province) {
        case "HN":
        case "HCM":
            return 15000;
        case "DN":
            return 25000;
        default:
            return 35000;
    }
}

// Switch Expression (Java 14+) – viết gọn hơn, hay dùng trong thực tế
public double getShippingFeeModern(String province) {
    return switch (province) {
        case "HN", "HCM" -> 15000;
        case "DN"         -> 25000;
        default           -> 35000;
    };
}
```

### 📝 Vòng Lặp

```java
// for – khi biết số lần lặp
for (int i = 0; i < 5; i++) {
    System.out.println("Lần " + i);
}

// for-each – LUÔN DÙNG KHI duyệt collection/mảng
List<String> names = List.of("An", "Binh", "Cuong");
for (String name : names) {
    System.out.println(name);
}

// while – khi không biết trước số lần lặp
int retry = 0;
while (retry < 3) {
    boolean success = callExternalAPI();
    if (success) break;
    retry++;
}

// ⚠️ Tránh modify collection trong khi đang duyệt!
// Sẽ gây ConcurrentModificationException
```

### 🎯 Bài Tập

Viết method `calculateOrderTotal(List<Integer> quantities, List<Double> prices)`:
- Tính tổng tiền đơn hàng
- Nếu tổng > 500,000 thì giảm 10%
- Trả về tổng cuối cùng

---

## 1.3 Mảng

### 🧠 Bản Chất Vấn Đề

Mảng trong Java có **kích thước cố định** sau khi khởi tạo – đây là điểm khác biệt quan trọng so với `ArrayList`. Trong thực tế backend, bạn sẽ ít dùng mảng trực tiếp mà hay dùng `List`, nhưng phải hiểu mảng để hiểu cách Collection hoạt động.

```java
// Khai báo và khởi tạo
int[] scores = new int[5];          // Tạo mảng 5 phần tử, default = 0
String[] days = {"Mon", "Tue", "Wed"};  // Khởi tạo ngay

// Truy cập phần tử
System.out.println(days[0]);        // "Mon" – index bắt đầu từ 0
System.out.println(days.length);    // 3

// Duyệt mảng
for (int i = 0; i < scores.length; i++) {
    scores[i] = i * 10;
}

// Cách hay dùng hơn với for-each
for (String day : days) {
    System.out.println(day);
}

// Mảng 2 chiều – ít dùng nhưng cần biết
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6}
};
System.out.println(matrix[1][2]); // = 6

// Chuyển mảng thành List (hay dùng)
List<String> dayList = Arrays.asList(days);

// Sắp xếp mảng
int[] numbers = {5, 2, 8, 1, 9};
Arrays.sort(numbers); // {1, 2, 5, 8, 9}
```

### 🎯 Bài Tập

Viết method `findTopStudents(int[] scores, int topN)`:
- Tìm `topN` điểm cao nhất trong mảng `scores`
- Trả về mảng các điểm đó theo thứ tự giảm dần

---

# PHẦN 2: Lập Trình Hướng Đối Tượng (OOP)

> **Đây là phần quan trọng nhất!** Spring Boot được xây dựng hoàn toàn dựa trên OOP. Không hiểu OOP thì không thể hiểu Spring.

## 2.1 Class & Object

### 🧠 Bản Chất Vấn Đề

**Class** là bản thiết kế (blueprint). **Object** là sản phẩm được tạo ra từ bản thiết kế đó.

Ví dụ thực tế: Class `User` là thiết kế cho mọi user trong hệ thống. Mỗi user cụ thể (user ID 1, user ID 2...) là một object.

```
Class User (bản thiết kế)
├── Thuộc tính: id, name, email, password
└── Hành vi: login(), logout(), updateProfile()

Object user1 (thực thể cụ thể)
├── id = 1
├── name = "Nguyen Van A"
└── email = "a@gmail.com"
```

### 📝 Ví Dụ Code

```java
// Định nghĩa Class
public class User {
    // Fields (thuộc tính)
    private Long id;
    private String name;
    private String email;
    private boolean active;

    // Methods (hành vi)
    public String getDisplayName() {
        return name + " (" + email + ")";
    }

    public void deactivate() {
        this.active = false;
    }
}

// Sử dụng – tạo Object
public class Main {
    public static void main(String[] args) {
        User user1 = new User();  // new tạo object trên Heap
        User user2 = new User();  // user1 và user2 là 2 object khác nhau

        // user1 và user2 là reference variables (lưu địa chỉ, không lưu giá trị)
        User ref = user1;   // ref và user1 trỏ đến CÙNG một object!
    }
}
```

> ⚠️ **Hiểu rõ điều này:** Khi bạn truyền object vào method, Java truyền **bản copy của địa chỉ** (pass by value of reference). Thay đổi thuộc tính của object bên trong method sẽ ảnh hưởng ra ngoài!

```java
public void updateName(User user, String newName) {
    user.name = newName; // Ảnh hưởng đến object gốc!
    user = new User();   // Chỉ thay đổi biến local, KHÔNG ảnh hưởng bên ngoài
}
```

---

## 2.2 Constructor

### 🧠 Bản Chất Vấn Đề

Constructor là method đặc biệt được gọi khi `new` object. Mục đích: **đảm bảo object được khởi tạo ở trạng thái hợp lệ**.

```java
public class Product {
    private Long id;
    private String name;
    private double price;
    private int quantity;

    // Default constructor (tự động có nếu không khai báo constructor nào)
    public Product() {}

    // Parameterized constructor
    public Product(Long id, String name, double price) {
        // Validate trước khi gán – good practice!
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");

        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0; // default
    }

    // Constructor chaining – gọi constructor khác trong cùng class
    public Product(Long id, String name, double price, int quantity) {
        this(id, name, price); // Gọi constructor 3 tham số trước
        this.quantity = quantity;
    }
}

// Sử dụng
Product p1 = new Product(1L, "Laptop", 15000000.0);
Product p2 = new Product(2L, "Mouse", 500000.0, 50);
```

> 💡 **Trong Spring Boot**, bạn sẽ hay thấy constructor injection:
> ```java
> @Service
> public class OrderService {
>     private final OrderRepository orderRepository;
>
>     // Spring tự inject dependency qua constructor
>     public OrderService(OrderRepository orderRepository) {
>         this.orderRepository = orderRepository;
>     }
> }
> ```

### 🎯 Bài Tập

Tạo class `BankAccount` với:
- Thuộc tính: `accountNumber`, `ownerName`, `balance`
- Constructor đảm bảo `balance` khởi tạo không âm
- Method `deposit(double amount)` và `withdraw(double amount)` – withdraw cần kiểm tra đủ tiền

---

## 2.3 Encapsulation (Đóng Gói)

### 🧠 Bản Chất Vấn Đề

Encapsulation = **kiểm soát quyền truy cập vào dữ liệu**. Ẩn chi tiết bên trong, chỉ expose những gì cần thiết ra ngoài.

Tại sao quan trọng? Vì nó giúp:
- Bảo vệ tính toàn vẹn dữ liệu (validate trước khi set)
- Dễ thay đổi implementation mà không ảnh hưởng code bên ngoài
- Đây là nền tảng của mọi design pattern trong Spring

### Access Modifiers

| Modifier | Cùng class | Cùng package | Subclass | Khác package |
|----------|-----------|-------------|---------|-------------|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

### 📝 Ví Dụ Code

```java
public class Employee {
    private Long id;
    private String name;
    private double salary;
    private String department;

    // Getter – chỉ đọc
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getSalary() { return salary; }

    // Setter với validation – kiểm soát việc ghi
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative: " + salary);
        }
        this.salary = salary;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name.trim();
    }

    // Business method – đóng gói logic
    public void giveRaise(double percentage) {
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("Invalid raise percentage");
        }
        this.salary *= (1 + percentage / 100);
    }
}
```

> 💡 **Trong Spring Boot với JPA**, bạn sẽ hay dùng Lombok để tự generate getter/setter:
> ```java
> @Entity
> @Data  // Lombok tự tạo getter, setter, equals, hashCode, toString
> public class Employee {
>     @Id
>     private Long id;
>     private String name;
>     private double salary;
> }
> ```

---

## 2.4 Inheritance (Kế Thừa)

### 🧠 Bản Chất Vấn Đề

Inheritance cho phép một class **tái sử dụng code** từ class khác và **mở rộng thêm** chức năng. Đây là mối quan hệ "IS-A".

```
Vehicle (class cha)
├── Car extends Vehicle    (Car IS-A Vehicle)
└── Truck extends Vehicle  (Truck IS-A Vehicle)
```

### 📝 Ví Dụ Thực Tế (Backend Context)

```java
// Base class – chứa các thuộc tính/method chung
public class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    public void markAsDeleted() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters/setters...
}

// User kế thừa BaseEntity – có thêm id, createdAt, updatedAt tự động
public class User extends BaseEntity {
    private String username;
    private String email;
    private String passwordHash;

    // Chỉ cần code đặc thù của User
    public boolean isValidEmail() {
        return email != null && email.contains("@");
    }
}

// Product cũng kế thừa BaseEntity
public class Product extends BaseEntity {
    private String name;
    private double price;
    private int stock;
}
```

### Từ Khóa Quan Trọng

```java
public class Manager extends Employee {
    private List<Employee> subordinates;

    // Gọi constructor của class cha
    public Manager(Long id, String name, double salary) {
        super(id, name, salary); // PHẢI gọi đầu tiên trong constructor
    }

    // Override method của class cha
    @Override  // Luôn viết @Override để compiler kiểm tra
    public void giveRaise(double percentage) {
        // Manager được raise gấp đôi nhân viên thường
        super.giveRaise(percentage * 1.5); // Gọi method gốc của class cha
    }
}
```

> ⚠️ **Quy tắc vàng:** Java chỉ hỗ trợ **đơn kế thừa** (một class chỉ extends một class). Muốn đa kế thừa hành vi → dùng Interface.

### 🎯 Bài Tập

Tạo hierarchy:
- `Animal` (base): `name`, `sound()` method
- `Dog extends Animal`: override `sound()` trả về "Gâu gâu"
- `Cat extends Animal`: override `sound()` trả về "Meo meo"
- `ServiceDog extends Dog`: thêm `task` (ví dụ: "Guide", "Police")

---

## 2.5 Polymorphism (Đa Hình)

### 🧠 Bản Chất Vấn Đề

Polymorphism = **một object có thể hành xử theo nhiều hình thức khác nhau** tùy vào kiểu thực sự của nó. Đây là cái giúp code của bạn **linh hoạt và dễ mở rộng**.

Có 2 loại:
- **Compile-time (Method Overloading):** Cùng tên method, khác tham số
- **Runtime (Method Overriding):** Class con override method của class cha

### 📝 Ví Dụ Code

```java
// Overloading – compile-time polymorphism
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
    // Java chọn method nào dựa vào kiểu tham số lúc compile
}

// Overriding – runtime polymorphism (quan trọng hơn!)
public abstract class PaymentMethod {
    public abstract double processPayment(double amount);
    public String getReceipt(double amount) {
        double result = processPayment(amount);
        return "Paid: " + result;
    }
}

public class CreditCard extends PaymentMethod {
    private double feeRate = 0.02; // 2% fee

    @Override
    public double processPayment(double amount) {
        return amount * (1 + feeRate);
    }
}

public class BankTransfer extends PaymentMethod {
    @Override
    public double processPayment(double amount) {
        return amount; // Không mất phí
    }
}

// Đây là sức mạnh của Polymorphism!
public class PaymentService {
    // Method này KHÔNG cần biết đang dùng loại payment nào
    public void pay(PaymentMethod method, double amount) {
        String receipt = method.getReceipt(amount); // Gọi đúng implementation tại runtime
        System.out.println(receipt);
    }
}

// Sử dụng
PaymentService service = new PaymentService();
service.pay(new CreditCard(), 1000000);   // Gọi CreditCard.processPayment()
service.pay(new BankTransfer(), 1000000); // Gọi BankTransfer.processPayment()
// Thêm PaypalPayment? Chỉ cần extends PaymentMethod, không sửa PaymentService!
```

> 💡 **Đây chính là "Open/Closed Principle"** – code mở để mở rộng, đóng để sửa đổi. Spring Boot dùng điều này khắp nơi.

---

## 2.6 Abstraction (Trừu Tượng)

### 🧠 Bản Chất Vấn Đề

Abstraction = **ẩn đi sự phức tạp, chỉ expose những gì người dùng cần biết**. Bạn cần biết CÁI GÌ (what), không cần biết NHƯ THẾ NÀO (how).

```java
// Abstract class – không thể new trực tiếp
public abstract class ReportGenerator {
    // Template method pattern – xác định "khung" của thuật toán
    public final void generateReport(List<?> data) {
        List<?> processed = processData(data);  // Step 1
        String formatted = formatData(processed); // Step 2
        exportReport(formatted);                  // Step 3
    }

    // Subclass PHẢI implement các bước này
    protected abstract List<?> processData(List<?> data);
    protected abstract String formatData(List<?> data);
    protected abstract void exportReport(String content);
}

public class ExcelReportGenerator extends ReportGenerator {
    @Override
    protected List<?> processData(List<?> data) { /* ... */ return data; }
    @Override
    protected String formatData(List<?> data) { return "Excel format"; }
    @Override
    protected void exportReport(String content) { System.out.println("Exporting to Excel..."); }
}

public class PDFReportGenerator extends ReportGenerator {
    @Override
    protected List<?> processData(List<?> data) { /* ... */ return data; }
    @Override
    protected String formatData(List<?> data) { return "PDF format"; }
    @Override
    protected void exportReport(String content) { System.out.println("Exporting to PDF..."); }
}
```

---

## 2.7 Interface vs Abstract Class

### 🧠 Bản Chất Vấn Đề

Đây là câu hỏi phỏng vấn kinh điển. Cần hiểu **khi nào dùng cái nào**.

| Tiêu chí | Interface | Abstract Class |
|----------|-----------|----------------|
| Đa kế thừa | ✅ Một class có thể implement nhiều | ❌ Chỉ extend một |
| Trạng thái (fields) | ❌ Chỉ có constants | ✅ Có instance fields |
| Constructor | ❌ Không có | ✅ Có |
| Method có body | ✅ default method (Java 8+) | ✅ |
| Mối quan hệ | "CAN-DO" (có khả năng) | "IS-A" (là một loại) |

### 📝 Ví Dụ Code

```java
// Interface – định nghĩa "khả năng" (contract)
public interface Serializable {
    byte[] serialize();
    void deserialize(byte[] data);
}

public interface Cacheable {
    String getCacheKey();
    int getTTL(); // Time to live
}

public interface Auditable {
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
}

// Một class có thể implement NHIỀU interface
public class UserProfile implements Serializable, Cacheable, Auditable {
    // Bắt buộc implement tất cả methods của cả 3 interface
    @Override
    public byte[] serialize() { /* ... */ return new byte[0]; }

    @Override
    public String getCacheKey() { return "user:" + id; }

    // ...
}

// Abstract class – dùng khi có "cơ sở chung"
public abstract class BaseRepository<T, ID> {
    // Code chung cho mọi repository
    protected List<T> cache = new ArrayList<>();

    public Optional<T> findById(ID id) {
        return cache.stream()
            .filter(entity -> getId(entity).equals(id))
            .findFirst();
    }

    // Subclass phải implement – vì mỗi loại entity có cách lấy ID khác nhau
    protected abstract ID getId(T entity);
    public abstract void save(T entity);
}
```

### 🎯 Bài Tập

Thiết kế hệ thống notification:
1. Interface `Notifiable`: method `send(String recipient, String message)`
2. Class `EmailNotifier implements Notifiable`: gửi email (in ra console)
3. Class `SMSNotifier implements Notifiable`: gửi SMS (in ra console)
4. Class `NotificationService`: nhận một `List<Notifiable>` và gửi thông báo cho tất cả

---

# PHẦN 3: Các Thành Phần Quan Trọng Trong Java Core

## 3.1 String & Xử Lý Chuỗi

### 🧠 Bản Chất Vấn Đề

`String` trong Java là **immutable** (bất biến). Mọi thao tác trên String đều tạo ra **object mới**. Đây là nguồn gốc của nhiều bug hiệu năng trong backend.

```java
// Hiểu String Pool
String a = "hello";           // Lưu vào String Pool
String b = "hello";           // Lấy từ String Pool – cùng object!
String c = new String("hello"); // Tạo object mới trên Heap

System.out.println(a == b);      // true
System.out.println(a == c);      // false
System.out.println(a.equals(c)); // true  ← luôn dùng equals()

// String immutable
String s = "Hello";
s.toUpperCase();          // KHÔNG thay đổi s!
s = s.toUpperCase();      // Phải gán lại – tạo object mới

// Vấn đề hiệu năng
String result = "";
for (int i = 0; i < 10000; i++) {
    result += i;  // Tạo 10000 object mới! – RẤT TỒI
}

// Giải pháp: StringBuilder (mutable, không thread-safe)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);  // Chỉnh sửa trực tiếp – hiệu quả
}
String result2 = sb.toString();
```

### 📝 Các Method Hay Dùng Nhất

```java
String text = "  Hello, World!  ";

// Làm sạch chuỗi
text.trim();              // "Hello, World!" – bỏ khoảng trắng 2 đầu
text.strip();             // Giống trim() nhưng hỗ trợ Unicode (Java 11+)
text.toLowerCase();       // "  hello, world!  "
text.toUpperCase();       // "  HELLO, WORLD!  "

// Kiểm tra
text.isEmpty();           // false (có khoảng trắng)
text.isBlank();           // false (Java 11+, kiểm tra cả khoảng trắng)
"".isBlank();             // true
"   ".isBlank();          // true

text.contains("World");   // true
text.startsWith("  H");   // true
text.endsWith("!  ");     // true

// Trích xuất
text.substring(7, 12);    // "World" (từ index 7 đến 11)
text.indexOf("World");    // 9 (vị trí đầu tiên xuất hiện)
text.charAt(7);           // 'W'

// Biến đổi
text.replace("World", "Java");         // "  Hello, Java!  "
text.replaceAll("\\s+", " ");          // Thay thế khoảng trắng bằng regex

// Tách và nối
"a,b,c".split(",");           // ["a", "b", "c"]
String.join(", ", "a", "b");  // "a, b"
String.join("-", List.of("2024", "01", "15")); // "2024-01-15"

// String.format – format chuỗi
String msg = String.format("User %s has balance: %,.0f VND", "An", 1500000.0);
// "User An has balance: 1,500,000 VND"

// String.format hiện đại hơn (Java 15+)
String msg2 = "User %s logged in at %s".formatted("An", LocalDateTime.now());
```

### 🎯 Bài Tập

Viết method `formatPhoneNumber(String phone)`:
- Input: "0912345678" hoặc "+84912345678" hoặc "84912345678"
- Output luôn là: "+84912345678"
- Cần xử lý trim, kiểm tra null/blank

---

## 3.2 Wrapper Class

### 🧠 Bản Chất Vấn Đề

Java Collection (List, Map...) chỉ làm việc với **Object**, không làm việc với primitive. Wrapper class là cái "bọc" primitive thành Object.

```
int     → Integer
long    → Long
double  → Double
boolean → Boolean
char    → Character
```

### Autoboxing & Unboxing

```java
// Autoboxing – Java tự động chuyển primitive → Wrapper
Integer i = 5;  // Thực ra là Integer.valueOf(5)
List<Integer> list = new ArrayList<>();
list.add(100);  // Java tự autobox int → Integer

// Unboxing – Wrapper → primitive
int x = i;  // Thực ra là i.intValue()
int sum = 0;
for (Integer num : list) {
    sum += num;  // Unboxing mỗi lần lặp
}

// ⚠️ NullPointerException từ unboxing!
Integer value = null;
int result = value;  // NPE! Vì null.intValue() → crash
```

### Các Method Hay Dùng

```java
// Parse string → number (HAY DÙNG KHI nhận input từ API/form)
int age = Integer.parseInt("25");
long id = Long.parseLong("1234567890");
double price = Double.parseDouble("15.99");

// Xử lý lỗi khi parse
try {
    int invalid = Integer.parseInt("abc");
} catch (NumberFormatException e) {
    System.out.println("Invalid number format");
}

// So sánh – KHÔNG dùng == với Wrapper!
Integer a = 127;
Integer b = 127;
System.out.println(a == b);      // true  (cache -128 đến 127)

Integer c = 128;
Integer d = 128;
System.out.println(c == d);      // false (ngoài cache range!)
System.out.println(c.equals(d)); // true  ← luôn dùng equals()

// Các constant hữu ích
Integer.MAX_VALUE;  // 2,147,483,647
Integer.MIN_VALUE;  // -2,147,483,648
Double.MAX_VALUE;
```

---

## 3.3 Exception Handling

### 🧠 Bản Chất Vấn Đề

Exception handling là cách bạn **xử lý lỗi một cách có kiểm soát**. Trong backend, đây là cực kỳ quan trọng vì lỗi xảy ra ở khắp nơi: database, API call, validation...

```
Exception Hierarchy
├── Error (nghiêm trọng – không xử lý được: OutOfMemoryError)
└── Exception
    ├── Checked Exception (phải xử lý hoặc khai báo throws)
    │   ├── IOException
    │   └── SQLException
    └── Unchecked Exception (RuntimeException – không bắt buộc xử lý)
        ├── NullPointerException
        ├── IllegalArgumentException
        ├── IndexOutOfBoundsException
        └── NumberFormatException
```

### 📝 Ví Dụ Code

```java
// try-catch-finally
public String readUserFile(String userId) {
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(new FileReader("user_" + userId + ".txt"));
        return reader.readLine();
    } catch (FileNotFoundException e) {
        // Log lỗi, trả về giá trị mặc định
        logger.warn("User file not found for: " + userId);
        return null;
    } catch (IOException e) {
        // Wrap thành exception của application
        throw new RuntimeException("Failed to read user file", e); // Giữ original cause!
    } finally {
        // Luôn chạy dù có exception hay không – dùng để đóng resource
        if (reader != null) {
            try { reader.close(); } catch (IOException ignored) {}
        }
    }
}

// try-with-resources (Java 7+) – CÁCH HIỆN ĐẠI, tự đóng resource
public String readUserFileModern(String userId) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader("user_" + userId + ".txt"))) {
        return reader.readLine();
    } // reader.close() tự động được gọi
}

// Custom Exception – quan trọng trong Spring Boot!
public class UserNotFoundException extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
}

// Trong Spring Boot Service
public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id)); // Clean và expressive
}
```

### Best Practices

```java
// ❌ SAI – nuốt exception, không biết lỗi gì
try {
    doSomething();
} catch (Exception e) {
    // im lặng
}

// ❌ SAI – bắt Exception quá rộng mà không xử lý đúng
try {
    doSomething();
} catch (Exception e) {
    System.out.println("Error");
}

// ✅ ĐÚNG – xử lý từng loại exception, log đầy đủ, wrap hoặc re-throw
try {
    doSomething();
} catch (SpecificException e) {
    logger.error("Specific error occurred: {}", e.getMessage(), e);
    throw new ApplicationException("Operation failed", e);
}
```

### 🎯 Bài Tập

Viết class `UserService` với method `createUser(String name, String email, int age)`:
- Throw `IllegalArgumentException` nếu name null/blank
- Throw `IllegalArgumentException` nếu email không chứa "@"
- Throw `IllegalArgumentException` nếu age < 0 hoặc > 150
- Tạo custom exception `DuplicateEmailException` nếu email đã tồn tại (dùng một Set<String> để giả lập)

---

## 3.4 Collection Framework

### 🧠 Bản Chất Vấn Đề

Collection là **bộ công cụ xử lý nhóm dữ liệu**. Đây là thứ bạn dùng MỌI NGÀY trong backend.

```
Collection Hierarchy (phần hay dùng)
├── List (có thứ tự, cho phép trùng)
│   ├── ArrayList  – truy cập nhanh theo index
│   └── LinkedList – thêm/xóa đầu/cuối nhanh
├── Set (không trùng)
│   ├── HashSet      – nhanh nhất, không có thứ tự
│   ├── LinkedHashSet – giữ thứ tự insert
│   └── TreeSet      – sắp xếp tự động
└── Map (key-value)
    ├── HashMap      – nhanh nhất, không có thứ tự
    ├── LinkedHashMap – giữ thứ tự insert
    └── TreeMap      – sắp xếp theo key
```

### 📝 List

```java
// ArrayList – dùng khi cần truy cập theo index, ít thêm/xóa giữa chừng
List<String> names = new ArrayList<>();
names.add("An");
names.add("Binh");
names.add(0, "Cuong"); // Thêm vào vị trí 0

names.get(0);           // "Cuong"
names.size();           // 3
names.remove("An");     // Xóa theo giá trị
names.remove(0);        // Xóa theo index
names.contains("Binh"); // true

// Khởi tạo với giá trị
List<String> fixed = List.of("a", "b", "c");        // Immutable!
List<String> mutable = new ArrayList<>(List.of("a", "b")); // Mutable copy

// Sắp xếp
Collections.sort(names);                    // Alphabetical
names.sort(Comparator.reverseOrder());       // Reverse
names.sort(Comparator.comparingInt(String::length)); // Theo độ dài
```

### 📝 Set

```java
// HashSet – dùng khi cần kiểm tra tồn tại nhanh, không quan tâm thứ tự
Set<String> emails = new HashSet<>();
emails.add("a@gmail.com");
emails.add("b@gmail.com");
emails.add("a@gmail.com"); // Bị bỏ qua – Set không chứa trùng

emails.contains("a@gmail.com"); // O(1) – rất nhanh!
emails.size(); // 2

// Ứng dụng thực tế: loại bỏ duplicate
List<String> withDupes = List.of("a", "b", "a", "c", "b");
Set<String> unique = new LinkedHashSet<>(withDupes); // Giữ thứ tự
System.out.println(unique); // [a, b, c]
```

### 📝 Map – Hay Dùng Nhất!

```java
// HashMap – key-value store
Map<String, User> userCache = new HashMap<>();
userCache.put("user:1", new User());
userCache.put("user:2", new User());

// Lấy giá trị
User user = userCache.get("user:1");        // null nếu không tìm thấy
User user2 = userCache.getOrDefault("user:99", new User()); // Có giá trị mặc định

// Kiểm tra
userCache.containsKey("user:1");    // true
userCache.containsValue(someUser);  // Chậm hơn – duyệt toàn bộ map

// Duyệt Map
for (Map.Entry<String, User> entry : userCache.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

// Cách hiện đại hơn
userCache.forEach((key, value) -> System.out.println(key + ": " + value));

// Các method hay dùng (Java 8+)
userCache.putIfAbsent("user:1", new User()); // Chỉ put nếu key chưa có
userCache.computeIfAbsent("user:3", key -> loadFromDB(key)); // Tính giá trị khi key chưa có
userCache.remove("user:1");

// Đếm tần suất – pattern rất hay dùng
List<String> words = List.of("apple", "banana", "apple", "cherry", "banana", "apple");
Map<String, Integer> frequency = new HashMap<>();
for (String word : words) {
    frequency.merge(word, 1, Integer::sum);
    // Hoặc: frequency.put(word, frequency.getOrDefault(word, 0) + 1);
}
System.out.println(frequency); // {apple=3, banana=2, cherry=1}
```

### 🎯 Bài Tập

Viết method `groupStudentsByGrade(List<Student> students)`:
- `Student` có `name` và `score` (0-10)
- Grade: A (>=9), B (>=7), C (>=5), D (<5)
- Trả về `Map<String, List<String>>` với key là grade, value là danh sách tên

---

## 3.5 Generics

### 🧠 Bản Chất Vấn Đề

Generics cho phép viết code **làm việc với nhiều kiểu dữ liệu khác nhau** mà vẫn an toàn kiểu (type-safe). Không có generics, bạn phải dùng `Object` và cast thủ công – rất dễ lỗi.

```java
// Không có generics – nguy hiểm!
List rawList = new ArrayList();
rawList.add("hello");
rawList.add(123);  // Thêm được, nhưng...
String s = (String) rawList.get(1); // ClassCastException tại runtime!

// Có generics – an toàn
List<String> typedList = new ArrayList<>();
typedList.add("hello");
// typedList.add(123);  // Lỗi ngay lúc compile!
String s2 = typedList.get(0); // Không cần cast
```

### 📝 Generic Class & Method

```java
// Generic class – class làm việc với bất kỳ kiểu T nào
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;      // T có thể là User, Product, List<Order>, bất cứ thứ gì
    private int statusCode;

    // Static factory method
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.statusCode = 200;
        return response;
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.statusCode = statusCode;
        return response;
    }
}

// Sử dụng trong Controller (Spring Boot)
ApiResponse<User> userResponse = ApiResponse.success(user);
ApiResponse<List<Product>> productsResponse = ApiResponse.success(productList);
ApiResponse<Void> errorResponse = ApiResponse.error("Not found", 404);

// Generic method
public static <T extends Comparable<T>> T findMax(List<T> list) {
    if (list == null || list.isEmpty()) throw new IllegalArgumentException("List is empty");
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) max = item;
    }
    return max;
}

int maxInt = findMax(List.of(3, 1, 4, 1, 5)); // 5
String maxStr = findMax(List.of("banana", "apple", "cherry")); // "cherry"
```

---

# PHẦN 4: Kiến Thức Cần Cho Backend

## 4.1 equals() & hashCode()

### 🧠 Bản Chất Vấn Đề

Đây là một trong những điểm gây bug nhiều nhất và là câu hỏi phỏng vấn kinh điển.

**Rule bất biến:** Nếu `a.equals(b)` là `true`, thì `a.hashCode()` PHẢI bằng `b.hashCode()`

Ngược lại không bắt buộc, nhưng để HashMap hoạt động đúng, khi `hashCode` bằng nhau thì mới gọi `equals`.

```java
// Tại sao cần override?
public class Point {
    int x, y;
    public Point(int x, int y) { this.x = x; this.y = y; }
}

Point p1 = new Point(1, 2);
Point p2 = new Point(1, 2);

System.out.println(p1.equals(p2)); // false! (default: so sánh địa chỉ)

Set<Point> set = new HashSet<>();
set.add(p1);
set.contains(p2);  // false! Dù p1 và p2 "giống nhau" về mặt logic
```

### 📝 Cách Override Đúng

```java
public class Point {
    private final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;               // Cùng object → true
        if (o == null) return false;              // null → false
        if (getClass() != o.getClass()) return false; // Khác class → false
        Point point = (Point) o;
        return x == point.x && y == point.y;     // So sánh fields
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y); // Dùng Objects.hash() – dễ và đúng
    }
}

// Bây giờ hoạt động đúng
Point p1 = new Point(1, 2);
Point p2 = new Point(1, 2);
System.out.println(p1.equals(p2)); // true

Set<Point> set = new HashSet<>();
set.add(p1);
set.contains(p2); // true ✓
```

> 💡 **Trong Spring Boot với JPA**, Hibernate entity cần `equals` và `hashCode` dựa trên `id`. Lombok có `@EqualsAndHashCode` nhưng cần cẩn thận với lazy loading!

---

## 4.2 Comparable vs Comparator

### 🧠 Bản Chất Vấn Đề

Cả hai đều dùng để **so sánh và sắp xếp object**, nhưng dùng trong tình huống khác nhau.

- `Comparable`: Object tự biết cách so sánh bản thân (natural ordering)
- `Comparator`: Bên ngoài định nghĩa cách so sánh (custom ordering)

### 📝 Ví Dụ Code

```java
// Comparable – "tự nhiên" của class là sắp xếp theo gì
public class Product implements Comparable<Product> {
    private String name;
    private double price;
    private int salesCount;

    @Override
    public int compareTo(Product other) {
        // Negative: this < other → this đứng trước
        // Zero: bằng nhau
        // Positive: this > other → other đứng trước
        return Double.compare(this.price, other.price); // Sắp xếp theo giá tăng dần
    }
}

List<Product> products = getProducts();
Collections.sort(products);             // Dùng Comparable
products.sort(null);                    // Cũng dùng Comparable

// Comparator – khi muốn nhiều cách sắp xếp khác nhau
Comparator<Product> byPrice = Comparator.comparingDouble(Product::getPrice);
Comparator<Product> byName = Comparator.comparing(Product::getName);
Comparator<Product> byPriceDesc = Comparator.comparingDouble(Product::getPrice).reversed();
Comparator<Product> bySales = Comparator.comparingInt(Product::getSalesCount).reversed();

// Sắp xếp phức tạp – theo nhiều tiêu chí
Comparator<Product> complex = Comparator
    .comparingDouble(Product::getPrice)     // Trước theo giá tăng dần
    .thenComparing(Product::getName)        // Cùng giá thì theo tên
    .thenComparingInt(Product::getSalesCount).reversed(); // Rồi theo lượt bán giảm dần

products.sort(complex);
```

---

## 4.3 Lambda Expression

### 🧠 Bản Chất Vấn Đề

Lambda là cách viết **anonymous function (hàm vô danh)** ngắn gọn hơn. Thay vì tạo một class implement interface chỉ để dùng 1 lần, lambda cho phép viết thẳng vào.

Lambda chỉ dùng được với **Functional Interface** – interface có đúng 1 abstract method.

```java
// Cú pháp: (parameters) -> expression hoặc { statements }
// Không có lambda (trước Java 8)
Comparator<String> old = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
};

// Với lambda
Comparator<String> lambda = (a, b) -> a.compareTo(b);
Comparator<String> methodRef = String::compareTo; // Method reference – gọn hơn nữa
```

### 📝 Functional Interfaces Hay Dùng

```java
// Predicate<T> – nhận T, trả về boolean
Predicate<String> isLong = s -> s.length() > 5;
Predicate<String> isUpperCase = s -> s.equals(s.toUpperCase());
Predicate<String> isLongAndUpper = isLong.and(isUpperCase); // Kết hợp

// Function<T, R> – nhận T, trả về R
Function<String, Integer> strLen = String::length;
Function<String, String> trim = String::trim;
Function<String, String> trimAndLen = trim.andThen(s -> "Length: " + s.length()); // Chain

// Consumer<T> – nhận T, không trả về gì
Consumer<String> print = System.out::println;
Consumer<User> saveUser = user -> userRepository.save(user);

// Supplier<T> – không nhận gì, trả về T
Supplier<List<String>> listFactory = ArrayList::new;
Supplier<LocalDateTime> now = LocalDateTime::now;

// BiFunction<T, U, R> – nhận 2 tham số, trả về R
BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);

// Ứng dụng thực tế trong code
List<User> users = getUsers();

// Lọc user active
users.stream()
     .filter(user -> user.isActive())         // Predicate
     .filter(User::isActive)                  // Method reference – gọn hơn
     .map(user -> user.getName())             // Function
     .map(User::getName)                      // Method reference
     .forEach(System.out::println);           // Consumer
```

---

## 4.4 Stream API

### 🧠 Bản Chất Vấn Đề

Stream API cho phép **xử lý collection theo cách declarative (khai báo)** thay vì imperative (lệnh). Code ngắn hơn, dễ đọc hơn, và dễ song song hóa hơn.

**Stream KHÔNG sửa collection gốc** – nó tạo ra pipeline xử lý mới.

```
Source → Intermediate Operations → Terminal Operation
  ↓              ↓                        ↓
List/Set    filter, map, sorted      collect, count,
            limit, distinct          forEach, reduce
```

### 📝 Các Operation Hay Dùng Nhất

```java
List<Employee> employees = getEmployees();

// filter – lọc
List<Employee> seniorDevs = employees.stream()
    .filter(e -> e.getRole().equals("DEVELOPER"))
    .filter(e -> e.getYearsOfExperience() >= 5)
    .collect(Collectors.toList()); // Hoặc .toList() (Java 16+)

// map – biến đổi từng phần tử
List<String> emails = employees.stream()
    .map(Employee::getEmail)
    .map(String::toLowerCase)
    .collect(Collectors.toList());

// sorted – sắp xếp
List<Employee> sorted = employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary).reversed())
    .collect(Collectors.toList());

// distinct, limit, skip
employees.stream()
    .map(Employee::getDepartment)
    .distinct()          // Loại bỏ trùng
    .sorted()
    .limit(5)            // Chỉ lấy 5 phần tử đầu
    .skip(1)             // Bỏ qua 1 phần tử đầu
    .collect(Collectors.toList());

// Terminal operations
long count = employees.stream()
    .filter(e -> e.getSalary() > 10000000)
    .count();

double avgSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .average()
    .orElse(0);

Optional<Employee> highestPaid = employees.stream()
    .max(Comparator.comparing(Employee::getSalary));

boolean anyManager = employees.stream()
    .anyMatch(e -> e.getRole().equals("MANAGER"));

boolean allActive = employees.stream()
    .allMatch(Employee::isActive);

// collect – quan trọng nhất!
// Gom thành List
List<String> nameList = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.toList());

// Gom thành Set
Set<String> departmentSet = employees.stream()
    .map(Employee::getDepartment)
    .collect(Collectors.toSet());

// Gom thành Map
Map<Long, Employee> employeeMap = employees.stream()
    .collect(Collectors.toMap(Employee::getId, e -> e));

// Group by – CỰC KỲ HAY DÙNG!
Map<String, List<Employee>> byDepartment = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

Map<String, Long> countByDepartment = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.averagingDouble(Employee::getSalary)
    ));

// joining – nối chuỗi
String allNames = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));
// "[An, Binh, Cuong]"
```

### 📝 Ví Dụ Thực Tế Backend

```java
// Tình huống: Tìm top 3 sản phẩm bán chạy nhất của mỗi danh mục
public Map<String, List<Product>> getTop3ByCategory(List<Product> products) {
    return products.stream()
        .collect(Collectors.groupingBy(
            Product::getCategory,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> list.stream()
                    .sorted(Comparator.comparingInt(Product::getSalesCount).reversed())
                    .limit(3)
                    .collect(Collectors.toList())
            )
        ));
}

// Tình huống: Validate và transform request data
public List<UserDTO> processUserRequests(List<UserRequest> requests) {
    return requests.stream()
        .filter(r -> r.getEmail() != null && r.getEmail().contains("@"))
        .filter(r -> r.getAge() >= 18)
        .map(r -> new UserDTO(r.getName().trim(), r.getEmail().toLowerCase()))
        .distinct()
        .collect(Collectors.toList());
}
```

### 🎯 Bài Tập Tổng Hợp Stream API

Cho danh sách `Order` (có `orderId`, `customerId`, `amount`, `status`, `createdDate`):

1. Tính tổng doanh thu từ các đơn hàng có status "COMPLETED"
2. Tìm 5 khách hàng có tổng chi tiêu cao nhất (`Map<Long customerId, Double totalSpent>`)
3. Đếm số đơn hàng theo từng tháng trong năm hiện tại
4. Lấy danh sách `orderId` của các đơn hàng "PENDING" quá 7 ngày

---

# 🗺️ Bản Đồ Kiến Thức – Tổng Kết

```
Java Core cho Backend
├── Nền tảng (Phần 1)
│   ├── Biến & kiểu dữ liệu → hiểu Stack vs Heap
│   ├── Điều khiển luồng → if/switch/loop
│   └── Mảng → bước đệm sang Collection
│
├── OOP (Phần 2) ← QUAN TRỌNG NHẤT
│   ├── Class & Object → mọi thứ trong Java là Object
│   ├── Constructor → khởi tạo đúng → Spring DI
│   ├── Encapsulation → kiểm soát data → Spring Bean
│   ├── Inheritance → tái sử dụng code → BaseEntity, BaseService
│   ├── Polymorphism → linh hoạt → Spring IoC
│   ├── Abstraction → ẩn complexity → Service Layer
│   └── Interface → đa kế thừa hành vi → Repository Pattern
│
├── Java Core APIs (Phần 3)
│   ├── String → xử lý text, API response
│   ├── Wrapper → parse input, null safety
│   ├── Exception → error handling → @ControllerAdvice
│   ├── Collection → xử lý dữ liệu hàng ngày
│   └── Generics → type-safe → Repository<T, ID>
│
└── Backend Essential (Phần 4)
    ├── equals/hashCode → JPA Entity, Set/Map keys
    ├── Comparable/Comparator → sort data
    ├── Lambda → code ngắn gọn
    └── Stream API → xử lý data pipeline
```

---

> **💬 Ghi Chú Của Giảng Viên:**
> Hãy thực hành code mỗi bài tập trước khi đọc phần tiếp theo. Kiến thức Java Core không nằm ở việc đọc – nó nằm ở việc viết code, gặp lỗi, và debug. Khi bạn hiểu rõ phần nào, hãy báo để chúng ta đi sâu hơn hoặc chuyển sang phần tiếp theo!
