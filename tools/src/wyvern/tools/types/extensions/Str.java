package wyvern.tools.types.extensions;

import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.*;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY2;
import static wyvern.tools.errors.ToolError.reportError;

public class Str extends AbstractTypeImpl implements OperatableType {
	private Str() {}
	private static Str instance = new Str();
	public static Str getInstance() { return instance; }

	private static final Set<String> legalOperators = new HashSet<String>(Arrays.asList(new String[] {"+", "=="}));
	
	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		String operatorName = opExp.getOperationName();

		
		if (!(legalOperators.contains(operatorName))) {
			JavaClassDecl strDecl = Util.javaToWyvDecl(String.class);
			try {
				return strDecl.getObjType().checkOperator(opExp, env);
			} catch (Exception e) {
				reportError(OPERATOR_DOES_NOT_APPLY, opExp, operatorName, this.toString());
			}
		}
		Type type2 = opExp.getArgument().typecheck(env, Optional.empty());

		if (operatorName.equals("+"))
			if (!((type2 instanceof Str) || (type2 instanceof Int)))
				reportError(OPERATOR_DOES_NOT_APPLY2, opExp, operatorName, this.toString(), type2.toString());
		if (operatorName.equals("==")) {
			if (type2 instanceof JavaClassType && String.class.isAssignableFrom(((JavaClassType)type2).getInnerClass()))
				return Bool.getInstance();
			if (!(type2.subtype(Str.getInstance())))
				reportError(OPERATOR_DOES_NOT_APPLY2, opExp, operatorName, this.toString(), type2.toString());
			return Bool.getInstance();
		}
		
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
//		nothing to write
	}

	@Override
	public String toString() {
		return "Str";
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (other instanceof JavaClassType)
			return (((JavaClassType) other).getInnerClass().isAssignableFrom(String.class));

		return super.subtype(other, subtypes);
	}
	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}
}