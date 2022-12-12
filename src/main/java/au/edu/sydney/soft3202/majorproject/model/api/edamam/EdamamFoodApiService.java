package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface EdamamFoodApiService {
  /**
   * This api call is in charge to get the parser content for the list of ingredient
   *
   * @param queries the queries of the api call
   * @return The call instance of the API that is responsible for making api call
   */
  @GET("/api/food-database/v2/parser")
  Call<FoodParserResponse> getFoodParserResponse(@QueryMap Map<String, String> queries);

  /**
   * This api call loads the next page on the food parser
   *
   * @param foodParserUrl the url of the food request
   * @return The call instance of the API that is responsible for making api call
   */
  @GET
  Call<FoodParserResponse> getFoodParserResponseWithURL(@Url String foodParserUrl);

  /**
   * This api call loads the nutrient information based on a given query
   *
   * @param queries the queries of the api call
   * @param ingredientRequestBody The ingredient request body that is to be sent to the api
   * @return The call instance of the API that is responsible for making api call
   */
  @POST("/api/food-database/v2/nutrients")
  Call<NutrientsResponse> getNutrientInfo(
      @QueryMap Map<String, String> queries, @Body IngredientRequestBody ingredientRequestBody);
}
