package model;

/**
 * Represents optional add-on services for flight bookings.
 */
public class AddOn {
    private String addOnId;
    private String name;
    private String description;
    private double price;
    private AddOnCategory category;
    private boolean isAvailable;

    public AddOn() {
        this.isAvailable = true;
    }

    public AddOn(String addOnId, String name, String description, double price, AddOnCategory category) {
        this();
        this.addOnId = addOnId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    public String getAddOnId() { return addOnId; }
    public void setAddOnId(String addOnId) { this.addOnId = addOnId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public AddOnCategory getCategory() { return category; }
    public void setCategory(AddOnCategory category) { this.category = category; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "AddOn{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}

/**
 * Enum for add-on categories
 */
enum AddOnCategory {
    BAGGAGE("Extra Baggage"),
    SEAT("Seat Selection"),
    MEAL("Meal Upgrade"),
    INSURANCE("Travel Insurance"),
    LOUNGE("Lounge Access");

    private final String displayName;

    AddOnCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 