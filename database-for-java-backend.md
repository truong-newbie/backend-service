# 🗄️ Database Toàn Tập Cho Java Backend Developer
> Từ cơ bản đến nâng cao — Hướng dẫn thực chiến

---

## 📚 MỤC LỤC

1. [Database là gì? Tại sao cần học?](#1-database-là-gì)
2. [Các loại Database](#2-các-loại-database)
3. [Relational Database & SQL Cơ Bản](#3-sql-cơ-bản)
4. [SQL Nâng Cao](#4-sql-nâng-cao)
5. [Database Design & Normalization](#5-database-design)
6. [Index & Query Optimization](#6-index--optimization)
7. [Transaction & ACID](#7-transaction--acid)
8. [Java & JDBC](#8-java--jdbc)
9. [JPA & Hibernate (ORM)](#9-jpa--hibernate)
10. [Spring Data JPA](#10-spring-data-jpa)
11. [Connection Pooling](#11-connection-pooling)
12. [NoSQL Database](#12-nosql)
13. [Database Migration (Flyway/Liquibase)](#13-database-migration)
14. [Caching Strategy](#14-caching)
15. [Bảo Mật Database](#15-bảo-mật)
16. [Best Practices & Checklist](#16-best-practices)

---

## 1. Database Là Gì?

### Khái niệm cơ bản

**Database (Cơ sở dữ liệu)** là nơi lưu trữ dữ liệu một cách có tổ chức, cho phép truy xuất, thêm, sửa, xóa dễ dàng.

**DBMS (Database Management System)** là phần mềm quản lý database. Ví dụ: MySQL, PostgreSQL, Oracle, MongoDB...

```
Ứng dụng Java  →  DBMS  →  Database (files trên disk)
```

### Tại sao không dùng file thông thường?

| Tiêu chí | File thường | Database |
|---|---|---|
| Tìm kiếm nhanh | ❌ Phải đọc toàn bộ | ✅ Index, query |
| Đồng thời nhiều user | ❌ Conflict | ✅ Transaction |
| Bảo mật | ❌ Khó kiểm soát | ✅ Phân quyền |
| Backup/Recovery | ❌ Thủ công | ✅ Tích hợp sẵn |
| Quan hệ dữ liệu | ❌ Khó | ✅ Foreign key |

---

## 2. Các Loại Database

### 2.1 Relational Database (SQL)

Dữ liệu lưu trong **bảng (table)**, các bảng liên kết với nhau qua **khóa (key)**.

**Phổ biến nhất:**
- **MySQL/MariaDB** — phổ biến, miễn phí, dùng nhiều trong web
- **PostgreSQL** — mạnh mẽ, hỗ trợ JSON, full-text search
- **Oracle** — enterprise, tốn phí
- **H2** — in-memory, dùng để test Java

### 2.2 NoSQL Database

Không dùng bảng, phù hợp dữ liệu phi cấu trúc hoặc quy mô lớn.

| Loại | Ví dụ | Dùng khi |
|---|---|---|
| Document | MongoDB, Firestore | Dữ liệu linh hoạt, JSON |
| Key-Value | Redis, DynamoDB | Cache, session |
| Column | Cassandra, HBase | Dữ liệu time-series, big data |
| Graph | Neo4j | Mạng xã hội, quan hệ phức tạp |

> 💡 **Lời khuyên cho người mới:** Học SQL thật vững trước, NoSQL học sau.

---

## 3. SQL Cơ Bản

### 3.1 Cài đặt MySQL (local)

```bash
# Docker (khuyến nghị — sạch, không cài lung tung)
docker run --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=myapp \
  -p 3306:3306 \
  -d mysql:8.0
```

### 3.2 Kiểu dữ liệu thường gặp

```sql
-- Số
INT, BIGINT, DECIMAL(10,2), FLOAT, DOUBLE

-- Chuỗi
VARCHAR(255)    -- độ dài biến đổi (thường dùng nhất)
CHAR(10)        -- độ dài cố định
TEXT            -- văn bản dài
LONGTEXT        -- rất dài

-- Ngày giờ
DATE            -- 2024-01-15
TIME            -- 14:30:00
DATETIME        -- 2024-01-15 14:30:00
TIMESTAMP       -- tự cập nhật khi thay đổi

-- Khác
BOOLEAN / TINYINT(1)
ENUM('active','inactive')
JSON            -- PostgreSQL & MySQL 5.7+
```

### 3.3 Tạo & Xóa Database/Table

```sql
-- Database
CREATE DATABASE myapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE myapp;
DROP DATABASE myapp;

-- Table
CREATE TABLE users (
    id         BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100),
    age        INT          CHECK (age >= 0 AND age <= 150),
    is_active  BOOLEAN      DEFAULT TRUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Xóa table
DROP TABLE users;

-- Thay đổi cấu trúc table
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users DROP COLUMN phone;
ALTER TABLE users MODIFY COLUMN full_name VARCHAR(200);
ALTER TABLE users RENAME COLUMN full_name TO name;
```

### 3.4 CRUD — 4 thao tác cơ bản

```sql
-- ✅ CREATE — Thêm dữ liệu
INSERT INTO users (username, email, password, full_name, age)
VALUES ('john_doe', 'john@email.com', 'hashed_pw', 'John Doe', 25);

-- Thêm nhiều dòng cùng lúc
INSERT INTO users (username, email, password) VALUES
    ('alice', 'alice@email.com', 'pw1'),
    ('bob',   'bob@email.com',   'pw2'),
    ('carol', 'carol@email.com', 'pw3');

-- ✅ READ — Đọc dữ liệu
SELECT * FROM users;                          -- tất cả cột
SELECT id, username, email FROM users;        -- chỉ 3 cột
SELECT * FROM users WHERE age > 20;           -- có điều kiện
SELECT * FROM users WHERE is_active = TRUE AND age BETWEEN 18 AND 30;
SELECT * FROM users WHERE username LIKE 'j%'; -- bắt đầu bằng 'j'
SELECT * FROM users ORDER BY age DESC;        -- sắp xếp
SELECT * FROM users LIMIT 10 OFFSET 20;       -- phân trang (trang 3 với 10/trang)

-- ✅ UPDATE — Cập nhật
UPDATE users SET age = 26, updated_at = NOW()
WHERE id = 1;

-- ⚠️ NGUY HIỂM: không có WHERE thì cập nhật TẤT CẢ
UPDATE users SET is_active = FALSE; -- Xóa tất cả active!

-- ✅ DELETE — Xóa
DELETE FROM users WHERE id = 1;
DELETE FROM users WHERE is_active = FALSE;

-- ⚠️ NGUY HIỂM: không có WHERE thì xóa TẤT CẢ
DELETE FROM users; -- Xóa hết dữ liệu!
```

### 3.5 Hàm tổng hợp (Aggregate Functions)

```sql
SELECT COUNT(*)        FROM users;              -- tổng số dòng
SELECT COUNT(DISTINCT email) FROM users;        -- đếm email không trùng
SELECT AVG(age)        FROM users;              -- trung bình
SELECT MAX(age)        FROM users;              -- lớn nhất
SELECT MIN(age)        FROM users;              -- nhỏ nhất
SELECT SUM(salary)     FROM employees;          -- tổng

-- GROUP BY: nhóm dữ liệu
SELECT age, COUNT(*) AS total
FROM users
GROUP BY age
ORDER BY total DESC;

-- HAVING: lọc sau khi GROUP BY (khác WHERE là lọc trước GROUP BY)
SELECT age, COUNT(*) AS total
FROM users
GROUP BY age
HAVING total > 5; -- chỉ lấy nhóm có hơn 5 người
```

---

## 4. SQL Nâng Cao

### 4.1 JOIN — Kết hợp bảng

```sql
-- Setup ví dụ
CREATE TABLE orders (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    product    VARCHAR(100),
    amount     DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- INNER JOIN: chỉ lấy dòng có liên kết ở CẢ 2 bảng
SELECT u.username, o.product, o.amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id;

-- LEFT JOIN: lấy TẤT CẢ users, kể cả chưa có đơn hàng
SELECT u.username, o.product, o.amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id;
-- users không có orders sẽ có o.product = NULL

-- RIGHT JOIN: lấy TẤT CẢ orders
SELECT u.username, o.product
FROM users u
RIGHT JOIN orders o ON u.id = o.user_id;

-- Nhiều JOIN
SELECT u.username, o.product, p.category
FROM users u
JOIN orders o    ON u.id = o.user_id
JOIN products p  ON o.product_id = p.id
WHERE u.is_active = TRUE;
```

**Minh họa JOIN:**
```
users:              orders:
id | name           id | user_id | product
1  | Alice          1  | 1       | Laptop
2  | Bob            2  | 1       | Mouse
3  | Carol          3  | 3       | Phone

INNER JOIN → 3 dòng (Alice×2, Carol×1)
LEFT JOIN  → 4 dòng (Alice×2, Bob×NULL, Carol×1)
```

### 4.2 Subquery (Truy vấn lồng nhau)

```sql
-- Tìm user có amount > mức trung bình
SELECT username FROM users
WHERE id IN (
    SELECT user_id FROM orders
    WHERE amount > (SELECT AVG(amount) FROM orders)
);

-- EXISTS: kiểm tra có tồn tại không
SELECT username FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = u.id
);
```

### 4.3 Window Functions (PostgreSQL & MySQL 8+)

Rất hữu ích trong báo cáo, phân tích.

```sql
-- ROW_NUMBER: đánh số thứ tự trong mỗi nhóm
SELECT
    username,
    age,
    ROW_NUMBER() OVER (ORDER BY age DESC) AS rank
FROM users;

-- RANK: xếp hạng (có thể bị bỏ số khi trùng)
-- DENSE_RANK: xếp hạng liên tục

-- Xếp hạng theo từng nhóm
SELECT
    department,
    username,
    salary,
    RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS dept_rank
FROM employees;

-- Running total (tổng tích lũy)
SELECT
    created_at,
    amount,
    SUM(amount) OVER (ORDER BY created_at) AS running_total
FROM orders;

-- LAG/LEAD: lấy giá trị dòng trước/sau
SELECT
    month,
    revenue,
    LAG(revenue) OVER (ORDER BY month)  AS prev_month,
    revenue - LAG(revenue) OVER (ORDER BY month) AS growth
FROM monthly_revenue;
```

### 4.4 CTE (Common Table Expression)

Code dễ đọc hơn subquery phức tạp.

```sql
-- WITH ... AS (...)
WITH active_users AS (
    SELECT id, username, email
    FROM users
    WHERE is_active = TRUE
),
user_orders AS (
    SELECT user_id, COUNT(*) AS order_count, SUM(amount) AS total
    FROM orders
    GROUP BY user_id
)
SELECT
    au.username,
    COALESCE(uo.order_count, 0) AS orders,
    COALESCE(uo.total, 0)       AS total_spent
FROM active_users au
LEFT JOIN user_orders uo ON au.id = uo.user_id
ORDER BY total_spent DESC;
```

### 4.5 Stored Procedure & Function (Tùy chọn)

```sql
-- Stored Procedure
DELIMITER $$
CREATE PROCEDURE GetUserOrders(IN p_user_id BIGINT)
BEGIN
    SELECT o.id, o.product, o.amount, o.created_at
    FROM orders o
    WHERE o.user_id = p_user_id
    ORDER BY o.created_at DESC;
END$$
DELIMITER ;

CALL GetUserOrders(1);

-- Function
DELIMITER $$
CREATE FUNCTION CalculateDiscount(amount DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    IF amount > 1000 THEN
        RETURN amount * 0.9;  -- 10% discount
    ELSE
        RETURN amount;
    END IF;
END$$
DELIMITER ;
```

---

## 5. Database Design

### 5.1 Các loại Relationship

```
One-to-One (1:1)
  User ←→ UserProfile
  Một user có một profile

One-to-Many (1:N)  ← phổ biến nhất
  User ←→ Orders
  Một user có nhiều đơn hàng

Many-to-Many (M:N)
  Student ←→ Course
  Một học sinh học nhiều khóa, một khóa có nhiều học sinh
  → Cần bảng trung gian: enrollments(student_id, course_id)
```

**Thiết kế Many-to-Many:**
```sql
CREATE TABLE students (id BIGINT PRIMARY KEY, name VARCHAR(100));
CREATE TABLE courses  (id BIGINT PRIMARY KEY, title VARCHAR(100));

-- Bảng junction (trung gian)
CREATE TABLE enrollments (
    student_id BIGINT,
    course_id  BIGINT,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    grade DECIMAL(4,2),
    PRIMARY KEY (student_id, course_id),  -- composite key
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE
);
```

### 5.2 Normalization (Chuẩn hóa)

Mục đích: tránh dữ liệu trùng lặp (redundancy), dễ bảo trì.

**❌ Bảng chưa chuẩn hóa:**
```
orders: id | customer_name | customer_email | customer_city | product | price
         1 | Alice         | a@mail.com     | Hanoi         | Laptop  | 1000
         2 | Alice         | a@mail.com     | Hanoi         | Mouse   | 30
```
> Vấn đề: Alice đổi email → phải cập nhật nhiều dòng!

**✅ Sau chuẩn hóa (3NF):**
```sql
CREATE TABLE customers (
    id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(100),
    email VARCHAR(100),
    city  VARCHAR(100)
);

CREATE TABLE orders (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT REFERENCES customers(id),
    product     VARCHAR(100),
    price       DECIMAL(10,2)
);
```

**Các dạng chuẩn:**
- **1NF**: Mỗi ô chứa một giá trị đơn (không có list, không lặp cột)
- **2NF**: Thỏa 1NF + mọi cột phụ thuộc vào toàn bộ primary key
- **3NF**: Thỏa 2NF + không có phụ thuộc bắc cầu (A→B→C)

> 💡 Trong thực tế, đến 3NF là đủ. Đôi khi **denormalize** (bớt chuẩn hóa) để tăng hiệu năng đọc.

### 5.3 Primary Key & Foreign Key

```sql
-- Primary Key: định danh duy nhất mỗi dòng
id BIGINT PRIMARY KEY AUTO_INCREMENT  -- auto tăng (MySQL)
id BIGserial PRIMARY KEY              -- auto tăng (PostgreSQL)
id UUID PRIMARY KEY DEFAULT gen_random_uuid()  -- UUID

-- Foreign Key với Cascade
FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE   -- xóa user → tự xóa orders
    ON DELETE SET NULL  -- xóa user → set user_id = NULL
    ON DELETE RESTRICT  -- xóa user bị chặn nếu còn orders (default)
    ON UPDATE CASCADE   -- cập nhật id user → tự cập nhật orders
```

---

## 6. Index & Optimization

### 6.1 Index là gì?

Index như **mục lục** của sách. Không có index → database phải quét toàn bộ bảng (Full Table Scan).

```sql
-- Xem query plan TRƯỚC KHI tối ưu
EXPLAIN SELECT * FROM users WHERE email = 'john@email.com';
-- Nếu thấy "type: ALL" → đang full scan → cần index!

-- Tạo index
CREATE INDEX idx_users_email    ON users(email);
CREATE INDEX idx_users_age      ON users(age);
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- Unique index
CREATE UNIQUE INDEX idx_users_username ON users(username);

-- Composite index (thứ tự cột quan trọng!)
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);
-- Tốt cho: WHERE user_id = 1 AND created_at > '2024-01-01'
-- Tốt cho: WHERE user_id = 1 (dùng cột đầu)
-- Không tốt cho: WHERE created_at > '2024-01-01' (bỏ cột đầu)

-- Xem indexes hiện có
SHOW INDEX FROM users;

-- Xóa index
DROP INDEX idx_users_age ON users;
```

### 6.2 Khi nào nên/không nên dùng Index?

**Nên dùng:**
- Cột thường xuyên dùng trong WHERE, JOIN ON, ORDER BY
- Foreign key columns
- Cột có nhiều giá trị phân biệt (high cardinality)

**Không nên dùng:**
- Bảng nhỏ (< 1000 dòng)
- Cột ít giá trị phân biệt (ví dụ: gender chỉ có M/F)
- Bảng thường xuyên INSERT/UPDATE (index làm chậm ghi)

### 6.3 Query Optimization Tips

```sql
-- ❌ Không dùng hàm trên cột index (phá index)
SELECT * FROM users WHERE YEAR(created_at) = 2024;
-- ✅ Thay bằng range
SELECT * FROM users WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01';

-- ❌ Không dùng LIKE với wildcard đầu
SELECT * FROM users WHERE username LIKE '%john%';
-- ✅ Dùng LIKE đầu chuỗi hoặc full-text search
SELECT * FROM users WHERE username LIKE 'john%';

-- ❌ SELECT * (lấy thừa dữ liệu)
SELECT * FROM users JOIN orders ON users.id = orders.user_id;
-- ✅ Chỉ lấy cột cần
SELECT users.username, orders.product, orders.amount ...

-- ❌ N+1 Query problem (rất phổ biến với ORM)
for (User user : users) {
    List<Order> orders = getOrdersByUserId(user.getId()); // N queries!
}
-- ✅ Dùng JOIN một lần
SELECT u.*, o.* FROM users u LEFT JOIN orders o ON u.id = o.user_id;

-- Phân trang hiệu quả
-- ❌ Chậm khi offset lớn
SELECT * FROM orders ORDER BY id LIMIT 10 OFFSET 100000;
-- ✅ Keyset pagination
SELECT * FROM orders WHERE id > 100000 ORDER BY id LIMIT 10;
```

---

## 7. Transaction & ACID

### 7.1 Transaction là gì?

Nhóm các thao tác **phải thành công tất cả hoặc thất bại tất cả**.

**Ví dụ:** Chuyển tiền ngân hàng
```sql
-- Phải cùng thành công/thất bại
UPDATE accounts SET balance = balance - 500 WHERE id = 1;  -- trừ người gửi
UPDATE accounts SET balance = balance + 500 WHERE id = 2;  -- cộng người nhận
```

### 7.2 ACID Properties

| Tính chất | Ý nghĩa | Ví dụ |
|---|---|---|
| **Atomicity** | Tất cả hoặc không có gì | Chuyển tiền: cả hai cập nhật hoặc không cập nhật nào |
| **Consistency** | Dữ liệu luôn hợp lệ | Tổng tiền trước và sau phải bằng nhau |
| **Isolation** | Transaction độc lập nhau | 2 người cùng đặt vé: không tranh nhau |
| **Durability** | Dữ liệu bền vững sau commit | Mất điện vẫn không mất dữ liệu đã commit |

### 7.3 SQL Transaction

```sql
START TRANSACTION;  -- hoặc BEGIN

UPDATE accounts SET balance = balance - 500 WHERE id = 1;
UPDATE accounts SET balance = balance + 500 WHERE id = 2;

-- Kiểm tra
SELECT balance FROM accounts WHERE id IN (1, 2);

COMMIT;    -- Xác nhận, lưu thay đổi
-- hoặc
ROLLBACK;  -- Hủy bỏ toàn bộ

-- Savepoint
START TRANSACTION;
  UPDATE ...;
  SAVEPOINT sp1;
  UPDATE ...;  -- nếu cái này lỗi
  ROLLBACK TO SAVEPOINT sp1;  -- quay về sp1, không rollback toàn bộ
COMMIT;
```

### 7.4 Isolation Levels

```sql
-- Xem mức hiện tại
SELECT @@transaction_isolation;

-- Thiết lập
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

| Level | Dirty Read | Non-Repeatable Read | Phantom Read | Hiệu năng |
|---|---|---|---|---|
| READ UNCOMMITTED | ✅ có | ✅ có | ✅ có | Cao nhất |
| READ COMMITTED | ❌ không | ✅ có | ✅ có | Cao |
| REPEATABLE READ | ❌ không | ❌ không | ✅ có | Trung bình |
| SERIALIZABLE | ❌ không | ❌ không | ❌ không | Thấp nhất |

> 💡 **Mặc định MySQL:** REPEATABLE READ. **PostgreSQL:** READ COMMITTED.

### 7.5 Deadlock

```
Transaction A:  Lock table_1 → chờ table_2
Transaction B:  Lock table_2 → chờ table_1
→ Deadlock! Database sẽ kill một trong hai.
```

**Phòng tránh:** Luôn lock table theo cùng thứ tự, giữ transaction ngắn gọn.

---

## 8. Java & JDBC

### 8.1 JDBC là gì?

**JDBC (Java Database Connectivity)** = API chuẩn của Java để kết nối database.

```
Java App → JDBC API → JDBC Driver → Database
```

### 8.2 Setup Maven Dependencies

```xml
<!-- pom.xml -->
<dependencies>
    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.1</version>
    </dependency>
</dependencies>
```

### 8.3 Kết nối cơ bản

```java
import java.sql.*;

public class DatabaseExample {

    // Connection string format:
    // jdbc:mysql://host:port/database?params
    private static final String URL = "jdbc:mysql://localhost:3306/myapp"
            + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "root123";

    public static void main(String[] args) {
        // try-with-resources → tự đóng connection
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("✅ Kết nối thành công!");
            System.out.println("Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
        }
    }
}
```

### 8.4 CRUD với JDBC

```java
public class UserRepository {
    
    // ❌ TUYỆT ĐỐI KHÔNG dùng String concatenation → SQL Injection!
    // String sql = "SELECT * FROM users WHERE id = " + id;
    
    // ✅ Luôn dùng PreparedStatement
    
    // CREATE
    public long createUser(String username, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            
            // Lấy ID vừa insert
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Insert thất bại");
    }
    
    // READ
    public User findById(long id) throws SQLException {
        String sql = "SELECT id, username, email, age, created_at FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null; // hoặc Optional.empty()
    }
    
    // READ ALL với pagination
    public List<User> findAll(int page, int size) throws SQLException {
        String sql = "SELECT * FROM users LIMIT ? OFFSET ?";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }
        return users;
    }
    
    // UPDATE
    public boolean updateUser(long id, String email) throws SQLException {
        String sql = "UPDATE users SET email = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setLong(2, id);
            
            return ps.executeUpdate() > 0; // true nếu có dòng bị ảnh hưởng
        }
    }
    
    // DELETE
    public boolean deleteUser(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    // Helper: map ResultSet → User object
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setAge(rs.getInt("age"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
```

### 8.5 Transaction với JDBC

```java
public void transferMoney(long fromId, long toId, double amount) throws SQLException {
    Connection conn = null;
    try {
        conn = getConnection();
        conn.setAutoCommit(false); // Bắt đầu transaction
        
        // Thao tác 1
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE accounts SET balance = balance - ? WHERE id = ?")) {
            ps.setDouble(1, amount);
            ps.setLong(2, fromId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Tài khoản nguồn không tồn tại");
        }
        
        // Thao tác 2
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE id = ?")) {
            ps.setDouble(1, amount);
            ps.setLong(2, toId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Tài khoản đích không tồn tại");
        }
        
        conn.commit(); // Thành công → commit
        System.out.println("✅ Chuyển tiền thành công");
        
    } catch (SQLException e) {
        if (conn != null) conn.rollback(); // Thất bại → rollback
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

---

## 9. JPA & Hibernate

### 9.1 ORM là gì?

**ORM (Object-Relational Mapping)** = ánh xạ giữa Java class và database table.

```
Java Class User  ←→  Database Table users
   field id      ←→     column id
   field name    ←→     column name
   User object   ←→     1 row
```

**JPA** = Specification (đặc tả), **Hibernate** = Implementation (triển khai phổ biến nhất).

### 9.2 Dependencies

```xml
<dependencies>
    <!-- Spring Boot tích hợp JPA + Hibernate -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
</dependencies>
```

### 9.3 Entity Class

```java
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users",
       indexes = { @Index(name = "idx_email", columnList = "email") },
       uniqueConstraints = { @UniqueConstraint(columnNames = {"username"}) })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    private Integer age;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist // Tự động set trước khi INSERT
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate  // Tự động set trước khi UPDATE
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters, Setters...
    // (dùng Lombok @Data để tự sinh)
}
```

### 9.4 Relationships trong JPA

```java
// One-to-Many (User có nhiều Orders)
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;

    // mappedBy: tên field "user" trong Order class
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}

// Many-to-One (Order thuộc về 1 User)
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String product;
    private BigDecimal amount;
}

// Many-to-Many
@Entity
public class Student {
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "enrollments",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}

// One-to-One
@Entity
public class User {
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### 9.5 FetchType — Quan trọng!

```java
// LAZY: chỉ load khi access (khuyến nghị cho collection)
@OneToMany(fetch = FetchType.LAZY)
private List<Order> orders; // Chưa load, chỉ load khi gọi getOrders()

// EAGER: load ngay cùng với entity (default cho @ManyToOne, @OneToOne)
@ManyToOne(fetch = FetchType.EAGER)
private User user; // Load user ngay khi load Order
```

**⚠️ N+1 Problem:**
```java
// ❌ N+1: load 100 users → 100 query load orders
List<User> users = userRepo.findAll(); // 1 query
for (User u : users) {
    u.getOrders().size(); // N queries! (LAZY load)
}

// ✅ Dùng JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();
```

---

## 10. Spring Data JPA

### 10.1 Repository Pattern

```java
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

// Chỉ cần khai báo interface, Spring tự tạo implementation!
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===== Derived Query Methods (tự sinh từ tên method) =====
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByIsActiveTrue();
    List<User> findByAgeGreaterThan(int age);
    List<User> findByAgeBetween(int min, int max);
    List<User> findByUsernameContainingIgnoreCase(String keyword);
    List<User> findByIsActiveTrueOrderByCreatedAtDesc();
    boolean existsByEmail(String email);
    long countByIsActiveTrue();
    void deleteByIsActiveFalse();

    // ===== JPQL Query =====
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByUsername(@Param("keyword") String keyword);

    // ===== Native SQL =====
    @Query(value = "SELECT * FROM users WHERE age > :age LIMIT :limit",
           nativeQuery = true)
    List<User> findUsersOlderThan(@Param("age") int age, @Param("limit") int limit);

    // ===== Modifying Query =====
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    int deactivateUser(@Param("id") Long id);

    // ===== Pagination =====
    Page<User> findByIsActiveTrue(Pageable pageable);

    // ===== Projection (chỉ lấy một số field) =====
    @Query("SELECT u.id as id, u.username as username, u.email as email FROM User u")
    List<UserSummary> findAllSummary();
}

// Projection interface
public interface UserSummary {
    Long getId();
    String getUsername();
    String getEmail();
}
```

### 10.2 Service Layer

```java
@Service
@Transactional // Tất cả method trong class này dùng transaction
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection (khuyến nghị hơn @Autowired)
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest req) {
        // Kiểm tra duplicate
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + req.getEmail());
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        return userRepo.save(user);
    }

    @Transactional(readOnly = true) // Tối ưu cho read-only operations
    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepo.findByIsActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public User updateUser(Long id, UpdateUserRequest req) {
        User user = getUserById(id);
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getAge() != null) user.setAge(req.getAge());
        return userRepo.save(user); // save() = INSERT nếu mới, UPDATE nếu có id
    }

    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepo.deleteById(id);
    }
}
```

### 10.3 application.properties / application.yml

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myapp?useSSL=false&serverTimezone=UTC
    username: root
    password: root123
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update     # create | create-drop | update | validate | none
                           # Prod: validate hoặc none (dùng migration tool)
    show-sql: true         # In SQL ra console (dev only)
    properties:
      hibernate:
        format_sql: true   # Format đẹp SQL
        dialect: org.hibernate.dialect.MySQLDialect
    open-in-view: false    # Tắt để tránh lazy loading anti-pattern
```

**ddl-auto values:**
| Giá trị | Ý nghĩa | Dùng khi |
|---|---|---|
| `create` | Xóa & tạo lại khi khởi động | Test |
| `create-drop` | Tạo khi start, xóa khi stop | Test |
| `update` | Cập nhật schema | Dev |
| `validate` | Chỉ kiểm tra, không thay đổi | Prod |
| `none` | Không làm gì | Prod (dùng migration) |

---

## 11. Connection Pooling

### 11.1 Tại sao cần Connection Pool?

Tạo connection tốn ~200ms. Nếu 1000 request/giây, mỗi request tạo connection mới → **hệ thống chết**.

```
Request 1  ──→  Pool  ──→  Connection 1  ──→  DB
Request 2  ──→  Pool  ──→  Connection 2  ──→  DB
Request 3  ──→  Pool  ──→  Connection 3  ──→  DB
                ↑
         Tái sử dụng connections!
```

### 11.2 HikariCP (Spring Boot mặc định)

```yaml
spring:
  datasource:
    hikari:
      pool-name: MyAppPool
      minimum-idle: 5           # Số connection tối thiểu
      maximum-pool-size: 20     # Số connection tối đa
      connection-timeout: 30000  # Chờ tối đa 30s để lấy connection
      idle-timeout: 600000       # Đóng connection idle sau 10 phút
      max-lifetime: 1800000      # Thay connection mới sau 30 phút
      connection-test-query: SELECT 1  # Kiểm tra connection còn sống
```

**Công thức tính pool size (PostgreSQL):**
```
pool_size = (core_count * 2) + effective_spindle_count

Ví dụ: 4 core, SSD (spindle=1) → pool = (4*2)+1 = 9 ≈ 10
```

---

## 12. NoSQL

### 12.1 MongoDB với Spring Boot

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

```java
@Document(collection = "products") // Tương đương @Entity
public class Product {
    @Id
    private String id; // MongoDB dùng String ObjectId

    private String name;
    private Double price;
    private List<String> tags;
    private Map<String, Object> attributes; // Linh hoạt!

    @DBRef
    private Category category;
}

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByTagsContaining(String tag);
    List<Product> findByPriceLessThan(double maxPrice);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Product> findByNameRegex(String pattern);
}
```

```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/myapp
```

### 12.2 Redis với Spring Boot

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

```java
@Service
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Lưu cache
    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    // Lấy cache
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa cache
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

// Hoặc dùng @Cacheable annotation (Spring Cache)
@Service
public class UserService {

    @Cacheable(value = "users", key = "#id") // Cache kết quả
    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(...);
    }

    @CacheEvict(value = "users", key = "#id") // Xóa cache khi update
    public User updateUser(Long id, ...) { ... }

    @CachePut(value = "users", key = "#result.id") // Cập nhật cache
    public User createUser(...) { ... }
}
```

```yaml
spring:
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
      password: redis123  # nếu có password
```

---

## 13. Database Migration

### 13.1 Tại sao cần Migration?

- Không dùng `ddl-auto: update` trong production (nguy hiểm)
- Theo dõi lịch sử thay đổi schema
- Rollback khi có vấn đề
- Đồng bộ schema giữa dev, staging, prod

### 13.2 Flyway

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

```
src/main/resources/db/migration/
    V1__create_users_table.sql
    V2__create_orders_table.sql
    V3__add_phone_to_users.sql
    V4__create_products_table.sql
```

```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id         BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    is_active  BOOLEAN      DEFAULT TRUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- V3__add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
CREATE INDEX idx_users_phone ON users(phone);
```

```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true  # Cho DB đã có sẵn
```

**Quy tắc đặt tên:** `V{version}__{description}.sql`
- Phiên bản: V1, V2, V1.1, V2024.01.15
- Mô tả: dùng `__` (double underscore) ngăn cách, `_` thay space

---

## 14. Caching

### 14.1 Caching Strategies

```
Cache-Aside (Lazy Loading):
  Read:  App → Cache miss → DB → Update Cache → Return
  Write: App → Update DB → Invalidate Cache

Write-Through:
  Write: App → Update Cache → Update DB (đồng bộ)
  
Write-Behind (Write-Back):
  Write: App → Update Cache → Return (DB update async sau)

Read-Through:
  Read:  App → Cache miss → Cache tự load từ DB → Return
```

**Cache-Aside là phổ biến nhất:**

```java
@Service
public class ProductService {

    @Autowired private ProductRepository productRepo;
    @Autowired private RedisTemplate<String, Product> redisTemplate;

    private static final String CACHE_KEY = "product:";
    private static final long TTL = 3600; // 1 giờ

    public Product getProduct(Long id) {
        String key = CACHE_KEY + id;

        // 1. Check cache
        Product cached = (Product) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached; // Cache hit
        }

        // 2. Cache miss → query DB
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));

        // 3. Store in cache
        redisTemplate.opsForValue().set(key, product, Duration.ofSeconds(TTL));

        return product;
    }

    public Product updateProduct(Long id, Product updated) {
        Product product = productRepo.save(updated);
        // Invalidate cache
        redisTemplate.delete(CACHE_KEY + id);
        return product;
    }
}
```

### 14.2 Cache Problems

```
Cache Stampede (Thundering Herd):
  Cache expire → nhiều request cùng lúc hit DB → DB quá tải
  Giải pháp: Mutex lock, probabilistic early expiration

Cache Penetration:
  Query cho dữ liệu không tồn tại → mỗi lần hit DB
  Giải pháp: Cache null values với TTL ngắn, Bloom Filter

Cache Avalanche:
  Nhiều cache expire cùng lúc → DB quá tải
  Giải pháp: Random TTL (3600 + random(0,600))
```

---

## 15. Bảo Mật

### 15.1 SQL Injection

```java
// ❌ NGUY HIỂM — Hacker nhập: ' OR '1'='1
String sql = "SELECT * FROM users WHERE username = '" + input + "'";
// → "SELECT * FROM users WHERE username = '' OR '1'='1'"

// ✅ LUÔN dùng PreparedStatement
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE username = ?"
);
ps.setString(1, input); // Tự escape, an toàn
```

### 15.2 Không lưu plain text password

```java
// ❌ TUYỆT ĐỐI không lưu mật khẩu thô
user.setPassword("mypassword123");

// ✅ Dùng BCrypt
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor = 12
}

// Encode khi lưu
user.setPassword(passwordEncoder.encode("mypassword123"));

// Verify khi login
boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
```

### 15.3 Principle of Least Privilege

```sql
-- Tạo user DB riêng cho app, không dùng root!
CREATE USER 'app_user'@'%' IDENTIFIED BY 'strong_password_here';

-- Chỉ cấp quyền cần thiết
GRANT SELECT, INSERT, UPDATE, DELETE ON myapp.* TO 'app_user'@'%';

-- Không cấp DROP, CREATE, ALTER cho app user
FLUSH PRIVILEGES;
```

### 15.4 Sensitive Data

```java
// Không log thông tin nhạy cảm
log.info("User login: {}", user.getUsername()); // ✅ OK
log.info("User login: {} password: {}", user.getUsername(), user.getPassword()); // ❌

// Mã hóa dữ liệu nhạy cảm (số CCCD, số thẻ tín dụng...)
@Convert(converter = EncryptedStringConverter.class)
@Column(name = "national_id")
private String nationalId;
```

### 15.5 Connection Security

```yaml
# Dùng SSL/TLS cho production
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/myapp?useSSL=true&requireSSL=true

# Secrets trong environment variables, không hardcode!
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

---

## 16. Best Practices & Checklist

### ✅ Database Design
- [ ] Đặt tên bảng/cột rõ ràng, theo một convention (snake_case)
- [ ] Luôn có `id` là primary key (BIGINT AUTO_INCREMENT hoặc UUID)
- [ ] Có `created_at` và `updated_at` cho mọi bảng quan trọng
- [ ] Dùng Foreign Key để đảm bảo data integrity
- [ ] Normalize đến 3NF, chỉ denormalize khi cần performance

### ✅ SQL & Query
- [ ] LUÔN dùng PreparedStatement, không bao giờ concatenate SQL
- [ ] Thêm WHERE vào UPDATE/DELETE (kiểm tra kỹ trước khi chạy)
- [ ] Tránh SELECT *, chỉ lấy cột cần thiết
- [ ] Index những cột thường dùng trong WHERE, JOIN, ORDER BY
- [ ] Kiểm tra EXPLAIN trước khi deploy query mới

### ✅ Java / Spring
- [ ] Dùng JPA/Hibernate thay JDBC thuần (trừ khi cần native query)
- [ ] `@Transactional(readOnly = true)` cho read operations
- [ ] Tránh N+1 problem, dùng JOIN FETCH khi cần
- [ ] Dùng Connection Pool (HikariCP)
- [ ] Không để logic database trong Controller, tách vào Service/Repository
- [ ] Dùng DTO, không expose Entity trực tiếp qua API

### ✅ Production
- [ ] Không dùng `ddl-auto: update`, dùng Flyway/Liquibase
- [ ] DB user riêng cho app, không dùng root
- [ ] SSL/TLS cho connection
- [ ] Backup tự động
- [ ] Monitoring (slow query log, connection pool metrics)
- [ ] Secrets qua env variables, không hardcode

### ✅ Performance
- [ ] Cache dữ liệu ít thay đổi bằng Redis
- [ ] Phân trang cho danh sách dài
- [ ] Batch insert thay vì insert từng dòng
- [ ] Async processing cho tác vụ nặng

---

## 🗺️ Lộ Trình Học Đề Xuất

```
Tuần 1-2:  SQL cơ bản (SELECT, JOIN, GROUP BY)
           → Thực hành: Tạo DB cho app blog/todo list

Tuần 3-4:  SQL nâng cao (Subquery, Window Function, Index)
           + Transaction & ACID
           → Thực hành: Viết queries phức tạp hơn

Tuần 5-6:  JDBC + JPA/Hibernate
           → Thực hành: Java app kết nối MySQL

Tuần 7-8:  Spring Data JPA (Repository, Pagination, Custom Query)
           → Thực hành: REST API CRUD với Spring Boot

Tuần 9:    Connection Pool, Migration (Flyway)
           → Thực hành: Setup project chuẩn production

Tuần 10:   Redis caching, NoSQL cơ bản
           → Thực hành: Thêm cache vào REST API

Ongoing:   Đọc EXPLAIN, tối ưu queries, học PostgreSQL
```

---

## 📖 Tài Nguyên Học Thêm

- **SQLZoo** / **LeetCode Database** — Luyện SQL
- **Hibernate docs** — https://hibernate.org/orm/documentation
- **Spring Data JPA** — https://docs.spring.io/spring-data/jpa
- **Use The Index, Luke** — https://use-the-index-luke.com (tối ưu query)
- **High Performance MySQL** — Sách kinh điển về MySQL

---

*Happy coding! 🚀 Nhớ rằng: Học database là học cả đời. Cứ thực hành nhiều vào!*
