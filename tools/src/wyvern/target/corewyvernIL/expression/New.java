package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class New extends Expression {

	private List<Declaration> decls;
	private String selfName;
	private boolean hasDelegate;
	private DelegateDeclaration delegateDeclaration;

	public New(List<Declaration> decls, String selfName, ValueType type) {
		super(type);
		this.decls = decls;
		this.selfName = selfName;
		for (Declaration d : decls) {
			if (d == null)
				throw new NullPointerException();
		}

		Optional<Declaration> delegate_option = decls.stream().filter(d-> d instanceof DelegateDeclaration).findFirst();

		hasDelegate = delegate_option.isPresent();
		if (hasDelegate) {
			delegateDeclaration = (DelegateDeclaration)delegate_option.get();
		}
	}

	public List<Declaration> getDecls() {
		return decls;
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
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		List<DeclType> dts = new LinkedList<DeclType>();

		TypeContext thisCtx = ctx.extend(selfName, getExprType());

		for (Declaration d : decls_ExceptDelegate()) {
			DeclType dt = d.typeCheck(ctx, thisCtx);
			dts.add(dt);
		}

		ValueType type = getExprType();
		if (hasDelegate) {
			ValueType delegateObjectType = ctx.lookup(delegateDeclaration.getFieldName()); 
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
			ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, actualT.getSelfName(), requiredT.getSelfName());;
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
		result = new ObjectValue(ds, selfName, getExprType(),delegateDeclaration, ctx);

		return result;
	}

	private List<Declaration> decls_ExceptDelegate() {
		return decls.stream().filter(x->x != delegateDeclaration).collect(Collectors.toList());
	}
}
