import java.util.ArrayList;

public class User extends Person {
    private String password;
    private String role;
    private ArrayList<String> borrowedBooks = new ArrayList<>();

    public User(String id, String name, String password, String role) {
        super(id, name);
        this.password = password;
        this.role = role;
    }

    public String getPassword() { return password; }
    public String getRole() { return role; }
    public ArrayList<String> getBorrowedBooks() { return borrowedBooks; }

    @Override
    public void displayInfo() {
        System.out.println("User ID: " + id + " | Name: " + name + " | Role: " + role);
    }
}
