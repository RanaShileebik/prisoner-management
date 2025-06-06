import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PrisonerGUI extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    private JTextField txtId, txtName, txtAge, txtCrime;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch;

    public PrisonerGUI() {
        setTitle("Prisoner Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents(); // تهيئة مكونات الواجهة
        loadPrisoners();  // تحميل البيانات من قاعدة البيانات للعرض

        pack();
    }

    private void initComponents() {
        // جدول البيانات
        model = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Crime"}, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowSelectionAllowed(true);

        // حقول الإدخال مع تسميات
        txtId = new JTextField(10);
        txtName = new JTextField(15);
        txtAge = new JTextField(5);
        txtCrime = new JTextField(15);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblId = new JLabel("ID:");
        JLabel lblName = new JLabel("Name:");
        JLabel lblAge = new JLabel("Age:");
        JLabel lblCrime = new JLabel("Crime:");

        lblId.setFont(labelFont);
        lblName.setFont(labelFont);
        lblAge.setFont(labelFont);
        lblCrime.setFont(labelFont);

        // تنظيم الحقول باستخدام GridBagLayout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(lblId, gbc);
        gbc.gridx = 1;
        inputPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(lblName, gbc);
        gbc.gridx = 1;
        inputPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(lblAge, gbc);
        gbc.gridx = 1;
        inputPanel.add(txtAge, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(lblCrime, gbc);
        gbc.gridx = 1;
        inputPanel.add(txtCrime, gbc);

        // أزرار التحكم
        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        btnSearch = new JButton("Search");

        Dimension btnSize = new Dimension(90, 30);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnSearch.setPreferredSize(btnSize);

        btnDelete.setForeground(Color.RED);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);

        // إضافة كل شيء للنافذة
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(800, 500));

        // أحداث الأزرار - نفس اللي عندك سابقًا:

        btnAdd.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText());
                String crime = txtCrime.getText();

                String sql = "INSERT INTO prisoners (id, name, age, crime) VALUES (?, ?, ?, ?)";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);
                    pstmt.setString(2, name);
                    pstmt.setInt(3, age);
                    pstmt.setString(4, crime);

                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Prisoner added successfully!");
                        model.addRow(new Object[]{id, name, age, crime});
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add prisoner.");
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID and Age must be numbers!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a prisoner to edit.");
                return;
            }

            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText());
                String crime = txtCrime.getText();

                String sql = "UPDATE prisoners SET name=?, age=?, crime=? WHERE id=?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, name);
                    pstmt.setInt(2, age);
                    pstmt.setString(3, crime);
                    pstmt.setInt(4, id);

                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Prisoner updated successfully!");
                        model.setValueAt(id, selectedRow, 0);
                        model.setValueAt(name, selectedRow, 1);
                        model.setValueAt(age, selectedRow, 2);
                        model.setValueAt(crime, selectedRow, 3);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update prisoner.");
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID and Age must be numbers!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a prisoner to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this prisoner?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                int id = (int) model.getValueAt(selectedRow, 0);

                String sql = "DELETE FROM prisoners WHERE id=?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);

                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Prisoner deleted successfully!");
                        model.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete prisoner.");
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        btnSearch.addActionListener(e -> {
            String idText = txtId.getText().trim();
            String nameText = txtName.getText().trim();

            if (idText.isEmpty() && nameText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter ID or Name to search.");
                return;
            }

            model.setRowCount(0);

            String sql;
            boolean searchById = !idText.isEmpty();

            if (searchById) {
                sql = "SELECT id, name, age, crime FROM prisoners WHERE id = ?";
            } else {
                sql = "SELECT id, name, age, crime FROM prisoners WHERE name LIKE ?";
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                if (searchById) {
                    pstmt.setInt(1, Integer.parseInt(idText));
                } else {
                    pstmt.setString(1, "%" + nameText + "%");
                }

                ResultSet rs = pstmt.executeQuery();

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("crime")
                    });
                }

                if (!found) {
                    JOptionPane.showMessageDialog(this, "No prisoners found.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID must be a number!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        // عرض بيانات الصف المحدد في الحقول
        ListSelectionListener selectionListener = e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText(model.getValueAt(selectedRow, 0).toString());
                txtName.setText(model.getValueAt(selectedRow, 1).toString());
                txtAge.setText(model.getValueAt(selectedRow, 2).toString());
                txtCrime.setText(model.getValueAt(selectedRow, 3).toString());
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);
    }

    private void loadPrisoners() {
        model.setRowCount(0);
        String sql = "SELECT id, name, age, crime FROM prisoners";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("crime")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load prisoners: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PrisonerGUI().setVisible(true);
        });
    }
}


