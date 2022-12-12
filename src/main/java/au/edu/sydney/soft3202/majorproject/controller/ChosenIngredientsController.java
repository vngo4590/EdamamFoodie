package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestWrapper;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.presenter.ChosenIngredientsPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ChosenIngredientsController
    implements ModelController<Map<String, IngredientRequestWrapper>> {
  @FXML protected VBox chosenIngredientScreen;
  @FXML protected VBox chosenIngredientsContainer;
  private ChosenIngredientsPresenter chosenIngredientsPresenter;


  private static final Logger LOGGER = LogManager.getLogger(ChosenIngredientsController.class);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Load Presenter
    chosenIngredientsPresenter = new ChosenIngredientsPresenter(this);
  }

  @Override
  public void loadDetails(Map<String, IngredientRequestWrapper> input) {
    // Load to view of each ingredient value
    for (Map.Entry<String, IngredientRequestWrapper> entry : input.entrySet()) {
      try {
        FXMLLoader loader =
            ScreenLoaderUtils.loadModelViewController(
                ScreenType.INGREDIENT_REQUEST_SCREEN, entry.getValue());
        ScreenLoaderUtils.screenContextLoad(chosenIngredientsContainer, loader.getRoot(), false);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void onResetIngredient(ActionEvent event) {
    LOGGER.info("Reset Button Clicked");
    chosenIngredientsPresenter.resetIngredient();
    resetIngredients();
  }

  public void resetIngredients() {
    this.chosenIngredientsContainer.getChildren().clear();
    LOGGER.info("Resetting Ingredient Completed");
  }
}
