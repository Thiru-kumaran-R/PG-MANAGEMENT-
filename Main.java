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

// ================= USERS =================

class Admin extends User {
    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, "ADMIN");
        approve();
    }

    public void showMenu() {
        System.out.println("\n--- ADMIN ---");
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
        System.out.println("\n--- OWNER ---");
        System.out.println("1. Add Property");
        System.out.println("2. View Properties");
        System.out.println("3. Update Property");
        System.out.println("4. Add Room to Property");
        System.out.println("5. Exit");
    }
}

class Tenant extends User {
    public Tenant(int id, String name, String email, String password) {
        super(id, name, email, password, "TENANT");
    }

    public void showMenu() {
        System.out.println("\n--- TENANT ---");
        System.out.println("1. View Properties & Rooms");
        System.out.println("2. Book Room");
        System.out.println("3. Raise Complaint");
        System.out.println("4. Pay Rent");
        System.out.println("5. View Payments");
        System.out.println("6. Exit");
    }
}

class Staff extends User {
    public Staff(int id, String name, String email, String password) {
        super(id, name, email, password, "STAFF");
    }

    public void showMenu() {
        System.out.println("\n--- STAFF ---");
        System.out.println("1. Exit");
    }
}

// ================= PROPERTY & ROOM =================

class Room {
    int roomId;
    String type;
    double rent;
    boolean available;
    String bookedBy;

    Room(int roomId, String type, double rent) {
        this.roomId = roomId;
        this.type = type;
        this.rent = rent;
        this.available = true;
    }
}

class Property {
    int id;
    String address;
    String facilities;
    String status;

    ArrayList<Room> rooms = new ArrayList<>();
    int freeRooms = 0;

    public Property(int id, String address, String facilities) {
        this.id = id;
        this.address = address;
        this.facilities = facilities;
        this.status = "PENDING";
    }

    public void addRoom(int roomId, String type, double rent) {
        rooms.add(new Room(roomId, type, rent));
        freeRooms++;
    }

    public void updateFreeRooms() {
        int count = 0;
        for (Room r : rooms) {
            if (r.available) count++;
        }
        freeRooms = count;
    }
}

class PropertyService {
    static ArrayList<Property> properties = new ArrayList<>();
    static int idCounter = 1;

    public static void addProperty(String addr, String facilities) {
        properties.add(new Property(idCounter++, addr, facilities));
        System.out.println("✅ Property added");
    }

    public static void updateProperty(int id, String addr, String fac) {
        for (Property p : properties) {
            if (p.id == id) {
                p.address = addr;
                p.facilities = fac;
                System.out.println("✅ Updated");
                return;
            }
        }
        System.out.println("❌ Not found");
    }

    public static void addRoomToProperty(int propertyId, int roomId, String type, double rent) {
        for (Property p : properties) {
            if (p.id == propertyId) {
                p.addRoom(roomId, type, rent);
                System.out.println("✅ Room added");
                return;
            }
        }
        System.out.println("❌ Property not found");
    }

    public static void viewProperties() {
        for (Property p : properties) {
            System.out.println("\nID: " + p.id +
                    " | Address: " + p.address +
                    " | Free Rooms: " + p.freeRooms);

            for (Room r : p.rooms) {
                System.out.println("   Room " + r.roomId +
                        " | " + r.type +
                        " | ₹" + r.rent +
                        " | Available: " + r.available +
                        " | Tenant: " + r.bookedBy);
            }
        }
    }

    public static void bookRoom(int propertyId, int roomId, String tenant) {
        for (Property p : properties) {
            if (p.id == propertyId) {
                for (Room r : p.rooms) {
                    if (r.roomId == roomId && r.available) {
                        r.available = false;
                        r.bookedBy = tenant;
                        p.updateFreeRooms();
                        System.out.println("✅ Room booked");
                        return;
                    }
                }
            }
        }
        System.out.println("❌ Booking failed");
    }
}

// ================= AUTH =================

class AuthService {
    static ArrayList<User> users = new ArrayList<>();
    static int idCounter = 1;

    public static void register(String name, String email, String pass, String role) {
        User u = null;

        switch (role) {
            case "ADMIN": u = new Admin(idCounter++, name, email, pass); break;
            case "OWNER": u = new Owner(idCounter++, name, email, pass); break;
            case "TENANT": u = new Tenant(idCounter++, name, email, pass); break;
            case "STAFF": u = new Staff(idCounter++, name, email, pass); break;
        }

        if (u != null) {
            users.add(u);
            System.out.println("✅ Registered");
        }
    }

