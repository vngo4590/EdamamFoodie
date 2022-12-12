package au.edu.sydney.soft3202.majorproject.model.api.reddit;

import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.Map;

public interface RedditOauthApiService {
  /**
   * Submit post to Reddit
   *
   * @param query the queries of the api call
   * @return The call instance of the API that is responsible for making api call
   */
  @FormUrlEncoded
  @POST("api/submit")
  Call<SubmissionParentResponse> submitPost(@FieldMap Map<String, String> query);
}
