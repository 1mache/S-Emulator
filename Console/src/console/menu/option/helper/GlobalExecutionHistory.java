package console.menu.option.helper;

import engine.api.RunHistory;

public class GlobalExecutionHistory {
    private static final RunHistory runHistory = new RunHistory();

    public static RunHistory get() {
        return runHistory;
    }
}
