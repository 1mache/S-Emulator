package web.user;

import java.util.HashSet;
import java.util.Set;

public class UserManager {
    private final Set<String> users = new HashSet<>();

    // returns true if user was added, false if user already exists
    public synchronized boolean addUser(String username) {
        if (users.contains(username)) {
            return false;
        }

        users.add(username);
        return true;
    }

    public synchronized boolean userExists(String username) {
        return users.contains(username);
    }

}
