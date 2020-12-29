package main;

public enum Step {
    ROLL("roll"),
    CONFIRM_ROLL("confirm"),
    STEAL("steal"),
    BUY("buy"),
    WON("won");

    private String stepName;

    Step(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }
}
