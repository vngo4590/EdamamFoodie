package au.edu.sydney.soft3202.majorproject.api;

import au.edu.sydney.soft3202.majorproject.model.api.ApiMode;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.RetrofitFactory;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.*;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.online.EdamamFoodApiRetrofitFactory;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.online.OnlineEdamamFoodRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiService;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.online.OnlinePastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.online.PastebinApiRetrofitFactory;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.*;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.online.OnlineRedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.online.RedditApiAccessType;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.online.RedditApiRetrofitFactory;
import au.edu.sydney.soft3202.majorproject.model.db.NutrientDao;
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
import au.edu.sydney.soft3202.majorproject.utils.FoodEdamamDummyData;
import okhttp3.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public abstract class BaseOnlineRepositoryTest {
    /**
     * Call of FoodParserResponse
     */
    protected Call<FoodParserResponse> foodParserResponseCall;
    /**
     * Call of FoodParserResponse but for next page of the response
     */
    protected Call<FoodParserResponse> foodParserResponseNextPageCall;
    /**
     * Call of NutrientsResponse
     */
    protected Call<NutrientsResponse> nutrientsResponseCall;
    /**
     * Call of the Pastebin response
     */
    protected Call<String> pastebinResponseCall;
    /**
     * Call of the Access Reddit Authorization response
     */
    protected Call<AuthorizationResponse> redditAuthorizationCall;
    /**
     * Call of the Access Reddit Authorization response
     */
    protected Call<SubmissionParentResponse> redditSubmissionResponseCall;

    /**
     * Observer for cache hit
     * */
    protected CacheHitObserver cacheHitObserver;

    protected MockedStatic<EdamamRepositorySingleton> edamamRepositorySingleton;
    protected MockedStatic<RedditRepositorySingleton> redditRepositorySingleton;
    protected MockedStatic<PastebinRepositorySingleton> pastebinRepositorySingleton;

    /*
     * Set response that these calls will be sending through
     * */
    protected Response<FoodParserResponse> foodParserResponseResult;
    protected Response<FoodParserResponse> foodParserResponseNextPageResult;
    protected Response<NutrientsResponse> nutrientsResponseResult;
    protected Response<String> pastebinResponseResult;
    protected Response<SubmissionParentResponse> redditSubmissionResponseResult;
    protected Response<AuthorizationResponse> redditAuthorizationResponseResult;

    /**
     * A listener to be inserted to the repository to get the data we want
     */
    protected ResponseCallbackListener<FoodParserResponse> foodParserResponseCallbackListener;
    protected ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener;
    protected ResponseCallbackListener<String> pastebinResponseCallbackListener;
    protected ResponseCallbackListener<SubmissionParentResponse> submissionResponseCallbackListener;
    protected ResponseCallbackListener<AuthorizationResponse> authorizationResponseCallbackListener;


    protected EdamamRepository foodRepository;
    protected EdamamFoodApiService edamamFoodApiService;
    protected PastebinRepository pastebinRepository;
    protected PastebinApiService pastebinApiService;

    protected RedditAccessApiService redditAccessApiService;
    protected RedditOauthApiService redditOauthApiService;
    protected RedditTokenAccessObserver redditTokenAccessObserver;
    protected RedditRepository redditRepository;

    /*
     * Query builder for api calls
     * */
    protected ArgumentCaptor<Map<String, String>> queryInputCaptor;
    protected ArgumentCaptor<IngredientRequestBody> ingredientRequestBodyCaptor;
    protected ArgumentCaptor<String> stringCaptor;
    /*
     * Reddit call captors
     * */
    protected ArgumentCaptor<String> redditGrantTypeCaptor;
    protected ArgumentCaptor<String> redditUsernameCaptor;
    protected ArgumentCaptor<String> redditPasswordCaptor;
    protected ArgumentCaptor<Map<String, String>> redditPostCaptor;
    protected ArgumentCaptor<String> redditAuthTokenCaptor;
    protected ArgumentCaptor<NutrientsResponse> nutrientResponseCaptor;


    protected ArgumentCaptor<Object> disposableCaptor;

    protected NutrientDao nutrientDao;

    public void setup() {
        foodParserResponseCall = Mockito.mock(Call.class);
        foodParserResponseNextPageCall = Mockito.mock(Call.class);
        nutrientsResponseCall = Mockito.mock(Call.class);
        pastebinResponseCall = Mockito.mock(Call.class);
        redditAuthorizationCall = Mockito.mock(Call.class);
        redditSubmissionResponseCall = Mockito.mock(Call.class);

        /*
         * Initialize responses to API call
         * */
        foodParserResponseResult = Mockito.mock(Response.class);
        foodParserResponseNextPageResult = Mockito.mock(Response.class);
        nutrientsResponseResult = Mockito.mock(Response.class);
        pastebinResponseResult = Mockito.mock(Response.class);
        redditAuthorizationResponseResult = Mockito.mock(Response.class);
        redditSubmissionResponseResult = Mockito.mock(Response.class);

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
        queryInputCaptor = ArgumentCaptor.forClass(Map.class);
        stringCaptor = ArgumentCaptor.forClass(String.class);
        ingredientRequestBodyCaptor = ArgumentCaptor.forClass(IngredientRequestBody.class);
        disposableCaptor = ArgumentCaptor.forClass(Object.class);

        redditGrantTypeCaptor = ArgumentCaptor.forClass(String.class);
        redditUsernameCaptor = ArgumentCaptor.forClass(String.class);
        redditPasswordCaptor = ArgumentCaptor.forClass(String.class);
        redditPostCaptor = ArgumentCaptor.forClass(Map.class);
        redditAuthTokenCaptor = ArgumentCaptor.forClass(String.class);
        nutrientResponseCaptor = ArgumentCaptor.forClass(NutrientsResponse.class);

        /*
         * Set to do nothing initially
         * */
        Mockito.doNothing().when(this.foodParserResponseCall)
                .enqueue(foodParserResponseCallbackListener);
        Mockito.doNothing().when(this.nutrientsResponseCall)
                .enqueue(nutrientsResponseCallbackListener);
        Mockito.doNothing().when(this.foodParserResponseNextPageCall)
                .enqueue(foodParserResponseCallbackListener);
        Mockito.doNothing().when(this.pastebinResponseCall)
                .enqueue(pastebinResponseCallbackListener);

        /*
         * Do nothing on success. This needs to be overridden later
         * */
        doAnswer(invocation -> null).when(foodParserResponseCallbackListener).onSuccess(disposableCaptor.capture());
        doAnswer(invocation -> null).when(nutrientsResponseCallbackListener).onSuccess(disposableCaptor.capture());
        doAnswer(invocation -> null).when(pastebinResponseCallbackListener).onSuccess(disposableCaptor.capture());

        /*
         * Mock online api service
         * */
        edamamFoodApiService = Mockito.mock(EdamamFoodApiService.class);
        pastebinApiService = Mockito.mock(PastebinApiService.class);
        redditAccessApiService = mock(RedditAccessApiService.class);
        redditOauthApiService = mock(RedditOauthApiService.class);
        redditTokenAccessObserver = mock(RedditTokenAccessObserver.class);
        doNothing().when(redditTokenAccessObserver).updateToken(redditAuthTokenCaptor.capture());

        PastebinApiRetrofitFactory pastebinApiRetrofitFactory = new PastebinApiRetrofitFactory();
        Retrofit pastebinRetrofit = Mockito.spy(pastebinApiRetrofitFactory.getRetrofit());
        when(pastebinRetrofit.create(PastebinApiService.class)).thenReturn(pastebinApiService);

        EdamamFoodApiRetrofitFactory edamamFoodApiRetrofitFactory = new EdamamFoodApiRetrofitFactory();
        Retrofit edamamRetrofit = Mockito.spy(edamamFoodApiRetrofitFactory.getRetrofit());
        when(edamamRetrofit.create(EdamamFoodApiService.class)).thenReturn(edamamFoodApiService);

        Mockito.when(edamamFoodApiService.getFoodParserResponse(queryInputCaptor.capture()))
                .thenReturn(foodParserResponseCall);
        Mockito.when(edamamFoodApiService.getFoodParserResponseWithURL(stringCaptor.capture()))
                .thenReturn(foodParserResponseNextPageCall);
        Mockito.when(edamamFoodApiService.getNutrientInfo(
                queryInputCaptor.capture(),
                ingredientRequestBodyCaptor.capture())).thenReturn(nutrientsResponseCall);
        Mockito.when(pastebinApiService.submitPaste(queryInputCaptor.capture())).thenReturn(
                pastebinResponseCall
        );
        Mockito.when(redditAccessApiService.getAccessToken(redditGrantTypeCaptor.capture(),
                        redditUsernameCaptor.capture(),
                        redditPasswordCaptor.capture()))
                .thenReturn(redditAuthorizationCall);
        Mockito.when(redditOauthApiService.submitPost(redditPostCaptor.capture()))
                .thenReturn(redditSubmissionResponseCall);
        /*
         * Setting up services for reddit
         * */

        RetrofitFactory accessRetrofitFactory = new RedditApiRetrofitFactory(RedditApiAccessType.ACCESS);
        Retrofit accessRetrofit = Mockito.spy(accessRetrofitFactory.getRetrofit());
        when(accessRetrofit.create(RedditAccessApiService.class)).thenReturn(redditAccessApiService);

        RetrofitFactory oauthRetrofitFactory = new RedditApiRetrofitFactory(RedditApiAccessType.OAUTH);
        Retrofit oauthRetrofit = Mockito.spy(oauthRetrofitFactory.getRetrofit());
        when(oauthRetrofit.create(RedditOauthApiService.class)).thenReturn(redditOauthApiService);

        redditRepository = new OnlineRedditRepository(redditAccessApiService, redditOauthApiService, redditTokenAccessObserver);

        doAnswer(invocation -> {
            if (redditAuthTokenCaptor.getValue() != null) {
                redditRepository.setRedditAccessState(RedditAccessState.LOGGED_IN);
            }
            return null;
        }).when(redditTokenAccessObserver).updateToken(redditAuthTokenCaptor.capture());

        /*
         * Mock of the DAO
         * */
        nutrientDao = mock(NutrientDao.class);


        /*
         * Initialize the repository
         * */
        foodRepository = new OnlineEdamamFoodRepository(edamamRetrofit.create(EdamamFoodApiService.class), nutrientDao);
        cacheHitObserver = mock(CacheHitObserver.class);
        doNothing().when(cacheHitObserver).onCacheHit();
        pastebinRepository = new OnlinePastebinRepository(pastebinRetrofit.create(PastebinApiService.class));
        /*
         * Create initiate the repository singletons
         * */
        edamamRepositorySingleton = Mockito.mockStatic(EdamamRepositorySingleton.class);
        edamamRepositorySingleton.when(() -> EdamamRepositorySingleton.init(ApiMode.ONLINE)).thenAnswer(invocation -> null);
        edamamRepositorySingleton.when(EdamamRepositorySingleton::getInstance).thenReturn(foodRepository);

        redditRepositorySingleton = Mockito.mockStatic(RedditRepositorySingleton.class);
        redditRepositorySingleton.when(() -> RedditRepositorySingleton.init(ApiMode.ONLINE)).thenAnswer(invocation -> null);
        redditRepositorySingleton.when(RedditRepositorySingleton::getInstance).thenReturn(redditRepository);

        pastebinRepositorySingleton = Mockito.mockStatic(PastebinRepositorySingleton.class);
        pastebinRepositorySingleton.when(() -> PastebinRepositorySingleton.init(ApiMode.ONLINE)).thenAnswer(invocation -> null);
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
    }

    public void validateFoodParserResponse(FoodParserResponse foodParserResponse) {
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
            ResponseCallbackListener<FoodParserResponse> listener,
            Call<FoodParserResponse> call, Response<FoodParserResponse> response, Map<String, String> queryMap
    ) {
        makeSuccessfulIngredientRequest(listener, call, response, queryMap);
        verify(listener, atLeastOnce()).onSuccess(disposableCaptor.capture());
        assertThat(disposableCaptor.getValue(), is(not(equalTo(null))));
        assertThat(disposableCaptor.getValue().getClass(), is(equalTo(FoodParserResponse.class)));
        assertThat(response.body(), is(equalTo(disposableCaptor.getValue())));
        assertThat(listener.getResponse(), is(equalTo(response)));
    }

    public void makeSuccessfulNutrientsRequest(
            ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener,
            Call<NutrientsResponse> nutrientsResponseCall,
            Response<NutrientsResponse> nutrientsResponseResult,
            NutrientDao nutrientDao,
            boolean doLoadCache,
            IngredientRequestBody body) {
        NutrientsResponse nutrientsResponse =
                FoodEdamamDummyData.getNutrientsSampleData();
        when(nutrientsResponseResult.isSuccessful()).thenReturn(true);
        when(nutrientsResponseResult.body()).thenReturn(
                nutrientsResponse
        );
        try {
            doReturn(nutrientsResponseResult)
                    .when(nutrientsResponseCall).execute();
            IngredientRequestData ingredientRequestData = body.getIngredients().get(0);
            doNothing().when(nutrientDao).insertNutrientResponse(nutrientResponseCaptor.capture());
            /*
             * Make sure the cache hit is found by returning the string
             * */
            doReturn(FoodEdamamDummyData.getNutrientsRawSampleData())
                    .when(nutrientDao)
                    .getNutrientResponse(ingredientRequestData.getFoodId(),
                            ingredientRequestData.getMeasureURI(),
                            ingredientRequestData.getQuantity());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        EdamamRepositorySingleton.getInstance().getNutrientInfo(doLoadCache, body, nutrientsResponseCallbackListener);
    }

    public void makeSuccessfulNutrientsRequest(
            ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener,
            Call<NutrientsResponse> nutrientsResponseCall,
            Response<NutrientsResponse> nutrientsResponseResult,
            NutrientDao nutrientDao,
            IngredientRequestBody body, CacheHitObserver cacheHitObserver) {
        NutrientsResponse nutrientsResponse =
                FoodEdamamDummyData.getNutrientsSampleData();
        when(nutrientsResponseResult.isSuccessful()).thenReturn(true);
        when(nutrientsResponseResult.body()).thenReturn(
                nutrientsResponse
        );
        IngredientRequestData ingredientRequestData = body.getIngredients().get(0);
        try {
            doReturn(nutrientsResponseResult)
                    .when(nutrientsResponseCall).execute();
            doNothing().when(nutrientDao).insertNutrientResponse(nutrientResponseCaptor.capture());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        EdamamRepositorySingleton.getInstance().getNutrientInfo(body, nutrientsResponseCallbackListener, cacheHitObserver);
        /*
         * Make sure the cache hit is found by returning the string
         * */
        try {
            doReturn(FoodEdamamDummyData.getNutrientsRawSampleData())
                    .when(nutrientDao)
                    .getNutrientResponse(ingredientRequestData.getFoodId(),
                            ingredientRequestData.getMeasureURI(),
                            ingredientRequestData.getQuantity());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeUnSuccessfulNutrientsRequestWithDaoFetchingError(
            ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener,
            Call<NutrientsResponse> nutrientsResponseCall,
            Response<NutrientsResponse> nutrientsResponseResult,
            NutrientDao nutrientDao,
            IngredientRequestBody body) {
        NutrientsResponse nutrientsResponse =
                FoodEdamamDummyData.getNutrientsSampleData();
        when(nutrientsResponseResult.isSuccessful()).thenReturn(true);
        when(nutrientsResponseResult.body()).thenReturn(
                nutrientsResponse
        );
        try {
            doReturn(nutrientsResponseResult)
                    .when(nutrientsResponseCall).execute();
            IngredientRequestData ingredientRequestData = body.getIngredients().get(0);
            doNothing().when(nutrientDao).insertNutrientResponse(nutrientResponseCaptor.capture());
            doThrow(new SQLException("An Error occurred while fetching the response"))
                    .when(nutrientDao)
                    .getNutrientResponse(ingredientRequestData.getFoodId(),
                            ingredientRequestData.getMeasureURI(),
                            ingredientRequestData.getQuantity());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        EdamamRepositorySingleton.getInstance().getNutrientInfo(true, body, nutrientsResponseCallbackListener);
    }

    public void makeUnSuccessfulNutrientsRequestWithInsertionError(
            ResponseCallbackListener<NutrientsResponse> nutrientsResponseCallbackListener,
            Call<NutrientsResponse> nutrientsResponseCall,
            Response<NutrientsResponse> nutrientsResponseResult,
            NutrientDao nutrientDao,
            IngredientRequestBody body) {
        NutrientsResponse nutrientsResponse =
                FoodEdamamDummyData.getNutrientsSampleData();
        when(nutrientsResponseResult.isSuccessful()).thenReturn(true);
        when(nutrientsResponseResult.body()).thenReturn(
                nutrientsResponse
        );
        try {
            doReturn(nutrientsResponseResult)
                    .when(nutrientsResponseCall).execute();
            IngredientRequestData ingredientRequestData = body.getIngredients().get(0);
            doThrow(new SQLException("An Error Occurred while trying to insert data"))
                    .when(nutrientDao)
                    .insertNutrientResponse(nutrientResponseCaptor.capture());
            doReturn(FoodEdamamDummyData.getNutrientsRawSampleData())
                    .when(nutrientDao)
                    .getNutrientResponse(ingredientRequestData.getFoodId(),
                            ingredientRequestData.getMeasureURI(),
                            ingredientRequestData.getQuantity());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        EdamamRepositorySingleton.getInstance().getNutrientInfo(false, body, nutrientsResponseCallbackListener);
    }

    public void makeSuccessfulIngredientRequest(ResponseCallbackListener<FoodParserResponse> listener, Call<FoodParserResponse> call, Response<FoodParserResponse> response, Map<String, String> queryMap) {
        doAnswer(invocation -> {
            when(response.isSuccessful()).thenReturn(true);
            when(response.body()).thenReturn(
                    FoodEdamamDummyData.getSampleData(queryMap.get("ingr"))
            );
            listener.onResponse(call, response);
            return null;
        }).when(call).enqueue(listener);
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        EdamamRepositorySingleton.getInstance().getFoodParserResponse(edamamFoodApiQueryBuilder.build(), listener);
    }


    public void makeSuccessfulIngredientNextPageRequest(ResponseCallbackListener<FoodParserResponse> listener, Call<FoodParserResponse> call, Response<FoodParserResponse> response, String url) {
        doAnswer(invocation -> {
            when(response.isSuccessful()).thenReturn(true);
            when(response.body()).thenReturn(
                    FoodEdamamDummyData.getSampleData("Next Page Sample Ingredient")
            );
            listener.onResponse(call, response);
            return null;
        }).when(call).enqueue(listener);
        EdamamRepositorySingleton.getInstance().getFoodParserResponseWithURL(url, listener);
    }

    public void makeSuccessfulPastebinRequest(ResponseCallbackListener<String> listener, Call<String> call, Response<String> response, String msg) {
        PastebinRepositorySingleton.getInstance().getPastebinApiQueryBuilder().setPasteContent(msg);
        PastebinApiQueryBuilder pastebinApiQueryBuilder = PastebinRepositorySingleton.getInstance()
                .getPastebinApiQueryBuilder();
        doAnswer(invocation -> {
            when(response.isSuccessful()).thenReturn(true);
            when(response.body()).thenReturn(
                    "The-result-page.com"
            );
            listener.onResponse(call, response);
            return null;
        }).when(call).enqueue(listener);
        PastebinRepositorySingleton.getInstance().submitPaste(pastebinApiQueryBuilder.build(), listener);
    }

    public void validateInputNutrient(Map<String, String> query, int querySize, Nutrient inputNutrient, String inputNutrientValue) {
        assertThat(query.size(), equalTo(querySize));
        assertNotNull(query.get(String.format("nutrients[%s]", inputNutrient.getTypeNTRCode())));
        assertEquals(query.get(String.format("nutrients[%s]", inputNutrient.getTypeNTRCode())), inputNutrientValue);
    }

    public void validateNoIngredients(ResponseCallbackListener<FoodParserResponse> listener,
                                       Call<FoodParserResponse> call, Response<FoodParserResponse> response) {
        doAnswer(invocation -> {
            mockEnqueueOnFailure(call,
                    response,
                    listener,
                    FoodEdamamDummyData.getErrorResponseNoIngredients(),
                    999
            );
            return null;
        }).when(call).enqueue(listener);
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        EdamamRepositorySingleton.getInstance().getFoodParserResponse(edamamFoodApiQueryBuilder.build(), listener);
        assertThat(listener.getErrorResponse(), is(not(equalTo(null))));
        assertThat(listener.getErrorResponse().getError(), is(equalTo("bad_request")));
        assertThat(listener.getErrorResponse().getMessage(), is(equalTo("Expected exactly one of ingredient text (ingr) or upc")));
        assertThat(listener.getErrorResponse().getErrorCode(), is(equalTo("999")));

    }

    public void mockEnqueueOnFailure(Call<?> call, Response<?> response, ResponseCallbackListener<?> responseCallbackListener, String errorMessage, int responseCode) {
        when(response.isSuccessful()).thenReturn(false);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        try {
            when(responseBody.string()).thenReturn(errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        when(response.errorBody()).thenReturn(responseBody);
        when(response.code()).thenReturn(responseCode);
        responseCallbackListener.onResponse(call, response);
    }

    public void mockEnqueueOnFailureWithException(Call<?> call, ResponseCallbackListener<?> responseCallbackListener, String errorMessage) {
        Throwable t = new IllegalStateException(errorMessage);
        responseCallbackListener.onFailure(call, t);
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

    public void validateAuthorizationResponse(AuthorizationResponse expectedResult, AuthorizationResponse result) {
        assertNotNull(result);
        assertNotNull(expectedResult);
        assertEquals(expectedResult.getTokenType(), result.getTokenType());
        assertEquals(expectedResult.getAccessToken(), result.getAccessToken());
        assertEquals(expectedResult.getError(), result.getError());
        assertEquals(expectedResult.getScope(), result.getScope());
        assertEquals(expectedResult.getExpiresIn(), result.getExpiresIn());
    }

    public void validateRedditErrorMessage(SubmissionParentResponse expected, SubmissionParentResponse result) {
        List<String> expectedMessages = new ArrayList<>();
        List<String> resultMessages = new ArrayList<>();
        for (List<String> es : expected.getSubmissionResponse().getErrors()) {
            expectedMessages.addAll(es);
        }
        for (List<String> es : result.getSubmissionResponse().getErrors()) {
            resultMessages.addAll(es);
        }
        assertFalse(expectedMessages.isEmpty());
        assertFalse(resultMessages.isEmpty());
        assertThat(expectedMessages, Matchers.containsInAnyOrder(resultMessages.toArray()));
    }

    public void makeSuccessfulRedditPostRequest(
            ResponseCallbackListener<SubmissionParentResponse> listener,
            Call<SubmissionParentResponse> call,
            Response<SubmissionParentResponse> response,
            SubmissionParentResponse result
    ) {
        doAnswer(invocation -> {
            when(response.isSuccessful()).thenReturn(true);
            when(response.body()).thenReturn(
                    result
            );
            listener.onSuccess(response.body());
            return null;
        }).when(call).enqueue(listener);
        RedditPostQueryBuilder queryBuilder = RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder();
        RedditRepositorySingleton.getInstance().submitPost(queryBuilder.build(), listener);
    }

    public void makeSuccessfulRedditLoginRequest(
            ResponseCallbackListener<AuthorizationResponse> listener,
            Call<AuthorizationResponse> call,
            Response<AuthorizationResponse> response,
            String username,
            String password,
            AuthorizationResponse result
    ) {
        try {
            when(response.isSuccessful()).thenReturn(true);
            when(response.body()).thenReturn(result);
            doReturn(response).when(call).execute();
            doNothing().when(listener).onSuccess(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RedditRepositorySingleton.getInstance().getAccessToken(username, password, listener);
    }

    public void makeUnsuccessfulRedditLoginRequest(
            ResponseCallbackListener<AuthorizationResponse> listener,
            Call<AuthorizationResponse> call,
            String username,
            String password,
            AuthorizationResponse result
    ) {
        try {
            doThrow(new IOException("An Error Occurred While trying to execute call")).when(call).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RedditRepositorySingleton.getInstance().getAccessToken(username, password, listener);
    }

}
