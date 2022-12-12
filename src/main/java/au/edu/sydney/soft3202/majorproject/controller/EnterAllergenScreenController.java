package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.presenter.EnterAllergenScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterAllergenScreenController implements Initializable {
  @FXML protected VBox enterAllergenScreen;
  @FXML protected TextField allergenTextField;
  @FXML protected Button allergenAddButton;
  @FXML protected ScrollPane scrollAllergenList;
  @FXML protected VBox allergenContainer;
  @FXML protected Text addedAllergenText;
  @FXML protected Button allergenSubmitButton;
  private EnterAllergenScreenPresenter enterAllergenScreenPresenter;
  private static final Logger LOGGER = LogManager.getLogger(EnterAllergenScreenController.class);
  private ActionEvent currentEvent;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    enterAllergenScreenPresenter = new EnterAllergenScreenPresenter(this);
    enterAllergenScreenPresenter.updateAllergenOnLoad();
  }

  public void onAllergenAdd() {
    LOGGER.info("Adding allergen " + allergenTextField.getText());
    enterAllergenScreenPresenter.addAllergen(allergenTextField.getText());
  }

  public void onSubmitAllergen(ActionEvent event) {
    currentEvent = event;
    enterAllergenScreenPresenter.submitAllergen();
  }

  /**
   * Load the error alert without an event.
   *
   * @param cause The cause of the error
   * @param reason The reason of the error
   */
  public void showAndWaitOnError(String cause, String reason) {
    LOGGER.error(reason);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
  }

  /** Update the UI with the current Allergen */
  public void updateAddedAllergenOnUI(String allergen) {
    this.addedAllergenText.setText(allergen);
    LOGGER.info("This is the allergen: " + allergen);
    this.allergenTextField.setText("");
  }

  /** Should be called by the presenter to load the next enter ingredient screen on the panel */
  public void loadEnterIngredientScreenOnSuccess() {
    Node containerNode =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_CONTAINER_ID, currentEvent);
    try {
      FXMLLoader loader = ScreenLoaderUtils.loadModelViewController(ScreenType.ENTER_INGR_SCREEN);
      ScreenLoaderUtils.screenContextLoad((Pane) containerNode, loader.getRoot(), true);
    } catch (IOException e) {
      AlertFactory.createErrorAlert("Unable to load screen", "Screen data is not correct")
          .showAndWait();
    }
  }
}
