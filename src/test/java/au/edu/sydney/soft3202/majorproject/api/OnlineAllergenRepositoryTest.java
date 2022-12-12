package au.edu.sydney.soft3202.majorproject.api;

import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamFoodApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen.AllergenIngredientObserver;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/** This test covers how the extension of adding allergen affects the add ingredient */
public class OnlineAllergenRepositoryTest extends BaseOnlineRepositoryTest {
  private AllergenIngredientObserver allergenIngredientObserver;

  @BeforeEach
  public void setup() {
    super.setup();
    // Setup for allergen observer
    allergenIngredientObserver = Mockito.mock(AllergenIngredientObserver.class);
    Mockito.doNothing().when(allergenIngredientObserver).onMatchingAllergen();
    Mockito.doNothing().when(allergenIngredientObserver).onNotMatchingAllergen();
  }

  @AfterEach
  public void cleanUp() {
    super.cleanUp();
  }

  /*
   * UC006: Test Allergen
   * - User does not select any allergen
   * - User entered invalid allergen
   * - User entered correct allergen and then add the ingredient which does not match
   * - User entered correct allergen and then add the ingredient which does match
   * */

  /**
   * This test case considers the case where the user doesn't add anything and add ingredient. We
   * check if there is any matching with the allergen
   */
  @Test
  public void uc006TestUserDoesNotSelectAnyAllergen() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();

