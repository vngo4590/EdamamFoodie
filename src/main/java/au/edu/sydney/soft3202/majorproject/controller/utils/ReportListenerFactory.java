package au.edu.sydney.soft3202.majorproject.controller.utils;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionResponse;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

import java.util.List;
import java.util.Objects;

/** A central class that provides listeners for posting reports to either pastebin or reddit */
public class ReportListenerFactory {
  private static final Logger LOGGER = LogManager.getLogger(ReportListenerFactory.class);

  public static ResponseCallbackListener<SubmissionParentResponse>
      createReportRedditResponseCallbackListener(
          Pane processIndicatorContainer, Button toDisableButton) {
    return new ResponseCallbackListener<>() {
      @Override
      public void onSuccess(Object responseBody) {
        Platform.runLater(
            () -> {
              LOGGER.debug("Validating data content");
              ScreenLoaderUtils.setDisableButtons(List.of(toDisableButton), false);
              ScreenLoaderUtils.removeProcessIndicatorFromPane(processIndicatorContainer);
              SubmissionParentResponse submissionParentResponse =
                  (SubmissionParentResponse) responseBody;
              SubmissionResponse submissionResponse =
                  submissionParentResponse.getSubmissionResponse();
              if (submissionResponse.getErrors() != null) {
                String errorString = submissionResponse.getErrorString();
                if (!errorString.isEmpty()) {
                  LOGGER.debug("Report failed with error " + errorString);
                  onCallbackError(new IllegalStateException(errorString));
                  return;
                }
              }
              LOGGER.debug("Successfully sent report to reddit!");
              VBox vBox =
                  ScreenLoaderUtils.createReportPanel(submissionResponse.getData().getUrl());
              AlertFactory.createInfoAlertPane(vBox).showAndWait();
            });
      }

      @Override
      public void onCallbackError(Throwable t) {
        super.onCallbackError(t);
        LOGGER.error(t);
        Platform.runLater(
            () -> {
              ScreenLoaderUtils.removeProcessIndicatorFromPane(processIndicatorContainer);
              ScreenLoaderUtils.setDisableButtons(List.of(toDisableButton), false);
              AlertFactory.createErrorAlert(
                      getErrorResponse().getError(), getErrorResponse().getMessage())
                  .showAndWait();
            });
      }

      @Override
      public void onCallbackError(Response<?> response) {
        String cause;
        try {
          cause = Objects.requireNonNull(response.errorBody()).string();
        } catch (Exception e) {
          cause =
              "Error while sending report. Please try again later!\nTip: Make sure you have logged in to reddit!";
        }
        String finalCause = "Error while sending report. Please try again later!";
        LOGGER.error(cause);
        Platform.runLater(
            () -> {
              ScreenLoaderUtils.removeProcessIndicatorFromPane(processIndicatorContainer);
              ScreenLoaderUtils.setDisableButtons(List.of(toDisableButton), false);
              AlertFactory.createErrorAlert(
                      "Error occurred while trying to parse reddit", finalCause)
                  .showAndWait();
            });
      }
    };
  }
}
