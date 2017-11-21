package wyvern.tools.typedAST.core.expressions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.values.TupleValue;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Intersection;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.EvaluationEnvironment;

public class TupleObject extends CachingTypedAST implements CoreAST {
	private ExpressionAST[] objects;
	private static ExpressionAST[] typeObj = new ExpressionAST[0];
	
	public TupleObject(List<TypedAST> objects, FileLocation loc) {
		this(objects.toArray(typeObj), loc);
	}
	public TupleObject(TypedAST[] objects, FileLocation loc) {
		this.location = loc;
		this.objects = Arrays.copyOf(objects, objects.length, ExpressionAST[].class); 
		/*if (objects.length > 0)
			this.location = objects[0].getLocation();*/
	}
	
	public ExpressionAST getObject(int index) {
		return (ExpressionAST) objects[index];
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
	public ExpressionAST doClone(Map<String, TypedAST> newChildren) {
    	ExpressionAST[] objs = new ExpressionAST[newChildren.size()];
		for (String s : newChildren.keySet()) {
			objs[Integer.parseInt(s)] = (ExpressionAST)newChildren.get(s);
		}
		return new TupleObject(objs, location);
	}

	public ExpressionAST[] getObjects() {
		return objects;
	}

	private FileLocation location;
	public FileLocation getLocation() {
		return this.location;
	}
	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
