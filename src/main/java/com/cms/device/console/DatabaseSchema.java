package com.cms.device.console;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {
    
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            createCustomerKeysTable(conn);
            createLicensesTable(conn);
            createLicenseDevicesTable(conn);
            System.out.println("Database schema initialized successfully");
        } catch (SQLException e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createCustomerKeysTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS customer_keys (
                id SERIAL PRIMARY KEY,
                customer_id VARCHAR(255) UNIQUE NOT NULL,
                public_key TEXT NOT NULL,
                private_key TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private static void createLicensesTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS licenses (
                id SERIAL PRIMARY KEY,
                license_key TEXT UNIQUE NOT NULL,
                customer_id VARCHAR(255) NOT NULL,
                creation_date VARCHAR(50) NOT NULL,
                expiry_date VARCHAR(50) NOT NULL,
                max_devices INTEGER NOT NULL,
                is_active BOOLEAN DEFAULT FALSE,
                license_type VARCHAR(50) DEFAULT 'Chưa kích hoạt',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private static void createLicenseDevicesTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS license_devices (
                id SERIAL PRIMARY KEY,
                license_key TEXT NOT NULL,
                device_name VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (license_key) REFERENCES licenses(license_key) ON DELETE CASCADE
            )
            """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
} 