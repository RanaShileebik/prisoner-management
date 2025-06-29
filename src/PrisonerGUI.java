import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

public class PrisonerGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
        }
    }

    public PrisonerGUI() {
        setTitle("Prisoner Management System");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);
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

        tab1.addActionListener(e -> {
            // Stay on current
        });

        tab2.addActionListener(e -> {
            dispose();
            new TimeTrackingUI().setVisible(true);
        });

        tab3.addActionListener(e -> {
            dispose();
            new visitationUI().setVisible(true);
        });

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

        String[] columns = {"ID", "Full Name", "Age", "Crime", "Nationality", "Sentence", "Entry Date", "Release Date", "Legal Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/prison_db", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM prisoners")) {

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
                        rs.getString("legal_status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
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
