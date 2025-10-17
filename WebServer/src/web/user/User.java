package web.user;

public class User {
    private final String username;
    private long usedCredits;
    private long totalCredits;

    public User(String username, long totalCredits) {
        this.username = username;
        this.totalCredits = totalCredits;
        this.usedCredits = 0;
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

    public void addCredits(long credits) {
        this.totalCredits += credits;
    }

    public void removeCredits(long credits) {
        this.totalCredits -= credits;
        this.usedCredits += credits;
    }
}
