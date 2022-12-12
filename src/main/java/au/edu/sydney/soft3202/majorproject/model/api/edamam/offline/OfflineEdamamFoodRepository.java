package au.edu.sydney.soft3202.majorproject.model.api.edamam.offline;

import au.edu.sydney.soft3202.majorproject.model.api.DummyURLEnum;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.*;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
/** This class is used to return dummy responses to the listeners attached to the method call */
public class OfflineEdamamFoodRepository extends BaseEdamamFoodRepository {

  private FoodParserResponse foodParserResponse;
  private NutrientsResponse nutrientsResponse;
  private static final Logger LOGGER = LogManager.getLogger(OfflineEdamamFoodRepository.class);

  /*
   * The nutritional information for specific food items for specific measures and values.
   * */
  private final int threadSleepMillis;

  public OfflineEdamamFoodRepository(int threadSleepMillis) {
    super();
    /*
     * This allows us to determine how long our thread sleep should be when we make an api call
     * */
    this.threadSleepMillis = threadSleepMillis;
    try {
      this.nutrientsResponse =
          (NutrientsResponse) DummyURLEnum.loadFromJson(DummyURLEnum.EDAMAM_NUTRIENTS_RESPONSE);
      this.foodParserResponse =
          (FoodParserResponse) DummyURLEnum.loadFromJson(DummyURLEnum.EDAMAM_FOOD_PARSER_RESPONSE);
    } catch (IOException e) {
      e.printStackTrace();
    }
    LOGGER.info("Offline repository has been initialized");
  }

  @Override
  public void getFoodParserResponse(
      Map<String, String> query, ResponseCallbackListener<FoodParserResponse> listener) {
    LOGGER.debug("Getting Food Responses ...");
    try {
      Thread.sleep(threadSleepMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
    listener.onSuccess(this.foodParserResponse);
  }

  @Override
  public void getFoodParserResponseWithURL(
      String url, ResponseCallbackListener<FoodParserResponse> listener) {
    LOGGER.debug("Getting Food Responses ...");
    try {
      Thread.sleep(threadSleepMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
    listener.onSuccess(this.foodParserResponse);
  }

  @Override
  public void getNutrientInfo(
      boolean doLoadCache,
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener) {
    LOGGER.debug("Getting Nutrient Responses ...");
    try {
      Thread.sleep(threadSleepMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
    listener.onSuccess(this.nutrientsResponse);
  }

  @Override
  public void getNutrientInfo(
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener,
      CacheHitObserver cacheHitObserver) {
    /*
    * For offline we don't call to the cache hit observer because we don't have any cache
    * */
    getNutrientInfo(false, ingredientRequestBody, listener);
  }

  @Override
  public void clearCache() {
    // Do nothing
    LOGGER.debug("Offline mode does not have cache, therefore cannot clear.");
  }
}
