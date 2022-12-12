package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

public class Next {

  @SerializedName("href")
  private String href;

  @SerializedName("title")
  private String title;

  public String getHref() {
    return href;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    return "Next{" + "href = '" + href + '\'' + ",title = '" + title + '\'' + "}";
  }
}
