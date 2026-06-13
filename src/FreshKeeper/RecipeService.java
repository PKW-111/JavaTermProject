package FreshKeeper;

import java.util.ArrayList;

public class RecipeService {
    private ArrayList<Recipe> recipes;

    public RecipeService() {
        this.recipes = new ArrayList<>();
        initializeRecipes();
    }

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

    // 고도화된 추천 메뉴 조회
    public ArrayList<RecipeResult> recommendRecipes(FoodService foodService) {
        ArrayList<RecipeResult> results = new ArrayList<>();

        for (Recipe recipe : recipes) {
            ArrayList<String> missingIngredients = new ArrayList<>();
            ArrayList<Food> availableFoods = new ArrayList<>();
            ArrayList<String> recipeIngredients = recipe.getIngredients();

            int score = 0;

            // 각 레시피의 재료 확인
            for (String ingredient : recipeIngredients) {
                Food food = foodService.findFoodByName(ingredient);

                if (food == null) {
                    // 재료 없음
                    missingIngredients.add(ingredient);
                    score -= 3;
                } else if (food.getStatus().equals("만료")) {
                    // 만료된 재료는 사용 불가
                    missingIngredients.add(ingredient);
                    score -= 3;
                } else {
                    // 재료 보유 (+10점)
                    availableFoods.add(food);
                    score += 10;

                    // 유통기한 3일 이내 재료 (+5점)
                    if (food.getDaysRemaining() >= 0 && food.getDaysRemaining() <= 3) {
                        score += 5;
                    }
                }
            }

            // 모든 재료 보유 시 추가 +10점
            if (missingIngredients.isEmpty() && recipeIngredients.size() > 0) {
                score += 10;
            }

            // 최소 0점으로 설정
            if (score < 0) {
                score = 0;
            }

            RecipeResult result = new RecipeResult(recipe, score, missingIngredients);
            results.add(result);
        }

        // 점수로 정렬 (높은 순서)
        sortByScore(results);

        return results;
    }

    // 점수로 정렬
    private void sortByScore(ArrayList<RecipeResult> results) {
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
