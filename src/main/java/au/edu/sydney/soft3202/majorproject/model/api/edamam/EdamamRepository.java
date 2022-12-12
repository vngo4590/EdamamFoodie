package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenIngredientBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenIngredientObserver;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenWrapper;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;

import java.util.Map;

public interface EdamamRepository extends AllergenWrapper {
  /** get the query builder that is used for getting the ingredients */
  EdamamFoodApiQueryBuilder getQueryBuilder();

  /** get the query builder that is used for getting the nutrient info of the ingredients */
  EdamamFoodIngredientRequestBuilder getIngredientRequestBuilder();

  /**
   * Fetch the food/ingredient response from the api. The call will respond to the listener with the
   * response and its status
   */
  void getFoodParserResponse(
      Map<String, String> query, ResponseCallbackListener<FoodParserResponse> listener);

  /**
   * Fetch the food/ingredient response from the api using a url. The call will respond to the
   * listener with the response and its status
   */
  void getFoodParserResponseWithURL(
      String url, ResponseCallbackListener<FoodParserResponse> listener);

  /**
   * Fetch the nutrient response from the api. The call will respond to the listener with the
   * response and its status
   *
   * @param doLoadCache whether to load cache or not load cache. The decision is up to the user
   * @param ingredientRequestBody the api request body to send through the api
   * @param listener the listener that observes the response
   */
  void getNutrientInfo(
      boolean doLoadCache,
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener);

  /**
   * Fetch the nutrient response from the api. The call will respond to the listener with the
   * response and its status. If there is a cache hit, we will report the call to {@link
   * CacheHitObserver}
   *
   * @param ingredientRequestBody the api request body to send through the api
   * @param listener the listener that observes the response
   * @param cacheHitObserver the observer that reacts as soon as there is a cache hit
   */
  void getNutrientInfo(
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener,
      CacheHitObserver cacheHitObserver);

  /** Reset all query builders */
  void reset();

  /** Clear all cache that is attached to the repository */
  void clearCache();
}
