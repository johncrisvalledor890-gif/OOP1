import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class LibrarySystem {
    private List<Book> books = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private User loggedInUser;
    private Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new LibrarySystem().start();
    }

    public void start() {
        try {
            loadUsers();
            loadBooks();
            loadTransactions();
            login();
        } catch (Exception e) {
            System.out.println("Error loading system: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("Welcome to the Library Management System");
        System.out.println("----------------------------------------");

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Username: ");
            String name = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            loggedInUser = validateLogin(name, pass);
            if (loggedInUser != null) {
                System.out.println("\nLogin successful! Welcome, " + loggedInUser.name + ".");
                menu();
                return;
            } else {
                attempts--;
                System.out.println("Invalid username or password. Attempts left: " + attempts);
            }
        }
        System.out.println("Too many failed attempts. Exiting...");
    }

    private User validateLogin(String name, String pass) {
        for (User u : users) {
            if (u.name.equalsIgnoreCase(name) && u.getPassword().equals(pass)) return u;
        }
        return null;
    }

    private void menu() {
        while (true) {
            System.out.println("\n----- MAIN MENU -----");
            System.out.println("1. View All Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            if (loggedInUser.getRole().equalsIgnoreCase("admin")) {
                System.out.println("4. Manage Users");
                System.out.println("5. Manage Books");
                System.out.println("6. View Transactions");
                System.out.println("7. Exit");
            } else {
                System.out.println("4. Exit");
            }

            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            if (loggedInUser.getRole().equalsIgnoreCase("admin")) {
                switch (choice) {
                    case "1": viewBooks(); break;
                    case "2": borrowBook(); break;
                    case "3": returnBook(); break;
                    case "4": manageUsers(); break;
                    case "5": manageBooks(); break;
                    case "6": viewTransactions(); break;
                    case "7": saveAll(); return;
                    default: System.out.println("Invalid choice.");
                }
            } else {
                switch (choice) {
                    case "1": viewBooks(); break;
                    case "2": borrowBook(); break;
                    case "3": returnBook(); break;
                    case "4": saveAll(); return;
                    default: System.out.println("Invalid choice.");
                }
            }
        }
    }

    // ==== BOOK ACTIONS ====
    private void viewBooks() {
        System.out.println("\n--- BOOK LIST ---");
        for (Book b : books) b.displayBookDetails();
    }

    private void borrowBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine();
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(id)) {
                if (!b.isAvailable()) {
                    System.out.println("Book not available.");
                    return;
                }
                b.setAvailable(false);
                String tid = "T" + String.format("%03d", transactions.size() + 1);
                transactions.add(new Transaction(tid, loggedInUser.id, id, LocalDate.now().toString(), "null"));
                System.out.println("Book borrowed successfully!");
                return;
            }
        }
        System.out.println("Book not found.");
    }

    private void returnBook() {
        System.out.print("Enter Book ID to return: ");
        String id = sc.nextLine();
        for (Transaction t : transactions) {
            if (t.getUserId().equals(loggedInUser.id) && t.getBookId().equalsIgnoreCase(id) && t.getDateReturned().equals("null")) {
                t.setDateReturned(LocalDate.now().toString());
                for (Book b : books)
                    if (b.getBookId().equalsIgnoreCase(id)) b.setAvailable(true);
                System.out.println("Book returned successfully!");
                return;
            }
        }
        System.out.println("No matching borrow record found.");
    }

    // ==== ADMIN ====
    private void manageUsers() {
        System.out.println("\n--- USERS ---");
        for (User u : users) u.displayInfo();
    }

    private void manageBooks() {
        System.out.println("\n--- BOOK MANAGEMENT ---");
        for (Book b : books) b.displayBookDetails();
    }

    private void viewTransactions() {
        System.out.println("\n--- TRANSACTIONS ---");
        for (Transaction t : transactions) t.displayTransaction();
    }

    // ==== FILE OPERATIONS ====
    private void loadUsers() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                users.add(new User(d[0], d[1], d[2], d[3]));
            }
        }
    }

    private void loadBooks() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                books.add(new Book(d[0], d[1], d[2], Boolean.parseBoolean(d[3])));
            }
        }
    }

    private void loadTransactions() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                transactions.add(new Transaction(d[0], d[1], d[2], d[3], d[4]));
            }
        }
    }

    private void saveAll() {
        try {
            saveBooks();
            saveTransactions();
            System.out.println("All changes saved successfully. Goodbye!");
        } catch (IOException e) {
            System.out.println("Error saving files: " + e.getMessage());
        }
    }

    private void saveBooks() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"))) {
            for (Book b : books) {
                bw.write(b.getBookId() + "," + b.getTitle() + "," + b.getAuthor() + "," + b.isAvailable());
                bw.newLine();
            }
        }
    }

    private void saveTransactions() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt"))) {
            for (Transaction t : transactions) {
                bw.write(t.getTransactionId() + "," + t.getUserId() + "," + t.getBookId() + "," +
                        t.getDateBorrowed() + "," + t.getDateReturned());
                bw.newLine();
            }
        }
    }
}
