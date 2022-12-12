package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestWrapper;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodHintsItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.MeasuresItem;
import au.edu.sydney.soft3202.majorproject.model.type.MeasureType;
import au.edu.sydney.soft3202.majorproject.presenter.FoodInfoScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class FoodInfoScreenController implements ModelController<FoodHintsItem> {
  @FXML protected TextField measureQuantityTextField;
  @FXML protected MenuButton measureSelectionButton;
  @FXML protected Label foodLabel;
  @FXML protected GridPane foodInfoScreen;
  @FXML protected VBox measuresContainer;

  private String selectedMeasure;

  private FoodHintsItem foodHintsItem;

  private static final Logger LOGGER = LogManager.getLogger(FoodInfoScreenController.class);
  private FoodInfoScreenPresenter foodInfoScreenPresenter;
  private Scene currentScene;

  @Override
  public void loadDetails(FoodHintsItem input) {
    this.foodHintsItem = input;
    foodLabel.setText(this.foodHintsItem.getFood().getLabel());
    loadMeasureMenuItems(this.measureSelectionButton, this.foodHintsItem);
  }

  /**
  * Load all the measures available to the ingredient on {@link MenuItem}
  * */
  private void loadMeasureMenuItems(MenuButton menuButton, FoodHintsItem item) {
    menuButton.getItems().clear();
    for (MeasuresItem measuresItem : item.getMeasures()) {
      MeasureType measureType = MeasureType.getMeasureTypeByName(measuresItem.getLabel());
      if (measureType != null) {
        MenuItem menuItem = new MenuItem(measureType.getTypeName());
        menuItem.setOnAction(this::onMenuItemSelect);
        menuButton.getItems().add(menuItem);
      }
    }
  }

  /**
   * Update on selecting the current measure
   * */
  public void onMenuItemSelect(ActionEvent event) {
    this.selectedMeasure = ((MenuItem) event.getTarget()).getText();
    this.measureSelectionButton.setText(this.selectedMeasure);
    LOGGER.info("Selected Measure: " + this.selectedMeasure);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.measureQuantityTextField.setTextFormatter(ScreenLoaderUtils.numericIntTextFormatter());
    this.foodInfoScreenPresenter = new FoodInfoScreenPresenter(this);
  }

  /**
   * Call to the presenter to update the chosen ingredients and measures
   * */
  public void onChooseMeasure(ActionEvent event) {
    currentScene = ((Node) event.getTarget()).getScene();
    String measureQuantity = this.measureQuantityTextField.getText();
    LOGGER.info(String.format("""
                    Chosen Measure FoodId: %s
                    FoodLabel: %s
                    Measure: %s
                    Quantity: %s
                    """, foodHintsItem.getFood().getFoodId(),
            foodHintsItem.getFood().getLabel(), selectedMeasure, measureQuantity));
    try {
      foodInfoScreenPresenter.addMeasure(foodHintsItem.getFood().getFoodId(),
              foodHintsItem.getFood().getLabel(), selectedMeasure, Integer.parseInt(measureQuantity));
    } catch (Exception e) {
      LOGGER.error(e);
      AlertFactory.createErrorAlert(
              "Measure not Selected", "Please select a measure and then enter an amount")
          .showAndWait();
    }
  }

  /**
   * Update on the UI of the chosen ingredients / measures
   * */
  public void updateIngredientMeasureList(Map<String, IngredientRequestWrapper> ingredientRequestWrapperMap) {
    Pane containerPane = (Pane) currentScene.lookup(MainScreenController.MAIN_DISPLAY_CONTAINER_ID);
    try {
      FXMLLoader loader =
          ScreenLoaderUtils.loadModelViewController(
              ScreenType.CHOSEN_INGREDIENT_SCREEN,
                  ingredientRequestWrapperMap);
      if (containerPane == null) {
        throw new RuntimeException("No Container Pane");
      }
      ScreenLoaderUtils.screenContextLoad(containerPane, loader.getRoot(), true);
    } catch (IOException | RuntimeException e) {
      e.printStackTrace();
      LOGGER.warn("Unable to load chosen ingredient measure screen");
      AlertFactory.createErrorAlert(
              "Unable to load chosen ingredient measure screen",
              "Unable to load chosen ingredient measure screen")
          .showAndWait();
    }
  }
}
