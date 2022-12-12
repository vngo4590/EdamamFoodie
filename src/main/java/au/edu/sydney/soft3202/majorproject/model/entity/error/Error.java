package au.edu.sydney.soft3202.majorproject.model.entity.error;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Error {

  @SerializedName("errorCode")
  private String errorCode;

  @SerializedName("message")
  private String message;

  @SerializedName("params")
  private List<String> params;

  public String getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getParams() {
    return params;
  }

  @Override
  public String toString() {
    return "Error{"
        + "errorCode='"
        + errorCode
        + '\''
        + ", message='"
        + message
        + '\''
        + ", params="
        + params
        + '}';
  }
}
