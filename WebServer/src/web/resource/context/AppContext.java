package web.resource.context;

import engine.api.SLanguageEngine;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import web.exception.InvalidUserException;
import web.user.UserManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppContext {
    private SLanguageEngine engine;
    private UserManager userManager;

    private final Map<String, String> function2User = new HashMap<String, String>();

    private static final Object ENGINE_LOCK = new Object();
    private static final Object USER_MANAGER_LOCK = new Object();

    public SLanguageEngine getEngine() {
        synchronized (ENGINE_LOCK) {
            if (engine == null) {
                engine = SLanguageEngine.getInstance();
            }
        }

        return engine;
    }

    public UserManager getUserManager() {
        synchronized (USER_MANAGER_LOCK) {
            if (userManager == null) {
                userManager = new UserManager();
            }
        }

        return userManager;
    }

    public void loadProgram(String user, InputStream inputStream)
            throws UnknownFunctionException, UnknownLabelException, InvalidUserException {

        if(!getUserManager().userExists(user)) {
            throw new InvalidUserException("User does not exist: " + user);
        }

        List<String> addedFunctions = engine.loadProgram(inputStream, null);
        synchronized (this){
            addedFunctions.forEach(function -> function2User.put(function, user));
        }
    }

    public List<String> getUserFunctions(String user) {
        synchronized (this) {
            return function2User.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(user))
                    .map(Map.Entry::getKey)
                    .toList();
        }
    }
}
