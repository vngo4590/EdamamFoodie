package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodHintsItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.model.type.Nutrient;
import au.edu.sydney.soft3202.majorproject.presenter.FoodListScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FoodListScreenController implements ModelController<FoodParserResponse> {
  @FXML protected MenuButton nutrientMenuButton;
  @FXML protected TextField nutrientTextFieldMin;
  @FXML protected TextField nutrientTextFieldMax;
  @FXML protected Button nutrientAddButton;
  @FXML protected Button nutrientResetButton;
  @FXML protected ScrollPane scrollNutrientList;
  @FXML protected VBox nutrientContainer;
  @FXML protected Text nutrientListText;
  @FXML protected Button foodListSubmitButton;
  @FXML protected Button foodNextPageButton;
  @FXML protected TabPane foodDetailsTabPane;
  @FXML protected VBox foodListScreen;

  private String currentUrl;
  private FoodListScreenPresenter foodListScreenPresenter;

  private static final Logger LOGGER = LogManager.getLogger(FoodListScreenController.class);
  private ActionEvent currentEvent;
  private VBox mainIngredientContainer;
  private ModelController<NutrientsResponse> modelController;
  private String selectedNutrient;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    foodListScreenPresenter = new FoodListScreenPresenter(this);
    loadNutrientMenuItem(this.nutrientMenuButton);
    this.nutrientTextFieldMax.setTextFormatter(ScreenLoaderUtils.numericIntTextFormatter());
    this.nutrientTextFieldMin.setTextFormatter(ScreenLoaderUtils.numericIntTextFormatter());
  }

  @Override
  public void loadDetails(FoodParserResponse input) {
    /*
     * From the food parser input,
     * we get the list of hints which contains the food information
     * */
    List<FoodHintsItem> hints = input.getHints();
    // We need to ensure that the list is not null
    if (hints != null) {
      // Add new tab
      Tab tab = new Tab();
      LOGGER.info("Loading Page " + foodListScreenPresenter.getPageCounter());
      tab.setText("Page " + foodListScreenPresenter.getPageCounter());
      foodDetailsTabPane.getTabs().add(tab);
      tab.setClosable(false);

      // Load info in new container
      ScrollPane scrollPane = new ScrollPane();
      scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
      tab.setContent(scrollPane);
      VBox container = new VBox();
      container.setId("foodDetailsContainerPage" + foodListScreenPresenter.getPageCounter());
      scrollPane.setContent(container);
      mainIngredientContainer = container;

      loadHints(hints);
      foodListScreenPresenter.incrementPageCounter();
    }

    /*
     * Disable button to avoid the next link
     * */
    if (input.getLinks() == null
        || input.getLinks().getNext() == null
        || input.getLinks().getNext().getHref() == null) {
      ScreenLoaderUtils.setDisableButtons(List.of(foodNextPageButton), true);
    } else {
      this.currentUrl = input.getLinks().getNext().getHref();
      ScreenLoaderUtils.setDisableButtons(List.of(foodNextPageButton), false);
    }
    ScreenLoaderUtils.setDisableButtons(List.of(foodListSubmitButton), false);
  }

  private void loadHints(List<FoodHintsItem> items) {
    for (FoodHintsItem item : items) {
      foodListScreenPresenter.loadFoodItemScreen(item);
    }
  }

  /**
  * Load food information & nutrient info FXML
   * @param item the food information
  * */
  public Node loadScreenOnFoodItem(FoodHintsItem item) {
    try {
      LOGGER.info("Loading item: " + item.getFood().getLabel());
      FXMLLoader loader =
          ScreenLoaderUtils.loadModelViewController(ScreenType.FOOD_INFO_SCREEN, item);
      return loader.getRoot();
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
    return null;
  }

  /**
   * Update the UI container
   * @param node UI element to add to the ingredient container panel
   * */
  public void addNodeToMainIngredientContainer(Node node) {
    mainIngredientContainer.getChildren().add(node);
  }

  /**
   * Submit the current ingredient requests by calling to the Presenter
   * */
  public void onSubmit(ActionEvent event) {
    currentEvent = event;
    Scene scene = ((Node) event.getTarget()).getScene();
    Pane containerPane = (Pane) scene.lookup(MainScreenController.MAIN_DISPLAY_CONTAINER_ID);
    ScreenLoaderUtils.setDisableButtons(List.of(foodListSubmitButton), true);
    // Build the query to get proper request
    FXMLLoader loader;
    Pane bottomContainer =
        (Pane)
            ScreenLoaderUtils.getNodeByIdFromEvent(
                MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
    ScreenLoaderUtils.addProcessIndicatorToPane(bottomContainer);
    try {
      loader = ScreenLoaderUtils.loadModelViewController(ScreenType.NUTRIENT_INFO_TABS_SCREEN);
      modelController = loader.getController();

      if (foodListScreenPresenter.isIngredientRequestBodyEmpty()) {
        ScreenLoaderUtils.setDisableButtons(List.of(foodListSubmitButton), false);
        AlertFactory.createErrorAlert("No Measures Selected", "No Measures Selected").showAndWait();
        LOGGER.info("No Measures Selected.");
        ScreenLoaderUtils.removeProcessIndicatorFromPane(bottomContainer);
        return;
      }

      LOGGER.info("Submitting Requests for ingredients");
      ScreenLoaderUtils.screenContextLoad(containerPane, loader.getRoot(), true);
      foodListScreenPresenter.submitIngredientRequests();
    } catch (Exception e) {
      cleanupAfterException(e);
    }
  }

  public void cleanupAfterException(Exception e) {
    Pane bottomContainer =
        (Pane)
            ScreenLoaderUtils.getNodeByIdFromEvent(
                MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane(bottomContainer);
    e.printStackTrace();
    LOGGER.error(e);
  }

  public void cleanupAfterCacheHitException(Exception e) {
    cleanupAfterException(e);
    AlertFactory.createErrorAlert(
            "Data processing error", "An error occurred while trying to process data")
        .show();
    ScreenLoaderUtils.setDisableButtons(List.of(foodListSubmitButton), false);
  }

  /** Ask the user to whether load the cache hit or not */
  public void askForCacheHit(IngredientRequestBody requestBody) {
    Alert alert =
        AlertFactory.createConfirmationAlert(
            "Cache Hit",
            "Cache hit for this data",
            "Use cache, or request fresh data from the API?");
    LOGGER.info("Cache hit found!");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      foodListScreenPresenter.loadCacheHit(true, requestBody);
    } else if (result.isPresent()) {
      foodListScreenPresenter.loadCacheHit(false, requestBody);
    }
  }

  /** Call to the presenter to load the next page of the ingredient list */
  public void onLoadNextPage(ActionEvent event) {
    currentEvent = event;
    if (this.currentUrl != null) {
      ScreenLoaderUtils.setDisableButtons(List.of(foodNextPageButton), true);
      LOGGER.info("Loading Next Ingredient Page");

      Node bottomContainer =
          ScreenLoaderUtils.getNodeByIdFromEvent(
              MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
      ScreenLoaderUtils.addProcessIndicatorToPane((Pane) bottomContainer);
      foodListScreenPresenter.loadFoodParserResponseWithURL(this.currentUrl);
    }
  }

  /** Update the ingredient screen with new ingredient information/data */
  public void updateIngredientPage(FoodParserResponse responseBody) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    ScreenLoaderUtils.setDisableButtons(List.of(foodNextPageButton), false);
    loadDetails(responseBody);
  }

  public void updateNutrientResponseInfoScreen(NutrientsResponse responseBody) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    ScreenLoaderUtils.setDisableButtons(List.of(foodListSubmitButton), false);
    if (modelController != null) {
      modelController.loadDetails(responseBody);
    }
  }

  public void showAndWaitOnErrorEvent(String cause, String reason) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
    ScreenLoaderUtils.setDisableButtons(List.of(foodListSubmitButton), false);
  }

  /**
  * Load all available nutrients on the nutrient menu button to allow the user to select the nutrient
  * */
  private void loadNutrientMenuItem(MenuButton menuButton) {
    menuButton.getItems().clear();
    for (Nutrient nutrient : Nutrient.values()) {
      MenuItem menuItem = new MenuItem(nutrient.getTypeName());
      menuItem.setOnAction(this::onMenuItemSelect);
      menuButton.getItems().add(menuItem);
    }
  }

  /**
  * On select the nutrient, we save the nutrient value
  * */
  public void onMenuItemSelect(ActionEvent event) {
    this.selectedNutrient = ((MenuItem) event.getTarget()).getText();
    this.nutrientMenuButton.setText(this.selectedNutrient);
    LOGGER.info("Selected Nutrient: " + this.selectedNutrient);
  }

  public void showAndWaitOnError(String cause, String reason) {
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
  }

  public void updateNutrientValue(String nutrientString) {
    // Updating the nutrient list text field
    if (!nutrientString.isEmpty()) {
      this.nutrientListText.setText(nutrientString);
    }
    this.nutrientTextFieldMax.setText("");
    this.nutrientTextFieldMin.setText("");
    LOGGER.info("Added Nutrients: " + nutrientString);
  }

  /**
   * Add the nutrient value by calling the presenter to update the value
   * */
  public void onAddNutrient(ActionEvent event) {
    if (this.selectedNutrient == null) {
      AlertFactory.createErrorAlert("No Nutrient Selected", "Please select a nutrient")
          .showAndWait();
      LOGGER.info("No Nutrient Selected");
      return;
    }
    try {
      // Call to presenter to update the ingredients
      foodListScreenPresenter.addNutrient(
          this.selectedNutrient,
          this.nutrientTextFieldMin.getText(),
          this.nutrientTextFieldMax.getText());
      LOGGER.info("Added Nutrient " + this.selectedNutrient);
    } catch (IllegalArgumentException e) {
      AlertFactory.createErrorAlert(
              e.getMessage(), "Please make sure that you have entered the correct number!")
          .showAndWait();
      LOGGER.warn("Invalid Nutrient Input");
    }
  }

  public void onResetNutrient(ActionEvent event) {
    foodListScreenPresenter.resetNutrient();
  }

  public void resetNutrientList() {
    // Updating the nutrient list text field after reset
    this.nutrientListText.setText("You have not added any Nutrients yet!");
    this.nutrientTextFieldMax.setText("");
    this.nutrientTextFieldMin.setText("");
    LOGGER.info("All Nutrients have been reset");
  }
}
