package web.user;

import engine.api.RunHistory;

public class User {
    private final String username;
    private long usedCredits;
    private long totalCredits;
    private final RunHistory runHistory;

    public User(String username, long totalCredits) {
        this.username = username;
        this.totalCredits = totalCredits;
        this.usedCredits = 0;
        this.runHistory = new RunHistory();
    }

    public String getUsername() {
        return username;
    }

    public long getUsedCredits() {
        return usedCredits;
    }

    public long getTotalCredits() {
        return totalCredits;
    }

    public RunHistory getRunHistory() {
        return runHistory;
    }

    public int getRunCount() {
        return runHistory.runCount();
    }

    public void addCredits(long credits) {
        totalCredits += credits;
    }

    public void removeCredits(long credits) {
        var hadCredits = totalCredits;

        totalCredits = Math.max(totalCredits - credits, 0);
        usedCredits += Math.min(hadCredits, credits);
    }
}
