package au.edu.sydney.soft3202.majorproject.model.api;

import au.edu.sydney.soft3202.majorproject.model.entity.error.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/** A central listener to provide a central behaviour when dealing with the responses */
public abstract class ResponseCallbackListener<T> implements Callback<T> {
  private ErrorResponse errorResponse = null;

  private Response<?> response;

  public Response<?> getResponse() {
    return response;
  }

  public abstract void onSuccess(Object responseBody);

  private static final Logger LOGGER = LogManager.getLogger(ResponseCallbackListener.class);

  @Override
  public void onResponse(Call call, Response response) {
    if (response.isSuccessful()) {
      this.response = response;
      this.onSuccess(response.body());
      if (response.body() != null) {
        LOGGER.debug("Response call is successful with result: " + response.body().getClass());
      } else {
        LOGGER.debug("Response call is successful");
      }
    } else {
      if (response.errorBody() != null) {
        LOGGER.error("An error occurred during response call");
        this.onCallbackError(response);
      }
    }
  }

  @Override
  public void onFailure(Call call, Throwable t) {
    call.cancel();
    this.onCallbackError(t);
  }

  public void onCallbackError(Throwable t) {
    this.errorResponse = new ErrorResponse();
    this.errorResponse.setMessage("An error has occurred " + t.getMessage());
    this.errorResponse.setError("forbidden");
  }

  public void onCallbackError(Response<?> response) {
    ErrorResponse error = null;
    if (response.errorBody() != null) {
      try {
        error = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
      } catch (IOException | JsonSyntaxException | NullPointerException e) {
        e.printStackTrace();
        LOGGER.error(e);
      }
    }
    if (error == null) {
      error = new ErrorResponse();
      error.setMessage("Unable to analyze the error response");
      error.setError("forbidden");
    }
    this.errorResponse = error;
    if (this.errorResponse.getErrorCode() == null) {
      this.errorResponse.setErrorCode(String.valueOf(response.code()));
    }
    this.errorResponse.setMessage(errorResponse.getMessage());
    LOGGER.error(errorResponse);
  }

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }
}
