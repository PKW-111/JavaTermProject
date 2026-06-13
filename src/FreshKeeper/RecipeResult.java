package FreshKeeper;

import java.util.ArrayList;

public class RecipeResult {
    private Recipe recipe;
    private int matchScore;
    private ArrayList<String> missingIngredients;

    public RecipeResult(Recipe recipe, int matchScore, ArrayList<String> missingIngredients) {
        this.recipe = recipe;
        this.matchScore = matchScore;
        this.missingIngredients = missingIngredients;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public ArrayList<String> getMissingIngredients() {
        return missingIngredients;
    }

    public int getMissingCount() {
        return missingIngredients.size();
    }

    // 추천 메뉴 조리 (재료 수량 감소)
    public void cook(FoodService foodService) {
        ArrayList<String> ingredients = recipe.getIngredients();

        for (String ingredient : ingredients) {
            Food food = foodService.findFoodByName(ingredient);
            if (food != null && !food.getStatus().equals("만료")) {
                food.decreaseQuantity(1);
            }
        }
    }

    @Override
    public String toString() {
        int total = recipe.getIngredients().size();
        int available = total - missingIngredients.size();
        return String.format("%s (점수: %d점) [보유: %d/%d]",
                recipe.getName(), matchScore, available, total);
    }
}
