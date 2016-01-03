package wyvern.target.corewyvernIL.astvisitor;

import java.util.List;
import java.util.Vector;

import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Literal;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIRBinding;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRNameBinding;
import wyvern.target.oir.OIRProgram;
import wyvern.target.oir.OIRTypeBinding;
import wyvern.target.oir.expressions.*;
import wyvern.target.oir.declarations.*;

public class EmitOIRVisitor extends ASTVisitor<OIRAST> {
	private int classCount = 0;
	private int interfaceCount = 0;
	
	private String generateClassName ()
	{
		classCount++;
		return "Class"+classCount;
	}
	
	private String generateInterfaceName ()
	{
		interfaceCount++;
		return "Interface"+interfaceCount;
	}
	
	public OIRAST visit(Environment env, OIREnvironment oirenv, New newExpr) {
		OIRClassDeclaration cd;
		ValueType exprType;
		OIRType oirtype;
		OIREnvironment classenv;
		List<OIRMemberDeclaration> oirMemDecls;
		String className;
		List<OIRDelegate> delegates;
		OIRTypeBinding oirTypeBinding;
		OIRExpression oirexpr;
		List<OIRFieldValueInitializePair> fieldValuePairs;
		List<OIRExpression> args;
		
		exprType = newExpr.getExprType();
		if (exprType != null)
			oirtype = (OIRType) exprType.acceptVisitor(this, env, oirenv);
		classenv = new OIREnvironment (oirenv);
		oirMemDecls = new Vector<OIRMemberDeclaration> ();
		delegates = new Vector<OIRDelegate> ();
		fieldValuePairs = new Vector<OIRFieldValueInitializePair> ();
		args = new Vector<OIRExpression> ();
		
		for (Declaration decl : newExpr.getDecls())
		{
			if (decl instanceof DelegateDeclaration)
			{
				OIRDelegate oirdelegate;
				
				oirdelegate = (OIRDelegate)decl.acceptVisitor(this, env,
						classenv);
				delegates.add(oirdelegate);
			}
			else
			{
				OIRMemberDeclaration oirMemDecl;
				
				oirMemDecl = (OIRMemberDeclaration) decl.acceptVisitor(this,
						env, classenv);
				
				if (decl instanceof VarDeclaration)
				{
					OIRExpression oirvalue;
					OIRFieldValueInitializePair pair;
					VarDeclaration varDecl;
					
					varDecl = (VarDeclaration)decl;
					oirvalue = (OIRExpression) varDecl.acceptVisitor(this,
							env, oirenv);
					pair = new OIRFieldValueInitializePair (
							(OIRFieldDeclaration)oirMemDecl, oirvalue);
					fieldValuePairs.add (pair);
					args.add((OIRExpression) oirvalue);
				}
				else if (decl instanceof ValDeclaration)
				{
					OIRExpression oirvalue;
					OIRFieldValueInitializePair pair;
					ValDeclaration varDecl;
					
					varDecl = (ValDeclaration)decl;
					oirvalue = (OIRExpression) varDecl.acceptVisitor(this,
							env, oirenv);
					pair = new OIRFieldValueInitializePair (
							(OIRFieldDeclaration)oirMemDecl, oirvalue);
					fieldValuePairs.add (pair);
					args.add((OIRExpression) oirvalue);
				}
				
				classenv.addName(oirMemDecl.getName (), oirMemDecl.getType ());
				oirMemDecls.add(oirMemDecl);
			}
		}
		
		className = generateClassName ();
		cd = new OIRClassDeclaration (classenv, className, newExpr.getSelfName(),
				delegates, oirMemDecls, fieldValuePairs);
		oirenv.addType(className, cd);
		classenv.addName(newExpr.getSelfName(), cd);
		OIRProgram.program.addTypeDeclaration(cd);
		oirexpr = new OIRNew (args, className);
		
		return oirexpr;
	}
	
