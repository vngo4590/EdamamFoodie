package au.edu.sydney.soft3202.majorproject.controller.utils;

import au.edu.sydney.soft3202.majorproject.controller.*;
import javafx.fxml.Initializable;

public enum ScreenType {
  ENTER_ALLERGEN_SCREEN(
      "#enterAllergenScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/enter_allergen_screen.fxml",
      EnterAllergenScreenController.class),
  ENTER_INGR_SCREEN(
      "#enterIngrScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/enter_ingredient_screen.fxml",
      EnterIngredientScreenController.class),
  MAIN_SCREEN(
      "#mainScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/main_screen.fxml",
      MainScreenController.class),
  FOOD_LIST_SCREEN(
      "#foodListScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/food_list_screen.fxml",
      FoodListScreenController.class),
  FOOD_INFO_SCREEN(
      "#foodInfoScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/food_info_screen.fxml",
      FoodInfoScreenController.class),
  CHOSEN_INGREDIENT_SCREEN(
      "#chosenIngredientScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/chosen_ingredients_screen.fxml",
      ChosenIngredientsController.class),
  INGREDIENT_REQUEST_SCREEN(
      "#ingredientRequestDisplayScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/ingredient_request_display_screen.fxml",
      IngredientRequestDisplayController.class),
  NUTRIENT_VALUE_SCREEN(
      "#nutrientValueScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/nutrient_value_screen.fxml",
      NutrientValueScreenController.class),
  NUTRIENT_INFO_SCREEN(
      "#nutrientInfoScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/nutrient_info_screen.fxml",
      NutrientInfoScreenController.class),
  NUTRIENT_INFO_INGR(
      "#nutrientInfoIngrContainer",
      "/au/edu/sydney/soft3202/majorproject/screen/nutrient_info_ingredient.fxml",
      NutrientInfoIngredientController.class),
  NUTRIENT_STACKED_BAR_TAB_SCREEN(
      "#nutrientStackedBarTabsScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/nutrient_stacked_bar_tabs_screen.fxml",
      NutrientStackedBarTabsScreenController.class),
  NUTRIENT_STACKED_BAR_CHART(
      "#nutrientStackedBarContainer",
      "/au/edu/sydney/soft3202/majorproject/screen/nutrient_stacked_bar.fxml",
      NutrientStackedBarController.class),
  NUTRIENT_INFO_TABS_SCREEN(
      "#nutrientInfoTabsScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/nutrient_info_tabs_screen.fxml",
      NutrientInfoTabsScreenController.class),
  REDDIT_LOGIN_SCREEN(
      "#redditLoginScreen",
      "/au/edu/sydney/soft3202/majorproject/screen/reddit_loggin_screen.fxml",
      RedditLoginScreenController.class),
  ;

  private final String id;
  private final String screenPath;
  private final Class<? extends Initializable> controller;

  ScreenType(String id, String screenPath, Class<? extends Initializable> controller) {
    this.screenPath = screenPath;
    this.id = id;
    this.controller = controller;
  }

  public String getScreenPath() {
    return screenPath;
  }

  public String getId() {
    return id;
  }

  public Class<? extends Initializable> getController() {
    return controller;
  }
}
