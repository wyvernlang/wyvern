package wyvern.target.corewyvernIL;

import java.io.IOException;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class FormalArg extends ASTNode implements IASTNode {

	private String name;
	private ValueType type;
	
	public FormalArg(String name, ValueType type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(name).append(':');
		type.doPrettyPrint(dest, indent);
	}

	public String getName() {
		return name;
	}
	
	public ValueType getType() {
		return type;
	}
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}
}
