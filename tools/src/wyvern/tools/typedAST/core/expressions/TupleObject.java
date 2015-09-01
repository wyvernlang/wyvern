package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.values.TupleValue;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Intersection;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TupleObject extends CachingTypedAST implements CoreAST {
	private TypedAST[] objects;
	private static TypedAST[] typeObj = new TypedAST[0];
	
	public TupleObject(List<TypedAST> objects) {
		this(objects.toArray(typeObj));
	}
	public TupleObject(TypedAST[] objects) {
		this.objects = objects;
		if (objects.length > 0)
			this.location = objects[0].getLocation();
	}
	
	public TupleObject(TypedAST first, TypedAST rest, FileLocation commaLine) {
		if (rest instanceof TupleObject) {
			objects = new TypedAST[((TupleObject) rest).objects.length + 1];
			objects[0] = first;
			for (int i = 1; i < ((TupleObject) rest).objects.length + 1; i++) {
				objects[i] = ((TupleObject) rest).objects[i-1];
			}
		} else {
			this.objects = new TypedAST[] { first, rest };
		}
		this.location = commaLine;
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
	public Value evaluate(EvaluationEnvironment env) {
		Value[] evaluatedResults = new Value[objects.length];
		for (int i = 0; i < objects.length; i++) {
			evaluatedResults[i] = objects[i].evaluate(env);
		}
		return new TupleValue((Tuple)this.getType(), evaluatedResults);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		Type[] newTypes = new Type[objects.length];
		for (int i = 0; i < objects.length; i++) {
			final int sti = i;
			newTypes[i] = objects[i].typecheck(env, expected.map(exp -> {
				if (exp instanceof Tuple) return ((Tuple)exp).getTypeArray()[sti];
				if (exp instanceof Intersection)
					return ((Intersection)exp).getTypes().stream().filter(tpe -> tpe instanceof Tuple).filter(tpe -> ((Tuple)tpe).getTypeArray().length == objects.length).findFirst().get();
				ToolError.reportError(ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH, this, getType().toString(), exp.toString());
				throw new RuntimeException();
			}));
		}
		return new Tuple(newTypes);
	}


	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		int i = 0;
		for (TypedAST object : objects) {
			childMap.put(i++ + "", object);
		}
		return childMap;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new RuntimeException("Cannot codegen a tuple");
    }

    @Override
	public TypedAST doClone(Map<String, TypedAST> newChildren) {
		TypedAST[] objs = new TypedAST[newChildren.size()];
		for (String s : newChildren.keySet()) {
			objs[Integer.parseInt(s)] = newChildren.get(s);
		}
		return new TupleObject(objs);
	}

	public TypedAST[] getObjects() {
		return objects;
	}

	private FileLocation location;
	public FileLocation getLocation() {
		return this.location;
	}
	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
