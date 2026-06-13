package FreshKeeper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FreshKeeperGUI extends JFrame {
    private FoodService foodService;
    private RecipeService recipeService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final String CSV_FILE = "data/foods.csv";

    // 색상 정의
    private static final Color DARK_BG = new Color(10, 14, 39);
    private static final Color PANEL_BG = new Color(20, 25, 60);
    private static final Color NEON_CYAN = new Color(0, 212, 255);
    private static final Color NEON_GREEN = new Color(0, 255, 136);
    private static final Color TEXT_COLOR = new Color(224, 224, 224);
    private static final Color EXPIRED_COLOR = new Color(255, 0, 0);
    private static final Color TODAY_COLOR = new Color(255, 149, 0);
    private static final Color DANGER_COLOR = new Color(255, 255, 0);
    private static final Color WARNING_COLOR = new Color(0, 153, 255);
    private static final Color SAFE_COLOR = new Color(0, 204, 0);

    // 한글 표시 및 입력 안내 문구
    private static final String FONT_NAME = "맑은 고딕";
    private static final String NAME_HINT = "식재료 이름 예: 계란";
    private static final String CATEGORY_HINT = "카테고리 예: 단백질, 채소, 반찬";
    private static final String QUANTITY_HINT = "수량 예: 10";
    private static final String LOCATION_HINT = "보관 위치 예: 냉장, 냉동, 실온";
    private static final String EXPIRY_HINT = "유통기한 예: 2026-06-20";
    private static final String SEARCH_HINT = "검색어 예: 계란 또는 냉장";

    // GUI 컴포넌트
    private JTable foodTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, categoryField, quantityField, locationField, expiryDateField;
    private JTextField searchField;
    private JPanel recommendPanel;
    private JPanel recommendationContentPanel;
    private JLabel statusLabel;
    private JLabel expiredCountLabel;
    private JLabel dangerCountLabel;
    private JLabel warningCountLabel;
    private JLabel safeCountLabel;

    public FreshKeeperGUI() {
        setKoreanFont();
        foodService = new FoodService();
        recipeService = new RecipeService();

        setTitle("FreshKeeper Smart Fridge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setResizable(false);

        // 데이터 로드
        loadData();

        // UI 구성
        initUI();

        // 알림 표시
        showUrgentNotifications();

        setVisible(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveData();
            }
        });
    }

    private void initUI() {
        getContentPane().setBackground(DARK_BG);
        getContentPane().setLayout(new BorderLayout());

        // 상단: 제목 및 요약 카드
        JPanel topPanel = createTopPanel();
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // 중앙: 입력 패널, 테이블, 추천 메뉴 패널
        JPanel centerPanel = createCenterPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // 하단: 버튼 패널
        JPanel bottomPanel = createBottomPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARK_BG);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        // 제목
        JLabel titleLabel = new JLabel("FreshKeeper Smart Fridge");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 28));
        titleLabel.setForeground(NEON_CYAN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);

        topPanel.add(Box.createVerticalStrut(10));

        // 요약 카드 패널
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(1, 4, 10, 0));
        summaryPanel.setBackground(DARK_BG);
        summaryPanel.setMaximumSize(new Dimension(500, 80));

        summaryPanel.add(createStatusCard("만료", "0", EXPIRED_COLOR));
        summaryPanel.add(createStatusCard("위험", "0", DANGER_COLOR));
        summaryPanel.add(createStatusCard("주의", "0", WARNING_COLOR));
        summaryPanel.add(createStatusCard("안전", "0", SAFE_COLOR));

        statusLabel = new JLabel("");
        statusLabel.setForeground(TEXT_COLOR);

        JPanel statusWrapper = new JPanel(new BorderLayout());
        statusWrapper.setBackground(DARK_BG);
        statusWrapper.add(summaryPanel, BorderLayout.WEST);
        statusWrapper.add(statusLabel, BorderLayout.EAST);
        statusWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        topPanel.add(statusWrapper);
        return topPanel;
    }

    private RoundedPanel createStatusCard(String title, String count, Color color) {
        RoundedPanel card = new RoundedPanel(10, color, 2);
        card.setBackground(PANEL_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        titleLabel.setForeground(color);

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        countLabel.setForeground(color);

        switch (title) {
            case "만료" -> expiredCountLabel = countLabel;
            case "위험" -> dangerCountLabel = countLabel;
            case "주의" -> warningCountLabel = countLabel;
            case "안전" -> safeCountLabel = countLabel;
        }

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(countLabel);

        return card;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(DARK_BG);
        centerPanel.setLayout(new GridLayout(1, 3, 10, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 좌측: 입력 패널
        JPanel inputPanel = createInputPanel();
        centerPanel.add(inputPanel);

        // 중앙: 테이블
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel);

        // 우측: 메뉴 추천 패널
        recommendPanel = createRecommendPanel();
        centerPanel.add(recommendPanel);

        return centerPanel;
    }

    private JPanel createInputPanel() {
        RoundedPanel panel = new RoundedPanel(15, NEON_CYAN, 2);
        panel.setBackground(PANEL_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel formLabel = new JLabel("식재료 등록/수정");
        formLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        formLabel.setForeground(NEON_CYAN);
        panel.add(formLabel);
        panel.add(Box.createVerticalStrut(10));

        // 입력 필드
        nameField = createInputField(NAME_HINT);
        categoryField = createInputField(CATEGORY_HINT);
        quantityField = createInputField(QUANTITY_HINT);
        locationField = createInputField(LOCATION_HINT);
        expiryDateField = createInputField(EXPIRY_HINT);

        panel.add(nameField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(categoryField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(quantityField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(locationField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(expiryDateField);

        panel.add(Box.createVerticalStrut(15));

        // 검색 패널
        JLabel searchLabel = new JLabel("검색");
        searchLabel.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        searchLabel.setForeground(NEON_GREEN);
        panel.add(searchLabel);
        panel.add(Box.createVerticalStrut(5));

        searchField = createInputField(SEARCH_HINT);
        searchField.setBorder(BorderFactory.createLineBorder(NEON_GREEN, 1));
        searchField.setToolTipText("식재료 이름, 카테고리, 보관 위치를 입력해 검색합니다. 예: 계란, 냉장");
        panel.add(searchField);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JTextField createInputField(String hint) {
        JTextField field = new JTextField(hint);
        field.setBackground(new Color(30, 35, 70));
        field.setForeground(new Color(170, 170, 170));
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        field.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 1));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setToolTipText(hint);

        // Placeholder
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(new Color(150, 150, 150));
                    field.setText(hint);
                }
            }
        });

        return field;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(DARK_BG);

        // 테이블 생성
        String[] columnNames = {"ID", "이름", "카테고리", "수량", "보관위치", "유통기한", "남은일수", "상태"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        foodTable = new JTable(tableModel);
        foodTable.setBackground(PANEL_BG);
        foodTable.setForeground(TEXT_COLOR);
        foodTable.setGridColor(new Color(50, 50, 100));
        foodTable.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        foodTable.setRowHeight(25);
        foodTable.setSelectionBackground(new Color(0, 100, 150));

        // 헤더 스타일
        foodTable.getTableHeader().setBackground(new Color(15, 20, 50));
        foodTable.getTableHeader().setForeground(NEON_CYAN);
        foodTable.getTableHeader().setFont(new Font(FONT_NAME, Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane(foodTable);
        scrollPane.setBackground(PANEL_BG);
        scrollPane.getViewport().setBackground(PANEL_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 1));

        panel.add(scrollPane, BorderLayout.CENTER);

        refreshTable();

        return panel;
    }

    private JPanel createRecommendPanel() {
        RoundedPanel panel = new RoundedPanel(15, NEON_GREEN, 2);
        panel.setBackground(PANEL_BG);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("메뉴 추천");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        titleLabel.setForeground(NEON_GREEN);
        panel.add(titleLabel, BorderLayout.NORTH);

        recommendationContentPanel = new JPanel();
        recommendationContentPanel.setBackground(PANEL_BG);
        recommendationContentPanel.setLayout(new BoxLayout(recommendationContentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(recommendationContentPanel);
        scrollPane.setBackground(PANEL_BG);
        scrollPane.getViewport().setBackground(PANEL_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        // 추천 메뉴 업데이트
        updateRecommendations(recommendationContentPanel);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addBtn = createButton("등록", NEON_CYAN);
        JButton editBtn = createButton("수정", NEON_CYAN);
        JButton deleteBtn = createButton("삭제", NEON_CYAN);
        JButton searchBtn = createButton("검색", NEON_GREEN);
        JButton clearSearchBtn = createButton("초기화", NEON_GREEN);
        JButton cookBtn = createButton("조리하기", NEON_GREEN);
        JButton saveBtn = createButton("저장", NEON_CYAN);
        JButton exitBtn = createButton("종료", new Color(255, 100, 100));

        addBtn.addActionListener(e -> addFood());
        editBtn.addActionListener(e -> editFood());
        deleteBtn.addActionListener(e -> deleteFood());
        searchBtn.addActionListener(e -> searchFood());
        clearSearchBtn.addActionListener(e -> clearSearch());
        cookBtn.addActionListener(e -> cookRecipe());
        saveBtn.addActionListener(e -> saveData());
        exitBtn.addActionListener(e -> {
            saveData();
            System.exit(0);
        });

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(searchBtn);
        panel.add(clearSearchBtn);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(cookBtn);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(saveBtn);
        panel.add(exitBtn);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(DARK_BG);
        button.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(Math.min(color.getRed() + 30, 255),
                        Math.min(color.getGreen() + 30, 255),
                        Math.min(color.getBlue() + 30, 255)));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void addFood() {
        try {
            String name = getFieldValue(nameField, NAME_HINT);
            String category = getFieldValue(categoryField, CATEGORY_HINT);
            int quantity = Integer.parseInt(getFieldValue(quantityField, QUANTITY_HINT));
            String location = getFieldValue(locationField, LOCATION_HINT);
            LocalDate expiryDate = LocalDate.parse(getFieldValue(expiryDateField, EXPIRY_HINT), dateFormatter);

            if (name.isEmpty() || category.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Food food = new Food(name, category, quantity, location, expiryDate);
            foodService.addFood(food);

            clearInputFields();
            refreshTable();

            JOptionPane.showMessageDialog(this, "식재료가 등록되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "수량은 숫자로 입력하세요. 예: 10", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "날짜는 yyyy-MM-dd 형식으로 입력하세요. 예: 2026-06-20", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editFood() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "수정할 식재료를 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Food food = foodService.getFoodById(id);

            if (food == null) {
                JOptionPane.showMessageDialog(this, "식재료를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name = getFieldValue(nameField, NAME_HINT);
            String category = getFieldValue(categoryField, CATEGORY_HINT);
            int quantity = Integer.parseInt(getFieldValue(quantityField, QUANTITY_HINT));
            String location = getFieldValue(locationField, LOCATION_HINT);
            LocalDate expiryDate = LocalDate.parse(getFieldValue(expiryDateField, EXPIRY_HINT), dateFormatter);

            if (name.isEmpty() || category.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            foodService.updateFood(id, name, category, quantity, location, expiryDate);
            clearInputFields();
            refreshTable();

            JOptionPane.showMessageDialog(this, "식재료가 수정되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "수정 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFood() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "삭제할 식재료를 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(this, "'" + name + "'을(를) 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            foodService.deleteFoodById(id);
            refreshTable();
            JOptionPane.showMessageDialog(this, "식재료가 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchFood() {
        String searchTerm = getFieldValue(searchField, SEARCH_HINT);
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색어를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);

        ArrayList<Food> results = new ArrayList<>();
        results.addAll(foodService.searchByName(searchTerm));
        results.addAll(foodService.searchByCategory(searchTerm));
        results.addAll(foodService.searchByLocation(searchTerm));

        // 중복 제거
        ArrayList<Food> uniqueResults = new ArrayList<>();
        for (Food food : results) {
            boolean found = false;
            for (Food unique : uniqueResults) {
                if (unique.getId() == food.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                uniqueResults.add(food);
            }
        }

        for (Food food : uniqueResults) {
            addFoodRowToTable(food);
        }
    }

    private void clearSearch() {
        resetField(searchField, SEARCH_HINT);
        refreshTable();
    }

    private void cookRecipe() {
        ArrayList<RecipeResult> recommendations = recipeService.recommendRecipes(foodService);

        if (recommendations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "추천 메뉴가 없습니다.", "메뉴 없음", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] menuNames = new String[recommendations.size()];
        for (int i = 0; i < recommendations.size(); i++) {
            menuNames[i] = recommendations.get(i).getRecipe().getName() + " (" + recommendations.get(i).getMatchScore() + "점)";
        }

        JComboBox<String> comboBox = new JComboBox<>(menuNames);
        int result = JOptionPane.showConfirmDialog(this, comboBox, "조리할 메뉴를 선택하세요", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            RecipeResult selected = recommendations.get(comboBox.getSelectedIndex());
            selected.cook(foodService);

            // 수량이 0인 식재료 확인
            ArrayList<Food> zeroQuantityFoods = new ArrayList<>();
            for (Food food : foodService.getAllFoods()) {
                if (food.getQuantity() <= 0) {
                    zeroQuantityFoods.add(food);
                }
            }

            refreshTable();

            if (!zeroQuantityFoods.isEmpty()) {
                StringBuilder sb = new StringBuilder("다음 식재료의 수량이 0이 되었습니다:\n");
                for (Food food : zeroQuantityFoods) {
                    sb.append("- ").append(food.getName()).append("\n");
                }
                sb.append("\n이 식재료들을 삭제하시겠습니까?");

                int deleteResult = JOptionPane.showConfirmDialog(this, sb.toString(), "수량 0 확인", JOptionPane.YES_NO_OPTION);
                if (deleteResult == JOptionPane.YES_OPTION) {
                    for (Food food : zeroQuantityFoods) {
                        foodService.deleteFoodById(food.getId());
                    }
                    refreshTable();
                }
            }

            updateRecommendations(recommendationContentPanel);
            JOptionPane.showMessageDialog(this, "'" + selected.getRecipe().getName() + "'을(를) 조리했습니다!", "조리 완료", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Food food : foodService.getAllFoods()) {
            addFoodRowToTable(food);
        }
        updateStatusCards();
    }

    private void addFoodRowToTable(Food food) {
        Object[] row = {
                food.getId(),
                food.getName(),
                food.getCategory(),
                food.getQuantity(),
                food.getLocation(),
                food.getExpiryDate(),
                food.getDaysRemaining(),
                food.getStatus()
        };
        tableModel.addRow(row);
    }

    private void updateRecommendations(JPanel contentPanel) {
        contentPanel.removeAll();

        ArrayList<RecipeResult> recommendations = recipeService.recommendRecipes(foodService);

        for (RecipeResult result : recommendations) {
            RoundedPanel recipeCard = new RoundedPanel(10, NEON_GREEN, 1);
            recipeCard.setBackground(new Color(25, 30, 65));
            recipeCard.setLayout(new BoxLayout(recipeCard, BoxLayout.Y_AXIS));
            recipeCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            recipeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JLabel menuNameLabel = new JLabel("[메뉴] " + result.getRecipe().getName());
            menuNameLabel.setFont(new Font(FONT_NAME, Font.BOLD, 12));
            menuNameLabel.setForeground(NEON_GREEN);

            JLabel scoreLabel = new JLabel("점수: " + result.getMatchScore() + "점");
            scoreLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
            scoreLabel.setForeground(TEXT_COLOR);

            StringBuilder ingredientText = new StringBuilder();
            for (String ingredient : result.getRecipe().getIngredients()) {
                if (result.getMissingIngredients().contains(ingredient)) {
                    ingredientText.append("[부족] ").append(ingredient).append("  ");
                } else {
                    ingredientText.append("[보유] ").append(ingredient).append("  ");
                }
            }

            JLabel ingredientLabel = new JLabel(ingredientText.toString());
            ingredientLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 10));
            ingredientLabel.setForeground(new Color(180, 180, 180));

            recipeCard.add(menuNameLabel);
            recipeCard.add(Box.createVerticalStrut(3));
            recipeCard.add(scoreLabel);
            recipeCard.add(Box.createVerticalStrut(3));
            recipeCard.add(ingredientLabel);

            contentPanel.add(recipeCard);
            contentPanel.add(Box.createVerticalStrut(8));
        }

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUrgentNotifications() {
        ArrayList<Food> urgentFoods = foodService.getExpiredAndUrgentFoods();

        if (urgentFoods.isEmpty()) {
            return;
        }

        int expiredCount = 0, dangerCount = 0, warningCount = 0;

        for (Food food : urgentFoods) {
            String status = food.getStatus();
            if (status.equals("만료")) expiredCount++;
            else if (status.equals("위험")) dangerCount++;
            else if (status.equals("주의")) warningCount++;
        }

        if (expiredCount > 0 || dangerCount > 0) {
            StringBuilder sb = new StringBuilder("[유통기한 알림]\n\n");
            if (expiredCount > 0) sb.append("만료: ").append(expiredCount).append("개\n");
            if (dangerCount > 0) sb.append("위험: ").append(dangerCount).append("개\n");
            if (warningCount > 0) sb.append("주의: ").append(warningCount).append("개");

            JOptionPane.showMessageDialog(this, sb.toString(), "유통기한 임박/만료 알림", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateStatusCards() {
        int expiredCount = 0;
        int dangerCount = 0;
        int warningCount = 0;
        int safeCount = 0;

        for (Food food : foodService.getAllFoods()) {
            String status = food.getStatus();
            if (status.equals("만료")) {
                expiredCount++;
            } else if (status.equals("오늘까지") || status.equals("위험")) {
                dangerCount++;
            } else if (status.equals("주의")) {
                warningCount++;
            } else if (status.equals("안전")) {
                safeCount++;
            }
        }

        if (expiredCountLabel != null) expiredCountLabel.setText(String.valueOf(expiredCount));
        if (dangerCountLabel != null) dangerCountLabel.setText(String.valueOf(dangerCount));
        if (warningCountLabel != null) warningCountLabel.setText(String.valueOf(warningCount));
        if (safeCountLabel != null) safeCountLabel.setText(String.valueOf(safeCount));
    }

    private void clearInputFields() {
        resetField(nameField, NAME_HINT);
        resetField(categoryField, CATEGORY_HINT);
        resetField(quantityField, QUANTITY_HINT);
        resetField(locationField, LOCATION_HINT);
        resetField(expiryDateField, EXPIRY_HINT);
    }

    private void resetField(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(new Color(170, 170, 170));
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String value = field.getText().trim();
        if (value.equals(placeholder) || value.isEmpty()) {
            return "";
        }
        return value;
    }

    private void loadData() {
        foodService.loadFromCSV(CSV_FILE);
    }

    private void saveData() {
        foodService.saveToCSV(CSV_FILE);
    }


    private static void setKoreanFont() {
        Font normal = new Font(FONT_NAME, Font.PLAIN, 13);
        Font bold = new Font(FONT_NAME, Font.BOLD, 13);

        UIManager.put("Label.font", normal);
        UIManager.put("Button.font", bold);
        UIManager.put("TextField.font", normal);
        UIManager.put("TextArea.font", normal);
        UIManager.put("Table.font", normal);
        UIManager.put("TableHeader.font", bold);
        UIManager.put("OptionPane.messageFont", normal);
        UIManager.put("OptionPane.buttonFont", bold);
        UIManager.put("ComboBox.font", normal);
    }

    public static void main(String[] args) {
        setKoreanFont();
        SwingUtilities.invokeLater(() -> new FreshKeeperGUI());
    }
}
