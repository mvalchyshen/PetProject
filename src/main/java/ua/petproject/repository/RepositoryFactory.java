package ua.petproject.repository;

import java.util.HashMap;
import java.util.Map;

public class RepositoryFactory {
    private static final Map<String, Repository> REPOSITORIES = new HashMap<>();

    public synchronized static <E, R extends Repository<E, ID>, ID> Repository<E, ID> of(Class<E> className) {
        final String modelName = className.getName();
        if (!REPOSITORIES.containsKey(modelName)) {
            REPOSITORIES.put(modelName, new RepositoryImpl(className));
        }
        return REPOSITORIES.get(modelName);
    }
}

