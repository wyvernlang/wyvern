package wyvern.tools.typedAST.extensions.values;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class UnitVal extends AbstractValue implements Value, CoreAST {
	private UnitVal() { }
	private static UnitVal instance = new UnitVal();
	public static UnitVal getInstance() { return instance; }
	
	@Override
	public Type getType() {
		return Unit.getInstance();
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "()";
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

}