    assertNull(edamamRepository.getAllergen());
    String ingredientInput = "banana";
    // When call to allergen observer, it should do the following
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInput))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("The ingredient should not match with anything"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(ingredientInput, allergenIngredientObserver);
    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(0)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(1)).onNotMatchingAllergen();

    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(ingredientInput));
  }

  public static Stream<Arguments> uc006TestUserAddsInvalidAllergenResources() {
    return Stream.of(Arguments.of("     "), Arguments.of(""));
  }
  /**
   * This test case considers the case where the user adds invalid allergen to see if any allergen
   * is added
   */
  @ParameterizedTest
  @MethodSource("uc006TestUserAddsInvalidAllergenResources")
  public void uc006TestUserAddsInvalidAllergen(String allergen) {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    assertNull(edamamRepository.getAllergen());
    assertThrows(IllegalArgumentException.class, () -> edamamRepository.addAllergen(allergen));
    assertThrows(IllegalArgumentException.class, () -> edamamRepository.addAllergen(null));
    assertNull(edamamRepository.getAllergen());
  }

  /**
   * This test case considers the case where the user entered valid allergen and then select the
   * matching ingredient & user does not approve of adding
   */
  @Test
  public void uc006TestUserSelectsValidAllergenAndValidIngredientDoesNotApprove() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();
    String allergen = "chicken";
    assertNull(edamamRepository.getAllergen());
    // Add allergen
    edamamRepository.addAllergen(allergen);
    assertEquals(allergen, edamamRepository.getAllergen());

    // Add Ingredient
    String ingredientInput = "chickens";
    // When call to allergen observer, it should do the following
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInput))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(ingredientInput, allergenIngredientObserver);
    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(1)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(0)).onNotMatchingAllergen();

    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(null));
  }

  /**
   * This test case considers the case where the user entered valid allergen and then select the
   * matching ingredient & user does approve of adding
   */
  @Test
  public void uc006TestUserSelectsValidAllergenAndValidIngredientApprove() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();
    String allergen = "chicken";
    assertNull(edamamRepository.getAllergen());
    // Add allergen
    edamamRepository.addAllergen(allergen);
    assertEquals(allergen, edamamRepository.getAllergen());

    // Add Ingredient
    String ingredientInput = "chickens";
    // When call to allergen observer, it should do the following
    doAnswer(invocationOnMock -> fail("This should not be called"))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInput))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(ingredientInput, allergenIngredientObserver);
    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(1)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(0)).onNotMatchingAllergen();

    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(ingredientInput));
  }

  /**
   * This test case considers the case where the user entered valid allergen and then select the
   * matching & non-matching ingredients & user approves of adding sometimes
   */
  @Test
  public void uc006TestUserSelectsValidAllergenAndMixedIngredientMixedApprove() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();
    String allergen = "egg";
    assertNull(edamamRepository.getAllergen());
    // Add allergen
    edamamRepository.addAllergen(allergen);
    assertEquals(allergen, edamamRepository.getAllergen());

    // Add Ingredient
    List<String> ingredientInputs = List.of("eggshell", "orange", "eGGshake");
    // When call to allergen observer, it should do the following
    doAnswer(invocationOnMock -> fail("This ingredient should match with the allergen"))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(0)))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(0), allergenIngredientObserver);
    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(1)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(0)).onNotMatchingAllergen();

    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(ingredientInputs.get(0)));

    // Add different ingredient that does not match
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(1)))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("This ingredient should not match with the allergen"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(1), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(1)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(1)).onNotMatchingAllergen();
    String resultIngredients = ingredientInputs.get(0) + " and " + ingredientInputs.get(1);
    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(resultIngredients));

    // Add a matching ingredient again
    doAnswer(invocationOnMock -> fail("This ingredient should match with the allergen"))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(2)))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();
    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(2), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(2)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(1)).onNotMatchingAllergen();
    resultIngredients += " and " + ingredientInputs.get(2);
    assertThat(
        edamamFoodApiQueryBuilder.build().get("ingr").toLowerCase(),
        equalTo(resultIngredients.toLowerCase()));
  }

  /** This test case tests whether it is possible to change the allergen multiple times */
  @Test
  public void uc006TestUserChangesAllergenMultipleTimes() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    List<Arguments> arguments =
        List.of(
            Arguments.of("egg"),
            Arguments.of("cranberry"),
            Arguments.of("corn"),
            Arguments.of("sand witch"));
    for (Arguments argument : arguments) {
      String allergen = (String) argument.get()[0];
      edamamRepository.addAllergen(allergen);
      assertEquals(allergen, edamamRepository.getAllergen());
    }
    String finalAllergen = (String) arguments.get(arguments.size() - 1).get()[0];
    assertEquals(finalAllergen, edamamRepository.getAllergen());
    // Attempt to add invalid allergen
    assertThrows(IllegalArgumentException.class, () -> edamamRepository.addAllergen(""));
    assertEquals(finalAllergen, edamamRepository.getAllergen());
  }

  /** This test case tests when the allergen is valid but the ingredient is invalid */
  @Test
  public void uc006TestUserSelectsValidAllergenAndInvalidIngredient() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();
    String allergen = "egg";
    assertNull(edamamRepository.getAllergen());
    // Add allergen
    edamamRepository.addAllergen(allergen);
    assertEquals(allergen, edamamRepository.getAllergen());

    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(null))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("This ingredient should not match with the allergen"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    assertThrows(
        IllegalArgumentException.class,
        () -> edamamRepository.checkIngredientSelectedAllergen(null, allergenIngredientObserver));

    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(0)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(1)).onNotMatchingAllergen();

    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient("     "))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("This ingredient should not match with the allergen"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            edamamRepository.checkIngredientSelectedAllergen("     ", allergenIngredientObserver));

    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(0)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(2)).onNotMatchingAllergen();
  }

  /**
   * This test case considers the case where the user entered valid allergen and then select the
   * matching & non-matching ingredients & then reset and add again with the same allergen
   */
  @Test
  public void uc006TestUserSelectsValidAllergenAndMixedIngredientAndResetIngredient() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();
    String allergen = "egg";
    assertNull(edamamRepository.getAllergen());
    // Add allergen
    edamamRepository.addAllergen(allergen);
    assertEquals(allergen, edamamRepository.getAllergen());

    // Add Ingredient
    List<String> ingredientInputs = List.of("eggsssshell", "orange", "shake eGGshake", "bananass");
    // When call to allergen observer, it should do the following
    doAnswer(invocationOnMock -> fail("This ingredient should match with the allergen"))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(0)))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(0), allergenIngredientObserver);
    // The ingredient should not be matched with anything
    verify(allergenIngredientObserver, times(1)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(0)).onNotMatchingAllergen();

    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(ingredientInputs.get(0)));

    // Add different ingredient that does not match
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(1)))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("This ingredient should not match with the allergen"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(1), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(1)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(1)).onNotMatchingAllergen();
    String resultIngredients = ingredientInputs.get(0) + " and " + ingredientInputs.get(1);
    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(resultIngredients));

    // Add a matching ingredient again
    doAnswer(invocationOnMock -> fail("This ingredient should match with the allergen"))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(2)))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();
    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(2), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(2)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(1)).onNotMatchingAllergen();
    resultIngredients += " and " + ingredientInputs.get(2);
    assertThat(
        edamamFoodApiQueryBuilder.build().get("ingr").toLowerCase(),
        equalTo(resultIngredients.toLowerCase()));

    // Add non-matching ingredient
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(3)))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("This ingredient should not match with the allergen"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(3), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(2)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(2)).onNotMatchingAllergen();
    resultIngredients += " and " + ingredientInputs.get(3);
    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(resultIngredients));

    edamamFoodApiQueryBuilder.resetIngredients();
    assertNull(edamamFoodApiQueryBuilder.build().get("ingr"));

    // Add different ingredient that does not match
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(1)))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> fail("This ingredient should not match with the allergen"))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();

    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(1), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(2)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(3)).onNotMatchingAllergen();
    resultIngredients = ingredientInputs.get(1);
    assertThat(edamamFoodApiQueryBuilder.build().get("ingr"), equalTo(resultIngredients));

    // Add a matching ingredient again
    doAnswer(invocationOnMock -> fail("This ingredient should match with the allergen"))
        .when(allergenIngredientObserver)
        .onNotMatchingAllergen();
    doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredientInputs.get(2)))
        .when(allergenIngredientObserver)
        .onMatchingAllergen();
    edamamRepository.checkIngredientSelectedAllergen(
        ingredientInputs.get(2), allergenIngredientObserver);
    verify(allergenIngredientObserver, times(3)).onMatchingAllergen();
    verify(allergenIngredientObserver, times(3)).onNotMatchingAllergen();
    resultIngredients += " and " + ingredientInputs.get(2);
    assertThat(
        edamamFoodApiQueryBuilder.build().get("ingr").toLowerCase(),
        equalTo(resultIngredients.toLowerCase()));

    // Make sure the allergen is the same and unchanged
    assertEquals(allergen, edamamRepository.getAllergen());
  }

  /**
   * This test case considers the case where the user entered valid allergen and then select the
   * matching & non-matching ingredients & submit the query to the API successfully
   */
  @Test
  public void uc006TestUserSelectsValidAllergenAndIngredientAndSubmitToApi() {
    EdamamRepository edamamRepository = EdamamRepositorySingleton.getInstance();
    EdamamFoodApiQueryBuilder edamamFoodApiQueryBuilder =
        EdamamRepositorySingleton.getInstance().getQueryBuilder();
    String allergen = "EGG";
    assertNull(edamamRepository.getAllergen());
    // Add allergen
    edamamRepository.addAllergen(allergen);

    assertEquals(allergen, edamamRepository.getAllergen());
    List<String> ingredientInputs =
        List.of("eggsssshell", "Dragon eggs", "orange", "shake eGGshake", "bananass");
    StringBuilder resultStringBuilder = new StringBuilder();
    for (String ingredient : ingredientInputs) {
      doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredient))
          .when(allergenIngredientObserver)
          .onNotMatchingAllergen();
      doAnswer(invocationOnMock -> edamamFoodApiQueryBuilder.addIngredient(ingredient))
          .when(allergenIngredientObserver)
          .onMatchingAllergen();
      edamamRepository.checkIngredientSelectedAllergen(ingredient, allergenIngredientObserver);
      if (!resultStringBuilder.isEmpty()) {
        resultStringBuilder.append(" and ");
      }
      resultStringBuilder.append(ingredient);
    }

    Map<String, String> edamamQuery = edamamFoodApiQueryBuilder.build();
    assertThat(edamamQuery.size(), equalTo(3));
    assertThat(edamamQuery.get("ingr"), equalTo(resultStringBuilder.toString()));

    validateIngredientQuery(
        foodParserResponseCallbackListener,
        foodParserResponseCall,
        foodParserResponseResult,
        edamamQuery);
    FoodParserResponse foodParserResponse = (FoodParserResponse) disposableCaptor.getValue();

    validateFoodParserResponse(foodParserResponse);
  }
}
