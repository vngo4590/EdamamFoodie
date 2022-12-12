package au.edu.sydney.soft3202.majorproject.controller.utils;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/** A factory class that provides spinning process inditator to a pane */
public class ProcessIndicatorFactory {
  public static final String PROCESS_INDICATOR_KEY = "#process_indicator_main";
  public static final String PROCESS_INDICATOR_KEY_CONTAINER = "#process_indicator_main_container";

  public static HBox createProcessIndicator() {
    ProgressIndicator progressIndicator = new ProgressIndicator();
    Text text = new Text("Loading . . .");
    HBox box = new HBox(progressIndicator, text);
    box.setAlignment(Pos.CENTER);
    box.setMaxHeight(30);
    box.setMinHeight(30);
    progressIndicator.setId(PROCESS_INDICATOR_KEY);
    box.setId(PROCESS_INDICATOR_KEY_CONTAINER);
    return box;
  }
}
