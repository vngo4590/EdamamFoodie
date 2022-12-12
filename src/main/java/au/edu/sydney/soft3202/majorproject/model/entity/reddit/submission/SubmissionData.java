package au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission;

import com.google.gson.annotations.SerializedName;

public class SubmissionData {

  @SerializedName("name")
  private String name;

  @SerializedName("id")
  private String id;

  @SerializedName("url")
  private String url;

  @SerializedName("drafts_count")
  private int draftsCount;

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public int getDraftsCount() {
    return draftsCount;
  }

  @Override
  public String toString() {
    return "SubmissionData {"
        + "name = '"
        + name
        + '\''
        + ",id = '"
        + id
        + '\''
        + ",url = '"
        + url
        + '\''
        + ",drafts_count = '"
        + draftsCount
        + '\''
        + "}";
  }
}
