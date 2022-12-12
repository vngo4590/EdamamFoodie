package au.edu.sydney.soft3202.majorproject.model.api.pastebin;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface PastebinApiService {
  /**
   * Api service for sending request to pastebin
   *
   * @param query the queries of the api call
   * @return The call instance of the API that is responsible for making api call
   */
  @FormUrlEncoded
  @POST("api/api_post.php")
  Call<String> submitPaste(@FieldMap Map<String, String> query);
}
