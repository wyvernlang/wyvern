package wyvern.target.corewyvernIL.expression;

import static wyvern.tools.errors.ToolError.reportError;

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
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class SeqExpr extends Expression {
	private LinkedList<HasLocation> elements; // either a VarBinding or an Expression
	
	public SeqExpr() {
	    //setExprType(Util.unitType()); // default to Unit
	    elements = new LinkedList<HasLocation>();
	}
	
	public void addExpr(IExpr expr) {
	    elements.addLast(expr);
	}

    public void addBinding(VarBinding binding) {
        elements.addLast(binding);
    }
    public void addBinding(String varName, ValueType type, IExpr toReplace) {
        addBinding(new VarBinding(varName, type, toReplace));
    }

    public List<HasLocation> getElements() {
        return Collections.unmodifiableList(elements);
    }
    
	@Override
	public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
	    TypeContext extendedCtx = ctx;
	    ValueType result = Util.unitType();
	    for (HasLocation elem : elements) {
	        if (elem instanceof VarBinding) {
	            VarBinding binding = (VarBinding) elem;
	            extendedCtx = binding.typecheck(extendedCtx, effectAccumulator);
	            //TODO: make this Unit
	            result = binding.getType();//Util.unitType();
	        } else if (elem instanceof Expression) {
	            result = ((Expression)elem).typeCheck(extendedCtx, effectAccumulator); 
	        } else {
	            throw new RuntimeException("invariant broken");
	        }
	    }
        for (int i = elements.size()-1; i >= 0; --i) {
            HasLocation elem = elements.get(i);
            if (elem instanceof VarBinding)
                result = result.avoid(((VarBinding)elem).getVarName(), extendedCtx);
        }
	    if (getExprType() == null) {
	        setExprType(result);
	    } else if (!result.isSubtypeOf(getExprType(), extendedCtx)) {
            ToolError.reportError(ErrorMessage.NOT_SUBTYPE, getLocation(), result.toString(), getExprType().toString());
	    }
		return getExprType();
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
                dest.append(indent);
                ((Expression)elem).doPrettyPrint(dest, newIndent); 
            } else {
                dest.append(indent).append("unexpected item in sequence!\n");
            }
        }
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
	    EvalContext extendedCtx = ctx;
        Value result = Util.unitValue();
        for (HasLocation elem : elements) {
            if (elem instanceof VarBinding) {
                VarBinding binding = (VarBinding) elem;
                extendedCtx = binding.interpret(extendedCtx);
                // TODO: return unit
                result = extendedCtx.lookupValue(binding.getVarName());//Util.unitValue();
            } else if (elem instanceof Expression) {
                result = ((Expression)elem).interpret(extendedCtx);
            } else {
                throw new RuntimeException("invariant broken");
            }
        }
        
		return result;
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
                freeVars.addAll(((Expression)elem).getFreeVariables());
            } else {
                throw new RuntimeException("invariant broken");
            }
            index--;
	    }
		return freeVars;
	}
}
