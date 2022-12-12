package au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen;

public interface AllergenWrapper {
    /**
     * Get the current allergen from the {@link AllergenIngredientBuilder}
     *
     * @return the allergen
     */
    String getAllergen();

    /**
     * Call to the {@link AllergenIngredientBuilder#addAllergen(String allergen)} to add the allergen.
     *
     * @param allergen The allergen
     * @throws IllegalArgumentException If the allergen is invalid. See {@link
     *     AllergenIngredientBuilder#addAllergen(String allergen)} for exception.
     */
    void addAllergen(String allergen) throws IllegalArgumentException;

    /**
     * Call to the {@link AllergenIngredientBuilder#isIngredientSelectedAllergen(String ingredient)}
     * to check if there is any matching allergen. <br>
     * This method will call {@link AllergenIngredientObserver#onMatchingAllergen()} if there is an
     * allergen. Else, it will call {@link AllergenIngredientObserver#onNotMatchingAllergen()} if
     * there is no matching allergen
     *
     * @param ingredient the possible ingredient
     * @param allergenIngredientObserver The observer of the allergen. It observes for any ingredient
     *     matching.
     */
    void checkIngredientSelectedAllergen(
            String ingredient, AllergenIngredientObserver allergenIngredientObserver);
}
