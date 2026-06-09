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

    @Override
    public String toString() {
        int total = recipe.getIngredients().size();
        int available = total - missingIngredients.size();
        return String.format("%s (일치도: %d%%) [보유: %d/%d]",
                recipe.getName(), matchScore, available, total);
    }
}
