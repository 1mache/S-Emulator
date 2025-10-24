package web.user;

import engine.execution.ExecutionLimiter;
import engine.instruction.Instruction;

public class CreditExecutionLimiter implements ExecutionLimiter {
    private final User user;

    private boolean stopped;

    public CreditExecutionLimiter(User user) {
        this.user = user;
    }

    @Override
    public boolean breakCheck(Instruction nextInstruction) {
        if(stopped || user.getTotalCredits() < nextInstruction.staticCycles())
            stopped = true;

        return stopped;
    }

    @Override
    public void update(long cycles) {
        user.removeCredits(cycles);
    }
}
