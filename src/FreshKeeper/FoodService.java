package FreshKeeper;

import java.util.ArrayList;

public class FoodService {
    private ArrayList<Food> foodList;

    public FoodService() {
        this.foodList = new ArrayList<>();
    }

    // 식재료 등록
    public void addFood(Food food) {
        foodList.add(food);
    }

    // 전체 식재료 조회
    public ArrayList<Food> getAllFoods() {
        return foodList;
    }

    // 특정 ID의 식재료 조회
    public Food getFoodById(int id) {
        for (Food food : foodList) {
            if (food.getId() == id) {
                return food;
            }
        }
        return null;
    }

    // 식재료 삭제 (ID 기반)
    public boolean deleteFoodById(int id) {
        for (int i = 0; i < foodList.size(); i++) {
            if (foodList.get(i).getId() == id) {
                foodList.remove(i);
                return true;
            }
        }
        return false;
    }

    // 식재료 목록이 비어있는지 확인
    public boolean isEmpty() {
        return foodList.isEmpty();
    }

    // 식재료 개수
    public int getSize() {
        return foodList.size();
    }
}
