package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.AlertFactory;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.presenter.RedditLoginScreenPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RedditLoginScreenController implements Initializable {
  @FXML protected Label redditLoginTitleLabel;
  @FXML protected VBox redditLoginScreen;
  @FXML protected TextField userNameTextField;
  @FXML protected PasswordField passwordTextField;
  @FXML protected Button loginButton;
  private ActionEvent currentEvent;
  private RedditLoginScreenPresenter redditLoginScreenPresenter;

  private static final Logger LOGGER = LogManager.getLogger(RedditLoginScreenController.class);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    redditLoginScreenPresenter = new RedditLoginScreenPresenter(this);
  }

  /**
  * Call to the presenter to try to login
  * */
  public void onLogin(ActionEvent event) {
    currentEvent = event;
    ScreenLoaderUtils.setDisableButtons(List.of(loginButton), true);
    LOGGER.info("Logging into Reddit");
    String userName = userNameTextField.getText();
    String password = passwordTextField.getText();
    ScreenLoaderUtils.addProcessIndicatorToPane(redditLoginScreen);
    redditLoginScreenPresenter.loginToReddit(userName, password);
  }

  public void showAndWaitOnRedditError(String cause, String reason) {
    ScreenLoaderUtils.removeProcessIndicatorFromPane(redditLoginScreen);
    AlertFactory.createErrorAlert(cause, reason).showAndWait();
    ScreenLoaderUtils.setDisableButtons(List.of(loginButton), false);
  }

  public void updateRedditSuccessfulLogin() {
    Stage stage = (Stage) ((Node) currentEvent.getTarget()).getScene().getWindow();
    ScreenLoaderUtils.removeProcessIndicatorFromPane(redditLoginScreen);
    ScreenLoaderUtils.setDisableButtons(List.of(loginButton), true);
    redditLoginTitleLabel.setText("You have Logged In!");
    // Close stage on success
    stage.close();
  }
}
