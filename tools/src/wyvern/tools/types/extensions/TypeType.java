package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.types.*;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class TypeType extends AbstractTypeImpl implements OperatableType, RecordType {
	private TypeDeclaration decl;
	private AtomicReference<Obj> attrObj;
	private AtomicReference<Environment> typeDeclEnv;

	public TypeType(TypeDeclaration decl) {
		typeDeclEnv = decl.getDeclEnv();
		attrObj = decl.getAttrObjRef();
	}

	public TypeType(Environment declEnv) {
		this.typeDeclEnv = new AtomicReference<>(declEnv);
	}

	public TypeType(AtomicReference<Environment> declEnv) {
		this.typeDeclEnv = declEnv;
	}
	
	public TypeDeclaration getDecl() {
		return this.decl;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}

	private boolean toStringing = false;
	@Override
	public String toString() {
		if (toStringing)
			return "TYPE(Repeated)";
		toStringing = true;
		String res = "TYPE(" + typeDeclEnv.get().toString() + ")";
		toStringing = false;
		return res;
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		// should not be any arguments - that is in a separate application at present
		assert opExp.getArgument() == null;
		
		// the operation should exist
		String opName = opExp.getOperationName();
		NameBinding m = typeDeclEnv.get().lookup(opName);

		if (m == null) {
            NameBinding n = attrObj.get().getIntEnv().lookup(opName);
            if (n == null)
			    reportError(OPERATOR_DOES_NOT_APPLY, opExp, opName, this.toString());
            return n.getType();
        }
		
		// TODO Auto-generated method stub
		return m.getType();
	}
	
	public Map<String, Type> getMembers() {
		HashMap<String, Type> thisMembers = new HashMap<>();
		for (Binding b : typeDeclEnv.get().getBindings()) {
			if (!(b instanceof NameBinding))
				continue;
			String name = b.getName();
			Type type = b.getType();
			thisMembers.put(name, type);
		}
		return thisMembers;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (super.subtype(other, subtypes)) {
			return true;
		}
		
		if (other instanceof TypeType) {
			Map<String, Type> thisMembers = this.getMembers();
			// System.out.println("this (" + this + ") : " + thisMembers);
			Map<String, Type> otherMembers = ((TypeType) other).getMembers();
			// System.out.println("other (" + other + ") : " + otherMembers);
			return checkSubtypeRecursively(this, other, thisMembers, otherMembers, subtypes);
		}
		
		return false;
	}
	
	public static boolean checkSubtypeRecursively(Type thisType, Type otherType,
			Map<String, Type> thisMembers, Map<String, Type> otherMembers,
			HashSet<SubtypeRelation> subtypes) {
		
		boolean subset = true;
		for (Map.Entry<String, Type> memberOther : otherMembers.entrySet()) {
			boolean hasImplementingCandidate = false;
			Type entryType = thisMembers.get(memberOther.getKey());
			if (entryType == null) {
				subset = false;
				break;
			}

			SubtypeRelation sr = new SubtypeRelation(thisType, otherType);
			if (!subtypes.contains(sr)) { // Avoid infinite recursion! :)
				subtypes.add(sr);
				boolean result = entryType.subtype(memberOther.getValue(), subtypes);
				subtypes.remove(sr);

				if (result) {
					hasImplementingCandidate = true;
				}
			}
			if (!hasImplementingCandidate) {
				subset = false;
				break;
			}
		}

		return subset;
	}

	@Override
	public Type getInnerType(String name) {
		return typeDeclEnv.get().lookupType(name).getType();
	}

	private boolean isParserCheck = false;
	private boolean isParserValid = false;
	@Override
	public LineParser getParser() {
		if (!isParserCheck) {
			isParserValid = Util.checkCast(attrObj.get(), LineParser.class);
			isParserCheck = true;
		}
		if (isParserValid)
			return Util.toJavaClass(attrObj.get(), LineParser.class);
		return null;
	}
}