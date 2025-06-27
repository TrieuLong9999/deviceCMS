package com.cms.device.console;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.charset.StandardCharsets;

public class LicenseConsoleMenu {

    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(str);
        return sb.toString();
    }

    private static Scanner scanner = new Scanner(System.in);

    // Quản lý nhiều license theo key
    private static Map<String, Map<String, Object>> allLicenses = new HashMap<>();
    // Đếm thiết bị cho từng license
    private static Map<String, AtomicInteger> deviceCounts = new HashMap<>();
    // License key đang được chọn (active)
    private static String activeLicenseKey = null;
    // KeyPair để tạo/giải mã offline license
    private static KeyPair keyPair;
    private static Timer licenseCheckTimer;

    public static void main(String[] args) {
        System.out.println("=== HỆ THỐNG QUẢN LÝ LICENSE NHIỀU LICENSE ===");
        System.out.println("Khởi tạo hệ thống...\n");
        initializeSystem();
        showMainMenu();
    }

    private static void initializeSystem() {
        // Khởi tạo một license mẫu (online)
        Map<String, Object> demoLicense = new HashMap<>();
        demoLicense.put("maxDevices", 10);
        demoLicense.put("expiryDate", "2024-12-31");
        demoLicense.put("isActive", true);
        demoLicense.put("licenseKey", "VHTC-2024-DEMO-LICENSE");
        demoLicense.put("type", "online");
        allLicenses.put("VHTC-2024-DEMO-LICENSE", demoLicense);
        deviceCounts.put("VHTC-2024-DEMO-LICENSE", new AtomicInteger(0));
        activeLicenseKey = "VHTC-2024-DEMO-LICENSE";
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n" + repeatString("=", 60));
            System.out.println("           MENU QUẢN LÝ LICENSE (ĐA LICENSE)");
            System.out.println(repeatString("=", 60));
            System.out.println("1. [VHTC_W_57] Kiểm tra thông tin license từ server");
            System.out.println("2. [VHTC_W_58] Giải mã key kích hoạt license online");
            System.out.println("3. [VHTC_W_59] Kiểm tra thông tin và đếm thiết bị theo license");
            System.out.println("4. [VHTC_W_60] Định kỳ kiểm tra thời hạn license (tất cả license)");
            System.out.println("5. [VHTC_W_67] Tạo cặp key private/public cho license offline");
            System.out.println("6. [VHTC_W_68] Tạo license key cho kích hoạt offline (có ký số)");
            System.out.println("7. [VHTC_W_69] Giải mã key kích hoạt license offline (kiểm tra chữ ký số)");
            System.out.println("8. Danh sách tất cả license đã lưu");
            System.out.println("0. Thoát chương trình");
            System.out.println(repeatString("=", 60));
            System.out.print("Vui lòng chọn chức năng (0-8): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                handleMenuChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Vui lòng nhập số từ 0-8!");
            }
        }
    }

    private static void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                checkLicenseFromServer();
                break;
            case 2:
                decodeOnlineLicenseKey();
                break;
            case 3:
                checkLicenseInfoAndDeviceCount();
                break;
            case 4:
                startPeriodicLicenseCheck();
                break;
            case 5:
                generateKeyPairForOffline();
                break;
            case 6:
                generateOfflineLicenseKey();
                break;
            case 7:
                decodeOfflineLicenseKey();
                break;
            case 8:
                listAllLicenses();
                break;
            case 0:
                exitProgram();
                break;
            default:
                System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn từ 0-8.");
        }
    }

    // 1. Kiểm tra license từ server (theo từng license)
    private static void checkLicenseFromServer() {
        System.out.println("\n📡 [VHTC_W_57] KIỂM TRA LICENSE TỪ SERVER");
        System.out.println(repeatString("-", 50));
        System.out.print("Nhập License Key để kiểm tra: ");
        String licenseKey = scanner.nextLine().trim();

        if (licenseKey.isEmpty()) {
            System.out.println("❌ License key không được để trống!");
            return;
        }
        Map<String, Object> licenseData = allLicenses.get(licenseKey);
        if (licenseData == null) {
            System.out.println("❌ License Key KHÔNG HỢP LỆ!");
            waitForEnter();
            return;
        }
        System.out.println("🔄 Đang kết nối tới server...");
        try {
            Thread.sleep(1000);
            System.out.println("✅ Kết nối server thành công!");
            System.out.println("📋 Thông tin license từ server:");
            System.out.println("   - License Key: " + licenseKey);
            System.out.println("   - Trạng thái: " + ((Boolean) licenseData.get("isActive") ? "Hoạt động" : "Không hoạt động"));
            System.out.println("   - Số thiết bị tối đa: " + licenseData.get("maxDevices"));
            System.out.println("   - Ngày hết hạn: " + licenseData.get("expiryDate"));
            System.out.println("   - Server response time: " + System.currentTimeMillis() % 1000 + "ms");
        } catch (InterruptedException e) {
            System.out.println("❌ Lỗi kết nối tới server!");
        }
        waitForEnter();
    }

    // 2. Giải mã license key online (cập nhật, không ghi đè license cũ)
    private static void decodeOnlineLicenseKey() {
        System.out.println("\n🔓 [VHTC_W_58] GIẢI MÃ LICENSE KEY ONLINE");
        System.out.println(repeatString("-", 50));

        System.out.print("Nhập license key cần giải mã: ");
        String encodedKey = scanner.nextLine().trim();

        if (encodedKey.isEmpty()) {
            System.out.println("❌ License key không được để trống!");
            return;
        }

        if (!allLicenses.containsKey(encodedKey)) {
            System.out.println("❌ License Key KHÔNG HỢP LỆ hoặc không khớp với hệ thống!");
            waitForEnter();
            return;
        }

        System.out.println("🔄 Đang giải mã license key...");
        try {
            Thread.sleep(1500);
            // Giả lập thông tin được giải mã từ key (ở thực tế bạn nên thực sự giải mã)
            String decodedInfo = "Company: VHTC, Version: 1.0, Devices: 12, Expiry: 2025-12-31";
            System.out.println("✅ Giải mã thành công!");
            System.out.println("📋 Thông tin được giải mã:");
            System.out.println("   - Mã gốc: " + encodedKey);
            System.out.println("   - Thông tin: " + decodedInfo);
            System.out.println("   - Thuật toán: RSA-2048");
            System.out.println("   - Trạng thái: Hợp lệ");

            int maxDevices = 12;
            String expiryDate = "2025-12-31";

            Map<String, Object> licenseData = allLicenses.get(encodedKey);
            licenseData.put("isActive", true);
            licenseData.put("maxDevices", maxDevices);
            licenseData.put("expiryDate", expiryDate);
            licenseData.put("decodedKey", decodedInfo);

            // Set là license đang active
            activeLicenseKey = encodedKey;

        } catch (InterruptedException e) {
            System.out.println("❌ Lỗi trong quá trình giải mã!");
        }
        waitForEnter();
    }

    // 3. Xem thông tin và quản lý thiết bị cho từng license
    private static void checkLicenseInfoAndDeviceCount() {
        if (allLicenses.isEmpty()) {
            System.out.println("❌ Chưa có license nào trong hệ thống!");
            return;
        }
        String licenseKey = selectLicenseKey();
        if (licenseKey == null) return;

        Map<String, Object> licenseData = allLicenses.get(licenseKey);
        AtomicInteger deviceCount = deviceCounts.get(licenseKey);

        // Cập nhật trạng thái license nếu hết hạn
        String expiryDate = (String) licenseData.get("expiryDate");
        if (java.time.LocalDate.now().isAfter(java.time.LocalDate.parse(expiryDate))) {
            licenseData.put("isActive", false);
        }

        System.out.println("\n📊 [VHTC_W_59] KIỂM TRA THÔNG TIN VÀ ĐẾM THIẾT BỊ");
        System.out.println(repeatString("-", 50));
        System.out.println("📋 Thông tin license hiện tại:");
        System.out.println("   - License Key: " + licenseKey);
        System.out.println("   - Loại: " + licenseData.get("type"));
        System.out.println("   - Trạng thái: " + ((Boolean) licenseData.get("isActive") ? "✅ Hoạt động" : "❌ Không hoạt động"));
        System.out.println("   - Số thiết bị tối đa: " + licenseData.get("maxDevices"));
        System.out.println("   - Số thiết bị đang sử dụng: " + deviceCount.get());
        System.out.println("   - Số thiết bị còn lại: " + ((Integer) licenseData.get("maxDevices") - deviceCount.get()));
        System.out.println("   - Ngày hết hạn: " + licenseData.get("expiryDate"));

        System.out.println("\n🔧 Quản lý thiết bị:");
        System.out.println("1. Thêm thiết bị mới");
        System.out.println("2. Xóa thiết bị");
        System.out.println("3. Quay lại menu chính");
        System.out.print("Chọn thao tác: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    addDevice(licenseKey);
                    break;
                case 2:
                    removeDevice(licenseKey);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Vui lòng nhập số!");
        }
        waitForEnter();
    }

    private static void addDevice(String licenseKey) {
        Map<String, Object> licenseData = allLicenses.get(licenseKey);
        AtomicInteger deviceCount = deviceCounts.get(licenseKey);

        int maxDevices = (Integer) licenseData.get("maxDevices");
        boolean isActive = (Boolean) licenseData.get("isActive");
        String expiryDate = (String) licenseData.get("expiryDate");

        if (java.time.LocalDate.now().isAfter(java.time.LocalDate.parse(expiryDate))) {
            System.out.println("❌ License đã hết hạn!");
            licenseData.put("isActive", false);
            return;
        }
        if (!isActive) {
            System.out.println("❌ License không hoạt động!");
            return;
        }
        if (deviceCount.get() >= maxDevices) {
            System.out.println("❌ Đã đạt giới hạn số thiết bị tối đa (" + maxDevices + ")!");
            return;
        }

        System.out.print("Nhập tên thiết bị: ");
        String deviceName = scanner.nextLine().trim();
        if (!deviceName.isEmpty()) {
            deviceCount.incrementAndGet();
            System.out.println("✅ Đã thêm thiết bị '" + deviceName + "'. Tổng số thiết bị: " + deviceCount.get());
        }
    }

    private static void removeDevice(String licenseKey) {
        AtomicInteger deviceCount = deviceCounts.get(licenseKey);
        if (deviceCount.get() == 0) {
            System.out.println("❌ Không có thiết bị nào để xóa!");
            return;
        }
        System.out.print("Nhập tên thiết bị cần xóa: ");
        String deviceName = scanner.nextLine().trim();
        if (!deviceName.isEmpty()) {
            deviceCount.decrementAndGet();
            System.out.println("✅ Đã xóa thiết bị '" + deviceName + "'. Tổng số thiết bị: " + deviceCount.get());
        }
    }

    // 4. Kiểm tra định kỳ cho tất cả license
    private static void startPeriodicLicenseCheck() {
        if (allLicenses.isEmpty()) {
            System.out.println("❌ Không có license nào để kiểm tra!");
            waitForEnter();
            return;
        }

        System.out.println("\n⏰ [VHTC_W_60] KIỂM TRA ĐỊNH KỲ TẤT CẢ LICENSE");
        System.out.println("-".repeat(50));

        if (licenseCheckTimer != null) {
            licenseCheckTimer.cancel();
            System.out.println("⏹️ Đã dừng kiểm tra định kỳ trước đó.");
        }

        System.out.print("Nhập khoảng thời gian kiểm tra (giây): ");
        try {
            int intervalSeconds = Integer.parseInt(scanner.nextLine().trim());
            if (intervalSeconds <= 0) {
                System.out.println("❌ Khoảng thời gian phải lớn hơn 0!");
                return;
            }

            licenseCheckTimer = new Timer();
            licenseCheckTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("\n🔄 [" + java.time.LocalTime.now() + "] Kiểm tra định kỳ tất cả license...");
                    int idx = 1;
                    for (Map.Entry<String, Map<String, Object>> entry : allLicenses.entrySet()) {
                        String key = entry.getKey();
                        Map<String, Object> licenseData = entry.getValue();
                        AtomicInteger deviceCount = deviceCounts.get(key);

                        String expiryDate = (String) licenseData.get("expiryDate");
                        // Cập nhật trạng thái nếu hết hạn
                        if (java.time.LocalDate.now().isAfter(java.time.LocalDate.parse(expiryDate))) {
                            licenseData.put("isActive", false);
                        }
                        System.out.println("  " + (idx++) + ". License: " + key +
                                " | Trạng thái: " + ((Boolean) licenseData.get("isActive") ? "Hoạt động" : "Hết hạn") +
                                " | Ngày hết hạn: " + licenseData.get("expiryDate") +
                                " | Số thiết bị: " + deviceCount.get() + "/" + licenseData.get("maxDevices")
                        );
                    }
                }
            }, 0, intervalSeconds * 1000L);

            System.out.println("✅ Đã bắt đầu kiểm tra định kỳ tất cả license mỗi " + intervalSeconds + " giây.");
            System.out.println("💡 Nhấn Enter để dừng kiểm tra định kỳ...");
            scanner.nextLine();

            licenseCheckTimer.cancel();
            System.out.println("⏹️ Đã dừng kiểm tra định kỳ.");

        } catch (NumberFormatException e) {
            System.out.println("❌ Vui lòng nhập số nguyên hợp lệ!");
        }
        waitForEnter();
    }

    // 5. Tạo cặp key offline (như cũ)
    private static void generateKeyPairForOffline() {
        System.out.println("\n🔐 [VHTC_W_67] TẠO CẶP KEY PRIVATE/PUBLIC");
        System.out.println("-".repeat(50));

        try {
            System.out.println("🔄 Đang tạo cặp key RSA-2048...");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            String privateKeyB64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            String publicKeyB64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            System.out.println("✅ Tạo cặp key thành công!");
            System.out.println("\n📋 Thông tin key:");
            System.out.println("   - Thuật toán: RSA");
            System.out.println("   - Độ dài key: 2048 bit");
            System.out.println("   - Private Key: " + privateKeyB64.substring(0, 50) + "...");
            System.out.println("   - Public Key: " + publicKeyB64.substring(0, 50) + "...");

            System.out.println("\n💾 Lưu key vào file?");
            System.out.println("1. Có");
            System.out.println("2. Không");
            System.out.print("Chọn: ");

            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 1) {
                System.out.println("✅ Key đã được lưu vào bộ nhớ để sử dụng cho các chức năng offline.");
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println("❌ Lỗi: Thuật toán RSA không được hỗ trợ!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Lựa chọn không hợp lệ!");
        }
        waitForEnter();
    }

    // 6. Tạo license offline (ký số thực tế, chỉ 1 trường khách hàng)
    private static void generateOfflineLicenseKey() {
        System.out.println("\n🔑 [VHTC_W_68] TẠO LICENSE KEY OFFLINE (có ký số + danh sách thiết bị)");
        System.out.println("-".repeat(50));

        if (keyPair == null) {
            System.out.println("❌ Chưa có cặp key! Vui lòng tạo cặp key trước (chức năng 5).");
            waitForEnter();
            return;
        }

        System.out.println("📝 Nhập thông tin license:");
        System.out.print("Tên khách hàng: ");
        String customerName = scanner.nextLine().trim();

        System.out.print("Tên công ty: ");
        String company = scanner.nextLine().trim();

        System.out.print("Số thiết bị tối đa: ");
        String maxDevices = scanner.nextLine().trim();

        System.out.print("Ngày hết hạn (YYYY-MM-DD): ");
        String expiryDate = scanner.nextLine().trim();

        // Nhập danh sách thiết bị (cách 1: ngăn cách bằng dấu phẩy)
        System.out.print("Danh sách thiết bị (nhập các tên, cách nhau bởi dấu phẩy): ");
        String devicesInput = scanner.nextLine().trim();
        String[] deviceArr = Arrays.stream(devicesInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        String devicesString = String.join(",", deviceArr);

        if (customerName.isEmpty() || company.isEmpty() || maxDevices.isEmpty() || expiryDate.isEmpty()) {
            System.out.println("❌ Vui lòng điền đầy đủ thông tin!");
            waitForEnter();
            return;
        }

        try {
            String licenseInfo = String.format(
                    "CUSTOMER_NAME=%s;COMPANY=%s;MAX_DEVICES=%s;EXPIRY=%s;DEVICES=%s;CREATED=%s",
                    customerName, company, maxDevices, expiryDate, devicesString, java.time.LocalDateTime.now()
            );

            // Ký số licenseInfo với private key
            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(keyPair.getPrivate());
            privateSignature.update(licenseInfo.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = privateSignature.sign();

            // Chuyển chữ ký số sang hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : signatureBytes) {
                sb.append(String.format("%02x", b));
            }
            String signatureHex = sb.toString();

            // License key offline thực tế: [licenseInfo].[signatureHex]
            String offlineLicenseKey = licenseInfo + "." + signatureHex;

            System.out.println("\n✅ Tạo license key offline thành công!");
            System.out.println("📋 Thông tin license:");
            System.out.println("   - Tên khách hàng: " + customerName);
            System.out.println("   - Công ty: " + company);
            System.out.println("   - Số thiết bị tối đa: " + maxDevices);
            System.out.println("   - Ngày hết hạn: " + expiryDate);
            System.out.println("   - Danh sách thiết bị: " + devicesString);
            System.out.println("   - License Key:\n" + offlineLicenseKey);

            Map<String, Object> licenseData = new HashMap<>();
            licenseData.put("offlineLicenseKey", offlineLicenseKey);
            licenseData.put("offlineLicenseInfo", licenseInfo);
            licenseData.put("licenseKey", offlineLicenseKey);
            licenseData.put("isActive", true);
            licenseData.put("maxDevices", Integer.parseInt(maxDevices));
            licenseData.put("expiryDate", expiryDate);
            licenseData.put("type", "offline");
            licenseData.put("customerName", customerName);
            licenseData.put("devices", Arrays.asList(deviceArr));

            allLicenses.put(offlineLicenseKey, licenseData);
            deviceCounts.put(offlineLicenseKey, new AtomicInteger(deviceArr.length));
            activeLicenseKey = offlineLicenseKey;

        } catch (Exception e) {
            System.out.println("❌ Lỗi trong quá trình tạo license key! " + e.getMessage());
        }
        waitForEnter();
    }


    // 7. Giải mã offline license: xác thực chữ ký số, giải mã thông tin khách hàng
    private static void decodeOfflineLicenseKey() {
        System.out.println("\n🔓 [VHTC_W_69] GIẢI MÃ LICENSE KEY OFFLINE (kiểm tra chữ ký số + danh sách thiết bị)");
        System.out.println("-".repeat(50));

        if (keyPair == null) {
            System.out.println("❌ Chưa có cặp key! Vui lòng tạo cặp key trước (chức năng 5).");
            waitForEnter();
            return;
        }

        System.out.print("Nhập offline license key cần giải mã: ");
        String offlineLicenseKey = scanner.nextLine().trim();

        if (offlineLicenseKey.isEmpty()) {
            System.out.println("❌ License key không được để trống!");
            waitForEnter();
            return;
        }

        System.out.println("🔄 Đang giải mã và xác thực license key...");

        try {
            Thread.sleep(800);

            int idx = offlineLicenseKey.lastIndexOf(".");
            if (idx == -1) {
                System.out.println("❌ Định dạng license key không hợp lệ!");
                waitForEnter();
                return;
            }

            String licenseInfo = offlineLicenseKey.substring(0, idx);
            String signatureHex = offlineLicenseKey.substring(idx + 1);

            // Chuyển chữ ký số hex -> byte[]
            int len = signatureHex.length();
            byte[] signatureBytes = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                signatureBytes[i / 2] = (byte) ((Character.digit(signatureHex.charAt(i), 16) << 4)
                        + Character.digit(signatureHex.charAt(i+1), 16));
            }

            // Kiểm tra chữ ký số bằng public key
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(keyPair.getPublic());
            publicSignature.update(licenseInfo.getBytes(StandardCharsets.UTF_8));
            boolean isVerified = publicSignature.verify(signatureBytes);

            if (isVerified) {
                System.out.println("✅ Giải mã thành công!");
                System.out.println("🔐 Xác thực chữ ký số: HỢP LỆ");
                System.out.println("\n📋 Thông tin license được giải mã:");
                String[] parts = licenseInfo.split(";");
                int maxDevices = 0;
                String expiryDate = null;
                String customerName = "";
                String devicesString = "";
                for (String part : parts) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        switch (key) {
                            case "CUSTOMER_NAME":
                                System.out.println("   - Tên khách hàng: " + value);
                                customerName = value; break;
                            case "COMPANY":
                                System.out.println("   - Công ty: " + value); break;
                            case "MAX_DEVICES":
                                System.out.println("   - Số thiết bị tối đa: " + value);
                                maxDevices = Integer.parseInt(value); break;
                            case "EXPIRY":
                                System.out.println("   - Ngày hết hạn: " + value);
                                expiryDate = value; break;
                            case "DEVICES":
                                devicesString = value;
                                // Hiển thị danh sách từng thiết bị cho rõ ràng
                                String[] deviceArr = value.split(",");
                                System.out.print("   - Danh sách thiết bị: ");
                                for (int i = 0; i < deviceArr.length; i++) {
                                    System.out.print(deviceArr[i].trim());
                                    if (i < deviceArr.length - 1) System.out.print(", ");
                                }
                                System.out.println();
                                break;
                            case "CREATED":
                                System.out.println("   - Ngày tạo: " + value); break;
                            default:
                                System.out.println("   - " + key + ": " + value);
                        }
                    }
                }

                // Lưu lại license đã giải mã vào danh sách license (nếu muốn)
                Map<String, Object> licenseData = new HashMap<>();
                licenseData.put("offlineLicenseKey", offlineLicenseKey);
                licenseData.put("offlineLicenseInfo", licenseInfo);
                licenseData.put("licenseKey", offlineLicenseKey);
                licenseData.put("isActive", true);
                licenseData.put("maxDevices", maxDevices);
                licenseData.put("expiryDate", expiryDate);
                licenseData.put("type", "offline");
                licenseData.put("customerName", customerName);
                licenseData.put("devices", Arrays.asList(devicesString.split(",")));

                allLicenses.put(offlineLicenseKey, licenseData);
                if (!deviceCounts.containsKey(offlineLicenseKey))
                    deviceCounts.put(offlineLicenseKey, new AtomicInteger(devicesString.isEmpty() ? 0 : devicesString.split(",").length));
                activeLicenseKey = offlineLicenseKey;

                System.out.println("\n✅ License offline đã được kích hoạt thành công!");
            } else {
                System.out.println("❌ License key không hợp lệ hoặc đã bị sửa đổi!");
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi trong quá trình giải mã! " + e.getMessage());
        }
        waitForEnter();
    }


    // 8. Danh sách tất cả license đang lưu
    private static void listAllLicenses() {
        if (allLicenses.isEmpty()) {
            System.out.println("❌ Không có license nào!");
            return;
        }
        System.out.println("\n📜 DANH SÁCH LICENSE HIỆN CÓ:");
        int i = 1;
        for (Map.Entry<String, Map<String, Object>> entry : allLicenses.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> lic = entry.getValue();
            System.out.println(i++ + ". Key: " + key + " | " +
                    "Loại: " + lic.get("type") +
                    " | Trạng thái: " + ((Boolean) lic.get("isActive") ? "Hoạt động" : "Không hoạt động") +
                    (key.equals(activeLicenseKey) ? " [Đang chọn]" : ""));
        }
        waitForEnter();
    }

    // Chọn license key để thao tác (cho chức năng 3)
    private static String selectLicenseKey() {
        if (allLicenses.isEmpty()) {
            System.out.println("❌ Không có license nào!");
            return null;
        }
        List<String> keys = new ArrayList<>(allLicenses.keySet());
        System.out.println("Chọn license muốn thao tác:");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            System.out.println((i + 1) + ". " + key + (key.equals(activeLicenseKey) ? " [Đang chọn]" : ""));
        }
        System.out.print("Nhập số thứ tự: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 1 || idx > keys.size()) {
                System.out.println("❌ Lựa chọn không hợp lệ!");
                return null;
            }
            return keys.get(idx - 1);
        } catch (NumberFormatException e) {
            System.out.println("❌ Vui lòng nhập số!");
            return null;
        }
    }

    private static void exitProgram() {
        System.out.println("\n👋 Cảm ơn bạn đã sử dụng hệ thống quản lý license!");
        System.out.println("🔄 Đang thoát chương trình...");

        if (licenseCheckTimer != null) licenseCheckTimer.cancel();

        scanner.close();
        System.exit(0);
    }

    private static void waitForEnter() {
        System.out.println("\n💡 Nhấn Enter để tiếp tục...");
        scanner.nextLine();
    }
}
