package wyvern.tools.typedAST;

import wyvern.tools.typedAST.extensions.BooleanConstant;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.typedAST.extensions.StringConstant;
import wyvern.tools.typedAST.extensions.UnitVal;
import wyvern.tools.typedAST.extensions.ValDeclaration;
import wyvern.tools.typedAST.extensions.Variable;

public interface CoreASTVisitor {
	void visit(Fn fn);
	void visit(Invocation invocation);
	void visit(Application application);

	void visit(ValDeclaration valDeclaration);
	void visit(Variable variable);
	
	void visit(IntegerConstant booleanConstant);
	void visit(StringConstant booleanConstant);
	void visit(BooleanConstant booleanConstant);
	void visit(UnitVal unitVal);
}
