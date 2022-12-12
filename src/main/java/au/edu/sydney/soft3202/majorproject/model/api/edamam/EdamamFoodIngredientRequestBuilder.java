package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestData;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestWrapper;
import au.edu.sydney.soft3202.majorproject.model.type.MeasureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This builder class build the ingredient requests with measure and food */
public class EdamamFoodIngredientRequestBuilder {
  /** This map corresponds to food id - measure type - quantity */
  private Map<String, IngredientRequestWrapper> ingredientRequestMap;

  private static final Logger LOGGER =
      LogManager.getLogger(EdamamFoodIngredientRequestBuilder.class);

  public EdamamFoodIngredientRequestBuilder() {
    reset();
  }

  /**
   * Resets all requests
   *
   * @return the instance of the builder class
   */
  public EdamamFoodIngredientRequestBuilder reset() {
    LOGGER.debug("All ingredient requests have been reset");
    ingredientRequestMap = new HashMap<>();
    return this;
  }

  /**
   * Get the map of all ingredients
   *
   * @return The Map of Ingredient Requests
   */
  public Map<String, IngredientRequestWrapper> getIngredientRequestMap() {
    return ingredientRequestMap;
  }

  /**
   * Add a measure with food label, id and the measure
   *
   * @param foodLabel the label of the food *
   * @param foodId the food id of the food *
   * @param measureType the measureType of the food measurement *
   * @param amount the amount of the food measurement
   * @throws IllegalArgumentException throws with any param is invalid such as null or empty string
   *     and undefinable measure type
   */
  public EdamamFoodIngredientRequestBuilder addMeasure(
      String foodLabel, String foodId, MeasureType measureType, int amount)
      throws IllegalArgumentException {
    if (measureType == null) {
      LOGGER.error("Incorrect Measure");
      throw new IllegalArgumentException("Incorrect Measure");
    }
    return addMeasure(foodLabel, foodId, measureType.getUri(), amount);
  }

  /**
   * Add a measure with food label, id and the measure
   *
   * @param foodLabel the label of the food
   * @param foodId the food id of the food
   * @param uri the food uri of the food measurement
   * @param amount the amount of the food measurement
   * @throws IllegalArgumentException throws with any param is invalid such as null or empty string
   *     and undefinable measure type
   */
  public EdamamFoodIngredientRequestBuilder addMeasure(
      String foodLabel, String foodId, String uri, int amount) throws IllegalArgumentException {
    if (foodLabel == null
        || foodLabel.trim().isEmpty()
        || foodId == null
        || foodId.trim().isEmpty()
        || uri == null
        || uri.trim().isEmpty()
        || amount < 0) {
      LOGGER.error("Incorrect Measure");
      throw new IllegalArgumentException("Incorrect Measure");
    }
    IngredientRequestWrapper ingredientRequestWrapper =
        this.ingredientRequestMap.computeIfAbsent(foodId, k -> new IngredientRequestWrapper());
    if (ingredientRequestWrapper.getRequestBody() == null) {
      IngredientRequestBody ingredientRequestBody = new IngredientRequestBody();
      ingredientRequestBody.setIngredients(new ArrayList<>());

      ingredientRequestWrapper.setFoodLabel(foodLabel);
      ingredientRequestWrapper.setRequestBody(ingredientRequestBody);
    }
    for (IngredientRequestData item : ingredientRequestWrapper.getRequestBody().getIngredients()) {
      if (uri.equalsIgnoreCase(item.getMeasureURI())) {
        LOGGER.debug("Ingredient request " + item + " Has been modified with amount: " + amount);
        item.setQuantity(amount);
        return this;
      }
    }
    // if not exist, we create a new one
    IngredientRequestData item = new IngredientRequestData();
    item.setMeasureURI(uri);
    item.setFoodId(foodId);
    item.setQuantity(amount);
    ingredientRequestWrapper.getRequestBody().getIngredients().add(item);
    LOGGER.debug("Ingredient request " + item + " Has been added");
    return this;
  }

  /**
   * Build the requests
   *
   * @return a list of ingredient request body
   */
  public List<IngredientRequestBody> build() {
    List<IngredientRequestBody> ingredientRequestBodies = new ArrayList<>();
    for (Map.Entry<String, IngredientRequestWrapper> entry : this.ingredientRequestMap.entrySet()) {
      for (IngredientRequestData requestData : entry.getValue().getRequestBody().getIngredients()) {
        IngredientRequestBody input = new IngredientRequestBody();
        List<IngredientRequestData> ingredientRequestDataList = new ArrayList<>();
        input.setIngredients(ingredientRequestDataList);
        ingredientRequestDataList.add(requestData);
        ingredientRequestBodies.add(input);
      }
    }
    LOGGER.debug("Ingredient request " + ingredientRequestBodies + " Built");
    return ingredientRequestBodies;
  }
}
