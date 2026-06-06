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
    public void approve() { this.approved = true; }
    public void setPassword(String password) { this.password = password; }
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
        System.out.println("3. Approve Properties");
        System.out.println("4. Exit");
    }
}

class Owner extends User {
    public Owner(int id, String name, String email, String password) {
        super(id, name, email, password, "OWNER");
    }

    @Override
    public void showMenu() {
        System.out.println("\n===== OWNER MENU =====");
        System.out.println("1. Add Property");
        System.out.println("2. View Properties");
        System.out.println("3. Update Property");
        System.out.println("4. Delete Property");
        System.out.println("5. View Staff Members");
        System.out.println("6. Assign Task");
        System.out.println("7. Logout");
        System.out.print("Enter choice: ");
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
        System.out.println("4. View Payments");
        System.out.println("5. Submit KYC");
        System.out.println("6. Vacate Room");   // ── NEW ──
        System.out.println("7. Exit");
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

class Task {
    int taskId;
    String taskName;
    int staffId;

    public Task(int taskId, String taskName, int staffId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.staffId = staffId;
    }
}

class TaskService {
    static ArrayList<Task> tasks = new ArrayList<>();
    static int taskCounter = 1;

    public static void assignTask(String taskName, int staffId) {
        boolean found = false;
        for (User u : AuthService.users) {
            if (u instanceof Staff && u.id == staffId) { found = true; break; }
        }
        if (!found) { System.out.println("❌ Staff ID Not Found"); return; }
        tasks.add(new Task(taskCounter++, taskName, staffId));
        System.out.println("✅ Task Assigned Successfully");
    }

    public static void viewTasks(int staffId) {
        boolean found = false;
        for (Task t : tasks) {
            if (t.staffId == staffId) {
                System.out.println("Task ID: " + t.taskId + " | Task: " + t.taskName);
                found = true;
            }
        }
        if (!found) System.out.println("No Tasks Assigned");
    }

    public static void updateTask(int taskId, boolean completed) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).taskId == taskId) {
                if (completed) { tasks.remove(i); System.out.println("✅ Task Completed"); }
                else System.out.println("Task Still Pending");
                return;
            }
        }
        System.out.println("❌ Task Not Found");
    }
}

class AuthService {
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<String> emails = new ArrayList<>();
    static int idCounter = 1;

    public static void register(String name, String email, String pass, String role) {
        if (emails.contains(email)) { System.out.println("Email already exists. Try another."); return; }
        User user = null;
        switch (role.toUpperCase()) {
            case "ADMIN":  user = new Admin(idCounter++, name, email, pass);  break;
            case "OWNER":  user = new Owner(idCounter++, name, email, pass);  break;
            case "TENANT": user = new Tenant(idCounter++, name, email, pass); break;
            case "STAFF":  user = new Staff(idCounter++, name, email, pass);  break;
        }
        if (user != null) {
            users.add(user);
            emails.add(email);
            System.out.println("Registered Successfully. Awaiting approval (if not admin).");
        }
    }

    public static User login(String email, String pass) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(pass)) {
                if (!u.isApproved()) { System.out.println("Waiting for admin approval."); return null; }
                System.out.println("✅ Login successful!");
                return u;
            }
        }
        System.out.println("❌ Invalid credentials!");
        return null;
    }

    public static void forgotPassword(String email, Scanner sc) {
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                System.out.print("Enter New Password: ");
                String newPass = sc.nextLine();
                System.out.print("Confirm Password: ");
                String confirmPass = sc.nextLine();
                if (newPass.equals(confirmPass)) { u.setPassword(newPass); System.out.println("✅ Password Updated Successfully"); }
                else System.out.println("❌ Passwords do not match");
                return;
            }
        }
        System.out.println("❌ Email not registered");
    }

    public static void viewStaffMembers() {
        boolean found = false;
        System.out.println("\n--- Staff Members ---");
        for (User u : users) {
            if (u instanceof Staff) { System.out.println("ID: " + u.id + " | Name: " + u.name); found = true; }
        }
        if (!found) System.out.println("No Staff Registered");
    }
}

