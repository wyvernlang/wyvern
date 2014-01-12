package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class UnitVal extends AbstractValue implements Value, CoreAST {
	private UnitVal(FileLocation location) { this.location = location; }
	// private static UnitVal instance = new UnitVal(); // FIXME: I have to move away from instance to provide line number! :(
	public static UnitVal getInstance(FileLocation fileLocation) {
		return new UnitVal(fileLocation); // instance; 
	}
	
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

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new UnitVal(location);
	}

	private FileLocation location;
	public FileLocation getLocation() {
		return this.location;
	}
}
