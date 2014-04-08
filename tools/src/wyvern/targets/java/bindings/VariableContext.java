package wyvern.targets.java.bindings;

import org.objectweb.asm.tree.InsnList;

public class VariableContext {
	public static VariableContext emptyContext = new VariableContext();
	private VariableContext parent = null;
	private JavaBinding selfBinding;

	private VariableContext() {}
	private VariableContext(JavaBinding binding, VariableContext parent) {
		selfBinding = binding;
		this.parent = parent;
	}

	public VariableContext extend(JavaBinding newBinding) {
		return new VariableContext(newBinding, this);
	}

	public void writeVariable(String name, InsnList insns) {
		if (selfBinding.getName().equals(name)) {
			selfBinding.writeGetValue(insns);
			return;
		}
		parent.writeVariable(name, insns);
	}
}
