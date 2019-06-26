package wyvern.target.corewyvernIL.astvisitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ForwardDeclaration;
import wyvern.target.corewyvernIL.decl.EffectDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.RecConstructDeclaration;
import wyvern.target.corewyvernIL.decl.RecDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.CharacterLiteral;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.FloatLiteral;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRProgram;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRForward;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRFieldValueInitializePair;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRIntegerType;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.declarations.OIRMethodDeclarationGroup;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.FFIType;
import wyvern.target.oir.expressions.OIRBoolean;
import wyvern.target.oir.expressions.OIRCast;
import wyvern.target.oir.expressions.OIRCharacter;
import wyvern.target.oir.expressions.OIRExpression;
import wyvern.target.oir.expressions.OIRFFIImport;
import wyvern.target.oir.expressions.OIRFieldGet;
import wyvern.target.oir.expressions.OIRFieldSet;
import wyvern.target.oir.expressions.OIRFloat;
import wyvern.target.oir.expressions.OIRIfThenElse;
import wyvern.target.oir.expressions.OIRInteger;
import wyvern.target.oir.expressions.OIRLet;
import wyvern.target.oir.expressions.OIRMethodCall;
import wyvern.target.oir.expressions.OIRNew;
import wyvern.target.oir.expressions.OIRRational;
import wyvern.target.oir.expressions.OIRString;
import wyvern.target.oir.expressions.OIRVariable;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.interop.FFI;
import wyvern.tools.interop.FFIImport;
import wyvern.tools.util.Pair;

public class EmitOIRVisitor extends ASTVisitor<EmitOIRState, OIRAST> {
    private int classCount = 0;
    private int interfaceCount = 0;

    private String generateClassName() {
        classCount++;
        return "Class" + classCount;
    }

    private String generateInterfaceName() {
        interfaceCount++;
        return "Interface" + interfaceCount;
    }

    public OIRAST visit(EmitOIRState state, New newExpr) {

        // Set up, initialise collections we'll be using.
        OIREnvironment classEnv = new OIREnvironment(state.getEnvironment());
        List<OIRMemberDeclaration> memberDecls = new Vector<OIRMemberDeclaration>();
        List<OIRForward> forwards = new Vector<OIRForward>();
        List<OIRFieldValueInitializePair> fieldValuePairs = new Vector<OIRFieldValueInitializePair>();
        List<OIRExpression> args = new Vector<OIRExpression>();

        // Add the object to the context.
        TypeContext ctx = state.getContext();
        ctx = ctx.extend(newExpr.getSelfSite(), newExpr.typeCheck(state.getContext(), null));

        // Process each declaration.
        for (Declaration decl : newExpr.getDecls()) {

            // Special case: processing a delegate declaration.
            if (decl instanceof ForwardDeclaration) {
                OIRForward oirforward;
                oirforward = (OIRForward) decl.acceptVisitor(this, new EmitOIRState(ctx, classEnv));
                forwards.add(oirforward);
                continue;
            }

            // Otherwise we're processing a declaration for some member of the object.
            OIRMemberDeclaration memberDecl;
            memberDecl = (OIRMemberDeclaration) decl.acceptVisitor(this, new EmitOIRState(ctx, classEnv));

            if (decl instanceof VarDeclaration) {
                VarDeclaration varDecl = (VarDeclaration) decl;
                OIRExpression oirvalue = (OIRExpression) varDecl.getDefinition().acceptVisitor(this,
                        new EmitOIRState(ctx, state.getEnvironment()));
                OIRFieldValueInitializePair pair = new OIRFieldValueInitializePair((OIRFieldDeclaration) memberDecl, oirvalue);
                fieldValuePairs.add(pair);
                args.add(oirvalue);
            } else if (decl instanceof ValDeclaration) {
                ValDeclaration varDecl = (ValDeclaration) decl;
                OIRExpression oirvalue = (OIRExpression) varDecl.getDefinition().acceptVisitor(this,
                        new EmitOIRState(ctx, state.getEnvironment()));
                OIRFieldValueInitializePair pair = new OIRFieldValueInitializePair((OIRFieldDeclaration) memberDecl, oirvalue);
                fieldValuePairs.add(pair);
                args.add(oirvalue);
            }

            // Add the Val/Var decl to the member declarations & the class environment.
            classEnv.addName(memberDecl.getName(), memberDecl.getType());
            memberDecls.add(memberDecl);
        }

        // Generate the OIR expression.
        String className = generateClassName();
        OIRClassDeclaration classDecl = new OIRClassDeclaration(classEnv, className, newExpr.getSelfName(), forwards, memberDecls,
                fieldValuePairs, newExpr.getFreeVariables());
        state.getEnvironment().addType(className, classDecl);
        classEnv.addName(newExpr.getSelfName(), classDecl);
        OIRProgram.PROGRAM.addTypeDeclaration(classDecl);
        OIRExpression oirExpr = new OIRNew(args, className);
        oirExpr.copyMetadata(newExpr);
        return oirExpr;

    }

