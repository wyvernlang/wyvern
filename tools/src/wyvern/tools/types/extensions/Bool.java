package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.*;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY2;
import static wyvern.tools.errors.ToolError.reportError;

public class Bool extends AbstractTypeImpl implements OperatableType {
	public Bool(FileLocation location) { super(location); }
	public Bool() { }

	private static final Set<String> legalOperators = new HashSet<String>(Arrays.asList(new String[] {
			"&&",
			"||",
	}));

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		Type type2 = opExp.getArgument().typecheck(env, Optional.empty());
		String operatorName = opExp.getOperationName();
		
		if (!(legalOperators.contains(operatorName)))
			reportError(OPERATOR_DOES_NOT_APPLY, opExp, operatorName, this.toString());
		
		if (!(type2 instanceof Bool))
			reportError(OPERATOR_DOES_NOT_APPLY2, opExp, operatorName, this.toString(), type2.toString());
		
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
		// POTANIN: At this stage as this was based on Int class, I assume there is nothing to write.
	}

	@Override
	public String toString() {
		return "Bool";
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return other instanceof Bool;
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
        throw new WyvernException("Boolean primitive not implemented", FileLocation.UNKNOWN); //TODO
    }

    @Override
	public boolean equals(Object other) { return other instanceof Bool; }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}