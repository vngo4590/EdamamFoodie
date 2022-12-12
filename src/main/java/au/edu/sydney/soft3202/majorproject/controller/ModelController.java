package au.edu.sydney.soft3202.majorproject.controller;

import javafx.fxml.Initializable;

/** Main interface to load details */
public interface ModelController<T> extends Initializable {
  void loadDetails(T input);
}
