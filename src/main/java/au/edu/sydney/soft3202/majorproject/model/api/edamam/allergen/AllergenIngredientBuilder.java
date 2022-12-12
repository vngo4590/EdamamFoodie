package au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** A builder class that builds the allergen */
public class AllergenIngredientBuilder {
  /** The allergen */
  private String allergen;

  private static final Logger LOGGER = LogManager.getLogger(AllergenIngredientBuilder.class);

  public AllergenIngredientBuilder() {
    this.allergen = null;
  }

  /**
   * This method sets the allergen
   *
   * @param allergen The allergen
   * @return The instance of the builder
   * @throws IllegalArgumentException If the allergen is null, empty or blank
   */
  public AllergenIngredientBuilder addAllergen(String allergen) throws IllegalArgumentException {
    boolean isNotValidAllergen = allergen == null || allergen.isEmpty() || allergen.isBlank();
    if (isNotValidAllergen) {
      throw new IllegalArgumentException(
          "Input Allergen is Invalid. Allergen cannot be null, empty or blank!");
    }
    this.allergen = allergen.trim();
    return this;
  }

  /**
   * This method checks if an ingredient input is a selected allergen
   *
   * @param ingredient The sample ingredient.
   * @return True if there is a matching, False otherwise.
   */
  public boolean isIngredientSelectedAllergen(String ingredient) {
    if (ingredient == null || allergen == null) {
      LOGGER.debug("Ingredient or Allergen is invalid");
      return false;
    }
    String modifiedIngredient = ingredient.toLowerCase().trim();
    String modifiedAllergen = allergen.toLowerCase().trim();

    if (modifiedIngredient.contains(modifiedAllergen)) {
      LOGGER.debug(
          "There is a matching: "
              + String.format("Allergen %s matches with ingredient %s.", allergen, ingredient));
      return true;
    }
    LOGGER.debug(
        "There is no matching: "
            + String.format(
                "Allergen %s does not match with ingredient %s.", allergen, ingredient));
    return false;
  }

  /**
   * Build the allergen
   *
   * @return The current allergen
   */
  public String build() {
    return allergen;
  }
}
