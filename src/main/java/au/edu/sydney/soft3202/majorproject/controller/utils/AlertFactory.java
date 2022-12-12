package au.edu.sydney.soft3202.majorproject.controller.utils;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AlertFactory {
  private static final String ERROR_TAG = "ERROR";
  private static final String INFO_TAG = "INFO";

  public static Alert createInfoAlert(String details) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(INFO_TAG);
    alert.getDialogPane().setContent(getInfoContainer(details));
    return alert;
  }

  public static Alert createConfirmationAlert(
      String titleText, String headerText, String contentText) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(titleText);
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    return alert;
  }

  public static Alert createInfoAlertPane(Pane pane) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(INFO_TAG);
    alert.getDialogPane().setContent(pane);
    return alert;
  }

  public static Alert createErrorAlert(String cause, String reason) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ERROR_TAG);
    alert.setHeaderText(cause);
    alert.getDialogPane().setContent(getInfoContainer(reason));
    return alert;
  }

  private static VBox getInfoContainer(String details) {
    VBox vBox = new VBox();
    Text infoText = new Text(details);
    infoText.wrappingWidthProperty().set(200);
    vBox.minWidth(200);
    vBox.getChildren().add(infoText);
    return vBox;
  }
}
