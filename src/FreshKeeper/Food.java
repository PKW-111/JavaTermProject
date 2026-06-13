package FreshKeeper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Food {
    private static int nextId = 1;
    private int id;
    private String name;
    private String category;
    private int quantity;
    private String location;
    private LocalDate expiryDate;

    public Food(String name, String category, int quantity, String location, LocalDate expiryDate) {
        this.id = nextId++;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
        this.expiryDate = expiryDate;
    }

    public Food(int id, String name, String category, int quantity, String location, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
        this.expiryDate = expiryDate;
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        }
    }

    public long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, expiryDate);
    }

    public String getStatus() {
        long daysRemaining = getDaysRemaining();

        if (daysRemaining < 0) {
            return "만료";
        } else if (daysRemaining == 0) {
            return "오늘까지";
        } else if (daysRemaining >= 1 && daysRemaining <= 2) {
            return "위험";
        } else if (daysRemaining >= 3 && daysRemaining <= 6) {
            return "주의";
        } else {
            return "안전";
        }
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] %s | 카테고리: %s | 수량: %d | 위치: %s | 유통기한: %s | 남은 일수: %d | 상태: %s",
                id, name, category, quantity, location, expiryDate, getDaysRemaining(), getStatus());
    }
}
