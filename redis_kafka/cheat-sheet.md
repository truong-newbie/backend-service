# 📝 Redis & Kafka Cheat Sheet - Tra Cứu Nhanh

## 🔴 Redis Commands

### String Operations
```bash
# Set/Get
SET key value
GET key
SETEX key seconds value          # Set với expire
SETNX key value                  # Set nếu key chưa tồn tại
DEL key                          # Xóa key

# Increment/Decrement
INCR key                         # Tăng 1
INCRBY key amount                # Tăng amount
DECR key                         # Giảm 1
DECRBY key amount                # Giảm amount
```

### Hash Operations
```bash
# Set/Get
HSET key field value
HGET key field
HGETALL key                      # Lấy tất cả
HMSET key field1 value1 field2 value2
HDEL key field

# Operations
HINCRBY key field amount         # Tăng field
HEXISTS key field                # Check field tồn tại
HLEN key                         # Số lượng fields
```

### List Operations
```bash
# Push/Pop
LPUSH key value                  # Thêm đầu list
RPUSH key value                  # Thêm cuối list
LPOP key                         # Lấy đầu list
RPOP key                         # Lấy cuối list

# Range
LRANGE key start stop            # Lấy range
LLEN key                         # Độ dài list
LTRIM key start stop             # Giữ chỉ range
```

### Set Operations
```bash
# Add/Remove
SADD key member                  # Thêm member
SREM key member                  # Xóa member
SMEMBERS key                     # Lấy tất cả members

# Operations
SISMEMBER key member             # Check member tồn tại
SCARD key                        # Số lượng members
SINTER key1 key2                 # Intersection
SUNION key1 key2                 # Union
SDIFF key1 key2                  # Difference
```

### Sorted Set Operations
```bash
# Add/Remove
ZADD key score member            # Thêm với score
ZREM key member                  # Xóa member

# Range
ZRANGE key start stop [WITHSCORES]        # Tăng dần
ZREVRANGE key start stop [WITHSCORES]     # Giảm dần
ZRANGEBYSCORE key min max                 # Range theo score

# Operations
ZSCORE key member                # Lấy score
ZRANK key member                 # Lấy rank (tăng dần)
ZREVRANK key member              # Lấy rank (giảm dần)
ZINCRBY key amount member        # Tăng score
```

### Key Management
```bash
# Expiration
EXPIRE key seconds               # Set expire
TTL key                          # Xem thời gian còn lại
PERSIST key                      # Xóa expire

# Info
EXISTS key                       # Check key tồn tại
TYPE key                         # Kiểu dữ liệu
KEYS pattern                     # Tìm keys (KHÔNG dùng production!)
SCAN cursor [MATCH pattern]      # Tìm keys an toàn

# Rename
RENAME key newkey
```

### Pub/Sub
```bash
# Publisher
PUBLISH channel message

# Subscriber
SUBSCRIBE channel
UNSUBSCRIBE channel
PSUBSCRIBE pattern               # Subscribe theo pattern
```

### Transactions
```bash
MULTI                            # Bắt đầu transaction
... commands ...
EXEC                             # Thực thi
DISCARD                          # Hủy transaction
WATCH key                        # Watch key
```

---

## 🟢 Kafka Commands

### Topic Management
```bash
# Create topic
kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic my-topic \
  --partitions 3 \
  --replication-factor 2

# List topics
kafka-topics --list \
  --bootstrap-server localhost:9092

# Describe topic
kafka-topics --describe \
  --bootstrap-server localhost:9092 \
  --topic my-topic

# Delete topic
kafka-topics --delete \
  --bootstrap-server localhost:9092 \
  --topic my-topic
```

### Producer Commands
```bash
# Console producer
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic my-topic

# Producer with key
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic my-topic \
  --property "parse.key=true" \
  --property "key.separator=:"
```

### Consumer Commands
```bash
# Console consumer
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic my-topic

# From beginning
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic my-topic \
  --from-beginning

# With group
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic my-topic \
  --group my-group

# With key
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic my-topic \
  --property print.key=true \
  --property key.separator=":"
```

### Consumer Group Management
```bash
# List groups
kafka-consumer-groups --list \
  --bootstrap-server localhost:9092

# Describe group
kafka-consumer-groups --describe \
  --bootstrap-server localhost:9092 \
  --group my-group

# Reset offset
kafka-consumer-groups --reset-offsets \
  --bootstrap-server localhost:9092 \
  --group my-group \
  --topic my-topic \
  --to-earliest \
  --execute
```

### Performance Testing
```bash
# Producer performance test
kafka-producer-perf-test \
  --topic my-topic \
  --num-records 1000000 \
  --record-size 1024 \
  --throughput -1 \
  --producer-props bootstrap.servers=localhost:9092

# Consumer performance test
kafka-consumer-perf-test \
  --topic my-topic \
  --bootstrap-server localhost:9092 \
  --messages 1000000
```

