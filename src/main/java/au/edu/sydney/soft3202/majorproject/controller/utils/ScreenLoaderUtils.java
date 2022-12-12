package au.edu.sydney.soft3202.majorproject.controller.utils;

import au.edu.sydney.soft3202.majorproject.controller.ModelController;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.function.UnaryOperator;

public class ScreenLoaderUtils {
  private static final Logger LOGGER = LogManager.getLogger(ScreenLoaderUtils.class);
  /**
   * Given a screen of type ModelPresenter of a specific class, we create a basic loader that loads
   * such screen
   */
  @SuppressWarnings("unchecked")
  public static FXMLLoader loadModelViewController(ScreenType screen, Object data)
      throws IOException {
    FXMLLoader loader = loadModelViewController(screen);
    /*
     * Because we are passing in POJO class, we can't really
     * set anything specific here
     * */
    ((ModelController<Object>) loader.getController()).loadDetails(data);
    return loader;
  }
  /** Given a screen, we create a basic loader that loads such screen */
  public static FXMLLoader loadModelViewController(ScreenType screen) throws IOException {
    FXMLLoader loader = new FXMLLoader(ScreenLoaderUtils.class.getResource(screen.getScreenPath()));
    loader.load();
    return loader;
  }

  /**
   * Loads another content inside a parent content
   *
   * @param displayPane parent content pane
   * @param contentPane child content pane
   * @param clearChildren whether children of the parent pane should be cleared or not
   */
  public static void screenContextLoad(Pane displayPane, Pane contentPane, boolean clearChildren) {
    if (clearChildren) {
      displayPane.getChildren().clear();
    }
    displayPane.getChildren().add(contentPane);
  }

  /**
   * Disable or enable all buttons in a given list
   *
   * @param buttons list of all buttons
   * @param value true of the buttons to be disabled / or false if the buttons to be enabled
   */
  public static void setDisableButtons(List<Button> buttons, boolean value) {
    for (Button b : buttons) {
      b.setDisable(value);
    }
  }

  public static TextFormatter<String> numericIntTextFormatter() {
    UnaryOperator<TextFormatter.Change> filter =
        change -> {
          String text = change.getText();

          if (text.matches("[0-9]*")) {
            return change;
          }

          return null;
        };
    return new TextFormatter<>(filter);
  }

  public static Stage loadNewWindow(Pane content, double width, double height) {
    Stage newStage = new Stage();
    Scene scene;
    if (width < 0 || height < 0) {
      scene = new Scene(content);
    } else {
      scene = new Scene(content, width, height);
    }
    newStage.setScene(scene);
    return newStage;
  }

  public static void clipboardText(String text) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    content.putString(text);
    clipboard.setContent(content);
  }

  public static Node getNodeByIdFromEvent(String id, Event event) {
    Scene currentScene = ((Node) event.getTarget()).getScene();
    return currentScene.lookup(id);
  }

  public static VBox createReportPanel(String responseBody) {
    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);
    vBox.minWidth(100);
    vBox.minHeight(50);
    Label label = new Label(responseBody);
    Button copyButton = new Button("Copy");
    copyButton.setOnAction(
        buttonEvent -> {
          ScreenLoaderUtils.clipboardText(responseBody);
        });
    vBox.getChildren().add(label);
    vBox.getChildren().add(copyButton);
    return vBox;
  }

  public static void removeProcessIndicatorFromPane(Pane pane) {
    Platform.runLater(
        () -> {
          for (Node node : pane.getChildren()) {
            if (ProcessIndicatorFactory.PROCESS_INDICATOR_KEY_CONTAINER.equals(node.getId())) {
              LOGGER.info("Removed Loading Bar");
              pane.getChildren().remove(node);
              return;
            }
          }
          LOGGER.info("Unable To Find Loading Bar");
        });
  }

  public static void addProcessIndicatorToPane(Pane pane) {
    Platform.runLater(
        () -> {
          for (Node node : pane.getChildren()) {
            if (ProcessIndicatorFactory.PROCESS_INDICATOR_KEY_CONTAINER.equals(node.getId())) {
              LOGGER.info("Loading Bar has already been added");
              return;
            }
          }
          pane.getChildren().add(ProcessIndicatorFactory.createProcessIndicator());
          LOGGER.info("Loading Bar has been added");
        });
  }

  public static String formatListStringToString(List<String> values) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String s : values) {
      stringBuilder.append(s).append("\n");
    }
    return stringBuilder.toString();
  }

}
