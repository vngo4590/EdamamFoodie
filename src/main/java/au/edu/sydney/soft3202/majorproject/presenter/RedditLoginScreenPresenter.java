package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.RedditLoginScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.thread.ThreadPoolSingleton;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

public class RedditLoginScreenPresenter {
  private final RedditRepository redditRepository;
  private final RedditLoginScreenController redditLoginScreenController;

  private static final Logger LOGGER = LogManager.getLogger(RedditLoginScreenPresenter.class);

  public RedditLoginScreenPresenter(RedditLoginScreenController redditLoginScreenController) {
    this.redditLoginScreenController = redditLoginScreenController;
    this.redditRepository = RedditRepositorySingleton.getInstance();
  }

  /**
   * call to the Reddit api to login to the Reddit and the call to the controller to update the UI
   *
   * @param userName The username
   * @param password The password
   */
  public void loginToReddit(String userName, String password) {
    LOGGER.debug("Saving username and executing Reddit login");
    ResponseCallbackListener<AuthorizationResponse> listener =
        getAuthorizationResponseCallbackListener();
    // Set the user name for the next query
    redditRepository.getRedditPostQueryBuilder().addUserProfileSubReddit(userName);
    ThreadPoolSingleton.getInstance()
        .execute(() -> redditRepository.getAccessToken(userName, password, listener));
  }

  private ResponseCallbackListener<AuthorizationResponse>
      getAuthorizationResponseCallbackListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        AuthorizationResponse authorizationResponse = (AuthorizationResponse) responseBody;
        if (authorizationResponse == null || authorizationResponse.getAccessToken() == null) {
          LOGGER.debug("Logged in failed");
          if (authorizationResponse != null && authorizationResponse.getError() != null) {
            onCallbackError(new IllegalStateException(authorizationResponse.getError()));
          } else {
            onCallbackError(new IllegalStateException("Login unsuccessfully! Please try again!"));
          }
        } else {
          LOGGER.debug("Logged in successfully");
          Platform.runLater(redditLoginScreenController::updateRedditSuccessfulLogin);
        }
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        t.printStackTrace();
        LOGGER.error("An error has occurred while logging in " + getErrorResponse());
        Platform.runLater(
            () ->
                redditLoginScreenController.showAndWaitOnRedditError(
                    "Problem occurred using logging in", getErrorResponse().getMessage()));
      }

      @Override
      public void onCallbackError(Response<?> response) {
        super.onCallbackError(response);
        LOGGER.error("An error has occurred while logging in " + getErrorResponse());
        Platform.runLater(
            () ->
                redditLoginScreenController.showAndWaitOnRedditError(
                    getErrorResponse().getError(), getErrorResponse().getMessage()));
      }
    };
  }
}
