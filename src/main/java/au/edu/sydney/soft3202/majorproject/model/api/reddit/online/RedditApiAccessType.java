package au.edu.sydney.soft3202.majorproject.model.api.reddit.online;

public enum RedditApiAccessType {
  ACCESS("https://www.reddit.com/"),
  OAUTH("https://oauth.reddit.com/");
  private final String baseUrl;

  RedditApiAccessType(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
