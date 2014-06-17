package wyvern.tools.types.extensions;

import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.types.*;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

public class ClassType extends AbstractTypeImpl implements OperatableType, RecordType, ParameterizableType {
	private ClassDeclaration decl = null;
	private Reference<Environment> declEnv;
	protected Reference<Environment> typeEquivalentEnv = new Reference<>();
	private List<String> params;
	private String name;


	public ClassType(ClassDeclaration td) {
		this(td.getDeclEnvRef(),
				td.getTypeEquivalentEnvironmentReference(),
				td.getTypeParams(),
				td.getName());
		this.decl = td;
	}

	public ClassType(Reference<Environment> declEnv,
					 Reference<Environment> typeEquivalentEnv,
					 List<String> typeParams,
					 String name) {
		this.declEnv = declEnv;
		this.typeEquivalentEnv = typeEquivalentEnv;
		this.params = typeParams;
		this.name = name;
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
		if (opExp.getArgument() != null)
			throw new RuntimeException(opExp.getLocation().toString());
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
		if (typeEquivalentEnv == null || typeEquivalentEnv.get() == null) {
			if (declEnv.get() != null) {
				if (typeEquivalentEnv == null)
					typeEquivalentEnv = new Reference<>();
				typeEquivalentEnv.set(TypeDeclUtils.getTypeEquivalentEnvironment(declEnv.get()));
			} else
				throw new RuntimeException();
		}

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
		return declEnv.get().lookupType(name).getType();
	}


	public Environment getEnv() {
		return declEnv.get();
	}

	@Override
	public Type checkParameters(List<Type> params) {
		return null;
	}

	private void writeBindings(String prefix, HashMap<String, Type> map, List<Binding> bindings) {
		int i = 0;
		for (Binding b : bindings) {
			if (b == null)
				continue;
			if (b instanceof NameBindingImpl) {
				NameBindingImpl ni = (NameBindingImpl)b;
				map.put(prefix+":"+i++ +":ni:"+ni.getName(), ni.getType());
			} else if (b instanceof TypeBinding) {
				TypeBinding tb = (TypeBinding)b;
				map.put(prefix+":"+i++ +":tb:"+tb.getName(), tb.getType());
			} else {
				throw new RuntimeException("Unexpected binding");
			}
		}
	}

	private Environment getEnvForDict(Map<String, Type> newChildren, Environment ndEnv, ArrayList<String> list) {
		for (String key : list) {
			String[] kSplit = key.split(":");
			Type nt = newChildren.get(key);
			if(kSplit[2].equals("ni")) {
				ndEnv = ndEnv.extend(new NameBindingImpl(kSplit[3], nt));
			} else if (kSplit[2].equals("tb")) {
				ndEnv = ndEnv.extend(new TypeBinding(kSplit[3], nt));
			} else {
				throw new RuntimeException("Unexpected binding");
			}
		}
		return ndEnv;
	}

	public String getName() {
		return name;
	}
}