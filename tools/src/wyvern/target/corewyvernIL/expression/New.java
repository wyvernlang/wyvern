package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class New extends Expression {

	private List<? extends Declaration> decls;
	private String selfName;
	private boolean hasDelegate;
	private DelegateDeclaration delegateDeclaration;

	/** convenience method for a single declaration */
	public New(NamedDeclaration decl, FileLocation loc) {
		this(Arrays.asList(decl), loc);
	}
	public New(NamedDeclaration decl) {
		this(decl, decl.getLocation());
	}

    /** convenience method for two declarations */
    public New(NamedDeclaration decl1, NamedDeclaration decl2) {
        this(Arrays.asList(decl1, decl2), decl1.getLocation());
    }
	
	/** computes the type itself, uses a don't care selfName */
	public New(List<NamedDeclaration> decls, FileLocation loc) {
		this(decls, "dontcare", typeOf(decls), loc);
	}
	
	public New(List<? extends Declaration> decls, String selfName, ValueType type, FileLocation loc) {
		super(type, loc);
		this.decls = decls;
		this.selfName = selfName;
		for (Declaration d : decls) {
			if (d == null)
				throw new NullPointerException();
		}

		Optional<? extends Declaration> delegate_option = decls.stream().filter(d-> d instanceof DelegateDeclaration).findFirst();

		hasDelegate = delegate_option.isPresent();
		if (hasDelegate) {
			delegateDeclaration = (DelegateDeclaration)delegate_option.get();
		}
	}

	public List<Declaration> getDecls() {
		return (List<Declaration>)decls;
	}

	public String getSelfName() {
		return selfName;
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append("new ").append(selfName).append(" : ");
		getExprType().doPrettyPrint(dest, indent);
		dest.append(" =>\n");

		for (Declaration decl: decls) {
			decl.doPrettyPrint(dest, indent + "    ");
		}
	}


	/** Returns a declaration of the proper name, or null if not found */
	public Declaration findDecl(String name) {
		for (Declaration d : decls) {
			if (name.equals(d.getName())) {
				return d;
			}
		}
		return null;
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		List<DeclType> dts = new LinkedList<DeclType>();

		TypeContext thisCtx = ctx.extend(selfName, getExprType());

		boolean isResource = false;
		for (Declaration d : decls_ExceptDelegate()) {
			DeclType dt = d.typeCheck(ctx, thisCtx);
			dts.add(dt);
			if (d.containsResource(thisCtx)) {
				isResource = true;
			}
		}

		ValueType type = getExprType();
		if (hasDelegate) {
			ValueType delegateObjectType = ctx.lookupTypeOf(delegateDeclaration.getFieldName());
			StructuralType delegateStructuralType = delegateObjectType.getStructuralType(thisCtx);
			// new defined declaration will override delegate object's method definition if they had subType relationship
			for (DeclType declType : delegateStructuralType.getDeclTypes()) {
				if (!dts.stream().anyMatch(newDefDeclType-> newDefDeclType.isSubtypeOf(declType, thisCtx))) {
					dts.add(declType);
				}
			}
		}

		// check that everything in the claimed structural type was accounted for
		StructuralType requiredT = type.getStructuralType(ctx);
		StructuralType actualT = new StructuralType(selfName, dts);
		if (!actualT.isSubtypeOf(requiredT, ctx)) {
			ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, actualT.getSelfName(), requiredT.getSelfName());
		}

		if (isResource && !requiredT.isResource(GenContext.empty())) {
			if (type instanceof StructuralType) {
				type = new StructuralType(selfName, dts, isResource);
				this.setExprType(type);
			} else {
				// can't update the type
				ToolError.reportError(ErrorMessage.MUST_BE_ASSIGNED_TO_RESOURCE_TYPE, this);
			}
		}

		return type;
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Value result = null;

		// evaluate all decls
		List<Declaration> ds = new LinkedList<Declaration>();
		for (Declaration d : decls_ExceptDelegate()) {;
		    Declaration newD = d.interpret(ctx);
		    ds.add(newD);
		}
		result = new ObjectValue(ds, selfName, getExprType().interpret(ctx),delegateDeclaration, getLocation(), ctx);

		return result;
	}

	private List<Declaration> decls_ExceptDelegate() {
		return decls.stream().filter(x->x != delegateDeclaration).collect(Collectors.toList());
	}

	@Override
	public Set<String> getFreeVariables() {
		Set<String> freeVars = new HashSet<>();
		if (hasDelegate) {
			freeVars.addAll(delegateDeclaration.getFreeVariables());
		}
		for (Declaration decl : decls) {
			freeVars.addAll(decl.getFreeVariables());
		}
		freeVars.remove(selfName);
		return freeVars;
	}
	
	private static ValueType typeOf(List<NamedDeclaration> decls2) {
		List<DeclType> declts =	new LinkedList<DeclType>();
		for (NamedDeclaration d : decls2) {
			declts.add(d.getDeclType());
		}
		ValueType type = new StructuralType("dontcare", declts);
		return type;
	}
}
