package au.edu.sydney.soft3202.majorproject.api;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodIngredientRequestBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditAccessState;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditPostQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestData;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestWrapper;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodInfo;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionResponse;
import au.edu.sydney.soft3202.majorproject.model.type.MeasureType;
import au.edu.sydney.soft3202.majorproject.model.type.Nutrient;
import au.edu.sydney.soft3202.majorproject.utils.FoodEdamamDummyData;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
* Same as online testing but different repositories
* */
public class OfflineRepositoryTest extends BaseOfflineRepositoryTest {

    @BeforeEach
    public void setup() {
        super.setup();
    }
    @AfterEach
    public void cleanUp() {
        super.cleanUp();
    }

    /*
     * UC001: Test user inserting ingredients:
     *   1. User insert No ingredients --> User should not see any list
     *   2. User insert 1 Ingredients --> User should list of matching ingredients
     *   2.1. User enters invalid arguments. eg: Ingredient name is null or empty
     *   4. User insert Multiple ingredients --> User should list of matching ingredients
     */

    @Test
    public void uc001TestUserInsertingSimpleIngredient() {
        checkRepositoryQueryBeforeModification();
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("strawberry icecream");
        Map<String, String> edamamQuery = edamamFoodApiQueryBuilder.build();
        assertThat(edamamQuery.size(), equalTo(3));
        assertThat(edamamQuery.get("ingr"), equalTo("strawberry icecream"));
        validateIngredientQuery(foodParserResponseCallbackListener, edamamQuery);
        FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();
        validateFoodParserResponse(edamamQuery, foodParserResponse);
    }


