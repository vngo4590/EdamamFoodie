package au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubmissionResponse {

  @SerializedName("ratelimit")
  private double rateLimit = 0;

  @SerializedName("data")
  private SubmissionData submissionData;

  @SerializedName("errors")
  private List<List<String>> errors;

  public double getRateLimit() {
    return rateLimit;
  }

  public SubmissionData getData() {
    return submissionData;
  }

  public List<List<String>> getErrors() {
    return errors;
  }

  public void setRateLimit(double rateLimit) {
    this.rateLimit = rateLimit;
  }

  public SubmissionData getSubmissionData() {
    return submissionData;
  }

  public void setSubmissionData(SubmissionData submissionData) {
    this.submissionData = submissionData;
  }

  public void setErrors(List<List<String>> errors) {
    this.errors = errors;
  }

  public String getErrorString() {
    StringBuilder errorString = new StringBuilder();
    for (List<String> errorList : errors) {
      for (String e : errorList) {
        errorString.append(e).append("\n");
      }
    }
    return errorString.toString();
  }

  @Override
  public String toString() {
    return "SubmissionResponse {"
        + "rateLimit = '"
        + rateLimit
        + '\''
        + ",data = '"
        + submissionData
        + '\''
        + ",errors = '"
        + errors
        + '\''
        + "}";
  }
}
