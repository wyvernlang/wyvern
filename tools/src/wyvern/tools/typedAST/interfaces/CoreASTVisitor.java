package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.IfExpr;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.core.expressions.Match;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.TypeInstance;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.expressions.WhileStatement;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;

public interface CoreASTVisitor {}
