package wyvern.tools.typedAST.core.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;

public class VarValue extends AbstractValue {
	private Value innerValue;

	public VarValue(Value initial) {
		this.innerValue = initial;
	}
	
	public void setValue(Value newV) {
        //Assume typecheck alright
		innerValue = newV;
	}

	@Override
	public Type getType() {
		return innerValue.getType();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		childMap.put("inner", innerValue);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new VarValue((Value)newChildren.get("inner"));
	}

	public Value getValue() {
		return innerValue;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location; //TODO
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
