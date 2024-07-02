import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InventoryManagementSystem {
    private static final int MAX_PRODUCTS = 100;
    private static final int MAX_SUPPLIERS = 100;
    private static Product[] products = new Product[MAX_PRODUCTS];
    private static Supplier[] suppliers = new Supplier[MAX_SUPPLIERS];
    private static int productCount = 0;
    private static int supplierCount = 0;

    private static DefaultTableModel productTableModel;
    private static DefaultTableModel supplierTableModel;
    private static DefaultComboBoxModel<String> supplierComboBoxModel;

    private static JTable productTable;

    public static void main(String[] args) {
        JFrame frame = new JFrame("NK Inventory Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ImageIcon icon = createScaledImageIcon("logo.jpeg", 50, 50);
        frame.setIconImage(icon.getImage());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBackground(Color.WHITE); 
        imageLabel.setOpaque(true); 
        titlePanel.add(imageLabel);

        JLabel titleLabel = new JLabel("NK Inventory Management System");
        titlePanel.add(titleLabel);

        frame.add(titlePanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel productPanel = createProductPanel();
        JPanel supplierPanel = createSupplierPanel();

        tabbedPane.addTab("Products", productPanel);
        tabbedPane.addTab("Suppliers", supplierPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);

        frame.setVisible(true);

        loadSuppliersFromFile("supplier.txt");
        loadProductsFromFile("product.txt");

        updateProductTable();
        updateSupplierTable();
    }

    private static JPanel createProductPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        JTextField productIdField = new JTextField(20);
        JTextField nameField = new JTextField(20);

        supplierComboBoxModel = new DefaultComboBoxModel<>();
        JComboBox<String> supplierComboBox = new JComboBox<>(supplierComboBoxModel);

        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);

        formPanel.add(new JLabel("Product ID:"));
        formPanel.add(productIdField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierComboBox);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);

        JButton addButton = new JButton("Add Product");

        formPanel.add(addButton);
        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"Product ID", "Name", "Supplier Name", "Price", "Quantity"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton reportButton = new JButton("Generate Report");
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(reportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
                    String productId = productIdField.getText();
                    String name = nameField.getText();
                    String supplierName = (String) supplierComboBox.getSelectedItem();
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());

                    boolean isDuplicate = false;
                    for (int i = 0; i < productCount; i++) {
                        if (products[i].getProductId().equals(productId)) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (isDuplicate) {
                        JOptionPane.showMessageDialog(panel, "Product ID already exists. Please use a different ID.");
                    } else {
                        if (productCount < MAX_PRODUCTS) {
                            products[productCount++] = new Product(productId, name, supplierName, price, quantity);
                            updateProductTable();
                            saveProductsToFile("product.txt");

                            productIdField.setText("");
                            nameField.setText("");
                            supplierComboBox.setSelectedIndex(0); 
                            priceField.setText("");
                            quantityField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(panel, "Maximum product limit reached.");
                        }
                    }
            });

        updateButton.addActionListener(e -> {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String productId = (String) productTableModel.getValueAt(selectedRow, 0);
                        String name = (String) productTableModel.getValueAt(selectedRow, 1);
                        String supplierName = (String) productTableModel.getValueAt(selectedRow, 2);
                        double price = Double.parseDouble(productTableModel.getValueAt(selectedRow, 3).toString());
                        int quantity = Integer.parseInt(productTableModel.getValueAt(selectedRow, 4).toString());

                        showUpdateProductDialog(productId, name, supplierName, price, quantity, selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Please select a product to update.");
                    }
            });

        deleteButton.addActionListener(e -> {
                    int[] selectedRows = productTable.getSelectedRows();
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        int rowIndex = selectedRows[i];
                        System.arraycopy(products, rowIndex + 1, products, rowIndex, productCount - rowIndex - 1);
                        productCount--;
                    }
                    updateProductTable();
                    saveProductsToFile("product.txt");
            });

        reportButton.addActionListener(e -> {
                    generateReport();
            });

        return panel;
    }

    private static void showUpdateProductDialog(String productId, String name, String supplierName, double price, int quantity, int rowIndex) {
        JFrame updateFrame = new JFrame("Update Product");
        updateFrame.setSize(400, 300);
        updateFrame.setLayout(new GridLayout(6, 2));

        JTextField productIdField = new JTextField(productId);
        JTextField nameField = new JTextField(name);
        JComboBox<String> supplierComboBox = new JComboBox<>(supplierComboBoxModel);
        supplierComboBox.setSelectedItem(supplierName);
        JTextField priceField = new JTextField(String.valueOf(price));
        JTextField quantityField = new JTextField(String.valueOf(quantity));

        updateFrame.add(new JLabel("Product ID:"));
        updateFrame.add(productIdField);
        updateFrame.add(new JLabel("Name:"));
        updateFrame.add(nameField);
        updateFrame.add(new JLabel("Supplier Name:"));
        updateFrame.add(supplierComboBox);
        updateFrame.add(new JLabel("Price:"));
        updateFrame.add(priceField);
        updateFrame.add(new JLabel("Quantity:"));
        updateFrame.add(quantityField);

        JButton updateButton = new JButton("Update");
        updateFrame.add(updateButton);

        updateButton.addActionListener(e -> {
                    products[rowIndex] = new Product(productIdField.getText(), nameField.getText(),
                        (String) supplierComboBox.getSelectedItem(), Double.parseDouble(priceField.getText()),
                        Integer.parseInt(quantityField.getText()));

                    updateProductTable();
                    saveProductsToFile("product.txt");
                    updateFrame.dispose();
            });

        updateFrame.setVisible(true);
    }

    private static JPanel createSupplierPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JTextField supplierIdField = new JTextField(20);
        JTextField supplierNameField = new JTextField(20);

        formPanel.add(new JLabel("Supplier ID:"));
        formPanel.add(supplierIdField);
        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierNameField);

        JButton addButton = new JButton("Add Supplier");
        formPanel.add(addButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"Supplier ID", "Supplier Name"};
        supplierTableModel = new DefaultTableModel(columnNames, 0);
        JTable supplierTable = new JTable(supplierTableModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
                    String supplierId = supplierIdField.getText();
                    String supplierName = supplierNameField.getText();

                    boolean isDuplicate = false;
                    for (int i = 0; i < supplierCount; i++) {
                        if (suppliers[i].getSupplierName().equals(supplierName)) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (isDuplicate) {
                        JOptionPane.showMessageDialog(panel, "Supplier Name already exists. Please use a different name.");
                    } else {
                        if (supplierCount < MAX_SUPPLIERS) {
                            suppliers[supplierCount++] = new Supplier(supplierId, supplierName);
                            updateSupplierTable();
                            saveSuppliersToFile("supplier.txt");

                            supplierIdField.setText("");
                            supplierNameField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(panel, "Maximum supplier limit reached.");
                        }
                    }
            });

        updateButton.addActionListener(e -> {
                    int selectedRow = supplierTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String supplierId = (String) supplierTableModel.getValueAt(selectedRow, 0);
                        String supplierName = (String) supplierTableModel.getValueAt(selectedRow, 1);

                        showUpdateSupplierDialog(supplierId, supplierName, selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Please select a supplier to update.");
                    }
            });

        deleteButton.addActionListener(e -> {
                    int[] selectedRows = supplierTable.getSelectedRows();
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        int rowIndex = selectedRows[i];
                        System.arraycopy(suppliers, rowIndex + 1, suppliers, rowIndex, supplierCount - rowIndex - 1);
                        supplierCount--;
                    }
                    updateSupplierTable();
                    saveSuppliersToFile("supplier.txt");
            });

        return panel;
    }

    private static void showUpdateSupplierDialog(String supplierId, String supplierName, int rowIndex) {
        JFrame updateFrame = new JFrame("Update Supplier");
        updateFrame.setSize(300, 200);
        updateFrame.setLayout(new GridLayout(3, 2));

        JTextField supplierIdField = new JTextField(supplierId);
        JTextField supplierNameField = new JTextField(supplierName);

        updateFrame.add(new JLabel("Supplier ID:"));
        updateFrame.add(supplierIdField);
        updateFrame.add(new JLabel("Supplier Name:"));
        updateFrame.add(supplierNameField);

        JButton updateButton = new JButton("Update");
        updateFrame.add(updateButton);

        updateButton.addActionListener(e -> {
                    suppliers[rowIndex] = new Supplier(supplierIdField.getText(), supplierNameField.getText());

                    updateSupplierTable();
                    saveSuppliersToFile("supplier.txt");
                    updateFrame.dispose();
            });

        updateFrame.setVisible(true);
    }

    private static void updateProductTable() {
        productTableModel.setRowCount(0);

        for (int i = 0; i < productCount; i++) {
            Product product = products[i];
            Object[] rowData = new Object[]{
                    product.getProductId(),
                    product.getName(),
                    product.getSupplierName(), 
                    product.getPrice(),
                    product.getQuantity()
                };
            productTableModel.addRow(rowData);
        }

        productTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    int quantity = (int) value;
                    if (quantity < 10) {
                        rendererComp.setBackground(Color.RED);
                    } else {
                        rendererComp.setBackground(Color.WHITE);
                    }
                    return rendererComp;
                }
            });

        productTable.repaint();
    }

    private static void updateSupplierTable() {
        supplierTableModel.setRowCount(0);
        supplierComboBoxModel.removeAllElements(); 
        for (int i = 0; i < supplierCount; i++) {
            Supplier supplier = suppliers[i];
            String[] data = {supplier.getSupplierId(), supplier.getSupplierName()};
            supplierTableModel.addRow(data);
            supplierComboBoxModel.addElement(supplier.getSupplierName());
        }
    }

    private static void loadProductsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    products[productCount++] = new Product(parts[0], parts[1], parts[2],
                        Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSuppliersFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    suppliers[supplierCount++] = new Supplier(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveProductsToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < productCount; i++) {
                Product product = products[i];
                writer.write(product.getProductId() + "," + product.getName() + "," +
                    product.getSupplierName() + "," + product.getPrice() + "," + product.getQuantity() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveSuppliersToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < supplierCount; i++) {
                Supplier supplier = suppliers[i];
                writer.write(supplier.getSupplierId() + "," + supplier.getSupplierName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateReport() {
        Product[] belowTen = new Product[productCount];
        Product[] aboveTen = new Product[productCount];
        int belowIndex = 0;
        int aboveIndex = 0;
        for (int i = 0; i < productCount; i++) {
            Product product = products[i];
            if (product.getQuantity() < 10) {
                belowTen[belowIndex++] = product;
            } else {
                aboveTen[aboveIndex++] = product;
            }
        }

        for (int i = 0; i < belowIndex - 1; i++) {
            for (int j = 0; j < belowIndex - i - 1; j++) {
                if (belowTen[j].getQuantity() > belowTen[j + 1].getQuantity()) {
                    Product temp = belowTen[j];
                    belowTen[j] = belowTen[j + 1];
                    belowTen[j + 1] = temp;
                }
            }
        }

        for (int i = 0; i < aboveIndex - 1; i++) {
            for (int j = 0; j < aboveIndex - i - 1; j++) {
                if (aboveTen[j].getQuantity() > aboveTen[j + 1].getQuantity()) {
                    Product temp = aboveTen[j];
                    aboveTen[j] = aboveTen[j + 1];
                    aboveTen[j + 1] = temp;
                }
            }
        }

        try (FileWriter writer = new FileWriter("report.txt")) {
            writer.write("Products with Quantity Below 10:\n" +
            "---------------------------------------------------\n");
            for (int i = 0; i < belowIndex; i++) {
                Product product = belowTen[i];
                if (product != null) {
                    writer.write(product.getProductId() + "," + product.getName() + "," +
                        product.getSupplierName() + "," + product.getPrice() + "," + product.getQuantity() + "\n");
                }
            }

            writer.write("\nProducts with Quantity 10 and Above:\n" +
            "---------------------------------------------------\n");
            for (int i = 0; i < aboveIndex; i++) {
                Product product = aboveTen[i];
                if (product != null) {
                    writer.write(product.getProductId() + "," + product.getName() + "," +
                        product.getSupplierName() + "," + product.getPrice() + "," + product.getQuantity() + "\n");
                }
            }

            JOptionPane.showMessageDialog(null, "Report generated successfully. Check report.txt");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to generate report.");
        }
    }

    private static ImageIcon createScaledImageIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
