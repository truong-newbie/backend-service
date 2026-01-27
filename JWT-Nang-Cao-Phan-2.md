# 📚 JWT NÂNG CAO - PHẦN 2

## <a id="phan-6"></a>🔑 PHẦN 6: JSON WEB KEYS (JWK)

### 6.1. JWK là gì?

**JWK** = JSON Web Key = **Format chuẩn để biểu diễn cryptographic keys dưới dạng JSON**

**Tại sao cần JWK?**

Trước đây, mỗi loại key có format riêng:
- RSA: PEM format
- EC: Khác format
- Symmetric: Raw bytes

→ Khó quản lý, khó share!

JWK giải quyết bằng cách: **Một format cho tất cả!**

### 6.2. Cấu trúc JWK

**Ví dụ JWK cho RSA key:**

```json
{
  "kty": "RSA",
  "use": "sig",
  "kid": "2024-01-key",
  "n": "0vx7agoebGcQSuuPiL...",
  "e": "AQAB"
}
```

**Ví dụ JWK cho Elliptic Curve key:**

```json
{
  "kty": "EC",
  "crv": "P-256",
  "x": "MKBCTNIcKUSDii11yS...",
  "y": "4Etl6SRW2YiLUrN5vf...",
  "d": "870MB6gfuTJ4HtUnUv...",
  "use": "enc",
  "kid": "2024-ec-key"
}
```

**Ví dụ JWK cho Symmetric key:**

```json
{
  "kty": "oct",
  "k": "GawgguFyGrWKav7AX4VKUg",
  "kid": "hmac-key-1",
  "use": "sig"
}
```

### 6.3. Các parameters quan trọng

#### Common Parameters (Dùng chung)

**1. `kty` (Key Type) - BẮT BUỘC**
- Loại key
- Giá trị: `RSA`, `EC`, `oct`, `OKP`

**2. `use` (Public Key Use) - TÙY CHỌN**
- Mục đích sử dụng
- Giá trị:
  - `sig`: Signature/Verification
  - `enc`: Encryption/Decryption

**3. `key_ops` (Key Operations) - TÙY CHỌN**
- Các operations cụ thể
- Giá trị: `sign`, `verify`, `encrypt`, `decrypt`, `wrapKey`, `unwrapKey`, `deriveKey`, `deriveBits`

**4. `alg` (Algorithm) - TÙY CHỌN**
- Thuật toán dự định dùng
- Ví dụ: `RS256`, `ES256`, `HS256`

**5. `kid` (Key ID) - TÙY CHỌN**
- ID duy nhất của key
- Dùng để match key với JWT header

**6. `x5u` (X.509 URL) - TÙY CHỌN**
- URL đến X.509 certificate chain

**7. `x5c` (X.509 Certificate Chain) - TÙY CHỌN**
- Chuỗi X.509 certificates

**8. `x5t` (X.509 Certificate SHA-1 Thumbprint)**

**9. `x5t#S256` (X.509 Certificate SHA-256 Thumbprint)**

#### RSA-specific Parameters

**1. `n` (Modulus) - BẮT BUỘC cho public key**
- Modulus của RSA key (Base64-URL encoded)

**2. `e` (Exponent) - BẮT BUỘC cho public key**
- Public exponent (thường là "AQAB" = 65537)

**3. `d` (Private Exponent) - BẮT BUỘC cho private key**
- Private exponent (chỉ có trong private key!)

**4. `p`, `q`, `dp`, `dq`, `qi` - TÙY CHỌN**
- Các parameters để tối ưu hóa tính toán

#### EC-specific Parameters

**1. `crv` (Curve) - BẮT BUỘC**
- Tên đường cong
- Giá trị: `P-256`, `P-384`, `P-521`

**2. `x` (X Coordinate) - BẮT BUỘC**
- Tọa độ x của điểm trên đường cong

**3. `y` (Y Coordinate) - BẮT BUỘC cho public key**
- Tọa độ y của điểm trên đường cong

