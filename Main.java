import java.util.*;
import java.util.Scanner;

abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected String role;
    protected boolean approved;

    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.approved = false;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isApproved() { return approved; }

    public void approve() {
        this.approved = true;
    }

    public abstract void showMenu();
}

class Admin extends User {
    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, "ADMIN");
        approve();
    }

    public void showMenu() {
        System.out.println("\n--- ADMIN DASHBOARD ---");
        System.out.println("1. Approve Users");
        System.out.println("2. Monitor Complaints");
        System.out.println("3. Exit");
    }
}

class Owner extends User {
    public Owner(int id, String name, String email, String password) {
        super(id, name, email, password, "OWNER");
    }

    public void showMenu() {
        System.out.println("\n--- OWNER DASHBOARD ---");
        System.out.println("1. Add Property");
        System.out.println("2. View Properties");
        System.out.println("3. Manage Tenants");
        System.out.println("4. Exit");
    }
}

class Tenant extends User {
    public Tenant(int id, String name, String email, String password) {
        super(id, name, email, password, "TENANT");
    }

    public void showMenu() {
        System.out.println("\n--- TENANT DASHBOARD ---");
        System.out.println("1. View Rooms");
        System.out.println("2. Raise Complaint");
        System.out.println("3. Pay Rent");
        System.out.println("4. Exit");
    }
}

class Staff extends User {
    public Staff(int id, String name, String email, String password) {
        super(id, name, email, password, "STAFF");
    }

    public void showMenu() {
        System.out.println("\n--- STAFF DASHBOARD ---");
        System.out.println("1. View Tasks");
        System.out.println("2. Update Task");
        System.out.println("3. Exit");
    }
}

class Property {
    int id;
    String address;
    String facilities;
    String status;

    public Property(int id, String address, String facilities) {
        this.id = id;
        this.address = address;
        this.facilities = facilities;
        this.status = "PENDING";
    }
}


class AuthService {

    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<String> emails = new ArrayList<>(); 
    static int idCounter = 1;

    public static void register(String name, String email, String pass, String role) {

  
        if (emails.contains(email)) {
            System.out.println("Email already exists. Try another.");
            return; // stop registration
        }

        User user = null;

        switch (role.toUpperCase()) {
            case "ADMIN":
                user = new Admin(idCounter++, name, email, pass);
                break;
            case "OWNER":
                user = new Owner(idCounter++, name, email, pass);
                break;
            case "TENANT":
                user = new Tenant(idCounter++, name, email, pass);
                break;
            case "STAFF":
                user = new Staff(idCounter++, name, email, pass);
                break;
        }

        // ✅ Add user and email only if valid
        if (user != null) {
            users.add(user);
            emails.add(email);  // ✅ store email

            System.out.println("Registered Successfully. Awaiting approval (if not admin).");
        }
    }

    public static User login(String email, String pass) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(pass)) {
                if (!u.isApproved()) {
                    System.out.println("Waiting for admin approval.");
                    return null;
                }
                System.out.println("✅ Login successful!");
                return u;
            }
        }
        System.out.println("❌ Invalid credentials!");
        return null;
    }
}

class Validator {
    public static boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z ]+$");
    }

    public static boolean isValidEmail(String email) {
        return email.contains("@") && email.endsWith(".com");
    }

    public static boolean isValidPassword(String pass) {
        return pass.length() >= 6;
    }
}

class PropertyService {
    static ArrayList<Property> properties = new ArrayList<>();
    static int idCounter = 1;

    public static void addProperty(String addr, String facilities) {
        properties.add(new Property(idCounter++, addr, facilities));
        System.out.println("Property request sent for approval.");
    }

    public static void viewProperties() {
        for (Property p : properties) {
            System.out.println("ID: " + p.id + " Address: " + p.address + " Status: " + p.status);
        }
    }
}

class Complaint {
    int id;
    String description;
    String status;

    public Complaint(int id, String desc) {
        this.id = id;
        this.description = desc;
        this.status = "PENDING";
    }
}

class ComplaintService {
    static ArrayList<Complaint> complaints = new ArrayList<>();
    static int idCounter = 1;

    public static void raiseComplaint(String desc) {
        complaints.add(new Complaint(idCounter++, desc));
        System.out.println("✅ Complaint submitted.");
    }

