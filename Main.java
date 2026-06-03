// Use this editor to write, compile and run your Java code online
import java.util.*;

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
        approve(); // auto-approved
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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
    static int idCounter = 1;

    public static void register(String name, String email, String pass, String role) {

        if (!Validator.isValidEmail(email) || !Validator.isValidPassword(pass)) {
            System.out.println("❌ Invalid input!");
            return;
        }

        User user = null;

        switch(role.toUpperCase()) {
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

        users.add(user);
        System.out.println("✅ Registered Successfully. Awaiting approval (if not admin).");
    }

    public static User login(String email, String pass) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(pass)) {
                if (!u.isApproved()) {
                    System.out.println("⏳ Waiting for admin approval.");
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
        System.out.println("✅ Property request sent for approval.");
    }

    public static void viewProperties() {
        for (Property p : properties) {
            System.out.println("ID: " + p.id + " Address: " + p.address + " Status: " + p.status);
        }
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

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Name: ");
                    String name = sc.nextLine();

                    System.out.print("Email: ");
                    String email = sc.nextLine();

                    System.out.print("Password: ");
                    String pass = sc.nextLine();

                    System.out.print("Role (ADMIN/OWNER/TENANT/STAFF): ");
                    String role = sc.nextLine();

                    AuthService.register(name, email, pass, role);
                    break;

                case 2:
                    System.out.print("Email: ");
                    email = sc.nextLine();

                    System.out.print("Password: ");
                    pass = sc.nextLine();

                    User user = AuthService.login(email, pass);

                    if (user != null) {
                        handleUser(user);
                    }
                    break;

                case 3:
                    System.out.println("👋 Goodbye!");
                    return;
            }
        }
    }

    static void handleUser(User user) {

        while (true) {
            user.showMenu();
            int choice = sc.nextInt();

            if (user instanceof Owner) {
                switch(choice) {
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
                }
            }

            else if (user instanceof Tenant) {
                switch(choice) {
                    case 2:
                        System.out.println("✅ Complaint registered.");
                        break;

                    case 4:
                        return;
                }
            }

            else if (user instanceof Admin) {
                switch(choice) {
                    case 1:
                        for (User u : AuthService.users) {
                            if (!u.isApproved()) {
                                u.approve();
                                System.out.println("Approved: " + u.name);
                            }
                        }
                        break;

                    case 3:
                        return;
                }
            }
        }
    }
}