/*
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PrisonerGUI extends JFrame {

    // متغيرات عضو (fields) عشان نستخدمها في كل الكلاس
    private DefaultTableModel model;
    private JTable table;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtAge;
    private JTextField txtCrime;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnSearch;

    public PrisonerGUI() {
        setTitle("Prisoner Management");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. إعداد جدول البيانات
        model = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Crime"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. إعداد حقول الإدخال
        JPanel inputPanel = new JPanel();
        txtId = new JTextField(8);
        txtName = new JTextField(10);
        txtAge = new JTextField(5);
        txtCrime = new JTextField(10);

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(txtAge);
        inputPanel.add(new JLabel("Crime:"));
        inputPanel.add(txtCrime);

        add(inputPanel, BorderLayout.NORTH);

        // 3. إعداد أزرار التحكم
        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        btnSearch = new JButton("Search");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);

        add(buttonPanel, BorderLayout.SOUTH);

        // 4. تحميل بيانات السجناء من قاعدة البيانات وعرضها في الجدول
        loadPrisoners();

        // 5. حدث زر Add
        btnAdd.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText());
                String crime = txtCrime.getText();

                String sql = "INSERT INTO prisoners (id, name, age, crime) VALUES (?, ?, ?, ?)";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);
                    pstmt.setString(2, name);
                    pstmt.setInt(3, age);
                    pstmt.setString(4, crime);

                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Prisoner added successfully!");
                        model.addRow(new Object[]{id, name, age, crime});
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add prisoner.");
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID and Age must be numbers!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        // 6. حدث زر Edit
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a prisoner to edit.");
                return;
            }

            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText());
                String crime = txtCrime.getText();

                String sql = "UPDATE prisoners SET name=?, age=?, crime=? WHERE id=?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, name);
                    pstmt.setInt(2, age);
                    pstmt.setString(3, crime);
                    pstmt.setInt(4, id);

                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Prisoner updated successfully!");
                        // تحديث الجدول
                        model.setValueAt(id, selectedRow, 0);
                        model.setValueAt(name, selectedRow, 1);
                        model.setValueAt(age, selectedRow, 2);
                        model.setValueAt(crime, selectedRow, 3);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update prisoner.");
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID and Age must be numbers!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        // 7. حدث زر Delete
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a prisoner to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this prisoner?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                int id = (int) model.getValueAt(selectedRow, 0);

                String sql = "DELETE FROM prisoners WHERE id=?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);

                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Prisoner deleted successfully!");
                        model.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete prisoner.");
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        // 8. حدث زر Search
        btnSearch.addActionListener(e -> {
            String idText = txtId.getText().trim();
            String nameText = txtName.getText().trim();

            if (idText.isEmpty() && nameText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter ID or Name to search.");
                return;
            }

            model.setRowCount(0); // تنظيف الجدول

            String sql;
            boolean searchById = !idText.isEmpty();

            if (searchById) {
                sql = "SELECT id, name, age, crime FROM prisoners WHERE id = ?";
            } else {
                sql = "SELECT id, name, age, crime FROM prisoners WHERE name LIKE ?";
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                if (searchById) {
                    pstmt.setInt(1, Integer.parseInt(idText));
                } else {
                    pstmt.setString(1, "%" + nameText + "%");
                }

                ResultSet rs = pstmt.executeQuery();

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("crime")
                    });
                }

                if (!found) {
                    JOptionPane.showMessageDialog(this, "No prisoners found.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID must be a number!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        // 9. (اختياري) عند اختيار صف في الجدول، اعرض بياناته في الحقول
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText(model.getValueAt(selectedRow, 0).toString());
                txtName.setText(model.getValueAt(selectedRow, 1).toString());
                txtAge.setText(model.getValueAt(selectedRow, 2).toString());
                txtCrime.setText(model.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    // تحميل سجناء من قاعدة البيانات وعرضهم في الجدول
    private void loadPrisoners() {
        model.setRowCount(0); // تنظيف الجدول أولاً
        String sql = "SELECT id, name, age, crime FROM prisoners";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("crime")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    // نقطة الدخول للبرنامج - Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PrisonerGUI().setVisible(true);
        });
    }
}
*/