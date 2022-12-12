package au.edu.sydney.soft3202.majorproject.model.api.edamam.online;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.*;
import au.edu.sydney.soft3202.majorproject.model.db.NutrientDao;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestData;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;

/**
 * This class is used to return responses from the online servers to the listeners attached to the
 * method call
 */
public class OnlineEdamamFoodRepository extends BaseEdamamFoodRepository {
  private final EdamamFoodApiService apiService;
  private final NutrientDao nutrientDao;
  private static final Logger LOGGER = LogManager.getLogger(OnlineEdamamFoodRepository.class);

  public OnlineEdamamFoodRepository(EdamamFoodApiService apiService, NutrientDao nutrientDao) {
    super();
    this.apiService = apiService;
    this.nutrientDao = nutrientDao;
    LOGGER.info("Online repository has been initialized");
  }

  @Override
  public void getFoodParserResponse(
      Map<String, String> query, ResponseCallbackListener<FoodParserResponse> listener) {
    /*
     * Make asynchronous call to the server through enqueue.
     * The listener will respond to this call whether successful for failure
     * */
    Call<FoodParserResponse> foodParserResponseCall = this.apiService.getFoodParserResponse(query);
    LOGGER.debug("Getting Food Responses ...");
    foodParserResponseCall.enqueue(listener);
  }

  @Override
  public void getFoodParserResponseWithURL(
      String url, ResponseCallbackListener<FoodParserResponse> listener) {
    /*
     * Make asynchronous call to the server through enqueue
     * */
    Call<FoodParserResponse> foodParserResponseCall =
        this.apiService.getFoodParserResponseWithURL(url);
    LOGGER.debug("Getting Food Responses ...");
    foodParserResponseCall.enqueue(listener);
  }

  @Override
  public void getNutrientInfo(
      boolean doLoadCache,
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener) {
    LOGGER.debug("Getting Nutrient Responses ...");
    /*
     * load cache if the user doesn't want it.
     * */
    if (!doLoadCache) {
      getNutrientInfo(ingredientRequestBody, listener);
    } else {
      getNutrientInfoFromCache(ingredientRequestBody, listener);
    }
  }

  @Override
  public void getNutrientInfo(
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener,
      CacheHitObserver cacheHitObserver) {
    Call<NutrientsResponse> nutrientResponseCall =
        this.apiService.getNutrientInfo(
            new EdamamFoodApiQueryBuilder().build(), ingredientRequestBody);
    try {
      if (checkForNutrientInfoCacheHit(ingredientRequestBody)) {
        LOGGER.debug("Cache Hit Found!");
        cacheHitObserver.onCacheHit();
      } else {
        // If no cache hit, then reload like normal
        getNutrientInfo(ingredientRequestBody, listener);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      listener.onFailure(nutrientResponseCall, e);
    }
  }

  private void getNutrientInfo(
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener) {
    Call<NutrientsResponse> nutrientResponseCall =
        this.apiService.getNutrientInfo(
            new EdamamFoodApiQueryBuilder().build(), ingredientRequestBody);
    try {
      Response<NutrientsResponse> nutrientsResponse = nutrientResponseCall.execute();
      LOGGER.debug("Inserting new Nutrient Data to the cache ...");
      nutrientDao.insertNutrientResponse(nutrientsResponse.body());
      LOGGER.debug("Nutrient Data has been successfully inserted & now returning the data");
      listener.onResponse(nutrientResponseCall, nutrientsResponse);
    } catch (IOException | SQLException e) {
      e.printStackTrace();
      listener.onFailure(nutrientResponseCall, e);
    }
  }

  private void getNutrientInfoFromCache(
      IngredientRequestBody ingredientRequestBody,
      ResponseCallbackListener<NutrientsResponse> listener) {
    Call<NutrientsResponse> nutrientResponseCall =
        this.apiService.getNutrientInfo(
            new EdamamFoodApiQueryBuilder().build(), ingredientRequestBody);
    try {
      /*
       * Fetch the response from the cache.
       * */
      IngredientRequestData ingredientRequestData = ingredientRequestBody.getIngredients().get(0);
      String nutritionString =
          nutrientDao.getNutrientResponse(
              ingredientRequestData.getFoodId(),
              ingredientRequestData.getMeasureURI(),
              ingredientRequestData.getQuantity());
      Type classType = (new TypeToken<NutrientsResponse>() {}.getType());
      NutrientsResponse nutrientsPojo = new Gson().fromJson(nutritionString, classType);
      Response<NutrientsResponse> nutrientsResponse = Response.success(nutrientsPojo);
      listener.onResponse(nutrientResponseCall, nutrientsResponse);
    } catch (Exception e) {
      listener.onFailure(nutrientResponseCall, e);
    }
  }

  private boolean checkForNutrientInfoCacheHit(IngredientRequestBody ingredientRequestBody)
      throws SQLException {
    LOGGER.debug("Checking for cache hit");
    IngredientRequestData ingredientRequestData = ingredientRequestBody.getIngredients().get(0);
    String existingResponse =
        nutrientDao.getNutrientResponse(
            ingredientRequestData.getFoodId(),
            ingredientRequestData.getMeasureURI(),
            ingredientRequestData.getQuantity());
    return existingResponse != null;
  }

  @Override
  public void clearCache() {
    LOGGER.debug("Executing clear cache ...");
    this.nutrientDao.deleteAllNutrientResponses();
  }
}
