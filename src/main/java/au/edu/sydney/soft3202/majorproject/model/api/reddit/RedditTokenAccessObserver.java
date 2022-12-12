package au.edu.sydney.soft3202.majorproject.model.api.reddit;

/** This interface provides an access to update the access token of the reddit. */
public interface RedditTokenAccessObserver {
  /**
   * Perform an update once we have a token
   *
   * @param token the token value of the reddit API
   */
  void updateToken(String token);
}
