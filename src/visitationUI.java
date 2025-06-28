import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class visitationUI extends JFrame {
    private JTable visitTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public visitationUI() {
        setTitle("Visitation Management System");
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
            dispose();
            new PrisonerGUI().setVisible(true);
        });
        tab2.addActionListener(e -> {
            dispose();
            new TimeTrackingUI().setVisible(true);
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

        JLabel title = new JLabel("Visitation Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Manage and track all visitations.");
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("+ Add Visit");
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
        searchField = new JTextField("Search visitor or prisoner", 25);
        searchField.setForeground(Color.GRAY);
        searchField.setToolTipText("Type to filter visits");
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search visitor or prisoner")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search visitor or prisoner");
                }
            }
        });

        searchPanel.add(searchField);

        tableModel = new DefaultTableModel(new String[]{"Visitor", "Prisoner", "Relation", "Date", "Time In", "Time Out", "Approved"}, 0);
        visitTable = new JTable(tableModel);
        visitTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(visitTable);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        visitTable.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add visit logic here"));
        btnExport.addActionListener(e -> exportVisitsToFile());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        return mainPanel;
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
        SwingUtilities.invokeLater(() -> new visitationUI().setVisible(true));
    }
}
