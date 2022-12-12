package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.MainScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainScreenPresenter {
  private final MainScreenController mainScreenController;
  private final EdamamRepository edamamRepository;
  private final RedditRepository redditRepository;
  private static final Logger LOGGER = LogManager.getLogger(MainScreenPresenter.class);

  public MainScreenPresenter(MainScreenController mainScreenController) {
    this.mainScreenController = mainScreenController;
    this.edamamRepository = EdamamRepositorySingleton.getInstance();
    this.redditRepository = RedditRepositorySingleton.getInstance();
  }

  /** Clear all cache and then call the controller to update the UI */
  public void clearCache() {
    LOGGER.debug("Clearing cache");
    this.edamamRepository.clearCache();
    mainScreenController.updateOnClearCache();
  }

  /** Clear all ingredients and then call the controller to update the UI */
  public void resetIngredientQuery() {
    LOGGER.debug("Resetting all ingredient queries");
    this.edamamRepository.getQueryBuilder().reset();
    mainScreenController.updateOnLoadingEnterIngredient();
  }

  /** Call to the controller to let them know whether the reddit current state is logged in */
  public void updateOnRedditStatus() {
    LOGGER.info("Reddit status is " + redditRepository.getRedditAccessState());
    if (redditRepository.getRedditAccessState().isLoggedIn()) {
      mainScreenController.updateOnRedditLoggedIn();
    } else {
      mainScreenController.updateOnRedditNotLoggedIn();
    }
  }

  /** Call to the controller to pause the music */
  public void pauseMusic() {
    Platform.runLater(mainScreenController::pauseMusic);
  }

  /** Call to the controller to play the music */
  public void playMusic() {
    Platform.runLater(mainScreenController::playMusic);
  }

  /** Call to the controller to load the Reddit login panel */
  public void loadRedditLoginPanel() {
    Platform.runLater(mainScreenController::loadRedditLoginPanel);
  }
}
