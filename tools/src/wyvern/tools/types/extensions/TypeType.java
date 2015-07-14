package wyvern.tools.types.extensions;

import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.*;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class TypeType extends AbstractTypeImpl implements OperatableType, RecordType {
	private TypeDeclaration decl;
	private Reference<Environment> typeDeclEnv;

	public TypeType(TypeDeclaration decl) {
		this.decl = decl;
		typeDeclEnv = decl.getDeclEnv();
	}

	public TypeType(Environment declEnv) {
		this.typeDeclEnv = new Reference<>(declEnv);
	}

	public TypeType(Reference<Environment> declEnv) {
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
		if (opExp.getArgument() != null)
			throw new RuntimeException(opExp.getLocation().toString());
		assert opExp.getArgument() == null;
		
		// the operation should exist
		String opName = opExp.getOperationName();

		NameBinding m = typeDeclEnv.get().lookup(opName);
		TypeBinding t = typeDeclEnv.get().lookupType(opName);
		
		// Accessing type members should be OK!!! // FIXME:
		if (m == null && t == null)
			throw new RuntimeException("Invalid operation "+opName+" on type " + this);
		
		// TODO Auto-generated method stub
		
		if (m != null) {
			return m.getType();
		} else {
			return t.getType();
		}
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
	public TypeBinding getInnerType(String name) {
		// System.out.println("Looking up (inside getInnerType) name " + name);
		// System.out.println("Currently inside TypeType: " + this);
		// System.out.println("this.decl.getName = " + this.decl.getName());
		// System.out.println("this.getMembers() = " + this.getMembers());
		
		TypeBinding tb = typeDeclEnv.get().lookupType(name);
		if (tb == null) {
			// System.out.println("Maybe it is a name?");
			
			NameBinding nm = typeDeclEnv.get().lookup(name);
			// System.out.println(nm.getType());
			
			return new TypeBinding(nm.getName(), nm.getType());
		} else {
			return tb;
		}
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

	@Override
	public Type cloneWithBinding(TypeBinding binding) {
		TypeType typeType = new TypeType(typeDeclEnv);
		typeType.setResolvedBinding(binding);
		return typeType;
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

	/**
	 * Returns the name of this type-type.
	 * 
	 * @return
	 */
	public String getName() {
		return decl.getName();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof TypeType && ((TypeType) other).typeDeclEnv.get().equals(typeDeclEnv.get());
	}

	@Override
	public TypeType getEquivType() {
		return this;
	}
	@Override
	public TaggedInfo getTaggedInfo() {
		return decl==null?null:decl.getTaggedInfo();
	}

}