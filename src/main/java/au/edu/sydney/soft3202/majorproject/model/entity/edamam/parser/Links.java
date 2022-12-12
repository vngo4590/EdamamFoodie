package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

public class Links {

  @SerializedName("next")
  private Next next;

  public Next getNext() {
    return next;
  }

  @Override
  public String toString() {
    return "Links{" + "next = '" + next + '\'' + "}";
  }
}