**4. `d` (ECC Private Key) - BẮT BUỘC cho private key**
- Private key value

#### Symmetric Key Parameters

**1. `k` (Key Value) - BẮT BUỘC**
- Symmetric key value (Base64-URL encoded)

### 6.4. JWK Set (JWKS)

**JWKS** = Tập hợp nhiều JWKs

**Format:**

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "rsa-key-1",
      "use": "sig",
      "n": "...",
      "e": "AQAB"
    },
    {
      "kty": "EC",
      "kid": "ec-key-1",
      "use": "enc",
      "crv": "P-256",
      "x": "...",
      "y": "..."
    }
  ]
}
```

**Khi nào dùng JWKS?**

- Key rotation (xoay key định kỳ)
- Nhiều keys cho mục đích khác nhau
- Public JWKS endpoint (/.well-known/jwks.json)

**Ví dụ thực tế: Auth0 JWKS endpoint**

```
https://YOUR_DOMAIN/.well-known/jwks.json
```

Trả về:

```json
{
  "keys": [
    {
      "kty": "RSA",
      "use": "sig",
      "n": "xGOr-H7A...",
      "e": "AQAB",
      "kid": "NjVBRjY5MDlCMUIwNzU4RTA2QzZFMDQ4QzQ2MDAyQjVDNjk1RTM2Qg",
      "x5t": "NjVBRjY5MDlCMUIwNzU4RTA2QzZFMDQ4QzQ2MDAyQjVDNjk1RTM2Qg",
      "alg": "RS256"
    }
  ]
}
```

### 6.5. Code Examples

#### Tạo JWK từ PEM key

```javascript
const jose = require('node-jose');
const fs = require('fs');

// Đọc PEM key
const pemKey = fs.readFileSync('public.pem', 'utf8');

// Convert sang JWK
jose.JWK.asKey(pemKey, 'pem').then(key => {
  const jwk = key.toJSON();
  console.log(JSON.stringify(jwk, null, 2));
});
```

#### Tạo JWK mới

```javascript
const jose = require('node-jose');

// Tạo keystore
const keystore = jose.JWK.createKeyStore();

// Generate RSA key
keystore.generate('RSA', 2048, {
  kid: 'rsa-2024-01',
  use: 'sig',
  alg: 'RS256'
}).then(key => {
  // Export public key
  const publicJWK = key.toJSON();
  console.log('Public JWK:', publicJWK);
  
  // Export private key (bao gồm cả d parameter)
  const privateJWK = key.toJSON(true);
  console.log('Private JWK:', privateJWK);
});
```

#### Verify JWT bằng JWK từ JWKS endpoint

```javascript
const jose = require('node-jose');
const axios = require('axios');
const jwt = require('jsonwebtoken');

async function verifyJWT(token, jwksUri) {
  // 1. Decode JWT header (không verify)
  const decoded = jwt.decode(token, { complete: true });
  const kid = decoded.header.kid;
  
  // 2. Fetch JWKS
  const response = await axios.get(jwksUri);
  const jwks = response.data;
  
  // 3. Tìm key matching kid
  const jwk = jwks.keys.find(k => k.kid === kid);
  if (!jwk) {
    throw new Error('Key not found');
  }
  
  // 4. Convert JWK to PEM
  const key = await jose.JWK.asKey(jwk);
  const pem = key.toPEM();
  
  // 5. Verify JWT
  const verified = jwt.verify(token, pem, {
    algorithms: [jwk.alg]
  });
  
  return verified;
}

