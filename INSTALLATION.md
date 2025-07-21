# HƯỚNG DẪN CÀI ĐẶT HỆ THỐNG QUẢN LÝ LICENSE

---

## 1. Yêu cầu hệ thống

### 1.1. Hệ điều hành
- **Windows 10/11** (64-bit)
- **macOS 10.14+** 
- **Linux** (Ubuntu 18.04+, CentOS 7+)

### 1.2. Yêu cầu phần mềm
- **Java Development Kit (JDK)**: Phiên bản 8 hoặc 11
- **Maven**: Phiên bản 3.6.0 trở lên

---

## 2. Cài đặt Java Development Kit (JDK)

### 2.1. Tải JDK
- Truy cập: https://www.oracle.com/java/technologies/downloads/
- Tải JDK 8 hoặc JDK 11 phù hợp với hệ điều hành
- Hoặc sử dụng OpenJDK: https://adoptium.net/

### 2.2. Cài đặt trên Windows
1. Chạy file installer đã tải về
2. Chọn thư mục cài đặt (mặc định: `C:\Program Files\Java\jdk-8.x.x`)
3. Thiết lập biến môi trường:
   - Mở **System Properties** → **Environment Variables**
   - Thêm `JAVA_HOME`: `C:\Program Files\Java\jdk-8.x.x`
   - Thêm `%JAVA_HOME%\bin` vào `Path`
4. Kiểm tra cài đặt: `java -version`

### 2.3. Cài đặt trên macOS
```bash
# Sử dụng Homebrew
brew install openjdk@8

# Hoặc tải từ Oracle và cài đặt
# Sau đó thiết lập JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-8.x.x.jdk/Contents/Home
```

### 2.4. Cài đặt trên Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-8-jdk

# CentOS/RHEL
sudo yum install java-1.8.0-openjdk-devel

# Thiết lập JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk
```

---

## 3. Cài đặt Maven

### 3.1. Tải Maven
- Truy cập: https://maven.apache.org/download.cgi
- Tải file binary (apache-maven-3.x.x-bin.zip)

### 3.2. Cài đặt trên Windows
1. Giải nén file đã tải về vào thư mục (ví dụ: `C:\Program Files\Apache\maven`)
2. Thiết lập biến môi trường:
   - Thêm `MAVEN_HOME`: `C:\Program Files\Apache\maven`
   - Thêm `%MAVEN_HOME%\bin` vào `Path`
3. Kiểm tra cài đặt: `mvn -version`

### 3.3. Cài đặt trên macOS
```bash
# Sử dụng Homebrew
brew install maven

# Hoặc cài đặt thủ công
export MAVEN_HOME=/usr/local/apache-maven-3.x.x
export PATH=$PATH:$MAVEN_HOME/bin
```

### 3.4. Cài đặt trên Linux
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven
```

---

## 4. Mở và Build dự án

### 4.1. Mở dự án
- Mở thư mục dự án trong IDE (Eclipse, IntelliJ IDEA, VS Code)
- Hoặc sử dụng command line để di chuyển vào thư mục dự án:
```bash
cd deviceCMS
```

### 4.2. Build dự án
```bash
# Clean và compile
mvn clean compile

# Package thành JAR file
mvn clean package

# Chạy tests (tùy chọn)
mvn test
```

### 4.3. Kiểm tra build thành công
- File JAR sẽ được tạo tại: `target/deviceCMS-1.0.jar`
- Kiểm tra bằng lệnh: `java -jar target/deviceCMS-1.0.jar`

---

## 5. Chạy ứng dụng

### 5.1. Chạy từ source code
```bash
# Chạy trực tiếp từ Maven
mvn exec:java -Dexec.mainClass="com.cms.device.console.LicenseSwingGUI"

# Hoặc chạy từ IDE (Eclipse, IntelliJ IDEA)
```

### 5.2. Chạy từ JAR file
```bash
java -jar target/deviceCMS-1.0.jar
```

### 5.3. Chạy với tham số (nếu có)
```bash
java -jar target/deviceCMS-1.0.jar --config=application.properties
```

---

## 6. Cấu hình ứng dụng

### 6.1. File cấu hình
- File cấu hình: `src/main/resources/application.properties`
- Có thể tùy chỉnh các thông số:
  - Database connection (nếu có)
  - Server endpoints
  - Logging level
  - Timeout settings

### 6.2. Cấu hình logging
```properties
# Trong application.properties
logging.level.com.cms.device=DEBUG
logging.file.name=logs/deviceCMS.log
```

---

## 7. Troubleshooting

### 7.1. Lỗi Java version
```
Error: Unsupported major.minor version 52.0
```
**Giải pháp:** Cài đặt JDK 8 hoặc 11

### 7.2. Lỗi Maven dependencies
```
Could not resolve dependencies
```
**Giải pháp:** 
```bash
mvn clean install -U
```

### 7.3. Lỗi permission
```
Permission denied
```
**Giải pháp:** Chạy với quyền admin hoặc sudo

### 7.4. Lỗi port đã sử dụng
```
Address already in use
```
**Giải pháp:** Thay đổi port trong cấu hình hoặc kill process đang sử dụng port

---

## 8. Kiểm tra cài đặt

### 8.1. Kiểm tra Java
```bash
java -version
javac -version
echo $JAVA_HOME
```

### 8.2. Kiểm tra Maven
```bash
mvn -version
echo $MAVEN_HOME
```

### 8.3. Kiểm tra ứng dụng
- Chạy ứng dụng và kiểm tra giao diện GUI
- Kiểm tra các chức năng cơ bản
- Xem log file để đảm bảo không có lỗi

