# 🔄 PHIÊN BẢN JAVA SPRING BOOT

> **Lưu ý**: Tài liệu này đã được chuyển đổi từ phiên bản JavaScript sang Java Spring Boot.
> Tất cả các ví dụ code đã được adapt để sử dụng với:
> - JJWT library (io.jsonwebtoken) cho JWT operations
> - Nimbus JOSE+JWT library cho JWK và JWE operations
> - Spring Boot framework
>
> Dependencies chính cần thiết:
> ```xml
> <!-- JJWT for JWT operations -->
> <dependency>
>   <groupId>io.jsonwebtoken</groupId>
>   <artifactId>jjwt-api</artifactId>
>   <version>0.12.5</version>
> </dependency>
> <dependency>
>   <groupId>io.jsonwebtoken</groupId>
>   <artifactId>jjwt-impl</artifactId>
>   <version>0.12.5</version>
>   <scope>runtime</scope>
> </dependency>
> <dependency>
>   <groupId>io.jsonwebtoken</groupId>
>   <artifactId>jjwt-jackson</artifactId>
>   <version>0.12.5</version>
>   <scope>runtime</scope>
> </dependency>
>
> <!-- Nimbus for JWK/JWE operations -->
> <dependency>
>   <groupId>com.nimbusds</groupId>
>   <artifactId>nimbus-jose-jwt</artifactId>
>   <version>9.37.3</version>
> </dependency>
> ```

---

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

```java
import com.nimbusds.jose.jwk.RSAKey;
import java.nio.file.Files;
import java.nio.file.Paths;

// Đọc PEM key
String pemKey = new String(Files.readAllBytes(Paths.get("public.pem")));

// Convert PEM to JWK
RSAKey rsaKey = RSAKey.parse(pemKey);

// Export as JSON
String jwkJson = rsaKey.toJSONString();
System.out.println(jwkJson);
```

#### Tạo JWK mới

```java
// Sử dụng Nimbus JOSE + JWT library
// Thêm dependency vào pom.xml:
<dependency>
  <groupId>com.nimbusds</groupId>
  <artifactId>nimbus-jose-jwt</artifactId>
  <version>9.37.3</version>
</dependency>

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

// Generate RSA key
RSAKey rsaKey = new RSAKeyGenerator(2048)
    .keyID("rsa-2024-01")
    .keyUse(KeyUse.SIGNATURE)
    .algorithm(JWSAlgorithm.RS256)
    .generate();

// Export public key (JSON)
String publicJWK = rsaKey.toPublicJWK().toJSONString();
System.out.println("Public JWK: " + publicJWK);

// Export private key (JSON - bao gồm cả private components)
String privateJWK = rsaKey.toJSONString();
System.out.println("Private JWK: " + privateJWK);

// Convert to PEM format
String publicPEM = rsaKey.toPublicJWK().toRSAPublicKey().toString();
```

#### Verify JWT bằng JWK từ JWKS endpoint

