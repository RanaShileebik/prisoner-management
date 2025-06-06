public class Prisoner {
    private int id;
    private String name;
    private int age;
    private String crime;

    public Prisoner(int id, String name, int age, String crime) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.crime = crime;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCrime() { return crime; }
}
