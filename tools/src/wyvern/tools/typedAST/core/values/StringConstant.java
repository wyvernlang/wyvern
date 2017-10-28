package wyvern.tools.typedAST.core.values;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public class StringConstant extends AbstractValue implements InvokableValue, CoreAST {
	private String value;
	
	public StringConstant(String s) { this.value = s; }
	public StringConstant(String s, FileLocation loc) { this.value = s; location = loc; }

	public String getValue() {
		return value;
	}
	


	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new StringConstant(value);
	}

    private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		return new StringLiteral(value, location);
	}

    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("StringConstant(");
        sb.append(value);
        sb.append(")");
        return sb;
    }
}
