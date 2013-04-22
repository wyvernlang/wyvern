package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;

import wyvern.tools.typedAST.core.Application;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Arrow extends AbstractTypeImpl implements ApplyableType {
	private Type result;
	private Type argument;
	
	public Arrow(Type argument, Type result) {
		this.argument = argument;
		this.result = result;
	}

	Type getResult() {
		return result;
	}
	
	Type getArgument() {
		return argument;
	}
	
	@Override
	public Type checkApplication(Application application, Environment env) {
		Type actualType = application.getArgument().typecheck(env);
		if (!actualType.subtype(argument))
			reportError(ACTUAL_FORMAL_TYPE_MISMATCH, application);
		return result;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(argument, result);		
	}
	
	@Override
	public String toString() {
		String argString = (argument == null)?null:argument.toString();
		if (argument instanceof Arrow)
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
}