class Validator {
    public static boolean isValidName(String name) { return name != null && name.matches("^[a-zA-Z ]+$"); }
    public static boolean isValidEmail(String email) { return email.contains("@") && email.endsWith(".com"); }
    public static boolean isValidPassword(String pass) { return pass.length() >= 6; }

    // ── Card validators ───────────────────────────────────────────────────────
    public static boolean isValidCardNumber(String c) { return c != null && c.matches("\\d{16}"); }
    public static boolean isValidExpiryMonth(String m) {
        if (m == null || !m.matches("\\d{1,2}")) return false;
        int v = Integer.parseInt(m); return v >= 1 && v <= 12;
    }
    public static boolean isValidExpiryYear(String y) {
        if (y == null || !y.matches("\\d{4}")) return false;
        return Integer.parseInt(y) >= 2024;
    }
    public static boolean isValidCVV(String cvv) { return cvv != null && cvv.matches("\\d{3}"); }
    public static boolean isValidAmount(String a) {
        try { return Double.parseDouble(a) > 0; } catch (NumberFormatException e) { return false; }
    }

    // ── Vacate date validator ─────────────────────────────────────────────────
    public static boolean isValidDay(String d) {
        if (d == null || !d.matches("\\d{1,2}")) return false;
        int v = Integer.parseInt(d); return v >= 1 && v <= 31;
    }
}

class PropertyService {
    static ArrayList<Property> properties = new ArrayList<>();
    static int idCounter = 1;

    public static void addProperty(String addr, String facilities, int rooms) {
        properties.add(new Property(idCounter++, addr, facilities, rooms));
        System.out.println("✅ Property added and sent for approval.");
    }

    public static void viewProperties() {
        if (properties.isEmpty()) { System.out.println("No properties available."); return; }
        for (Property p : properties) p.display();
    }

    public static void updateProperty(int id, String newAddr, String newFac, int newRooms) {
        for (Property p : properties) {
            if (p.id == id) { p.address = newAddr; p.facilities = newFac; p.availableRooms = newRooms;
                System.out.println("✅ Property updated successfully."); return; }
        }
        System.out.println("❌ Property not found.");
    }

    public static void deleteProperty(int id) {
        for (Property p : properties) {
            if (p.id == id) { properties.remove(p); System.out.println("✅ Property deleted successfully."); return; }
        }
        System.out.println("❌ Property not found.");
    }

    public static void approveProperty(int id) {
        for (Property p : properties) {
            if (p.id == id) { p.status = "APPROVED"; System.out.println("✅ Property approved successfully."); return; }
        }
        System.out.println("❌ Property not found.");
    }

    public static void viewPendingProperties() {
        boolean found = false;
        for (Property p : properties) { if (p.status.equals("PENDING")) { p.display(); found = true; } }
        if (!found) System.out.println("No pending properties.");
    }
}

class Property {
    int id; String address; String facilities; int availableRooms; String status;

    public Property(int id, String address, String facilities, int availableRooms) {
        this.id = id; this.address = address; this.facilities = facilities;
        this.availableRooms = availableRooms; this.status = "PENDING";
    }

    public void display() {
        System.out.println("ID: " + id + " | Address: " + address + " | Facilities: " + facilities
                + " | Available Rooms: " + availableRooms + " | Status: " + status);
    }
}

class Complaint {
    int id; String description; String status;
    public Complaint(int id, String desc) { this.id = id; this.description = desc; this.status = "PENDING"; }
}

class ComplaintService {
    static ArrayList<Complaint> complaints = new ArrayList<>();
    static int idCounter = 1;

    public static void raiseComplaint(String desc) { complaints.add(new Complaint(idCounter++, desc)); System.out.println("✅ Complaint submitted."); }

    public static void viewComplaints() {
        for (Complaint c : complaints)
            System.out.println("ID: " + c.id + " | " + c.description + " | Status: " + c.status);
    }