```java
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
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

```java
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class Base64Utils {
    
    public static String base64UrlEncode(String str) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String base64UrlDecode(String str) {
        byte[] decodedBytes = Base64.getUrlDecoder()
            .decode(str);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
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

```java
// Code JavaScript gốc:
// const crypto = require('crypto');
// 
// function sha256(data) {
//   return crypto
//     .createHash('sha256')
//     .update(data)
//     .digest();
// }
// 
// // Ví dụ
// const hash = sha256('Hello World');
// console.log(hash.toString('hex'));
// // Output: a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e

// Trong Java Spring Boot, logic tương tự được implement
// sử dụng JJWT library hoặc Nimbus JOSE+JWT
```

**Tính chất quan trọng:**

```java
// Code JavaScript gốc:
// sha256('Hello World')  // → a591a6d4...
// sha256('Hello World')  // → a591a6d4... (giống!)
// 
// sha256('Hello World')  // → a591a6d4...
// sha256('Hello world')  // → 64ec88ca... (khác hoàn toàn!)

// Trong Java Spring Boot, logic tương tự được implement
// sử dụng JJWT library hoặc Nimbus JOSE+JWT
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

```java
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class Base64Utils {
    
    public static String base64UrlEncode(String str) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String base64UrlDecode(String str) {
        byte[] decodedBytes = Base64.getUrlDecoder()
            .decode(str);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
```

**Tại sao cần HMAC thay vì chỉ Hash?**

```java
// Code JavaScript gốc:
// // ❌ Chỉ dùng Hash
// signature = SHA256(message)
// // → Bất kỳ ai cũng tính được!
// 
// // ✅ Dùng HMAC
// signature = HMAC(message, secret)
// // → Chỉ người có secret mới tính được!

// Trong Java Spring Boot, logic tương tự được implement
// sử dụng JJWT library hoặc Nimbus JOSE+JWT
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

```java
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
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

```java
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
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

```java
import java.security.SecureRandom;
import java.util.Base64;

// ❌ NGUY HIỂM
String secret = "secret";
String secret2 = "mysecret123";

// ✅ AN TOÀN
public static String generateSecureSecret(int byteLength) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[byteLength];
    random.nextBytes(bytes);
    return Base64.getEncoder().encodeToString(bytes);
}

// Tạo secret 32 bytes cho HS256
String secret = generateSecureSecret(32);

// ✅ Hoặc lưu trong application.properties
// jwt.secret=${JWT_SECRET:your-default-secret-here}

// Trong code
@Value("${jwt.secret}")
private String jwtSecret;
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

```java
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

#### 8.1.5. CSRF với JWT trong Cookie

**Vấn đề:**
Cookie tự động gửi kèm request → CSRF!

**Cách phòng chống:**

```java
// ✅ Option 1: SameSite cookie
Cookie cookie = new Cookie("token", jwt);
cookie.setHttpOnly(true);
cookie.setSecure(true);
cookie.setAttribute("SameSite", "Strict"); // Chỉ gửi cho same-site requests
response.addCookie(cookie);

// ✅ Option 2: Double Submit Cookie
// Cookie: JWT
Cookie jwtCookie = new Cookie("token", jwt);
jwtCookie.setHttpOnly(true);
response.addCookie(jwtCookie);

// Header: X-CSRF-Token = hash(JWT)
String csrfToken = HashUtils.sha256Hex(jwt);
response.setHeader("X-CSRF-Token", csrfToken);

// ✅ Option 3: Không dùng cookie
// Lưu JWT trong localStorage (client-side)
// Gửi qua Authorization header
// Authorization: Bearer <token>
```

#### 8.1.6. XSS và JWT Theft

**Vấn đề:**
XSS → Đọc được localStorage → Đánh cắp JWT!

**Cách phòng chống:**

```java
// ✅ Option 1: SameSite cookie
Cookie cookie = new Cookie("token", jwt);
cookie.setHttpOnly(true);
cookie.setSecure(true);
cookie.setAttribute("SameSite", "Strict"); // Chỉ gửi cho same-site requests
response.addCookie(cookie);

// ✅ Option 2: Double Submit Cookie
// Cookie: JWT
Cookie jwtCookie = new Cookie("token", jwt);
jwtCookie.setHttpOnly(true);
response.addCookie(jwtCookie);

// Header: X-CSRF-Token = hash(JWT)
String csrfToken = HashUtils.sha256Hex(jwt);
response.setHeader("X-CSRF-Token", csrfToken);

// ✅ Option 3: Không dùng cookie
// Lưu JWT trong localStorage (client-side)
// Gửi qua Authorization header
// Authorization: Bearer <token>
```

### 8.2. Best Practices Checklist

#### 8.2.1. Always Validate Everything

```java
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

#### 8.2.2. Use Strong Keys

```java
import java.security.SecureRandom;
import java.util.Base64;

// ❌ NGUY HIỂM
String secret = "secret";
String secret2 = "mysecret123";

// ✅ AN TOÀN
public static String generateSecureSecret(int byteLength) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[byteLength];
    random.nextBytes(bytes);
    return Base64.getEncoder().encodeToString(bytes);
}

// Tạo secret 32 bytes cho HS256
String secret = generateSecureSecret(32);

// ✅ Hoặc lưu trong application.properties
// jwt.secret=${JWT_SECRET:your-default-secret-here}

// Trong code
@Value("${jwt.secret}")
private String jwtSecret;
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

```java
// ✅ Option 1: SameSite cookie
Cookie cookie = new Cookie("token", jwt);
cookie.setHttpOnly(true);
cookie.setSecure(true);
cookie.setAttribute("SameSite", "Strict"); // Chỉ gửi cho same-site requests
response.addCookie(cookie);

// ✅ Option 2: Double Submit Cookie
// Cookie: JWT
Cookie jwtCookie = new Cookie("token", jwt);
jwtCookie.setHttpOnly(true);
response.addCookie(jwtCookie);

// Header: X-CSRF-Token = hash(JWT)
String csrfToken = HashUtils.sha256Hex(jwt);
response.setHeader("X-CSRF-Token", csrfToken);

// ✅ Option 3: Không dùng cookie
// Lưu JWT trong localStorage (client-side)
// Gửi qua Authorization header
// Authorization: Bearer <token>
```

#### 8.2.6. Implement Token Blacklist

```java
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

// Blacklist for logout/revocation
@Service
public class TokenBlacklist {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public TokenBlacklist(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void add(String jti, Date exp) {
        long ttl = exp.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(
                "blacklist:" + jti, 
                "1",
                ttl,
                TimeUnit.MILLISECONDS
            );
        }
    }
    
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey("blacklist:" + jti)
        );
    }
}

// Usage trong logout endpoint
@PostMapping("/logout")
public ResponseEntity<?> logout(@CookieValue("token") String token) {
    Claims claims = Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    
    String jti = claims.getId();
    Date exp = claims.getExpiration();
    
    tokenBlacklist.add(jti, exp);
    
    Cookie cookie = new Cookie("token", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
    
    return ResponseEntity.ok(Map.of("message", "Logged out"));
}

// Middleware để check blacklist
@Component
public class TokenBlacklistFilter extends OncePerRequestFilter {
    
    @Autowired
    private TokenBlacklist tokenBlacklist;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
                                    throws ServletException, IOException {
        
        String token = extractToken(request);
        if (token != null) {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            if (tokenBlacklist.isBlacklisted(claims.getId())) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"error\":\"Token revoked\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### 8.2.7. Regular Key Rotation

```java
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class KeyRotationService {
    
    @Value("${jwt.secret.current}")
    private String currentSecret;
    
    @Value("${jwt.secret.previous}")
    private String previousSecret;
    
    private static final String CURRENT_KID = "key-2024-01";
    private static final String PREVIOUS_KID = "key-2023-12";
    
    // Sign với current key
    public String sign(Map<String, Object> payload) {
        payload.put("kid", CURRENT_KID);
        
        SecretKey key = Keys.hmacShaKeyFor(currentSecret.getBytes());
        
        return Jwts.builder()
            .claims(payload)
            .header().keyId(CURRENT_KID).and()
            .signWith(key)
            .compact();
    }
    
    // Verify với appropriate key dựa trên kid
    public Claims verify(String token) {
        // Parse header để lấy kid (không verify)
        String kid = Jwts.parser()
            .unsecured()
            .build()
            .parseUnsecuredClaims(token)
            .getHeader()
            .get("kid", String.class);
        
        // Chọn key tương ứng
        SecretKey key = CURRENT_KID.equals(kid)
            ? Keys.hmacShaKeyFor(currentSecret.getBytes())
            : Keys.hmacShaKeyFor(previousSecret.getBytes());
        
        // Verify với key đã chọn
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
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