	public OIRAST visit(Environment env, OIREnvironment oirenv, MethodCall methodCall) {
		OIRExpression oirbody;
		Expression body;
		List<OIRExpression> args;
		OIRMethodCall oirMethodCall;
		
		args =	new Vector<OIRExpression> ();
		
		for (Expression e : methodCall.getArgs())
		{
			args.add ((OIRExpression)e.acceptVisitor(this, env, oirenv));
		}
		
		body = methodCall.getObjectExpr();
		
		oirbody = (OIRExpression)body.acceptVisitor(this, env, oirenv);
		oirMethodCall = new OIRMethodCall (oirbody, 
				methodCall.getMethodName(),  args);
		
		return oirMethodCall;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, Match match) {
		OIRLet oirParentLet;
		OIRIfThenElse oirIfExpr;
		OIRLet oirThenLet;
		OIRExpression oirElseExpr;
		OIRExpression oirMatchExpr;
		
		oirMatchExpr = (OIRExpression) match.getMatchExpr().acceptVisitor(this,
				env, oirenv);
		oirElseExpr = (OIRExpression) match.getElseExpr().acceptVisitor(this, 
				env, oirenv);
		
		/* Build the let in if let in else let in if chain bottom up */
		for (int i = match.getCases().size() - 1; i >= 0; i--)
		{
			Case matchCase;
			OIRExpression oirTag;
			OIRExpression body;
			OIRExpression condition;
			List<OIRExpression> arg;
			
			matchCase = match.getCases().get(i);
			body = (OIRExpression)matchCase.getBody().acceptVisitor(this, 
					env, oirenv);
			oirTag = (OIRExpression)matchCase.getPattern().acceptVisitor(
					this, env, oirenv);
			oirThenLet = new OIRLet (matchCase.getVarName(), oirMatchExpr, body);
			arg = new Vector<OIRExpression> ();
			arg.add(oirTag);
			condition = new OIRMethodCall (new OIRFieldGet (
					new OIRVariable ("tmp"), "tag"),
					"isSubtag", arg);
			oirIfExpr = new OIRIfThenElse (condition, oirThenLet, oirElseExpr);
			oirElseExpr = oirIfExpr;
		}
		
		oirParentLet = new OIRLet ("tmp", oirMatchExpr, oirElseExpr);
		return oirParentLet;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, FieldGet fieldGet) {
		OIRFieldGet oirFieldGet;
		OIRExpression oirObject;
		Expression object;
		
		object = (Expression) fieldGet.getObjectExpr();
		oirObject = (OIRExpression) object.acceptVisitor(this, 
				env, oirenv);
		oirFieldGet = new OIRFieldGet (oirObject, fieldGet.getName());
		
		return oirFieldGet;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, Let let) {

		OIRLet oirLet;
		OIRExpression oirToReplace;
		OIRExpression oirInExpr;
		Expression toReplace;
		Expression inExpr;
		
		toReplace = let.getToReplace();
		oirToReplace = (OIRExpression)toReplace.acceptVisitor(this, 
				env, oirenv);
		oirenv.addName(let.getVarName(), oirToReplace.typeCheck(oirenv));
		inExpr = let.getInExpr();
		oirInExpr = (OIRExpression)inExpr.acceptVisitor(this, env, oirenv);
		oirLet = new OIRLet (let.getVarName(), oirToReplace, oirInExpr);
		
		return oirLet;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, FieldSet fieldSet) {
		OIRFieldSet oirFieldSet;
		OIRExpression oirObject;
		OIRExpression oirToSet;
		Expression object;
		Expression toSet;
		
		object = (Expression) fieldSet.getObjectExpr();
		toSet = fieldSet.getExprToAssign();
		oirObject = (OIRExpression) object.acceptVisitor(this, env,
				oirenv);
		oirToSet = (OIRExpression) toSet.acceptVisitor(this, env, 
				oirenv);
		oirFieldSet = new OIRFieldSet (oirObject, fieldSet.getFieldName(), 
				oirToSet);
		
		return oirFieldSet;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, Variable variable) {
		OIRVariable oirVar;
		OIRType oirType;
		ValueType type;
		
		oirVar = new OIRVariable (variable.getName());
		
		return oirVar;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, Cast cast) {
		OIRCast oirCast;
		OIRType oirType;
		OIRExpression oirExpr;
		Expression expr;
		
		expr = cast.getToCastExpr();
		oirExpr = (OIRExpression)expr.acceptVisitor(this, env, oirenv);
		oirType = (OIRType)cast.getExprType().acceptVisitor(this, env,
				oirenv);
		oirCast = new OIRCast (oirExpr, oirType);
		
		return oirCast;
	}

	
	public OIRAST visit(Environment env, OIREnvironment oirenv, VarDeclaration varDecl) {
		OIRFieldDeclaration oirMember;
		OIRType type;
		ValueType _type;
		
		_type = varDecl.getType();
		type = (OIRType)_type.acceptVisitor(this, env, oirenv);
		oirMember = new OIRFieldDeclaration (varDecl.getName(), type);
		return oirMember;
	}
	
