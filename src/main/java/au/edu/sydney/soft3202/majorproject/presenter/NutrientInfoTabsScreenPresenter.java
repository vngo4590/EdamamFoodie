package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.NutrientInfoTabsScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditPostQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionResponse;
import au.edu.sydney.soft3202.majorproject.model.general.FlattenSerializable;
import au.edu.sydney.soft3202.majorproject.model.thread.ThreadPoolSingleton;
import javafx.application.Platform;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NutrientInfoTabsScreenPresenter {
  private final RedditRepository redditRepository;
  private final PastebinRepository pastebinRepository;
  private static final Logger LOGGER = LogManager.getLogger(NutrientInfoTabsScreenPresenter.class);
  private final NutrientInfoTabsScreenController nutrientInfoTabsScreenController;

  public NutrientInfoTabsScreenPresenter(
      NutrientInfoTabsScreenController nutrientInfoTabsScreenController) {
    this.nutrientInfoTabsScreenController = nutrientInfoTabsScreenController;
    this.redditRepository = RedditRepositorySingleton.getInstance();
    this.pastebinRepository = PastebinRepositorySingleton.getInstance();
  }

  /**
   * Send all the reports to Pastebin by calling the api and then call to the controller to update
   * the UI
   *
   * @param reportSerializables Data we need to send
   */
  public void pastebinSendReport(List<FlattenSerializable> reportSerializables) {
    ResponseCallbackListener<String> listener = createReportPastebinResponseCallbackListener();
    LOGGER.debug("Preparing to data to send to Pastebin");
    PastebinApiQueryBuilder pastebinApiQueryBuilder =
        pastebinRepository.getPastebinApiQueryBuilder();
    pastebinApiQueryBuilder.setPasteContent(getReportContent(reportSerializables));
    ThreadPoolSingleton.getInstance()
        .execute(() -> pastebinRepository.submitPaste(pastebinApiQueryBuilder.build(), listener));
  }

  /**
   * Send all the reports to Reddit by calling the api and then call to the controller to update the
   * UI
   *
   * @param postTitle The title of the report
   * @param reportSerializables Data we need to send
   */
  public void redditSendReport(String postTitle, List<FlattenSerializable> reportSerializables) {
    LOGGER.debug("Preparing to data to send to Pastebin");
    ResponseCallbackListener<SubmissionParentResponse> listener =
        createReportRedditResponseCallbackListener();
    RedditPostQueryBuilder redditPostQueryBuilder = redditRepository.getRedditPostQueryBuilder();
    redditPostQueryBuilder.addPostTittle(postTitle);
    redditPostQueryBuilder.addPostBodyText(
        getReportContent(reportSerializables).replace("\n", "\n\n"));
    ThreadPoolSingleton.getInstance()
        .execute(() -> redditRepository.submitPost(redditPostQueryBuilder.build(), listener));
  }

  /**
   * Parse the list of data to the string of report
   *
   * @param reportSerializables Data we need to send
   * @return The report String
   */
  private String getReportContent(List<FlattenSerializable> reportSerializables) {
    StringBuilder reportContent = new StringBuilder();
    for (FlattenSerializable f : reportSerializables) {
      reportContent.append(f.flatten()).append("\n");
    }
    return reportContent.toString();
  }

  /**
   * Get the listener for the Pastebin Api. This listener is also responsible for telling the
   * controller to update the UI on response
   *
   * @return the listener
   */
  public ResponseCallbackListener<String> createReportPastebinResponseCallbackListener() {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        LOGGER.debug("Sent report to Pastebin successfully");
        Platform.runLater(
            () -> nutrientInfoTabsScreenController.loadSuccessfulPastebinSend(responseBody));
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error(t);
        String cause = getErrorResponse().getError();
        String reason = "Pastebin Error";
        Platform.runLater(
            () -> nutrientInfoTabsScreenController.showAndWaitOnPastebinError(cause, reason));
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
            () -> nutrientInfoTabsScreenController.showAndWaitOnPastebinError(cause, finalReason));
      }
    };
  }

  /**
   * Get the listener for the Reddit Api. This listener is also responsible for telling the
   * controller to update the UI on response
   *
   * @return the listener
   */
  public ResponseCallbackListener<SubmissionParentResponse>
      createReportRedditResponseCallbackListener() {
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
            () ->
                nutrientInfoTabsScreenController.loadSuccessfulRedditSend(
                    submissionParentResponse));
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error(t);
        String cause = getErrorResponse().getError();
        String reason = getErrorResponse().getMessage();
        Platform.runLater(
            () -> nutrientInfoTabsScreenController.showAndWaitOnRedditError(cause, reason));
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
            () -> nutrientInfoTabsScreenController.showAndWaitOnRedditError(cause, finalReason));
      }
    };
  }

  /**
   * Call to thread to initiate a future thread call to load the data in the background and then
   * update the data on the UI thread
   *
   * @param input Nutrient data
   */
  public void loadScreenOnNutrientInfo(NutrientsResponse input) {
    Future<Node> futureNode =
        ThreadPoolSingleton.getInstance()
            .submit(() -> nutrientInfoTabsScreenController.loadScreenOnNutrientInfo(input));
    ThreadPoolSingleton.getInstance()
        .execute(
            () -> {
              try {
                Node node = futureNode.get();
                if (node != null) {
                  Platform.runLater(
                      () -> nutrientInfoTabsScreenController.loadContentToTab(input, node));
                }
              } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                LOGGER.error(e);
              }
            });
  }
}
