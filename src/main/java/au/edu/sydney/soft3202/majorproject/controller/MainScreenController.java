package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.presenter.MainScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
  public static final String MAIN_CONTAINER_ID = "#mainContainerPane";
  public static final String MAIN_DISPLAY_CONTAINER_ID = "#mainDisplayContainer";
  public static final String MAIN_BOTTOM_DISPLAY_CONTAINER_ID = "#bottomMainContainerPane";
  @FXML protected Button redditLoginButton;
  @FXML protected HBox bottomMainContainerPane;
  @FXML protected StackPane mainStackPaneScreen;
  @FXML protected AnchorPane mainDisplayContainer;
  @FXML protected VBox mainScreen;
  @FXML protected AnchorPane mainContainerPane;
  private static final Logger LOGGER = LogManager.getLogger(MainScreenController.class);
  private MediaPlayer mediaPlayer;
  private MainScreenPresenter mainScreenPresenter;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mainContainerPane.getChildren().clear();
    Media media =
        new Media(
            Objects.requireNonNull(
                    MainScreenController.class.getResource(
                        "/au/edu/sydney/soft3202/majorproject/music/YOASOBI - Racing into the Night - jian lofi.wav"))
                .toString());
    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    mediaPlayer.setAutoPlay(true);
    LOGGER.info("Song YOASOBI - Racing into the Night - jian lofi is autoplayed");
    loadScreen(ScreenType.ENTER_ALLERGEN_SCREEN);
    mainScreenPresenter = new MainScreenPresenter(this);
  }

  public void onEnterIngredient() {
    mainScreenPresenter.resetIngredientQuery();
  }

  public void updateOnLoadingEnterIngredient() {
    LOGGER.info("Going back to entering allergen and then the ingredient");
    loadScreen(ScreenType.ENTER_ALLERGEN_SCREEN);
  }

  private void loadScreen(ScreenType screenType) {
    Pane contentPane = null;
    try {
      FXMLLoader loader = ScreenLoaderUtils.loadModelViewController(screenType);
      contentPane = loader.getRoot();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (contentPane != null) {
      ScreenLoaderUtils.screenContextLoad(this.mainContainerPane, contentPane, true);
    }
  }

  public void onResetCache(ActionEvent event) {
    mainScreenPresenter.clearCache();
  }

  public void updateOnClearCache() {
    LOGGER.info("Cache has been reset!");
    AlertFactory.createInfoAlert("Cache has been Cleared!").show();
  }

  public void onStopMusic() {
    mainScreenPresenter.pauseMusic();
  }

  public void onPlayMusic() {
    mainScreenPresenter.playMusic();
  }

  public void onRedditLogin() {
    mainScreenPresenter.loadRedditLoginPanel();
  }

  public void updateOnRedditNotLoggedIn() {
    // Disable the login button if and only if the state is not logged in
    redditLoginButton.setText("Try logging in to Reddit again!");
    ScreenLoaderUtils.setDisableButtons(List.of(redditLoginButton), false);
  }

  public void updateOnRedditLoggedIn() {
    redditLoginButton.setText("You have logged in to Reddit!");
  }

  public void onSetInfoLogging() {
    LOGGER.info(
        "The application sets log level from "
            + LogManager.getRootLogger().getLevel()
            + " to INFO");
    Configurator.setRootLevel(Level.INFO);
  }

  public void onSetDebugLogging() {
    LOGGER.info(
        "The application sets log level from "
            + LogManager.getRootLogger().getLevel()
            + " to DEBUG");
    Configurator.setRootLevel(Level.DEBUG);
  }

  public void onSetAllLogging() {
    LOGGER.info(
        "The application sets log level from " + LogManager.getRootLogger().getLevel() + " to ALL");
    Configurator.setRootLevel(Level.ALL);
  }

  public void playMusic() {
    mediaPlayer.play();
    LOGGER.info("Song YOASOBI - Racing into the Night - jian lofi has been put on play");
  }

  public void pauseMusic() {
    mediaPlayer.pause();
    LOGGER.info("Song YOASOBI - Racing into the Night - jian lofi has been stopped");
  }

  public void loadRedditLoginPanel() {
    LOGGER.info("Opening panel to login to Reddit!");
    // Disable the login button
    ScreenLoaderUtils.setDisableButtons(List.of(redditLoginButton), true);
    try {
      FXMLLoader fxmlLoader =
          ScreenLoaderUtils.loadModelViewController(ScreenType.REDDIT_LOGIN_SCREEN);
      Stage newStage = ScreenLoaderUtils.loadNewWindow(fxmlLoader.getRoot(), -1, -1);
      newStage.setOnHidden(
          ev -> {
            LOGGER.info("Login Reddit Panel has been closed!");
            mainScreenPresenter.updateOnRedditStatus();
            LOGGER.info("User closed Login panel.");
          });
      newStage.show();
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error(e);
      AlertFactory.createErrorAlert(
              "An error ocurred while trying to load reddit login screen", e.getMessage())
          .showAndWait();
    }
  }
}
