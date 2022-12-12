package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenIngredientBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenIngredientObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** This class provides a common method to all repositories */
public abstract class BaseEdamamFoodRepository implements EdamamRepository {
  /** a builder that is used to build the ingredient and nutrient requests for the api calls */
  private final EdamamFoodApiQueryBuilder queryBuilder;
  /** builder class build the ingredient requests with measure and food */
  private final EdamamFoodIngredientRequestBuilder ingredientRequestBuilder;
  /** builder that is used to build allergen */
  private final AllergenIngredientBuilder allergenIngredientBuilder;

  private static final Logger LOGGER = LogManager.getLogger(BaseEdamamFoodRepository.class);

  public BaseEdamamFoodRepository() {
    /* Initialize and Reset all builders so that we can use them for building queries */
    this.queryBuilder = new EdamamFoodApiQueryBuilder();
    this.ingredientRequestBuilder = new EdamamFoodIngredientRequestBuilder();
    this.allergenIngredientBuilder = new AllergenIngredientBuilder();
  }

  @Override
  public EdamamFoodApiQueryBuilder getQueryBuilder() {
    return queryBuilder;
  }

  @Override
  public EdamamFoodIngredientRequestBuilder getIngredientRequestBuilder() {
    return this.ingredientRequestBuilder;
  }

  @Override
  public void reset() {
    LOGGER.debug("All Queries for Edamam Food Repository have been reset");
    this.queryBuilder.reset();
    this.ingredientRequestBuilder.reset();
  }

  @Override
  public String getAllergen() {
    return this.allergenIngredientBuilder.build();
  }

  @Override
  public void addAllergen(String allergen) throws IllegalArgumentException {
    this.allergenIngredientBuilder.addAllergen(allergen);
  }

  @Override
  public void checkIngredientSelectedAllergen(
      String ingredient, AllergenIngredientObserver allergenIngredientObserver) {
    if (this.allergenIngredientBuilder.isIngredientSelectedAllergen(ingredient)) {
      allergenIngredientObserver.onMatchingAllergen();
    } else {
      allergenIngredientObserver.onNotMatchingAllergen();
    }
  }
}
