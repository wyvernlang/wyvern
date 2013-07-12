package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ClassType extends AbstractTypeImpl implements OperatableType {
	private ClassDeclaration decl = null;
	private String name;
	private AtomicReference<Environment> declEnv;
	private AtomicReference<Environment> typeEquivalentEnv;
	private TypeType implementsType;


	public ClassType(ClassDeclaration td) {
		this(td.getName(), td.getDeclEnvRef(),
				td.getTypeEquivalentEnvironmentReference(), td.getImplementsType());
		this.decl = td;
	}

	public ClassType(String name, AtomicReference<Environment> declEnv, AtomicReference<Environment> typeEquivalentEnv, TypeType implementsType) {
		this.name = name;
		this.declEnv = declEnv;
		this.typeEquivalentEnv = typeEquivalentEnv;
		this.implementsType = implementsType;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "CLASS " + name;
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		// should not be any arguments - that is in a separate application at present
		assert opExp.getArgument() == null;
		
		// the operation should exist
		String opName = opExp.getOperationName();
		NameBinding m = declEnv.get().lookup(opName);

		if (m == null)
			reportError(OPERATOR_DOES_NOT_APPLY, opName, this.toString(), opExp);
		
		// TODO Auto-generated method stub
		return m.getType();
	}

	public ClassDeclaration getDecl() {
		return decl;
	}

	private TypeType equivType = null;
	public TypeType getEquivType() {
		if (typeEquivalentEnv.get() == null)
			throw new RuntimeException();

		if (equivType == null)
			equivType = new TypeType(name, typeEquivalentEnv.get());
		return equivType;
	}

	// FIXME: Do something similar here to TypeType maybe and maybe try to integrate the above
	// implements checks into here and change ClassDeclaration to use this instead.
	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (super.subtype(other, subtypes)) {
			return true;
		}

		if (other instanceof TypeType) {
			return getEquivType().subtype(other);
		} else if (other instanceof ClassType) {
			return getEquivType().subtype(((ClassType) other).getEquivType());
		}
		
		return false;
	}

	public String getName() {
		return name;
	}
}