	public OIRAST visit(Environment env, OIREnvironment oirenv, DefDeclaration defDecl) {
		OIRMethodDeclaration oirMethodDecl;
		OIRMethod oirMethod;
		OIRType oirReturnType;
		List<OIRFormalArg> listOIRFormalArgs;
		OIRExpression oirBody;
		OIREnvironment defEnv;
		
		listOIRFormalArgs = new Vector <OIRFormalArg> ();
		defEnv = new OIREnvironment (oirenv);

		for (FormalArg arg : defDecl.getFormalArgs())
		{
			OIRFormalArg formalArg;
			
			formalArg = (OIRFormalArg) arg.acceptVisitor(this, env,
					oirenv);
			defEnv.addName(formalArg.getName(), formalArg.getType());
			listOIRFormalArgs.add(formalArg);
		}
		
		oirReturnType = (OIRType) defDecl.getType().acceptVisitor(this,
				env, oirenv);
		oirMethodDecl = new OIRMethodDeclaration (oirReturnType, 
				defDecl.getName(), listOIRFormalArgs);
		oirBody = (OIRExpression) defDecl.getBody().acceptVisitor(this,
				env, defEnv);
		oirMethod = new OIRMethod (defEnv, oirMethodDecl, oirBody);
		
		return oirMethod;
	}

	public OIRAST visit(Environment env, OIREnvironment oirenv, ValDeclaration valDecl) {
		OIRFieldDeclaration oirMember;
		OIRType type;
		ValueType _type;

		_type = valDecl.getType();
		type = (OIRType)_type.acceptVisitor(this, env, oirenv);
		oirMember = new OIRFieldDeclaration (valDecl.getName(), type, true);
		
		return oirMember;
	}


	public OIRAST visit(Environment env, OIREnvironment oirenv,
			IntegerLiteral integerLiteral) {
		
		return new OIRInteger (integerLiteral.getValue());
	}


	public OIRAST visit(Environment env, OIREnvironment oirenv,
			RationalLiteral rational) {
		return new OIRRational (rational.getNumerator(), 
				rational.getDenominator());
	}


	public OIRAST visit(Environment env, OIREnvironment oirenv,
			FormalArg formalArg) {
		OIRType oirtype;
		OIRFormalArg oirarg;
		
		oirtype = (OIRType) formalArg.getType().acceptVisitor(this, 
				env, oirenv);
		oirarg = new OIRFormalArg (formalArg.getName(), oirtype);
		return oirarg;
	}


	public OIRAST visit(Environment env, OIREnvironment oirenv,
			VarDeclType varDeclType) {
		OIRInterface oirtype;
		ValueType type;
		OIRMethodDeclarationGroup methoDecls;
		List<OIRFormalArg> args;
		String fieldName;
		
		fieldName = varDeclType.getName();
		methoDecls = new OIRMethodDeclarationGroup ();
		type = varDeclType.getRawResultType();
		oirtype = (OIRInterface)type.acceptVisitor(this, env, oirenv);
		methoDecls.addMethodDeclaration(new OIRMethodDeclaration (oirtype, 
				"get"+fieldName, null));
		args = new Vector<OIRFormalArg> ();
		args.add(new OIRFormalArg ("_"+fieldName, oirtype));
		methoDecls.addMethodDeclaration(new OIRMethodDeclaration (null, 
				"set"+fieldName, args));
		
		return methoDecls;
	}

	public OIRAST visit(Environment env, OIREnvironment oirenv,
			ValDeclType valDeclType) {
		OIRInterface oirtype;
		ValueType type;
		OIRMethodDeclaration methodDecl;
		OIRMethodDeclarationGroup methoDecls;
		
		type = valDeclType.getRawResultType();
		oirtype = (OIRInterface)type.acceptVisitor(this, env, oirenv);
		methodDecl = new OIRMethodDeclaration (oirtype, 
				"set"+valDeclType.getName(), null);
		methoDecls = new OIRMethodDeclarationGroup ();
		methoDecls.addMethodDeclaration(methodDecl);
		
		return methoDecls;
	}


