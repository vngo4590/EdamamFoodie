package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.EnterAllergenScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenWrapper;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnterAllergenScreenPresenter {
  private final AllergenWrapper allergenWrapper;
  private final EnterAllergenScreenController enterAllergenScreenController;
  private static final Logger LOGGER = LogManager.getLogger(EnterAllergenScreenPresenter.class);

  public EnterAllergenScreenPresenter(EnterAllergenScreenController enterAllergenScreenController) {
    this.allergenWrapper = EdamamRepositorySingleton.getInstance();
    this.enterAllergenScreenController = enterAllergenScreenController;
  }

  /**
   * This method checks if there is an allergen currently selected. If there is, then update the UI
   * of the allergen
   */
  public void updateAllergenOnLoad() {
    String currentAllergen = allergenWrapper.getAllergen();
    boolean isAllergenValid =
            currentAllergen != null && !currentAllergen.isEmpty() & !currentAllergen.isBlank();
    LOGGER.debug("Allergen " + currentAllergen + " currently exists");
    if (isAllergenValid) {
      enterAllergenScreenController.updateAddedAllergenOnUI(currentAllergen);
    }
  }

  /**
   * Call to the {@link AllergenWrapper} to add the allergen and then update the UI controlled by
   * {@link EnterAllergenScreenController}
   *
   * @param allergen The allergen
   */
  public void addAllergen(String allergen) {
    try {
      allergenWrapper.addAllergen(allergen);
      LOGGER.debug("Allergen " + allergen + " has been added");
      enterAllergenScreenController.updateAddedAllergenOnUI(allergen);
    } catch (Exception e) {
      Platform.runLater(
          () ->
              enterAllergenScreenController.showAndWaitOnError(
                  "Add Allergen Error", e.getMessage()));
    }
  }

  /**
   * If the current allergen is valid, we then call to the UI controlled by {@link
   * EnterAllergenScreenController} to load the next screen
   */
  public void submitAllergen() {
    // Check if the user has entered any allergen
    String currentAllergen = allergenWrapper.getAllergen();
    boolean isAllergenValid =
        currentAllergen != null && !currentAllergen.isEmpty() & !currentAllergen.isBlank();
    if (isAllergenValid) {
      enterAllergenScreenController.loadEnterIngredientScreenOnSuccess();
    } else {
      Platform.runLater(
          () ->
              enterAllergenScreenController.showAndWaitOnError(
                  "Allergen Missing", "You Must Enter An Allergen!"));
    }
  }
}
