package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.IfExpr;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.TypeInstance;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.expressions.WhileStatement;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;

public interface CoreASTVisitor {
	void visit(Fn fn);
	void visit(Invocation invocation);
	void visit(Application application);
	void visit(ValDeclaration valDeclaration);
	void visit(VarDeclaration valDeclaration);
	void visit(Variable variable);
	void visit(IntegerConstant booleanConstant);
	void visit(StringConstant booleanConstant);
	void visit(BooleanConstant booleanConstant);
	void visit(UnitVal unitVal);
	void visit(ClassDeclaration clsDeclaration);
	void visit(New new1);
	void visit(LetExpr let);
	void visit(DefDeclaration meth);
	void visit(TupleObject meth);
	void visit(TypeInstance typeInstance);
	void visit(Assignment assignment);
	void visit(TypeDeclaration interfaceDeclaration);
	void visit(Sequence sequence);
	void visit(IfExpr ifExpr);
	void visit(WhileStatement whileStatement);
	void visit(ImportDeclaration importDeclaration);
}
