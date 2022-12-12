package au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen;
/**
* An observer that observes whether an ingredient matches with any allergen
* */
public interface AllergenIngredientObserver {
    /**
    * To be called when a given allergen matches with an ingredient
    * */
    void onMatchingAllergen();
    /**
     * To be called when a given allergen does not match with an ingredient
     * */
    void onNotMatchingAllergen();
}
