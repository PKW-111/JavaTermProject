package FreshKeeper;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FoodService {
    private ArrayList<Food> foodList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FoodService() {
        this.foodList = new ArrayList<>();
    }

    public void addFood(Food food) {
        foodList.add(food);
    }

    public ArrayList<Food> getAllFoods() {
        return foodList;
    }

    public Food getFoodById(int id) {
        for (Food food : foodList) {
            if (food.getId() == id) {
                return food;
            }
        }
        return null;
    }

    public boolean deleteFoodById(int id) {
        for (int i = 0; i < foodList.size(); i++) {
            if (foodList.get(i).getId() == id) {
                foodList.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return foodList.isEmpty();
    }

    public int getSize() {
        return foodList.size();
    }

    // CSV 파일에서 식재료 로드
    public void loadFromCSV(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 6) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String category = parts[2];
                        int quantity = Integer.parseInt(parts[3]);
                        String location = parts[4];
                        LocalDate expiryDate = LocalDate.parse(parts[5], dateFormatter);

                        Food food = new Food(id, name, category, quantity, location, expiryDate);
                        foodList.add(food);
                    } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
                        System.out.println("CSV 파일 읽기 오류: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("파일 읽기 오류: " + e.getMessage());
        }
    }

    // 식재료를 CSV 파일에 저장
    public void saveToCSV(String filename) {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("id,name,category,quantity,location,expiryDate");

            for (Food food : foodList) {
                writer.printf("%d,%s,%s,%d,%s,%s\n",
                        food.getId(),
                        food.getName(),
                        food.getCategory(),
                        food.getQuantity(),
                        food.getLocation(),
                        food.getExpiryDate());
            }
        } catch (IOException e) {
            System.out.println("파일 저장 오류: " + e.getMessage());
        }
    }

    // 이름으로 검색
    public ArrayList<Food> searchByName(String name) {
        ArrayList<Food> results = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(food);
            }
        }
        return results;
    }

    // 카테고리로 검색
    public ArrayList<Food> searchByCategory(String category) {
        ArrayList<Food> results = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getCategory().toLowerCase().contains(category.toLowerCase())) {
                results.add(food);
            }
        }
        return results;
    }

    // 보관 위치로 검색
    public ArrayList<Food> searchByLocation(String location) {
        ArrayList<Food> results = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getLocation().toLowerCase().contains(location.toLowerCase())) {
                results.add(food);
            }
        }
        return results;
    }

    // 상태로 검색
    public ArrayList<Food> searchByStatus(String status) {
        ArrayList<Food> results = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getStatus().equals(status)) {
                results.add(food);
            }
        }
        return results;
    }

    // 식재료 수정
    public boolean updateFood(int id, String name, String category, int quantity, String location, LocalDate expiryDate) {
        Food food = getFoodById(id);
        if (food != null) {
            food.setName(name);
            food.setCategory(category);
            food.setQuantity(quantity);
            food.setLocation(location);
            food.setExpiryDate(expiryDate);
            return true;
        }
        return false;
    }

    // 만료 또는 임박 식재료 조회 (3일 이내)
    public ArrayList<Food> getExpiredAndUrgentFoods() {
        ArrayList<Food> results = new ArrayList<>();
        for (Food food : foodList) {
            long daysRemaining = food.getDaysRemaining();
            if (daysRemaining <= 3) {
                results.add(food);
            }
        }
        return results;
    }

    // 음식 이름으로 음식 객체 찾기 (부분 일치)
    public Food findFoodByName(String foodName) {
        for (Food food : foodList) {
            if (food.getName().toLowerCase().contains(foodName.toLowerCase()) ||
                    foodName.toLowerCase().contains(food.getName().toLowerCase())) {
                return food;
            }
        }
        return null;
    }

    // 수량이 0 이상인 식재료만 반환
    public void removeZeroQuantityFoods() {
        for (int i = foodList.size() - 1; i >= 0; i--) {
            if (foodList.get(i).getQuantity() <= 0) {
                foodList.remove(i);
            }
        }
    }
}
