package au.edu.sydney.soft3202.majorproject.api;

import au.edu.sydney.soft3202.majorproject.model.api.ApiMode;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodIngredientRequestBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.offline.OfflineEdamamFoodRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.offline.OfflinePastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.offline.OfflineRedditRepository;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestData;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestWrapper;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodHintsItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodInfo;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.MeasuresItem;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.type.MeasureType;
import au.edu.sydney.soft3202.majorproject.model.type.Nutrient;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public abstract class BaseOfflineRepositoryTest {
    /**
     * A listener to be inserted to the repository to get the data we want
     */
    protected ResponseCallbackListener<FoodParserResponse> foodParserResponseCallbackListener;
    protected ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener;
    protected ResponseCallbackListener<String> pastebinResponseCallbackListener;
    protected ResponseCallbackListener<SubmissionParentResponse> submissionResponseCallbackListener;
    protected ResponseCallbackListener<AuthorizationResponse> authorizationResponseCallbackListener;

    protected EdamamRepository foodRepository;
    protected PastebinRepository pastebinRepository;
    protected RedditRepository redditRepository;

    protected MockedStatic<EdamamRepositorySingleton> edamamRepositorySingleton;
    protected MockedStatic<RedditRepositorySingleton> redditRepositorySingleton;
    protected MockedStatic<PastebinRepositorySingleton> pastebinRepositorySingleton;


    protected ArgumentCaptor<Object> disposableCaptor;
    public void setup() {

        /*
         * Mock listeners
         * Since these classes are abstract, we want to use real implementations instead
         * */
        foodParserResponseCallbackListener = Mockito.spy(ResponseCallbackListener.class);
        nutrientsResponseCallbackListener = Mockito.spy(ResponseCallbackListener.class);
        pastebinResponseCallbackListener = Mockito.spy(ResponseCallbackListener.class);
        submissionResponseCallbackListener = Mockito.spy(ResponseCallbackListener.class);
        authorizationResponseCallbackListener = Mockito.spy(ResponseCallbackListener.class);

        /*
         * Initialize captors for the api calls
         * */
        disposableCaptor = ArgumentCaptor.forClass(Object.class);


        /*
         * Do nothing on success. This needs to be overridden later
         * */
        doAnswer(invocation -> null).when(foodParserResponseCallbackListener).onSuccess(disposableCaptor.capture());
        doAnswer(invocation -> null).when(nutrientsResponseCallbackListener).onSuccess(disposableCaptor.capture());
        doAnswer(invocation -> null).when(pastebinResponseCallbackListener).onSuccess(disposableCaptor.capture());

        /*
         * Initialize the repository
         * */
        foodRepository = new OfflineEdamamFoodRepository(0);
        pastebinRepository = new OfflinePastebinRepository(0);
        redditRepository = new OfflineRedditRepository(0);

        /*
         * Create initiate the repository singletons
         * */
        edamamRepositorySingleton = Mockito.mockStatic(EdamamRepositorySingleton.class);
        edamamRepositorySingleton.when(() -> EdamamRepositorySingleton.init(ApiMode.OFFLINE)).thenAnswer(invocation -> null);
        edamamRepositorySingleton.when(EdamamRepositorySingleton::getInstance).thenReturn(foodRepository);

        redditRepositorySingleton = Mockito.mockStatic(RedditRepositorySingleton.class);
        redditRepositorySingleton.when(() -> RedditRepositorySingleton.init(ApiMode.OFFLINE)).thenAnswer(invocation -> null);
        redditRepositorySingleton.when(RedditRepositorySingleton::getInstance).thenReturn(redditRepository);

        pastebinRepositorySingleton = Mockito.mockStatic(PastebinRepositorySingleton.class);
        pastebinRepositorySingleton.when(() -> PastebinRepositorySingleton.init(ApiMode.OFFLINE)).thenAnswer(invocation -> null);
        pastebinRepositorySingleton.when(PastebinRepositorySingleton::getInstance).thenReturn(pastebinRepository);
        /*
         * Initialise the singletons
         * */
        EdamamRepositorySingleton.init(ApiMode.ONLINE);
        RedditRepositorySingleton.init(ApiMode.ONLINE);
        PastebinRepositorySingleton.init(ApiMode.ONLINE);
    }
    public void cleanUp() {
        edamamRepositorySingleton.close();
        redditRepositorySingleton.close();
        pastebinRepositorySingleton.close();
    }

    /**
     * This method checks the query builder and make sure that it has the standard variables before we do anything
     */
    public void checkRepositoryQueryBeforeModification() {
        EdamamRepository foodRepository = EdamamRepositorySingleton.getInstance();
        assertThat(foodRepository
                .getQueryBuilder()
                .getNutrientMap()
                .size(), equalTo(0)
        );
        assertThat(foodRepository
                .getQueryBuilder()
                .getIngredients(), is(emptyString())
        );
        assertThat(foodRepository.getQueryBuilder().build().size(), equalTo(2));
        /*assertThat(foodRepository.getQueryBuilder().build().get("app_id"), is(both(not(equalTo(null))).and(not(equalTo("")))));
        assertThat(foodRepository.getQueryBuilder().build().get("app_key"), is(both(not(equalTo(null))).and(not(equalTo("")))));*/
    }

    public void validateFoodParserResponse(Map<String, String> edamamQuery, FoodParserResponse foodParserResponse) {
        assertThat(foodParserResponse.getHints(), is(not(equalTo(null))));
        assertThat(foodParserResponse.getHints().size(), is(greaterThan(1)));
        for (FoodHintsItem foodHintsItem : foodParserResponse.getHints()) {
            assertNotNull(foodHintsItem.getFood());
            FoodInfo foodInfo = foodHintsItem.getFood();
            assertNotNull(foodInfo.getFoodId());
            assertNotNull(foodInfo.getLabel());
            List<MeasuresItem> measuresItemList = foodHintsItem.getMeasures();
            assertNotNull(measuresItemList);
            for (MeasuresItem measuresItem : measuresItemList) {
                assertNotNull(measuresItem.getLabel());
                assertNotNull(measuresItem.getUri());
            }
        }
    }

    public void validateIngredientQuery(
            ResponseCallbackListener<FoodParserResponse> listener, Map<String, String> queryMap
    ) {
        makeSuccessfulIngredientRequest(listener, queryMap);
        verify(listener, atLeastOnce()).onSuccess(disposableCaptor.capture());
        assertThat(disposableCaptor.getValue(), is(not(equalTo(null))));
        assertThat(disposableCaptor.getValue().getClass(), is(equalTo(FoodParserResponse.class)));
    }

    public void makeSuccessfulNutrientsRequest(
            ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener,
            IngredientRequestBody body) {
        EdamamRepositorySingleton.getInstance().getNutrientInfo(false, body, nutrientsResponseCallbackListener);
    }

    public void makeSuccessfulIngredientRequest(ResponseCallbackListener<FoodParserResponse> listener, Map<String, String> queryMap) {
        EdamamRepositorySingleton.getInstance().getFoodParserResponse(queryMap, listener);
    }


    public void makeSuccessfulIngredientNextPageRequest(ResponseCallbackListener<FoodParserResponse> listener, String url) {
        EdamamRepositorySingleton.getInstance().getFoodParserResponseWithURL(url, listener);
    }

    public void makeSuccessfulPastebinRequest(ResponseCallbackListener<String> listener, String msg) {
        PastebinRepositorySingleton.getInstance().getPastebinApiQueryBuilder().setPasteContent(msg);
        PastebinApiQueryBuilder pastebinApiQueryBuilder = PastebinRepositorySingleton.getInstance()
                .getPastebinApiQueryBuilder();
        PastebinRepositorySingleton.getInstance().submitPaste(pastebinApiQueryBuilder.build(), listener);
    }

    public void validateInputNutrient(Map<String, String> query, int querySize, Nutrient inputNutrient, String inputNutrientValue) {
        assertThat(query.size(), equalTo(querySize));
        assertNotNull(query.get(String.format("nutrients[%s]", inputNutrient.getTypeNTRCode())));
        assertEquals(query.get(String.format("nutrients[%s]", inputNutrient.getTypeNTRCode())), inputNutrientValue);
    }

    public void addMeasureThroughArguments(List<Arguments> arguments, EdamamFoodIngredientRequestBuilder ingredientRequestBuilder) {
        for (Arguments arg : arguments) {
            String label = (String) arg.get()[0];
            String foodId = (String) arg.get()[1];
            Object measureType = arg.get()[2];
            int amount = (int) (arg.get()[3]);
            Map<String, IngredientRequestWrapper> ingredientRequestWrapperMap = ingredientRequestBuilder.getIngredientRequestMap();

            String uri;

            if (measureType instanceof MeasureType) {
                uri = ((MeasureType) measureType).getUri();
            } else {
                uri = (String) measureType;
            }
            if (ingredientRequestWrapperMap.containsKey(foodId)) {
                // Assume that the current food doesnt have the current measure
                for (IngredientRequestData item : ingredientRequestWrapperMap.get(foodId).getRequestBody().getIngredients()) {
                    if (uri.equalsIgnoreCase(item.getMeasureURI())) {
                        break;
                    }
                }
            }
            if (measureType instanceof MeasureType) {
                assertDoesNotThrow(() -> ingredientRequestBuilder.addMeasure(label,
                        foodId, (MeasureType) measureType, amount));
            } else {
                assertDoesNotThrow(() -> ingredientRequestBuilder.addMeasure(label,
                        foodId, uri, amount));
            }
        }
    }

    public void addNutrients(EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder, List<Arguments> arguments) {
        for (Arguments argument : arguments) {
            String operation = (String) argument.get()[0];
            Nutrient nutrient = (Nutrient) argument.get()[1];
            switch (operation) {
                case "range" -> {
                    int min = (int) (argument.get()[2]);
                    int max = (int) (argument.get()[3]);
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientRange(nutrient, min, max));
                }
                case "max" -> {
                    int max = (int) argument.get()[2];
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMax(nutrient, max));
                }
                case "min" -> {
                    int min = (int) argument.get()[2];
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMin(nutrient, min));
                }
            }
        }
    }

    public void validateIngredientMeasureRequests(EdamamFoodIngredientRequestBuilder ingredientRequestBuilder, List<IngredientRequestBody> ingredientRequestBodies, String id, String measureUri, int quantity) {
        boolean isFound = false;
        for (IngredientRequestBody body : ingredientRequestBodies) {
            IngredientRequestData requestData = body.getIngredients().get(0);
            if (requestData.getFoodId().equals(id) && requestData.getMeasureURI().equals(measureUri)) {
                assertEquals(quantity, requestData.getQuantity());
                isFound = true;
                break;
            }
        }
        assertTrue(isFound);
        Map<String, IngredientRequestWrapper> ingredientRequestWrapperMap = ingredientRequestBuilder.getIngredientRequestMap();
        IngredientRequestWrapper currentWrapper = ingredientRequestWrapperMap.get(id);
        isFound = false;
        for (IngredientRequestData data : currentWrapper.getRequestBody().getIngredients()) {
            if (data.getFoodId().equals(id) && data.getMeasureURI().equals(measureUri)) {
                assertEquals(quantity, data.getQuantity());
                isFound = true;
                break;
            }
        }
        assertTrue(isFound);
    }

    public void addMeasuresToIngredientRequest(List<Arguments> arguments, EdamamFoodIngredientRequestBuilder ingredientRequestBuilder) {
        for (Arguments arg : arguments) {
            String label = (String) arg.get()[0];
            String foodId = (String) arg.get()[1];
            Object measureType = arg.get()[2];
            int amount = (int) (arg.get()[3]);
            if (measureType instanceof MeasureType) {
                assertDoesNotThrow(() -> ingredientRequestBuilder.addMeasure(label,
                        foodId, (MeasureType) measureType, amount));
            } else {
                assertDoesNotThrow(() -> ingredientRequestBuilder.addMeasure(label,
                        foodId, (String) measureType, amount));
            }
        }
    }
}