    public OIRAST visit(EmitOIRState state, MethodCall methodCall) {

        // Process arguments passed to the method call.
        List<OIRExpression> args = new Vector<OIRExpression>();
        for (IExpr e : methodCall.getArgs()) {
            args.add((OIRExpression) e.acceptVisitor(this, state));
        }

        // Process the method body.
        IExpr body = methodCall.getObjectExpr();
        OIRExpression oirbody = (OIRExpression) body.acceptVisitor(this, state);

        // Return the OIRMethodCall.
        OIRMethodCall oirMethodCall = new OIRMethodCall(oirbody, body.typeCheck(state.getContext(), null), methodCall.getMethodName(),
                args);
        oirMethodCall.copyMetadata(methodCall);
        return oirMethodCall;

    }

    public OIRAST visit(EmitOIRState state, Match match) {

        OIRExpression oirMatchExpr = (OIRExpression) match.getMatchExpr().acceptVisitor(this, state);
        OIRExpression oirElseExpr = (OIRExpression) match.getElseExpr().acceptVisitor(this, state);

        /* Build the "let in if let in else let in if chain", from the bottom up */
        for (int i = match.getCases().size() - 1; i >= 0; i--) {
            Case matchCase = match.getCases().get(i);
            OIRExpression body = (OIRExpression) matchCase.getBody().acceptVisitor(this, state);
            OIRExpression oirTag = (OIRExpression) matchCase.getPattern().acceptVisitor(this, state);
            OIRLet oirThenLet = new OIRLet(matchCase.getVarName(), oirMatchExpr, body);
            List<OIRExpression> arg = new Vector<OIRExpression>();
            arg.add(oirTag);
            OIRExpression condition = new OIRMethodCall(new OIRFieldGet(new OIRVariable("tmp"), "tag"), null, "isSubtag", arg);
            OIRIfThenElse oirIfExpr = new OIRIfThenElse(condition, oirThenLet, oirElseExpr);
            oirElseExpr = oirIfExpr;
        }

        OIRLet oirParentLet = new OIRLet("tmp", oirMatchExpr, oirElseExpr);
        oirParentLet.copyMetadata(match);
        return oirParentLet;
    }

    public OIRAST visit(EmitOIRState state, FieldGet fieldGet) {
        IExpr object = fieldGet.getObjectExpr();
        OIRExpression oirObject = (OIRExpression) object.acceptVisitor(this, state);
        OIRFieldGet oirFieldGet = new OIRFieldGet(oirObject, fieldGet.getName());
        oirFieldGet.copyMetadata(fieldGet);
        return oirFieldGet;
    }

    public OIRAST visit(EmitOIRState state, Let let) {
        TypeContext ctx = state.getContext().extend(let.getSite(), let.getVarType());
        IExpr toReplace = let.getToReplace();
        OIRExpression oirToReplace = (OIRExpression) toReplace.acceptVisitor(this,
                new EmitOIRState(ctx, state.getEnvironment()));
        state.getEnvironment().addName(let.getVarName(), null);
        IExpr inExpr = let.getInExpr();
        OIRExpression oirInExpr = (OIRExpression) inExpr.acceptVisitor(this, new EmitOIRState(ctx, state.getEnvironment()));
        OIRLet oirLet = new OIRLet(let.getVarName(), oirToReplace, oirInExpr);
        oirLet.copyMetadata(let);
        return oirLet;
    }

