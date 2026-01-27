# 🎯 Bài Tập Thực Hành Redis & Kafka

## 📚 Mục Lục
1. [Level 1: Beginner](#level-1-beginner)
2. [Level 2: Intermediate](#level-2-intermediate)
3. [Level 3: Advanced](#level-3-advanced)
4. [Dự Án Tổng Hợp](#dự-án-tổng-hợp)

---

# Level 1: Beginner

## 🔴 Redis Exercises

### Bài 1: String Operations - Counter
**Yêu cầu:** Xây dựng hệ thống đếm lượt xem bài viết

```python
# TODO: Implement these functions

def view_article(article_id: int):
    """Tăng view count của bài viết"""
    # Your code here
    pass

def get_view_count(article_id: int) -> int:
    """Lấy số lượt xem"""
    # Your code here
    pass

def get_top_articles(limit: int = 10) -> list:
    """Lấy top N bài viết nhiều view nhất"""
    # Hint: Dùng Sorted Set
    # Your code here
    pass

# Test
view_article(1)
view_article(1)
view_article(2)
print(get_view_count(1))  # Expected: 2
print(get_top_articles(10))
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
import redis

r = redis.Redis(decode_responses=True)

def view_article(article_id: int):
    # Tăng counter
    r.incr(f"article:{article_id}:views")
    
    # Cập nhật sorted set để ranking
    views = r.get(f"article:{article_id}:views")
    r.zadd("article:ranking", {str(article_id): int(views)})

def get_view_count(article_id: int) -> int:
    views = r.get(f"article:{article_id}:views")
    return int(views) if views else 0

def get_top_articles(limit: int = 10) -> list:
    return r.zrevrange("article:ranking", 0, limit-1, withscores=True)
```
</details>

---

### Bài 2: Hash - User Profile Cache
**Yêu cầu:** Cache thông tin user profile

```python
# TODO: Implement these functions

def save_user_profile(user_id: int, profile: dict):
    """Lưu user profile vào Redis"""
    # Cache trong 1 giờ
    # Your code here
    pass

def get_user_profile(user_id: int) -> dict:
    """Lấy user profile từ Redis"""
    # Your code here
    pass

def update_user_field(user_id: int, field: str, value: str):
    """Update một field cụ thể"""
    # Your code here
    pass

# Test
profile = {
    "name": "John Doe",
    "email": "john@example.com",
    "age": "25"
}
save_user_profile(1, profile)
print(get_user_profile(1))
update_user_field(1, "age", "26")
print(get_user_profile(1))
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
def save_user_profile(user_id: int, profile: dict):
    key = f"user:{user_id}:profile"
    r.hset(key, mapping=profile)
    r.expire(key, 3600)  # 1 giờ

def get_user_profile(user_id: int) -> dict:
    return r.hgetall(f"user:{user_id}:profile")

def update_user_field(user_id: int, field: str, value: str):
    r.hset(f"user:{user_id}:profile", field, value)
```
</details>

---

### Bài 3: List - Task Queue
**Yêu cầu:** Xây dựng task queue đơn giản

```python
# TODO: Implement these functions

def add_task(task_data: dict):
    """Thêm task vào queue"""
    # Your code here
    pass

def get_next_task() -> dict:
    """Lấy task tiếp theo (FIFO)"""
    # Your code here
    pass

def get_queue_length() -> int:
    """Số lượng tasks đang chờ"""
    # Your code here
    pass

# Test
add_task({"type": "email", "to": "user@example.com"})
add_task({"type": "sms", "to": "+1234567890"})
print(get_queue_length())  # Expected: 2
print(get_next_task())
print(get_queue_length())  # Expected: 1
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
import json

def add_task(task_data: dict):
    r.rpush("task:queue", json.dumps(task_data))

def get_next_task() -> dict:
    task_json = r.lpop("task:queue")
    return json.loads(task_json) if task_json else None

def get_queue_length() -> int:
    return r.llen("task:queue")
```
</details>

---

## 🟢 Kafka Exercises

### Bài 4: Simple Producer & Consumer
**Yêu cầu:** Tạo producer gửi messages và consumer nhận messages

```python
# TODO: Implement producer
def send_log(level: str, message: str):
    """Gửi log message vào Kafka"""
    # Topic: "application.logs"
    # Your code here
    pass

# TODO: Implement consumer
def consume_logs():
    """Nhận và in ra logs"""
    # Your code here
    pass

# Test
send_log("INFO", "Application started")
send_log("ERROR", "Database connection failed")
consume_logs()
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
from kafka import KafkaProducer, KafkaConsumer
import json
from datetime import datetime

# Producer
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def send_log(level: str, message: str):
    log_data = {
        "level": level,
        "message": message,
        "timestamp": datetime.now().isoformat()
    }
    producer.send("application.logs", value=log_data)
    print(f"Sent: {log_data}")

# Consumer
def consume_logs():
    consumer = KafkaConsumer(
        "application.logs",
        bootstrap_servers='localhost:9092',
        value_deserializer=lambda m: json.loads(m.decode('utf-8')),
        auto_offset_reset='earliest'
    )
    
    print("Listening for logs...")
    for message in consumer:
        log = message.value
        print(f"[{log['level']}] {log['timestamp']}: {log['message']}")
```
</details>

---

# Level 2: Intermediate

## 🔴 Redis Exercises

### Bài 5: Leaderboard với Sorted Set
**Yêu cầu:** Xây dựng game leaderboard với các features:
- Add/Update player score
- Get player rank
- Get top N players
- Get players around a specific player

```python
# TODO: Implement
class GameLeaderboard:
    def __init__(self):
        self.redis = redis.Redis(decode_responses=True)
        self.key = "game:leaderboard"
    
    def add_score(self, player_id: str, score: int):
        """Thêm/Update score"""
        pass
    
    def get_rank(self, player_id: str) -> int:
        """Lấy rank (1-based)"""
        pass
    
    def get_top_players(self, n: int = 10) -> list:
        """Lấy top N players"""
        pass
    
    def get_nearby_players(self, player_id: str, range: int = 2) -> list:
        """Lấy players xung quanh"""
        pass
    
    def get_player_score(self, player_id: str) -> int:
        """Lấy score của player"""
        pass

# Test
lb = GameLeaderboard()
lb.add_score("player1", 100)
lb.add_score("player2", 200)
lb.add_score("player3", 150)
print(lb.get_rank("player2"))  # Expected: 1
print(lb.get_top_players(3))
print(lb.get_nearby_players("player3", 1))
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
class GameLeaderboard:
    def __init__(self):
        self.redis = redis.Redis(decode_responses=True)
        self.key = "game:leaderboard"
    
    def add_score(self, player_id: str, score: int):
        self.redis.zadd(self.key, {player_id: score})
    
    def get_rank(self, player_id: str) -> int:
        rank = self.redis.zrevrank(self.key, player_id)
        return rank + 1 if rank is not None else None
    
    def get_top_players(self, n: int = 10) -> list:
        return self.redis.zrevrange(self.key, 0, n-1, withscores=True)
    
    def get_nearby_players(self, player_id: str, range: int = 2) -> list:
        rank = self.redis.zrevrank(self.key, player_id)
        if rank is None:
            return []
        
        start = max(0, rank - range)
        end = rank + range
        return self.redis.zrevrange(self.key, start, end, withscores=True)
    
    def get_player_score(self, player_id: str) -> int:
        score = self.redis.zscore(self.key, player_id)
        return int(score) if score else 0
```
</details>

---

### Bài 6: Rate Limiter (Sliding Window)
**Yêu cầu:** Implement rate limiter cho API
- Giới hạn 10 requests/phút cho mỗi user
- Dùng Sorted Set để track requests

```python
# TODO: Implement
class RateLimiter:
    def __init__(self, max_requests: int = 10, window_seconds: int = 60):
        self.redis = redis.Redis(decode_responses=True)
        self.max_requests = max_requests
        self.window = window_seconds
    
    def is_allowed(self, user_id: str) -> bool:
        """Check nếu request được phép"""
        pass
    
    def get_remaining_requests(self, user_id: str) -> int:
        """Số requests còn lại"""
        pass
    
    def get_reset_time(self, user_id: str) -> int:
        """Thời gian reset (seconds)"""
        pass

# Test
limiter = RateLimiter(max_requests=3, window_seconds=60)
print(limiter.is_allowed("user1"))  # True
print(limiter.is_allowed("user1"))  # True
print(limiter.is_allowed("user1"))  # True
print(limiter.is_allowed("user1"))  # False (exceeded)
print(limiter.get_remaining_requests("user1"))  # 0
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
import time

class RateLimiter:
    def __init__(self, max_requests: int = 10, window_seconds: int = 60):
        self.redis = redis.Redis(decode_responses=True)
        self.max_requests = max_requests
        self.window = window_seconds
    
    def is_allowed(self, user_id: str) -> bool:
        key = f"rate_limit:{user_id}"
        now = time.time()
        window_start = now - self.window
        
        # Xóa requests cũ
        self.redis.zremrangebyscore(key, 0, window_start)
        
        # Đếm requests trong window
        count = self.redis.zcard(key)
        
        if count >= self.max_requests:
            return False
        
        # Thêm request mới
        self.redis.zadd(key, {str(now): now})
        self.redis.expire(key, self.window)
        
        return True
    
    def get_remaining_requests(self, user_id: str) -> int:
        key = f"rate_limit:{user_id}"
        now = time.time()
        window_start = now - self.window
        
        self.redis.zremrangebyscore(key, 0, window_start)
        count = self.redis.zcard(key)
        
        return max(0, self.max_requests - count)
    
    def get_reset_time(self, user_id: str) -> int:
        key = f"rate_limit:{user_id}"
        oldest = self.redis.zrange(key, 0, 0, withscores=True)
        
        if not oldest:
            return 0
        
        oldest_time = oldest[0][1]
        reset_time = oldest_time + self.window
        remaining = max(0, reset_time - time.time())
        
        return int(remaining)
```
</details>

---

## 🟢 Kafka Exercises

### Bài 7: Order Processing System
**Yêu cầu:** Xây dựng hệ thống xử lý đơn hàng với nhiều consumers
- Producer: Tạo orders
- Consumer 1: Gửi email confirmation
- Consumer 2: Update inventory
- Consumer 3: Calculate loyalty points

```python
# TODO: Implement
class OrderProducer:
    def create_order(self, order_data: dict):
        """Tạo order và gửi vào Kafka"""
        pass

class EmailConsumer:
    def process(self):
        """Gửi email confirmation"""
        pass

class InventoryConsumer:
    def process(self):
        """Update inventory"""
        pass

class LoyaltyConsumer:
    def process(self):
        """Calculate points"""
        pass

# Test
producer = OrderProducer()
producer.create_order({
    "order_id": "ORD001",
    "user_id": "user123",
    "email": "user@example.com",
    "items": [{"product_id": "P1", "quantity": 2}],
    "total": 99.99
})

# Run consumers in separate terminals
EmailConsumer().process()
InventoryConsumer().process()
LoyaltyConsumer().process()
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
from kafka import KafkaProducer, KafkaConsumer
import json

# Producer
class OrderProducer:
    def __init__(self):
        self.producer = KafkaProducer(
            bootstrap_servers='localhost:9092',
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )
    
    def create_order(self, order_data: dict):
        self.producer.send('orders', value=order_data)
        print(f"Order {order_data['order_id']} created")

# Email Consumer
class EmailConsumer:
    def __init__(self):
        self.consumer = KafkaConsumer(
            'orders',
            bootstrap_servers='localhost:9092',
            group_id='email-service',
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )
    
    def process(self):
        print("Email Consumer started...")
        for message in self.consumer:
            order = message.value
            print(f"📧 Sending email to {order['email']} for order {order['order_id']}")

# Inventory Consumer
class InventoryConsumer:
    def __init__(self):
        self.consumer = KafkaConsumer(
            'orders',
            bootstrap_servers='localhost:9092',
            group_id='inventory-service',
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )
    
    def process(self):
        print("Inventory Consumer started...")
        for message in self.consumer:
            order = message.value
            for item in order['items']:
                print(f"📦 Updating inventory for {item['product_id']}: -{item['quantity']}")

# Loyalty Consumer
class LoyaltyConsumer:
    def __init__(self):
        self.consumer = KafkaConsumer(
            'orders',
            bootstrap_servers='localhost:9092',
            group_id='loyalty-service',
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )
    
    def process(self):
        print("Loyalty Consumer started...")
        for message in self.consumer:
            order = message.value
            points = int(order['total'] * 10)  # 10 points per dollar
            print(f"⭐ Added {points} points to user {order['user_id']}")
```
</details>

---

# Level 3: Advanced

### Bài 8: Distributed Lock với Redis
**Yêu cầu:** Implement distributed lock để đảm bảo chỉ 1 process có thể chạy tại một thời điểm

```python
# TODO: Implement
class DistributedLock:
    def __init__(self, lock_name: str, timeout: int = 10):
        """
        lock_name: Tên của lock
        timeout: Thời gian tự động release (seconds)
        """
        pass
    
    def acquire(self) -> bool:
        """Acquire lock"""
        pass
    
    def release(self) -> bool:
        """Release lock"""
        pass
    
    def __enter__(self):
        """Context manager entry"""
        pass
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit"""
        pass

# Test
# Process 1
with DistributedLock("critical_section"):
    print("Process 1: Doing critical work...")
    time.sleep(5)

# Process 2 (chạy đồng thời)
with DistributedLock("critical_section"):
    print("Process 2: This should wait...")
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
import uuid
import time

class DistributedLock:
    def __init__(self, lock_name: str, timeout: int = 10):
        self.redis = redis.Redis(decode_responses=True)
        self.lock_name = f"lock:{lock_name}"
        self.timeout = timeout
        self.lock_id = str(uuid.uuid4())
    
    def acquire(self) -> bool:
        """Acquire lock using SET NX EX"""
        return self.redis.set(
            self.lock_name,
            self.lock_id,
            nx=True,  # Only set if not exists
            ex=self.timeout  # Expire time
        )
    
    def release(self) -> bool:
        """Release lock using Lua script (atomic)"""
        lua_script = """
        if redis.call("get", KEYS[1]) == ARGV[1] then
            return redis.call("del", KEYS[1])
        else
            return 0
        end
        """
        
        release_script = self.redis.register_script(lua_script)
        return release_script(keys=[self.lock_name], args=[self.lock_id])
    
    def __enter__(self):
        # Try to acquire lock with retry
        max_retries = 10
        for i in range(max_retries):
            if self.acquire():
                return self
            time.sleep(0.5)
        
        raise Exception("Failed to acquire lock")
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.release()
```
</details>

---

### Bài 9: Kafka Streams - Real-time Analytics
**Yêu cầu:** Tính tổng revenue theo từng sản phẩm real-time

```python
# TODO: Implement
class RevenueAnalytics:
    def __init__(self):
        """
        Input: Topic "sales" - {product_id, quantity, price}
        Output: Topic "revenue" - {product_id, total_revenue}
        """
        pass
    
    def process(self):
        """Process and aggregate revenue"""
        pass

# Test
# Producer gửi sales events
sales = [
    {"product_id": "P1", "quantity": 2, "price": 10.0},
    {"product_id": "P2", "quantity": 1, "price": 20.0},
    {"product_id": "P1", "quantity": 3, "price": 10.0},
]

# Consumer nhận revenue updates
# P1: 50.0 (2*10 + 3*10)
# P2: 20.0 (1*20)
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
from kafka import KafkaProducer, KafkaConsumer
from collections import defaultdict
import json

class RevenueAnalytics:
    def __init__(self):
        self.consumer = KafkaConsumer(
            'sales',
            bootstrap_servers='localhost:9092',
            group_id='revenue-analytics',
            value_deserializer=lambda m: json.loads(m.decode('utf-8')),
            auto_offset_reset='earliest'
        )
        
        self.producer = KafkaProducer(
            bootstrap_servers='localhost:9092',
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )
        
        # In-memory state
        self.revenue_state = defaultdict(float)
    
    def process(self):
        print("Revenue Analytics started...")
        
        for message in self.consumer:
            sale = message.value
            product_id = sale['product_id']
            revenue = sale['quantity'] * sale['price']
            
            # Update state
            self.revenue_state[product_id] += revenue
            
            # Publish update
            result = {
                "product_id": product_id,
                "total_revenue": self.revenue_state[product_id]
            }
            
            self.producer.send('revenue', value=result)
            print(f"📊 {product_id}: ${self.revenue_state[product_id]}")

# Producer
def send_sales():
    producer = KafkaProducer(
        bootstrap_servers='localhost:9092',
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    
    sales = [
        {"product_id": "P1", "quantity": 2, "price": 10.0},
        {"product_id": "P2", "quantity": 1, "price": 20.0},
        {"product_id": "P1", "quantity": 3, "price": 10.0},
    ]
    
    for sale in sales:
        producer.send('sales', value=sale)
        print(f"Sent: {sale}")
```
</details>

---

### Bài 10: Cache với Read-Through Pattern
**Yêu cầu:** Implement cache decorator với read-through pattern

```python
# TODO: Implement
def cache_with_redis(expire: int = 3600):
    """
    Decorator để cache kết quả function
    - Tự động get từ cache
    - Nếu không có, gọi function và cache kết quả
    """
    def decorator(func):
        def wrapper(*args, **kwargs):
            # Your code here
            pass
        return wrapper
    return decorator

# Test
@cache_with_redis(expire=60)
def get_user_from_db(user_id: int):
    print(f"DB Query: Loading user {user_id}")
    time.sleep(1)  # Simulate slow DB
    return {
        "id": user_id,
        "name": f"User {user_id}",
        "email": f"user{user_id}@example.com"
    }

# First call - DB query
user = get_user_from_db(1)  # Prints "DB Query..."

# Second call - From cache
user = get_user_from_db(1)  # No print (from cache)
```

**Solution:**
<details>
<summary>Xem đáp án</summary>

```python
import functools
import json
import hashlib

def cache_with_redis(expire: int = 3600):
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            r = redis.Redis(decode_responses=True)
            
            # Generate cache key from function name and arguments
            key_data = f"{func.__name__}:{args}:{kwargs}"
            cache_key = f"cache:{hashlib.md5(key_data.encode()).hexdigest()}"
            
            # Try to get from cache
            cached = r.get(cache_key)
            if cached:
                print(f"✅ Cache HIT: {func.__name__}")
                return json.loads(cached)
            
            # Cache miss - call function
            print(f"❌ Cache MISS: {func.__name__}")
            result = func(*args, **kwargs)
            
            # Save to cache
            r.setex(cache_key, expire, json.dumps(result))
            
            return result
        
        return wrapper
    return decorator
```
</details>

---

# Dự Án Tổng Hợp

## 🎯 Project: Real-time E-commerce Platform

**Mô tả:** Xây dựng một nền tảng e-commerce với các tính năng:

### Features:
1. **Product Catalog với Cache**
   - Cache product details trong Redis
   - Cache hit rate > 95%
   - Invalidate cache khi product update

2. **Shopping Cart với Redis**
   - Lưu cart trong Redis (session)
   - Expire sau 30 phút không activity
   - Support concurrent updates

3. **Order Processing với Kafka**
   - Event-driven architecture
   - Async processing: Email, SMS, Inventory
   - Dead letter queue cho failed messages

4. **Real-time Analytics**
   - Track revenue theo real-time
   - Top selling products
   - Active users count

5. **Rate Limiting**
   - API rate limit: 100 req/min per user
   - Prevent DDoS

### Architecture:
```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ↓
┌─────────────────────────────┐
│      API Gateway            │
│   (Rate Limiting)           │
└──────┬──────────────────────┘
       │
       ↓
┌─────────────────────────────┐
│    Product Service          │
│  (Redis Cache)              │
└──────┬──────────────────────┘
       │
       ↓
┌─────────────────────────────┐
│    Order Service            │
│  (Kafka Producer)           │
└──────┬──────────────────────┘
       │
       ↓
┌─────────────────────────────┐
│      Kafka                  │
└──────┬──────────────────────┘
       │
       ├─→ Email Consumer
       ├─→ SMS Consumer
       ├─→ Inventory Consumer
       └─→ Analytics Consumer
```

### Requirements:
- Python 3.8+
- Redis
- Kafka
- Flask/FastAPI

### Deliverables:
1. Source code
2. README với hướng dẫn setup
3. Docker Compose file
4. Load testing results
5. Performance metrics

### Evaluation Criteria:
- ✅ Functionality (40%)
- ✅ Code quality (20%)
- ✅ Performance (20%)
- ✅ Documentation (10%)
- ✅ Error handling (10%)

---

## 📊 Rubric

| Level | Redis Skills | Kafka Skills |
|-------|-------------|--------------|
| **Beginner** | - Basic data types<br>- Simple cache<br>- String operations | - Producer/Consumer<br>- Single topic<br>- Basic serialization |
| **Intermediate** | - Advanced data structures<br>- Leaderboard<br>- Rate limiting<br>- Pub/Sub | - Multiple consumers<br>- Consumer groups<br>- Error handling<br>- Offset management |
| **Advanced** | - Distributed lock<br>- Lua scripts<br>- Custom patterns<br>- Performance tuning | - Stream processing<br>- Exactly-once semantics<br>- Complex aggregations<br>- Monitoring |

---

## 🎓 Bonus Challenges

### Challenge 1: Implement Cache Stampede Prevention
Khi cache expire, nhiều requests cùng lúc query DB → Overload

**Solution:** Sử dụng Redis lock để chỉ 1 request query DB

### Challenge 2: Kafka Consumer với Exactly-Once Processing
Đảm bảo mỗi message được xử lý đúng 1 lần

### Challenge 3: Redis Cluster Setup
Setup Redis cluster với 3 master + 3 replica nodes

### Challenge 4: Kafka Performance Optimization
Optimize producer và consumer để đạt 100,000 messages/second

---

**Chúc bạn thực hành vui vẻ! 💪**

*Nhớ làm từ dễ đến khó, và đừng ngại hỏi khi gặp khó khăn!*