// Sử dụng
verifyJWT(
  'eyJhbGc...',
  'https://YOUR_DOMAIN/.well-known/jwks.json'
).then(payload => {
  console.log('Verified payload:', payload);
});
```

### 6.6. Best Practices với JWK

**1. Luôn set `kid`**
- Dễ dàng key rotation
- Dễ debug

**2. Separate public/private keys**
- Chỉ public trong JWKS endpoint
- Private key giữ bí mật

**3. Regular key rotation**
- Rotate keys định kỳ (mỗi 3-6 tháng)
- Giữ old keys trong JWKS một thời gian

**4. Use `use` và `alg` parameters**
- Rõ ràng mục đích key
- Prevent algorithm confusion attacks

---

## <a id="phan-7"></a>🧮 PHẦN 7: CÁC THUẬT TOÁN (ALGORITHMS)

### 7.1. Base64-URL Encoding

**Base64** = Binary-to-Text encoding

**Tại sao cần?**
- JWT cần truyền qua HTTP, URL, JSON
- Chỉ chấp nhận text, không chấp nhận binary
- → Encode binary thành text!

**Base64 thông thường:**

```
ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/
```

**Base64-URL (JWT dùng):**

```
ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_
```

**Khác biệt:**
- `+` → `-`
- `/` → `_`
- Bỏ padding `=`

**Tại sao?** Để **URL-safe**!

**Code mẫu:**

```javascript
function base64UrlEncode(str) {
  return Buffer.from(str)
    .toString('base64')
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}

function base64UrlDecode(str) {
  // Thêm padding
  str += '='.repeat((4 - str.length % 4) % 4);
  
  return Buffer.from(
    str.replace(/-/g, '+').replace(/_/g, '/'),
    'base64'
  ).toString('utf8');
}
```

### 7.2. SHA (Secure Hash Algorithm)

**SHA** là họ các **hash functions** - hàm băm mật mã.

**Đặc điểm:**
- Input: Bất kỳ độ dài
- Output: Độ dài cố định
- **One-way**: Không thể reverse (hash → input)
- **Collision-resistant**: Khó tìm 2 inputs cho cùng hash

**Các biến thể:**

| Algorithm | Output Size | Status |
|-----------|-------------|--------|
| SHA-1 | 160 bits | ⛔ Deprecated |
| SHA-256 | 256 bits | ✅ Recommended |
| SHA-384 | 384 bits | ✅ Good |
| SHA-512 | 512 bits | ✅ Good |

**Code mẫu SHA-256:**

```javascript
const crypto = require('crypto');

function sha256(data) {
  return crypto
    .createHash('sha256')
    .update(data)
    .digest();
}

// Ví dụ
const hash = sha256('Hello World');
console.log(hash.toString('hex'));
// Output: a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e
```

**Tính chất quan trọng:**

```javascript
sha256('Hello World')  // → a591a6d4...
sha256('Hello World')  // → a591a6d4... (giống!)

sha256('Hello World')  // → a591a6d4...
sha256('Hello world')  // → 64ec88ca... (khác hoàn toàn!)
```

### 7.3. HMAC (Hash-based Message Authentication Code)

**HMAC** = Hash + Secret Key

**Công thức đơn giản:**

```
HMAC(message, secret) = Hash(
  (secret ⊕ opad) + Hash((secret ⊕ ipad) + message)
)
```

Trong đó:
- `ipad` = 0x36 lặp lại
- `opad` = 0x5C lặp lại
- `⊕` = XOR operation

**Code mẫu:**

```javascript
const crypto = require('crypto');

function hmacSHA256(message, secret) {
  return crypto
    .createHmac('sha256', secret)
    .update(message)
    .digest();
}

// Sử dụng cho JWT HS256
function signHS256(header, payload, secret) {
  const data = base64UrlEncode(header) + '.' + base64UrlEncode(payload);
  const signature = hmacSHA256(data, secret);
  return base64UrlEncode(signature);
}
```

**Tại sao cần HMAC thay vì chỉ Hash?**

```javascript
// ❌ Chỉ dùng Hash
signature = SHA256(message)
// → Bất kỳ ai cũng tính được!

// ✅ Dùng HMAC
signature = HMAC(message, secret)
// → Chỉ người có secret mới tính được!
```

### 7.4. RSA (Rivest-Shamir-Adleman)

**RSA** dựa trên **bài toán phân tích thừa số nguyên tố**.

#### Cách hoạt động cơ bản

**Bước 1: Key Generation**

```
1. Chọn 2 số nguyên tố lớn: p, q
2. Tính n = p × q
3. Tính φ(n) = (p-1)(q-1)
4. Chọn e sao cho: gcd(e, φ(n)) = 1
5. Tính d sao cho: e × d ≡ 1 (mod φ(n))

