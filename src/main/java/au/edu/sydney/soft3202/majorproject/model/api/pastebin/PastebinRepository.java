package au.edu.sydney.soft3202.majorproject.model.api.pastebin;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;

import java.util.Map;

public interface PastebinRepository {
  /** Call submission api call to the pastebin api. The response will call to listener on result. */
  void submitPaste(
      Map<String, String> query, ResponseCallbackListener<String> responseCallbackListener);

  /** Get the pastebin builder for the repository. */
  PastebinApiQueryBuilder getPastebinApiQueryBuilder();
}
