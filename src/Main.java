public class Main {
    public static void main(String[] args) {
        try {
            DBConnection.getConnection();
            System.out.println("Connected to the database successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }
}
