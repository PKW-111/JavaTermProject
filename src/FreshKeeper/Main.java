package FreshKeeper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static FoodService foodService;
    private static RecipeService recipeService;
    private static Scanner scanner;
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        foodService = new FoodService();
        recipeService = new RecipeService();
        scanner = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("   FreshKeeper: 냉장고 유통기한 관리");
        System.out.println("======================================\n");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    addFood();
                    break;
                case 2:
                    displayAllFoods();
                    break;
                case 3:
                    deleteFood();
                    break;
                case 4:
                    recommendRecipes();
                    break;
                case 5:
                    System.out.println("\n프로그램을 종료합니다.");
                    running = false;
                    break;
                default:
                    System.out.println("❌ 잘못된 입력입니다. 다시 선택해주세요.\n");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("======= 메인 메뉴 =======");
        System.out.println("1. 식재료 등록");
        System.out.println("2. 식재료 조회");
        System.out.println("3. 식재료 삭제");
        System.out.println("4. 메뉴 추천");
        System.out.println("5. 종료");
        System.out.println("========================");
    }

    private static void addFood() {
        System.out.println("\n========= 식재료 등록 =========");

        System.out.print("식재료 이름: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ 이름을 입력해주세요.\n");
            return;
        }

        System.out.print("카테고리 (예: 채소, 육류, 유제품): ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("❌ 카테고리를 입력해주세요.\n");
            return;
        }

        int quantity = getIntInput("수량: ");
        if (quantity <= 0) {
            System.out.println("❌ 수량은 0보다 커야 합니다.\n");
            return;
        }

        System.out.print("보관 위치 (예: 냉장실, 냉동실): ");
        String location = scanner.nextLine().trim();
        if (location.isEmpty()) {
            System.out.println("❌ 보관 위치를 입력해주세요.\n");
            return;
        }

        LocalDate expiryDate = getDateInput("유통기한 (yyyy-MM-dd): ");
        if (expiryDate == null) {
            return;
        }

        Food food = new Food(name, category, quantity, location, expiryDate);
        foodService.addFood(food);

        System.out.println("✅ 식재료가 등록되었습니다!\n");
    }

    private static void displayAllFoods() {
        System.out.println("\n========= 식재료 조회 =========");

        if (foodService.isEmpty()) {
            System.out.println("❌ 등록된 식재료가 없습니다.\n");
            return;
        }

        ArrayList<Food> foods = foodService.getAllFoods();
        System.out.println("");
        for (Food food : foods) {
            System.out.println(food);
        }
        System.out.println("");
    }

    private static void deleteFood() {
        System.out.println("\n========= 식재료 삭제 =========");

        if (foodService.isEmpty()) {
            System.out.println("❌ 등록된 식재료가 없습니다.\n");
            return;
        }

        displayAllFoods();

        int id = getIntInput("삭제할 식재료 ID: ");

        Food food = foodService.getFoodById(id);
        if (food == null) {
            System.out.println("❌ 해당 ID의 식재료를 찾을 수 없습니다.\n");
            return;
        }

        System.out.println("\n삭제할 식재료: " + food.getName());
        System.out.print("정말 삭제하시겠습니까? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            foodService.deleteFoodById(id);
            System.out.println("✅ 식재료가 삭제되었습니다!\n");
        } else {
            System.out.println("⏹️  삭제가 취소되었습니다.\n");
        }
    }

    private static void recommendRecipes() {
        System.out.println("\n========= 메뉴 추천 =========");

        ArrayList<RecipeResult> recommendations = recipeService.recommendRecipes(foodService);

        System.out.println("\n📋 추천 메뉴 목록:\n");
        for (int i = 0; i < recommendations.size(); i++) {
            RecipeResult result = recommendations.get(i);
            System.out.printf("%d. %s\n", i + 1, result);
        }

        System.out.print("\n📌 상세 정보를 보고 싶은 메뉴 번호 (취소: 0): ");
        int choice = getIntInput("");

        if (choice == 0) {
            System.out.println("");
            return;
        }

        if (choice < 1 || choice > recommendations.size()) {
            System.out.println("❌ 잘못된 메뉴 번호입니다.\n");
            return;
        }

        RecipeResult selected = recommendations.get(choice - 1);
        displayRecipeDetail(selected);
        System.out.println("");
    }

    private static void displayRecipeDetail(RecipeResult result) {
        Recipe recipe = result.getRecipe();
        System.out.println("\n---------- 메뉴 상세 정보 ----------");
        System.out.println("메뉴명: " + recipe.getName());
        System.out.println("일치도: " + result.getMatchScore() + "%");

        ArrayList<String> ingredients = recipe.getIngredients();
        ArrayList<String> missingIngredients = result.getMissingIngredients();

        System.out.println("\n🛒 필요 재료:");
        for (String ingredient : ingredients) {
            if (missingIngredients.contains(ingredient)) {
                System.out.println("  ❌ " + ingredient + " (없음)");
            } else {
                System.out.println("  ✅ " + ingredient);
            }
        }

        if (missingIngredients.size() > 0) {
            System.out.println("\n⚠️  부족 재료:");
            for (String missing : missingIngredients) {
                System.out.println("  - " + missing);
            }
        } else {
            System.out.println("\n✅ 모든 재료를 보유하고 있습니다!");
        }
        System.out.println("------------------------------------");
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ 숫자를 입력해주세요.");
            }
        }
    }

    private static LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                LocalDate date = LocalDate.parse(input, dateFormatter);
                return date;
            } catch (Exception e) {
                System.out.println("❌ 날짜 형식이 올바르지 않습니다. (예: 2024-12-31)");
            }
        }
    }
}
