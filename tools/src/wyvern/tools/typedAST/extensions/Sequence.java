package wyvern.tools.typedAST.extensions;

import java.util.ArrayList;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Sequence implements CoreAST {
	private ArrayList<TypedAST> exps;
	
	public Sequence(TypedAST first) {
		exps = new ArrayList<TypedAST>();
		exps.add(first);
	}
	
	public void append(TypedAST exp) {
		this.exps.add(exp);
	}
	
	@Override
	public Type getType() {
		return null; // FIXME:
	}

	@Override
	public Type typecheck(Environment env) {
		for (TypedAST t : exps) {
			t.typecheck(env);
		}
		return null; // FIXME:
	}

	@Override
	public Value evaluate(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LineParser getLineParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
	}

	private int line = -1;
	@Override
	public int getLine() {
		return this.line;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		return this.exps.toString();
	}

}