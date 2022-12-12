package au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission;

import com.google.gson.annotations.SerializedName;

public class SubmissionParentResponse {
  @SerializedName("json")
  public SubmissionResponse submissionResponse;

  public SubmissionResponse getSubmissionResponse() {
    return submissionResponse;
  }

  @Override
  public String toString() {
    return "SubmissionParentResponse {" + "submissionResponse=" + submissionResponse + '}';
  }
}