    public OIRAST visit(EmitOIRState state, FieldSet fieldSet) {
        IExpr object = (Expression) fieldSet.getObjectExpr();
        IExpr toSet = fieldSet.getExprToAssign();
        OIRExpression oirObject = (OIRExpression) object.acceptVisitor(this, state);
        OIRExpression oirToSet = (OIRExpression) toSet.acceptVisitor(this, state);
        OIRFieldSet oirFieldSet = new OIRFieldSet(oirObject, fieldSet.getFieldName(), oirToSet);
        oirFieldSet.copyMetadata(fieldSet);
        return oirFieldSet;
    }

    public OIRAST visit(EmitOIRState state, Variable variable) {
        OIRVariable oirVar = new OIRVariable(variable.getName());
        oirVar.copyMetadata(variable);
        return oirVar;
    }

    public OIRAST visit(EmitOIRState state, Cast cast) {
        IExpr expr = cast.getToCastExpr();
        OIRExpression oirExpr = (OIRExpression) expr.acceptVisitor(this, state);
        OIRType oirType = (OIRType) cast.getType().acceptVisitor(this, state);
        OIRCast oirCast = new OIRCast(oirExpr, oirType);
        oirCast.copyMetadata(cast);
        return oirCast;
    }

    public OIRAST visit(EmitOIRState state, VarDeclaration varDecl) {
        ValueType varDeclType = varDecl.getType();
        OIRType type = (OIRType) varDeclType.acceptVisitor(this, state);
        OIRFieldDeclaration oirMember = new OIRFieldDeclaration(varDecl.getName(), type);
        oirMember.copyMetadata(varDecl);
        return oirMember;
    }

    public OIRAST visit(EmitOIRState state, DefDeclaration defDecl) {

        // Set up data structures, contexts, etc. to be used.
        List<OIRFormalArg> listOIRFormalArgs = new Vector<OIRFormalArg>();
        OIREnvironment defEnv = new OIREnvironment(state.getEnvironment());
        TypeContext defCxt = state.getContext();

        // Process the formal arguments of the method declaration.
        for (FormalArg arg : defDecl.getFormalArgs()) {
            OIRFormalArg formalArg = (OIRFormalArg) arg.acceptVisitor(this,
                    new EmitOIRState(state.getContext(), state.getEnvironment()));
            defEnv.addName(formalArg.getName(), formalArg.getType());
            defCxt = defCxt.extend(formalArg.getSite(), arg.getType());
            listOIRFormalArgs.add(formalArg);
        }

        // Construct the OIR method declaration.
        OIRMethodDeclaration oirMethodDecl = new OIRMethodDeclaration(null, defDecl.getName(), listOIRFormalArgs);
        OIRExpression oirBody = (OIRExpression) defDecl.getBody().acceptVisitor(this, new EmitOIRState(defCxt, defEnv));
        OIRMethod oirMethod = new OIRMethod(defEnv, oirMethodDecl, oirBody);
        oirMethod.copyMetadata(defDecl);
        return oirMethod;

    }

    public OIRAST visit(EmitOIRState state, ValDeclaration valDecl) {
        ValueType valDeclType = valDecl.getType();
        OIRType type = (OIRType) valDeclType.acceptVisitor(this, state);
        OIRFieldDeclaration  oirMember = new OIRFieldDeclaration(valDecl.getName(), type, true);
        oirMember.copyMetadata(valDecl);
        return oirMember;
    }

    public OIRAST visit(EmitOIRState state, IntegerLiteral integerLiteral) {
        OIRInteger oirInt = new OIRInteger(integerLiteral.getValue());
        oirInt.copyMetadata(integerLiteral);
        return oirInt;
    }


