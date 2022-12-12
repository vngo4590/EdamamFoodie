package au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization;

import com.google.gson.annotations.SerializedName;

public class AuthorizationResponse {

  @SerializedName("access_token")
  private String accessToken;

  @SerializedName("scope")
  private String scope;

  @SerializedName("token_type")
  private String tokenType;

  @SerializedName("expires_in")
  private int expiresIn;

  @SerializedName("error")
  private String error;

  public String getAccessToken() {
    return accessToken;
  }

  public String getScope() {
    return scope;
  }

  public String getTokenType() {
    return tokenType;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public String getError() {
    return error;
  }

  @Override
  public String toString() {
    return "AuthorizationResponse{"
        + "access_token = '"
        + accessToken
        + '\''
        + ",scope = '"
        + scope
        + '\''
        + ",token_type = '"
        + tokenType
        + '\''
        + ",expires_in = '"
        + expiresIn
        + '\''
        + ",error = '"
        + error
        + '\''
        + "}";
  }
}
