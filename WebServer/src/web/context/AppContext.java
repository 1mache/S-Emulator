package web.context;

import engine.api.RunHistory;
import engine.api.SLanguageEngine;
import engine.api.debug.DebugHandle;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import web.exception.InvalidUserException;
import web.exception.NotInDebugException;
import web.user.UserManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppContext {
    private SLanguageEngine engine;
    private UserManager userManager;
    private RunHistory runHistory;

    private final Map<String, String> function2User = new HashMap<>();
    private final Map<String, DebugHandle> debugHandlesOfUsers = new HashMap<>();

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

        synchronized (this){
            List<String> addedFunctions = getEngine().loadProgramIncremental(inputStream, null);
            addedFunctions.forEach(function -> function2User.put(function, user));
        }
    }

    public synchronized List<String> getAllUserFunctions(String user) {
        return function2User.entrySet().stream()
                .filter(entry -> entry.getValue().equals(user))
                .map(Map.Entry::getKey)
                .toList();
    }

    public synchronized List<String> getUserFunctions(String user) {
        return getAllUserFunctions(user).stream()
                .filter(funcName -> !getEngine().getProgramIdentifier(funcName).isMain())
                .toList();
    }

    public synchronized List<String> getUserPrograms(String user) {
        return getAllUserFunctions(user).stream()
                .filter(funcName -> getEngine().getProgramIdentifier(funcName).isMain())
                .toList();
    }

    public synchronized String getFunctionOwner(String functionName){
        return function2User.get(functionName);
    }

    public DebugHandle getDebugHandle(String username) throws NotInDebugException {
        DebugHandle debugHandle;
        synchronized (this) {
            debugHandle = debugHandlesOfUsers.get(username);
        }

        if(debugHandle == null)
            throw new NotInDebugException("User " + username + " is not in debug");

        return debugHandle;
    }

    public synchronized void setDebugHandle(String username, DebugHandle debugHandle) {
        debugHandlesOfUsers.put(username, debugHandle);
    }

    public synchronized void removeDebugHandle(String username) {
        debugHandlesOfUsers.remove(username);
    }
}
