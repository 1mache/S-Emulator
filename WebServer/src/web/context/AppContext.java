package web.context;

import engine.api.RunHistory;
import engine.api.SLanguageEngine;
import engine.api.debug.DebugHandle;
import web.exception.NotInDebugException;
import web.user.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppContext {
    private SLanguageEngine engine;
    private UserManager userManager;
    private RunHistory runHistory;

    private final Map<String, String> program2User = new HashMap<>();
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

    public synchronized void addProgramsFromUser(String user, List<String> programNames) {
        programNames.forEach(programName -> program2User.put(programName, user));
    }

    public synchronized List<String> getAllUserFunctions(String user) {
        return program2User.entrySet().stream()
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
        return program2User.get(functionName);
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
