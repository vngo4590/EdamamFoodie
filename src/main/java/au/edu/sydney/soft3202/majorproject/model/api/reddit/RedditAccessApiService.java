package au.edu.sydney.soft3202.majorproject.model.api.reddit;

import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RedditAccessApiService {
  /**
   * API service to retrieve the access token of Reddit
   *
   * @param grantType the grant type of the user credential
   * @param userName the Reddit username
   * @param password user password
   * @return The call instance of the API that is responsible for making api call
   */
  @FormUrlEncoded
  @POST("api/v1/access_token")
  Call<AuthorizationResponse> getAccessToken(
      @Field("grant_type") String grantType,
      @Field("username") String userName,
      @Field("password") String password);
}
