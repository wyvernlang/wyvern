package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.util.Pair;

public class SeqExpr extends Expression {
    private LinkedList<HasLocation> elements; // either a VarBinding or an Expression

    public SeqExpr() {
        //setExprType(Util.unitType()); // default to Unit
        elements = new LinkedList<HasLocation>();
    }

    public SeqExpr(ValueType expectedType) {
        this();
        setExprType(expectedType);
    }

    /** Side-effects the current SeqExpr to add an expression.
     *
     * @param expr
     * @return this
     */
    public SeqExpr addExpr(IExpr expr) {
        elements.addLast(expr);
        return this;
    }

    public void addBindingLast(VarBinding binding) {
        addBinding(binding, true);
    }
    public void addBinding(VarBinding binding, boolean isLast) {
        if (isLast) {
            elements.addLast(binding);
        } else {
            elements.addFirst(binding);
        }
    }
    public void addBindingLast(String varName, ValueType type, IExpr toReplace) {
        addBinding(varName, type, toReplace, true);
    }
    public void addBinding(String varName, ValueType type, IExpr toReplace, boolean isLast) {
        addBinding(new VarBinding(varName, type, toReplace), isLast);
    }


    public List<HasLocation> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public ValueType typecheckNoAvoidance(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return typecheckWithCtx(ctx, effectAccumulator).getSecond();
    }
    public Pair<TypeContext, ValueType> typecheckWithCtx(TypeContext ctx, EffectAccumulator effectAccumulator) {
        TypeContext extendedCtx = ctx;
        ValueType result = Util.unitType();
        for (HasLocation elem : elements) {
            if (elem instanceof VarBinding) {
                VarBinding binding = (VarBinding) elem;
                extendedCtx = binding.typecheck(extendedCtx, effectAccumulator);
                //TODO: make this Unit
                result = binding.getType(); // Util.unitType();
            } else if (elem instanceof Expression) {
                result = ((Expression) elem).typeCheck(extendedCtx, effectAccumulator);
            } else {
                throw new RuntimeException("invariant broken");
            }
        }
        return new Pair<TypeContext, ValueType>(extendedCtx, result);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        Pair<TypeContext, ValueType> p = typecheckWithCtx(ctx, effectAccumulator);
        TypeContext extendedCtx = p.getFirst();
        ValueType result = p.getSecond();
        FailureReason r = new FailureReason();
        if (this.getExprType() != null) {
            if (!result.isSubtypeOf(getExprType(), extendedCtx, r)) {
                ToolError.reportError(ErrorMessage.NOT_SUBTYPE, getLocation(), result.toString(), getExprType().toString(), r.getReason());
            }
            return getExprType();
        }
        for (int i = elements.size() - 1; i >= 0; --i) {
            HasLocation elem = elements.get(i);
            if (elem instanceof VarBinding) {
                String varName = ((VarBinding) elem).getVarName();
                // TODO (hack): avoid only variables not present outside
                if (!ctx.isPresent(varName, true)) {
                    result = result.avoid(varName, extendedCtx);
                }
            }
        }
        setExprType(result);
        return getExprType();
    }

    public GenContext extendContext(GenContext ctx) {
        for (HasLocation elem : elements) {
            if (elem instanceof VarBinding) {
                VarBinding binding = (VarBinding) elem;
                ctx = ctx.extend(binding.getVarName(), new Variable(binding.getVarName()), binding.getType());
            }
        }
        return ctx;
    }

    // making this public
    @Override
    public void setExprType(ValueType exprType) {
        super.setExprType(exprType);
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        String newIndent = indent + "    ";
        dest.append("seqexpr\n");

        for (HasLocation elem : elements) {
            if (elem instanceof VarBinding) {
                VarBinding binding = (VarBinding) elem;
                binding.doPrettyPrint(dest, newIndent);
            } else if (elem instanceof Expression) {
                dest.append(newIndent);
                ((Expression) elem).doPrettyPrint(dest, newIndent);
            } else {
                dest.append(newIndent).append("unexpected item in sequence!\n");
            }
        }
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    public Pair<Value, EvalContext> interpretCtx(EvalContext ctx) {
        EvalContext extendedCtx = ctx;
        Value result = Util.unitValue();
        for (HasLocation elem : elements) {
            if (elem instanceof VarBinding) {
                VarBinding binding = (VarBinding) elem;
                extendedCtx = binding.interpret(extendedCtx);
                // TODO: return unit
                result = extendedCtx.lookupValue(binding.getVarName()); // Util.unitValue();
            } else if (elem instanceof Expression) {
                result = ((Expression) elem).interpret(extendedCtx);
            } else {
                throw new RuntimeException("invariant broken");
            }
        }

        return new Pair<Value, EvalContext>(result, extendedCtx);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        return interpretCtx(ctx).getFirst();
    }

    @Override
    public Set<String> getFreeVariables() {
        int index = elements.size() - 1;
        Set<String> freeVars = new HashSet<String>();
        while (index >= 0) {
            HasLocation elem = elements.get(index);
            if (elem instanceof VarBinding) {
                VarBinding binding = (VarBinding) elem;
                binding.modFreeVars(freeVars);
            } else if (elem instanceof Expression) {
                freeVars.addAll(((Expression) elem).getFreeVariables());
            } else {
                throw new RuntimeException("invariant broken");
            }
            index--;
        }
        return freeVars;
    }

    /** Works like addExpr, except that if body is a SeqExpr then the elements of the other SeqExpr are appended to this one.
     * This allows us to avoid nested SeqExprs, which makes for a more understandable IL. */
    public void merge(IExpr body) {
        if (body instanceof SeqExpr) {
            SeqExpr bodySE = (SeqExpr) body;
            elements.addAll(bodySE.elements);
        } else {
            addExpr(body);
        }
    }
}
