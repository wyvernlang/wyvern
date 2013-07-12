package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class TypeType extends AbstractTypeImpl implements OperatableType {
	private String name;
	private TypeDeclaration decl;
	private AtomicReference<Environment> declEnv;

	public TypeType(TypeDeclaration decl) {
		declEnv = decl.getDeclEnv();
		name = decl.getName();
	}

	public TypeType(String name, Environment declEnv) {
		this.declEnv = new AtomicReference<>(declEnv);
		this.name = name;
	}
	
	public TypeDeclaration getDecl() {
		return this.decl;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return /*"TYPE " +*/ name;
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
	
	public HashSet<Pair<String, Type>> getMembers() {
		HashSet<Pair<String, Type>> thisMembers = new HashSet<Pair<String, Type>>();
		for (Binding b : declEnv.get().getBindings()) {
			if (!(b instanceof NameBinding))
				continue;
			String name = b.getName();
			Type type = b.getType();
			thisMembers.add(new Pair<>(name, type));
		}
		return thisMembers;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (super.subtype(other, subtypes)) {
			return true;
		}
		
		if (other instanceof TypeType) {
			HashSet<Pair<String, Type>> thisMembers = this.getMembers();
			// System.out.println("this (" + this + ") : " + thisMembers);
			HashSet<Pair<String, Type>> otherMembers = ((TypeType) other).getMembers();
			// System.out.println("other (" + other + ") : " + otherMembers);
			return checkSubtypeRecursively(this, other, thisMembers, otherMembers, subtypes);
		}
		
		return false;
	}
	
	public static boolean checkSubtypeRecursively(Type thisType, Type otherType,
			HashSet<Pair<String, Type>> thisMembers, HashSet<Pair<String, Type>> otherMembers,
			HashSet<SubtypeRelation> subtypes) {
		
		boolean subset = true;
		for (Pair<String, Type> memberOther : otherMembers) {
			boolean hasImplementingCandidate = false;
			for (Pair<String, Type> memberThis : thisMembers) {
				if (memberThis.first.equals(memberOther.first)) { // Name has to be equal! Duh! :-)
					// Apply S-Amber rule here!
					SubtypeRelation sr = new SubtypeRelation(thisType, otherType);
					if (!subtypes.contains(sr)) { // Avoid infinite recursion! :)
						subtypes.add(sr);
						boolean result = memberThis.second.subtype(memberOther.second, subtypes);
						subtypes.remove(sr);
								
						if (result) {
							hasImplementingCandidate = true;
							break;
						}
					}
				}
			}
			if (!hasImplementingCandidate) {
				subset = false;
				break;
			}
		}

		return subset;
	}
}