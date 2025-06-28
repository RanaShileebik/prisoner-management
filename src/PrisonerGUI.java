import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class PrisonerGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public PrisonerGUI() {
        setTitle("Prisoner Management System");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createMainPanel(), BorderLayout.CENTER);
        loadPrisonersFromDatabase();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Prisoner Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("List of all registered prisoners");
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("+ Add Prisoner");
        JButton btnExport = new JButton("Export CSV");

        btnAdd.setBackground(new Color(41, 98, 255));
        btnAdd.setForeground(Color.WHITE);
        btnExport.setBackground(Color.LIGHT_GRAY);

        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField("Search...", 25);
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search...");
                }
            }
        });
        searchPanel.add(searchField);

        String[] columns = {"ID", "Name", "Age", "Crime", "Nationality", "Sentence", "Entry Date", "Release Date", "Legal Status", "Actions"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("Actions").setCellRenderer(new IconCellRenderer());
        table.getColumn("Actions").setCellEditor(new IconCellEditor(new JCheckBox(), model, table));

        JScrollPane scrollPane = new JScrollPane(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnExport.addActionListener(e -> exportToCSV());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        return mainPanel;
    }

    private void loadPrisonersFromDatabase() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM prisoners");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("crime"),
                        rs.getString("nationality"),
                        rs.getString("sentence"),
                        rs.getString("entry_date"),
                        rs.getString("release_date"),
                        rs.getString("legal_status"),
                        ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField name = new JTextField();
        JTextField age = new JTextField();
        JTextField crime = new JTextField();
        JTextField nationality = new JTextField();
        JTextField sentence = new JTextField();
        JTextField entry = new JTextField();
        JTextField release = new JTextField();
        JTextField status = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:")); panel.add(name);
        panel.add(new JLabel("Age:")); panel.add(age);
        panel.add(new JLabel("Crime:")); panel.add(crime);
        panel.add(new JLabel("Nationality:")); panel.add(nationality);
        panel.add(new JLabel("Sentence:")); panel.add(sentence);
        panel.add(new JLabel("Entry Date:")); panel.add(entry);
        panel.add(new JLabel("Release Date:")); panel.add(release);
        panel.add(new JLabel("Legal Status:")); panel.add(status);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Prisoner", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "")) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO prisoners (name, age, crime, nationality, sentence, entry_date, release_date, legal_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                );
                ps.setString(1, name.getText());
                ps.setInt(2, Integer.parseInt(age.getText()));
                ps.setString(3, crime.getText());
                ps.setString(4, nationality.getText());
                ps.setString(5, sentence.getText());
                ps.setString(6, entry.getText());
                ps.setString(7, release.getText());
                ps.setString(8, status.getText());
                ps.executeUpdate();
                loadPrisonersFromDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Insert error: " + e.getMessage());
            }
        }
    }

    private void exportToCSV() {
        try (FileWriter fw = new FileWriter("prisoners.csv")) {
            for (int i = 0; i < model.getColumnCount() - 1; i++) {
                fw.write(model.getColumnName(i) + ",");
            }
            fw.write("\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount() - 1; j++) {
                    fw.write(model.getValueAt(i, j).toString() + ",");
                }
                fw.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Exported to prisoners.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrisonerGUI().setVisible(true));
    }
}

class IconCellRenderer extends JPanel implements TableCellRenderer {
    private final JButton editBtn = new JButton("✎");
    private final JButton deleteBtn = new JButton("✖");

    public IconCellRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        editBtn.setFocusable(false);
        deleteBtn.setFocusable(false);
        add(editBtn);
        add(deleteBtn);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        return this;
    }
}

class IconCellEditor extends DefaultCellEditor {
    private final JPanel panel = new JPanel();
    private final JButton editBtn = new JButton("✎");
    private final JButton deleteBtn = new JButton("✖");

    public IconCellEditor(JCheckBox checkBox, DefaultTableModel model, JTable table) {
        super(checkBox);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.add(editBtn);
        panel.add(deleteBtn);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                JTextField[] fields = new JTextField[8];
                for (int i = 1; i <= 8; i++) {
                    fields[i - 1] = new JTextField(model.getValueAt(row, i).toString());
                }

                JPanel editPanel = new JPanel(new GridLayout(0, 1));
                String[] labels = {"Name", "Age", "Crime", "Nationality", "Sentence", "Entry Date", "Release Date", "Legal Status"};
                for (int i = 0; i < labels.length; i++) {
                    editPanel.add(new JLabel(labels[i] + ":"));
                    editPanel.add(fields[i]);
                }

                int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Prisoner", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "");
                         PreparedStatement ps = conn.prepareStatement(
                                 "UPDATE prisoners SET name=?, age=?, crime=?, nationality=?, sentence=?, entry_date=?, release_date=?, legal_status=? WHERE id=?")) {
                        for (int i = 0; i < 8; i++) {
                            if (i == 1) ps.setInt(i + 1, Integer.parseInt(fields[i].getText()));
                            else ps.setString(i + 1, fields[i].getText());
                        }
                        ps.setInt(9, (int) model.getValueAt(row, 0));
                        ps.executeUpdate();
                        for (int i = 1; i <= 8; i++) {
                            model.setValueAt(fields[i - 1].getText(), row, i);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating: " + ex.getMessage());
                    }
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(null, "Delete selected prisoner?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "");
                         PreparedStatement ps = conn.prepareStatement("DELETE FROM prisoners WHERE id=?")) {
                        ps.setInt(1, (int) model.getValueAt(row, 0));
                        ps.executeUpdate();
                        model.removeRow(row);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error deleting: " + ex.getMessage());
                    }
                }
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        return panel;
    }
}
