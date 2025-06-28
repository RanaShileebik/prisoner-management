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
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public PrisonerGUI() {
        setTitle("Prisoner Management System");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.add(createMainPanel(), "prisoners");
        cardPanel.add(createDummyPanel("Sentence Tracker Section"), "sentences");
        cardPanel.add(createDummyPanel("Visitation Management Section"), "visits");
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("\uD83D\uDEE1 PMS");
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        logo.setBorder(new EmptyBorder(20, 15, 20, 0));

        JButton tab1 = new JButton("Prisoner Management");
        JButton tab2 = new JButton("Sentence Tracker");
        JButton tab3 = new JButton("Visitation Management");

        tab1.addActionListener(e -> cardLayout.show(cardPanel, "prisoners"));
        tab2.addActionListener(e -> cardLayout.show(cardPanel, "sentences"));
        tab3.addActionListener(e -> cardLayout.show(cardPanel, "visits"));

        for (JButton b : new JButton[]{tab1, tab2, tab3}) {
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setBorder(new EmptyBorder(10, 20, 10, 10));
        }

        sidebar.add(logo);
        sidebar.add(tab1);
        sidebar.add(tab2);
        sidebar.add(tab3);
        return sidebar;
    }

    private JPanel createDummyPanel(String label) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(label));
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Prisoner Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Manage and track all prisoner records.");
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("+ Add New Prisoner");
        JButton btnExport = new JButton("Export");

        btnAdd.setBackground(new Color(41, 98, 255));
        btnAdd.setForeground(Color.WHITE);
        btnExport.setBackground(Color.LIGHT_GRAY);
        btnExport.setForeground(Color.BLACK);

        buttonPanel.setOpaque(false);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchField = new JTextField("Search by ID, Name, Status, etc.", 25);
        searchField.setForeground(Color.GRAY);
        searchField.setToolTipText("Type to filter all fields");
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search by ID, Name, Status, etc.")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search by ID, Name, Status, etc.");
                }
            }
        });

        searchPanel.add(searchField);

        String[] columns = {"Full Name", "Nationality", "Charge", "Sentence", "Entry Date", "Release Date", "Status", "Actions"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("Actions").setCellRenderer(new IconCellRenderer());
        table.getColumn("Actions").setCellEditor(new IconCellEditor(new JCheckBox(), model, table));

        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, nationality, charge, sentence, entry_date, release_date, legal_status FROM prisoners")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("nationality"),
                        rs.getString("charge"),
                        rs.getString("sentence"),
                        rs.getString("entry_date"),
                        rs.getString("release_date"),
                        rs.getString("legal_status"),
                        ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }

        btnAdd.addActionListener(e -> {
            JTextField name = new JTextField();
            JTextField nat = new JTextField();
            JTextField charge = new JTextField();
            JTextField sentence = new JTextField();
            JTextField entry = new JTextField();
            JTextField release = new JTextField();
            JComboBox<String> status = new JComboBox<>(new String[]{"Convicted", "Pre-trial"});

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Full Name:")); panel.add(name);
            panel.add(new JLabel("Nationality:")); panel.add(nat);
            panel.add(new JLabel("Charge:")); panel.add(charge);
            panel.add(new JLabel("Sentence:")); panel.add(sentence);
            panel.add(new JLabel("Entry Date:")); panel.add(entry);
            panel.add(new JLabel("Release Date:")); panel.add(release);
            panel.add(new JLabel("Status:")); panel.add(status);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add Prisoner", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "");
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO prisoners (name, nationality, charge, sentence, entry_date, release_date, legal_status) VALUES (?, ?, ?, ?, ?, ?, ?)");) {
                    ps.setString(1, name.getText());
                    ps.setString(2, nat.getText());
                    ps.setString(3, charge.getText());
                    ps.setString(4, sentence.getText());
                    ps.setString(5, entry.getText());
                    ps.setString(6, release.getText());
                    ps.setString(7, (String) status.getSelectedItem());
                    ps.executeUpdate();
                    model.addRow(new Object[]{
                            name.getText(), nat.getText(), charge.getText(), sentence.getText(),
                            entry.getText(), release.getText(), status.getSelectedItem(), ""
                    });
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error inserting: " + ex.getMessage());
                }
            }
        });

        btnExport.addActionListener(e -> {
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
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrisonerGUI().setVisible(true));
    }
}

class IconCellRenderer extends JPanel implements TableCellRenderer {
    private final JButton editBtn = new JButton("\u270E");
    private final JButton deleteBtn = new JButton("\u2716");

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
    private final JButton editBtn = new JButton("\u270E");
    private final JButton deleteBtn = new JButton("\u2716");

    public IconCellEditor(JCheckBox checkBox, DefaultTableModel model, JTable table) {
        super(checkBox);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.add(editBtn);
        panel.add(deleteBtn);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String name = (String) model.getValueAt(row, 0);
                String nationality = (String) model.getValueAt(row, 1);
                String charge = (String) model.getValueAt(row, 2);
                String sentence = (String) model.getValueAt(row, 3);
                String entryDate = (String) model.getValueAt(row, 4);
                String releaseDate = (String) model.getValueAt(row, 5);
                String status = (String) model.getValueAt(row, 6);

                JTextField f1 = new JTextField(name);
                JTextField f2 = new JTextField(nationality);
                JTextField f3 = new JTextField(charge);
                JTextField f4 = new JTextField(sentence);
                JTextField f5 = new JTextField(entryDate);
                JTextField f6 = new JTextField(releaseDate);
                JTextField f7 = new JTextField(status);

                JPanel editPanel = new JPanel(new GridLayout(0, 1));
                editPanel.add(new JLabel("Full Name:")); editPanel.add(f1);
                editPanel.add(new JLabel("Nationality:")); editPanel.add(f2);
                editPanel.add(new JLabel("Charge:")); editPanel.add(f3);
                editPanel.add(new JLabel("Sentence:")); editPanel.add(f4);
                editPanel.add(new JLabel("Entry Date:")); editPanel.add(f5);
                editPanel.add(new JLabel("Release Date:")); editPanel.add(f6);
                editPanel.add(new JLabel("Status:")); editPanel.add(f7);

                int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Prisoner", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "");
                         PreparedStatement ps = conn.prepareStatement(
                                 "UPDATE prisoners SET name=?, nationality=?, charge=?, sentence=?, entry_date=?, release_date=?, legal_status=? WHERE name=?")) {
                        ps.setString(1, f1.getText());
                        ps.setString(2, f2.getText());
                        ps.setString(3, f3.getText());
                        ps.setString(4, f4.getText());
                        ps.setString(5, f5.getText());
                        ps.setString(6, f6.getText());
                        ps.setString(7, f7.getText());
                        ps.setString(8, name);
                        ps.executeUpdate();
                        model.setValueAt(f1.getText(), row, 0);
                        model.setValueAt(f2.getText(), row, 1);
                        model.setValueAt(f3.getText(), row, 2);
                        model.setValueAt(f4.getText(), row, 3);
                        model.setValueAt(f5.getText(), row, 4);
                        model.setValueAt(f6.getText(), row, 5);
                        model.setValueAt(f7.getText(), row, 6);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating: " + ex.getMessage());
                    }
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String name = (String) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Delete prisoner '" + name + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/prison_db", "root", "");
                         PreparedStatement ps = conn.prepareStatement("DELETE FROM prisoners WHERE name=?")) {
                        ps.setString(1, name);
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