    public static void viewComplaints() {
        for (Complaint c : complaints) {
            System.out.println("ID: " + c.id +
                " | " + c.description +
                " | Status: " + c.status);
        }
    }

    public static void resolveComplaint(int id) {
        for (Complaint c : complaints) {
            if (c.id == id) {
                c.status = "RESOLVED";
                System.out.println("✅ Complaint resolved.");
                return;
            }
        }
        System.out.println("❌ Complaint not found.");
    }
}



public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n====== PG MANAGEMENT SYSTEM ======");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // clear invalid input
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    handleRegister();
                    break;

                case 2:
                    handleLogin();
                    break;

                case 3:
                    System.out.println("👋 Goodbye!");
                    return;

                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }

    // ================= REGISTER =================
    static void handleRegister() {

        String name, email, pass;
        int roleChoice;

        // ✅ Name validation
        while (true) {
            System.out.print("Name: ");
            name = sc.nextLine();

            if (Validator.isValidName(name)) {
                break;
            } else {
                System.out.println("Name must contain only letters and spaces.");
            }
        }
        // ✅ Email validation
        
while (true) {
    System.out.print("Email: ");
    email = sc.nextLine();

    if (!Validator.isValidEmail(email)) {
        System.out.println("Invalid email format.");
        continue;
    }

    // ✅ CHECK DUPLICATE HERE (IMMEDIATE)
    if (AuthService.emails.contains(email)) {
        System.out.println("Email already exists.");
        continue;
    }

    break; // ✅ only when valid + unique
}

        // ✅ Password validation
        while (true) {
            System.out.print("Password: ");
            pass = sc.nextLine();

            if (Validator.isValidPassword(pass)) {
                break;
            } else {
                System.out.println("Password must be at least 6 characters.");
            }
        }

        // ✅ Role selection
        while (true) {
            System.out.println("Select Role:");
            System.out.println("1. ADMIN");
            System.out.println("2. OWNER");
            System.out.println("3. TENANT");
            System.out.println("4. STAFF");
            System.out.print("Enter choice: ");

            if (sc.hasNextInt()) {
                roleChoice = sc.nextInt();
                sc.nextLine();

                if (roleChoice >= 1 && roleChoice <= 4) {
                    break;
                } else {
                    System.out.println("Invalid role selection.");
                }
            } else {
                System.out.println("Enter a valid number.");
                sc.next(); // clear invalid input
            }
        }

        String role = switch (roleChoice) {
            case 1 -> "ADMIN";
            case 2 -> "OWNER";
            case 3 -> "TENANT";
            case 4 -> "STAFF";
            default -> "";
        };

        AuthService.register(name, email, pass, role);
    }

    // ================= LOGIN =================
    static void handleLogin() {

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        User user = AuthService.login(email, pass);

        if (user != null) {
            handleUser(user);
        } else {
            System.out.println("Invalid login credentials.");
        }
    }

    // ================= USER MENU =================
    static void handleUser(User user) {

        while (true) {

            user.showMenu();

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();

            // ================= OWNER =================
            if (user instanceof Owner) {
                switch (choice) {

                    case 1:
                        sc.nextLine();
                        System.out.print("Address: ");
                        String addr = sc.nextLine();

                        System.out.print("Facilities: ");
                        String fac = sc.nextLine();

                        PropertyService.addProperty(addr, fac);
                        break;

                    case 2:
                        PropertyService.viewProperties();
                        break;

                    case 4:
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

            // ================= TENANT =================
            else if (user instanceof Tenant) {
                switch (choice) {

                    case 2:
                        sc.nextLine();
                        System.out.print("Enter complaint: ");
                        String desc = sc.nextLine();

                        ComplaintService.raiseComplaint(desc);
                        break;

                    case 4:
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

            // ================= ADMIN =================
            else if (user instanceof Admin) {
                switch (choice) {

                    case 1:
                        for (User u : AuthService.users) {
                            if (!u.isApproved()) {
                                u.approve();
                                System.out.println("Approved: " + u.name);
                            }
                        }
                        break;

                    case 2:
                        ComplaintService.viewComplaints();
                        System.out.print("Enter complaint ID to resolve: ");

                        if (sc.hasNextInt()) {
                            int id = sc.nextInt();
                            ComplaintService.resolveComplaint(id);
                        } else {
                            System.out.println("Invalid ID.");
                            sc.next();
                        }
                        break;

                    case 3:
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }
}