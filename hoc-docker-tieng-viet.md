# 🐳 HỌC DOCKER TỪ CƠ BẢN ĐẾN DEPLOY

**Tài liệu học Docker bằng tiếng Việt cho người mới bắt đầu**

---

## 📚 MỤC LỤC

- [Phần 1: Docker Cơ Bản](#phần-1-docker-cơ-bản)
- [Phần 2: Dockerfile](#phần-2-dockerfile)
- [Phần 3: Docker Compose](#phần-3-docker-compose)
- [Phần 4: Volumes & Networks](#phần-4-volumes--networks)
- [Phần 5: Deploy Production](#phần-5-deploy-production)
- [Phần 6: Ví Dụ Thực Tế](#phần-6-ví-dụ-thực-tế)

---

## Phần 1: Docker Cơ Bản

### Docker là gì?

**Docker** = Công cụ đóng gói ứng dụng vào "containers" để chạy mọi nơi giống nhau.

**Ví dụ thực tế:**
- Bạn code trên máy: Node 18, Ubuntu 22.04 → Chạy OK ✅
- Đưa cho bạn: Node 16, Windows 11 → Lỗi ❌  
- Deploy lên server: Node 14, CentOS → Lỗi ❌

**Với Docker:**
- Đóng gói app + Node 18 + dependencies vào container
- Chạy ở đâu cũng giống nhau! ✅✅✅

### Khái niệm cơ bản

```
┌─────────────┐
│   IMAGE     │  ← Bản thiết kế (như công thức nấu ăn)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ CONTAINER   │  ← Món ăn thực tế đang nấu
└─────────────┘
```

**3 khái niệm chính:**

1. **Image** = Bản thiết kế
   - Chỉ đọc (read-only)
   - Ví dụ: `nginx:latest`, `node:18`

2. **Container** = Instance đang chạy
   - Tạo từ Image
   - Có thể start/stop/restart

3. **Dockerfile** = "Công thức" tạo Image
   - File text chứa các bước build

### Cài đặt Docker

**Ubuntu/Debian:**
```bash
# Cài Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Kiểm tra
docker --version
docker run hello-world
```

**Windows/Mac:**
- Tải Docker Desktop: https://www.docker.com/products/docker-desktop

### Lệnh cơ bản MỚI NHẤT PHẢI NHỚ

```bash
# ═══════════════════════════════════════
# IMAGES
# ═══════════════════════════════════════

# Tải image về
docker pull nginx
docker pull node:18-alpine

# Xem images đã tải
docker images

# Xóa image
docker rmi nginx

# Build image từ Dockerfile
docker build -t my-app:1.0 .

# ═══════════════════════════════════════
# CONTAINERS
# ═══════════════════════════════════════

# Chạy container
docker run nginx                    # Chạy và attach terminal
docker run -d nginx                 # Chạy background (detached)
docker run -d --name web nginx      # Đặt tên
docker run -d -p 8080:80 nginx      # Map port host:container

# Xem containers
docker ps              # Đang chạy
docker ps -a           # Tất cả (cả đã dừng)

# Quản lý containers
docker stop web        # Dừng
docker start web       # Khởi động lại
docker restart web     # Restart
docker rm web          # Xóa (phải stop trước)
docker rm -f web       # Force xóa (đang chạy)

# Xem logs
docker logs web
docker logs -f web     # Follow real-time

# Vào trong container
docker exec -it web bash
docker exec -it web sh             # Nếu không có bash

# Copy files
docker cp file.txt web:/app/       # Host → Container
docker cp web:/app/log.txt .       # Container → Host

# Xem stats
docker stats
docker stats web
```

### Ví dụ đầu tiên

```bash
# 1. Chạy Nginx web server
docker run -d -p 8080:80 --name my-nginx nginx

# 2. Kiểm tra
curl http://localhost:8080

# 3. Xem logs
docker logs my-nginx

# 4. Dừng và xóa
docker stop my-nginx
docker rm my-nginx
```

---

## Phần 2: Dockerfile

### Cấu trúc Dockerfile

```dockerfile
# 1. Base image
FROM node:18-alpine

# 2. Working directory
WORKDIR /app

# 3. Copy files
COPY package*.json ./

# 4. Install dependencies
RUN npm install

# 5. Copy source code
COPY . .

# 6. Expose port
EXPOSE 3000

# 7. Run command
CMD ["node", "server.js"]
```

### Các instruction quan trọng

```dockerfile
# ═══════════════════════════════════════
# FROM - Chọn base image
# ═══════════════════════════════════════
FROM ubuntu:22.04
FROM node:18                # Full (~900MB)
FROM node:18-alpine         # Nhẹ (~170MB) ✅ Khuyến nghị

# ═══════════════════════════════════════
# WORKDIR - Thư mục làm việc
# ═══════════════════════════════════════
WORKDIR /app
# Tạo folder /app và cd vào đó

# ═══════════════════════════════════════
# COPY vs ADD
# ═══════════════════════════════════════
COPY file.txt /app/              # Copy đơn giản ✅
COPY . .                         # Copy tất cả

ADD https://example.com/file /app/  # Download từ URL
ADD archive.tar.gz /app/            # Tự động extract

# Khuyến nghị: Dùng COPY cho hầu hết trường hợp

# ═══════════════════════════════════════
# RUN - Chạy lệnh khi BUILD image
# ═══════════════════════════════════════
RUN npm install
RUN apt-get update && apt-get install -y curl

# Best practice: Gộp lệnh để giảm layers
RUN apt-get update &&     apt-get install -y curl vim &&     apt-get clean &&     rm -rf /var/lib/apt/lists/*

# ═══════════════════════════════════════
# CMD vs ENTRYPOINT
# ═══════════════════════════════════════
CMD ["node", "app.js"]           # Lệnh mặc định (có thể override)
ENTRYPOINT ["node"]              # Lệnh cố định
CMD ["app.js"]                   # Params cho ENTRYPOINT

# ═══════════════════════════════════════
# EXPOSE - Khai báo port
# ═══════════════════════════════════════
EXPOSE 3000
# Chỉ là metadata, phải dùng -p khi run container

# ═══════════════════════════════════════
# ENV - Biến môi trường
# ═══════════════════════════════════════
ENV NODE_ENV=production
ENV PORT=3000     DB_HOST=localhost

# ═══════════════════════════════════════
# USER - Chạy với user khác root
# ═══════════════════════════════════════
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
# Best practice: Không chạy với root!
```

### Ví dụ: Node.js App

**Dockerfile:**
```dockerfile
FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source
COPY . .

# Security: Non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup &&     chown -R appuser:appgroup /app
USER appuser

EXPOSE 3000

CMD ["node", "server.js"]
```

**Build và run:**
```bash
# Build
docker build -t my-node-app:1.0 .

# Run
docker run -d -p 3000:3000 --name app my-node-app:1.0

# Test
curl http://localhost:3000
```

### Multi-stage Build (Tối ưu kích thước)

```dockerfile
# ═══════════════════════════════════════
# Stage 1: Build
# ═══════════════════════════════════════
FROM node:18 AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# ═══════════════════════════════════════
# Stage 2: Production
# ═══════════════════════════════════════
FROM node:18-alpine

WORKDIR /app

# Chỉ copy những gì cần từ builder
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY package*.json ./

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 3000
CMD ["node", "dist/server.js"]
```

**Lợi ích:**
- Image builder: ~900MB
- Image cuối: ~170MB  
- Giảm 80% dung lượng! 🎉

### .dockerignore

Tạo file `.dockerignore` để loại bỏ files không cần:

```
node_modules
npm-debug.log
.git
.gitignore
README.md
.env
.DS_Store
*.log
dist
coverage
.vscode
.idea
```

---

## Phần 3: Docker Compose

### Docker Compose là gì?

Tool để quản lý **nhiều containers** cùng lúc bằng 1 file YAML.

**Ví dụ:**
- Web app cần: Frontend + Backend + Database + Redis
- Thay vì chạy 4 lệnh `docker run` → Chỉ cần 1 lệnh: `docker compose up`

### File docker-compose.yml cơ bản

```yaml
version: '3.8'

services:
  # Service 1: Web
  web:
    build: .
    ports:
      - "3000:3000"
    depends_on:
      - db
  
  # Service 2: Database
  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_PASSWORD=secret
    volumes:
      - db-data:/var/lib/postgresql/data

volumes:
  db-data:
```

### Cú pháp chi tiết

```yaml
version: '3.8'

services:
  web:
    # ═══════════════════════════════════════
    # 1. Image hoặc Build
    # ═══════════════════════════════════════
    image: nginx:alpine              # Dùng image có sẵn
    # HOẶC
    build: .                         # Build từ Dockerfile
    # HOẶC
    build:
      context: ./app
      dockerfile: Dockerfile.prod
      args:
        NODE_VERSION: 18

    # ═══════════════════════════════════════
    # 2. Ports
    # ═══════════════════════════════════════
    ports:
      - "8080:80"        # host:container
      - "8443:443"

    # ═══════════════════════════════════════
    # 3. Environment Variables
    # ═══════════════════════════════════════
    environment:
      - NODE_ENV=production
      - DB_HOST=db
      - DB_PORT=5432
    
    # Hoặc đọc từ file
    env_file:
      - .env

    # ═══════════════════════════════════════
    # 4. Volumes
    # ═══════════════════════════════════════
    volumes:
      - ./src:/app/src              # Bind mount
      - app-data:/app/data          # Named volume
      - /app/node_modules           # Anonymous volume

    # ═══════════════════════════════════════
    # 5. Networks
    # ═══════════════════════════════════════
    networks:
      - frontend
      - backend

    # ═══════════════════════════════════════
    # 6. Dependencies
    # ═══════════════════════════════════════
    depends_on:
      - db
      - redis

    # ═══════════════════════════════════════
    # 7. Restart Policy
    # ═══════════════════════════════════════
    restart: unless-stopped
    # Các giá trị: no, always, on-failure, unless-stopped

    # ═══════════════════════════════════════
    # 8. Command Override
    # ═══════════════════════════════════════
    command: npm run dev

    # ═══════════════════════════════════════
    # 9. Healthcheck
    # ═══════════════════════════════════════
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost/health"]
      interval: 30s
      timeout: 3s
      retries: 3

volumes:
  app-data:

networks:
  frontend:
  backend:
```

### Lệnh Docker Compose

```bash
# ═══════════════════════════════════════
# Start/Stop
# ═══════════════════════════════════════

# Khởi động tất cả
docker compose up
docker compose up -d              # Background

# Build trước khi start
docker compose up --build

# Start chỉ 1 service
docker compose up web

# Dừng tất cả (giữ containers)
docker compose stop

# Dừng và xóa containers
docker compose down

# Xóa cả volumes
docker compose down -v

# ═══════════════════════════════════════
# Logs
# ═══════════════════════════════════════

# Xem logs
docker compose logs

# Follow real-time
docker compose logs -f

# Logs của 1 service
docker compose logs -f web

# ═══════════════════════════════════════
# Quản lý
# ═══════════════════════════════════════

# Xem containers
docker compose ps

# Restart
docker compose restart
docker compose restart web

# Exec vào container
docker compose exec web bash

# Build
docker compose build
docker compose build --no-cache

# Pull images mới
docker compose pull

# Scale service (tạo nhiều instances)
docker compose up --scale web=3
```

### Ví dụ: Full Stack App

```yaml
version: '3.8'

services:
  # ═══════════════════════════════════════
  # Frontend - React
  # ═══════════════════════════════════════
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - app-network

  # ═══════════════════════════════════════
  # Backend - Node.js API
  # ═══════════════════════════════════════
  backend:
    build: ./backend
    ports:
      - "5000:5000"
    environment:
      - NODE_ENV=production
      - MONGO_URI=mongodb://mongo:27017/myapp
      - REDIS_HOST=redis
    depends_on:
      - mongo
      - redis
    volumes:
      - ./backend:/app
      - /app/node_modules
    networks:
      - app-network

  # ═══════════════════════════════════════
  # Database - MongoDB
  # ═══════════════════════════════════════
  mongo:
    image: mongo:7-alpine
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=password123
    volumes:
      - mongo-data:/data/db
    networks:
      - app-network
    restart: unless-stopped

  # ═══════════════════════════════════════
  # Cache - Redis
  # ═══════════════════════════════════════
  redis:
    image: redis:7-alpine
    volumes:
      - redis-data:/data
    networks:
      - app-network
    restart: unless-stopped

volumes:
  mongo-data:
  redis-data:

networks:
  app-network:
    driver: bridge
```

**Khởi động:**
```bash
docker compose up -d
```

---

## Phần 4: Volumes & Networks

### Volumes - Lưu trữ dữ liệu

**Vấn đề:** Container xóa → Dữ liệu mất!  
**Giải pháp:** Volumes lưu dữ liệu bên ngoài container

**3 loại volumes:**

```bash
# ═══════════════════════════════════════
# 1. Named Volume (Khuyến nghị) ✅
# ═══════════════════════════════════════
docker volume create my-data
docker run -v my-data:/app/data my-app

# Trong docker-compose.yml:
services:
  db:
    volumes:
      - db-data:/var/lib/postgresql/data

volumes:
  db-data:

# ═══════════════════════════════════════
# 2. Bind Mount (Mount folder từ host)
# ═══════════════════════════════════════
docker run -v /home/user/app:/app/data my-app
docker run -v $(pwd):/app my-app

# Trong docker-compose.yml:
services:
  web:
    volumes:
      - ./src:/app/src              # Development hot reload

# ═══════════════════════════════════════
# 3. Anonymous Volume
# ═══════════════════════════════════════
docker run -v /app/data my-app
# Không khuyến nghị - khó quản lý
```

**Lệnh quản lý volumes:**

```bash
# Tạo volume
docker volume create my-volume

# Xem danh sách
docker volume ls

# Xem chi tiết
docker volume inspect my-volume

# Xóa volume
docker volume rm my-volume

# Xóa volumes không dùng
docker volume prune
```

**Ví dụ: PostgreSQL persistent data**

```yaml
services:
  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_PASSWORD=secret
    volumes:
      - postgres-data:/var/lib/postgresql/data  # ✅ Dữ liệu không mất!
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro  # Init script

volumes:
  postgres-data:
```

### Networks - Mạng

**Containers cùng network có thể gọi nhau bằng tên service!**

```yaml
services:
  web:
    networks:
      - app-network
    environment:
      - DB_HOST=postgres    # ✅ Gọi bằng tên service
      - REDIS_HOST=redis

  postgres:
    networks:
      - app-network

  redis:
    networks:
      - app-network

networks:
  app-network:
```

**Code trong app:**

```javascript
// Kết nối database
const db = new Pool({
  host: 'postgres',  // ← Tên service trong docker-compose
  port: 5432
});

// Kết nối Redis
const redis = new Redis({
  host: 'redis',     // ← Tên service
  port: 6379
});
```

**Cô lập networks:**

```yaml
services:
  # Frontend - truy cập public + backend
  frontend:
    networks:
      - frontend
  
  # API - truy cập frontend + database
  api:
    networks:
      - frontend
      - backend
  
  # Database - chỉ API mới truy cập được
  database:
    networks:
      - backend       # Không có internet, không public!

networks:
  frontend:
  backend:
    internal: true    # Không có internet access
```

---

## Phần 5: Deploy Production

### Chuẩn bị VPS

**Bước 1: Chọn VPS**
- DigitalOcean, AWS, Google Cloud, Vultr
- Yêu cầu: 2GB RAM, 1 CPU, Ubuntu 22.04

**Bước 2: SSH vào server**

```bash
ssh root@your-server-ip
```

**Bước 3: Cài Docker**

```bash
# Cài Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Cài Docker Compose
sudo apt install docker-compose-plugin -y

# Add user vào docker group
sudo usermod -aG docker $USER

# Kiểm tra
docker --version
docker compose version
```

### Upload code lên server

**Cách 1: Dùng Git (Khuyến nghị)**

```bash
# Trên server
cd /home/username
git clone https://github.com/yourusername/your-app.git
cd your-app
```

**Cách 2: SCP**

```bash
# Từ máy local
scp -r ./my-app username@server-ip:/home/username/
```

### Deploy

**Tạo file .env:**

```bash
cat > .env << EOF
NODE_ENV=production
DB_PASSWORD=your-secure-password
MONGO_PASSWORD=another-secure-password
EOF
```

**docker-compose.production.yml:**

```yaml
version: '3.8'

services:
  web:
    build: .
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=production
      - DB_HOST=postgres
    restart: unless-stopped
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - /etc/letsencrypt:/etc/letsencrypt:ro
    depends_on:
      - web
    restart: unless-stopped

volumes:
  postgres-data:
```

**Khởi động:**

```bash
docker compose -f docker-compose.production.yml up -d --build
```

### Setup SSL với Let's Encrypt

```bash
# Cài Certbot
sudo apt install certbot python3-certbot-nginx -y

# Generate SSL
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Auto renewal
sudo certbot renew --dry-run
```

**nginx.conf với SSL:**

```nginx
events {
    worker_connections 1024;
}

http {
    # Redirect HTTP → HTTPS
    server {
        listen 80;
        server_name yourdomain.com;
        return 301 https://$server_name$request_uri;
    }

    # HTTPS
    server {
        listen 443 ssl http2;
        server_name yourdomain.com;

        ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

        location / {
            proxy_pass http://web:3000;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

### Monitoring

```bash
# Xem logs
docker compose logs -f

# Xem resource usage
docker stats

# Backup database
docker compose exec postgres pg_dump -U user dbname > backup.sql
```

### Auto-deploy với GitHub Actions

**.github/workflows/deploy.yml:**

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to VPS
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_IP }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /home/user/app
            git pull origin main
            docker compose down
            docker compose up -d --build
```

---

## Phần 6: Ví Dụ Thực Tế

### 1. WordPress + MySQL

```yaml
version: '3.8'

services:
  wordpress:
    image: wordpress:latest
    ports:
      - "80:80"
    environment:
      WORDPRESS_DB_HOST: db
      WORDPRESS_DB_USER: wordpress
      WORDPRESS_DB_PASSWORD: password123
      WORDPRESS_DB_NAME: wordpress
    volumes:
      - wordpress-data:/var/www/html

  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress
      MYSQL_PASSWORD: password123
      MYSQL_ROOT_PASSWORD: rootpassword
    volumes:
      - db-data:/var/lib/mysql

volumes:
  wordpress-data:
  db-data:
```

### 2. MERN Stack

```yaml
version: '3.8'

services:
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

  backend:
    build: ./backend
    ports:
      - "5000:5000"
    environment:
      - MONGO_URI=mongodb://mongo:27017/mern
    depends_on:
      - mongo

  mongo:
    image: mongo:7-alpine
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data:
```

### 3. Laravel + MySQL + Redis

```yaml
version: '3.8'

services:
  app:
    build: .
    volumes:
      - ./src:/var/www/html
    depends_on:
      - mysql
      - redis

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./src:/var/www/html
    depends_on:
      - app

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: laravel
      MYSQL_USER: laravel
      MYSQL_PASSWORD: secret
      MYSQL_ROOT_PASSWORD: root
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7-alpine

volumes:
  mysql-data:
```

---

## 📝 CHEAT SHEET - Lệnh Hay Dùng

```bash
# ═══════════════════════════════════════
# CONTAINERS
# ═══════════════════════════════════════
docker ps                              # Đang chạy
docker ps -a                           # Tất cả
docker run -d -p 8080:80 nginx         # Chạy container
docker stop <id>                       # Dừng
docker rm <id>                         # Xóa
docker logs -f <id>                    # Xem logs
docker exec -it <id> bash              # Vào terminal

# ═══════════════════════════════════════
# IMAGES
# ═══════════════════════════════════════
docker images                          # Xem images
docker pull nginx                      # Tải image
docker build -t my-app .               # Build image
docker rmi <id>                        # Xóa image

# ═══════════════════════════════════════
# DOCKER COMPOSE
# ═══════════════════════════════════════
docker compose up -d                   # Khởi động
docker compose down                    # Dừng và xóa
docker compose logs -f                 # Xem logs
docker compose ps                      # Xem containers
docker compose exec web bash           # Vào container

# ═══════════════════════════════════════
# CLEANUP
# ═══════════════════════════════════════
docker system prune -a                 # Xóa tất cả không dùng
docker volume prune                    # Xóa volumes không dùng
docker image prune -a                  # Xóa images không dùng
```

---

## 🎯 BEST PRACTICES

### Security

```dockerfile
# ✅ ĐÚNG: Non-root user
FROM node:18-alpine
RUN addgroup -S app && adduser -S app -G app
USER app

# ❌ SAI: Chạy với root
FROM node:18-alpine
# Không có USER → Mặc định root
```

### Performance

```dockerfile
# ✅ ĐÚNG: Tối ưu caching
COPY package*.json ./
RUN npm install
COPY . .

# ❌ SAI: Rebuild mọi thứ mỗi lần code thay đổi
COPY . .
RUN npm install
```

### Image Size

```dockerfile
# ✅ ĐÚNG: Alpine (~170MB)
FROM node:18-alpine

# ❌ SAI: Full (~900MB)
FROM node:18
```

---

## 🐛 TROUBLESHOOTING

**Container không start:**
```bash
docker logs <container-id>
docker inspect <container-id>
```

**Port already in use:**
```bash
sudo lsof -i :80
sudo kill -9 <PID>
```

**Cannot connect to database:**
```yaml
# ❌ Sai
environment:
  - DB_HOST=localhost

# ✅ Đúng
environment:
  - DB_HOST=postgres  # Tên service
```

**Out of disk space:**
```bash
docker system prune -a --volumes -f
```

---

## 📚 TÀI LIỆU THAM KHẢO

- Docker Docs: https://docs.docker.com
- Docker Hub: https://hub.docker.com
- Docker Compose: https://docs.docker.com/compose

---

**Happy Dockering! 🐳**

> *"Build once, run anywhere!"*

