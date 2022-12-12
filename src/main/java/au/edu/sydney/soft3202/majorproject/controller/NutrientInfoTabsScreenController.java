package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.IngredientParsedItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionResponse;
import au.edu.sydney.soft3202.majorproject.model.general.FlattenSerializable;
import au.edu.sydney.soft3202.majorproject.presenter.NutrientInfoTabsScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NutrientInfoTabsScreenController implements ModelController<NutrientsResponse> {
  @FXML protected Button sendAllPastebinReportButton;
  @FXML protected Button sendAllRedditReportButton;
  @FXML protected VBox nutrientInfoTabsScreen;
  @FXML protected TabPane nutrientInfoTabsContainer;

  private static final Logger LOGGER = LogManager.getLogger(NutrientInfoTabsScreenController.class);
  private NutrientInfoTabsScreenPresenter nutrientInfoTabsScreenPresenter;
  private ActionEvent currentEvent;
  private List<FlattenSerializable> reportSerializables;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.reportSerializables = new ArrayList<>();
    nutrientInfoTabsScreenPresenter = new NutrientInfoTabsScreenPresenter(this);
  }

  @Override
  public void loadDetails(NutrientsResponse input) {
    /*
     * For every input, we need to create new tab and add the tab to container
     * */
    // Find the existing tab
    nutrientInfoTabsScreenPresenter.loadScreenOnNutrientInfo(input);
  }

  /**
   * Prepare to load the nutrient information
   *
   * @param input the nutrient information
   * @return The panel which contains the nutrient information
   */
  public Node loadScreenOnNutrientInfo(NutrientsResponse input) {
    FXMLLoader loader;
    try {
      loader = ScreenLoaderUtils.loadModelViewController(ScreenType.NUTRIENT_INFO_SCREEN, input);
      return loader.getRoot();
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
    return null;
  }

  /**
   * Load the nutrient information onto the UI
   *
   * @param input the nutrient response information
   * @param node the panel to load the nutrient information
   */
  public void loadContentToTab(NutrientsResponse input, Node node) {
    IngredientParsedItem parsedItem = input.getIngredients().get(0);
    String inputId = parsedItem.getFoodId() + "_" + parsedItem.getMeasure();
    Tab matchingTab = getMatchingTab(parsedItem, inputId);
    LOGGER.info("Loading tab with id " + inputId);
    reportSerializables.add(input);
    final TabPane tabPane = this.nutrientInfoTabsContainer;
    matchingTab.setContent(node);
    tabPane.getTabs().remove(matchingTab);
    tabPane.getTabs().add(matchingTab);
  }

  /**
   * Try to fetch the tab of a given Id. if it does not exist, we will create a new tab
   *
   * @param parsedItem the tab's metadata
   * @param inputId the tab's id
   */
  private Tab getMatchingTab(IngredientParsedItem parsedItem, String inputId) {
    Tab matchingTab = null;
    for (Tab tab : nutrientInfoTabsContainer.getTabs()) {
      if (inputId.equals(tab.getId())) {
        matchingTab = tab;
        break;
      }
    }
    if (matchingTab == null) {
      matchingTab = new Tab(parsedItem.getMeasure() + " " + parsedItem.getFood());
      matchingTab.setId(inputId);
    }
    return matchingTab;
  }
  /** Call to the presenter to send all reports to Reddit */
  public void onAllRedditSendReport(ActionEvent event) {
    currentEvent = event;
    boolean validationResult = validateAndGetContent();
    if (!validationResult) return;
    LOGGER.info("Sending Reddit report.");
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
    ScreenLoaderUtils.addProcessIndicatorToPane((Pane) bottomContainer);
    try {
      ScreenLoaderUtils.setDisableButtons(List.of(sendAllRedditReportButton), true);
      // The reddit content
      nutrientInfoTabsScreenPresenter.redditSendReport(
          "All Chosen Ingredients", reportSerializables);
    } catch (IllegalArgumentException | NullPointerException e) {
      ScreenLoaderUtils.setDisableButtons(List.of(sendAllRedditReportButton), false);
      ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
      LOGGER.error(e);
    }
  }
  /** Call to the presenter to send all Pastebin to Reddit */
  public void onAllPastebinSendReport(ActionEvent event) {
    currentEvent = event;
    boolean validationResult = validateAndGetContent();
    if (!validationResult) return;
    LOGGER.info("Sending Pastebin report.");
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, event);
    ScreenLoaderUtils.addProcessIndicatorToPane((Pane) bottomContainer);
    try {
      ScreenLoaderUtils.setDisableButtons(List.of(sendAllPastebinReportButton), true);
      nutrientInfoTabsScreenPresenter.pastebinSendReport(reportSerializables);
    } catch (IllegalArgumentException | NullPointerException e) {
      ScreenLoaderUtils.setDisableButtons(List.of(sendAllPastebinReportButton), false);
      ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
      LOGGER.error(e);
    }
  }

  private boolean validateAndGetContent() {
    if (reportSerializables == null || reportSerializables.isEmpty()) {
      LOGGER.error("Content does not seem to be correct");
      AlertFactory.createErrorAlert(
              "No Ingredients", "No ingredient found! Please reselect your ingredients!")
          .showAndWait();
      return false;
    }
    return true;
  }

  public void showAndWaitOnPastebinError(String cause, String reason) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
    ScreenLoaderUtils.setDisableButtons(List.of(sendAllPastebinReportButton), false);
  }

  public void showAndWaitOnRedditError(String cause, String reason) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
    ScreenLoaderUtils.setDisableButtons(List.of(sendAllRedditReportButton), false);
  }

  /** Load the panel of information if the report was sent successfully */
  public void loadSuccessfulPastebinSend(Object responseBody) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    LOGGER.info("Sending report succeeded. Report Link: " + responseBody);
    ScreenLoaderUtils.setDisableButtons(List.of(sendAllPastebinReportButton), false);
    VBox vBox = ScreenLoaderUtils.createReportPanel((String) responseBody);
    AlertFactory.createInfoAlertPane(vBox).showAndWait();
  }

  /** Load the panel of information if the report was sent successfully */
  public void loadSuccessfulRedditSend(SubmissionParentResponse responseBody) {
    Node bottomContainer =
        ScreenLoaderUtils.getNodeByIdFromEvent(
            MainScreenController.MAIN_BOTTOM_DISPLAY_CONTAINER_ID, currentEvent);
    LOGGER.debug("Validating data content");
    ScreenLoaderUtils.setDisableButtons(List.of(sendAllRedditReportButton), false);
    ScreenLoaderUtils.removeProcessIndicatorFromPane((Pane) bottomContainer);
    SubmissionResponse submissionResponse = responseBody.getSubmissionResponse();
    LOGGER.debug("Successfully sent report to reddit!");
    VBox vBox = ScreenLoaderUtils.createReportPanel(submissionResponse.getData().getUrl());
    AlertFactory.createInfoAlertPane(vBox).showAndWait();
  }
}