Public Key: (n, e)
Private Key: (n, d)
```

**Bước 2: Signing**

```
1. Hash message: h = SHA256(message)
2. Sign: s = h^d mod n
```

**Bước 3: Verification**

```
1. Compute: h' = s^e mod n
2. Hash message: h = SHA256(message)
3. Check: h' == h
```

**Tại sao an toàn?**

Vì **rất khó** tìm d khi chỉ biết e và n (phải phân tích n = p × q)

**Ví dụ với số nhỏ (minh họa):**

```
p = 61, q = 53
n = 61 × 53 = 3233
φ(n) = 60 × 52 = 3120
e = 17 (chọn)
d = 2753 (tính được)

Message = 123
Signature = 123^2753 mod 3233 = 855
Verify = 855^17 mod 3233 = 123 ✓
```

**Key size recommendations:**

| Year | Minimum Key Size |
|------|------------------|
| 2015-2030 | 2048 bits |
| 2030+ | 3072 bits |

**Code mẫu với crypto:**

```javascript
const crypto = require('crypto');

// Generate RSA key pair
const { publicKey, privateKey } = crypto.generateKeyPairSync('rsa', {
  modulusLength: 2048,
  publicKeyEncoding: {
    type: 'spki',
    format: 'pem'
  },
  privateKeyEncoding: {
    type: 'pkcs8',
    format: 'pem'
  }
});

// Sign
function signRS256(message, privateKey) {
  const sign = crypto.createSign('RSA-SHA256');
  sign.update(message);
  return sign.sign(privateKey);
}

// Verify
function verifyRS256(message, signature, publicKey) {
  const verify = crypto.createVerify('RSA-SHA256');
  verify.update(message);
  return verify.verify(publicKey, signature);
}
```

### 7.5. ECDSA (Elliptic Curve Digital Signature Algorithm)

**ECDSA** dựa trên **đường cong elliptic**.

**Ưu điểm so với RSA:**

| | ECDSA | RSA |
|---|---|---|
| Key Size | 256 bits | 3072 bits |
| Signing Speed | Nhanh hơn | Chậm hơn |
| Verify Speed | Chậm hơn | Nhanh hơn |
| Overall | Nhỏ gọn, hiện đại | Tương thích tốt |

**Equivalent Security:**

| ECDSA | RSA |
|-------|-----|
| 256 bits | 3072 bits |
| 384 bits | 7680 bits |
| 521 bits | 15360 bits |

**Các curves phổ biến:**

- **P-256** (secp256r1): ES256
- **P-384** (secp384r1): ES384
- **P-521** (secp521r1): ES512

**Code mẫu:**

```javascript
const crypto = require('crypto');

// Generate EC key pair
const { publicKey, privateKey } = crypto.generateKeyPairSync('ec', {
  namedCurve: 'prime256v1', // P-256
  publicKeyEncoding: {
    type: 'spki',
    format: 'pem'
  },
  privateKeyEncoding: {
    type: 'pkcs8',
    format: 'pem'
  }
});

// Sign
function signES256(message, privateKey) {
  const sign = crypto.createSign('SHA256');
  sign.update(message);
  return sign.sign({
    key: privateKey,
    dsaEncoding: 'ieee-p1363'
  });
}

// Verify
function verifyES256(message, signature, publicKey) {
  const verify = crypto.createVerify('SHA256');
  verify.update(message);
  return verify.verify({
    key: publicKey,
    dsaEncoding: 'ieee-p1363'
  }, signature);
}
```

---

## <a id="phan-8"></a>🛡️ PHẦN 8: BEST PRACTICES & BẢO MẬT

### 8.1. Common Attacks (Các tấn công phổ biến)

#### 8.1.1. "alg: none" Attack

**Mô tả:**
Attacker đổi `alg` header thành `none` và bỏ signature!

**Ví dụ:**

Token gốc:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJ1c2VyIjoiam9obiIsInJvbGUiOiJ1c2VyIn0.
signature_here
```

