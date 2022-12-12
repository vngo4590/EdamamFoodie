package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.presenter.EnterIngredientScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

public class EnterIngredientScreenController implements Initializable {
  @FXML protected Button ingrSubmitButton;
  @FXML protected Button ingrResetButton;
  @FXML protected Button ingrAddButton;
  @FXML protected VBox enterIngrScreen;
  @FXML protected TextField ingrTextField;
  @FXML protected ScrollPane scrollIngrList;
  @FXML protected VBox ingrContainer;
  @FXML protected Text ingredientListText;

  private static final Logger LOGGER = LogManager.getLogger(EnterIngredientScreenController.class);

  private ActionEvent currentEvent;
  private EnterIngredientScreenPresenter enterIngredientScreenPresenter;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Set node to resize to match with the scroll pane
    this.scrollIngrList.setFitToWidth(true);
    this.enterIngredientScreenPresenter = new EnterIngredientScreenPresenter(this);
  }

  public void onSubmit(ActionEvent event) {
    LOGGER.info("Submit Button Clicked");
    LOGGER.info("Submitting Ingredient Requests");
    currentEvent = event;
    disableButton(ingrSubmitButton, true);

    // Add process indicator to show loading status
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
    ScreenLoaderUtils.addProcessIndicatorToPane((Pane) bottomContainer);

    // Call to Presenter to make submission call
    this.enterIngredientScreenPresenter.makeFoodParserResponseCall();
  }

  /**
   * Load the ingredient list panel based on the obtained results.
   *
   * @param foodParserResponse the food information/data
   */
  public void loadFoodListScreenOnSuccess(FoodParserResponse foodParserResponse) {
    Node containerNode =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_CONTAINER_ID, currentEvent);
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    disableButton(ingrSubmitButton, false);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    try {
      FXMLLoader loader =
          ScreenLoaderUtils.loadModelViewController(
              ScreenType.FOOD_LIST_SCREEN, foodParserResponse);
      ScreenLoaderUtils.screenContextLoad((Pane) containerNode, loader.getRoot(), true);
    } catch (IOException e) {
      AlertFactory.createErrorAlert("Unable to load screen", "Screen data is not correct")
          .showAndWait();
    }
  }

  /**
   * Load the error alert based on a triggered event.
   *
   * @param cause The cause of the error
   * @param reason The reason of the error
   */
  public void showAndWaitOnErrorEvent(String cause, String reason) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    disableButton(ingrSubmitButton, false);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
  }

  /**
   * Load the error alert without an event.
   *
   * @param cause The cause of the error
   * @param reason The reason of the error
   */
  public void showAndWaitOnError(String cause, String reason) {
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
  }

  /** Given an ingredient, call to the presenter to add the ingredient to cache */
  public void onAddIngr(ActionEvent event) {
    LOGGER.info("Adding Ingredient " + ingrTextField.getText() + " to the list...");
    this.enterIngredientScreenPresenter.addIngredientAndCheckForAllergen(ingrTextField.getText());
  }

  /** Update the UI with the current list of ingredients */
  public void updateIngredientsList(String ingredients) {
    try {
      this.ingredientListText.setText(ingredients);
      LOGGER.info("These are the ingredients: " + ingredients);
    } catch (IllegalArgumentException e) {
      AlertFactory.createErrorAlert(e.getMessage(), "Ingredient Cannot be EMPTY!").showAndWait();
      LOGGER.info("Entered Invalid Ingredient");
    }
    this.ingrTextField.setText("");
  }

  /** Clean all ingredients */
  public void onResetIngredients(ActionEvent event) {
    this.enterIngredientScreenPresenter.resetIngredients();
  }

  /** Update the UI to clean the ingredients */
  public void resetIngredientsList() {
    this.ingredientListText.setText("You have not added any ingredients yet!");
    this.ingrTextField.setText("");
    LOGGER.info("Ingredient Reset");
  }

  public void disableButton(Button button, boolean toDisable) {
    ScreenLoaderUtils.setDisableButtons(List.of(button), toDisable);
  }

  /**
   * Ask the user to whether load the cache hit or not
   *
   * @param ingredient the input ingredient
   */
  public void askForAddingIngredientOnMatchingAllergen(String ingredient) {
    Alert alert =
        AlertFactory.createConfirmationAlert(
            "The ingredient matched an allergen!", "You are allergic to this", "Are you sure?");
    ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
    alert.getButtonTypes().clear();
    alert.getButtonTypes().add(yesButton);
    alert.getButtonTypes().add(noButton);
    LOGGER.info("Allergen Found!");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
      enterIngredientScreenPresenter.addIngredient(ingredient);
    }
  }
}
