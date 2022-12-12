package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.FoodInfoScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodIngredientRequestBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.type.MeasureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class FoodInfoScreenPresenter {
  private FoodInfoScreenController foodInfoScreenController;
  private EdamamFoodIngredientRequestBuilder edamamFoodIngredientRequestBuilder;
  private EdamamRepository edamamRepository;
  private static final Logger LOGGER = LogManager.getLogger(FoodInfoScreenPresenter.class);

  public FoodInfoScreenPresenter(FoodInfoScreenController foodInfoScreenController) {
    this.foodInfoScreenController = foodInfoScreenController;
    this.edamamRepository = EdamamRepositorySingleton.getInstance();
    this.edamamFoodIngredientRequestBuilder = edamamRepository.getIngredientRequestBuilder();
  }

  /**
   * Add the measure to the ingredient
   *
   * @param foodId The food Id if the ingredient
   * @param foodLabel The name of the ingredient
   * @param selectedMeasure The measure
   * @param measureQuantity The quantity of the measure
   */
  public void addMeasure(
      String foodId, String foodLabel, String selectedMeasure, int measureQuantity) {
    edamamFoodIngredientRequestBuilder.addMeasure(
        foodLabel,
        foodId,
        Objects.requireNonNull(MeasureType.getMeasureTypeByName(selectedMeasure)),
        measureQuantity);

    LOGGER.debug("Selected Measure: " + selectedMeasure + " & Quantity: " + measureQuantity);
    // call to the controller to update the view
    foodInfoScreenController.updateIngredientMeasureList(
        edamamFoodIngredientRequestBuilder.getIngredientRequestMap());
  }
}