Token tấn công:
```
eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.
eyJ1c2VyIjoiam9obiIsInJvbGUiOiJhZG1pbiJ9.
```
(không có signature!)

**Cách phòng chống:**

```javascript
// ❌ NGUY HIỂM
jwt.verify(token, secret);

// ✅ AN TOÀN
jwt.verify(token, secret, {
  algorithms: ['HS256']  // Luôn chỉ định!
});

// ✅ Hoặc reject "none"
if (decoded.header.alg === 'none') {
  throw new Error('Unsecured JWT not allowed');
}
```

#### 8.1.2. RS256 Public Key as HS256 Secret

**Mô tả:**
Attacker dùng **public key** làm **HMAC secret**!

**Kịch bản:**

```javascript
// Server code
function verify(token, key) {
  // Key có thể là public key HOẶC secret
  return jwt.verify(token, key); // ← Lỗ hổng!
}

// Attacker's plan:
// 1. Lấy public key (công khai)
// 2. Tạo JWT mới với alg: HS256
// 3. Ký bằng public key làm HMAC secret
// 4. Server dùng public key verify → PASS!
```

**Cách phòng chống:**

```javascript
// ✅ Separate functions
function verifyHS256(token, secret) {
  return jwt.verify(token, secret, {
    algorithms: ['HS256']
  });
}

function verifyRS256(token, publicKey) {
  return jwt.verify(token, publicKey, {
    algorithms: ['RS256']
  });
}

// ✅ Hoặc check algorithm
const decoded = jwt.decode(token, { complete: true });
if (decoded.header.alg !== expectedAlgorithm) {
  throw new Error('Unexpected algorithm');
}
```

#### 8.1.3. Weak HMAC Keys

**Vấn đề:**
Secret quá ngắn → Dễ brute-force!

**Minimum key lengths:**

| Algorithm | Minimum |
|-----------|---------|
| HS256 | 256 bits = 32 bytes |
| HS384 | 384 bits = 48 bytes |
| HS512 | 512 bits = 64 bytes |

**Examples:**

```javascript
// ❌ NGUY HIỂM
const secret = 'password';        // 8 bytes
const secret = 'mysecret123';     // 11 bytes

// ✅ AN TOÀN
const secret = crypto.randomBytes(32).toString('hex');  // 64 hex chars = 32 bytes

// ✅ Hoặc dùng môi trường
const secret = process.env.JWT_SECRET;  // Đảm bảo đủ dài!
```

#### 8.1.4. Token Substitution

**Mô tả:**
Dùng token của service A cho service B!

**Ví dụ:**

```json
{
  "sub": "user123",
  "role": "admin",
  "aud": "service-a"  // Cho service A
}
```

Attacker gửi token này đến **service B** → Nếu B không check `aud` → PASS!

**Cách phòng chống:**

```javascript
// ✅ Luôn validate claims
jwt.verify(token, secret, {
  algorithms: ['HS256'],
  audience: 'my-service',         // Check aud
  issuer: 'https://my-auth.com',  // Check iss
  clockTolerance: 60              // Cho phép lệch 60s
});
```

#### 8.1.5. CSRF với JWT trong Cookie

**Vấn đề:**
Cookie tự động gửi kèm request → CSRF!

**Cách phòng chống:**

```javascript
// ✅ Option 1: SameSite cookie
res.cookie('token', jwt, {
  httpOnly: true,
  secure: true,
  sameSite: 'strict'  // Chỉ gửi cho same-site requests
});

// ✅ Option 2: Double Submit Cookie
// Cookie: JWT
// Header: X-CSRF-Token = hash(JWT)

// ✅ Option 3: Không dùng cookie
// Lưu JWT trong localStorage
// Gửi qua Authorization header
```

