import java.io.*;
import java.util.*;

class EWasteItem {
    private int id;
    private String name;
    private double weight;
    private String category;
    private String disposal;

    public EWasteItem(int id, String name, double weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        classify();
    }

    private void classify() {
        String s = name.toLowerCase();
        if (s.contains("battery") || s.contains("lamp")) {
            category = "Hazardous";
            disposal = "Chemical Neutralization";
        } else if (s.contains("pcb") || s.contains("board") || s.contains("motherboard")) {
            category = "E-Board";
            disposal = "Metal Smelting";
        } else {
            category = "General";
            disposal = "Shredding & Recycling";
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public String getCategory() { return category; }
    public String getDisposal() { return disposal; }

    public String toFileString() {
        return id + "|" + name + "|" + weight + "|" + category + "|" + disposal;
    }

    public void display() {
        System.out.printf("%-6d %-18s %-10.2f %-15s %-25s%n",
                id, name, weight, category, disposal);
    }
}

public class EWasteManagement {
    private static final String FILE_NAME = "ewaste.txt";
    private static final ArrayList<EWasteItem> items = new ArrayList<>();
    private static final Queue<Integer> pickupQueue = new LinkedList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void addItem() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            for (EWasteItem item : items) {
                if (item.getId() == id) {
                    throw new IllegalArgumentException("Duplicate Item ID!");
                }
            }
            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter Weight (kg): ");
            double weight = Double.parseDouble(sc.nextLine().trim());

            if (name.isEmpty() || weight < 0) {
                throw new IllegalArgumentException("Invalid item details!");
            }

            EWasteItem item = new EWasteItem(id, name, weight);
            items.add(item);
            saveToFile();
            System.out.println("Item registered successfully!");
            System.out.println("Category: " + item.getCategory());
            System.out.println("Disposal: " + item.getDisposal());
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void displayItems() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        System.out.println("\n--- Registered E-Waste Items ---");
        System.out.printf("%-6s %-18s %-10s %-15s %-25s%n",
                "ID", "Name", "Weight", "Category", "Disposal Method");
        for (EWasteItem item : items) {
            item.display();
        }
    }

    public static void requestPickup() {
        try {
            System.out.print("Enter Item ID to queue for pickup: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            for (EWasteItem item : items) {
                if (item.getId() == id) {
                    pickupQueue.offer(id);
                    System.out.println("Item " + id + " added to pickup queue.");
                    return;
                }
            }
            System.out.println("Item ID not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid Item ID.");
        }
    }

    public static void processPickup() {
        if (pickupQueue.isEmpty()) {
            System.out.println("No pending pickup requests.");
        } else {
            System.out.println("Processed pickup for Item ID: " + pickupQueue.poll());
        }
    }

    public static void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (EWasteItem item : items) {
                out.println(item.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Unable to save records.");
        }
    }

    public static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length == 5) {
                    int id = Integer.parseInt(p[0]);
                    String name = p[1];
                    double weight = Double.parseDouble(p[2]);
                    items.add(new EWasteItem(id, name, weight));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Unable to load saved records.");
        }
    }

    public static void main(String[] args) {
        loadFromFile();
        while (true) {
            System.out.println("\n--- E-WASTE MANAGEMENT ---");
            System.out.println("1. Register Item");
            System.out.println("2. View All Items");
            System.out.println("3. Request Pickup");
            System.out.println("4. Process Pickup");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            String input = sc.nextLine();

            switch (input) {
                case "1": addItem(); break;
                case "2": displayItems(); break;
                case "3": requestPickup(); break;
                case "4": processPickup(); break;
                case "5":
                    saveToFile();
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
