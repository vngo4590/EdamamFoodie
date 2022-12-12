package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.IngredientParsedItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientInfo;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionResponse;
import au.edu.sydney.soft3202.majorproject.presenter.NutrientInfoScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class NutrientInfoScreenController implements ModelController<NutrientsResponse> {
  @FXML protected Button sendRedditReportButton;
  @FXML protected Label nutrientInfoLabel;
  @FXML protected Button sendPastebinReportButton;
  @FXML protected Button viewStackedBarButton;
  @FXML protected VBox nutrientInfoIngredientContainer;
  @FXML protected VBox nutrientInfoScreen;
  @FXML protected VBox totalDailyContainer;
  @FXML protected VBox totalNutrientsContainer;
  @FXML protected Label foodCaloriesText;
  @FXML protected Label foodTotalWeightText;
  @FXML protected Label foodCautionsText;
  @FXML protected Label foodHealthLabelsText;
  @FXML protected Label foodDietLabelsText;

  private NutrientsResponse nutrientsResponse;
  private ActionEvent currentEvent;
  private NutrientInfoScreenPresenter nutrientInfoScreenPresenter;
  private static final Logger LOGGER = LogManager.getLogger(NutrientInfoScreenController.class);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nutrientInfoScreenPresenter = new NutrientInfoScreenPresenter(this);
  }

  @Override
  public void loadDetails(NutrientsResponse input) {
    this.nutrientsResponse = input;
    IngredientParsedItem parsedItem = input.getIngredients().get(0);
    nutrientInfoLabel.setText(
        String.format("[%s] %s", parsedItem.getMeasure(), parsedItem.getFood()));
    foodCaloriesText.setText(String.valueOf(input.getCalories()));
    foodTotalWeightText.setText(String.valueOf(input.getTotalWeight()));
    foodCautionsText.setText(ScreenLoaderUtils.formatListStringToString(input.getCautions()));
    foodDietLabelsText.setText(ScreenLoaderUtils.formatListStringToString(input.getDietLabels()));
    foodHealthLabelsText.setText(
        ScreenLoaderUtils.formatListStringToString(input.getHealthLabels()));
    loadNutrientInfo(input.getTotalDaily(), totalDailyContainer);
    loadNutrientInfo(input.getTotalNutrients(), totalNutrientsContainer);
    loadIngredientInfo(input.getIngredients(), nutrientInfoIngredientContainer);
  }

  /** Load the nutrient information on a panel */
  private void loadNutrientInfo(Map<String, NutrientInfo> input, Pane container) {
    for (Map.Entry<String, NutrientInfo> entry : input.entrySet()) {
      try {
        FXMLLoader loader =
            ScreenLoaderUtils.loadModelViewController(
                ScreenType.NUTRIENT_VALUE_SCREEN, entry.getValue());
        ScreenLoaderUtils.screenContextLoad(container, loader.getRoot(), false);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /** Load the ingredient information on a panel */
  private void loadIngredientInfo(List<IngredientParsedItem> input, Pane container) {
    if (input != null) {
      for (IngredientParsedItem parsedItem : input) {
        try {
          FXMLLoader loader =
              ScreenLoaderUtils.loadModelViewController(ScreenType.NUTRIENT_INFO_INGR, parsedItem);
          ScreenLoaderUtils.screenContextLoad(container, loader.getRoot(), false);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** Load the ingredient information on a panel */
  public void onViewStackedBar() {
    LOGGER.info("Viewing Stacked Bar Chart");
    nutrientInfoScreenPresenter.loadStackedBarChart();
  }

  /** Create new stacked bar chart pop up */
  public void loadStackedBarChart() {
    if (this.nutrientsResponse != null) {
      try {
        FXMLLoader loader =
            ScreenLoaderUtils.loadModelViewController(
                ScreenType.NUTRIENT_STACKED_BAR_TAB_SCREEN, this.nutrientsResponse);
        ScreenLoaderUtils.loadNewWindow(loader.getRoot(), 500, 500).show();
      } catch (IOException e) {
        AlertFactory.createErrorAlert(
                "Unable to load Stacked Bar chart!",
                "An error has occurred while trying to load the stacked bar chart")
            .showAndWait();
        LOGGER.error(e);
      }
    } else {
      AlertFactory.createErrorAlert(
              "No Nutrient Response found",
              "Please ensure that you have a nutrient response by selecting an ingredient!")
          .showAndWait();
      LOGGER.warn("No Nutrient Response found");
    }
  }

  /** Call to the presenter to send the Reddit report */
  public void onRedditSendReport(ActionEvent event) {
    currentEvent = event;
    ScreenLoaderUtils.setDisableButtons(List.of(sendRedditReportButton), true);
    LOGGER.info("Sending Reddit report.");
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
    ScreenLoaderUtils.addProcessIndicatorToPane((Pane) bottomContainer);
    // The reddit content
    try {
      nutrientInfoScreenPresenter.redditSendReport(nutrientInfoLabel.getText(), nutrientsResponse);
    } catch (Exception e) {
      ScreenLoaderUtils.setDisableButtons(List.of(sendPastebinReportButton), false);
      ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
      LOGGER.error(e);
      AlertFactory.createErrorAlert(
              "No Ingredients", "No ingredient found! Please reselect your ingredients!")
          .showAndWait();
    }
  }

  /** Call to the presenter to send the Pastebin report */
  public void onPastebinSendReport(ActionEvent event) {
    currentEvent = event;
    ScreenLoaderUtils.setDisableButtons(List.of(sendPastebinReportButton), true);
    LOGGER.info("Sending Pastebin report.");
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
    ScreenLoaderUtils.addProcessIndicatorToPane((Pane) bottomContainer);
    try {
      nutrientInfoScreenPresenter.pastebinSendReport(nutrientsResponse);
    } catch (Exception e) {
      ScreenLoaderUtils.setDisableButtons(List.of(sendPastebinReportButton), false);
      ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
      LOGGER.error(e);
      AlertFactory.createErrorAlert(
              "No Ingredients", "No ingredient found! Please reselect your ingredients!")
          .showAndWait();
    }
  }

  public void showAndWaitOnPastebinError(String cause, String reason) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
    ScreenLoaderUtils.setDisableButtons(List.of(sendPastebinReportButton), false);
  }

  public void showAndWaitOnRedditError(String cause, String reason) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
    ScreenLoaderUtils.setDisableButtons(List.of(sendRedditReportButton), false);
  }

  /** Load the panel of information if the report was sent successfully */
  public void loadSuccessfulPastebinSend(Object responseBody) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    LOGGER.info("Sending report succeeded. Report Link: " + responseBody);
    ScreenLoaderUtils.setDisableButtons(List.of(sendPastebinReportButton), false);
    VBox vBox = ScreenLoaderUtils.createReportPanel((String) responseBody);
    AlertFactory.createInfoAlertPane(vBox).showAndWait();
  }

  /** Load the panel of information if the report was sent successfully */
  public void loadSuccessfulRedditSend(SubmissionParentResponse responseBody) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    LOGGER.debug("Validating data content");
    ScreenLoaderUtils.setDisableButtons(List.of(sendRedditReportButton), false);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    SubmissionResponse submissionResponse = responseBody.getSubmissionResponse();
    LOGGER.debug("Successfully sent report to reddit!");
    VBox vBox = ScreenLoaderUtils.createReportPanel(submissionResponse.getData().getUrl());
    AlertFactory.createInfoAlertPane(vBox).showAndWait();
  }
}
