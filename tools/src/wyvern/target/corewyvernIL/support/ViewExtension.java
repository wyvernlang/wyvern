package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;

public class ViewExtension extends View {
    private Variable from;
    private Path to;
    private View previous;

    public ViewExtension(Variable from, Path to, View previous) {
        this.from = from;
        this.to = to;
        this.previous = previous;
    }

    @Override
    public Path adapt(Variable v) {
        if (v.equals(from)) {
            if (to == null) {
                throw new RuntimeException("view adaptation failed");
            }
            return to;
        } else {
            return previous.adapt(v);
        }
    }

}
