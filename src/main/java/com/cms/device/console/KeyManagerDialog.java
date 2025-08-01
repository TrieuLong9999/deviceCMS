package com.cms.device.console;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KeyManagerDialog extends JDialog {

    private final Map<String, KeyPair> customerKeyPairs;
    private final Map<String, String> customerNames; // Map Customer ID -> Customer Name
    private final DefaultTableModel tableModel;
    private final JTable keyTable;

    public KeyManagerDialog(Frame owner, Map<String, KeyPair> customerKeyPairs, Map<String, String> customerNames) {
        super(owner, "Quản lý Khách hàng", true);
        this.customerKeyPairs = customerKeyPairs;
        this.customerNames = customerNames;

        setSize(1100, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

        // Table
        String[] columnNames = {"Khách hàng", "Mã khách hàng", "Public Key (rút gọn)", "Thao tác"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // a cell is editable only when it is in the "Thao tác" column
                return column == 3;
            }
        };
        keyTable = new JTable(tableModel);
        keyTable.setRowHeight(40); // Make rows taller to fit buttons
        keyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Bỏ hiệu ứng selection cho toàn bộ table
        keyTable.setSelectionBackground(keyTable.getBackground());
        keyTable.setSelectionForeground(keyTable.getForeground());

        // Set column widths
        keyTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Khách hàng
        keyTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Mã khách hàng
        keyTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Public Key
        keyTable.getColumnModel().getColumn(3).setPreferredWidth(350); // Thao tác (mở rộng cho text dài hơn)

        // Set custom renderer and editor for the "Thao tác" column
        TableColumn actionsColumn = keyTable.getColumn("Thao tác");
        actionsColumn.setCellRenderer(new ActionsPanelRenderer());
        actionsColumn.setCellEditor(new ActionsPanelEditor(keyTable));
        actionsColumn.setMinWidth(350); // Ensure column is wide enough for buttons

        add(new JScrollPane(keyTable), BorderLayout.CENTER);
        refreshTable();

        // Button Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newKeyButton = new JButton("Tạo khách hàng mới");
        actionPanel.add(newKeyButton);
        
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closePanel.add(closeButton);
        
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        bottomPanel.add(closePanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        newKeyButton.addActionListener(e -> createNewKey());
        closeButton.addActionListener(e -> setVisible(false));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        customerKeyPairs.forEach((customerId, keyPair) -> {
            String pubKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String customerName = customerNames.getOrDefault(customerId, "Không có tên");
            tableModel.addRow(new Object[]{
                    customerName, // Khách hàng
                    customerId, // Mã khách hàng
                    pubKey.substring(0, Math.min(pubKey.length(), 60)) + "...", // Public Key
                    customerId // Pass customerId to the action cell
            });
        });
    }

    private void createNewKey() {
        String customerName = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:", "");
        if (customerName == null || customerName.trim().isEmpty()) {
            return;
        }
        
        customerName = customerName.trim();
        
        // Tự động tạo Customer ID duy nhất
        String customerId = generateUniqueCustomerId(customerName);
        
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            
            // Lưu key pair và tên khách hàng
            customerKeyPairs.put(customerId, keyPair);
            customerNames.put(customerId, customerName);
            
            refreshTable();
            JOptionPane.showMessageDialog(this, 
                "✅ Tạo key thành công!\n\n" +
                "🏢 Khách hàng: " + customerName + "\n" +
                "🆔 Mã khách hàng: " + customerId, 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo key: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tạo Customer ID duy nhất dựa trên tên khách hàng
     */
    private String generateUniqueCustomerId(String customerName) {
        // Tạo prefix từ tên khách hàng (lấy chữ cái đầu của mỗi từ)
        String prefix = customerName.toUpperCase()
                .replaceAll("[^A-Z\\s]", "") // Chỉ giữ lại chữ cái và khoảng trắng
                .replaceAll("\\s+", " ") // Chuẩn hóa khoảng trắng
                .trim();
        
        StringBuilder prefixBuilder = new StringBuilder();
        String[] words = prefix.split(" ");
        for (String word : words) {
            if (!word.isEmpty()) {
                prefixBuilder.append(word.charAt(0));
            }
        }
        
        // Nếu prefix quá ngắn, lấy thêm ký tự
        String finalPrefix = prefixBuilder.toString();
        if (finalPrefix.length() < 2 && !prefix.isEmpty()) {
            finalPrefix = prefix.substring(0, Math.min(3, prefix.length())).replaceAll("\\s", "");
        }
        
        // Fallback nếu không có chữ cái hợp lệ
        if (finalPrefix.isEmpty()) {
            finalPrefix = "CUST";
        }
        
        // Thêm timestamp để đảm bảo tính duy nhất
        String timestamp = new SimpleDateFormat("yyMMdd").format(new Date());
        
        // Tạo ID base
        String baseId = finalPrefix + "-" + timestamp;
        
        // Kiểm tra và thêm số thứ tự nếu trùng
        String customerId = baseId;
        int counter = 1;
        while (customerKeyPairs.containsKey(customerId)) {
            customerId = baseId + "-" + String.format("%02d", counter);
            counter++;
        }
        
        return customerId;
    }

    /**
     * Inner class to render a panel of buttons in a table cell.
     */
    class ActionsPanelRenderer extends JPanel implements TableCellRenderer {
        final JButton copyPubButton = new JButton("Sao chép Public Key");
        final JButton copyPrivButton = new JButton("Sao chép Private Key");
        final JButton deleteButton = new JButton("Xóa");

        public ActionsPanelRenderer() {
            super();
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 5)); // Thêm vertical gap
            setAlignmentY(Component.CENTER_ALIGNMENT);
            
            // Đặt font size nhỏ hơn để fit text dài
            Font buttonFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
            copyPubButton.setFont(buttonFont);
            copyPrivButton.setFont(buttonFont);
            deleteButton.setFont(buttonFont);
            
            // Đặt alignment cho các button
            copyPubButton.setAlignmentY(Component.CENTER_ALIGNMENT);
            copyPrivButton.setAlignmentY(Component.CENTER_ALIGNMENT);
            deleteButton.setAlignmentY(Component.CENTER_ALIGNMENT);
            
            add(copyPubButton);
            add(copyPrivButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Bỏ hiệu ứng selection - luôn dùng background mặc định
            setBackground(table.getBackground());
            copyPubButton.setBackground(table.getBackground());
            copyPrivButton.setBackground(table.getBackground());
            deleteButton.setBackground(table.getBackground());
            
            // Đảm bảo panel căn giữa trong cell
            setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            
            return this;
        }
    }

    /**
     * Inner class to edit a cell with a panel of buttons, handling their actions.
     */
    class ActionsPanelEditor extends AbstractCellEditor implements TableCellEditor {
        final JPanel panel = new JPanel();
        final JButton copyPubButton = new JButton("Sao chép Public Key");
        final JButton copyPrivButton = new JButton("Sao chép Private Key");
        final JButton deleteButton = new JButton("Xóa");

        private String currentCustomerId;

        public ActionsPanelEditor(JTable table) {
            // Setup panel layout để căn giữa theo chiều dọc
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 5)); // Thêm vertical gap
            panel.setAlignmentY(Component.CENTER_ALIGNMENT);
            
            // Đặt font size nhỏ hơn để fit text dài
            Font buttonFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
            copyPubButton.setFont(buttonFont);
            copyPrivButton.setFont(buttonFont);
            deleteButton.setFont(buttonFont);
            
            // Đặt alignment cho các button
            copyPubButton.setAlignmentY(Component.CENTER_ALIGNMENT);
            copyPrivButton.setAlignmentY(Component.CENTER_ALIGNMENT);
            deleteButton.setAlignmentY(Component.CENTER_ALIGNMENT);
            
            panel.add(copyPubButton);
            panel.add(copyPrivButton);
            panel.add(deleteButton);

            ActionListener actionListener = e -> {
                // Stop editing before performing the action to commit any pending changes
                // and to prevent issues with the table's state.
                fireEditingStopped();
                
                if (e.getSource() == copyPubButton) {
                    copyKey(true);
                } else if (e.getSource() == copyPrivButton) {
                    copyKey(false);
                } else if (e.getSource() == deleteButton) {
                    deleteSelectedKey();
                }
            };

            copyPubButton.addActionListener(actionListener);
            copyPrivButton.addActionListener(actionListener);
            deleteButton.addActionListener(actionListener);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentCustomerId = (String) value;
            
            // Bỏ hiệu ứng selection - luôn dùng background mặc định
            panel.setBackground(table.getBackground());
            copyPubButton.setBackground(table.getBackground());
            copyPrivButton.setBackground(table.getBackground());
            deleteButton.setBackground(table.getBackground());
            
            // Đảm bảo panel căn giữa trong cell
            panel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentCustomerId;
        }

        private void copyKey(boolean isPublicKey) {
            if (currentCustomerId == null) return;
            KeyPair keyPair = customerKeyPairs.get(currentCustomerId);
            if (keyPair == null) return;

            String keyString;
            String keyType;

            if (isPublicKey) {
                keyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
                keyType = "Public Key";
            } else {
                keyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
                keyType = "Private Key";
            }

            StringSelection stringSelection = new StringSelection(keyString);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            String customerName = customerNames.getOrDefault(currentCustomerId, currentCustomerId);
            JOptionPane.showMessageDialog(KeyManagerDialog.this, keyType + " của '" + customerName + " (" + currentCustomerId + ")' đã được copy vào clipboard.");
        }

        private void deleteSelectedKey() {
            if (currentCustomerId == null) return;

            String customerName = customerNames.getOrDefault(currentCustomerId, currentCustomerId);
            int confirm = JOptionPane.showConfirmDialog(KeyManagerDialog.this,
                    "Bạn có chắc chắn muốn xóa key của:\n\n" +
                    "🏢 Khách hàng: " + customerName + "\n" +
                    "🆔 Mã khách hàng: " + currentCustomerId + "\n\n" +
                    "Thao tác này không thể hoàn tác.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                customerKeyPairs.remove(currentCustomerId);
                customerNames.remove(currentCustomerId); // Xóa luôn tên khách hàng
                refreshTable();
                JOptionPane.showMessageDialog(KeyManagerDialog.this, 
                    "✅ Đã xóa key của '" + customerName + "' thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
} 