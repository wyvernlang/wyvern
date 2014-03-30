package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.*;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class TypeType extends AbstractTypeImpl implements OperatableType, RecordType {
	private TypeDeclaration decl;
	private Reference<Value> attrObj;
	private Reference<Environment> typeDeclEnv;

	public TypeType(TypeDeclaration decl) {
		typeDeclEnv = decl.getDeclEnv();
		attrObj = decl.getMetaValue();
	}

	public TypeType(Environment declEnv) {
		this.typeDeclEnv = new Reference<>(declEnv);
	}

	public TypeType(Reference<Environment> declEnv) {
		this.typeDeclEnv = declEnv;
	}

	public Value getAttrValue() { return attrObj.get(); }
	
	public TypeDeclaration getDecl() {
		return this.decl;
	}
	public Obj getAttrObj() { return null; }

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
		return null;
	}

	@Override
	public Map<String, Type> getChildren() {
		HashMap<String, Type> map = new HashMap<>();
		List<Binding> bindings = typeDeclEnv.get().getBindings();
		writeBindings("denv", map, bindings);
		return map;
	}

	private void writeBindings(String prefix, HashMap<String, Type> map, List<Binding> bindings) {
		int i = 0;
		for (Binding b : bindings) {
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

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		ArrayList<String> denvList = new ArrayList<>(newChildren.keySet());
		Comparator<String> c = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int io1 = Integer.parseInt(o1.split(":")[1]);
				int io2 = Integer.parseInt(o2.split(":")[1]);
				return io2 - io1;
			}
		};
		Collections.sort(denvList, c);
		Environment ndEnv = getEnvForDict(newChildren, Environment.getEmptyEnvironment(), denvList);
		return new TypeType(new Reference<>(ndEnv));
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
}