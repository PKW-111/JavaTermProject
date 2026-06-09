package FreshKeeper;

import java.util.ArrayList;

public class Recipe {
    private String name;
    private ArrayList<String> ingredients;

    public Recipe(String name) {
        this.name = name;
        this.ingredients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    // 재료 추가
    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
    }

    @Override
    public String toString() {
        return name;
    }
}
