package FreshKeeper;

import java.util.ArrayList;
import java.util.Collections;

public class RecipeService {
    private ArrayList<Recipe> recipes;

    public RecipeService() {
        this.recipes = new ArrayList<>();
        initializeRecipes();
    }

    // 기본 레시피 초기화
    private void initializeRecipes() {
        Recipe pasta = new Recipe("스파게티");
        pasta.addIngredient("파스타");
        pasta.addIngredient("토마토");
        pasta.addIngredient("마늘");
        pasta.addIngredient("올리브유");
        recipes.add(pasta);

        Recipe salad = new Recipe("샐러드");
        salad.addIngredient("양상추");
        salad.addIngredient("토마토");
        salad.addIngredient("오이");
        salad.addIngredient("당근");
        recipes.add(salad);

        Recipe soup = new Recipe("된장국");
        soup.addIngredient("된장");
        soup.addIngredient("두부");
        soup.addIngredient("파");
        soup.addIngredient("물");
        recipes.add(soup);

        Recipe friedRice = new Recipe("계란 볶음밥");
        friedRice.addIngredient("쌀");
        friedRice.addIngredient("계란");
        friedRice.addIngredient("당근");
        friedRice.addIngredient("양파");
        recipes.add(friedRice);

        Recipe pizza = new Recipe("피자");
        pizza.addIngredient("밀가루");
        pizza.addIngredient("토마토");
        pizza.addIngredient("치즈");
        pizza.addIngredient("양파");
        recipes.add(pizza);

        Recipe omelette = new Recipe("계란 계란말이");
        omelette.addIngredient("계란");
        omelette.addIngredient("소금");
        omelette.addIngredient("파");
        recipes.add(omelette);
    }

    // 추천 메뉴 조회
    public ArrayList<RecipeResult> recommendRecipes(FoodService foodService) {
        ArrayList<RecipeResult> results = new ArrayList<>();

        for (Recipe recipe : recipes) {
            ArrayList<String> missingIngredients = new ArrayList<>();
            ArrayList<String> recipeIngredients = recipe.getIngredients();

            // 각 레시피의 재료를 확인
            for (String ingredient : recipeIngredients) {
                if (!hasFoodByName(foodService, ingredient)) {
                    missingIngredients.add(ingredient);
                }
            }

            // 일치도 계산 (보유하고 있는 재료의 비율)
            int matchScore = 0;
            if (recipeIngredients.size() > 0) {
                int availableCount = recipeIngredients.size() - missingIngredients.size();
                matchScore = (availableCount * 100) / recipeIngredients.size();
            }

            RecipeResult result = new RecipeResult(recipe, matchScore, missingIngredients);
            results.add(result);
        }

        // 일치도로 정렬 (높은 순서)
        sortByMatchScore(results);

        return results;
    }

    // 음식명으로 보유 여부 확인
    private boolean hasFoodByName(FoodService foodService, String foodName) {
        ArrayList<Food> foods = foodService.getAllFoods();
        for (Food food : foods) {
            if (food.getName().toLowerCase().contains(foodName.toLowerCase()) ||
                    foodName.toLowerCase().contains(food.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // 일치도로 정렬
    private void sortByMatchScore(ArrayList<RecipeResult> results) {
        for (int i = 0; i < results.size() - 1; i++) {
            for (int j = i + 1; j < results.size(); j++) {
                if (results.get(i).getMatchScore() < results.get(j).getMatchScore()) {
                    RecipeResult temp = results.get(i);
                    results.set(i, results.get(j));
                    results.set(j, temp);
                }
            }
        }
    }

    public ArrayList<Recipe> getAllRecipes() {
        return recipes;
    }

    public Recipe getRecipeByIndex(int index) {
        if (index >= 0 && index < recipes.size()) {
            return recipes.get(index);
        }
        return null;
    }

    public int getRecipeCount() {
        return recipes.size();
    }
}
