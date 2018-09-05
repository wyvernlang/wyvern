package wyvern.tools.generics;

/**
 * Represents a generic parameter, such as:
 *   def foo[T, effect E](...) : ...
 *           ^  ^^^^^^^^
 *           1  2
 *  1: TYPE parameter
 *  2: EFFECT parameter
 *
 * @author justinlubin
 */
public class GenericParameter {
    private final GenericKind kind;
    private final String name;

    public GenericParameter(GenericKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public GenericKind getKind() {
        return this.kind;
    }

    public String getName() {
        return this.name;
    }
}
