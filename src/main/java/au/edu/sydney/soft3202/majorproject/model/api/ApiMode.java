package au.edu.sydney.soft3202.majorproject.model.api;

public enum ApiMode {
    ONLINE("online"),
    OFFLINE("offline");
    private final String apiModeType;

    ApiMode(String apiModeType) {
        this.apiModeType = apiModeType;
    }

    public String getApiModeType() {
        return apiModeType;
    }
}