---

## 🐍 Python Code Snippets

### Redis - Python
```python
import redis

# Connect
r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

# String
r.set('name', 'John')
name = r.get('name')

# Hash
r.hset('user:1', mapping={'name': 'John', 'age': 25})
user = r.hgetall('user:1')

# List
r.rpush('queue', 'task1', 'task2')
task = r.lpop('queue')

# Set
r.sadd('tags', 'python', 'redis', 'kafka')
tags = r.smembers('tags')

# Sorted Set
r.zadd('leaderboard', {'player1': 100, 'player2': 200})
top = r.zrevrange('leaderboard', 0, 9, withscores=True)

# Pipeline (batch)
pipe = r.pipeline()
pipe.set('key1', 'value1')
pipe.set('key2', 'value2')
pipe.execute()

# Pub/Sub
pubsub = r.pubsub()
pubsub.subscribe('news')
for message in pubsub.listen():
    print(message)
```

### Kafka - Python Producer
```python
from kafka import KafkaProducer
import json

# Create producer
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
    acks='all',
    retries=3
)

# Send message
data = {'user_id': 123, 'action': 'login'}
future = producer.send('events', value=data)

# Send with key
producer.send('events', key=b'user_123', value=data)

# Wait for confirmation
record = future.get(timeout=10)
print(f"Sent to partition {record.partition} at offset {record.offset}")

# Close
producer.close()
```

### Kafka - Python Consumer
```python
from kafka import KafkaConsumer
import json

# Create consumer
consumer = KafkaConsumer(
    'events',
    bootstrap_servers='localhost:9092',
    group_id='my-group',
    value_deserializer=lambda m: json.loads(m.decode('utf-8')),
    auto_offset_reset='earliest',  # 'earliest' hoặc 'latest'
    enable_auto_commit=True
)

# Consume messages
for message in consumer:
    print(f"Topic: {message.topic}")
    print(f"Partition: {message.partition}")
    print(f"Offset: {message.offset}")
    print(f"Key: {message.key}")
    print(f"Value: {message.value}")

# Manual commit
consumer = KafkaConsumer(
    'events',
    enable_auto_commit=False
)

for message in consumer:
    process(message.value)
    consumer.commit()  # Commit sau khi xử lý xong
```

---

## 🎯 Common Patterns

### Pattern 1: Cache-Aside
```python
def get_user(user_id):
    # Try cache first
    user = redis.get(f"user:{user_id}")
    if user:
        return json.loads(user)
    
    # Cache miss - get from DB
    user = db.get_user(user_id)
    
    # Save to cache
    redis.setex(f"user:{user_id}", 3600, json.dumps(user))
    
    return user
```

### Pattern 2: Write-Through Cache
```python
def update_user(user_id, data):
    # Update DB
    db.update_user(user_id, data)
    
    # Update cache
    redis.setex(f"user:{user_id}", 3600, json.dumps(data))
```

### Pattern 3: Event Sourcing
```python
# Producer
def create_order(order_data):
    order_id = db.save_order(order_data)
    
    event = {
        'event_type': 'ORDER_CREATED',
        'order_id': order_id,
        'timestamp': time.time(),
        'data': order_data
    }
    
    producer.send('orders', value=event)
    return order_id

# Consumer
consumer = KafkaConsumer('orders')
for message in consumer:
    event = message.value
    
    if event['event_type'] == 'ORDER_CREATED':
        send_confirmation_email(event['data'])
```

### Pattern 4: CQRS (Command Query Responsibility Segregation)
```python
# Write Side (Command)
def create_product(data):
    product_id = db.insert(data)
    
    # Publish event
    producer.send('product.events', {
        'type': 'PRODUCT_CREATED',
        'product_id': product_id,
        'data': data
    })

# Read Side (Query)
consumer = KafkaConsumer('product.events')
for message in consumer:
    event = message.value
    
    # Update read-optimized store (Redis)
    if event['type'] == 'PRODUCT_CREATED':
        redis.hset(f"product:{event['product_id']}", 
                   mapping=event['data'])
```

### Pattern 5: Rate Limiting (Sliding Window)
```python
def is_rate_limited(user_id, limit=10, window=60):
    key = f"rate:{user_id}"
    current = time.time()
    
    # Remove old entries
    redis.zremrangebyscore(key, 0, current - window)
    
    # Count requests in window
    count = redis.zcard(key)
    
    if count >= limit:
        return True
    
    # Add current request
    redis.zadd(key, {str(current): current})
    redis.expire(key, window)
    
    return False
```

