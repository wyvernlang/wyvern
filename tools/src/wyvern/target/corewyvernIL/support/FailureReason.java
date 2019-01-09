package wyvern.target.corewyvernIL.support;

public class FailureReason {
    private String reason = null;
    public void setReason(String reason) {
        /*if (this.reason != null) {
            throw new RuntimeException("broke invariant: cannot reassign failure reason");
        }*/
        this.reason = reason;
    }
    public String getReason() {
        return reason == null ? "" : reason;
    }
    @Override
    public String toString() {
        return reason;
    }
    public boolean isDefined() {
        return reason != null;
    }
}
