package wyvern.tools.typedAST.core.values;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("Cannot generate code for runtime values", FileLocation.UNKNOWN);
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(innerValue);
	}
	
	public Value getValue() {
		return innerValue;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location; //TODO
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
