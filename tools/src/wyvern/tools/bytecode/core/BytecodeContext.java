package wyvern.tools.bytecode.core;

import wyvern.tools.bytecode.values.BytecodeValue;

/**
 * the context being used by the intermediate level interpreter
 * @author Tal Man
 *
 */
public interface BytecodeContext {
	
	/**
	 * checks whether a name exists in the context
	 * @param valName
	 * 		the name to be looked for in the context
	 * @return
	 * 		a boolean value representing whether the name exists
	 */
	public boolean existsInContext(String valName);
	
	/**
	 * gets the value associated with a name in the context
	 * @param valName
	 * 		the name to be looked for in the context
	 * @return
	 * 		a BytecodeValue object representing the value found
	 * @throws RuntimeException
	 * 		if the name does not exist in the context
	 */
	public BytecodeValue getValue(String valName);

	/*
	 * for testing purposes only, prints the context while ingoring every
	 * variable with a '$' in it, for a nicer print, the toString() method
	 * will print the entire context including the temps with the '$'
	 */
	public String toSimpleString();
	
	/**
	 * insert a new value into the context
	 * @param name
	 * 		the name to be associated with the new value
	 * @param val
	 * 		the value to be inserted
	 */
	public void addToContext(String name, BytecodeValue val);
	
}