#### 8.1.6. XSS và JWT Theft

**Vấn đề:**
XSS → Đọc được localStorage → Đánh cắp JWT!

**Cách phòng chống:**

```javascript
// ✅ Option 1: HttpOnly Cookie
res.cookie('token', jwt, {
  httpOnly: true,  // JS không đọc được
  secure: true,
  sameSite: 'strict'
});

// ✅ Option 2: Content Security Policy
app.use((req, res, next) => {
  res.setHeader(
    'Content-Security-Policy',
    "default-src 'self'; script-src 'self'"
  );
  next();
});

// ✅ Option 3: Sanitize inputs
const sanitizeHtml = require('sanitize-html');
const clean = sanitizeHtml(dirty);
```

### 8.2. Best Practices Checklist

#### 8.2.1. Always Validate Everything

```javascript
function validateJWT(token, options) {
  const decoded = jwt.verify(token, getSecret(), {
    // ✅ Algorithm
    algorithms: ['HS256', 'RS256'],
    
    // ✅ Audience
    audience: options.audience,
    
    // ✅ Issuer
    issuer: options.issuer,
    
    // ✅ Clock tolerance
    clockTolerance: 60,
    
    // ✅ Max age
    maxAge: '1h'
  });
  
  // ✅ Additional checks
  if (!decoded.sub) {
    throw new Error('Missing subject');
  }
  
  if (!decoded.exp) {
    throw new Error('Missing expiration');
  }
  
  // ✅ Custom claims
  if (options.requiredRole && decoded.role !== options.requiredRole) {
    throw new Error('Insufficient permissions');
  }
  
  return decoded;
}
```

#### 8.2.2. Use Strong Keys

```javascript
// ❌ NGUY HIỂM
const secret = 'secret';

// ✅ AN TOÀN
const secret = crypto.randomBytes(32).toString('base64');

// ✅ Store securely
// .env file
JWT_SECRET=your-randomly-generated-secret-here

// Environment variable
process.env.JWT_SECRET
```

#### 8.2.3. Short Expiration Times

```javascript
// ✅ Access token: Short-lived
const accessToken = jwt.sign(payload, secret, {
  expiresIn: '15m'  // 15 phút
});

// ✅ Refresh token: Long-lived
const refreshToken = jwt.sign(payload, refreshSecret, {
  expiresIn: '7d'   // 7 ngày
});

// ✅ Remember: Store refresh token in DB!
await db.saveRefreshToken(userId, refreshToken);
```

#### 8.2.4. Proper Claims

```javascript
// ✅ Minimal but complete
const payload = {
  // Standard claims
  iss: 'https://your-domain.com',
  sub: user.id,
  aud: 'https://your-api.com',
  exp: Math.floor(Date.now() / 1000) + (60 * 15),
  iat: Math.floor(Date.now() / 1000),
  jti: uuid.v4(),
  
  // Custom claims
  email: user.email,
  role: user.role,
  permissions: user.permissions
};

// ❌ Không nên:
// - Password
// - Credit card numbers
// - SSN
// - Quá nhiều dữ liệu (JWT sẽ lớn)
```

#### 8.2.5. HTTPS Only

```javascript
// ✅ Force HTTPS
app.use((req, res, next) => {
  if (!req.secure && process.env.NODE_ENV === 'production') {
    return res.redirect('https://' + req.headers.host + req.url);
  }
  next();
});

// ✅ Secure cookies
res.cookie('token', jwt, {
  secure: true,  // Chỉ gửi qua HTTPS
  httpOnly: true,
  sameSite: 'strict'
});
```

#### 8.2.6. Implement Token Blacklist

