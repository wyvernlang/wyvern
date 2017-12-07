package wyvern.tools.typedAST.core.expressions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class TupleObject extends AbstractExpressionAST implements CoreAST {
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
