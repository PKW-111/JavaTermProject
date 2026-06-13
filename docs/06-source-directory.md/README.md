# 6. 소스 디렉터리 구성

## 6.1 문서 목적

본 문서는 FreshKeeper Smart Fridge 프로젝트의 Java 소스 파일 구조와 각 클래스의 역할을 정리하기 위해 작성하였다.

소스 코드는 기능별로 클래스를 분리하여 유지보수성과 확장성을 높였으며, 모든 Java 클래스는 `src/FreshKeeper/` 경로에서 관리한다.

---

## 6.2 프로젝트 디렉터리 구조

```text
JavaTermProject/
├── src/
│   └── FreshKeeper/
│       ├── Main.java
│       ├── FreshKeeperGUI.java
│       ├── Food.java
│       ├── FoodService.java
│       ├── Recipe.java
│       ├── RecipeService.java
│       ├── RecipeResult.java
│       └── RoundedPanel.java
│
├── data/
│   └── foods.csv
│
├── docs/
│   ├── 0-project-overview.md
│   ├── 1-requirement-analysis.md
│   ├── 2-project-structure.md
│   ├── 3-feature-implementation.md
│   ├── 4-summary.md
│   ├── 5-testing-optimization.md
│   ├── 6-source-directory.md
│   ├── 7-release.md
│   ├── 8-collaborators.md
│   └── 9-AI-prompts.md
│
├── .gitignore
└── README.md
```

> `RoundedPanel`이 `FreshKeeperGUI.java` 내부 클래스라면 `RoundedPanel.java` 항목은 삭제한다.

---

## 6.3 Java 소스 파일 역할

| 파일                    | 구분         | 주요 역할                                 |
| --------------------- | ---------- | ------------------------------------- |
| `Main.java`           | 실행 클래스     | 프로그램 시작 및 Swing GUI 실행                |
| `FreshKeeperGUI.java` | 화면 클래스     | GUI 구성, 입력 처리, 버튼 이벤트, 테이블과 상태 카드 갱신  |
| `Food.java`           | 모델 클래스     | 식재료 정보 저장, 유통기한 계산, 상태 분류             |
| `FoodService.java`    | 서비스 클래스    | 식재료 등록, 조회, 검색, 수정, 삭제, CSV 저장 및 불러오기 |
| `Recipe.java`         | 모델 클래스     | 메뉴 이름과 필요한 재료 목록 저장                   |
| `RecipeService.java`  | 서비스 클래스    | 보유 재료 비교, 부족 재료 확인, 추천 점수 계산          |
| `RecipeResult.java`   | 결과 클래스     | 추천 결과 저장, 메뉴 조리, 식재료 수량 차감            |
| `RoundedPanel.java`   | GUI 보조 클래스 | 상태 카드와 추천 카드의 둥근 테두리 표현               |

---

## 6.4 패키지 구성

현재 모든 Java 파일은 다음 패키지를 사용한다.

```java
package FreshKeeper;
```

프로젝트 진행 중 패키지명이 서로 달라 발생하는 컴파일 오류를 방지하기 위해 모든 Java 파일에서 동일한 패키지명을 유지하였다.

Java의 일반적인 패키지 명명 규칙은 소문자 사용이지만, 현재 프로젝트에서는 기존 소스와의 호환성을 위해 `FreshKeeper` 패키지명을 유지하였다.

---

## 6.5 클래스 작성 원칙

* 하나의 클래스는 하나의 주요 책임을 가진다.
* 클래스명은 첫 글자를 대문자로 작성한다.
* 메서드와 변수는 역할을 알 수 있는 이름으로 작성한다.
* GUI 처리와 데이터 처리 기능을 분리한다.
* 유통기한 상태 계산은 `Food` 클래스에서 공통으로 처리한다.
* 파일 입출력은 `FoodService`에서 처리한다.
* 메뉴 추천 로직은 `RecipeService`에서 처리한다.
* 자동 생성 파일과 컴파일 결과물은 GitHub에 올리지 않는다.

---

## 6.6 데이터 파일

식재료 데이터는 다음 경로에 저장한다.

```text
data/foods.csv
```

CSV 저장 형식은 다음과 같다.

```text
id,name,category,quantity,location,expiryDate
1,계란,단백질,10,냉장,2026-06-20
```

`daysRemaining`과 `status` 값은 현재 날짜와 유통기한을 기준으로 계산되므로 CSV에 별도로 저장하지 않는다.

---

## 6.7 GitHub 업로드 제외 항목

`.gitignore`에는 다음과 같은 자동 생성 파일과 개인 개발 환경 파일을 등록한다.

```gitignore
out/
*.class
*.jar
.idea/
*.iml
.DS_Store
Thumbs.db
```

`out/` 폴더는 IntelliJ에서 생성되는 컴파일 결과물이므로 소스 코드 제출 대상에 포함하지 않는다.

---

## 6.8 소스 구조 점검 결과

| 점검 항목                               | 결과 |
| ----------------------------------- | -- |
| Java 소스가 `src/FreshKeeper/` 아래에 존재함 | 완료 |
| 모든 클래스의 패키지명이 동일함                   | 완료 |
| 클래스별 기능이 분리되어 있음                    | 완료 |
| GUI와 서비스 로직이 분리되어 있음                | 완료 |
| CSV 데이터 저장 경로가 구분되어 있음              | 완료 |
| 컴파일 결과물이 `.gitignore`에 등록되어 있음      | 완료 |
| GitHub에서 실제 소스 파일을 확인할 수 있음         | 완료 |
