package au.edu.sydney.soft3202.majorproject.model.api.reddit.online;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.*;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;
/** Online Reddit Repository that makes online calls to the Reddit API */
public class OnlineRedditRepository implements RedditRepository {
  private static final Logger LOGGER = LogManager.getLogger(OnlineRedditRepository.class);
  private final RedditPostQueryBuilder redditPostQueryBuilder;
  private final RedditAccessApiService redditAccessApiService;
  private RedditOauthApiService redditOauthApiService;
  private RedditTokenAccessObserver redditTokenAccessObserver;
  private RedditAccessState redditAccessState = RedditAccessState.NOT_LOGGED_IN;

  public OnlineRedditRepository(
      RedditAccessApiService redditAccessApiService,
      RedditOauthApiService redditOauthApiService,
      RedditTokenAccessObserver redditTokenAccessObserver) {
    this.redditAccessApiService = redditAccessApiService;
    this.redditOauthApiService = redditOauthApiService;
    this.redditPostQueryBuilder = new RedditPostQueryBuilder().addDefaultPostSubmissionForm();
    this.redditTokenAccessObserver = redditTokenAccessObserver;
  }

  @Override
  public void getAccessToken(
      String userName, String password, ResponseCallbackListener<AuthorizationResponse> listener) {
    LOGGER.debug("Getting Reddit Access Token ...");
    Call<AuthorizationResponse> authorizationResponseCall =
        this.redditAccessApiService.getAccessToken("password", userName, password);
    try {
      /*
       * Call non-asynchronous call to the server
       * and then call to the listener once we got the response
       * */
      Response<AuthorizationResponse> response = authorizationResponseCall.execute();
      if (response.body() != null) {
        LOGGER.debug("Updating Reddit Access Token ...");
        redditTokenAccessObserver.updateToken(response.body().getAccessToken());
      }
      listener.onResponse(authorizationResponseCall, response);
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error(e);
      listener.onFailure(authorizationResponseCall, e);
    }
  }

  public void setRedditTokenAccessObserver(RedditTokenAccessObserver redditTokenAccessObserver) {
    this.redditTokenAccessObserver = redditTokenAccessObserver;
  }

  public void setRedditOauthApiService(RedditOauthApiService redditOauthApiService) {
    this.redditOauthApiService = redditOauthApiService;
  }

  @Override
  public void submitPost(
      Map<String, String> query, ResponseCallbackListener<SubmissionParentResponse> listener) {
    /*
     * Make synchronous call to the server and call to listener on result.
     * */
    LOGGER.debug("Sending post with query: " + query);
    Call<SubmissionParentResponse> submissionResponse =
        this.redditOauthApiService.submitPost(query);
    submissionResponse.enqueue(listener);
  }

  @Override
  public RedditPostQueryBuilder getRedditPostQueryBuilder() {
    return redditPostQueryBuilder;
  }

  @Override
  public RedditAccessState getRedditAccessState() {
    return this.redditAccessState;
  }

  @Override
  public void setRedditAccessState(RedditAccessState redditAccessState) {
    LOGGER.debug("Set Access State to: " + redditAccessState);
    this.redditAccessState = redditAccessState;
  }
}
