package wyvern.targets.java.bindings;

import org.objectweb.asm.tree.InsnList;

public interface JavaBinding {

	public String getName();

	/**
	 * Writes a set of instructions to get the variable represented by the binding.
	 *
	 * Precondition: nothing
	 * Postcondition: toProduce is appended with a list of instructions that pushes the given variable onto the stack in the first position
	 * @param toProduce the list of instruction nodes to append to
	 */
	public void writeGetValue(InsnList toProduce);
}
