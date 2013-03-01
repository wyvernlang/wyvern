package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.InvokableValue;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.extensions.values.TupleValue;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.TreeWriter;

public class TupleObject extends CachingTypedAST implements CoreAST {
	private TypedAST[] objects;
	
	public TupleObject(TypedAST[] objects) {
		this.objects = objects;
	}
	
	public TupleObject(TypedAST first, TypedAST rest) {
		if (rest instanceof TupleObject) {
			objects = new TypedAST[((TupleObject) rest).objects.length + 1];
			objects[0] = first;
			for (int i = 0; i < ((TupleObject) rest).objects.length; i++) {
				objects[i] = ((TupleObject) rest).objects[i];
			}
		} else {
			this.objects = new TypedAST[] { first, rest };
		}
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(objects);	
	}
	
	public TypedAST getObject(int index) {
		return objects[index];
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Value evaluate(Environment env) {
		Value[] evaluatedResults = new Value[objects.length];
		for (int i = 0; i < objects.length; i++) {
			evaluatedResults[i] = objects[i].evaluate(env);
		}
		return new TupleValue((Tuple)this.getType(), evaluatedResults);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		Type[] newTypes = new Type[objects.length];
		for (int i = 0; i < objects.length; i++) {
			newTypes[i] = objects[i].typecheck(env);
		}
		return new Tuple(newTypes);
	}

	public TypedAST[] getObjects() {
		return objects;
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