    public OIRAST visit(EmitOIRState state, BooleanLiteral booleanLiteral) {
        OIRBoolean oirBool = new OIRBoolean(booleanLiteral.getValue());
        oirBool.copyMetadata(booleanLiteral);
        return oirBool;
    }

    public OIRAST visit(EmitOIRState state, RationalLiteral rational) {
        OIRRational oirRational = new OIRRational(rational.getNumerator(), rational.getDenominator());
        oirRational.copyMetadata(rational);
        return oirRational;
    }

    public OIRAST visit(EmitOIRState state, FormalArg formalArg) {
        OIRFormalArg oirarg = new OIRFormalArg(formalArg.getSite(), null);
        oirarg.copyMetadata(formalArg);
        return oirarg;
    }

    public OIRAST visit(EmitOIRState state, VarDeclType varDeclType) {
        String fieldName = varDeclType.getName();
        OIRMethodDeclarationGroup methodDecls = new OIRMethodDeclarationGroup();
        ValueType type = varDeclType.getRawResultType();
        OIRInterface oirType = (OIRInterface) type.acceptVisitor(this, state);
        methodDecls.addMethodDeclaration(new OIRMethodDeclaration(oirType, "get" + fieldName, null));
        List<OIRFormalArg> args = new Vector<OIRFormalArg>();
        args.add(new OIRFormalArg("_" + fieldName, oirType));
        methodDecls.addMethodDeclaration(new OIRMethodDeclaration(null, "set" + fieldName, args));
        methodDecls.copyMetadata(varDeclType);
        return methodDecls;
    }

    public OIRAST visit(EmitOIRState state, ValDeclType valDeclType) {
        ValueType type = valDeclType.getRawResultType();
        OIRInterface oirtype = (OIRInterface) type.acceptVisitor(this, state);
        OIRMethodDeclaration methodDecl = new OIRMethodDeclaration(oirtype, "set" + valDeclType.getName(), null);
        OIRMethodDeclarationGroup methodDecls = new OIRMethodDeclarationGroup();
        methodDecls.addMethodDeclaration(methodDecl);
        methodDecls.copyMetadata(valDeclType);
        return methodDecls;
    }

    public OIRAST visit(EmitOIRState state, DefDeclType defDeclType) {

        // Process types of the formal arguments.
        List<OIRFormalArg> listOIRFormalArgs = new Vector<OIRFormalArg>();
        for (FormalArg arg : defDeclType.getFormalArgs()) {
            OIRFormalArg formalArg = (OIRFormalArg) arg.acceptVisitor(this, state);
            listOIRFormalArgs.add(formalArg);
        }

        // Construct the list of method declarations.
        ValueType returnType = defDeclType.getRawResultType();
        OIRType oirReturnType = (OIRType) returnType.acceptVisitor(this, state);
        OIRMethodDeclaration oirMethodDecl = new OIRMethodDeclaration(oirReturnType, defDeclType.getName(), listOIRFormalArgs);
        OIRMethodDeclarationGroup methodDecls = new OIRMethodDeclarationGroup();
        methodDecls.addMethodDeclaration(oirMethodDecl);
        methodDecls.copyMetadata(defDeclType);
        return methodDecls;

    }

    public OIRAST visit(EmitOIRState state, AbstractTypeMember abstractDeclType) {
        OIRType oirtype = (OIRType) abstractDeclType.acceptVisitor(this, state);
        OIRMethodDeclaration methDecl = new OIRMethodDeclaration(oirtype, "get" + abstractDeclType.getName(), null);
        OIRMethodDeclarationGroup methodDecls = new OIRMethodDeclarationGroup();
        methodDecls.addMethodDeclaration(methDecl);
        methodDecls.copyMetadata(abstractDeclType);
        return methodDecls;
    }

