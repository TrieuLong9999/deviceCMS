# HỆ THỐNG QUẢN LÝ LICENSE 

---

## 1. Giới thiệu

Hệ thống Quản Lý License là phần mềm giao diện đồ họa giúp quản lý, tạo, kích hoạt, vô hiệu hóa và xóa các license (bản quyền) cho khách hàng, đồng thời hỗ trợ quản lý thiết bị sử dụng license. Hệ thống hỗ trợ cả kích hoạt offline và online, đồng thời cho phép quản lý key RSA cho từng khách hàng.

---

## 2. Các chức năng giao diện chính

### 2.1. Quản lý Key (67)
- **Mô tả:** Quản lý các cặp khóa RSA cho từng Customer ID, phục vụ cho việc tạo và xác thực license.
- **Chức năng tạo cặp key private/public để xử lý license offline (67):**
  1. Nhấn nút **Quản lý Key** trên thanh công cụ.
  2. Tại cửa sổ quản lý key, chọn tạo mới để sinh ra cặp khóa RSA (private key và public key) cho Customer ID.
  3. Có thể xóa hoặc xem chi tiết các cặp khóa đã tạo.
  4. Mỗi Customer ID cần có một cặp khóa để tạo license.

### 2.2. Tạo License (68)
- **Mô tả:** Tạo license offline cho khách hàng đã có key, quy định số thiết bị, ngày hết hạn, danh sách thiết bị.
- **Chức năng tạo license key kích hoạt license offline (68):**
  1. Nhấn nút **Tạo License** trên thanh công cụ.
  2. Chọn Customer ID (phải có key).
  3. Nhập số thiết bị tối đa, ngày hết hạn, danh sách thiết bị (mỗi dòng một thiết bị).
  4. Hệ thống sử dụng private key để ký và mã hóa thông tin license, sinh ra license key (dạng mã hóa + chữ ký số).
  5. License key được thêm vào danh sách license.

### 2.3. Kích hoạt License (69, 57, 58)
- **Mô tả:** Kích hoạt license offline hoặc online, xác thực tính hợp lệ của license key.
- **Kích hoạt Offline (69):**
  - **Chức năng giải mã thông tin key để kích hoạt license offline (69):**
    1. Nhấn **Kích hoạt License Offline** trên thanh công cụ.
    2. Nhập license key (được cấp từ nơi khác hoặc vừa tạo).
    3. Hệ thống sử dụng public key của Customer ID để giải mã và xác thực chữ ký số của license key.
    4. Nếu hợp lệ, license được thêm vào danh sách và kích hoạt.
- **Kích hoạt Online (57, 58):**
  - **Chức năng gọi tới server kiểm tra thông tin license (57):**
    1. Nhấn **Kích hoạt License Online** trên thanh công cụ.
    2. Nhập license key.
    3. Hệ thống gửi license key lên server để xác thực thông tin license, kiểm tra trạng thái, hạn sử dụng, số lượng thiết bị, v.v.
  - **Chức năng giải mã thông tin key để kích hoạt license online (58):**
    4. Sau khi server xác thực, hệ thống nhận lại thông tin license đã được giải mã và xác thực từ server, tiến hành kích hoạt license trên hệ thống.

### 2.4. Quản lý License (59)
- **Mô tả:** Quản lý trạng thái, thông tin, thiết bị của từng license.
- **Chức năng kiểm tra thông tin, đếm số lượng thiết bị theo license (59):**
  1. Bảng danh sách hiển thị các license đã tạo/thêm vào hệ thống với các thông tin: trạng thái, Customer ID, ngày tạo, ngày hết hạn, số thiết bị đã sử dụng/tối đa, license key (rút gọn).
  2. Chọn một hoặc nhiều license bằng checkbox để kích hoạt, vô hiệu hóa hoặc xóa license.
  3. Nhấn **Xem Chi tiết** để xem thông tin license, danh sách thiết bị.
  4. Có thể thêm, sửa, xóa thiết bị (không vượt quá số lượng tối đa).

### 2.5. Tìm kiếm License
- **Mô tả:** Tìm kiếm nhanh license theo Customer ID hoặc License Key.
  1. Nhập Customer ID hoặc License Key vào ô tìm kiếm để lọc nhanh danh sách license.

### 2.6. Kiểm tra định kỳ (60)
- **Mô tả:** Tự động kiểm tra trạng thái, thời hạn của license.
- **Chức năng định kỳ gọi tới server kiểm tra thời hạn của license (60):**
  1. Bật checkbox "Tự động kiểm tra thời hạn (5s)" trên giao diện.
  2. Hệ thống sẽ tự động gửi yêu cầu lên server mỗi 5 giây để kiểm tra trạng thái, hạn sử dụng của các license.
  3. License hết hạn sẽ tự động chuyển trạng thái sang "Hết hạn" và bị vô hiệu hóa.

---

## 4. Một số lưu ý

- **Chỉ có thể tạo license cho Customer ID đã có key.**
- **License hết hạn sẽ tự động chuyển trạng thái sang "Hết hạn" và bị vô hiệu hóa.**
- **Có thể copy nhanh License Key bằng chuột phải vào dòng license.**
- **Các thao tác đều được ghi lại trong nhật ký hoạt động.**

---

## 5. Kết luận

Hệ thống Quản Lý License giúp đơn giản hóa việc quản lý bản quyền phần mềm, hỗ trợ đầy đủ các thao tác cần thiết cho cả quản trị viên và người dùng cuối. Nếu có thắc mắc hoặc cần hỗ trợ, vui lòng liên hệ bộ phận phát triển.
