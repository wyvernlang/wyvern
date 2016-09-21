package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY2;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Str extends AbstractTypeImpl implements OperatableType {
	public Str() {}

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
				return new Bool();
			if (!(type2.subtype(new Str())))
				reportError(OPERATOR_DOES_NOT_APPLY2, opExp, operatorName, this.toString(), type2.toString());
			return new Bool();
		}
		
		return this;
	}

	@Override
	public String toString() {
		return "Str";
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (other instanceof JavaClassType)
			return (((JavaClassType) other).getInnerClass().isAssignableFrom(String.class));

		return other instanceof Str;
	}
	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}

    @Override
    public ValueType generateILType() {
        throw new WyvernException("Primitive type conversion unimplmented"); //TODO
    }

    @Override
	public boolean equals(Object other) { return other instanceof Str; }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