    public static User login(String email, String pass) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(pass)) {
                if (!u.isApproved()) return null;
                return u;
            }
        }
        return null;
    }
}

// ================= COMPLAINT =================

class Complaint {
    int id;
    String desc;
    String status;

    Complaint(int id, String d) {
        this.id = id;
        this.desc = d;
        status = "PENDING";
    }
}

class ComplaintService {
    static ArrayList<Complaint> list = new ArrayList<>();
    static int idCounter = 1;

    public static void raise(String d) {
        list.add(new Complaint(idCounter++, d));
    }
}

// ================= PAYMENT =================

class Payment {
    String name;
    double amount;

    Payment(String n, double a) {
        name = n;
        amount = a;
    }
}

class PaymentService {
    static ArrayList<Payment> list = new ArrayList<>();

    public static void pay(String name, double amt) {
        list.add(new Payment(name, amt));
        System.out.println("✅ Paid ₹" + amt);
    }
}

// ================= MAIN =================

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("========= Welcome to PG Management ===========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int ch = sc.nextInt();

            switch (ch) {
                case 1:
                    sc.nextLine();
                    System.out.print("Name: ");
                    String n = sc.nextLine();

                    System.out.print("Email: ");
                    String e = sc.nextLine();

                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    // ✅ ROLE SELECTION MENU
                    System.out.println("Select Role:");
                    System.out.println("1. ADMIN");
                    System.out.println("2. OWNER");
                    System.out.println("3. TENANT");
                    System.out.println("4. STAFF");
                    System.out.print("Enter choice: ");

                    int roleChoice = sc.nextInt();
                    sc.nextLine();

                    String r = "";

                    switch (roleChoice) {
                        case 1: r = "ADMIN"; break;
                        case 2: r = "OWNER"; break;
                        case 3: r = "TENANT"; break;
                        case 4: r = "STAFF"; break;
                        default:
                            System.out.println("❌ Invalid role selected");
                            continue;
                    }

                    AuthService.register(n, e, p, r);
                    break;

                case 2:
                    sc.nextLine();
                    System.out.print("Email: ");
                    e = sc.nextLine();

                    System.out.print("Password: ");
                    p = sc.nextLine();

                    User user = AuthService.login(e, p);

                    if (user != null) {
                        handleUser(user);
                    } else {
                        System.out.println("❌ Login Failed");
                    }
                    break;

                case 3:
                    System.out.println("👋 Exiting...");
                    return;

                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= HANDLE USER =================
    static void handleUser(User u) {

        while (true) {
            u.showMenu();
            int ch = sc.nextInt();

            // ================= OWNER =================
            if (u instanceof Owner) {

                switch (ch) {
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

                    case 3:
                        System.out.print("Property ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("New Address: ");
                        String na = sc.nextLine();

                        System.out.print("New Facilities: ");
                        String nf = sc.nextLine();

                        PropertyService.updateProperty(id, na, nf);
                        break;

                    case 4:
                        System.out.print("Property ID: ");
                        int pid = sc.nextInt();

                        System.out.print("Room ID: ");
                        int rid = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Type: ");
                        String type = sc.nextLine();

                        System.out.print("Rent: ");
                        double rent = sc.nextDouble();

                        PropertyService.addRoomToProperty(pid, rid, type, rent);
                        break;

                    case 5:
                        return;

                    default:
                        System.out.println("❌ Invalid choice");
                }
            }

            // ================= TENANT =================
            else if (u instanceof Tenant) {

                switch (ch) {
                    case 1:
                        PropertyService.viewProperties();
                        break;

                    case 2:
                        System.out.print("Property ID: ");
                        int pid = sc.nextInt();

                        System.out.print("Room ID: ");
                        int rid = sc.nextInt();

                        PropertyService.bookRoom(pid, rid, u.name);
                        break;

                    case 3:
                        sc.nextLine();
                        System.out.print("Complaint: ");
                        String d = sc.nextLine();

                        ComplaintService.raise(d);
                        break;

                    case 4:
                        System.out.print("Amount: ");
                        double amt = sc.nextDouble();

                        PaymentService.pay(u.name, amt);
                        break;

                    case 6:
                        return;

                    default:
                        System.out.println("❌ Invalid choice");
                }
            }

            // ================= ADMIN =================
            else if (u instanceof Admin) {

                if (ch == 3) return;

                for (User usr : AuthService.users) {
                    if (!usr.isApproved()) {
                        usr.approve();
                    }
                }
                System.out.println("✅ Approved all users");
            }
        }
    }
}