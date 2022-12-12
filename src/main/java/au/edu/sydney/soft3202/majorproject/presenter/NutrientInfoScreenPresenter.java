package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.NutrientInfoScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditPostQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionResponse;
import au.edu.sydney.soft3202.majorproject.model.general.FlattenSerializable;
import au.edu.sydney.soft3202.majorproject.model.thread.ThreadPoolSingleton;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

import java.util.Objects;

public class NutrientInfoScreenPresenter {
  private static final Logger LOGGER = LogManager.getLogger(NutrientInfoScreenPresenter.class);
  private final NutrientInfoScreenController nutrientInfoScreenController;
  private final PastebinRepository pastebinRepository;
  private final RedditRepository redditRepository;

  public NutrientInfoScreenPresenter(NutrientInfoScreenController nutrientInfoScreenController) {
    this.nutrientInfoScreenController = nutrientInfoScreenController;
    this.pastebinRepository = PastebinRepositorySingleton.getInstance();
    this.redditRepository = RedditRepositorySingleton.getInstance();
  }

  /**
   * Send the report to Pastebin by calling the api and then call to the controller to update the UI
   *
   * @param flattenSerializable Data we need to send
   */
  public void pastebinSendReport(FlattenSerializable flattenSerializable) {
    LOGGER.debug("Preparing to data to send to Pastebin");
    ResponseCallbackListener<String> listener = getReportPastebinResponseCallbackListener();
    PastebinApiQueryBuilder pastebinApiQueryBuilder =
        pastebinRepository.getPastebinApiQueryBuilder();
    pastebinApiQueryBuilder.setPasteContent(flattenSerializable.flatten());
    ThreadPoolSingleton.getInstance()
        .execute(() -> pastebinRepository.submitPaste(pastebinApiQueryBuilder.build(), listener));
  }

  /**
   * Send the report to Reddit by calling the api and then call to the controller to update the UI
   *
   * @param postTitle The title of the report
   * @param flattenSerializable Data we need to send
   */
  public void redditSendReport(String postTitle, FlattenSerializable flattenSerializable) {
    LOGGER.debug("Preparing to data to send to Pastebin");
    ResponseCallbackListener<SubmissionParentResponse> listener =
        getReportRedditResponseCallbackListener();
    RedditPostQueryBuilder redditPostQueryBuilder = redditRepository.getRedditPostQueryBuilder();
    redditPostQueryBuilder.addPostTittle(postTitle);
    redditPostQueryBuilder.addPostBodyText(flattenSerializable.flatten().replace("\n", "\n\n"));
    ThreadPoolSingleton.getInstance()
        .execute(() -> redditRepository.submitPost(redditPostQueryBuilder.build(), listener));
  }

  /**
   * Get the listener for the Pastebin Api. This listener is also responsible for telling the
   * controller to update the UI on response
   *
   * @return the listener
   */
  private ResponseCallbackListener<String> getReportPastebinResponseCallbackListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        LOGGER.debug("Sent report to Pastebin successfully");
        Platform.runLater(
            () -> {
              nutrientInfoScreenController.loadSuccessfulPastebinSend(responseBody);
            });
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error(t);
        String cause = getErrorResponse().getError();
        String reason = "Pastebin Error";
        Platform.runLater(
            () -> {
              nutrientInfoScreenController.showAndWaitOnPastebinError(cause, reason);
            });
      }

      @Override
      public void onCallbackError(Response<?> response) {
        String reason;
        try {
          reason = Objects.requireNonNull(response.errorBody()).string();
        } catch (Exception e) {
          reason = "Error while sending report. Please try again later!";
        }
        LOGGER.error(reason);
        String finalReason = reason;
        String cause = "Error occurred while trying to parse pastebin";
        Platform.runLater(
            () -> {
              nutrientInfoScreenController.showAndWaitOnPastebinError(cause, finalReason);
            });
      }
    };
  }

  /**
   * Get the listener for the Reddit Api. This listener is also responsible for telling the
   * controller to update the UI on response
   *
   * @return the listener
   */
  private ResponseCallbackListener<SubmissionParentResponse>
      getReportRedditResponseCallbackListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        LOGGER.debug("Sent report to Pastebin successfully");
        SubmissionParentResponse submissionParentResponse = (SubmissionParentResponse) responseBody;
        SubmissionResponse submissionResponse = submissionParentResponse.getSubmissionResponse();
        if (submissionResponse.getErrors() != null) {
          String errorString = submissionResponse.getErrorString();
          if (!errorString.isEmpty()) {
            LOGGER.debug("Report failed with error " + errorString);
            onCallbackError(new IllegalStateException(errorString));
            return;
          }
        }
        Platform.runLater(
            () -> nutrientInfoScreenController.loadSuccessfulRedditSend(submissionParentResponse));
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error(t);
        String cause = getErrorResponse().getError();
        String reason = getErrorResponse().getMessage();
        Platform.runLater(
            () -> nutrientInfoScreenController.showAndWaitOnRedditError(cause, reason));
      }

      @Override
      public void onCallbackError(Response<?> response) {
        String reason;
        try {
          reason = Objects.requireNonNull(response.errorBody()).string();
          if (reason.length() > 100) {
            throw new IllegalStateException("Response is too long");
          }
        } catch (Exception e) {
          reason =
              "Error while sending report. Please try again later!\nTip: Make sure you have logged in to reddit!";
        }
        LOGGER.error(reason);
        String finalReason = reason;
        String cause = "Error while sending report. Please try again later!";
        Platform.runLater(
            () -> nutrientInfoScreenController.showAndWaitOnRedditError(cause, finalReason));
      }
    };
  }

  /**
  * Call to the controller to load the stacked bar chart
  * */
  public void loadStackedBarChart() {
    nutrientInfoScreenController.loadStackedBarChart();
  }
}
