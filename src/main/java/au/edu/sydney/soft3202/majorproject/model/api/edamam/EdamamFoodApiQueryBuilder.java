package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.type.Nutrient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/** a builder that is used to build the ingredient and nutrient requests for the api calls */
public class EdamamFoodApiQueryBuilder {
  private static final String APP_ID = System.getenv("INPUT_API_APP_ID");
  private static final String APP_KEY = System.getenv("INPUT_API_KEY");
  private StringBuilder ingredientBuilder;
  private Map<String, String> queryRequest;
  private Map<Nutrient, String> nutrientMap;

  private static final Logger LOGGER = LogManager.getLogger(EdamamFoodApiQueryBuilder.class);

  public EdamamFoodApiQueryBuilder() {
    reset();
  }

  /**
   * Add an ingredient to the request
   *
   * @param ingr the ingredient name
   */
  public EdamamFoodApiQueryBuilder addIngredient(String ingr) throws IllegalArgumentException {
    if (ingr == null || ingr.trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid Ingredient Input");
    }
    if (!this.ingredientBuilder.isEmpty()) {
      this.ingredientBuilder.append(" and ");
    }
    this.ingredientBuilder.append(ingr.trim());
    LOGGER.debug("Ingredient added: " + ingr);
    return this;
  }

  public String getIngredients() {
    return this.ingredientBuilder.toString();
  }

  public Map<Nutrient, String> getNutrientMap() {
    return nutrientMap;
  }

  /** This resets all the nutrients and ingredients */
  public EdamamFoodApiQueryBuilder reset() {
    softReset();
    ingredientBuilder = new StringBuilder();
    nutrientMap = new HashMap<>();
    LOGGER.debug("Ingredients and Nutrients have been reset");
    return this;
  }

  /** This resets the current query */
  public EdamamFoodApiQueryBuilder softReset() {
    queryRequest = new HashMap<>();
    queryRequest.put("app_id", APP_ID);
    queryRequest.put("app_key", APP_KEY);
    LOGGER.debug("Query has been reset");
    return this;
  }

  /** This resets all the ingredients */
  public EdamamFoodApiQueryBuilder resetIngredients() {
    softReset();
    ingredientBuilder = new StringBuilder();
    LOGGER.debug("Ingredient has been reset");
    return this;
  }

  /** This resets all the nutrients */
  public EdamamFoodApiQueryBuilder resetNutrient() {
    softReset();
    nutrientMap = new HashMap<>();
    LOGGER.debug("Nutrient has been reset");
    return this;
  }

  /**
   * Add the nutrient with the min & max values
   *
   * @param nutrient the nutrient value to be added
   * @param min the minimum value to be added
   * @param max the maximum value to be added
   * @return the instance of the builder
   * @throws IllegalArgumentException throw this exception if any of this value is negative
   */
  public EdamamFoodApiQueryBuilder addNutrientRange(Nutrient nutrient, int min, int max)
      throws IllegalArgumentException {
    if (min < 0 || max < 0 || min > max) {
      LOGGER.error(String.format("Invalid Range min: %d max: %d", min, max));
      throw new IllegalArgumentException(String.format("Invalid Range min: %d max: %d", min, max));
    }
    this.nutrientMap.put(nutrient, String.format("%d-%d", min, max));
    LOGGER.debug(
        String.format(
            "Nutrient %s has been added with value %d-%d", nutrient.getTypeName(), min, max));
    return this;
  }

  /**
   * Add the nutrient with the max values
   *
   * @param nutrient the nutrient value to be added
   * @param max the maximum value to be added
   * @return the instance of the builder
   * @throws IllegalArgumentException throw this exception if any of this range is negative
   */
  public EdamamFoodApiQueryBuilder addNutrientMax(Nutrient nutrient, int max)
      throws IllegalArgumentException {
    if (max < 0) {
      LOGGER.error("Invalid nutrient maximum value");
      throw new IllegalArgumentException(String.format("Invalid max value: %d", max));
    }
    this.nutrientMap.put(nutrient, String.format("%d", max));
    LOGGER.debug(
        String.format("Nutrient %s has been added with value %d", nutrient.getTypeName(), max));
    return this;
  }

  /**
   * Add the nutrient with the max values
   *
   * @param nutrient the nutrient value to be added
   * @param min the minimum value to be added
   * @return the instance of the builder
   * @throws IllegalArgumentException throw this exception if any of this range is negative
   */
  public EdamamFoodApiQueryBuilder addNutrientMin(Nutrient nutrient, int min)
      throws IllegalArgumentException {
    if (min < 0) {
      LOGGER.error("Invalid nutrient minimum value");
      throw new IllegalArgumentException(String.format("Invalid min value: %d", min));
    }
    this.nutrientMap.put(nutrient, String.format("%d+", min));
    LOGGER.debug(
        String.format("Nutrient %s has been added with value %d+", nutrient.getTypeName(), min));
    return this;
  }

  /**
   * Given a type name of the nutrient, we look for the maximum value of the nutrient
   *
   * @param typeName the name of the nutrient
   * @return the maximum range of the nutrient. if it does not exist or the type name is invalid
   *     then return -1
   */
  public double getMax(String typeName) {
    if (typeName == null || typeName.isEmpty()) {
      LOGGER.error("Type name is invalid");
      return -1;
    }
    Nutrient nutrient = Nutrient.getNutrientByTypeName(typeName);
    if (nutrient != null) {
      String range = this.nutrientMap.get(nutrient);
      if (range != null) {
        String value = range;
        if (range.contains("-")) {
          String[] splitRange = value.split("-");
          value = splitRange[1];
        } else if (range.contains("+")) {
          return -1;
        }
        LOGGER.debug(
            String.format("Max for this Nutrient %s is %s", nutrient.getTypeName(), value));
        return Double.parseDouble(value);
      }
    }
    return -1;
  }

  /**
   * Build the query request
   *
   * @return the map of query requests
   */
  public Map<String, String> build() {
    for (Map.Entry<Nutrient, String> entry : nutrientMap.entrySet()) {
      this.queryRequest.put(
          String.format("nutrients[%s]", entry.getKey().getTypeNTRCode()), entry.getValue());
    }
    if (!this.ingredientBuilder.toString().isEmpty()) {
      this.queryRequest.put("ingr", this.ingredientBuilder.toString());
    }
    LOGGER.debug("Query request built: " + this.queryRequest);
    return this.queryRequest;
  }
}