	public OIRAST visit(Environment env, OIREnvironment oirenv,
			DefDeclType defDeclType) {
		OIRMethodDeclaration oirMethodDecl;
		OIRType oirReturnType;
		List<OIRFormalArg> listOIRFormalArgs;
		ValueType returnType;
		OIRMethodDeclarationGroup methodDecls;
		
		listOIRFormalArgs = new Vector <OIRFormalArg> ();
		
		for (FormalArg arg : defDeclType.getFormalArgs())
		{
			OIRFormalArg formalArg;
			
			formalArg = (OIRFormalArg) arg.acceptVisitor(this, env,
					oirenv);
			listOIRFormalArgs.add(formalArg);
		}
		
		returnType = defDeclType.getRawResultType();
		oirReturnType = (OIRType)returnType.acceptVisitor(this,
				env, oirenv);
		oirMethodDecl = new OIRMethodDeclaration (oirReturnType, 
				defDeclType.getName(), listOIRFormalArgs);
		methodDecls = new OIRMethodDeclarationGroup ();
		methodDecls.addMethodDeclaration(oirMethodDecl);
		
		return methodDecls;
	}


	public OIRAST visit(Environment env, OIREnvironment oirenv,
			AbstractTypeMember abstractDeclType) {
		OIRType oirtype;
		OIRMethodDeclaration methDecl;
		OIRMethodDeclarationGroup methodDecls;

		oirtype = (OIRType) abstractDeclType.acceptVisitor(this,
				env, oirenv);
		methDecl = new OIRMethodDeclaration (oirtype, "get"+abstractDeclType.getName(), null);
		methodDecls = new OIRMethodDeclarationGroup ();
		methodDecls.addMethodDeclaration(methDecl);

		return methodDecls;
	}

	public OIRAST visit(Environment env, OIREnvironment oirenv,
			StructuralType structuralType) {
		OIRInterface oirinterface;
		List<OIRMethodDeclaration> methodDecls;
		String interfaceName;
		OIREnvironment oirInterfaceEnv;
		
		interfaceName = generateInterfaceName();
		methodDecls = new Vector<OIRMethodDeclaration> ();
		oirInterfaceEnv = new OIREnvironment (oirenv);
		
		for (DeclType declType : structuralType.getDeclTypes())
		{
			OIRMethodDeclarationGroup declTypeGroup;

			declTypeGroup = (OIRMethodDeclarationGroup) declType.acceptVisitor(
					this, env, oirenv);
			for (int i = 0; i < declTypeGroup.size(); i++)
			{
				oirInterfaceEnv.addName(declTypeGroup.elementAt(i).getName(), 
						declTypeGroup.elementAt(i).getReturnType());
				methodDecls.add (declTypeGroup.elementAt(i));
			}
		}
		
		oirinterface = new OIRInterface (oirInterfaceEnv, interfaceName, 
				structuralType.getSelfName(), methodDecls);
		oirenv.addType(interfaceName, oirinterface);
		OIRProgram.program.addTypeDeclaration(oirinterface);
		
		return oirinterface;
	}

	public OIRAST visit(Environment env, OIREnvironment oirenv,
			NominalType nominalType) {
		OIRFieldGet oirfieldget;
		Path path;
		
		path = nominalType.getPath();
		oirfieldget = (OIRFieldGet) path.acceptVisitor(this, env, oirenv);
		
		return new OIRFieldGet (oirfieldget, nominalType.getTypeMember()+"tag");
	}

	public OIRAST visit(Environment env, OIREnvironment oirenv,
			StringLiteral stringLiteral) {
		OIRString oirstring;
		
		oirstring = new OIRString (stringLiteral.getValue());

		return oirstring;
	}

	public OIRAST visit(Environment env, OIREnvironment oirenv,
			DelegateDeclaration delegateDecl) {
		OIRDelegate oirdelegate;
		OIRType oirtype;
		ValueType type;
		
		type = delegateDecl.getValueType();
		oirtype = (OIRType) type.acceptVisitor(this, env, oirenv);
		oirdelegate = new OIRDelegate (oirtype, delegateDecl.getFieldName());
		
		return oirdelegate;
	}

	@Override
	public OIRAST visit(Environment env, OIREnvironment oirenv, Bind bind) {
		throw new RuntimeException("not implemented");
	}
}
