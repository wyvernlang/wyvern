package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;

public class Arrow extends AbstractTypeImpl implements ApplyableType {
	private Type result;
	private Type argument;
	
	public Arrow(Type argument, Type result) {
		this.argument = argument;
		this.result = result;
	}

	public Type getResult() {
		return result;
	}
	
	public Type getArgument() {
		return argument;
	}
	
	@Override
	public Type checkApplication(Application application, Environment env) {
		Type actualType = application.getArgument().typecheck(env, Optional.of(argument));
		argument = TypeResolver.resolve(argument, env);
		
		// System.out.println(argument); //FIXME:
		
		if (!actualType.subtype(argument))
			reportError(ACTUAL_FORMAL_TYPE_MISMATCH, application,actualType.toString(),argument.toString());
		return result;
	}
	
	@Override
	public String toString() {
		String argString = (argument == null)?null:argument.toString();
		if (!argument.isSimple())
			argString = "(" + argString + ")";
		return argString + " -> " + result;
	}
	
	@Override
	public boolean equals(Object otherT) {
		if (!(otherT instanceof Arrow))
			return false;
		Arrow otherAT = (Arrow) otherT; 
		return argument.equals(otherAT.argument) && result.equals(otherAT.result);
	}
	
	@Override
	public int hashCode() {
		return 37*argument.hashCode()+result.hashCode();
	}	

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (super.subtype(other, subtypes)) {
			return true;
		}
		
		if (other instanceof Arrow) {
			Arrow oa = (Arrow) other;
			
			return 	oa.argument.subtype(this.argument, subtypes) &&
					this.result.subtype(oa.result, subtypes);
		} else {
			return false;
		}
	}
	@Override
	public boolean isSimple() {
		return false;
	}
	@Override
	public Map<String, Type> getChildren() {
		HashMap<String, Type> map = new HashMap<>();
		map.put("result", result);
		map.put("argument", argument);
		return map;
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return new Arrow(newChildren.get("argument"), newChildren.get("result"));
	}

	@Override
	public Type cloneWithBinding(TypeBinding binding) {
		return null;
	}

    @Override
    @Deprecated
    public ValueType generateILType() {
        return new StructuralType(Fn.LAMBDA_STRUCTUAL_DECL, Arrays.asList(new DefDeclType(Util.APPLY_NAME, result.generateILType(), Arrays.asList(new FormalArg("arg1", argument.generateILType())))));
    }

    static final ValueType nominalUnit = new NominalType("system", "Unit"); 
    
	@Override
	public ValueType getILType(GenContext ctx) {
		List<FormalArg> formals = new LinkedList<FormalArg>();
		if (argument instanceof Tuple) {
		    Tuple tuple = (Tuple) argument;
		    for (int i = 0; i < tuple.getTypeArray().length; ++i) {
                formals.add(new FormalArg("arg" + i, tuple.getTypeArray()[i].getILType(ctx)));
		    }
		} else {
	        final ValueType argType = argument.getILType(ctx);
	        if (!Util.unitType().equals(argType) && !nominalUnit.equals(argType)) {
	            // it's a real argument, add it to the list
	            formals.add(new FormalArg("arg1", argType));
	        }
		}
		return new StructuralType(Fn.LAMBDA_STRUCTUAL_DECL, Arrays.asList(new DefDeclType(Util.APPLY_NAME, result.getILType(ctx), formals)));
	}
}
