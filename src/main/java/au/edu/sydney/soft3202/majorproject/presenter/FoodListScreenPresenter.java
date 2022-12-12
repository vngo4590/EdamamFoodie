package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.FoodListScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.*;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodHintsItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.model.thread.ThreadPoolSingleton;
import au.edu.sydney.soft3202.majorproject.model.type.Nutrient;
import javafx.application.Platform;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class FoodListScreenPresenter {
  private final EdamamRepository edamamRepository;
  private final FoodListScreenController foodListScreenController;
  private final EdamamFoodIngredientRequestBuilder edamamFoodIngredientRequestBuilder;
  private static final Logger LOGGER = LogManager.getLogger(FoodListScreenPresenter.class);
  private ResponseCallbackListener<NutrientsResponse> responseCallbackListener;
  private final ExecutorService executorService;
  private final AtomicInteger pageCounter;
  private final EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder;

  public FoodListScreenPresenter(FoodListScreenController foodListScreenController) {
    this.foodListScreenController = foodListScreenController;
    this.edamamRepository = EdamamRepositorySingleton.getInstance();
    this.edamamFoodIngredientRequestBuilder = edamamRepository.getIngredientRequestBuilder();
    this.executorService = ThreadPoolSingleton.getInstance();
    this.pageCounter = new AtomicInteger(1);
    this.edamamFoodApiQueryBuilder = edamamRepository.getQueryBuilder();
  }

  /**
   * Given a URL, we call to the api to load the results
   *
   * @param currentUrl The URL
   */
  public void loadFoodParserResponseWithURL(String currentUrl) {
    ResponseCallbackListener<FoodParserResponse> listener =
        getNextPageFoodParserResponseCallbackListener();
    ThreadPoolSingleton.getInstance()
        .execute(() -> edamamRepository.getFoodParserResponseWithURL(currentUrl, listener));
  }

  /** Submit Ingredients to the API by calling to the repository */
  public void submitIngredientRequests() {
    // Build the query to get proper request
    this.responseCallbackListener = getNutrientsResponseCallbackListener();
    List<IngredientRequestBody> requestBodies = edamamFoodIngredientRequestBuilder.build();
    for (IngredientRequestBody requestBody : requestBodies) {
      // Check ì we have cache hit
      // if we do, ask the view controller to decide whether we should load cache
      LOGGER.debug("Loading Ingredient Requests " + requestBodies);
      executorService.execute(
          () -> {
            try {
              // Execute cache
              CacheHitObserver cacheHitObserver =
                  () ->
                      Platform.runLater(() -> foodListScreenController.askForCacheHit(requestBody));
              edamamRepository.getNutrientInfo(
                  requestBody, responseCallbackListener, cacheHitObserver);
            } catch (Exception e) {
              e.printStackTrace();
              LOGGER.error(e);
              Platform.runLater(
                  () -> {
                    // Report back to the controller that we have an error while
                    // fetching the results
                    foodListScreenController.cleanupAfterException(e);
                  });
            }
          });
    }
  }
  /**
   * Get the {@link ResponseCallbackListener} for fetching nutrient information
   *
   * @return the listener
   */
  private ResponseCallbackListener<NutrientsResponse> getNutrientsResponseCallbackListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        LOGGER.debug("Loaded Nutrients");
        Platform.runLater(
            () ->
                foodListScreenController.updateNutrientResponseInfoScreen(
                    (NutrientsResponse) responseBody));
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error("Loaded Nutrients with error " + t.getMessage());
        String cause = getErrorResponse().getError();
        String reason = getErrorResponse().getMessage();
        Platform.runLater(() -> foodListScreenController.showAndWaitOnErrorEvent(cause, reason));
      }

      @Override
      public void onCallbackError(Response<?> response) {
        super.onCallbackError(response);
        LOGGER.error("Loaded Nutrients with error " + getErrorResponse());
        String cause = getErrorResponse().getError();
        String reason = getErrorResponse().getMessage();
        Platform.runLater(() -> foodListScreenController.showAndWaitOnErrorEvent(cause, reason));
      }
    };
  }

  /**
   * Get the {@link ResponseCallbackListener} for fetching ingredients information
   *
   * @return the listener
   */
  private ResponseCallbackListener<FoodParserResponse>
      getNextPageFoodParserResponseCallbackListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        LOGGER.debug("Loading Next Ingredient Page Successful");
        Platform.runLater(
            () -> foodListScreenController.updateIngredientPage((FoodParserResponse) responseBody));
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error("Loading Next Ingredient Page Unsuccessful: " + t.getMessage());
        String cause = getErrorResponse().getError();
        String reason = getErrorResponse().getMessage();
        Platform.runLater(() -> foodListScreenController.showAndWaitOnErrorEvent(cause, reason));
      }

      @Override
      public void onCallbackError(Response<?> response) {
        super.onCallbackError(response);
        LOGGER.error(
            "Loading Next Ingredient Page Unsuccessful: " + getErrorResponse().getMessage());
        String cause = getErrorResponse().getError();
        String reason = getErrorResponse().getMessage();
        Platform.runLater(() -> foodListScreenController.showAndWaitOnErrorEvent(cause, reason));
      }
    };
  }

  /**
   * Load the cache hit from the user input
   *
   * @param toLoadCache whether to load the cache
   * @param requestBody the request that we are going to send through the api
   */
  public void loadCacheHit(boolean toLoadCache, IngredientRequestBody requestBody) {
    loadCacheHit(toLoadCache, requestBody, responseCallbackListener);
  }

  /**
   * Load the cache hit from the user input
   *
   * @param toLoadCache whether to load the cache
   * @param requestBody the request that we are going to send through the api
   * @param responseCallbackListener The listener that observes the api call
   */
  public void loadCacheHit(
      boolean toLoadCache,
      IngredientRequestBody requestBody,
      ResponseCallbackListener<NutrientsResponse> responseCallbackListener) {
    if (toLoadCache) {
      LOGGER.debug("Use cache hit");
    } else {
      LOGGER.debug("Load new data");
    }
    this.edamamRepository.getNutrientInfo(toLoadCache, requestBody, responseCallbackListener);
  }

  /**
   * Load ingredient information panel and call to the view controller to update the UI
   *
   * @param item The ingredient information to load
   */
  public void loadFoodItemScreen(FoodHintsItem item) {
    LOGGER.debug("Loading item " + item.getFood().getLabel());
    // Call to controller to initialize the node creation process
    // and pass this future value to the main ingredient container
    Future<Node> futureNode =
        ThreadPoolSingleton.getInstance()
            .submit(() -> foodListScreenController.loadScreenOnFoodItem(item));
    ThreadPoolSingleton.getInstance()
        .execute(
            () -> {
              try {
                Node node = futureNode.get();
                if (node != null) {
                  Platform.runLater(
                      () -> foodListScreenController.addNodeToMainIngredientContainer(node));
                }
              } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
              }
            });
  }

  public boolean isIngredientRequestBodyEmpty() {
    return edamamFoodIngredientRequestBuilder.build().isEmpty();
  }

  public void incrementPageCounter() {
    this.pageCounter.incrementAndGet();
  }

  public int getPageCounter() {
    return this.pageCounter.get();
  }

  /**
   * Add a nutrient to the program
   *
   * @param nutrientText The nutrient value to add
   * @param minText The minimum amount of the nutrient
   * @param maxText The maximum amount of the nutrient
   */
  public void addNutrient(String nutrientText, String minText, String maxText) {
    try {
      int min = -1;
      int max = -1;

      Nutrient nutrient = Nutrient.getNutrientByTypeName(nutrientText);
      if (!maxText.isEmpty() && !minText.isEmpty()) {
        min = Integer.parseInt(minText);
        max = Integer.parseInt(maxText);
        this.edamamFoodApiQueryBuilder.addNutrientRange(nutrient, min, max);
      } else if (!maxText.isEmpty()) {
        max = Integer.parseInt(maxText);
        this.edamamFoodApiQueryBuilder.addNutrientMax(nutrient, max);
      } else if (!minText.isEmpty()) {
        min = Integer.parseInt(minText);
        this.edamamFoodApiQueryBuilder.addNutrientMin(nutrient, min);
      } else {
        this.edamamFoodApiQueryBuilder.addNutrientRange(nutrient, min, max);
      }
      LOGGER.debug("Added Nutrient " + nutrient);
      // Call to view controller to update the nutrient values
      updateNutrientValue();
    } catch (Exception e) {
      Platform.runLater(
          () -> foodListScreenController.showAndWaitOnError("Add Nutrient Error", e.getMessage()));
    }
  }

  /** Update the nutrient value inform of string and then call the controller to update the UI */
  public void updateNutrientValue() {
    StringBuilder nutrientString = new StringBuilder();
    for (Map.Entry<Nutrient, String> nutrientEntry :
        this.edamamFoodApiQueryBuilder.getNutrientMap().entrySet()) {
      nutrientString
          .append(nutrientEntry.getKey().getTypeName())
          .append(": ")
          .append(nutrientEntry.getValue().replace("-", " to ").replace("+", " minimum"))
          .append(" ")
          .append(nutrientEntry.getKey().getUnitMeasurement().getTypeName())
          .append("\n");
    }
    foodListScreenController.updateNutrientValue(nutrientString.toString());
  }

  /** Clear all nutrients and then call the UI to update the Nutrient List */
  public void resetNutrient() {
    this.edamamFoodApiQueryBuilder.resetNutrient();
    LOGGER.debug("All Nutrients have been reset");
    this.foodListScreenController.resetNutrientList();
  }
}
