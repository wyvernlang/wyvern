package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WhileStatement implements TypedAST, CoreAST {

	private TypedAST conditional;
	private TypedAST body;
	private FileLocation location;

	public WhileStatement(TypedAST conditional, TypedAST body,
			FileLocation location) {
		this.conditional = conditional;
		this.body = body;
		this.location = location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public Type getType() {
		return new Unit();
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		if (!(conditional.typecheck(env, Optional.empty()) instanceof Bool))
			ToolError.reportError(ErrorMessage.TYPE_CANNOT_BE_APPLIED, conditional);
		
		body.typecheck(env, Optional.empty());
		return new Unit();
	}
	
	private boolean evaluateConditional(EvaluationEnvironment env) {
		return ((BooleanConstant)conditional.evaluate(env)).getValue();
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		while (evaluateConditional(env)) {
			body.evaluate(env);
		}
		return UnitVal.getInstance(this.getLocation());
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	public TypedAST getConditional() {
		return conditional;
	}
	
	public TypedAST getBody() {
		return body;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		childMap.put("cond", conditional);
		childMap.put("body", body);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new WhileStatement(newChildren.get("cond"), newChildren.get("body"), location);
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new RuntimeException("Not implemented codegen for whilestatement"); //TODO remove this
    }

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
