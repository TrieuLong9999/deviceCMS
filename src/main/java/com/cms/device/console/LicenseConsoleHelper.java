package com.cms.device.console;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helper class để kiểm tra trạng thái license
 */
public class LicenseConsoleHelper {
    
    /**
     * Kiểm tra license có hết hạn hay không
     * @param expiryDateStr Ngày hết hạn dạng "YYYY-MM-DD"
     * @return true nếu còn hạn, false nếu hết hạn
     */
    public static boolean isLicenseValid(String expiryDateStr) {
        try {
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate currentDate = LocalDate.now();
            return currentDate.isBefore(expiryDate) || currentDate.isEqual(expiryDate);
        } catch (DateTimeParseException e) {
            System.out.println("❌ Lỗi: Định dạng ngày không hợp lệ: " + expiryDateStr);
            return false;
        }
    }
    
    /**
     * Tính số ngày còn lại của license
     * @param expiryDateStr Ngày hết hạn dạng "YYYY-MM-DD"
     * @return Số ngày còn lại (âm nếu đã hết hạn)
     */
    public static long getDaysRemaining(String expiryDateStr) {
        try {
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate currentDate = LocalDate.now();
            return currentDate.until(expiryDate).getDays();
        } catch (DateTimeParseException e) {
            return -999; // Lỗi format
        }
    }
    
    /**
     * Lấy trạng thái license dạng text
     * @param expiryDateStr Ngày hết hạn
     * @param isActive Trạng thái kích hoạt
     * @return Chuỗi mô tả trạng thái
     */
    public static String getLicenseStatusText(String expiryDateStr, boolean isActive) {
        if (!isActive) {
            return "❌ Chưa kích hoạt";
        }
        
        if (!isLicenseValid(expiryDateStr)) {
            return "⚠️ Đã hết hạn";
        }
        
        long daysRemaining = getDaysRemaining(expiryDateStr);
        if (daysRemaining <= 7) {
            return "⚠️ Sắp hết hạn (" + daysRemaining + " ngày)";
        }
        
        return "✅ Hoạt động (" + daysRemaining + " ngày còn lại)";
    }
    
    /**
     * Hiển thị cảnh báo nếu license sắp hết hạn
     * @param expiryDateStr Ngày hết hạn
     */
    public static void checkAndWarnExpiry(String expiryDateStr) {
        long daysRemaining = getDaysRemaining(expiryDateStr);
        
        if (daysRemaining < 0) {
            System.out.println("\n🚨 CẢNH BÁO: LICENSE ĐÃ HẾT HẠN!");
            System.out.println("   Đã hết hạn " + Math.abs(daysRemaining) + " ngày trước");
            System.out.println("   Vui lòng gia hạn license để tiếp tục sử dụng.");
        } else if (daysRemaining <= 7) {
            System.out.println("\n⚠️ CẢNH BÁO: LICENSE SẮP HẾT HẠN!");
            System.out.println("   Còn " + daysRemaining + " ngày");
            System.out.println("   Vui lòng chuẩn bị gia hạn license.");
        }
    }
} 