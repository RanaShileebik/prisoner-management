import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class visitationUI extends JFrame {

    private JComboBox<String> prisonerCombo;
    private JTextField visitorField, relationField, dateField, timeInField, timeOutField;
    private JCheckBox approvalCheck;
    private JTable visitTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnExport;

    public visitationUI() {
        setTitle("Visitation Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(950, 600);
        setLayout(new BorderLayout());

        initMenuBar();
        initComponents();
        loadPrisoners();
        loadVisitations();

        setVisible(true);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuPrisoner = new JMenu("Prisoner Management");
        JMenu menuRelease = new JMenu("Release Tracking");
        JMenu menuVisitation = new JMenu("Visitation Management");

        menuPrisoner.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent e) {
                dispose();
                new PrisonerGUI().setVisible(true);
            }
            public void menuDeselected(javax.swing.event.MenuEvent e) {}
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });

        menuRelease.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent e) {
                dispose();
                new TimeTrackingUI().setVisible(true);
            }
            public void menuDeselected(javax.swing.event.MenuEvent e) {}
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });

        menuBar.add(menuPrisoner);
        menuBar.add(menuRelease);
        menuBar.add(menuVisitation);
        setJMenuBar(menuBar);
    }

    private void initComponents() {
        JPanel panelTopWrapper = new JPanel();
        panelTopWrapper.setLayout(new BorderLayout());

        JLabel title = new JLabel("Visitation Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTopWrapper.add(title, BorderLayout.NORTH);

        JPanel panelTop = new JPanel(new GridBagLayout());
        panelTop.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font uiFont = new Font("Segoe UI", Font.PLAIN, 14);

        gbc.gridx = 0; gbc.gridy = 0;
        panelTop.add(new JLabel("Visitor Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        visitorField = new JTextField(15);
        visitorField.setFont(uiFont);
        panelTop.add(visitorField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panelTop.add(new JLabel("Select Prisoner:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        prisonerCombo = new JComboBox<>();
        prisonerCombo.setFont(uiFont);
        panelTop.add(prisonerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panelTop.add(new JLabel("Relationship to Prisoner:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        relationField = new JTextField(15);
        relationField.setFont(uiFont);
        panelTop.add(relationField, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.gridwidth = 1;
        panelTop.add(new JLabel("Visit Date (yyyy-mm-dd):"), gbc);
        gbc.gridx = 5;
        dateField = new JTextField(10);
        dateField.setFont(uiFont);
        dateField.setText(LocalDate.now().toString());
        panelTop.add(dateField, gbc);
        gbc.gridx = 4; gbc.gridy = 1;
        panelTop.add(new JLabel("Time In (hh:mm):"), gbc);
        gbc.gridx = 5;
        timeInField = new JTextField(10);
        timeInField.setFont(uiFont);
        panelTop.add(timeInField, gbc);

        gbc.gridx = 4; gbc.gridy = 2;
        panelTop.add(new JLabel("Time Out (hh:mm):"), gbc);
        gbc.gridx = 5;
        timeOutField = new JTextField(10);
        timeOutField.setFont(uiFont);
        panelTop.add(timeOutField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelTop.add(new JLabel("Security Approval:"), gbc);
        gbc.gridx = 1;
        approvalCheck = new JCheckBox("Approved");
        approvalCheck.setFont(uiFont);
        panelTop.add(approvalCheck, gbc);

        gbc.gridx = 4;
        btnAdd = new JButton("➕ Add Visit");
        btnAdd.setBackground(new Color(144, 238, 144));
        btnAdd.setFont(uiFont);
        btnAdd.addActionListener(e -> saveVisitation());
        panelTop.add(btnAdd, gbc);

        gbc.gridx = 5;
        btnExport = new JButton("📤 Export Visits");
        btnExport.setBackground(new Color(173, 216, 230));
        btnExport.setFont(uiFont);
        btnExport.addActionListener(e -> exportVisitsToFile());
        panelTop.add(btnExport, gbc);

        panelTopWrapper.add(panelTop, BorderLayout.CENTER);
        add(panelTopWrapper, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Prisoner", "Visitor", "Relation", "Visit Date", "Time In", "Time Out", "Approved"});
        visitTable = new JTable(tableModel);
        visitTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        visitTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        visitTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(visitTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPrisoners() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, name FROM prisoners");
            while (rs.next()) {
                prisonerCombo.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error loading prisoners: " + ex.getMessage());
        }
    }

    private void loadVisitations() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT v.id, p.name AS prisoner_name, v.visitor_name, v.relation, v.visit_date, v.time_in, v.time_out, v.security_approval " +
                           "FROM visitations v JOIN prisoners p ON v.prisoner_id = p.id";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("prisoner_name"));
                row.add(rs.getString("visitor_name"));
                row.add(rs.getString("relation"));
                row.add(rs.getString("visit_date"));
                row.add(rs.getString("time_in"));
                row.add(rs.getString("time_out"));
                row.add(rs.getBoolean("security_approval") ? "Yes" : "No");
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error loading visits: " + ex.getMessage());
        }
    }

    private void saveVisitation() {
        String visitorName = visitorField.getText();
        String relation = relationField.getText();
        String selected = (String) prisonerCombo.getSelectedItem();
        if (selected == null || selected.isEmpty()) return;
        int prisonerId = Integer.parseInt(selected.split(" - ")[0]);
        String visitDate = dateField.getText();
        String timeIn = timeInField.getText();
        String timeOut = timeOutField.getText();
        boolean approved = approvalCheck.isSelected();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO visitations (prisoner_id, visitor_name, relation, visit_date, time_in, time_out, security_approval) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prisonerId);
            ps.setString(2, visitorName);
            ps.setString(3, relation);
            ps.setString(4, visitDate);
            ps.setString(5, timeIn);
            ps.setString(6, timeOut);
            ps.setBoolean(7, approved);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Visit added successfully");
            loadVisitations();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error saving visit: " + ex.getMessage());
        }
    }

    private void exportVisitsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("visits_log.txt", true))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row.append(tableModel.getValueAt(i, j).toString()).append(" | ");
                }
                writer.write(row.toString().trim());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "📁 Visits exported to visits_log.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new visitationUI());
    }
}
