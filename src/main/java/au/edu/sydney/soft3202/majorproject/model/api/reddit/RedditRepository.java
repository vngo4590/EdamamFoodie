package au.edu.sydney.soft3202.majorproject.model.api.reddit;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;

import java.util.Map;

/** Interface for interacting with the Reddit API. */
public interface RedditRepository {
  /**
   * Get the access token of the user credential. The result will get posted to the listener
   *
   * @param userName The Reddit username
   * @param password The password of the user
   * @param listener The listener that listens to the result
   */
  void getAccessToken(
      String userName, String password, ResponseCallbackListener<AuthorizationResponse> listener);
  /**
   * Get the submission post return result after posting the report. The result will get posted to
   * the listener
   *
   * @param query Query of the submission to post report to Reddit
   * @param listener The listener that listens to the result
   */
  void submitPost(
      Map<String, String> query, ResponseCallbackListener<SubmissionParentResponse> listener);

  RedditPostQueryBuilder getRedditPostQueryBuilder();

  RedditAccessState getRedditAccessState();

  void setRedditAccessState(RedditAccessState redditAccessState);
}
