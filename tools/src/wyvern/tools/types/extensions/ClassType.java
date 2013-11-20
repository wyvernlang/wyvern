package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

public class ClassType extends AbstractTypeImpl implements OperatableType, RecordType {
	private ClassDeclaration decl = null;
	private AtomicReference<Environment> declEnv;
	protected AtomicReference<Environment> typeEquivalentEnv;


	public ClassType(ClassDeclaration td) {
		this(td.getDeclEnvRef(),
				td.getTypeEquivalentEnvironmentReference());
		this.decl = td;
	}

	public ClassType(AtomicReference<Environment> declEnv, AtomicReference<Environment> typeEquivalentEnv) {
		this.declEnv = declEnv;
		this.typeEquivalentEnv = typeEquivalentEnv;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		if (declEnv.get() != null)
			return "CLASS(" + "" + ")";
		else
			return "CLASS()";
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		// should not be any arguments - that is in a separate application at present
		assert opExp.getArgument() == null;
		
		// the operation should exist
		String opName = opExp.getOperationName();
		NameBinding m = declEnv.get().lookup(opName);

		if (m == null)
			reportError(OPERATOR_DOES_NOT_APPLY, opExp, opName, this.toString());
		
		// TODO Auto-generated method stub
		return m.getType();
	}

	public ClassDeclaration getDecl() {
		return decl;
	}

	private TypeType equivType = null;
	public TypeType getEquivType() {
		if (typeEquivalentEnv == null || typeEquivalentEnv.get() == null)
			throw new RuntimeException();

		if (equivType == null)
			equivType = new TypeType(typeEquivalentEnv.get());
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

	@Override
	public Type getInnerType(String name) {
		return declEnv.get().lookup(name).getType();
	}


	public Environment getEnv() {
		return declEnv.get();
	}
}