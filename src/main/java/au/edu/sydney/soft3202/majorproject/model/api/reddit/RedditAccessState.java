package au.edu.sydney.soft3202.majorproject.model.api.reddit;

public enum RedditAccessState {
  LOGGED_IN(true),
  NOT_LOGGED_IN(false);

  private final boolean isLoggedIn;

  RedditAccessState(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }
}
