package web.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();

    // returns true if user was added, false if user already exists
    public synchronized boolean addUser(String username) {
        return addUser(username, 0L);
    }

    // returns true if user was added, false if user already exists
    public synchronized boolean addUser(String username, long initialCredits) {
        if (userExists(username)) {
            return false;
        }

        users.put(username, new User(username, initialCredits));
        return true;
    }

    public synchronized boolean userExists(String username) {
        return users.containsKey(username);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public Set<User> getUsers() {
        return Set.copyOf(users.values());
    }
}
