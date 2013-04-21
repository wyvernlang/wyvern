package wyvern.tools.typedAST.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.Unit;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.PartialDeclSequence;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Sequence implements CoreAST, Iterable<TypedAST> {
	private LinkedList<TypedAST> exps = new LinkedList<TypedAST>();
	private Type retType = null;
	
	public Sequence(TypedAST first) {
		exps.add(first);
	}
	public Sequence(Iterable<TypedAST> first) {
		exps = new LinkedList<TypedAST>();
		for (TypedAST elem : first)
			exps.add(elem);
	}
	public Sequence() {
		// TODO Auto-generated constructor stub
	}
	public void append(TypedAST exp) {
		this.exps.add(exp);
	}
	
	@Override
	public Type getType() {
		if (retType == null)
			ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, this);
		return retType;
	}

	@Override
	public Type typecheck(Environment env) {
		Type lastType = wyvern.tools.types.extensions.Unit.getInstance();
		for (TypedAST t : exps) {
			lastType = t.typecheck(env);
			if (t instanceof EnvironmentExtender)
				env = ((EnvironmentExtender) t).extend(env);
		}
		retType = lastType;
		return lastType;
	}

	@Override
	public Value evaluate(Environment env) {
		Environment iEnv = env;
		Value lastVal = UnitVal.getInstance(this.getLocation());
		for (TypedAST exp : this) {
			if (exp instanceof EnvironmentExtender) {
				iEnv = ((EnvironmentExtender)exp).evalDecl(iEnv);
			} else {
				lastVal = exp.evaluate(iEnv);
			}
		}
		return lastVal;
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

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	public String toString() {
		return this.exps.toString();
	}

	@Override
	public Iterator<TypedAST> iterator() {
		return exps.iterator();
	}
	
	public static Sequence fromAST(Sequence s) {
		return s;
	}
	
	public static Sequence fromAST(TypedAST s) {
		return new Sequence(s);
	}
	
	public Iterable<Declaration> getDeclIterator() {
		final Iterator<TypedAST> inner = iterator();
		return new Iterable<Declaration>() {
			
			@Override
			public Iterator<Declaration> iterator() {
				return new Iterator<Declaration>() {

					@Override
					public boolean hasNext() {
						// TODO Auto-generated method stub
						return inner.hasNext();
					}

					@Override
					public Declaration next() {
						// TODO Auto-generated method stub
						return (Declaration)inner.next();
					}

					@Override
					public void remove() {
						inner.remove();
						
					}
				};
			}
			
		};
	}
	public TypedAST getLast() {
		return exps.getLast();
	}
	public int size() {
		return exps.size();
	}
}