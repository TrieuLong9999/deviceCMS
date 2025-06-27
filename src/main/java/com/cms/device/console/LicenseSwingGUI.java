package com.cms.device.console;

import com.formdev.flatlaf.FlatLightLaf;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LicenseSwingGUI extends JFrame {

    // --- Data Management ---
    private final Map<String, Map<String, Object>> allLicenses = new HashMap<>();
    private final Map<String, AtomicInteger> deviceCounts = new HashMap<>();
    private final Map<String, KeyPair> customerKeyPairs = new HashMap<>();
    private Timer licenseCheckTimer;

    // --- UI Components ---
    private final DefaultTableModel licenseTableModel;
    private final JTable licenseTable;
    private final JTextArea logArea;
    private final JButton activateButton, deactivateButton, deleteButton, viewDetailsButton;
    private final JCheckBox periodicCheckToggle;
    private final JTextField searchField;
    private JCheckBox selectAllCheckBox;

    public LicenseSwingGUI() {
        super("Hệ Thống Quản Lý License v2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Top Action Panel ---
        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton manageKeysButton = new JButton("Quản lý Key");
        JButton createLicenseButton = new JButton("Tạo License");
        JButton activateOfflineButton = new JButton("Kích hoạt License Offline");
        JButton activateOnlineButton = new JButton("Kích hoạt License Online");
        topActionPanel.add(manageKeysButton);
        topActionPanel.add(createLicenseButton);
        topActionPanel.add(activateOfflineButton);
        topActionPanel.add(activateOnlineButton);

        // --- License Management Panel ---
        JPanel licensePanel = new JPanel(new BorderLayout(0, 10));
        licensePanel.setBorder(BorderFactory.createTitledBorder("Danh sách License"));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Toolbar for license table ---
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 5));
        JPanel toolbarActions = new JPanel(new FlowLayout(FlowLayout.LEFT));

        activateButton = new JButton("Kích hoạt");
        deactivateButton = new JButton("Vô hiệu hóa");
        deleteButton = new JButton("Xóa License");

        viewDetailsButton = new JButton("Xem Chi tiết");

        toolbarActions.add(activateButton);
        toolbarActions.add(deactivateButton);
        toolbarActions.add(deleteButton);
        toolbarActions.add(viewDetailsButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchField = new JTextField(25);
        searchPanel.add(searchField);

        toolbarPanel.add(toolbarActions, BorderLayout.WEST);
        toolbarPanel.add(searchPanel, BorderLayout.EAST);

        licensePanel.add(toolbarPanel, BorderLayout.NORTH);

        // --- License Table ---
        String[] columnNames = {"", "Trạng thái", "Customer ID", "Ngày tạo", "Ngày hết hạn", "Số thiết bị", "License Key (rút gọn)"};
        licenseTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox column is editable
            }
        };
        licenseTable = new JTable(licenseTableModel);
        setupLicenseTable();

        licensePanel.add(new JScrollPane(licenseTable), BorderLayout.CENTER);

        // --- Log Panel ---
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Nhật ký hoạt động"));
        logArea = new JTextArea(8, 0);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel bottomControls = new JPanel(new BorderLayout());
        periodicCheckToggle = new JCheckBox("Tự động kiểm tra thời hạn (5s)");
        bottomControls.add(periodicCheckToggle, BorderLayout.WEST);
        logPanel.add(bottomControls, BorderLayout.SOUTH);

        // --- Main Layout ---
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, licensePanel, logPanel);
        mainSplit.setResizeWeight(0.75);
        add(mainSplit, BorderLayout.CENTER);
        add(topActionPanel, BorderLayout.NORTH);

        // --- Status Bar with Watermark ---
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        JLabel watermarkLabel = new JLabel("Developed by Lifesup Technology Co., Ltd.");
        watermarkLabel.setForeground(Color.GRAY);
        statusBar.add(watermarkLabel);
        add(statusBar, BorderLayout.SOUTH);

        // --- Button Actions ---
        manageKeysButton.addActionListener(e -> openKeyManager());
        createLicenseButton.addActionListener(e -> generateOfflineLicenseKey());
        activateOfflineButton.addActionListener(e -> activateOfflineLicense());
        activateOnlineButton.addActionListener(e -> activateOnlineLicense());

        activateButton.addActionListener(e -> activateSelectedLicenses());
        deactivateButton.addActionListener(e -> deactivateSelectedLicenses());
        deleteButton.addActionListener(e -> deleteSelectedLicenses());
        viewDetailsButton.addActionListener(e -> showLicenseDetails());

        searchField.addActionListener(e -> refreshLicenseTable());
        periodicCheckToggle.addActionListener(e -> togglePeriodicCheck());

        // --- Initial State ---
        updateActionButtons(0);
        initializeSystem();
    }

    private void setupLicenseTable() {
        licenseTable.setRowHeight(30);
        licenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn checkboxColumn = licenseTable.getColumnModel().getColumn(0);
        checkboxColumn.setMaxWidth(40);
        checkboxColumn.setMinWidth(40);

        licenseTable.getColumnModel().getColumn(1).setMinWidth(120); // Trạng thái
        licenseTable.getColumnModel().getColumn(2).setMinWidth(150); // Customer ID
        licenseTable.getColumnModel().getColumn(3).setMinWidth(160); // Ngày tạo
        licenseTable.getColumnModel().getColumn(4).setMinWidth(160); // Ngày hết hạn
        licenseTable.getColumnModel().getColumn(5).setMinWidth(100); // Số thiết bị

        // --- Select All Checkbox Feature ---
        selectAllCheckBox = new JCheckBox();
        checkboxColumn.setHeaderRenderer((table, value, isSelected, hasFocus, row, column) -> {
            selectAllCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
            return selectAllCheckBox;
        });

        licenseTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (licenseTable.columnAtPoint(e.getPoint()) == 0) {
                    boolean isSelected = !selectAllCheckBox.isSelected();
                    selectAllCheckBox.setSelected(isSelected);
                    for (int i = 0; i < licenseTableModel.getRowCount(); i++) {
                        licenseTableModel.setValueAt(isSelected, i, 0);
                    }
                    licenseTable.getTableHeader().repaint();
                }
            }
        });

        licenseTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 0 && e.getFirstRow() != -1) {
                String key = findFullKeyFromRow(e.getFirstRow());
                if (key != null) {
                    allLicenses.get(key).put("isSelected", licenseTableModel.getValueAt(e.getFirstRow(), 0));
                }
                updateActionButtons(getSelectedLicenseKeys().size());
                updateSelectAllCheckBoxState();
            }
        });

        // Add context menu for right-click copy
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem("Copy License Key");
        popupMenu.add(copyMenuItem);
        licenseTable.setComponentPopupMenu(popupMenu);

        copyMenuItem.addActionListener(e -> {
            int selectedRow = licenseTable.getSelectedRow();
            if (selectedRow != -1) {
                String fullKey = findFullKeyFromRow(selectedRow);
                if (fullKey != null) {
                    StringSelection stringSelection = new StringSelection(fullKey);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    log("✅ Đã copy license key vào clipboard: " + getShortenedKey(fullKey));
                }
            }
        });

        // Ensure row is selected on right-click
        licenseTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int currentRow = licenseTable.rowAtPoint(point);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    licenseTable.setRowSelectionInterval(currentRow, currentRow);
                }
            }
        });
    }

    private void updateActionButtons(int selectedCount) {
        activateButton.setEnabled(selectedCount > 0);
        deactivateButton.setEnabled(selectedCount > 0);
        deleteButton.setEnabled(selectedCount > 0);
        viewDetailsButton.setEnabled(selectedCount == 1); // Only enable for single selection
    }

    private void openKeyManager() {
        KeyManagerDialog keyManagerDialog = new KeyManagerDialog(this, customerKeyPairs);
        keyManagerDialog.setVisible(true);
        log("Quản lý key đã đóng.");
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " - " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void generateOfflineLicenseKey() {
        if (customerKeyPairs.isEmpty()) {
            log("❌ Chưa có Customer ID nào có key. Vui lòng tạo key trong 'Quản lý Key Offline' trước.");
            return;
        }

        Object[] customerIDs = customerKeyPairs.keySet().toArray();
        String selectedCustomerID = (String) JOptionPane.showInputDialog(this, "Chọn Customer ID để tạo license:", "Chọn Customer ID", JOptionPane.QUESTION_MESSAGE, null, customerIDs, customerIDs[0]);
        if (selectedCustomerID == null) {
            log("❌ Thao tác tạo license đã bị hủy.");
            return;
        }

        KeyPair selectedKeyPair = customerKeyPairs.get(selectedCustomerID);

        // --- Create generation dialog with better controls ---
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JSpinner maxDevicesSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));

        // --- Expiry Date Time Picker ---
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JDateChooser expiryDateChooser = new JDateChooser(new Date(System.currentTimeMillis() + 31536000000L)); // Default 1 year
        expiryDateChooser.setDateFormatString("dd/MM/yyyy");
        JSpinner expiryHourSpinner = new JSpinner(new SpinnerNumberModel(23, 0, 23, 1));
        JSpinner expiryMinuteSpinner = new JSpinner(new SpinnerNumberModel(59, 0, 59, 1));
        JSpinner expirySecondSpinner = new JSpinner(new SpinnerNumberModel(59, 0, 59, 1));
        expiryPanel.add(expiryDateChooser);
        expiryPanel.add(new JLabel(" Giờ:"));
        expiryPanel.add(expiryHourSpinner);
        expiryPanel.add(new JLabel(" Phút:"));
        expiryPanel.add(expiryMinuteSpinner);
        expiryPanel.add(new JLabel(" Giây:"));
        expiryPanel.add(expirySecondSpinner);

        JTextArea devicesArea = new JTextArea(5, 30);
        devicesArea.setText("SERVER-01\nPC-01\nPC-02");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Số thiết bị tối đa:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panel.add(maxDevicesSpinner, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Ngày hết hạn:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(expiryPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Danh sách thiết bị (mỗi dòng một thiết bị):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(devicesArea), gbc);

        int option = JOptionPane.showConfirmDialog(this, panel, "Tạo License cho Customer: " + selectedCustomerID, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            log("❌ Thao tác đã bị hủy.");
            return;
        }

        try {
            // Combine Date and Time for expiry
            Calendar cal = Calendar.getInstance();
            cal.setTime(expiryDateChooser.getDate());
            cal.set(Calendar.HOUR_OF_DAY, (Integer) expiryHourSpinner.getValue());
            cal.set(Calendar.MINUTE, (Integer) expiryMinuteSpinner.getValue());
            cal.set(Calendar.SECOND, (Integer) expirySecondSpinner.getValue());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String expiryDateTime = dateFormat.format(cal.getTime());
            String devicesAsString = devicesArea.getText().replace("\n", ",");
            String creationDateTime = dateFormat.format(new Date());

            String licenseInfo = String.format("UUID=%s;CUSTOMER_ID=%s;MAX_DEVICES=%s;EXPIRY=%s;DEVICES=%s",
                    UUID.randomUUID().toString(), selectedCustomerID, maxDevicesSpinner.getValue(), expiryDateTime, devicesAsString);

            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(selectedKeyPair.getPrivate());
            privateSignature.update(licenseInfo.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = privateSignature.sign();
            String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);
            String licenseInfoBase64 = Base64.getEncoder().encodeToString(licenseInfo.getBytes(StandardCharsets.UTF_8));
            String offlineLicenseKey = licenseInfoBase64 + "." + signatureBase64;

            Map<String, Object> licenseData = new HashMap<>();
            licenseData.put("isSelected", false);
            licenseData.put("licenseKey", offlineLicenseKey);
            licenseData.put("customerID", selectedCustomerID);
            licenseData.put("creationDate", creationDateTime);
            licenseData.put("isActive", false);
            licenseData.put("type", "Chưa kích hoạt");
            licenseData.put("maxDevices", maxDevicesSpinner.getValue());
            licenseData.put("expiryDate", expiryDateTime);
            licenseData.put("devices", new ArrayList<>());

            allLicenses.put(offlineLicenseKey, licenseData);
            deviceCounts.put(offlineLicenseKey, new AtomicInteger(0));

            log("✅ Tạo license thành công cho '" + selectedCustomerID + "'. License đã được thêm vào danh sách.");
            refreshLicenseTable();

        } catch (Exception e) {
            log("❌ Lỗi tạo license: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String findFullKeyFromRow(int row) {
        if (row < 0 || row >= licenseTableModel.getRowCount()) return null;
        String shortKey = (String) licenseTableModel.getValueAt(row, licenseTableModel.getColumnCount() - 1);
        return allLicenses.entrySet().stream()
                .filter(entry -> shortKey.equals(getShortenedKey(entry.getKey())))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private List<String> getSelectedLicenseKeys() {
        return allLicenses.values().stream()
                .filter(lic -> (Boolean) lic.getOrDefault("isSelected", false))
                .map(lic -> (String) lic.get("licenseKey"))
                .collect(Collectors.toList());
    }

    private void activateSelectedLicenses() {
        List<String> keysToActivate = getSelectedLicenseKeys();
        if (keysToActivate.isEmpty()) {
            log("🔔 Vui lòng chọn ít nhất một license để kích hoạt.");
            return;
        }

        int activatedCount = 0;
        for (String key : keysToActivate) {
            Map<String, Object> licenseData = allLicenses.get(key);
            if (licenseData == null) continue;

            String currentType = (String) licenseData.get("type");

            if ("Chưa kích hoạt".equals(currentType)) {
                if (verifyAndApplyOfflineLicense(key)) {
                    activatedCount++;
                }
            } else if (!(Boolean) licenseData.get("isActive")) {
                licenseData.put("isActive", true);
                log("✅ License cho '" + licenseData.get("customerID") + "' đã được kích hoạt lại.");
                activatedCount++;
            }
        }

        if (activatedCount > 0) {
            refreshLicenseTable();
        }
        log("✅ Hoàn tất. Đã xử lý kích hoạt " + activatedCount + " license.");
    }

    private boolean verifyAndApplyOfflineLicense(String offlineLicenseKey) {
        if (customerKeyPairs.isEmpty()) {
            log("❌ Không có public key nào để xác thực.");
            return false;
        }

        try {
            int idx = offlineLicenseKey.lastIndexOf(".");
            if (idx == -1) throw new IllegalArgumentException("Định dạng key không hợp lệ.");

            String licenseInfoBase64 = offlineLicenseKey.substring(0, idx);
            String signatureBase64 = offlineLicenseKey.substring(idx + 1);
            byte[] licenseInfoBytes = Base64.getDecoder().decode(licenseInfoBase64);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

            boolean isVerified = false;
            String verifierCustomerID = "Không xác định";

            for (Map.Entry<String, KeyPair> entry : customerKeyPairs.entrySet()) {
                Signature publicSignature = Signature.getInstance("SHA256withRSA");
                publicSignature.initVerify(entry.getValue().getPublic());
                publicSignature.update(licenseInfoBytes);
                if (publicSignature.verify(signatureBytes)) {
                    isVerified = true;
                    verifierCustomerID = entry.getKey();
                    break;
                }
            }

            if (isVerified) {
                String licenseInfoStr = new String(licenseInfoBytes, StandardCharsets.UTF_8);
                Map<String, String> dataMap = Arrays.stream(licenseInfoStr.split(";"))
                        .map(s -> s.split("=", 2)).collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));

                String customerIdFromLicense = dataMap.get("CUSTOMER_ID");
                if (!verifierCustomerID.equals(customerIdFromLicense)) {
                    log("⚠️ Cảnh báo: License được ký bởi '" + verifierCustomerID + "' nhưng dành cho '" + customerIdFromLicense + "'.");
                }

                log("✅ Chữ ký HỢP LỆ (bởi " + verifierCustomerID + "). Kích hoạt license cho " + customerIdFromLicense + ".");

                Map<String, Object> licenseData = allLicenses.get(offlineLicenseKey);
                int maxDevices = Integer.parseInt(dataMap.getOrDefault("MAX_DEVICES", "0"));
                List<String> devices = new ArrayList<>(Arrays.asList(dataMap.getOrDefault("DEVICES", "").split(",")));
                devices.removeIf(d -> d == null || d.trim().isEmpty());

                licenseData.put("isActive", true);
                licenseData.put("type", "offline-verified");
                licenseData.put("maxDevices", maxDevices);
                licenseData.put("expiryDate", dataMap.get("EXPIRY"));
                licenseData.put("devices", devices);
                deviceCounts.get(offlineLicenseKey).set(devices.size());
                return true;
            } else {
                log("❌ Key KHÔNG HỢP LỆ cho " + getShortenedKey(offlineLicenseKey) + ". Không thể xác thực.");
                return false;
            }
        } catch (Exception e) {
            log("❌ Lỗi giải mã: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }

    private void deactivateSelectedLicenses() {
        List<String> keysToDeactivate = getSelectedLicenseKeys();
        if (keysToDeactivate.isEmpty()) {
            log("🔔 Vui lòng chọn ít nhất một license để vô hiệu hóa.");
            return;
        }

        keysToDeactivate.forEach(key -> {
            Map<String, Object> licenseData = allLicenses.get(key);
            if (licenseData != null && (Boolean) licenseData.getOrDefault("isActive", false)) {
                licenseData.put("isActive", false);
                log("☑️ License cho '" + licenseData.get("customerID") + "' đã được vô hiệu hóa.");
            }
        });
        refreshLicenseTable();
    }

    private void deleteSelectedLicenses() {
        List<String> keysToDelete = getSelectedLicenseKeys();
        if (keysToDelete.isEmpty()) {
            log("🔔 Vui lòng chọn ít nhất một license để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa " + keysToDelete.size() + " license đã chọn không?\nThao tác này không thể hoàn tác.", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int deletedCount = 0;
            for (String key : keysToDelete) {
                if (allLicenses.remove(key) != null) {
                    deviceCounts.remove(key);
                    deletedCount++;
                }
            }
            log("✅ Đã xóa " + deletedCount + " license.");
            refreshLicenseTable();
        }
    }

    private String calculateStatus(Map<String, Object> lic) {
        if ("Chưa kích hoạt".equals(lic.get("type"))) {
            return "Chưa kích hoạt";
        }
        if (!(Boolean) lic.get("isActive")) {
            return "Vô hiệu hóa";
        }
        try {
            String expiryDateStr = (String) lic.get("expiryDate");
            if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                Date expiryDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(expiryDateStr);
                if (new Date().after(expiryDate)) {
                    lic.put("isActive", false);
                    return "Hết hạn";
                }
            }
        } catch (Exception e) { /* Ignore */ }
        return "Hoạt động";
    }

    private void refreshLicenseTable() {
        String filterText = searchField.getText().toLowerCase();

        // Persist checkbox state
        for (int i = 0; i < licenseTableModel.getRowCount(); i++) {
            String key = findFullKeyFromRow(i);
            if (key != null) {
                allLicenses.get(key).put("isSelected", licenseTableModel.getValueAt(i, 0));
            }
        }

        licenseTableModel.setRowCount(0);

        allLicenses.values().stream()
                .filter(lic -> matchesFilter(lic, filterText))
                .forEach(lic -> {
                    String status = calculateStatus(lic);
                    List<String> devices = (List<String>) lic.get("devices");
                    int deviceCount = devices != null ? devices.size() : 0;
                    String deviceText = deviceCount + " / " + lic.get("maxDevices");

                    licenseTableModel.addRow(new Object[]{
                            lic.getOrDefault("isSelected", false),
                            status,
                            lic.get("customerID"),
                            lic.get("creationDate"),
                            lic.get("expiryDate"),
                            deviceText,
                            getShortenedKey((String) lic.get("licenseKey"))
                    });
                });
        updateActionButtons(getSelectedLicenseKeys().size());
        updateSelectAllCheckBoxState();
    }

    private boolean matchesFilter(Map<String, Object> lic, String filterText) {
        if (filterText.isEmpty()) return true;
        return ((String) lic.getOrDefault("customerID", "")).toLowerCase().contains(filterText) ||
                ((String) lic.getOrDefault("licenseKey", "")).toLowerCase().contains(filterText);
    }

    private String getShortenedKey(String fullKey) {
        if (fullKey == null || fullKey.length() < 60) return fullKey;
        return fullKey.substring(0, 30) + "..." + fullKey.substring(fullKey.length() - 30);
    }

    private void togglePeriodicCheck() {
        if (periodicCheckToggle.isSelected()) {
            if (licenseCheckTimer != null) licenseCheckTimer.cancel();
            licenseCheckTimer = new Timer();
            licenseCheckTimer.scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        log("⏰ Đang kiểm tra định kỳ tất cả license...");
                        refreshLicenseTable();
                        log("⏰ Hoàn tất kiểm tra.");
                    });
                }
            }, 0, 5000);
            log("✅ Bắt đầu kiểm tra định kỳ.");
        } else {
            if (licenseCheckTimer != null) {
                licenseCheckTimer.cancel();
                licenseCheckTimer = null;
            }
            log("⏹️ Đã dừng kiểm tra định kỳ.");
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (licenseCheckTimer != null) {
            licenseCheckTimer.cancel();
        }
    }

    // This method is now obsolete as activation is handled from the table
    private void decodeOfflineLicenseKey() {
        // Logic will be moved to activateSelectedLicenses
    }

    private void initializeSystem() {
        log("Hệ thống quản lý License v2.0 đã sẵn sàng.");
    }

    private void updateSelectAllCheckBoxState() {
        if (licenseTableModel.getRowCount() == 0) {
            selectAllCheckBox.setSelected(false);
            return;
        }
        boolean allSelected = true;
        for (int i = 0; i < licenseTableModel.getRowCount(); i++) {
            if (!(Boolean) licenseTableModel.getValueAt(i, 0)) {
                allSelected = false;
                break;
            }
        }
        selectAllCheckBox.setSelected(allSelected);
    }

    private void showLicenseDetails() {
        List<String> selectedKeys = getSelectedLicenseKeys();
        if (selectedKeys.size() != 1) {
            log("🔔 Vui lòng chọn một license duy nhất để xem chi tiết.");
            return;
        }

        String key = selectedKeys.get(0);
        Map<String, Object> lic = allLicenses.get(key);
        if (lic == null) {
            log("❌ Lỗi: Không tìm thấy thông tin license.");
            return;
        }

        LicenseDetailDialog dialog = new LicenseDetailDialog(this, key, lic);
        dialog.setVisible(true);

        refreshLicenseTable();
    }

    private void activateOfflineLicense() {
        String licenseKeyStr = JOptionPane.showInputDialog(this, "Vui lòng nhập License Key để kích hoạt:", "Kích hoạt License Offline", JOptionPane.PLAIN_MESSAGE);

        if (licenseKeyStr == null || licenseKeyStr.trim().isEmpty()) {
            log("ℹ️ Thao tác kích hoạt offline đã bị hủy.");
            return;
        }

        licenseKeyStr = licenseKeyStr.trim();

        if (customerKeyPairs.isEmpty()) {
            log("❌ Không có Public Key nào trong hệ thống để xác thực. Vui lòng thêm key trong 'Quản lý Key'.");
            JOptionPane.showMessageDialog(this, "Không có Public Key nào trong hệ thống để xác thực.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log("🔄 Đang duyệt qua các key đã lưu để xác thực license...");

        try {
            int idx = licenseKeyStr.lastIndexOf(".");
            if (idx == -1) throw new IllegalArgumentException("Định dạng license key không hợp lệ.");

            String licenseInfoBase64 = licenseKeyStr.substring(0, idx);
            String signatureBase64 = licenseKeyStr.substring(idx + 1);
            byte[] licenseInfoBytes = Base64.getDecoder().decode(licenseInfoBase64);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

            boolean isVerified = false;
            String verifierCustomerID = null;

            // Duyệt qua tất cả các public key đã biết
            for (Map.Entry<String, KeyPair> entry : customerKeyPairs.entrySet()) {
                PublicKey publicKey = entry.getValue().getPublic();
                Signature publicSignature = Signature.getInstance("SHA256withRSA");
                publicSignature.initVerify(publicKey);
                publicSignature.update(licenseInfoBytes);
                if (publicSignature.verify(signatureBytes)) {
                    isVerified = true;
                    verifierCustomerID = entry.getKey();
                    break; // Tìm thấy key hợp lệ, thoát vòng lặp
                }
            }

            if (isVerified) {
                log("✅ Chữ ký hợp lệ! Xác thực thành công bằng key của Customer ID: " + verifierCustomerID);

                String licenseInfoStr = new String(licenseInfoBytes, StandardCharsets.UTF_8);
                Map<String, String> dataMap = Arrays.stream(licenseInfoStr.split(";"))
                        .map(s -> s.split("=", 2)).collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));

                String customerIdFromLicense = dataMap.get("CUSTOMER_ID");
                if (!verifierCustomerID.equals(customerIdFromLicense)) {
                    log("⚠️ Cảnh báo: License được ký bởi '" + verifierCustomerID + "' nhưng dữ liệu trong license lại cho '" + customerIdFromLicense + "'.");
                }

                // Thêm license vào hệ thống
                Map<String, Object> licenseData = new HashMap<>();
                int maxDevices = Integer.parseInt(dataMap.getOrDefault("MAX_DEVICES", "0"));
                List<String> devices = new ArrayList<>(Arrays.asList(dataMap.getOrDefault("DEVICES", "").split(",")));
                devices.removeIf(d -> d == null || d.trim().isEmpty());

                licenseData.put("isSelected", false);
                licenseData.put("licenseKey", licenseKeyStr);
                licenseData.put("customerID", customerIdFromLicense);
                licenseData.put("creationDate", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                licenseData.put("isActive", true);
                licenseData.put("type", "offline-verified");
                licenseData.put("maxDevices", maxDevices);
                licenseData.put("expiryDate", dataMap.get("EXPIRY"));
                licenseData.put("devices", devices);

                allLicenses.put(licenseKeyStr, licenseData);
                deviceCounts.put(licenseKeyStr, new AtomicInteger(devices.size()));

                refreshLicenseTable();
                log("✅ Kích hoạt thành công license cho: " + customerIdFromLicense);
                JOptionPane.showMessageDialog(this, "License đã được kích hoạt thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                log("❌ Không tìm thấy Public Key phù hợp. License không hợp lệ hoặc không thuộc hệ thống quản lý.");
                JOptionPane.showMessageDialog(this, "License Key không hợp lệ hoặc không có Public Key phù hợp trong hệ thống.", "Lỗi Xác Thực", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            log("❌ Lỗi kích hoạt offline: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình kích hoạt: \n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void activateOnlineLicense() {
        String licenseKeyStr = JOptionPane.showInputDialog(this, "Vui lòng nhập License Key để kích hoạt:", "Kích hoạt License Online", JOptionPane.PLAIN_MESSAGE);

        if (licenseKeyStr == null || licenseKeyStr.trim().isEmpty()) {
            log("ℹ️ Thao tác kích hoạt online đã bị hủy.");
            return;
        }

        licenseKeyStr = licenseKeyStr.trim();

        // For now, online activation will just use the local key store for verification
        // This simulates checking against a "central" repository of public keys
        if (customerKeyPairs.isEmpty()) {
            log("❌ Không có Public Key nào trong hệ thống để xác thực. Vui lòng thêm key trong 'Quản lý Key'.");
            JOptionPane.showMessageDialog(this, "Không có Public Key nào trong hệ thống để xác thực.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log("🔄 Đang thực hiện xác thực online (giả lập)...");

        try {
            int idx = licenseKeyStr.lastIndexOf(".");
            if (idx == -1) throw new IllegalArgumentException("Định dạng license key không hợp lệ.");

            String licenseInfoBase64 = licenseKeyStr.substring(0, idx);
            String signatureBase64 = licenseKeyStr.substring(idx + 1);
            byte[] licenseInfoBytes = Base64.getDecoder().decode(licenseInfoBase64);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

            boolean isVerified = false;
            String verifierCustomerID = null;

            // The logic remains the same: iterate through known keys to find a match.
            for (Map.Entry<String, KeyPair> entry : customerKeyPairs.entrySet()) {
                PublicKey publicKey = entry.getValue().getPublic();
                Signature publicSignature = Signature.getInstance("SHA256withRSA");
                publicSignature.initVerify(publicKey);
                publicSignature.update(licenseInfoBytes);
                if (publicSignature.verify(signatureBytes)) {
                    isVerified = true;
                    verifierCustomerID = entry.getKey();
                    break;
                }
            }

            if (isVerified) {
                log("✅ Chữ ký hợp lệ! Xác thực thành công bằng key của Customer ID: " + verifierCustomerID);

                String licenseInfoStr = new String(licenseInfoBytes, StandardCharsets.UTF_8);
                Map<String, String> dataMap = Arrays.stream(licenseInfoStr.split(";"))
                        .map(s -> s.split("=", 2)).collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));

                String customerIdFromLicense = dataMap.get("CUSTOMER_ID");
                if (!verifierCustomerID.equals(customerIdFromLicense)) {
                    log("⚠️ Cảnh báo: License được ký bởi '" + verifierCustomerID + "' nhưng dữ liệu trong license lại cho '" + customerIdFromLicense + "'.");
                }

                // Add license to the system
                Map<String, Object> licenseData = new HashMap<>();
                int maxDevices = Integer.parseInt(dataMap.getOrDefault("MAX_DEVICES", "0"));
                List<String> devices = new ArrayList<>(Arrays.asList(dataMap.getOrDefault("DEVICES", "").split(",")));
                devices.removeIf(d -> d == null || d.trim().isEmpty());

                licenseData.put("isSelected", false);
                licenseData.put("licenseKey", licenseKeyStr);
                licenseData.put("customerID", customerIdFromLicense);
                licenseData.put("creationDate", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                licenseData.put("isActive", true);
                licenseData.put("type", "online-verified"); // Mark as online
                licenseData.put("maxDevices", maxDevices);
                licenseData.put("expiryDate", dataMap.get("EXPIRY"));
                licenseData.put("devices", devices);

                allLicenses.put(licenseKeyStr, licenseData);
                deviceCounts.put(licenseKeyStr, new AtomicInteger(devices.size()));

                refreshLicenseTable();
                log("✅ Kích hoạt online thành công license cho: " + customerIdFromLicense);
                JOptionPane.showMessageDialog(this, "License đã được kích hoạt online thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                log("❌ Không tìm thấy Public Key phù hợp. License không hợp lệ hoặc không thuộc hệ thống quản lý.");
                JOptionPane.showMessageDialog(this, "License Key không hợp lệ hoặc không có Public Key phù hợp trong hệ thống.", "Lỗi Xác Thực", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            log("❌ Lỗi kích hoạt online: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình kích hoạt: \n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- Inner class for managing license details and devices ---
    private static class LicenseDetailDialog extends JDialog {
        private final LicenseSwingGUI owner;
        private final Map<String, Object> licenseData;
        private final DefaultListModel<String> deviceListModel;
        private final JList<String> deviceList;
        private final JButton editButton;
        private final JButton deleteButton;
        private final int maxDevices;

        public LicenseDetailDialog(LicenseSwingGUI owner, String licenseKey, Map<String, Object> licenseData) {
            super(owner, "Chi tiết License & Quản lý thiết bị", true);
            this.owner = owner;
            this.licenseData = licenseData;
            this.maxDevices = (int) licenseData.get("maxDevices");

            setSize(600, 500);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));
            ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            // --- Details Panel ---
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chung"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;

            Consumer<Component[]> addRow = (components) -> {
                gbc.gridy++;
                gbc.gridx = 0;
                gbc.weightx = 0;
                gbc.fill = GridBagConstraints.NONE;
                detailsPanel.add(components[0], gbc);
                gbc.gridx = 1;
                gbc.weightx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                detailsPanel.add(components[1], gbc);
            };

            addRow.accept(new Component[]{new JLabel("Customer ID:"), new JTextField((String) licenseData.get("customerID")) {{
                setEditable(false);
            }}});
            addRow.accept(new Component[]{new JLabel("Trạng thái:"), new JTextField(owner.calculateStatus(licenseData)) {{
                setEditable(false);
            }}});
            addRow.accept(new Component[]{new JLabel("Ngày hết hạn:"), new JTextField((String) licenseData.get("expiryDate")) {{
                setEditable(false);
            }}});
            JTextArea keyArea = new JTextArea(licenseKey, 4, 1);
            keyArea.setWrapStyleWord(true);
            keyArea.setLineWrap(true);
            keyArea.setEditable(false);
            addRow.accept(new Component[]{new JLabel("License Key:"), new JScrollPane(keyArea)});

            // --- Device Management Panel ---
            JPanel devicePanel = new JPanel(new BorderLayout(0, 5));
            devicePanel.setBorder(BorderFactory.createTitledBorder("Danh sách thiết bị"));

            List<String> devices = (List<String>) licenseData.getOrDefault("devices", new ArrayList<String>());
            deviceListModel = new DefaultListModel<>();
            devices.forEach(deviceListModel::addElement);
            deviceList = new JList<>(deviceListModel);
            devicePanel.add(new JScrollPane(deviceList), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton addButton = new JButton("Thêm");
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            devicePanel.add(buttonPanel, BorderLayout.SOUTH);

            add(detailsPanel, BorderLayout.NORTH);
            add(devicePanel, BorderLayout.CENTER);

            // --- Actions ---
            addButton.addActionListener(e -> addDevice());
            editButton.addActionListener(e -> editDevice());
            deleteButton.addActionListener(e -> deleteDevice());
            deviceList.addListSelectionListener(e -> updateButtons());

            updateButtons();
        }

        private void updateButtons() {
            int selectedIndex = deviceList.getSelectedIndex();
            editButton.setEnabled(selectedIndex != -1);
            deleteButton.setEnabled(selectedIndex != -1);
        }

        private void addDevice() {
            if (deviceListModel.getSize() >= maxDevices) {
                JOptionPane.showMessageDialog(this, "Đã đạt giới hạn thiết bị tối đa (" + maxDevices + ").", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newDevice = JOptionPane.showInputDialog(this, "Nhập tên thiết bị mới:", "Thêm Thiết Bị", JOptionPane.PLAIN_MESSAGE);
            if (newDevice != null && !newDevice.trim().isEmpty()) {
                newDevice = newDevice.trim();
                deviceListModel.addElement(newDevice);
                ((List<String>) licenseData.get("devices")).add(newDevice);
                owner.log("✅ Đã thêm thiết bị '" + newDevice + "'.");
                owner.refreshLicenseTable();
            }
        }

        private void editDevice() {
            int selectedIndex = deviceList.getSelectedIndex();
            String currentDevice = deviceListModel.getElementAt(selectedIndex);

            String newDevice = (String) JOptionPane.showInputDialog(this, "Sửa tên thiết bị:", "Sửa Thiết Bị", JOptionPane.PLAIN_MESSAGE, null, null, currentDevice);
            if (newDevice != null && !newDevice.trim().isEmpty()) {
                newDevice = newDevice.trim();
                deviceListModel.set(selectedIndex, newDevice);
                ((List<String>) licenseData.get("devices")).set(selectedIndex, newDevice);
                owner.log("✅ Đã sửa thiết bị '" + currentDevice + "' -> '" + newDevice + "'.");
            }
        }

        private void deleteDevice() {
            int selectedIndex = deviceList.getSelectedIndex();
            String deviceToDelete = deviceListModel.getElementAt(selectedIndex);

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thiết bị '" + deviceToDelete + "'?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deviceListModel.remove(selectedIndex);
                ((List<String>) licenseData.get("devices")).remove(deviceToDelete);
                owner.log("✅ Đã xóa thiết bị '" + deviceToDelete + "'.");
                owner.refreshLicenseTable();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
            }
            new LicenseSwingGUI().setVisible(true);
        });
    }
}