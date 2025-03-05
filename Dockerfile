# Sử dụng OpenJDK 17 làm môi trường runtime
FROM openjdk:17-jdk-slim

# Đặt biến môi trường để giảm log cảnh báo
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Tạo thư mục chứa ứng dụng trong container
WORKDIR /app

# Sao chép file JAR từ thư mục build vào container
COPY target/*.jar app.jar

# Cấu hình cổng chạy ứng dụng (Spring Boot mặc định là 8080)
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
