package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.EnterIngredientScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenIngredientObserver;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.model.thread.ThreadPoolSingleton;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

public class EnterIngredientScreenPresenter {
  private static final Logger LOGGER = LogManager.getLogger(EnterIngredientScreenPresenter.class);
  private final EnterIngredientScreenController enterIngredientScreenController;
  private EdamamRepository edamamRepository;
  private final EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder;

  public EnterIngredientScreenPresenter(
      EnterIngredientScreenController enterIngredientScreenController) {
    this.enterIngredientScreenController = enterIngredientScreenController;
    this.edamamRepository = EdamamRepositorySingleton.getInstance();
    this.edamamFoodApiQueryBuilder = edamamRepository.getQueryBuilder();
  }
  /**
   * On call, this method will first check if there is an allergen. If the ingredient matches with
   * an existing allergen then we would need to call the controller to ask for the user decision
   * whether to add the ingredient or not. Else, we just add the ingredient like normal
   *
   * @param ingredient The ingredient to be added
   */
  public void addIngredientAndCheckForAllergen(String ingredient) {
    AllergenIngredientObserver allergenIngredientObserver =
        getAllergenIngredientObserver(ingredient);
    this.edamamRepository.checkIngredientSelectedAllergen(ingredient, allergenIngredientObserver);
  }
  /**
   * Returns the Allergen observer that responses to the 2 cases:
   *
   * <ol>
   *   <li>The ingredient matches with the allergen. If there is a matching allergen, then we call
   *       to the controller to ask the user to decide whether to add the ingredient or not
   *   <li>The ingredient does not match with the allergen. Then we would just add the ingredient
   *       like normal
   * </ol>
   */
  private AllergenIngredientObserver getAllergenIngredientObserver(String ingredient) {
    return new AllergenIngredientObserver() {
      @Override
      public void onMatchingAllergen() {
        // Call to the UI to check if the user wants
        // to add the ingredient
        LOGGER.debug("The ingredient matched with an allergen. Asking the user for approval.");
        Platform.runLater(
            () ->
                enterIngredientScreenController.askForAddingIngredientOnMatchingAllergen(
                    ingredient));
      }

      @Override
      public void onNotMatchingAllergen() {
        LOGGER.debug(
            "The ingredient does not match with any allergen. Attempting to add the ingredient.");
        // Add ingredient like normal
        addIngredient(ingredient);
      }
    };
  }

  /**
   * This calls to the {@link EdamamFoodApiQueryBuilder} to add the ingredient without checking for
   * any allergen
   *
   * @param ingredient The ingredient to be added
   */
  public void addIngredient(String ingredient) {
    try {
      edamamFoodApiQueryBuilder.addIngredient(ingredient);
      LOGGER.debug("Ingredient " + ingredient + " has been added");
      Platform.runLater(
          () ->
              enterIngredientScreenController.updateIngredientsList(
                  edamamFoodApiQueryBuilder.getIngredients()));
    } catch (Exception e) {
      Platform.runLater(
          () ->
              enterIngredientScreenController.showAndWaitOnError(
                  "Add Ingredient Error", e.getMessage()));
    }
  }

  public String getIngredients() {
    return edamamFoodApiQueryBuilder.getIngredients();
  }

  public void resetIngredients() {
    this.edamamFoodApiQueryBuilder.resetIngredients();
    LOGGER.debug("All ingredients have been cleared");
    this.enterIngredientScreenController.resetIngredientsList();
  }

  public void makeFoodParserResponseCall() {
    ResponseCallbackListener<FoodParserResponse> listener = getSubmissionFoodParserListener();
    ThreadPoolSingleton.getInstance()
        .execute(
            () ->
                EdamamRepositorySingleton.getInstance()
                    .getFoodParserResponse(edamamFoodApiQueryBuilder.build(), listener));
  }

  /**
   * Create a listener for the api call
   *
   * @return the listener for Food api call
   */
  private ResponseCallbackListener<FoodParserResponse> getSubmissionFoodParserListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        FoodParserResponse foodParserResponse = (FoodParserResponse) responseBody;
        LOGGER.debug("Obtained Ingredients");
        Platform.runLater(
            () -> enterIngredientScreenController.loadFoodListScreenOnSuccess(foodParserResponse));
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error(t);
        String cause = "Unable to fetch response";
        String reason = "Unable to fetch response due to an exception";
        Platform.runLater(
            () -> enterIngredientScreenController.showAndWaitOnErrorEvent(cause, reason));
      }

      @Override
      public void onCallbackError(Response<?> response) {
        super.onCallbackError(response);
        LOGGER.error(this.getErrorResponse().getMessage());
        String cause = this.getErrorResponse().getErrorCode();
        String reason = this.getErrorResponse().getMessage();
        Platform.runLater(
            () -> enterIngredientScreenController.showAndWaitOnErrorEvent(cause, reason));
      }
    };
  }
}
