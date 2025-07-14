# HỆ THỐNG QUẢN LÝ LICENSE 

---

## 1. Giới thiệu

Hệ thống Quản Lý License là phần mềm giao diện đồ họa giúp quản lý, tạo, kích hoạt, vô hiệu hóa và xóa các license (bản quyền) cho khách hàng, đồng thời hỗ trợ quản lý thiết bị sử dụng license. Hệ thống hỗ trợ cả kích hoạt offline và online, đồng thời cho phép quản lý key RSA cho từng khách hàng.

---

## 2. Giao diện chính

- **Thanh công cụ trên cùng**:
  - **Quản lý Key**: Quản lý các cặp khóa RSA cho từng Customer ID.
  - **Tạo License**: Tạo license offline cho khách hàng đã có key.
  - **Kích hoạt License Offline**: Nhập license key để kích hoạt offline.
  - **Kích hoạt License Online**: Nhập license key để kích hoạt online (giả lập).

- **Bảng danh sách License**:
  Hiển thị các license đã tạo/thêm vào hệ thống với các thông tin:
  - Trạng thái (Chưa kích hoạt, Hoạt động, Vô hiệu hóa, Hết hạn)
  - Customer ID
  - Ngày tạo
  - Ngày hết hạn
  - Số thiết bị đã sử dụng / tối đa
  - License Key (rút gọn)
  - Checkbox chọn nhiều license để thao tác hàng loạt

- **Thanh công cụ bảng**:
  - **Kích hoạt**: Kích hoạt các license đã chọn.
  - **Vô hiệu hóa**: Vô hiệu hóa các license đã chọn.
  - **Xóa License**: Xóa các license đã chọn.
  - **Xem Chi tiết**: Xem chi tiết và quản lý thiết bị của license (chỉ chọn 1 license).

- **Tìm kiếm**:
  Nhập Customer ID hoặc License Key để lọc nhanh danh sách license.

- **Nhật ký hoạt động**:
  Hiển thị các thao tác, thông báo, lỗi trong quá trình sử dụng.

- **Tùy chọn kiểm tra định kỳ**:
  Checkbox "Tự động kiểm tra thời hạn (5s)" để hệ thống tự động kiểm tra trạng thái license mỗi 5 giây.


---

## 3. Luồng hoạt động chính

### 3.1. Quản lý Key (Quản lý cặp khóa RSA cho khách hàng)
- Nhấn **Quản lý Key** để mở cửa sổ quản lý key.
- Tại đây, bạn có thể tạo mới, xóa hoặc xem các cặp khóa cho từng Customer ID.
- Mỗi Customer ID cần có một cặp khóa để tạo license.

### 3.2. Tạo License
- Nhấn **Tạo License**.
- Chọn Customer ID (phải có key).
- Nhập số thiết bị tối đa, ngày hết hạn, danh sách thiết bị (mỗi dòng một thiết bị).
- Hệ thống sẽ tạo license key (dạng mã hóa + chữ ký số) và thêm vào danh sách.

### 3.3. Kích hoạt License (Offline/Online)
- **Kích hoạt Offline**:
  - Nhấn **Kích hoạt License Offline**.
  - Nhập license key (được cấp từ nơi khác hoặc vừa tạo).
  - Hệ thống xác thực chữ ký số, nếu hợp lệ sẽ thêm vào danh sách và kích hoạt.
- **Kích hoạt Online**:
  - Nhấn **Kích hoạt License Online**.
  - Nhập license key.
  - Hệ thống xác thực tương tự offline (giả lập online).

### 3.4. Quản lý License
- **Kích hoạt/Vô hiệu hóa/Xóa**:
  - Chọn một hoặc nhiều license bằng checkbox.
  - Nhấn nút tương ứng để thực hiện thao tác.
- **Xem chi tiết & Quản lý thiết bị**:
  - Chọn một license, nhấn **Xem Chi tiết**.
  - Cửa sổ chi tiết cho phép xem thông tin license, danh sách thiết bị.
  - Có thể thêm, sửa, xóa thiết bị (không vượt quá số lượng tối đa).

### 3.5. Tìm kiếm License
- Nhập Customer ID hoặc License Key vào ô tìm kiếm để lọc nhanh danh sách.

### 3.6. Kiểm tra định kỳ
- Bật checkbox "Tự động kiểm tra thời hạn (5s)" để hệ thống tự động kiểm tra trạng thái license (hết hạn, hoạt động, v.v.).

---

## 4. Một số lưu ý

- **Chỉ có thể tạo license cho Customer ID đã có key.**
- **License hết hạn sẽ tự động chuyển trạng thái sang "Hết hạn" và bị vô hiệu hóa.**
- **Có thể copy nhanh License Key bằng chuột phải vào dòng license.**
- **Các thao tác đều được ghi lại trong nhật ký hoạt động.**

---

## 5. Kết luận

Hệ thống Quản Lý License giúp đơn giản hóa việc quản lý bản quyền phần mềm, hỗ trợ đầy đủ các thao tác cần thiết cho cả quản trị viên và người dùng cuối. Nếu có thắc mắc hoặc cần hỗ trợ, vui lòng liên hệ bộ phận phát triển.
