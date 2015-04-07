package wyvern.tools.util;

import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EvaluationEnvironment {
    private EvaluationEnvironment() {
    }
    private EvaluationEnvironment(ValueBinding vb, EvaluationEnvironment old) {
        this.binding = vb;
        this.parent = old;
    }
    public static final EvaluationEnvironment EMPTY = new EvaluationEnvironment();

    private ValueBinding binding;
    private EvaluationEnvironment parent;


    public EvaluationEnvironment extend(ValueBinding vb) {
        return new EvaluationEnvironment(vb, this);
    }

    public EvaluationEnvironment extend(EvaluationEnvironment other) {
        if (other == EMPTY) return this;
        return new EvaluationEnvironment(other.binding, extend(other.parent));
    }

    public Optional<ValueBinding> lookup(String name) {
        if (binding.getName().equals(name)) return Optional.of(binding);
        return parent.flatMap(e -> e.lookup(name));
    }

    public <T extends ValueBinding> Optional<T> lookupBinding(String name, Class<T> bindingType) {
        if (binding.getClass().equals(bindingType) && binding.getName().equals(name))
            return Optional.of((T)binding);
        return parent.flatMap(e -> e.lookupBinding(name, bindingType));
    }

    public LinkedList<ValueBinding> getBindings() {
        LinkedList<ValueBinding> bindings = parent.<LinkedList<ValueBinding>>map(EvaluationEnvironment::getBindings).orElseGet(LinkedList::new);
        bindings.addFirst(binding);
        return bindings;
    }

    public <T> Optional<T> map(Function<EvaluationEnvironment, T> ifPresent) {
        if (this == EMPTY) return Optional.empty();
        return Optional.of(ifPresent.apply(this));
    }

    public <T> Optional<T> flatMap(Function<EvaluationEnvironment, Optional<T>> ifPresent) {
        if (this == EMPTY) return Optional.empty();
        return ifPresent.apply(this);
    }

    public Environment toTypeEnv() {
        return parent.<Environment>map(e -> e.toTypeEnv().extend(binding)).orElse(Environment.getEmptyEnvironment());
    }
}
