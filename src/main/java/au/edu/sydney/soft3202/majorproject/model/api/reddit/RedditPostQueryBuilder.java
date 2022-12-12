package au.edu.sydney.soft3202.majorproject.model.api.reddit;

import java.util.HashMap;
import java.util.Map;

/** Query builder for the reddit post given the default values */
public class RedditPostQueryBuilder {
  Map<String, String> query;

  public RedditPostQueryBuilder() {
    reset();
  }

  public RedditPostQueryBuilder reset() {
    this.query = new HashMap<>();
    return addDefaultPostSubmissionForm();
  }
  /**
   * Set the default variables for the reddit post requests
   *
   * @return The instance of builder class
   */
  public RedditPostQueryBuilder addDefaultPostSubmissionForm() {
    this.query.put("ad", "false");
    this.query.put("api_type", "json");
    this.query.put("kind", "self");
    this.query.put("title", "Title Sample");
    this.query.put("text", "Text Sample");
    this.query.put("sr", "FoodieTestSample");
    return this;
  }
  /**
   * Set or add the user reddit profile value for our report
   *
   * @return The instance of builder class
   */
  public RedditPostQueryBuilder addPostSubreddit(String subReddit) {
    this.query.put("sr", subReddit);
    return this;
  }
  /**
   * Set or add the subreddit value for our report
   *
   * @return The instance of builder class
   */
  public RedditPostQueryBuilder addUserProfileSubReddit(String userName) {
    return addPostSubreddit("u_" + userName);
  }
  /**
   * Set or add the title value for our report
   *
   * @return The instance of builder class
   */
  public RedditPostQueryBuilder addPostTittle(String title) {
    this.query.put("title", title);
    return this;
  }

  /**
   * Set or add the body value for our report
   *
   * @return The instance of builder class
   */
  public RedditPostQueryBuilder addPostBodyText(String text) {
    this.query.put("text", text);
    return this;
  }

  /**
   * Build the report parameters for the API
   *
   * @return the query of the reddit parameters
   */
  public Map<String, String> build() {
    return this.query;
  }
}