    public static void resolveComplaint(int id) {
        for (Complaint c : complaints) {
            if (c.id == id) { c.status = "RESOLVED"; System.out.println("✅ Complaint resolved."); return; }
        }
        System.out.println("❌ Complaint not found.");
    }
}

class Room {
    int roomId; String type; double rent; boolean available; String bookedBy;

    Room(int roomId, String type, double rent) {
        this.roomId = roomId; this.type = type; this.rent = rent; this.available = true; this.bookedBy = null;
    }
}

class RoomService {
    static ArrayList<Room> rooms = new ArrayList<>();

    static {
        rooms.add(new Room(101, "Single", 11000));
        rooms.add(new Room(104, "Double", 9000));
        rooms.add(new Room(133, "Triple", 7000));
        rooms.add(new Room(243, "Double", 9000));
        rooms.add(new Room(333, "Triple", 9500));
    }

    public static void viewRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room r : rooms) {
            if (r.available)
                System.out.println("Room ID: " + r.roomId + " | Type: " + r.type + " | Rent: ₹" + r.rent);
        }
    }

    public static void bookRoom(int roomId, String tenantName) {
        for (Room r : rooms) {
            if (r.roomId == roomId && r.available) {
                r.available = false; r.bookedBy = tenantName;
                System.out.println("✅ Room booked successfully!"); return;
            }
        }
        System.out.println("❌ Room not available.");
    }

    public static void viewMyRoom(String tenantName) {
        for (Room r : rooms) {
            if (tenantName.equals(r.bookedBy)) {
                System.out.println("✅ Your Room:");
                System.out.println("Room ID: " + r.roomId + " | Type: " + r.type + " | Rent: ₹" + r.rent);
                return;
            }
        }
        System.out.println("❌ No room booked.");
    }

    // ── Returns monthly rent for tenant's booked room, or -1 if none ─────────
    public static double getRentForTenant(String tenantName) {
        for (Room r : rooms) { if (tenantName.equals(r.bookedBy)) return r.rent; }
        return -1;
    }

    // ── Frees the room when tenant vacates ───────────────────────────────────
    public static void vacateRoom(String tenantName) {
        for (Room r : rooms) {
            if (tenantName.equals(r.bookedBy)) { r.available = true; r.bookedBy = null; return; }
        }
    }
}

class Payment {
    String tenantName; double amount;
    Payment(String tenantName, double amount) { this.tenantName = tenantName; this.amount = amount; }
}

class PaymentService {
    static ArrayList<Payment> payments = new ArrayList<>();

    // ── Card-based payment flow ───────────────────────────────────────────────
    public static void payRentWithCard(Scanner sc, String tenantName) {

        System.out.println("\n--- 💳 RENT PAYMENT ---");

        String cardNumber;
        while (true) {
            System.out.print("Enter 16-digit Card Number: ");
            cardNumber = sc.nextLine().trim();
            if (Validator.isValidCardNumber(cardNumber)) break;
            System.out.println("❌ Invalid card number. Must be exactly 16 digits.");
        }

        String expiryMonth;
        while (true) {
            System.out.print("Enter Expiry Month (MM): ");
            expiryMonth = sc.nextLine().trim();
            if (Validator.isValidExpiryMonth(expiryMonth)) break;
            System.out.println("❌ Invalid month. Enter a value between 01 and 12.");
        }

        String expiryYear;
        while (true) {
            System.out.print("Enter Expiry Year (YYYY): ");
            expiryYear = sc.nextLine().trim();
            if (Validator.isValidExpiryYear(expiryYear)) break;
            System.out.println("❌ Invalid year. Enter a 4-digit year (e.g. 2025).");
        }

        String cvv;
        while (true) {
            System.out.print("Enter CVV (3 digits): ");
            cvv = sc.nextLine().trim();
            if (Validator.isValidCVV(cvv)) break;
            System.out.println("❌ Invalid CVV. Must be exactly 3 digits.");
        }

        String amountStr;
        while (true) {
            System.out.print("Enter Amount to Pay (₹): ");
            amountStr = sc.nextLine().trim();
            if (Validator.isValidAmount(amountStr)) break;
            System.out.println("❌ Invalid amount. Please enter a positive number.");
        }

        double amount = Double.parseDouble(amountStr);
        payments.add(new Payment(tenantName, amount));

        String maskedCard = "**** **** **** " + cardNumber.substring(12);
        System.out.println("\n========================================");
        System.out.println("       ✅ PAYMENT SUCCESSFUL! 🎉         ");
        System.out.println("========================================");
        System.out.println("  Card    : " + maskedCard);
        System.out.println("  Expiry  : " + expiryMonth + "/" + expiryYear);
        System.out.println("  Amount  : ₹" + amount);
        System.out.println("========================================");
    }

