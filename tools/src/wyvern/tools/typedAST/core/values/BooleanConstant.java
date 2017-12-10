package wyvern.tools.typedAST.core.values;

import java.util.List;

import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;

public class BooleanConstant extends AbstractValue implements InvokableValue, CoreAST {
	private boolean value;
	
	public BooleanConstant(boolean b) {
		this.value = b;
	}

	@Override
	public Type getType() {
		return new Bool(this.getLocation());
	}

	public boolean getValue() {
		return this.value;
	}

    private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		return new BooleanLiteral(value);
	}
}
