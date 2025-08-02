# Device CMS Backend

Spring Boot backend cho hệ thống quản lý license thiết bị.

## Tính năng

- **Quản lý Customer**: Tạo, cập nhật, xóa khách hàng với key pair tự động
- **Quản lý License**: Tạo, kích hoạt, vô hiệu hóa license cho khách hàng
- **Quản lý Device**: Thêm, xóa thiết bị cho license
- **API RESTful**: Đầy đủ các endpoint CRUD
- **Swagger UI**: Tài liệu API tự động
- **Scheduled Tasks**: Tự động cập nhật license hết hạn

## Cấu trúc Project

```
backend/
├── src/main/java/com/cms/device/
│   ├── entity/           # JPA Entities
│   │   ├── Customer.java
│   │   ├── License.java
│   │   └── Device.java
│   ├── dto/             # Data Transfer Objects
│   │   ├── CustomerDto.java
│   │   ├── LicenseDto.java
│   │   └── DeviceDto.java
│   ├── repository/       # Spring Data Repositories
│   │   ├── CustomerRepository.java
│   │   ├── LicenseRepository.java
│   │   └── DeviceRepository.java
│   ├── service/         # Business Logic
│   │   ├── CustomerService.java
│   │   ├── LicenseService.java
│   │   └── DeviceService.java
│   ├── controller/      # REST Controllers
│   │   ├── CustomerController.java
│   │   ├── LicenseController.java
│   │   └── DeviceController.java
│   ├── config/          # Configuration
│   │   └── SecurityConfig.java
│   └── DeviceCmsApplication.java
└── src/main/resources/
    └── application.yml
```

## API Endpoints

### Customer APIs
- `POST /api/customers` - Tạo customer mới
- `GET /api/customers` - Lấy tất cả customers
- `GET /api/customers/{customerId}` - Lấy customer theo ID
- `PUT /api/customers/{customerId}` - Cập nhật customer
- `DELETE /api/customers/{customerId}` - Xóa customer
- `GET /api/customers/active` - Lấy active customers
- `GET /api/customers/search?term={term}` - Tìm kiếm customers
- `POST /api/customers/{customerId}/regenerate-keys` - Tạo lại key pair

### License APIs
- `POST /api/licenses` - Tạo license mới
- `GET /api/licenses` - Lấy tất cả licenses
- `GET /api/licenses/{licenseKey}` - Lấy license theo key
- `PUT /api/licenses/{licenseKey}` - Cập nhật license
- `DELETE /api/licenses/{licenseKey}` - Xóa license
- `GET /api/licenses/active` - Lấy active licenses
- `GET /api/licenses/customer/{customerId}` - Lấy licenses theo customer
- `PUT /api/licenses/{licenseKey}/activate` - Kích hoạt license
- `PUT /api/licenses/{licenseKey}/deactivate` - Vô hiệu hóa license
- `POST /api/licenses/update-expired` - Cập nhật expired licenses

### Device APIs
- `POST /api/devices` - Thêm device mới
- `GET /api/devices` - Lấy tất cả devices
- `GET /api/devices/{deviceId}` - Lấy device theo ID
- `PUT /api/devices/{deviceId}` - Cập nhật device
- `DELETE /api/devices/{deviceId}` - Xóa device
- `GET /api/devices/license/{licenseKey}` - Lấy devices theo license
- `GET /api/devices/active` - Lấy active devices
- `GET /api/devices/customer/{customerId}` - Lấy devices theo customer
- `GET /api/devices/search?term={term}` - Tìm kiếm devices
- `PUT /api/devices/{deviceId}/activity` - Cập nhật activity

## Cài đặt và Chạy

### Yêu cầu
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### Cấu hình Database
1. Tạo database PostgreSQL:
```sql
CREATE DATABASE device_cms;
```

2. Cập nhật `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/device_cms
    username: your_username
    password: your_password
```

### Chạy ứng dụng
```bash
cd backend
mvn spring-boot:run
```

### Truy cập
- API: http://localhost:8080/api/
- Swagger UI: http://localhost:8080/swagger-ui/

## Database Schema

### Customers Table
- `id` (PK)
- `customer_id` (Unique)
- `customer_name`
- `email`
- `phone`
- `public_key` (TEXT)
- `private_key` (TEXT)
- `is_active`
- `created_at`
- `updated_at`

### Licenses Table
- `id` (PK)
- `license_key` (Unique)
- `customer_id` (FK)
- `creation_date`
- `expiry_date`
- `max_devices`
- `is_active`
- `license_type`
- `created_at`
- `updated_at`

### Devices Table
- `id` (PK)
- `license_key` (FK)
- `device_name`
- `device_id`
- `device_type`
- `operating_system`
- `ip_address`
- `is_active`
- `last_activity`
- `created_at`
- `updated_at`

## Tính năng đặc biệt

1. **Tự động tạo key pair**: Khi tạo customer mới, hệ thống tự động tạo RSA key pair
2. **Kiểm tra giới hạn thiết bị**: Không cho phép thêm device vượt quá max_devices
3. **Scheduled task**: Tự động cập nhật license hết hạn hàng ngày
4. **Validation**: Đầy đủ validation cho tất cả input
5. **Error handling**: Xử lý lỗi chi tiết với HTTP status codes phù hợp 