---

## 🔧 Configuration Templates

### Redis Configuration (redis.conf)
```ini
# Network
bind 127.0.0.1
port 6379
protected-mode yes

# Memory
maxmemory 2gb
maxmemory-policy allkeys-lru

# Persistence
save 900 1
save 300 10
save 60 10000
appendonly yes
appendfsync everysec

# Security
requirepass your_password_here

# Performance
tcp-backlog 511
timeout 300
```

### Kafka Producer Config
```python
producer_config = {
    'bootstrap.servers': 'localhost:9092',
    'acks': 'all',                    # 0, 1, or all
    'retries': 3,
    'max.in.flight.requests.per.connection': 5,
    'compression.type': 'snappy',     # none, gzip, snappy, lz4
    'batch.size': 16384,
    'linger.ms': 10,
    'buffer.memory': 33554432,
}
```

### Kafka Consumer Config
```python
consumer_config = {
    'bootstrap.servers': 'localhost:9092',
    'group.id': 'my-group',
    'auto.offset.reset': 'earliest',  # earliest or latest
    'enable.auto.commit': True,
    'auto.commit.interval.ms': 5000,
    'max.poll.records': 500,
    'session.timeout.ms': 30000,
    'heartbeat.interval.ms': 3000,
}
```

---

## 🚨 Common Mistakes & Solutions

### Redis Mistakes

#### ❌ Mistake 1: Không set expiration
```python
# BAD
redis.set('session:abc', data)

# GOOD
redis.setex('session:abc', 3600, data)
```

#### ❌ Mistake 2: Dùng KEYS trong production
```python
# BAD - Block Redis
keys = redis.keys('user:*')

# GOOD - Non-blocking
cursor = 0
while True:
    cursor, keys = redis.scan(cursor, match='user:*', count=100)
    # Process keys
    if cursor == 0:
        break
```

#### ❌ Mistake 3: Lưu data quá lớn
```python
# BAD
redis.set('big_data', huge_json)  # 10MB+

# GOOD - Chia nhỏ
for chunk in chunks(data, 1000):
    redis.set(f'data:{chunk.id}', chunk)
```

### Kafka Mistakes

#### ❌ Mistake 4: Không handle consumer rebalance
```python
# BAD
consumer = KafkaConsumer('topic')
for message in consumer:
    process(message)  # Mất data khi rebalance

# GOOD
consumer = KafkaConsumer(
    'topic',
    enable_auto_commit=False
)
for message in consumer:
    try:
        process(message)
        consumer.commit()
    except Exception:
        # Không commit - sẽ xử lý lại
        pass
```

#### ❌ Mistake 5: Partition quá ít/nhiều
```python
# BAD - 1 partition = không parallel
kafka-topics --create --partitions 1

# BAD - Quá nhiều partitions = overhead
kafka-topics --create --partitions 1000

# GOOD - Vừa phải (thường 3-10)
kafka-topics --create --partitions 6
```

---

## 📊 Monitoring Commands

### Redis Monitoring
```bash
# Real-time monitoring
redis-cli monitor

# Stats
redis-cli info
redis-cli info stats
redis-cli info memory

# Slow queries
redis-cli slowlog get 10

# Client list
redis-cli client list
```

### Kafka Monitoring
```bash
# Topic lag
kafka-consumer-groups --describe \
  --bootstrap-server localhost:9092 \
  --group my-group

# Broker metrics
kafka-run-class kafka.tools.JmxTool \
  --object-name kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec

# Under-replicated partitions
kafka-topics --describe \
  --bootstrap-server localhost:9092 \
  --under-replicated-partitions
```

---

## 🎓 Quick Decision Tree

### Khi nào dùng Redis?
```
Cần tốc độ cực nhanh? ──YES──> Redis
        │
        NO
        ↓
Cần cache data? ──YES──> Redis
        │
        NO
        ↓
Session storage? ──YES──> Redis
        │
        NO
        ↓
Real-time leaderboard? ──YES──> Redis
```

### Khi nào dùng Kafka?
```
Event-driven architecture? ──YES──> Kafka
        │
        NO
        ↓
Async processing? ──YES──> Kafka
        │
        NO
        ↓
Log aggregation? ──YES──> Kafka
        │
        NO
        ↓
Microservices messaging? ──YES──> Kafka
```

---

## 🔗 Useful Links

### Redis
- [Redis Documentation](https://redis.io/documentation)
- [Redis Commands](https://redis.io/commands)
- [Try Redis](https://try.redis.io/)

### Kafka
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Kafka](https://docs.confluent.io/)
- [Kafka Tutorials](https://kafka-tutorials.confluent.io/)

---

**💡 Tip:** In cheat sheet này ra và để bên cạnh khi code!
