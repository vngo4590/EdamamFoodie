package au.edu.sydney.soft3202.majorproject;

import au.edu.sydney.soft3202.majorproject.model.api.ApiMode;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.model.thread.ThreadPoolSingleton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
  private static String edamamMode = "offline";
  private static String pastebinMode = "offline";
  private static String redditMode = "offline";
  private static final Logger LOGGER = LogManager.getLogger(MainApplication.class);

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  @Override
  public void start(Stage stage) throws IOException {
    // Set up all repos for the app
    if (edamamMode.equals("online")) {
      EdamamRepositorySingleton.init(ApiMode.ONLINE);
    } else {
      EdamamRepositorySingleton.init(ApiMode.OFFLINE);
    }
    if (pastebinMode.equals("online")) {
      PastebinRepositorySingleton.init(ApiMode.ONLINE);
    } else {
      PastebinRepositorySingleton.init(ApiMode.OFFLINE);
    }
    if (redditMode.equals("online")) {
      RedditRepositorySingleton.init(ApiMode.ONLINE);
    } else {
      RedditRepositorySingleton.init(ApiMode.OFFLINE);
    }

    FXMLLoader fxmlLoader =
        new FXMLLoader(MainApplication.class.getResource("screen/main_screen.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
    stage.setScene(scene);
    stage.show();
    stage.setOnCloseRequest(
        event -> {
          ThreadPoolSingleton.getInstance().shutdown();
          try {
            boolean terminated =
                ThreadPoolSingleton.getInstance()
                    .awaitTermination(10, TimeUnit.SECONDS); // wait for 10s in this case
            if (!terminated) {
              Thread.currentThread().interrupt();
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          ThreadPoolSingleton.getInstance().shutdownNow();
          Platform.exit();
          System.exit(0);
        });
  }

  public static boolean isValidArgument(String a) {
    return a.equalsIgnoreCase("online") || a.equalsIgnoreCase("offline");
  }

  public static void main(String[] args) {
    List<String> filteredArgs = new ArrayList<>();
    for (String v : args) {
      if (isValidArgument(v.trim())) {
        filteredArgs.add(v.trim().toLowerCase());
      }
    }
    if (filteredArgs.size() == 1) {
      String a = filteredArgs.get(0);
      edamamMode = a;
      pastebinMode = a;
      redditMode = a;
    } else if (filteredArgs.size() >= 2) {
      String a = filteredArgs.get(0).trim().toLowerCase();
      String b = filteredArgs.get(1).trim().toLowerCase();
      if (isValidArgument(a)) {
        edamamMode = a;
      }
      if (isValidArgument(b)) {
        pastebinMode = b;
        redditMode = b;
      }
      if (filteredArgs.size() >= 3) {
        String c = filteredArgs.get(2).trim().toLowerCase();
        if (isValidArgument(c)) {
          redditMode = c;
        }
      }
    }

    LOGGER.info("Starting " + edamamMode + " " + pastebinMode + " " + redditMode);

    launch();
  }
}