    public static void viewPayments(String tenantName) {
        double total = 0; boolean found = false;
        System.out.println("\n--- Payment History ---");
        for (Payment p : payments) {
            if (p.tenantName.equals(tenantName)) { System.out.println("₹ " + p.amount); total += p.amount; found = true; }
        }
        if (!found) System.out.println("❌ No payments found.");
        else System.out.println("Total Paid: ₹" + total);
    }
}

// ── VacateService ─────────────────────────────────────────────────────────────

class VacateService {

    public static void processVacate(Scanner sc, String tenantName) {

        System.out.println("\n--- 🏠 VACATE ROOM ---");

        double monthlyRent = RoomService.getRentForTenant(tenantName);
        if (monthlyRent < 0) {
            System.out.println("❌ You have no room booked. Nothing to vacate.");
            return;
        }

        System.out.println("Your current monthly rent: ₹" + monthlyRent);

        // Ask vacate day
        int day = 0;
        while (true) {
            System.out.print("Enter vacate date (day of month, e.g. 10 or 20): ");
            String input = sc.nextLine().trim();
            if (Validator.isValidDay(input)) { day = Integer.parseInt(input); break; }
            System.out.println("❌ Invalid date. Enter a day between 1 and 31.");
        }

        // Refund rule
        double refundPercent = (day > 15) ? 70 : 30;
        double refundAmount  = (refundPercent / 100.0) * monthlyRent;

        // Ordinal suffix for display
        String suffix = (day == 1) ? "st" : (day == 2) ? "nd" : (day == 3) ? "rd" : "th";

        System.out.println("\n--- Vacate Summary ---");
        System.out.println("Vacate Date       : " + day + suffix + " of this month");
        System.out.println("Monthly Rent      : ₹" + monthlyRent);
        System.out.println("Refund Percentage : " + (int) refundPercent + "%");
        System.out.printf( "Refund Amount     : ₹%.2f%n", refundAmount);
        System.out.print("\nConfirm vacate? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("↩ Vacate cancelled. Your room is still active.");
            return;
        }

        RoomService.vacateRoom(tenantName);

        System.out.println("\n========================================");
        System.out.println("      ✅ ROOM VACATED SUCCESSFULLY!      ");
        System.out.println("========================================");
        System.out.println("  Vacate Day  : " + day + suffix);
        System.out.println("  Refund      : " + (int) refundPercent + "% of ₹" + monthlyRent);
        System.out.printf( "  Amount Due  : ₹%.2f (will be returned to you)%n", refundAmount);
        System.out.println("========================================");
    }
}

// ─────────────────────────────────────────────────────────────────────────────

class KYC {
    String phoneNumber; String panNumber; String aadhaarNumber;
    public KYC(String phoneNumber, String panNumber, String aadhaarNumber) {
        this.phoneNumber = phoneNumber; this.panNumber = panNumber; this.aadhaarNumber = aadhaarNumber;
    }
}

class KYCService {
    static ArrayList<KYC> kycList = new ArrayList<>();