    @Test
    public void uc001TestUserInsertingMultipleMixedIngredients() {
        checkRepositoryQueryBeforeModification();
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("strawberry icecream")
                .addIngredient("honey").addIngredient("cake").addIngredient("         bacon sauce       ");

        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addIngredient(null));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addIngredient(""));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addIngredient("    "));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addIngredient(null).addIngredient("sausage").addIngredient(""));

        Map<String, String> edamamQuery = edamamFoodApiQueryBuilder.build();
        assertThat(edamamQuery.size(), equalTo(3));
        assertThat(edamamQuery.get("ingr"), equalTo("strawberry icecream and honey and cake and bacon sauce"));

        validateIngredientQuery(foodParserResponseCallbackListener, edamamQuery);
        FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();

        validateFoodParserResponse(edamamQuery, foodParserResponse);
    }

    @Test
    public void uc001TestUserInsertingMultipleMixedIngredientsAndLoadNextPage() {
        checkRepositoryQueryBeforeModification();
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("strawberry icecream")
                .addIngredient("honey").addIngredient("cake").addIngredient("bacon sauce");

        Map<String, String> edamamQuery = edamamFoodApiQueryBuilder.build();
        assertThat(edamamQuery.size(), equalTo(3));
        assertThat(edamamQuery.get("ingr"), equalTo("strawberry icecream and honey and cake and bacon sauce"));

        validateIngredientQuery(foodParserResponseCallbackListener, edamamQuery);
        FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();

        validateFoodParserResponse(edamamQuery, foodParserResponse);

        makeSuccessfulIngredientNextPageRequest(foodParserResponseCallbackListener, "My URL");

        verify(foodParserResponseCallbackListener, atLeastOnce()).onSuccess(disposableCaptor.capture());
        assertThat(disposableCaptor.getValue(), is(not(equalTo(null))));
        assertThat(disposableCaptor.getValue().getClass(), is(equalTo(FoodParserResponse.class)));
    }

    @Test
    public void uc001TestUserInsertingAndResettingMultipleMixedIngredients() {
        checkRepositoryQueryBeforeModification();
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("strawberry icecream")
                .addIngredient("honey").addIngredient("cake");

        Map<String, String> edamamQuery = edamamFoodApiQueryBuilder.build();
        assertThat(edamamQuery.size(), equalTo(3));
        assertThat(edamamQuery.get("ingr"), equalTo("strawberry icecream and honey and cake"));
        validateIngredientQuery(foodParserResponseCallbackListener, edamamQuery);
        FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();

        validateFoodParserResponse(edamamQuery, foodParserResponse);

        edamamFoodApiQueryBuilder.resetIngredients();
        checkRepositoryQueryBeforeModification();

        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addIngredient("chocolate").addIngredient(null));
        edamamFoodApiQueryBuilder.addIngredient("banana");
        Map<String, String> newQuery = edamamFoodApiQueryBuilder.build();
        assertThat(newQuery.size(), equalTo(3));
        assertThat(newQuery.get("ingr"), equalTo("chocolate and banana"));

        validateIngredientQuery(foodParserResponseCallbackListener, newQuery);
        foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();
        validateFoodParserResponse(newQuery, foodParserResponse);

        EdamamRepositorySingleton.getInstance().reset();
        assertNull(edamamFoodApiQueryBuilder.build().get("ingr"));

        // Re add the ingredients again
        assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addIngredient("chocolate"));
        edamamFoodApiQueryBuilder.addIngredient("banana");
        newQuery = edamamFoodApiQueryBuilder.build();
        assertThat(newQuery.size(), equalTo(3));
        assertThat(newQuery.get("ingr"), equalTo("chocolate and banana"));
        validateIngredientQuery(foodParserResponseCallbackListener, newQuery);
        foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();
        validateFoodParserResponse(newQuery, foodParserResponse);

    }


    /*
     * UC001xAF01: Test user enter nutrient values:
     *   1. User enters invalid arguments
     *   2. User insert 1 nutrient value
     *   3. User insert multiple nutrient value
     *   4. User insert nutrient values with many combinations of max, min and between ranges --> User should list of matching ingredients
     *       & Nutrient values bound to limit
     * */
    @Test
    public void uc001xAF001TestUserAddingIncorrectNutrients() {
        checkRepositoryQueryBeforeModification();
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("Sweet Chocolate").addIngredient("banana");
        Map<String, String> query = edamamFoodApiQueryBuilder.build();
        assertThat(query.size(), equalTo(3));
        assertThat(query.get("ingr"), equalTo("Sweet Chocolate and banana"));

        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientMax(Nutrient.CA, -1));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientMin(Nutrient.CA, -1));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientRange(Nutrient.CA, -1, 100));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientRange(Nutrient.CA, 101, 100));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientRange(Nutrient.CA, -1, -1));
        assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMin(Nutrient.CA, 0));
        int querySize = 4;
        Nutrient inputNutrient = Nutrient.CA;
        String inputNutrientValue = "0+";
        query = edamamFoodApiQueryBuilder.build();
        validateInputNutrient(query, querySize, inputNutrient, inputNutrientValue);

        assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMax(Nutrient.CA, 0));
        query = edamamFoodApiQueryBuilder.build();
        inputNutrientValue = "0";
        validateInputNutrient(query, querySize, inputNutrient, inputNutrientValue);

        validateIngredientQuery(foodParserResponseCallbackListener, query);

        assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientRange(Nutrient.CA, 10, 1000));
        query = edamamFoodApiQueryBuilder.build();
        inputNutrientValue = "10-1000";
        validateInputNutrient(query, querySize, inputNutrient, inputNutrientValue);

        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientMax(Nutrient.CA, -1));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientMin(Nutrient.CA, -1));
        assertThrows(IllegalArgumentException.class, () -> edamamFoodApiQueryBuilder.addNutrientRange(Nutrient.CA, -1, -1));
        validateInputNutrient(query, querySize, inputNutrient, inputNutrientValue);

        assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientRange(Nutrient.PROCNT, 10, 100));
        querySize = 5;
        inputNutrient = Nutrient.PROCNT;
        inputNutrientValue = "10-100";
        query = edamamFoodApiQueryBuilder.build();
        validateInputNutrient(query, querySize, inputNutrient, inputNutrientValue);
        validateInputNutrient(query, querySize, Nutrient.CA, "10-1000");

        assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMin(Nutrient.CA, 100));
        query = edamamFoodApiQueryBuilder.build();
        validateInputNutrient(query, querySize, Nutrient.CA, "100+");
        validateIngredientQuery(foodParserResponseCallbackListener, query);
        verify(foodParserResponseCallbackListener, times(2)).onSuccess(disposableCaptor.capture());
        FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();
        /*
         * Send successful response
         * */
        validateFoodParserResponse(query, foodParserResponse);
    }


    @Test
    public void uc001xAF001TestUserAddingNutrientsNoIngredients() {
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        List<Arguments> arguments = List.of(
                Arguments.of("max", Nutrient.CA, 90),
                Arguments.of("max", Nutrient.PROCNT, 90),
                Arguments.of("range", Nutrient.ADDED_SUGAR, 10, 1900),
                Arguments.of("max", Nutrient.CARBOHYDRATE, 90),
                Arguments.of("min", Nutrient.FAMS, 90),
                Arguments.of("range", Nutrient.FATRN, 100, 1000)
        );
        int querySize = 3;
        Map<String, String> query;
        for (Arguments argument : arguments) {
            String operation = (String) argument.get()[0];
            Nutrient nutrient = (Nutrient) argument.get()[1];
            switch (operation) {
                case "range" -> {
                    int min = (int) (argument.get()[2]);
                    int max = (int) (argument.get()[3]);
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientRange(nutrient, min, max));
                    query = edamamFoodApiQueryBuilder.build();
                    validateInputNutrient(query, querySize, nutrient, String.format("%s-%s", min, max));
                    assertEquals(max, edamamFoodApiQueryBuilder.getMax(nutrient.getTypeName()));
                }
                case "max" -> {
                    int max = (int) argument.get()[2];
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMax(nutrient, max));
                    query = edamamFoodApiQueryBuilder.build();
                    validateInputNutrient(query, querySize, nutrient, String.format("%s", max));
                    assertEquals(max, edamamFoodApiQueryBuilder.getMax(nutrient.getTypeName()));
                }
                case "min" -> {
                    int min = (int) argument.get()[2];
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMin(nutrient, min));
                    query = edamamFoodApiQueryBuilder.build();
                    validateInputNutrient(query, querySize, nutrient, String.format("%s+", min));
                    assertEquals(-1, edamamFoodApiQueryBuilder.getMax(nutrient.getTypeName()));
                }
            }
            querySize += 1;
        }
    }


    @Test
    public void uc001xAF001TestUserInsertMultipleMixedNutrientsSuccessfully() {
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("Chocolate");
        List<Arguments> arguments = List.of(
                Arguments.of("max", Nutrient.CA, 90),
                Arguments.of("max", Nutrient.PROCNT, 90),
                Arguments.of("range", Nutrient.ADDED_SUGAR, 10, 1900),
                Arguments.of("max", Nutrient.CARBOHYDRATE, 90),
                Arguments.of("min", Nutrient.FAMS, 90),
                Arguments.of("range", Nutrient.FATRN, 100, 1000),
                Arguments.of("range", Nutrient.FAMS, 100, 1000),
                Arguments.of("range", Nutrient.PROCNT, 100, 200),
                Arguments.of("range", Nutrient.PROCNT, 100, 200),
                Arguments.of("min", Nutrient.VITD, 100),
                Arguments.of("max", Nutrient.VITA_RAE, 100)
        );

        int querySize = 3;
        Map<String, String> query;
        Set<Nutrient> nutrientSet = new HashSet<>();
        for (Arguments argument : arguments) {
            String operation = (String) argument.get()[0];
            Nutrient nutrient = (Nutrient) argument.get()[1];
            if (!nutrientSet.contains(nutrient)) {
                querySize += 1;
            }
            if (querySize == 7) {
                edamamFoodApiQueryBuilder.resetNutrient();
                nutrientSet = new HashSet<>();
                querySize = 4;
            }
            switch (operation) {
                case "range" -> {
                    int min = (int) (argument.get()[2]);
                    int max = (int) (argument.get()[3]);
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientRange(nutrient, min, max));
                    query = edamamFoodApiQueryBuilder.build();
                    validateInputNutrient(query, querySize, nutrient, String.format("%s-%s", min, max));
                    assertEquals(max, edamamFoodApiQueryBuilder.getMax(nutrient.getTypeName()));
                }
                case "max" -> {
                    int max = (int) argument.get()[2];
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMax(nutrient, max));
                    query = edamamFoodApiQueryBuilder.build();
                    validateInputNutrient(query, querySize, nutrient, String.format("%s", max));
                    assertEquals(max, edamamFoodApiQueryBuilder.getMax(nutrient.getTypeName()));
                }
                case "min" -> {
                    int min = (int) argument.get()[2];
                    assertDoesNotThrow(() -> edamamFoodApiQueryBuilder.addNutrientMin(nutrient, min));
                    query = edamamFoodApiQueryBuilder.build();
                    validateInputNutrient(query, querySize, nutrient, String.format("%s+", min));
                    assertEquals(-1, edamamFoodApiQueryBuilder.getMax(nutrient.getTypeName()));
                }
            }

            nutrientSet.add(nutrient);
        }
        query = edamamFoodApiQueryBuilder.build();
        /*
         * The nutrients + ingredients + keys
         * */
        assertEquals(query.size(), nutrientSet.size() + 3);
        validateIngredientQuery(foodParserResponseCallbackListener, edamamFoodApiQueryBuilder.build());
    }

    /*
     * UC002: User submit measure & get the Nutrient Information for those ingredients
     *   1. User select a measure and submit a request
     *   2. User doesn't select a measure and then submit
     *   3. User selected correct measure but given incorrect quantity
     * */
    @Test
    public void uc002TestUserSelectMeasuresAndSubmit() {
        /*
         * Suppose that the user have already submitted their query,
         * we now test whether they can submit the measurements alongside ingredients
         * */
        EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder = EdamamRepositorySingleton.getInstance().getQueryBuilder();
        edamamFoodApiQueryBuilder.addIngredient("Chocolate");
        List<Arguments> arguments = List.of(
                Arguments.of("max", Nutrient.CA, 90),
                Arguments.of("max", Nutrient.PROCNT, 90),
                Arguments.of("range", Nutrient.ADDED_SUGAR, 10, 1900),
                Arguments.of("max", Nutrient.CARBOHYDRATE, 90)
        );
        addNutrients(edamamFoodApiQueryBuilder, arguments);
        makeSuccessfulIngredientRequest(foodParserResponseCallbackListener,
                edamamFoodApiQueryBuilder.build());

        verify(foodParserResponseCallbackListener, times(1)).onSuccess(disposableCaptor.capture());
        FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();
        // Select a food
        FoodInfo info = foodParserResponse.getHints().get(0).getFood();
        EdamamFoodIngredientRequestBuilder ingredientRequestBuilder = EdamamRepositorySingleton.getInstance().getIngredientRequestBuilder();
        ingredientRequestBuilder.addMeasure(info.getLabel(),
                info.getFoodId(), MeasureType.GRAM, 30);

        // Confirm we have gotten the right data
        List<IngredientRequestBody> ingredientRequestBodies = ingredientRequestBuilder.build();
        assertEquals(ingredientRequestBodies.size(), 1);
        assertNotNull(ingredientRequestBodies.get(0));

        IngredientRequestBody body = ingredientRequestBodies.get(0);
        assertEquals(body.getIngredients().size(), 1);
        assertNotNull(body.getIngredients().get(0));

        IngredientRequestData requestData = body.getIngredients().get(0);
        assertNotNull(requestData.getFoodId());
        assertEquals(requestData.getFoodId(), info.getFoodId());
        assertEquals(requestData.getMeasureURI(), MeasureType.GRAM.getUri());
        assertEquals(requestData.getQuantity(), 30);

        Map<String, IngredientRequestWrapper> ingredientRequestWrapperMap = ingredientRequestBuilder.getIngredientRequestMap();
        assertNotEquals(ingredientRequestWrapperMap.size(), 0);
        assertNotNull(ingredientRequestWrapperMap.get(info.getFoodId()));
        IngredientRequestWrapper ingredientRequestWrapper = ingredientRequestWrapperMap.get(info.getFoodId());
        assertEquals(ingredientRequestWrapper.getRequestBody().getIngredients().size(), 1);

        IngredientRequestData preRequestData = ingredientRequestWrapper.getRequestBody().getIngredients().get(0);
        assertEquals(preRequestData.getQuantity(), requestData.getQuantity());
        assertEquals(preRequestData.getFoodId(), requestData.getFoodId());
        assertEquals(preRequestData.getMeasureURI(), requestData.getMeasureURI());

        makeSuccessfulNutrientsRequest(nutrientsResponseCallbackListener, body);
        verify(nutrientsResponseCallbackListener, times(1)).onSuccess(disposableCaptor.capture());
        NutrientsResponse nutrientsResponse = (NutrientsResponse) disposableCaptor.getValue();
        assertNotNull(nutrientsResponse);
    }

    @Test
    public void uc002TestUserSelectMixedMeasuresAndSubmit() {
        List<Arguments> arguments = List.of(
                Arguments.of("Food Label 1", "food_id_1", MeasureType.GRAM.getUri(), 10),
                Arguments.of("Food Label 2", "food_id_2", "Sample URI", 20),
                Arguments.of("Food Label 3", "food_id_3", "New URI", 20),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 100),
                Arguments.of("Food Label 5", "food_id_4", MeasureType.GRAM, 100),
                Arguments.of("Food Label 6", "food_id_4", MeasureType.KILOGRAM, 100),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 300)
        );
        EdamamFoodIngredientRequestBuilder ingredientRequestBuilder = EdamamRepositorySingleton.getInstance().getIngredientRequestBuilder();
        int mapSize = 0;
        int listSize = 0;
        for (Arguments arg : arguments) {
            String label = (String) arg.get()[0];
            String foodId = (String) arg.get()[1];
            Object measureType = arg.get()[2];
            int amount = (int) (arg.get()[3]);
            Map<String, IngredientRequestWrapper> ingredientRequestWrapperMap = ingredientRequestBuilder.getIngredientRequestMap();
            int ingrSize = 1;

            String uri;

            if (measureType instanceof MeasureType) {
                uri = ((MeasureType) measureType).getUri();
            } else {
                uri = (String) measureType;
            }
            boolean measureExists = false;
            if (!ingredientRequestWrapperMap.containsKey(foodId)) {
                mapSize += 1;
            } else {
                // Assume that the current food doesnt have the current measure
                ingrSize = ingredientRequestWrapperMap.get(foodId).getRequestBody().getIngredients().size() + 1;
                for (IngredientRequestData item : ingredientRequestWrapperMap.get(foodId).getRequestBody().getIngredients()) {
                    if (uri.equalsIgnoreCase(item.getMeasureURI())) {
                        ingrSize--;
                        measureExists = true;
                        break;
                    }
                }
            }
            if (!measureExists) {
                listSize++;
            }
            if (measureType instanceof MeasureType) {
                assertDoesNotThrow(() -> ingredientRequestBuilder.addMeasure(label,
                        foodId, (MeasureType) measureType, amount));
            } else {
                assertDoesNotThrow(() -> ingredientRequestBuilder.addMeasure(label,
                        foodId, uri, amount));
            }
            List<IngredientRequestBody> ingredientRequestBodies = ingredientRequestBuilder.build();
            // 1 we store as map and the other we store as list so we have 2 different sizes
            assertEquals(ingredientRequestBodies.size(), listSize);
            assertEquals(ingredientRequestWrapperMap.size(), mapSize);
            assertEquals(ingredientRequestWrapperMap.get(foodId).getRequestBody().getIngredients().size(), ingrSize);

            // Make sure the corresponding wrapper has the same data
            IngredientRequestWrapper currentWrapper = ingredientRequestWrapperMap.get(foodId);
            IngredientRequestData mappedIngrData = currentWrapper.getRequestBody().getIngredients().get(ingrSize - 1);
            assertEquals(mappedIngrData.getFoodId(), foodId);
            assertEquals(mappedIngrData.getMeasureURI(), uri);
            assertEquals(mappedIngrData.getQuantity(), amount);
        }
        /*Submit*/
        List<IngredientRequestBody> ingredientRequestBodies = ingredientRequestBuilder.build();
        for (int i = 0; i < ingredientRequestBodies.size(); i++) {
            IngredientRequestBody body = ingredientRequestBodies.get(0);
            makeSuccessfulNutrientsRequest(nutrientsResponseCallbackListener, body);
            verify(nutrientsResponseCallbackListener, times(i + 1)).onSuccess(disposableCaptor.capture());
            NutrientsResponse nutrientsResponse = (NutrientsResponse) disposableCaptor.getValue();
            assertNotNull(nutrientsResponse);
        }
    }

    @Test
    public void uc002TestUserSelectMixedMeasuresAndSubmitButIsResetMidwayWithFetchingCacheHit() throws SQLException {
        List<Arguments> arguments = List.of(
                Arguments.of("Food Label 1", "food_id_1", MeasureType.GRAM.getUri(), 10),
                Arguments.of("Food Label 1", "food_id_1", MeasureType.GRAM.getUri(), 102),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 100),
                Arguments.of("Food Label 5", "food_id_4", MeasureType.GRAM, 100),
                Arguments.of("Food Label 6", "food_id_4", MeasureType.KILOGRAM, 100),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 300)
        );
        EdamamFoodIngredientRequestBuilder ingredientRequestBuilder = EdamamRepositorySingleton.getInstance().getIngredientRequestBuilder();
        addMeasureThroughArguments(arguments, ingredientRequestBuilder);


        List<IngredientRequestBody> ingredientRequestBodies = ingredientRequestBuilder.build();
        assertFalse(ingredientRequestBodies.isEmpty());
        /*
         * Reset all requests
         * */
        EdamamRepositorySingleton.getInstance().reset();
        ingredientRequestBodies = ingredientRequestBuilder.build();
        // Re add all measures
        assertTrue(ingredientRequestBodies.isEmpty());
        addMeasureThroughArguments(arguments, ingredientRequestBuilder);

        ingredientRequestBodies = ingredientRequestBuilder.build();
        assertFalse(ingredientRequestBodies.isEmpty());

        /*Submit*/
        for (int i = 0; i < ingredientRequestBodies.size(); i++) {
            IngredientRequestBody body = ingredientRequestBodies.get(0);
            makeSuccessfulNutrientsRequest(nutrientsResponseCallbackListener, body);
            verify(nutrientsResponseCallbackListener, times(i + 1)).onSuccess(disposableCaptor.capture());
            NutrientsResponse nutrientsResponse = (NutrientsResponse) disposableCaptor.getValue();
            assertNotNull(nutrientsResponse);
        }
    }

    public static Stream<Arguments> uc002TestUserSelectIncorrectMeasuresResources() {
        return Stream.of(
                Arguments.of("Food Label 1", "food_id_1", MeasureType.GRAM, -1),
                Arguments.of("Food Label 2", "food_id_2", null, 4),
                Arguments.of("Food Label 2", "food_id_2", "", 30),
                Arguments.of("Food Label 3", "", MeasureType.MILLILITER.getUri(), 0),
                Arguments.of("", "", MeasureType.FLUID_OUNCE, 10),
                Arguments.of(null, "food_id_2", MeasureType.FLUID_OUNCE, 0),
                Arguments.of("Food Label 3", null, MeasureType.FLUID_OUNCE, 0),
                Arguments.of(null, null, null, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("uc002TestUserSelectIncorrectMeasuresResources")
    public void uc002TestUserSelectIncorrectMeasures(String label, String foodId,
                                                     Object measureType, int amount) {
        EdamamFoodIngredientRequestBuilder ingredientRequestBuilder = EdamamRepositorySingleton.getInstance().getIngredientRequestBuilder();
        if (measureType instanceof MeasureType) {
            assertThrows(IllegalArgumentException.class, () -> ingredientRequestBuilder.addMeasure(label,
                    foodId, (MeasureType) measureType, amount));
        } else {
            assertThrows(IllegalArgumentException.class, () -> ingredientRequestBuilder.addMeasure(label,
                    foodId, (String) measureType, amount));
        }

        // Confirm we have gotten the right data
        List<IngredientRequestBody> ingredientRequestBodies = ingredientRequestBuilder.build();
        assertEquals(ingredientRequestBodies.size(), 0);

        Map<String, IngredientRequestWrapper> requestWrapperMap = ingredientRequestBuilder.getIngredientRequestMap();
        assertEquals(requestWrapperMap.size(), 0);
    }

    /*
     * UC003: User submit ingredients & get the Nutrient Information for those ingredients
     *   1. Nutrient information will show on the screen once the user submitted 1 or several nutrients
     *   2. User continues to search and adds ingredients after they have submitted the ingredients previously
     *   3. User can choose to add multiple measures and as much as they want
     * */
    @Test
    public void uc003TestUserChoosingMeasuresAfterSubmission() {
        /*
         * Assume that adding measure before making sending call is correct,
         * we try to add new measures and validate whether we still have the same query
         * */
        List<Arguments> arguments = List.of(
                Arguments.of("Food Label 1", "food_id_1", MeasureType.GRAM.getUri(), 10),
                Arguments.of("Food Label 2", "food_id_2", MeasureType.KILOGRAM, 20),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 20),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 100),
                Arguments.of("Food Label 5", "food_id_4", MeasureType.GRAM, 100),
                Arguments.of("Food Label 6", "food_id_4", MeasureType.KILOGRAM, 100),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 300)
        );
        EdamamFoodIngredientRequestBuilder ingredientRequestBuilder = EdamamRepositorySingleton.getInstance().getIngredientRequestBuilder();
        addMeasuresToIngredientRequest(arguments, ingredientRequestBuilder);

        List<IngredientRequestBody> ingredientRequestBodies = ingredientRequestBuilder.build();
        for (int i = 0; i < ingredientRequestBodies.size(); i++) {
            IngredientRequestBody body = ingredientRequestBodies.get(0);
            makeSuccessfulNutrientsRequest(nutrientsResponseCallbackListener, body);
            verify(nutrientsResponseCallbackListener, times(i + 1)).onSuccess(disposableCaptor.capture());
            NutrientsResponse nutrientsResponse = (NutrientsResponse) disposableCaptor.getValue();
            assertNotNull(nutrientsResponse);
        }
        int prevIngrRequestSize = ingredientRequestBodies.size();
        arguments = List.of(
                Arguments.of("Food Label 7", "food_id_7", MeasureType.GRAM.getUri(), 100),
                Arguments.of("Food Label 2", "food_id_2", MeasureType.KILOGRAM, 90),
                Arguments.of("Food Label 3", "food_id_3", MeasureType.CUP, 40)
        );
        addMeasuresToIngredientRequest(arguments, ingredientRequestBuilder);
        ingredientRequestBodies = ingredientRequestBuilder.build();
        assertEquals(prevIngrRequestSize + 1, ingredientRequestBodies.size());
        for (Arguments arg : arguments) {
            String foodId = (String) arg.get()[1];
            Object measureType = arg.get()[2];
            int amount = (int) (arg.get()[3]);
            String uri;
            if (measureType instanceof MeasureType) {
                uri = ((MeasureType) measureType).getUri();
            } else {
                uri = measureType.toString();
            }
            validateIngredientMeasureRequests(ingredientRequestBuilder, ingredientRequestBodies,
                    foodId, uri, amount);
        }
    }

    /*
     * UC004: From the nutrient info, the user attempts to send the results to pastebin
     * */
    @Test
    public void uc004TestUserSendingPastebinMessage() {
        NutrientsResponse nutrientsResponse = FoodEdamamDummyData.getNutrientsSampleData();
        assertNotNull(nutrientsResponse);
        makeSuccessfulPastebinRequest(pastebinResponseCallbackListener,
                nutrientsResponse.flatten());
        verify(pastebinResponseCallbackListener, times(1)).onSuccess(disposableCaptor.capture());
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
            ResponseCallbackListener<SubmissionParentResponse> listener
    ) {
        RedditPostQueryBuilder queryBuilder = RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder();
        RedditRepositorySingleton.getInstance().submitPost(queryBuilder.build(), listener);
    }

    public void makeSuccessfulRedditLoginRequest(
            ResponseCallbackListener<AuthorizationResponse> listener,
            String username,
            String password
    ) {
        doNothing().when(listener).onSuccess(disposableCaptor.capture());
        RedditRepositorySingleton.getInstance().getAccessToken(username, password, listener);
    }

    /*
     * UC005: From the nutrient info, the user attempts to send the results to reddit
     *   + user sends report while not logged in
     *   + user sends report logged in
     *   + user sends report after failed logging in
     *   + user sends report with invalid contents or subreddit
     *   + user sends post requests too fast
     *   + user sends report with both reddit and pastebin
     * */
    @Test
    public void uc005TestUserSendingRedditMessageNotLoggedIn() {
        NutrientsResponse nutrientsResponse = FoodEdamamDummyData.getNutrientsSampleData();
        assertNotNull(nutrientsResponse);

        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit("username");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle("Some Title");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(nutrientsResponse.flatten());

        /*
         * For reddit errors related to values will always be successful
         * */
        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        /*
         * Success must be called
         * */
        verify(submissionResponseCallbackListener, times(1)).onCallbackError((Throwable) disposableCaptor.capture());
        assertInstanceOf(IllegalStateException.class, disposableCaptor.getValue());
        // Ensure we get the error message
        assertEquals(submissionResponseCallbackListener.getErrorResponse().getMessage(),
                "An error has occurred " + ((Throwable) disposableCaptor.getValue()).getMessage());
    }

    @Test
    public void uc005TestUserSendingRedditMessageLoggedIn() {
        NutrientsResponse nutrientsResponse = FoodEdamamDummyData.getNutrientsSampleData();
        assertNotNull(nutrientsResponse);

        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit("username");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle("Some Title");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(nutrientsResponse.flatten());
        /*
         * Login to Reddit
         * */
        makeSuccessfulRedditLoginRequest(authorizationResponseCallbackListener,
                 "sample_user", "sample_password");
        /*
         * Confirm if the result was successful
         * */
        verify(authorizationResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());
        AuthorizationResponse result = (AuthorizationResponse) disposableCaptor.getValue();
        assertNotNull(result);
        assertEquals(RedditAccessState.LOGGED_IN, RedditRepositorySingleton.getInstance().getRedditAccessState());

        /*
         * Send a reddit message to
         * */
        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());
        SubmissionParentResponse resultParentSubmission = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultParentSubmission);
        SubmissionResponse resultSubmission = resultParentSubmission.getSubmissionResponse();
        assertNotNull(resultSubmission);
        assertNotNull(resultSubmission.getData());
    }

    @Test
    public void uc005TestUserSendingRedditMessageWithInvalidPostDetails() {
        /*
         * Login to Reddit
         * */
        makeSuccessfulRedditLoginRequest(authorizationResponseCallbackListener,
                 "sample_user", "sample_password");
        /*
         * Confirm if the result was successful
         * */
        verify(authorizationResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());
        AuthorizationResponse result = (AuthorizationResponse) disposableCaptor.getValue();
        assertNotNull(result);

        NutrientsResponse nutrientsResponse = FoodEdamamDummyData.getNutrientsSampleData();
        assertNotNull(nutrientsResponse);
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit(null);
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle("Some Title");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(nutrientsResponse.flatten());

        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());
        SubmissionParentResponse resultPostResult = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultPostResult);

        /*
         * Submit again but with no titles
         * */
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().reset();
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit("username");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle(null);
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(nutrientsResponse.flatten());

        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(2)).onSuccess(disposableCaptor.capture());

        resultPostResult = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultPostResult);

        /*
         * Submit again but with no titles
         * */
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().reset();
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit("username");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle("Valid Title");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(null);

        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(3)).onSuccess(disposableCaptor.capture());

        resultPostResult = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultPostResult);
    }

    @Test
    public void uc005TestUserSendingRedditMessageTooFast() {
        NutrientsResponse nutrientsResponse = FoodEdamamDummyData.getNutrientsSampleData();
        assertNotNull(nutrientsResponse);

        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit("username");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle("Some Title");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(nutrientsResponse.flatten());
        /*
         * Login to Reddit
         * */
        makeSuccessfulRedditLoginRequest(authorizationResponseCallbackListener,
                "sample_user", "sample_password");
        /*
         * Confirm if the result was successful
         * */
        verify(authorizationResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());

        /*
         * Send a reddit message to
         * */
        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());
        SubmissionParentResponse resultParentSubmission = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultParentSubmission);
        SubmissionResponse resultSubmission = resultParentSubmission.getSubmissionResponse();
        assertNotNull(resultSubmission);
        assertNotNull(resultSubmission.getData());

        /*
         * Send a reddit message again
         * */
        SubmissionParentResponse expectedErrorResponse = FoodEdamamDummyData
                .parseSubmissionParentResponse(
                        FoodEdamamDummyData.getErrorResponsePostingTooFast()
                );
        assertNotNull(expectedErrorResponse);
        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(2)).onSuccess(disposableCaptor.capture());
        resultParentSubmission = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultParentSubmission);
    }

    @Test
    public void uc005TestUserSendingRedditAndPastebinMessages() {
        NutrientsResponse nutrientsResponse = FoodEdamamDummyData.getNutrientsSampleData();
        assertNotNull(nutrientsResponse);

        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addUserProfileSubReddit("username");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostTittle("Some Title");
        RedditRepositorySingleton.getInstance().getRedditPostQueryBuilder().addPostBodyText(nutrientsResponse.flatten());
        makeSuccessfulRedditLoginRequest(authorizationResponseCallbackListener, "username", "password");
        assertNotNull(disposableCaptor.getValue());
        verify(authorizationResponseCallbackListener, times(1)).onSuccess(disposableCaptor.capture());
        makeSuccessfulPastebinRequest(pastebinResponseCallbackListener,
                nutrientsResponse.flatten());
        verify(pastebinResponseCallbackListener, times(1)).onSuccess(disposableCaptor.capture());

        makeSuccessfulRedditPostRequest(
                this.submissionResponseCallbackListener
        );
        verify(submissionResponseCallbackListener,
                times(1)).onSuccess(disposableCaptor.capture());
        SubmissionParentResponse resultParentSubmission = (SubmissionParentResponse) disposableCaptor.getValue();
        assertNotNull(resultParentSubmission);
    }
}
