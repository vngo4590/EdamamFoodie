package au.edu.sydney.soft3202.majorproject.model.entity.error;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorResponse {

  @SerializedName("error")
  private String error;

  @SerializedName("message")
  private String message;

  @SerializedName("errorCode")
  private String errorCode;

  @SerializedName("errors")
  private List<Error> errors;

  public List<Error> getErrors() {
    return errors;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "ErrorResponse{"
        + "error='"
        + error
        + '\''
        + ", message='"
        + message
        + '\''
        + ", errorCode='"
        + errorCode
        + '\''
        + ", errors="
        + errors
        + '}';
  }
}
