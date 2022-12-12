package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.ChosenIngredientsController;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChosenIngredientsPresenter {
  private final EdamamRepository edamamRepository;
  private final ChosenIngredientsController chosenIngredientsController;
  private static final Logger LOGGER = LogManager.getLogger(ChosenIngredientsPresenter.class);

  public ChosenIngredientsPresenter(ChosenIngredientsController chosenIngredientsController) {
    this.edamamRepository = EdamamRepositorySingleton.getInstance();
    this.chosenIngredientsController = chosenIngredientsController;
  }

  /** Reset all ingredients in the query */
  public void resetIngredient() {
    this.edamamRepository.getIngredientRequestBuilder().reset();
    LOGGER.debug("Ingredient requests have been reset");
    this.chosenIngredientsController.resetIngredients();
  }
}
