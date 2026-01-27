# 📚 Hướng Dẫn Học Redis và Kafka Từ Cơ Bản Đến Nâng Cao

> Tài liệu này được thiết kế cho người mới bắt đầu, giải thích dễ hiểu với ví dụ thực tế.

---

## 📖 Mục Lục

1. [Phần 1: Redis](#phần-1-redis)
   - [1.1. Redis là gì?](#11-redis-là-gì)
   - [1.2. Tại sao cần Redis?](#12-tại-sao-cần-redis)
   - [1.3. Kiến trúc Redis](#13-kiến-trúc-redis)
   - [1.4. Các kiểu dữ liệu trong Redis](#14-các-kiểu-dữ-liệu-trong-redis)
   - [1.5. Các use case thực tế](#15-các-use-case-thực-tế)
   - [1.6. Redis nâng cao](#16-redis-nâng-cao)

2. [Phần 2: Kafka](#phần-2-kafka)
   - [2.1. Kafka là gì?](#21-kafka-là-gì)
   - [2.2. Tại sao cần Kafka?](#22-tại-sao-cần-kafka)
   - [2.3. Kiến trúc Kafka](#23-kiến-trúc-kafka)
   - [2.4. Các khái niệm cốt lõi](#24-các-khái-niệm-cốt-lõi)
   - [2.5. Các use case thực tế](#25-các-use-case-thực-tế)
   - [2.6. Kafka nâng cao](#26-kafka-nâng-cao)

3. [Phần 3: Ứng Dụng Thực Tế](#phần-3-ứng-dụng-thực-tế)
   - [3.1. Kiến trúc ứng dụng demo](#31-kiến-trúc-ứng-dụng-demo)
   - [3.2. So sánh hiệu suất](#32-so-sánh-hiệu-suất)

4. [Phần 4: Best Practices](#phần-4-best-practices)

---

# Phần 1: Redis

## 1.1. Redis là gì?

**Redis** (Remote Dictionary Server) là một **cơ sở dữ liệu lưu trữ trên bộ nhớ** (in-memory database) dạng **key-value**.

### 🎯 Ví dụ đơn giản để hiểu:

Tưởng tượng bạn có một cuốn sổ tay:
- **Cơ sở dữ liệu thông thường (MySQL, PostgreSQL)**: Như một thư viện lớn, bạn phải tìm kiếm sách trong kệ (đọc từ ổ cứng - chậm)
- **Redis**: Như giấy note dán trên bàn làm việc, bạn nhìn là thấy ngay (đọc từ RAM - cực nhanh)

### ⚡ Đặc điểm chính:

```
┌─────────────────────────────────────────┐
│  RAM (Redis)                            │
│  • Tốc độ: 100,000+ ops/giây           │
│  • Độ trễ: < 1ms                        │
│  • Dữ liệu: Tạm thời hoặc lâu dài       │
└─────────────────────────────────────────┘
          ↕️ (Chậm hơn 1000 lần)
┌─────────────────────────────────────────┐
│  Ổ cứng (MySQL, PostgreSQL)             │
│  • Tốc độ: ~1,000 ops/giây              │
│  • Độ trễ: 10-100ms                     │
│  • Dữ liệu: Lâu dài, an toàn            │
└─────────────────────────────────────────┘
```

---

## 1.2. Tại sao cần Redis?

### 🔴 Vấn đề KHÔNG có Redis:

Tưởng tượng bạn có một website bán hàng:

```python
# Mỗi lần user xem sản phẩm, phải query database
def get_product(product_id):
    # Truy vấn database mất ~50ms
    product = db.query("SELECT * FROM products WHERE id = ?", product_id)
    return product

# 1000 users cùng lúc = 1000 queries = Database quá tải! 💥
```

**Hậu quả:**
- Website chậm
- Database bị quá tải
- Chi phí server tăng
- Trải nghiệm người dùng tệ

### 🟢 Giải pháp với Redis:

```python
# Lần đầu tiên, lưu vào Redis
def get_product(product_id):
    # Kiểm tra Redis trước (~0.1ms)
    product = redis.get(f"product:{product_id}")
    
    if product:
        return product  # Trả về ngay từ Redis
    
    # Nếu không có trong Redis, mới query database
    product = db.query("SELECT * FROM products WHERE id = ?", product_id)
    
    # Lưu vào Redis cho lần sau
    redis.set(f"product:{product_id}", product, expire=3600)  # Lưu 1 giờ
    
    return product

# 1000 users cùng lúc:
# - User đầu: Query database 1 lần
# - 999 users còn lại: Đọc từ Redis (cực nhanh)
# Database chỉ xử lý 1 query thay vì 1000! ✨
```

**Kết quả:**
- Tốc độ nhanh hơn 100-1000 lần
- Database giảm tải 99%
- Tiết kiệm chi phí
- Người dùng hài lòng

---

## 1.3. Kiến trúc Redis

### 🏗️ Cấu trúc cơ bản:

```
┌──────────────────────────────────────────────────┐
│              Application (Your Code)              │
└────────────────────┬─────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────┐
│           Redis Client (Library)                  │
│   • Python: redis-py                              │
│   • Node.js: ioredis                              │
│   • Java: Jedis                                   │
└────────────────────┬─────────────────────────────┘
                     │
                     ↓ (TCP Connection)
┌──────────────────────────────────────────────────┐
│              Redis Server                         │
│                                                   │
│  ┌────────────────────────────────────────┐      │
│  │         RAM (Main Memory)              │      │
│  │                                         │      │
│  │  Key: "user:1001"                      │      │
│  │  Value: {"name": "John", "age": 25}    │      │
│  │                                         │      │
│  │  Key: "session:abc123"                 │      │
│  │  Value: "user_data_here"               │      │
│  └────────────────────────────────────────┘      │
│                     │                             │
│                     ↓ (Optional: Persistence)     │
│  ┌────────────────────────────────────────┐      │
│  │         Disk (Backup)                  │      │
│  │  • RDB: Snapshot định kỳ               │      │
│  │  • AOF: Log mọi thao tác                │      │
│  └────────────────────────────────────────┘      │
└──────────────────────────────────────────────────┘
```

---

## 1.4. Các kiểu dữ liệu trong Redis

Redis không chỉ lưu trữ string đơn giản, mà hỗ trợ nhiều kiểu dữ liệu phức tạp:

### 1️⃣ **String** - Kiểu cơ bản nhất

```python
# Lưu và lấy giá trị đơn giản
redis.set("name", "John")
name = redis.get("name")  # "John"

# Tăng/giảm số
redis.set("views", 0)
redis.incr("views")  # views = 1
redis.incr("views")  # views = 2
redis.decr("views")  # views = 1
```

**Use case:** Đếm lượt view, cache dữ liệu đơn giản, session storage

---

### 2️⃣ **Hash** - Lưu object/dictionary

```python
# Lưu thông tin user như một object
redis.hset("user:1001", mapping={
    "name": "John",
    "age": "25",
    "email": "john@example.com"
})

# Lấy một field
name = redis.hget("user:1001", "name")  # "John"

# Lấy tất cả
user = redis.hgetall("user:1001")
# {"name": "John", "age": "25", "email": "john@example.com"}

# Chỉ tăng tuổi
redis.hincrby("user:1001", "age", 1)  # age = 26
```

**Use case:** Lưu thông tin user, product details, cache object phức tạp

---

### 3️⃣ **List** - Danh sách có thứ tự

```python
# Thêm vào cuối list (như queue)
redis.rpush("notifications", "Message 1")
redis.rpush("notifications", "Message 2")
redis.rpush("notifications", "Message 3")

# Lấy từ đầu list
message = redis.lpop("notifications")  # "Message 1"

# Xem toàn bộ list (không xóa)
all_messages = redis.lrange("notifications", 0, -1)
# ["Message 2", "Message 3"]

# Giữ chỉ 100 items mới nhất
redis.ltrim("recent_activities", 0, 99)
```

**Use case:** Queue (hàng đợi), activity logs, recent items, timeline

---

### 4️⃣ **Set** - Tập hợp không trùng lặp

```python
# Thêm vào set
redis.sadd("online_users", "user1")
redis.sadd("online_users", "user2")
redis.sadd("online_users", "user1")  # Không thêm vì đã có

# Kiểm tra member
is_online = redis.sismember("online_users", "user1")  # True

# Lấy tất cả members
users = redis.smembers("online_users")  # {"user1", "user2"}

# Số lượng members
count = redis.scard("online_users")  # 2

# Thao tác giữa các sets
redis.sadd("users_group_a", "user1", "user2", "user3")
redis.sadd("users_group_b", "user2", "user3", "user4")

# Users ở cả 2 groups
common = redis.sinter("users_group_a", "users_group_b")  # {"user2", "user3"}

# Users ở ít nhất 1 group
all_users = redis.sunion("users_group_a", "users_group_b")  # {"user1", "user2", "user3", "user4"}
```

**Use case:** Tags, online users, unique visitors, permissions, recommendations

---

### 5️⃣ **Sorted Set** - Tập hợp có điểm số

```python
# Thêm với điểm số (score)
redis.zadd("leaderboard", {"player1": 100, "player2": 200, "player3": 150})

# Top 3 (điểm cao nhất)
top3 = redis.zrevrange("leaderboard", 0, 2, withscores=True)
# [("player2", 200), ("player3", 150), ("player1", 100)]

# Thứ hạng của player
rank = redis.zrevrank("leaderboard", "player1")  # 2 (đứng thứ 3, index từ 0)

# Tăng điểm
redis.zincrby("leaderboard", 50, "player1")  # player1 = 150

# Lấy players có điểm từ 100-200
players = redis.zrangebyscore("leaderboard", 100, 200)
```

**Use case:** Leaderboard, ranking, priority queue, trending posts

---

### 6️⃣ **Pub/Sub** - Gửi/nhận message real-time

```python
# Publisher (người gửi)
redis.publish("news_channel", "Breaking news!")

# Subscriber (người nhận)
pubsub = redis.pubsub()
pubsub.subscribe("news_channel")

for message in pubsub.listen():
    if message["type"] == "message":
        print(f"Received: {message['data']}")
```

**Use case:** Chat app, notifications, live updates, broadcasting

---

## 1.5. Các use case thực tế

### 📌 Use Case 1: **Caching** (Cache dữ liệu)

**Vấn đề:** Query database chậm, tốn tài nguyên

```python
# ❌ KHÔNG tốt - Query database mỗi lần
def get_product_info(product_id):
    product = db.query("SELECT * FROM products WHERE id = ?", product_id)
    return product

# ✅ TỐT - Cache bằng Redis
def get_product_info(product_id):
    cache_key = f"product:{product_id}"
    
    # Kiểm tra cache
    cached = redis.get(cache_key)
    if cached:
        return json.loads(cached)
    
    # Không có cache, query database
    product = db.query("SELECT * FROM products WHERE id = ?", product_id)
    
    # Lưu vào cache (expire sau 1 giờ)
    redis.setex(cache_key, 3600, json.dumps(product))
    
    return product
```

**Lợi ích:**
- Tốc độ: Nhanh hơn 100-1000 lần
- Giảm tải database: 99% requests đọc từ Redis
- Scalability: Xử lý được hàng triệu requests

---

### 📌 Use Case 2: **Session Management**

**Vấn đề:** Lưu session trong memory server → Không scale được

```python
# ✅ Lưu session trong Redis
from flask import Flask, session
from flask_session import Session

app = Flask(__name__)
app.config['SESSION_TYPE'] = 'redis'
app.config['SESSION_REDIS'] = redis.StrictRedis(host='localhost', port=6379)
Session(app)

@app.route('/login', methods=['POST'])
def login():
    user = authenticate(request.form['username'], request.form['password'])
    session['user_id'] = user.id
    session['username'] = user.username
    return "Logged in"

@app.route('/dashboard')
def dashboard():
    if 'user_id' in session:
        return f"Welcome {session['username']}"
    return "Please login"
```

**Lợi ích:**
- Session được share giữa nhiều server
- Dễ dàng scale horizontal
- Session không mất khi restart server

---

### 📌 Use Case 3: **Rate Limiting** (Giới hạn request)

**Vấn đề:** Ngăn chặn spam, DDoS attack

```python
# Giới hạn 10 requests/phút cho mỗi IP
def is_rate_limited(ip_address):
    key = f"rate_limit:{ip_address}"
    
    # Tăng counter
    current = redis.incr(key)
    
    # Set expire lần đầu tiên
    if current == 1:
        redis.expire(key, 60)  # 60 giây
    
    # Kiểm tra giới hạn
    if current > 10:
        return True  # Vượt giới hạn
    
    return False  # OK

@app.route('/api/data')
def get_data():
    ip = request.remote_addr
    
    if is_rate_limited(ip):
        return {"error": "Too many requests"}, 429
    
    return {"data": "your data"}
```

---

### 📌 Use Case 4: **Leaderboard** (Bảng xếp hạng)

**Vấn đề:** Cần update và query ranking nhanh chóng

```python
# Cập nhật điểm
def update_score(player_id, score):
    redis.zadd("game_leaderboard", {player_id: score})

# Lấy top 10
def get_top_10():
    return redis.zrevrange("game_leaderboard", 0, 9, withscores=True)

# Lấy rank của player
def get_player_rank(player_id):
    rank = redis.zrevrank("game_leaderboard", player_id)
    return rank + 1 if rank is not None else None

# Lấy players xung quanh
def get_nearby_players(player_id, range=5):
    rank = redis.zrevrank("game_leaderboard", player_id)
    if rank is None:
        return []
    
    start = max(0, rank - range)
    end = rank + range
    
    return redis.zrevrange("game_leaderboard", start, end, withscores=True)
```

---

### 📌 Use Case 5: **Real-time Analytics**

```python
# Đếm page views theo thời gian thực
def track_page_view(page_url):
    today = datetime.now().strftime("%Y-%m-%d")
    
    # Tăng counter cho ngày hôm nay
    redis.hincrby(f"pageviews:{today}", page_url, 1)
    
    # Tăng tổng
    redis.incr(f"total_pageviews:{page_url}")

# Lấy thống kê
def get_today_stats():
    today = datetime.now().strftime("%Y-%m-%d")
    return redis.hgetall(f"pageviews:{today}")

# Lấy top 10 pages
def get_trending_pages():
    today = datetime.now().strftime("%Y-%m-%d")
    pages = redis.hgetall(f"pageviews:{today}")
    
    # Sort by views
    sorted_pages = sorted(pages.items(), key=lambda x: int(x[1]), reverse=True)
    return sorted_pages[:10]
```

---

## 1.6. Redis nâng cao

### 🔄 **Persistence** (Lưu trữ lâu dài)

Redis lưu dữ liệu trong RAM, nhưng có thể backup xuống disk:

#### **1. RDB (Redis Database Backup)**
Snapshot định kỳ:

```bash
# redis.conf
save 900 1      # Sau 900 giây (15 phút) nếu có ít nhất 1 thay đổi
save 300 10     # Sau 300 giây (5 phút) nếu có ít nhất 10 thay đổi
save 60 10000   # Sau 60 giây nếu có ít nhất 10000 thay đổi
```

**Ưu điểm:**
- File backup nhỏ gọn
- Restore nhanh
- Performance tốt

**Nhược điểm:**
- Có thể mất data giữa 2 snapshot
- Snapshot lớn tốn thời gian

---

#### **2. AOF (Append Only File)**
Log mọi thao tác write:

```bash
# redis.conf
appendonly yes
appendfsync everysec  # Sync mỗi giây (cân bằng performance và safety)
```

**Ưu điểm:**
- Mất ít data hơn (chỉ mất tối đa 1 giây)
- Có thể replay lại operations

**Nhược điểm:**
- File lớn hơn RDB
- Restore chậm hơn

---

### 🔐 **Transactions**

Đảm bảo nhiều operations được thực thi atomic (tất cả hoặc không):

```python
# Chuyển tiền giữa 2 tài khoản
pipe = redis.pipeline()

try:
    # Bắt đầu transaction
    pipe.watch("balance:user1", "balance:user2")
    
    balance1 = int(redis.get("balance:user1"))
    balance2 = int(redis.get("balance:user2"))
    
    if balance1 >= 100:  # Kiểm tra đủ tiền
        pipe.multi()
        pipe.decrby("balance:user1", 100)
        pipe.incrby("balance:user2", 100)
        pipe.execute()
        print("Transaction successful")
    else:
        print("Insufficient balance")
        
except redis.WatchError:
    print("Transaction failed - data changed by another client")
```

---

### 📊 **Redis Cluster** (Phân tán dữ liệu)

Khi dữ liệu quá lớn cho 1 server:

```
┌─────────────────────────────────────────────────────┐
│                  Redis Cluster                       │
│                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Master 1 │  │ Master 2 │  │ Master 3 │          │
│  │ Slots    │  │ Slots    │  │ Slots    │          │
│  │ 0-5460   │  │ 5461-    │  │ 10923-   │          │
│  │          │  │ 10922    │  │ 16383    │          │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘          │
│       │             │             │                 │
│       ↓             ↓             ↓                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Replica 1│  │ Replica 2│  │ Replica 3│          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
```

**Lợi ích:**
- Horizontal scaling
- High availability
- Automatic failover

---

### 🔥 **Lua Scripts** (Atomic operations phức tạp)

Thực thi logic phức tạp atomic trên Redis server:

```python
# Script Lua để implement rate limiting chính xác
lua_script = """
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])

local current = redis.call('INCR', key)

if current == 1 then
    redis.call('EXPIRE', key, window)
end

if current > limit then
    return 0
else
    return 1
end
"""

# Register script
rate_limit = redis.register_script(lua_script)

# Sử dụng
def check_rate_limit(user_id):
    result = rate_limit(keys=[f"rate:{user_id}"], args=[10, 60])
    return result == 1  # True nếu OK, False nếu vượt giới hạn
```

---

# Phần 2: Kafka

## 2.1. Kafka là gì?

**Apache Kafka** là một **hệ thống message streaming phân tán** (distributed streaming platform), được thiết kế để xử lý và truyền tải **hàng triệu message mỗi giây**.

### 🎯 Ví dụ đơn giản để hiểu:

Tưởng tượng Kafka như một **hệ thống đường ống nước**:

```
[Nguồn nước 1] ──┐
[Nguồn nước 2] ──┤
[Nguồn nước 3] ──┤── [Đường ống chính (Kafka)] ──┬─→ [Nhà 1]
                 │                                ├─→ [Nhà 2]
                 │                                └─→ [Nhà 3]
```

- **Nguồn nước** = Producers (ứng dụng gửi data)
- **Đường ống** = Kafka Topics (kênh truyền tải)
- **Nhà** = Consumers (ứng dụng nhận data)

Đặc biệt:
- Nước (data) được lưu trong đường ống một thời gian
- Nhiều nhà có thể uống cùng 1 nguồn nước
- Thêm nhà mới không ảnh hưởng nguồn nước

---

## 2.2. Tại sao cần Kafka?

### 🔴 Vấn đề KHÔNG có Kafka:

Tưởng tượng bạn xây dựng hệ thống e-commerce:

```python
# Khi user đặt hàng, phải làm NHIỀU việc:
def place_order(order_data):
    # 1. Lưu vào database
    db.save_order(order_data)  # 50ms
    
    # 2. Gửi email xác nhận
    send_email(order_data['email'])  # 200ms
    
    # 3. Gửi SMS
    send_sms(order_data['phone'])  # 300ms
    
    # 4. Update inventory
    update_inventory(order_data['items'])  # 100ms
    
    # 5. Tính điểm thưởng
    calculate_points(order_data['user_id'])  # 50ms
    
    # 6. Gửi notification
    send_notification(order_data['user_id'])  # 100ms
    
    # TỔNG: 800ms - User phải chờ!
    return "Order placed"
```

**Vấn đề:**
1. **Chậm**: User chờ 800ms
2. **Coupling**: Tất cả services phụ thuộc nhau
3. **Failure**: 1 service lỗi → Toàn bộ lỗi
4. **Không scale**: Khó mở rộng

```
┌──────────┐
│  Order   │
│ Service  │──→ Email Service (lỗi) ❌
└──────────┘    ↓
                SMS Service không chạy được
                Notification không chạy được
                → Đơn hàng thất bại hoàn toàn!
```

---

### 🟢 Giải pháp với Kafka:

```python
# Producer: Chỉ gửi message vào Kafka
def place_order(order_data):
    # 1. Lưu vào database
    db.save_order(order_data)  # 50ms
    
    # 2. Gửi event vào Kafka
    kafka.send('orders', order_data)  # 5ms
    
    # TỔNG: 55ms - User hài lòng!
    return "Order placed"

# Consumers: Xử lý độc lập và song song
# Consumer 1: Email Service
kafka.consume('orders', lambda order: send_email(order['email']))

# Consumer 2: SMS Service
kafka.consume('orders', lambda order: send_sms(order['phone']))

# Consumer 3: Inventory Service
kafka.consume('orders', lambda order: update_inventory(order['items']))

# Consumer 4: Loyalty Service
kafka.consume('orders', lambda order: calculate_points(order['user_id']))

# Consumer 5: Notification Service
kafka.consume('orders', lambda order: send_notification(order['user_id']))
```

**Kiến trúc:**

```
                    ┌─────────────────┐
                    │  Order Service  │
                    └────────┬────────┘
                             │ (55ms)
                             ↓
                    ┌─────────────────┐
                    │  Kafka Topic    │
                    │    "orders"     │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┬───────────────┐
            ↓                ↓                ↓               ↓
    ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
    │Email Service │ │ SMS Service  │ │Inventory Svc │ │Loyalty Svc   │
    └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
    (độc lập)        (độc lập)        (độc lập)        (độc lập)
```

**Lợi ích:**
1. **Nhanh**: User chỉ chờ 55ms (nhanh hơn 14 lần)
2. **Decoupling**: Services độc lập, không phụ thuộc nhau
3. **Fault tolerance**: 1 service lỗi không ảnh hưởng khác
4. **Scalability**: Dễ dàng thêm consumers mới
5. **Replay**: Có thể xử lý lại events cũ

---

## 2.3. Kiến trúc Kafka

### 🏗️ Các thành phần chính:

```
┌─────────────────────────────────────────────────────────────┐
│                      Kafka Cluster                          │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Broker 1    │  │  Broker 2    │  │  Broker 3    │      │
│  │              │  │              │  │              │      │
│  │ Topic: logs  │  │ Topic: logs  │  │ Topic: logs  │      │
│  │ Partition 0  │  │ Partition 1  │  │ Partition 2  │      │
│  │ [msg][msg]   │  │ [msg][msg]   │  │ [msg][msg]   │      │
│  │              │  │              │  │              │      │
│  │ Topic: users │  │ Topic: users │  │ Topic: users │      │
│  │ Partition 0  │  │ Partition 1  │  │ Partition 2  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                              │
└─────────────────────────────────────────────────────────────┘
         ↑                                      ↓
         │                                      │
    ┌────────────┐                         ┌────────────┐
    │ Producer   │                         │ Consumer   │
    │ (gửi data) │                         │ (nhận data)│
    └────────────┘                         └────────────┘
```

---

## 2.4. Các khái niệm cốt lõi

### 1️⃣ **Topic** (Chủ đề)

Một kênh để gửi/nhận messages, giống như một folder.

```python
# Ví dụ topics trong hệ thống
topics = [
    "user.registered",      # User mới đăng ký
    "order.created",        # Đơn hàng mới
    "payment.completed",    # Thanh toán thành công
    "email.sent",          # Email đã gửi
    "analytics.pageview"   # Lượt xem trang
]
```

---

### 2️⃣ **Partition** (Phân vùng)

Mỗi topic được chia thành nhiều partitions để:
- **Parallel processing**: Xử lý song song
- **Scalability**: Mở rộng dễ dàng
- **Ordering**: Đảm bảo thứ tự trong partition

```
Topic: "orders"
├── Partition 0: [msg1] [msg2] [msg3] [msg4] → Consumer A
├── Partition 1: [msg5] [msg6] [msg7] [msg8] → Consumer B
└── Partition 2: [msg9] [msg10][msg11][msg12] → Consumer C
```

**Ordering guarantee:**
- Messages trong **cùng partition**: Đảm bảo thứ tự
- Messages ở **khác partition**: Không đảm bảo thứ tự

```python
# Producer gửi với key để đảm bảo cùng user vào cùng partition
producer.send('orders', 
              key=str(user_id).encode(),  # Key
              value=order_data)            # Value

# Cùng user_id → Cùng partition → Đảm bảo thứ tự
```

---

### 3️⃣ **Producer** (Người gửi)

Ứng dụng gửi messages vào Kafka.

```python
from kafka import KafkaProducer
import json

producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# Gửi message
order = {
    "order_id": "12345",
    "user_id": "user_001",
    "amount": 99.99,
    "items": ["item1", "item2"]
}

future = producer.send('orders', value=order)
result = future.get(timeout=10)  # Chờ confirm

print(f"Sent to partition {result.partition} at offset {result.offset}")
```

**Producer config quan trọng:**

```python
producer = KafkaProducer(
    # Độ tin cậy
    acks='all',  # 0: không chờ, 1: chờ leader, all: chờ tất cả replicas
    
    # Retry khi lỗi
    retries=3,
    
    # Batch để tăng performance
    batch_size=16384,
    linger_ms=10,  # Chờ 10ms để gom batch
    
    # Compression để giảm bandwidth
    compression_type='gzip'
)
```

---

### 4️⃣ **Consumer** (Người nhận)

Ứng dụng đọc messages từ Kafka.

```python
from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'orders',  # Topic name
    bootstrap_servers=['localhost:9092'],
    value_deserializer=lambda m: json.loads(m.decode('utf-8')),
    group_id='order-processors',  # Consumer group
    auto_offset_reset='earliest'  # Đọc từ đầu nếu chưa có offset
)

# Đọc messages
for message in consumer:
    order = message.value
    print(f"Processing order {order['order_id']}")
    
    # Xử lý order
    process_order(order)
    
    # Kafka tự động commit offset
```

---

### 5️⃣ **Consumer Group** (Nhóm consumers)

Nhiều consumers cùng nhóm để xử lý song song:

```
Topic "orders" (3 partitions)
├── Partition 0 → Consumer A (group: processors)
├── Partition 1 → Consumer B (group: processors)
└── Partition 2 → Consumer C (group: processors)

→ Mỗi partition được 1 consumer xử lý
→ Load balancing tự động
→ Fault tolerance: Consumer chết → partition reassign
```

**Nhiều consumer groups:**

```
Topic "orders"
├── Consumer Group "processors" (xử lý đơn hàng)
│   ├── Consumer 1
│   └── Consumer 2
│
└── Consumer Group "analytics" (phân tích dữ liệu)
    ├── Consumer 1
    └── Consumer 2

→ Mỗi group đọc độc lập toàn bộ messages
```

---

### 6️⃣ **Offset** (Vị trí)

Vị trí của message trong partition:

```
Partition 0:
┌────┬────┬────┬────┬────┬────┬────┐
│ 0  │ 1  │ 2  │ 3  │ 4  │ 5  │ 6  │
└────┴────┴────┴────┴────┴────┴────┘
                     ↑
              Current offset = 3
              (Consumer đã đọc đến message 3)
```

**Offset management:**

```python
# Auto commit (mặc định)
consumer = KafkaConsumer(
    'orders',
    enable_auto_commit=True,
    auto_commit_interval_ms=5000  # Commit mỗi 5 giây
)

# Manual commit (an toàn hơn)
consumer = KafkaConsumer(
    'orders',
    enable_auto_commit=False
)

for message in consumer:
    try:
        process_order(message.value)
        consumer.commit()  # Chỉ commit khi xử lý thành công
    except Exception as e:
        print(f"Error: {e}")
        # Không commit → Sẽ xử lý lại message này
```

---

### 7️⃣ **Replication** (Sao lưu)

Mỗi partition có nhiều replicas để đảm bảo availability:

```
Topic "orders" - Partition 0 (Replication factor = 3)

Broker 1: [Leader] ──┐
Broker 2: [Replica]  ├─ Sync
Broker 3: [Replica] ──┘

Producer → Ghi vào Leader
Consumer → Đọc từ Leader
Nếu Leader chết → 1 Replica trở thành Leader mới
```

---

### 8️⃣ **Retention** (Lưu trữ)

Kafka giữ messages trong một khoảng thời gian:

```bash
# Giữ 7 ngày
log.retention.hours=168

# Hoặc giữ tối đa 1GB
log.retention.bytes=1073741824

# Consumer có thể replay messages cũ
consumer = KafkaConsumer(
    'orders',
    auto_offset_reset='earliest'  # Đọc từ message cũ nhất còn lưu
)
```

---

## 2.5. Các use case thực tế

### 📌 Use Case 1: **Event-Driven Architecture**

Hệ thống e-commerce với nhiều services:

```python
# ===== Order Service =====
def create_order(order_data):
    # Lưu vào database
    order_id = db.save_order(order_data)
    
    # Publish event
    event = {
        "event_type": "ORDER_CREATED",
        "order_id": order_id,
        "user_id": order_data['user_id'],
        "amount": order_data['amount'],
        "items": order_data['items'],
        "timestamp": datetime.now().isoformat()
    }
    
    producer.send('order.events', value=event)
    return order_id

# ===== Email Service (Consumer) =====
def email_consumer():
    consumer = KafkaConsumer('order.events', group_id='email-service')
    
    for message in consumer:
        event = message.value
        
        if event['event_type'] == 'ORDER_CREATED':
            send_order_confirmation_email(
                user_id=event['user_id'],
                order_id=event['order_id']
            )

# ===== Inventory Service (Consumer) =====
def inventory_consumer():
    consumer = KafkaConsumer('order.events', group_id='inventory-service')
    
    for message in consumer:
        event = message.value
        
        if event['event_type'] == 'ORDER_CREATED':
            reserve_inventory(event['items'])

# ===== Analytics Service (Consumer) =====
def analytics_consumer():
    consumer = KafkaConsumer('order.events', group_id='analytics-service')
    
    for message in consumer:
        event = message.value
        
        if event['event_type'] == 'ORDER_CREATED':
            track_revenue(event['amount'])
            update_sales_report(event)
```

**Lợi ích:**
- Services độc lập hoàn toàn
- Dễ thêm service mới (chỉ cần thêm consumer)
- Fault tolerant
- Có thể replay events

---

### 📌 Use Case 2: **Log Aggregation**

Thu thập logs từ nhiều servers:

```python
# ===== Application Servers (Producers) =====
# Server 1, 2, 3, ... tất cả gửi logs vào Kafka

import logging
from kafka.logging.handlers import KafkaHandler

logger = logging.getLogger('my_app')
handler = KafkaHandler(
    hosts='localhost:9092',
    topic='application.logs'
)
logger.addHandler(handler)

# Logs tự động gửi vào Kafka
logger.info("User logged in", extra={"user_id": "123"})
logger.error("Payment failed", extra={"order_id": "456"})

# ===== Log Processor (Consumer) =====
def log_processor():
    consumer = KafkaConsumer('application.logs', group_id='log-processor')
    
    for message in consumer:
        log = message.value
        
        # Lưu vào Elasticsearch
        elasticsearch.index('logs', log)
        
        # Alert nếu error
        if log['level'] == 'ERROR':
            send_alert_to_slack(log['message'])
```

---

### 📌 Use Case 3: **Real-time Analytics & Metrics**

Theo dõi metrics real-time:

```python
# ===== Application (Producer) =====
def track_event(event_type, properties):
    event = {
        "event_type": event_type,
        "properties": properties,
        "timestamp": time.time()
    }
    producer.send('analytics.events', value=event)

# Ví dụ tracking
track_event("page_view", {"page": "/products", "user_id": "123"})
track_event("button_click", {"button": "buy_now", "product_id": "456"})

# ===== Real-time Dashboard (Consumer) =====
from collections import defaultdict
import time

def realtime_dashboard():
    consumer = KafkaConsumer('analytics.events', group_id='dashboard')
    
    # Metrics trong 1 phút
    metrics = defaultdict(int)
    window_start = time.time()
    
    for message in consumer:
        event = message.value
        
        # Reset window mỗi phút
        if time.time() - window_start > 60:
            print(f"Last minute metrics: {dict(metrics)}")
            metrics.clear()
            window_start = time.time()
        
        # Đếm events
        metrics[event['event_type']] += 1
        
        # Update dashboard real-time
        update_dashboard(metrics)
```

---

### 📌 Use Case 4: **Stream Processing**

Xử lý luồng dữ liệu real-time:

```python
# ===== Fraud Detection System =====
from kafka import KafkaConsumer, KafkaProducer
import json

def fraud_detector():
    consumer = KafkaConsumer('transactions', group_id='fraud-detector')
    alert_producer = KafkaProducer()
    
    # Theo dõi transactions của user trong 5 phút
    user_transactions = defaultdict(list)
    
    for message in consumer:
        transaction = message.value
        user_id = transaction['user_id']
        
        # Thêm vào history
        user_transactions[user_id].append(transaction)
        
        # Giữ chỉ 5 phút gần nhất
        cutoff_time = time.time() - 300
        user_transactions[user_id] = [
            t for t in user_transactions[user_id]
            if t['timestamp'] > cutoff_time
        ]
        
        # Kiểm tra fraud
        if is_suspicious(user_transactions[user_id]):
            alert = {
                "user_id": user_id,
                "reason": "Multiple transactions in short time",
                "transactions": user_transactions[user_id]
            }
            alert_producer.send('fraud.alerts', value=alert)
            
            # Block user tạm thời
            block_user(user_id)

def is_suspicious(transactions):
    # Quá 5 transactions trong 5 phút
    if len(transactions) > 5:
        return True
    
    # Tổng amount quá lớn
    total = sum(t['amount'] for t in transactions)
    if total > 10000:
        return True
    
    return False
```

---

### 📌 Use Case 5: **Microservices Communication**

Services giao tiếp qua Kafka:

```python
# ===== User Service =====
def register_user(user_data):
    # Lưu user
    user_id = db.create_user(user_data)
    
    # Publish event
    event = {
        "event_type": "USER_REGISTERED",
        "user_id": user_id,
        "email": user_data['email'],
        "name": user_data['name']
    }
    producer.send('user.events', value=event)
    
    return user_id

# ===== Email Service (lắng nghe USER_REGISTERED) =====
def email_service():
    consumer = KafkaConsumer('user.events', group_id='email-service')
    
    for message in consumer:
        event = message.value
        
        if event['event_type'] == 'USER_REGISTERED':
            send_welcome_email(event['email'], event['name'])

# ===== Recommendation Service (lắng nghe USER_REGISTERED) =====
def recommendation_service():
    consumer = KafkaConsumer('user.events', group_id='recommendation-service')
    
    for message in consumer:
        event = message.value
        
        if event['event_type'] == 'USER_REGISTERED':
            initialize_recommendations(event['user_id'])

# ===== Analytics Service (lắng nghe USER_REGISTERED) =====
def analytics_service():
    consumer = KafkaConsumer('user.events', group_id='analytics-service')
    
    for message in consumer:
        event = message.value
        
        if event['event_type'] == 'USER_REGISTERED':
            track_new_user(event)
            update_growth_metrics()
```

---

## 2.6. Kafka nâng cao

### 🎯 **Kafka Streams** (Stream processing framework)

Xử lý dữ liệu real-time trực tiếp trong Kafka:

```python
from kafka import KafkaConsumer, KafkaProducer
import json

# Ví dụ: Tính tổng doanh thu theo từng sản phẩm real-time
def product_revenue_aggregator():
    consumer = KafkaConsumer('orders', group_id='revenue-aggregator')
    producer = KafkaProducer()
    
    # State: Tổng revenue của mỗi product
    product_revenue = defaultdict(float)
    
    for message in consumer:
        order = message.value
        
        # Cập nhật revenue
        for item in order['items']:
            product_id = item['product_id']
            revenue = item['price'] * item['quantity']
            product_revenue[product_id] += revenue
        
        # Publish kết quả aggregate
        for product_id, total_revenue in product_revenue.items():
            result = {
                "product_id": product_id,
                "total_revenue": total_revenue,
                "timestamp": time.time()
            }
            producer.send('product.revenue', value=result)
```

---

### 🔄 **Exactly-Once Semantics**

Đảm bảo message được xử lý đúng 1 lần:

```python
from kafka import KafkaConsumer, KafkaProducer

# Producer với idempotence
producer = KafkaProducer(
    enable_idempotence=True,  # Tránh duplicate
    transactional_id='my-transactional-id'
)

# Consumer với transaction
consumer = KafkaConsumer(
    'orders',
    group_id='payment-processor',
    enable_auto_commit=False,
    isolation_level='read_committed'  # Chỉ đọc committed messages
)

# Xử lý transactional
producer.init_transactions()

for message in consumer:
    try:
        producer.begin_transaction()
        
        # Xử lý message
        order = message.value
        process_payment(order)
        
        # Gửi kết quả
        result = {"order_id": order['id'], "status": "paid"}
        producer.send('payment.results', value=result)
        
        # Commit offset và transaction cùng lúc
        producer.send_offsets_to_transaction(
            {TopicPartition('orders', message.partition): message.offset + 1},
            consumer.config['group_id']
        )
        
        producer.commit_transaction()
        
    except Exception as e:
        producer.abort_transaction()
        print(f"Transaction aborted: {e}")
```

---

### 📊 **Kafka Connect** (Tích hợp với hệ thống khác)

Kết nối Kafka với databases, S3, Elasticsearch, ...

```yaml
# Ví dụ: Sync data từ MySQL vào Kafka
# jdbc-source-connector.json
{
  "name": "mysql-source-connector",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "connection.url": "jdbc:mysql://localhost:3306/mydb",
    "connection.user": "user",
    "connection.password": "password",
    "table.whitelist": "orders,users",
    "mode": "incrementing",
    "incrementing.column.name": "id",
    "topic.prefix": "mysql-"
  }
}

# Kết quả:
# - Table "orders" → Topic "mysql-orders"
# - Table "users" → Topic "mysql-users"
# - Tự động sync khi có thay đổi
```

---

### ⚡ **Performance Tuning**

```python
# ===== Producer Optimization =====
producer = KafkaProducer(
    # Batching để giảm network calls
    batch_size=32768,  # 32KB
    linger_ms=20,      # Chờ 20ms để gom batch
    
    # Compression
    compression_type='snappy',  # hoặc 'gzip', 'lz4'
    
    # Buffer
    buffer_memory=67108864,  # 64MB
    
    # Acks
    acks='1',  # Chỉ chờ leader (nhanh hơn 'all')
    
    # Retry
    retries=3,
    retry_backoff_ms=100
)

# ===== Consumer Optimization =====
consumer = KafkaConsumer(
    'orders',
    # Fetch size
    fetch_min_bytes=1024,       # 1KB
    fetch_max_wait_ms=500,      # Chờ tối đa 500ms
    max_partition_fetch_bytes=1048576,  # 1MB
    
    # Batch processing
    max_poll_records=500,  # Lấy tối đa 500 messages/lần
    
    # Session timeout
    session_timeout_ms=30000,
    heartbeat_interval_ms=3000
)

# Xử lý batch thay vì từng message
messages = consumer.poll(timeout_ms=1000, max_records=500)
for topic_partition, records in messages.items():
    # Xử lý hàng loạt
    batch_process(records)
    consumer.commit()
```

---

# Phần 3: Ứng Dụng Thực Tế

## 3.1. Kiến trúc ứng dụng demo

Chúng ta sẽ xây dựng một **hệ thống xử lý đơn hàng** để so sánh:

### ❌ **Phiên bản 1: KHÔNG dùng Redis & Kafka**

```
User → API Server → Database
              ↓
        (Chậm, coupling, không scale)
```

### ✅ **Phiên bản 2: SỬ DỤNG Redis & Kafka**

```
                    ┌─→ Redis (Cache)
User → API Server ──┤
                    └─→ Kafka → [Email Service]
                              → [SMS Service]
                              → [Inventory Service]
                              → [Analytics Service]
```

---

## 3.2. So sánh hiệu suất

### 📊 **Metrics để đo**

1. **Response Time**: Thời gian user chờ
2. **Throughput**: Số requests xử lý/giây
3. **Database Load**: Số queries đến database
4. **Failure Rate**: Tỷ lệ lỗi
5. **Scalability**: Khả năng mở rộng

### 📈 **Kết quả dự kiến**

| Metric | Không Redis/Kafka | Có Redis/Kafka | Cải thiện |
|--------|-------------------|----------------|-----------|
| Response Time | 800ms | 55ms | **14.5x nhanh hơn** |
| Throughput | 100 req/s | 5000 req/s | **50x nhiều hơn** |
| DB Queries | 100% requests | 1% requests | **99% giảm** |
| Failure Rate | 5% | 0.1% | **50x ít hơn** |
| Max Users | 1,000 | 100,000 | **100x scale** |

---

# Phần 4: Best Practices

## 🎯 **Redis Best Practices**

### 1. **Naming Convention**

```python
# ✅ TỐT - Rõ ràng, có cấu trúc
"user:1001:profile"
"product:5432:details"
"session:abc123"
"cache:homepage:en"

# ❌ KHÔNG TỐT - Khó hiểu, không cấu trúc
"u1001"
"p5432"
"s1"
```

### 2. **Set Expiration**

```python
# ✅ LUÔN set expiration để tránh memory leak
redis.setex("session:abc123", 3600, session_data)  # Expire sau 1 giờ

# ❌ Không set expiration = Tốn memory vô hạn
redis.set("session:abc123", session_data)  # BAD!
```

### 3. **Use Pipeline cho nhiều commands**

```python
# ❌ CHẬM - 3 network round-trips
redis.set("key1", "value1")
redis.set("key2", "value2")
redis.set("key3", "value3")

# ✅ NHANH - 1 network round-trip
pipe = redis.pipeline()
pipe.set("key1", "value1")
pipe.set("key2", "value2")
pipe.set("key3", "value3")
pipe.execute()
```

### 4. **Monitor Memory**

```python
# Kiểm tra memory usage
info = redis.info('memory')
print(f"Used memory: {info['used_memory_human']}")
print(f"Max memory: {info['maxmemory_human']}")

# Set max memory policy
redis.config_set('maxmemory-policy', 'allkeys-lru')  # Xóa keys ít dùng nhất
```

### 5. **Avoid Large Keys**

```python
# ❌ KHÔNG TỐT - Key quá lớn
large_list = list(range(1000000))
redis.set("big_data", json.dumps(large_list))  # 10MB+

# ✅ TỐT - Chia nhỏ
for i in range(10):
    chunk = large_list[i*100000:(i+1)*100000]
    redis.set(f"data:chunk:{i}", json.dumps(chunk))
```

---

## 🎯 **Kafka Best Practices**

### 1. **Topic Naming Convention**

```python
# ✅ TỐT
"user.registered"
"order.created"
"payment.completed"
"analytics.pageview"

# ❌ KHÔNG TỐT
"users"
"orders"
"data"
```

### 2. **Choose Right Partition Key**

```python
# ✅ TỐT - Đảm bảo ordering cho cùng user
producer.send('orders',
              key=str(user_id).encode(),
              value=order_data)

# ❌ KHÔNG TỐT - Không có key = random partition = mất ordering
producer.send('orders', value=order_data)
```

### 3. **Handle Failures**

```python
# ✅ TỐT - Xử lý lỗi và retry
def process_message(message):
    max_retries = 3
    for attempt in range(max_retries):
        try:
            # Xử lý message
            result = do_something(message.value)
            
            # Commit offset khi thành công
            consumer.commit()
            return result
            
        except Exception as e:
            if attempt == max_retries - 1:
                # Gửi vào Dead Letter Queue
                producer.send('failed.messages', value={
                    "original_message": message.value,
                    "error": str(e),
                    "timestamp": time.time()
                })
            else:
                time.sleep(2 ** attempt)  # Exponential backoff
```

### 4. **Monitor Lag**

```python
# Consumer lag = Số messages chưa xử lý
# Nếu lag tăng → Consumer xử lý không kịp → Cần scale

# Monitoring
def check_consumer_lag():
    admin_client = KafkaAdminClient()
    consumer_groups = admin_client.describe_consumer_groups(['my-group'])
    
    for group in consumer_groups:
        for member in group.members:
            lag = member.lag
            if lag > 10000:
                alert(f"High lag: {lag} messages")
```

### 5. **Right Number of Partitions**

```python
# Công thức:
# Partitions = max(
#     Target Throughput / Producer Throughput per Partition,
#     Target Throughput / Consumer Throughput per Partition
# )

# Ví dụ:
# - Target: 100MB/s
# - Producer: 10MB/s/partition
# - Consumer: 20MB/s/partition
# → Partitions = max(100/10, 100/20) = 10 partitions
```

---

## 🔒 **Security Best Practices**

### Redis:

```bash
# redis.conf
# 1. Đổi port mặc định
port 6380

# 2. Set password
requirepass your_strong_password

# 3. Disable dangerous commands
rename-command FLUSHDB ""
rename-command FLUSHALL ""
rename-command CONFIG ""

# 4. Bind to specific IP
bind 127.0.0.1 192.168.1.100
```

### Kafka:

```properties
# server.properties
# 1. Enable SSL
listeners=SSL://0.0.0.0:9093
ssl.keystore.location=/var/private/ssl/kafka.server.keystore.jks
ssl.keystore.password=your_password

# 2. Enable SASL authentication
sasl.enabled.mechanisms=PLAIN
sasl.mechanism.inter.broker.protocol=PLAIN

# 3. Enable ACLs
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
super.users=User:admin
```

---

## 📚 Tài Nguyên Học Tập

### Redis:
- 📖 [Redis Documentation](https://redis.io/documentation)
- 📺 [Redis University](https://university.redis.com/)
- 📝 [Redis Best Practices](https://redis.io/topics/best-practices)

### Kafka:
- 📖 [Kafka Documentation](https://kafka.apache.org/documentation/)
- 📺 [Kafka Tutorials](https://kafka-tutorials.confluent.io/)
- 📝 [Confluent Blog](https://www.confluent.io/blog/)

---

## 🎓 Bài Tập Thực Hành

### Level 1: Beginner
1. Cài đặt Redis và Kafka
2. Thực hiện các lệnh cơ bản
3. Viết Producer và Consumer đơn giản

### Level 2: Intermediate
1. Xây dựng cache layer với Redis
2. Implement event-driven architecture với Kafka
3. Xử lý failover và retry

### Level 3: Advanced
1. Setup Redis Cluster
2. Implement Kafka Streams
3. Optimize performance và monitoring

---

## 📞 Kết Luận

### Khi nào dùng Redis?
- ✅ Cần tốc độ đọc/ghi cực nhanh
- ✅ Cache dữ liệu thường xuyên truy cập
- ✅ Session management
- ✅ Real-time analytics
- ✅ Rate limiting, leaderboard

### Khi nào dùng Kafka?
- ✅ Event-driven architecture
- ✅ Microservices communication
- ✅ Log aggregation
- ✅ Stream processing
- ✅ Real-time data pipeline

### Khi nào dùng CẢ HAI?
- ✅ Hệ thống lớn, phức tạp
- ✅ Cần cả cache LẪN message queue
- ✅ Real-time + High performance
- ✅ Scalability cao

---

**Chúc bạn học tốt! 🚀**

*Nếu có câu hỏi gì, đừng ngại hỏi thầy nhé! 😊*
