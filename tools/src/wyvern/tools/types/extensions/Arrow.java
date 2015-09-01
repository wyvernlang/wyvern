package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.*;

import static wyvern.tools.errors.ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH;
import static wyvern.tools.errors.ToolError.reportError;

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
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(argument, result);		
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
    public ValueType generateILType() {
        return new StructuralType("this", Arrays.asList(new DefDeclType("call", result.generateILType(), Arrays.asList(new FormalArg("arg1", argument.generateILType())))));
    }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}