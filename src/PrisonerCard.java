import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class PrisonerCard extends JPanel {
    private final LocalDate entryDate;
    private final LocalDate releaseDate;
    private final long      totalDays;

    private final JProgressBar progressBar;
    private final JLabel       progressLabel;
    private final JLabel       daysLeftLabel;

    private Thread monitorThread;

    public PrisonerCard(String name, int id, String crime,
                        LocalDate entryDate, LocalDate releaseDate) {

        this.entryDate   = entryDate;
        this.releaseDate = releaseDate;
        this.totalDays   = ChronoUnit.DAYS.between(entryDate, releaseDate);

       
        setPreferredSize(new Dimension(260, 170));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel idLabel    = new JLabel("ID: " + id);
        JLabel crimeLabel = new JLabel("Charge: " + crime);
        JLabel datesLabel = new JLabel("From " + entryDate + "  to " + releaseDate);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(33, 150, 243));

        progressLabel = new JLabel(); 
        daysLeftLabel = new JLabel();  

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.add(idLabel);
        info.add(crimeLabel);
        info.add(datesLabel);
        info.add(progressBar);
        info.add(progressLabel);
        info.add(daysLeftLabel);

        add(nameLabel, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);

        startMonitor();
    }

   
    private int calcProgress() {
        long served = ChronoUnit.DAYS.between(entryDate, LocalDate.now());
        return (int) Math.min((served * 100.0) / totalDays, 100);
    }

   
    private long calcDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), releaseDate);
    }

   
    private void startMonitor() {
        monitorThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int  progress = calcProgress();
                    long daysLeft = calcDaysLeft();

                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                        progressBar.setString(progress + "%");
                        progressLabel.setText("Served: " + progress + "%");

                        if (daysLeft <= 0) {
                            daysLeftLabel.setText("Released");
                            progressBar.setForeground(new Color(0, 153, 0));
                            Thread.currentThread().interrupt(); 
                        } else {
                            daysLeftLabel.setText("Days left: " + daysLeft);
                        }
                    });

                    Thread.sleep(60_000); 
                }
            } catch (InterruptedException ignored) {
            }
        });

        monitorThread.setDaemon(true); 
        monitorThread.start();
    }

    
    public void stopMonitor() {
        if (monitorThread != null && monitorThread.isAlive()) {
            monitorThread.interrupt();
        }
    }
}