package wyvern.tools.typedAST;

import wyvern.tools.typedAST.extensions.BooleanConstant;
import wyvern.tools.typedAST.extensions.ClassDeclaration;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.typedAST.extensions.InterfaceDeclaration;
import wyvern.tools.typedAST.extensions.LetExpr;
import wyvern.tools.typedAST.extensions.Meth;
import wyvern.tools.typedAST.extensions.New;
import wyvern.tools.typedAST.extensions.StringConstant;
import wyvern.tools.typedAST.extensions.TupleObject;
import wyvern.tools.typedAST.extensions.TypeDeclaration;
import wyvern.tools.typedAST.extensions.TypeInstance;
import wyvern.tools.typedAST.extensions.UnitVal;
import wyvern.tools.typedAST.extensions.ValDeclaration;
import wyvern.tools.typedAST.extensions.VarDeclaration;
import wyvern.tools.typedAST.extensions.Variable;

public interface CoreASTVisitor {
	void visit(Fn fn);
	void visit(Invocation invocation);
	void visit(Application application);

	void visit(ValDeclaration valDeclaration);
	void visit(Variable variable);
	
	void visit(TypeDeclaration typeDeclaration);
	
	void visit(IntegerConstant booleanConstant);
	void visit(StringConstant booleanConstant);
	void visit(BooleanConstant booleanConstant);
	void visit(UnitVal unitVal);
	void visit(ClassDeclaration clsDeclaration);
	void visit(New new1);
	void visit(LetExpr let);
	void visit(Meth meth);
	void visit(TupleObject meth);
	void visit(TypeInstance typeInstance);

	void visit(VarDeclaration valDeclaration);
	void visit(InterfaceDeclaration interfaceDeclaration);
}