    public static void submitKYC(String phone, String pan, String aadhaar) {
        if (!phone.matches("\\d{10}"))            { System.out.println("❌ Invalid Phone Number"); return; }
        if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]")){ System.out.println("❌ Invalid PAN Number");   return; }
        if (!aadhaar.matches("\\d{12}"))           { System.out.println("❌ Invalid Aadhaar Number"); return; }
        kycList.add(new KYC(phone, pan, aadhaar));
        System.out.println("✅ KYC Submitted Successfully");
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

            if (!sc.hasNextInt()) { System.out.println("Invalid input. Please enter a number."); sc.next(); continue; }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: handleRegister(); break;
                case 2: handleLogin();    break;
                case 3: System.out.println("👋 Goodbye!"); return;
                default: System.out.println("Invalid menu option.");
            }
        }
    }

    // ================= REGISTER =================
    static void handleRegister() {
        String name, email, pass; int roleChoice;

        while (true) {
            System.out.print("Name: "); name = sc.nextLine();
            if (Validator.isValidName(name)) break;
            System.out.println("Name must contain only letters and spaces.");
        }

        while (true) {
            System.out.print("Email: "); email = sc.nextLine();
            if (!Validator.isValidEmail(email)) { System.out.println("Invalid email format."); continue; }
            if (AuthService.emails.contains(email)) { System.out.println("Email already exists."); continue; }
            break;
        }

        while (true) {
            System.out.print("Password: "); pass = sc.nextLine();
            if (Validator.isValidPassword(pass)) break;
            System.out.println("Password must be at least 6 characters.");
        }

        while (true) {
            System.out.println("Select Role:");
            System.out.println("1. ADMIN"); System.out.println("2. OWNER");
            System.out.println("3. TENANT"); System.out.println("4. STAFF");
            System.out.print("Enter choice: ");
            if (sc.hasNextInt()) {
                roleChoice = sc.nextInt(); sc.nextLine();
                if (roleChoice >= 1 && roleChoice <= 4) break;
                System.out.println("Invalid role selection.");
            } else { System.out.println("Enter a valid number."); sc.next(); }
        }

        String role = switch (roleChoice) {
            case 1 -> "ADMIN"; case 2 -> "OWNER"; case 3 -> "TENANT"; case 4 -> "STAFF"; default -> "";
        };
        AuthService.register(name, email, pass, role);
    }

    // ================= LOGIN =================
    static void handleLogin() {
        while (true) {
            System.out.print("Email: "); String email = sc.nextLine();
            System.out.print("Password: "); String pass = sc.nextLine();

            User user = AuthService.login(email, pass);
            if (user != null) { handleUser(user); return; }

            System.out.println("\n❌ Invalid credentials!");
            System.out.println("1. Try Again"); System.out.println("2. Forgot Password"); System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");

            if (sc.hasNextInt()) {
                int choice = sc.nextInt(); sc.nextLine();
                switch (choice) {
                    case 1: break;
                    case 2: handleForgotPassword(); break;
                    case 3: return;
                    default: System.out.println("Invalid option.");
                }
            } else { System.out.println("Invalid input."); sc.next(); }
        }
    }

    // ================= USER MENU =================
    static void handleUser(User user) {

        while (true) {

            user.showMenu();

            if (!sc.hasNextInt()) { System.out.println("Invalid input."); sc.next(); continue; }

            int choice = sc.nextInt();

            // ================= OWNER =================
            if (user instanceof Owner) {
                switch (choice) {
                    case 1:
                        sc.nextLine();
                        System.out.print("Enter PG Address: "); String addr = sc.nextLine();
                        System.out.print("Enter Facilities: "); String fac = sc.nextLine();
                        System.out.print("Enter Available Rooms: "); int rooms = sc.nextInt();
                        PropertyService.addProperty(addr, fac, rooms);
                        break;
                    case 2: PropertyService.viewProperties(); break;
                    case 3:
                        System.out.print("Enter Property ID to update: "); int upId = sc.nextInt(); sc.nextLine();
                        System.out.print("New Address: "); String newAddr = sc.nextLine();
                        System.out.print("New Facilities: "); String newFac = sc.nextLine();
                        System.out.print("New Available Rooms: "); int newRooms = sc.nextInt();
                        PropertyService.updateProperty(upId, newAddr, newFac, newRooms);
                        break;
                    case 4:
                        System.out.print("Enter Property ID to delete: "); int delId = sc.nextInt();
                        PropertyService.deleteProperty(delId);
                        break;
                    case 5: AuthService.viewStaffMembers(); break;
                    case 6:
                        AuthService.viewStaffMembers(); sc.nextLine();
                        System.out.print("Enter Task Name: "); String taskName = sc.nextLine();
                        System.out.print("Enter Staff ID: "); int staffId = sc.nextInt();
                        TaskService.assignTask(taskName, staffId);
                        break;
                    case 7: return;
                    default: System.out.println("Invalid choice.");
                }
            }

            // ================= TENANT =================
            else if (user instanceof Tenant) {
                switch (choice) {

                    case 1:
                        RoomService.viewRooms();
                        sc.nextLine();
                        System.out.print("Do you want to book a room? (yes/no): ");
                        String ans = sc.nextLine();
                        if (ans.equalsIgnoreCase("yes")) {
                            System.out.print("Enter Room ID to book: ");
                            int roomId = sc.nextInt();
                            RoomService.bookRoom(roomId, user.name);
                        }
                        break;

                    case 2:
                        sc.nextLine();
                        System.out.print("Enter complaint: "); String desc = sc.nextLine();
                        ComplaintService.raiseComplaint(desc);
                        break;

                    // ── Card-based payment ────────────────────────────────
                    case 3:
                        sc.nextLine();
                        PaymentService.payRentWithCard(sc, user.name);
                        break;

                    case 4:
                        PaymentService.viewPayments(user.name);
                        break;

                    case 5:
                        sc.nextLine();
                        System.out.print("Enter Phone Number: "); String phone = sc.nextLine();
                        System.out.print("Enter PAN Number: ");   String pan   = sc.nextLine().toUpperCase();
                        System.out.print("Enter Aadhaar Number: "); String aadhaar = sc.nextLine();
                        KYCService.submitKYC(phone, pan, aadhaar);
                        break;

                    // ── Vacate Room ───────────────────────────────────────
                    case 6:
                        sc.nextLine();
                        VacateService.processVacate(sc, user.name);
                        break;

                    case 7: return;

                    default: System.out.println("Invalid choice.");
                }
            }

            // ================= ADMIN =================
            else if (user instanceof Admin) {
                switch (choice) {
                    case 1:
                        for (User u : AuthService.users) {
                            if (!u.isApproved()) { u.approve(); System.out.println("Approved: " + u.name); }
                        }
                        break;
                    case 2:
                        ComplaintService.viewComplaints();
                        System.out.print("Enter complaint ID to resolve: ");
                        if (sc.hasNextInt()) { int id = sc.nextInt(); ComplaintService.resolveComplaint(id); }
                        else { System.out.println("Invalid ID."); sc.next(); }
                        break;
                    case 3:
                        PropertyService.viewPendingProperties();
                        System.out.print("Enter Property ID to approve: "); int pid = sc.nextInt();
                        PropertyService.approveProperty(pid);
                        break;
                    case 4: return;
                    default: System.out.println("Invalid choice.");
                }
            }

            // ================= STAFF =================
            else if (user instanceof Staff) {
                switch (choice) {
                    case 1: TaskService.viewTasks(user.id); break;
                    case 2:
                        System.out.print("Enter Task ID: "); int taskId = sc.nextInt(); sc.nextLine();
                        System.out.print("Completed? (yes/no): "); String status = sc.nextLine();
                        TaskService.updateTask(taskId, status.equalsIgnoreCase("yes"));
                        break;
                    case 3: return;
                    default: System.out.println("Invalid Choice");
                }
            }
        }
    }

    // ================= FORGOT PASSWORD =================
    static void handleForgotPassword() {
        System.out.print("Enter your registered email: ");
        String email = sc.nextLine();
        AuthService.forgotPassword(email, sc);
    }
}