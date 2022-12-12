package au.edu.sydney.soft3202.majorproject.model.api.reddit.offline;

import au.edu.sydney.soft3202.majorproject.model.api.DummyURLEnum;
import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditAccessState;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditPostQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.RedditRepository;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/** Offline Reddit repository for the reddit api requests */
public class OfflineRedditRepository implements RedditRepository {
  private static final Logger LOGGER = LogManager.getLogger(OfflineRedditRepository.class);
  private RedditAccessState redditAccessState = RedditAccessState.NOT_LOGGED_IN;
  /** The query builder of the response */
  private final RedditPostQueryBuilder redditPostQueryBuilder;

  private AuthorizationResponse authorizationResponse;
  private SubmissionParentResponse submissionResponse;
  private final int threadSleepMillis;

  public OfflineRedditRepository(int threadSleepMillis) {
    this.threadSleepMillis = threadSleepMillis;
    redditPostQueryBuilder = new RedditPostQueryBuilder().addDefaultPostSubmissionForm();
    try {
      this.authorizationResponse =
          (AuthorizationResponse)
              DummyURLEnum.loadFromJson(DummyURLEnum.REDDIT_AUTHORIZATION_RESPONSE);
      this.submissionResponse =
          (SubmissionParentResponse)
              DummyURLEnum.loadFromJson(DummyURLEnum.REDDIT_SUBMISSION_PARENT_RESPONSE);
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
  }

  @Override
  public void getAccessToken(
      String userName, String password, ResponseCallbackListener<AuthorizationResponse> listener) {
    LOGGER.debug("Getting Reddit Access Token ...");
    try {
      Thread.sleep(threadSleepMillis);
      setRedditAccessState(RedditAccessState.LOGGED_IN);
      listener.onSuccess(this.authorizationResponse);
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
  }

  @Override
  public void submitPost(
      Map<String, String> query, ResponseCallbackListener<SubmissionParentResponse> listener) {
    LOGGER.debug("Sending post with query: " + query);
    try {
      Thread.sleep(threadSleepMillis);
      if (redditAccessState.isLoggedIn()) {
        listener.onSuccess(submissionResponse);
      } else {
        listener.onCallbackError(
            new IllegalStateException("Unable to fetch response due to the user not logged in"));
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
  }

  @Override
  public RedditPostQueryBuilder getRedditPostQueryBuilder() {
    return this.redditPostQueryBuilder;
  }

  @Override
  public RedditAccessState getRedditAccessState() {
    return redditAccessState;
  }

  @Override
  public void setRedditAccessState(RedditAccessState redditAccessState) {
    LOGGER.debug("Set Access State to: " + redditAccessState);
    this.redditAccessState = redditAccessState;
  }
}