    public OIRAST visit(EmitOIRState state, StructuralType structuralType) {

        String interfaceName = generateInterfaceName();
        List<OIRMethodDeclaration> methodDecls = new Vector<OIRMethodDeclaration>();
        OIREnvironment oirInterfaceEnv = new OIREnvironment(state.getEnvironment());

        // Process each declaration in the structural type.
        for (DeclType declType : structuralType.getDeclTypes()) {
            OIRMethodDeclarationGroup declTypeGroup;
            OIRAST declAST = declType.acceptVisitor(this, state);
            declTypeGroup = (OIRMethodDeclarationGroup) declAST;
            for (int i = 0; i < declTypeGroup.size(); i++) {
                oirInterfaceEnv.addName(declTypeGroup.elementAt(i).getName(),
                        declTypeGroup.elementAt(i).getReturnType());
                methodDecls.add(declTypeGroup.elementAt(i));
            }
        }

        // Construct and return the interface.
        OIRInterface oirinterface = new OIRInterface(oirInterfaceEnv, interfaceName, structuralType.getSelfName(), methodDecls);
        state.getEnvironment().addType(interfaceName, oirinterface);
        OIRProgram.PROGRAM.addTypeDeclaration(oirinterface);
        oirinterface.copyMetadata(structuralType);
        return oirinterface;
    }

    public OIRAST visit(EmitOIRState state, NominalType nominalType) {
        // Note: This code belongs more in the Match case
        // OIRExpression oirfieldget;
        // Path path;

        // path = nominalType.getPath();
        // oirfieldget = (OIRExpression) path.acceptVisitor(this, cxt, oirenv);

        // return new OIRFieldGet (oirfieldget,
        // nominalType.getTypeMember()+"tag");

        StructuralType defaultType = new StructuralType("emptyType", new ArrayList<DeclType>());
        OIRAST result = defaultType.acceptVisitor(this, state);
        result.copyMetadata(nominalType);
        return result;

        // TODO: This should also take into account types available in the
        // OIREnvironment
        // TypeContext context = TestUtil.getStandardGenContext();

        // StructuralType st = nominalType.getStructuralType(context,
        // defaultType);
        // return st.acceptVisitor(this, cxt, oirenv);
    }

    public OIRAST visit(EmitOIRState state, StringLiteral stringLiteral) {
        OIRString oirstring = new OIRString(stringLiteral.getValue());
        oirstring.copyMetadata(stringLiteral);
        return oirstring;
    }
    
    public OIRAST visit(EmitOIRState state, CharacterLiteral characterLiteral) {
        OIRCharacter oircharacter = new OIRCharacter(characterLiteral.getValue());
        oircharacter.copyMetadata(characterLiteral);
        return oircharacter;
    }

    public OIRAST visit(EmitOIRState state, ForwardDeclaration forwardDecl) {
        OIRForward oirforward = new OIRForward(null, forwardDecl.getFieldName());
        oirforward.copyMetadata(forwardDecl);
        return oirforward;
    }

    @Override
    public OIRAST visit(EmitOIRState state, Bind bind) {
        throw new RuntimeException("EMITOIRVisitor: visit not implemented on Bind expressions.");
    }

    @Override
    public OIRAST visit(EmitOIRState state, ConcreteTypeMember concreteTypeMember) {
        OIRMethodDeclarationGroup group = new OIRMethodDeclarationGroup();
        group.copyMetadata(concreteTypeMember);
        return group;
    }

    @Override
    public OIRAST visit(EmitOIRState state, TypeDeclaration typeDecl) {
        // The tag field
        OIRFieldDeclaration fieldDecl = new OIRFieldDeclaration(typeDecl.getName() + "tag",
                OIRIntegerType.getIntegerType());
        fieldDecl.copyMetadata(typeDecl);
        return fieldDecl;
    }

    @Override
    public OIRAST visit(EmitOIRState state, ValueType caseType) {
        throw new RuntimeException("EMITOIRVisitor: CaseType -> OIR unimplemented");
    }

    @Override
    public OIRAST visit(EmitOIRState state, ExtensibleTagType extensibleTagType) {
        throw new RuntimeException("EMITOIRVisitor: ExtensibleTagType -> OIR unimplemented");
    }

    @Override
    public OIRAST visit(EmitOIRState state, DataType dataType) {
        throw new RuntimeException("EMITOIRVisitor: DataType -> OIR unimplemented");
    }