```javascript
// Blacklist for logout/revocation
class TokenBlacklist {
  constructor() {
    this.blacklist = new Set();
  }
  
  async add(jti, exp) {
    this.blacklist.add(jti);
    
    // Auto-remove sau khi exp
    const ttl = exp - Math.floor(Date.now() / 1000);
    setTimeout(() => {
      this.blacklist.delete(jti);
    }, ttl * 1000);
    
    // Hoặc lưu vào Redis với TTL
    await redis.setex(`blacklist:${jti}`, ttl, '1');
  }
  
  async isBlacklisted(jti) {
    return this.blacklist.has(jti);
    // Hoặc check Redis
    // return await redis.exists(`blacklist:${jti}`);
  }
}

// Usage
app.post('/logout', async (req, res) => {
  const token = req.cookies.token;
  const decoded = jwt.verify(token, secret);
  
  await tokenBlacklist.add(decoded.jti, decoded.exp);
  
  res.clearCookie('token');
  res.json({ message: 'Logged out' });
});

// Middleware
app.use(async (req, res, next) => {
  const token = req.cookies.token;
  const decoded = jwt.verify(token, secret);
  
  if (await tokenBlacklist.isBlacklisted(decoded.jti)) {
    return res.status(401).json({ error: 'Token revoked' });
  }
  
  next();
});
```

#### 8.2.7. Regular Key Rotation

```javascript
// Key rotation strategy
const keys = {
  current: {
    kid: 'key-2024-01',
    secret: process.env.JWT_SECRET_CURRENT
  },
  previous: {
    kid: 'key-2023-12',
    secret: process.env.JWT_SECRET_PREVIOUS
  }
};

// Sign with current key
function sign(payload) {
  return jwt.sign(
    { ...payload, kid: keys.current.kid },
    keys.current.secret
  );
}

// Verify with appropriate key
function verify(token) {
  const decoded = jwt.decode(token, { complete: true });
  const kid = decoded.header.kid;
  
  const key = kid === keys.current.kid
    ? keys.current.secret
    : keys.previous.secret;
  
  return jwt.verify(token, key);
}
```

### 8.3. Security Checklist

Cuối cùng, thầy tổng hợp checklist bảo mật cho em:

**Development:**
- [ ] Dùng thư viện JWT uy tín và cập nhật
- [ ] Luôn chỉ định `algorithms` khi verify
- [ ] Validate tất cả standard claims (iss, aud, exp...)
- [ ] Không lưu sensitive data trong JWT
- [ ] Sử dụng strong random secrets
- [ ] Implement proper error handling

**Deployment:**
- [ ] Chỉ dùng HTTPS
- [ ] Set secure cookie flags (httpOnly, secure, sameSite)
- [ ] Implement rate limiting
- [ ] Monitor và log token usage
- [ ] Regular security audits

**Operational:**
- [ ] Key rotation schedule
- [ ] Incident response plan
- [ ] Token revocation mechanism
- [ ] Regular backups
- [ ] Security training for team

---

## 🎓 KẾT LUẬN

Chúc mừng em! Em đã hoàn thành khóa học JWT từ cơ bản đến nâng cao! 

**Những điểm quan trọng cần nhớ:**

1. **JWT là gì**: JSON Web Token - format chuẩn để truyền thông tin an toàn
2. **Cấu trúc**: Header + Payload + Signature
3. **Security**: Sign ≠ Encrypt, luôn validate mọi thứ
4. **Best practices**: Strong keys, short expiration, HTTPS only

**Kỹ năng em đã có:**
✅ Hiểu cách JWT hoạt động  
✅ Tạo và verify JWT  
✅ Nhận biết các lỗ hổng bảo mật  
✅ Implement best practices  
✅ Debug JWT issues  

**Next steps:**
- Thực hành với project thực tế
- Tìm hiểu OAuth2 và OpenID Connect sâu hơn
- Học về microservices authentication
- Contribute to open-source JWT libraries

Chúc em áp dụng thành công JWT vào dự án của mình! 🚀

---

**📚 Tài liệu tham khảo:**
- RFC 7519: JWT Specification
- RFC 7515: JWS Specification  
- RFC 7516: JWE Specification
- JWT.io - Interactive JWT debugger
- Auth0 Blog - JWT best practices

**Có thắc mắc? Hỏi thầy bất cứ lúc nào! 😊**
