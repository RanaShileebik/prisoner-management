import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import prison_sys.PrisonerCard;

public class TimeTrackingUI extends JFrame {
    private final JPanel mainPanel;
    private final List<PrisonerCard> prisonerCards = new ArrayList<>();

    private static final String URL = "jdbc:mysql://localhost:3307/prison_db";
    private static final String USER = "root";
    private static final String PASS = "";

    public TimeTrackingUI() {
        setTitle("Time Tracking Interface");
        setSize(1250, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField searchField = new JTextField("Search...", 20);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        loadAllPrisoners();

        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (kw.isEmpty() || kw.equals("Search...")) {
                loadAllPrisoners();
            } else {
                searchPrisoners(kw);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                prisonerCards.forEach(PrisonerCard::stopMonitor);
            }
        });
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

    private void loadAllPrisoners() {
        mainPanel.removeAll();
        prisonerCards.clear();

        String sql = "SELECT * FROM prisoners";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                addCard(rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, " Database Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void searchPrisoners(String keyword) {
        mainPanel.removeAll();
        prisonerCards.clear();

        String sql = "SELECT * FROM prisoners WHERE name LIKE ? OR id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            int idVal;
            try {
                idVal = Integer.parseInt(keyword);
            } catch (NumberFormatException ex) {
                idVal = -1;
            }
            ps.setInt(2, idVal);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    addCard(rs);
                }
                if (!found) {
                    JOptionPane.showMessageDialog(this, " No prisoner found.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, " Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void addCard(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String crime = rs.getString("crime");
        LocalDate entryDate = LocalDate.parse(rs.getString("entry_date"));
        LocalDate releaseDate = LocalDate.parse(rs.getString("release_date"));

        PrisonerCard card = new PrisonerCard(name, id, crime, entryDate, releaseDate);
        prisonerCards.add(card);
        mainPanel.add(card);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TimeTrackingUI().setVisible(true));
    }
}
