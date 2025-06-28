import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NoteManager {
    private static final String USERS_FILE = "users.txt";
    private static Scanner scanner = new Scanner(System.in);
    private static String loggedInUser = null;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Welcome to Notes Manager ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> register();
                case "2" -> {
                    if (login()) {
                        notesMenu();  // Start notes system for logged in user
                    }
                }
                case "3" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // Registration
    private static void register() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        if (userExists(username)) {
            System.out.println("User already exists. Try a different one.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (FileWriter writer = new FileWriter(USERS_FILE, true)) {
            writer.write(username + ":" + password + "\n");
            System.out.println("Registration successful.");
        } catch (IOException e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    // Login
    private static boolean login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    loggedInUser = username;
                    System.out.println("Login successful. Welcome " + username + "!");
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        System.out.println("Invalid credentials.");
        return false;
    }

    // Check if user already exists
    private static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // ignore for now
        }
        return false;
    }

    // Notes Menu
    private static void notesMenu() {
        while (true) {
            System.out.println("\n--- Notes Manager for " + loggedInUser + " ---");
            System.out.println("1. Create Note");
            System.out.println("2. View All Notes");
            System.out.println("3. Search Notes");
            System.out.println("4. Delete Note by Keyword");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> createNote();
                case 2 -> viewNotes();
                case 3 -> searchNotes();
                case 4 -> deleteNoteByKeyword();
                case 5 -> {
                    System.out.println("Logged out.");
                    loggedInUser = null;
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static String getUserFileName() {
        return "notes_" + loggedInUser + ".txt";
    }

    private static void createNote() {
        System.out.println("\n--- Create Note ---");
        System.out.print("Enter note content: ");
        String content = scanner.nextLine();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String note = "Timestamp: " + timestamp + "\n" + content + "\n--- END NOTE ---\n";

        try (FileWriter writer = new FileWriter(getUserFileName(), true)) {
            writer.write(note);
            System.out.println("Note saved.");
        } catch (IOException e) {
            System.out.println("Error saving note.");
        }
    }

    private static void viewNotes() {
        System.out.println("\n--- Your Notes ---");
        File file = new File(getUserFileName());

        if (!file.exists()) {
            System.out.println("You have no notes.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean empty = true;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                empty = false;
            }
            if (empty) {
                System.out.println("You have no notes.");
            }
        } catch (IOException e) {
            System.out.println("Error reading notes.");
        }
    }

    private static void searchNotes() {
        System.out.print("\nEnter keyword to search: ");
        String keyword = scanner.nextLine();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(getUserFileName()))) {
            StringBuilder note = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                note.append(line).append("\n");
                if (line.equals("--- END NOTE ---")) {
                    if (note.toString().toLowerCase().contains(keyword.toLowerCase())) {
                        System.out.println("\n--- Match Found ---");
                        System.out.print(note);
                        found = true;
                    }
                    note.setLength(0);
                }
            }

            if (!found) {
                System.out.println("No matching note found.");
            }

        } catch (IOException e) {
            System.out.println("Error during search.");
        }
    }

    private static void deleteNoteByKeyword() {
        System.out.print("\nEnter keyword of note to delete: ");
        String keyword = scanner.nextLine();

        File inputFile = new File(getUserFileName());
        File tempFile = new File("temp_" + loggedInUser + ".txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            StringBuilder note = new StringBuilder();
            String line;
            boolean deleted = false;

            while ((line = reader.readLine()) != null) {
                note.append(line).append("\n");

                if (line.equals("--- END NOTE ---")) {
                    if (!note.toString().toLowerCase().contains(keyword.toLowerCase())) {
                        writer.write(note.toString());
                    } else {
                        deleted = true;
                    }
                    note.setLength(0);
                }
            }

            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                if (deleted) {
                    System.out.println("Note deleted.");
                } else {
                    System.out.println("No note matched the keyword.");
                }
            } else {
                System.out.println("Failed to update notes.");
            }

        } catch (IOException e) {
            System.out.println("Error deleting note.");
        }
    }
}
