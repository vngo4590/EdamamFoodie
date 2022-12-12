package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.api.ApiMode;

/** Singleton that provides global access to Edamam Food Repository */
public class EdamamRepositorySingleton {
    private static EdamamRepository edamamRepository;

    /**
     * Initialize the instance of the EdamamRepository. If no api mode is provided, the default will
     * be online
     *
     * @param apiMode the api mode of the repository. Null for default
     */
    public static void init(ApiMode apiMode) {
        if (apiMode == ApiMode.OFFLINE) {
            edamamRepository = EdamamRepositoryFactory.createOfflineFoodRepository();
        } else {
            edamamRepository = EdamamRepositoryFactory.createOnlineFoodRepository();
        }
    }
    /**
     * Get the instance of the EdamamRepository. If no instance of the repository has been
     * initialized, this method will create the object using the default api mode.
     *
     * @return the instance of EdamamRepository
     */
    public synchronized static EdamamRepository getInstance() {
        if (edamamRepository == null) {
            init(null);
        }
        return edamamRepository;
    }
}
