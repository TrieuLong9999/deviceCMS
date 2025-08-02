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
import java.util.Base64;
import java.util.Map;

public class KeyManagerDialog extends JDialog {

    private final DefaultTableModel tableModel;
    private final JTable keyTable;

    public KeyManagerDialog(Frame owner) {
        super(owner, "Quản lý Offline Key", true);

        setSize(850, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

        // Table
        String[] columnNames = {"Customer ID", "Public Key (rút gọn)", "Thao tác"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // a cell is editable only when it is in the "Thao tác" column
                return column == 2;
            }
        };
        keyTable = new JTable(tableModel);
        keyTable.setRowHeight(40); // Make rows taller to fit buttons
        keyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set custom renderer and editor for the "Thao tác" column
        TableColumn actionsColumn = keyTable.getColumn("Thao tác");
        actionsColumn.setCellRenderer(new ActionsPanelRenderer());
        actionsColumn.setCellEditor(new ActionsPanelEditor(keyTable));
        actionsColumn.setMinWidth(280); // Ensure column is wide enough for buttons

        add(new JScrollPane(keyTable), BorderLayout.CENTER);
        refreshTable();

        // Button Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newKeyButton = new JButton("Tạo Key Mới");
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
        Map<String, KeyPair> customerKeyPairs = CustomerKeyDAO.loadAllCustomerKeys();
        customerKeyPairs.forEach((customerId, keyPair) -> {
            String pubKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            tableModel.addRow(new Object[]{
                    customerId,
                    pubKey.substring(0, Math.min(pubKey.length(), 60)) + "...",
                    customerId // Pass customerId to the action cell
            });
        });
    }

    private void createNewKey() {
        String customerId = JOptionPane.showInputDialog(this, "Nhập Customer ID (duy nhất):", "customer-" + (CustomerKeyDAO.loadAllCustomerKeys().size() + 1));
        if (customerId == null || customerId.trim().isEmpty()) {
            return;
        }
        if (CustomerKeyDAO.customerKeyExists(customerId.trim())) {
            JOptionPane.showMessageDialog(this, "Customer ID đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            
            if (CustomerKeyDAO.saveCustomerKey(customerId.trim(), keyPair)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Tạo key thành công cho: " + customerId.trim(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi lưu key vào database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo key: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inner class to render a panel of buttons in a table cell.
     */
    class ActionsPanelRenderer extends JPanel implements TableCellRenderer {
        final JButton copyPubButton = new JButton("Copy Pub");
        final JButton copyPrivButton = new JButton("Copy Priv");
        final JButton deleteButton = new JButton("Xóa");

        public ActionsPanelRenderer() {
            super(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(copyPubButton);
            add(copyPrivButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    /**
     * Inner class to edit a cell with a panel of buttons, handling their actions.
     */
    class ActionsPanelEditor extends AbstractCellEditor implements TableCellEditor {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        final JButton copyPubButton = new JButton("Copy Pub");
        final JButton copyPrivButton = new JButton("Copy Priv");
        final JButton deleteButton = new JButton("Xóa");

        private String currentCustomerId;

        public ActionsPanelEditor(JTable table) {
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
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentCustomerId;
        }

        private void copyKey(boolean isPublicKey) {
            if (currentCustomerId == null) return;
            Map<String, KeyPair> customerKeyPairs = CustomerKeyDAO.loadAllCustomerKeys();
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
            JOptionPane.showMessageDialog(KeyManagerDialog.this, keyType + " của '" + currentCustomerId + "' đã được copy vào clipboard.");
        }

        private void deleteSelectedKey() {
            if (currentCustomerId == null) return;

            int confirm = JOptionPane.showConfirmDialog(KeyManagerDialog.this,
                    "Bạn có chắc chắn muốn xóa key của '" + currentCustomerId + "' không?\nThao tác này không thể hoàn tác.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (CustomerKeyDAO.deleteCustomerKey(currentCustomerId)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(KeyManagerDialog.this, "Đã xóa key thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(KeyManagerDialog.this, "Lỗi xóa key từ database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
} 