    @Override
    public OIRAST visit(EmitOIRState state, FFIImport ffiImport) {
        NominalType javaType = new NominalType("system", "java");
        NominalType pythonType = new NominalType("system", "python");

        OIRFFIImport result;
        if (ffiImport.getFFIType().equals(javaType)) {
            result = new OIRFFIImport(FFIType.JAVA, ffiImport.getPath());
        } else if (ffiImport.getFFIType().equals(pythonType)) {
            result = new OIRFFIImport(FFIType.PYTHON, ffiImport.getPath());
        } else {
            throw new RuntimeException("Unknown FFI type: " + ffiImport.getFFIType());
        }

        result.copyMetadata(ffiImport);
        return result;
    }

    @Override
    public OIRAST visit(EmitOIRState state, FFI ffi) {
        String platform = ffi.getImportName();

        // Specialize FFI to python if it is currently a generic platform
        if (platform.equals("platform")) {
            platform = "python";
        }
        return new OIRVariable("ffi_" + platform, false);
    }

    @Override
    public OIRAST visit(EmitOIRState state, Case c) {
        throw new RuntimeException("EMITOIRVisitor: Case -> OIR implemented inside the visit method for Match expressions.");
    }

    @Override
    public OIRAST visit(EmitOIRState state, ModuleDeclaration modDecl) {
        throw new RuntimeException("ModuleDeclarations should be specialized before emitting Python!");
    }

    @Override
    public OIRAST visit(EmitOIRState state, EffectDeclaration effectDeclaration) {
        // TODO Auto-generated method stub
        throw new RuntimeException("EmitOIRVisitor: hopefully not necessary?");
    }

    @Override
    public OIRAST visit(EmitOIRState state, EffectDeclType effectDeclType) {
        throw new RuntimeException("EmitOIRVisitor: hopefully not necessary?");
    }

    @Override
    public OIRAST visit(EmitOIRState state, SeqExpr seqExpr) {
        TypeContext ctx = state.getContext();
        LinkedList<Pair<String, OIRExpression>> list = new LinkedList<Pair<String, OIRExpression>>();
        for (HasLocation hl : seqExpr.getElements()) {
            IExpr expr = null;
            String varName = null;
            ValueType varType = null;
            BindingSite site = null;
            if (hl instanceof IExpr) {
                expr = (IExpr) hl;
                varName = GenContext.generateName();
                site = new BindingSite(varName);
                varType = expr.typeCheck(ctx, null);
            } else {
                VarBinding vb = (VarBinding) hl;
                expr = vb.getExpression();
                varName = vb.getVarName();
                site = vb.getSite();
                varType = vb.getType();
            }
            ctx = ctx.extend(site, varType);
            OIRExpression newExpr = (OIRExpression) expr.acceptVisitor(this, new EmitOIRState(ctx, state.getEnvironment()));
            state.getEnvironment().addName(varName, null);

            list.addLast(new Pair<String, OIRExpression>(varName, newExpr));
        }

        OIRExpression current = list.getLast().getSecond();

        for (int i = list.size() - 2; i >= 0; --i) {
            String varName = list.get(i).getFirst();
            OIRExpression oirExpr = list.get(i).getSecond();
            OIRLet oirLet = new OIRLet(varName, oirExpr, current);
            oirLet.copyMetadata(seqExpr);
            current = oirLet;
        }

        return current;
    }

    @Override
    public OIRAST visit(EmitOIRState state, RefinementType type) {
        StructuralType defaultType = new StructuralType("emptyType", new ArrayList<DeclType>());
        OIRAST result = defaultType.acceptVisitor(this, state);
        result.copyMetadata(type);
        return result;
    }

    public OIRAST visit(EmitOIRState state, FloatLiteral flt) {
      OIRFloat oirInt = new OIRFloat(flt.getFullValue());
      oirInt.copyMetadata(flt);
      return oirInt;
    }

  @Override
  public OIRAST visit(EmitOIRState state, RecDeclaration recDecl) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OIRAST visit(EmitOIRState state, RecConstructDeclaration recConstructDecl) {
    // TODO Auto-generated method stub
    return null;
  }
}
