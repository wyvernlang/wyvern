package wyvern.tools.typedAST;

import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.LetExpr;
import wyvern.tools.typedAST.extensions.Meth;
import wyvern.tools.typedAST.extensions.New;
import wyvern.tools.typedAST.extensions.TupleObject;
import wyvern.tools.typedAST.extensions.TypeInstance;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.typedAST.extensions.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.declarations.InterfaceDeclaration;
import wyvern.tools.typedAST.extensions.declarations.TypeDeclaration;
import wyvern.tools.typedAST.extensions.declarations.ValDeclaration;
import wyvern.tools.typedAST.extensions.declarations.VarDeclaration;
import wyvern.tools.typedAST.extensions.values.BooleanConstant;
import wyvern.tools.typedAST.extensions.values.IntegerConstant;
import wyvern.tools.typedAST.extensions.values.StringConstant;
import wyvern.tools.typedAST.extensions.values.UnitVal;

public interface CoreASTVisitor {
	void visit(Fn fn);
	void visit(Invocation invocation);
	void visit(Application application);

	void visit(ValDeclaration valDeclaration);
	void visit(VarDeclaration valDeclaration);
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
	void visit(Assignment assignment);
	void visit(InterfaceDeclaration interfaceDeclaration);
}
