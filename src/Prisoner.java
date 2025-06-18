import java.time.LocalDate;

public class Prisoner {
    private int id;
    private String name;
    private int age;
    private String crime;

    private String nationality;
    private String sentence;
    private LocalDate entryDate;
    private LocalDate releaseDate;
    private String legalStatus; // محكوم / موقوف احتياطياً

    public Prisoner(int id, String name, int age, String crime,
                    String nationality, String sentence,
                    LocalDate entryDate, LocalDate releaseDate,
                    String legalStatus) {

        this.id = id;
        this.name = name;
        this.age = age;
        this.crime = crime;
        this.nationality = nationality;
        this.sentence = sentence;
        this.entryDate = entryDate;
        this.releaseDate = releaseDate;
        this.legalStatus = legalStatus;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCrime() { return crime; }
    public String getNationality() { return nationality; }
    public String getSentence() { return sentence; }
    public LocalDate getEntryDate() { return entryDate; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getLegalStatus() { return legalStatus; }

    // Setters 
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCrime(String crime) { this.crime = crime; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setSentence(String sentence) { this.sentence = sentence; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public void setLegalStatus(String legalStatus) { this.legalStatus = legalStatus; }
}

