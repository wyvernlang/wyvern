package wyvern.tools.util;

/**
 * During IL generation, getter and setter methods are automatically generated for mutable fields.
 * This class provides a uniform description of how the names of those methods relate to the name
 * of the mutable variable.
 */
public class GetterAndSetterGeneration {

	/**
	 * From the name of a mutable variable, return the "this" name of the object containing the
	 * mutable field and the getter/setter methods operating on that mutable field. 
	 */
	public static String varNameToTempObj (String s) {
		return "_temp" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	/**
	 * From the name of a mutable variable, return the name of its getter method.
	 */
	public static String varNameToGetter (String s) {
		return "_get" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	/**
	 * From the name of a mutable variable, return the name of its setter method.
	 */
	public static String varNameToSetter (String s) {
		return "_set" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	/**
	 * From the name of a getter method, return the name of the variable it is accessing.
	 */
	public static String getterToVarName (String s) {
		return s.replaceFirst("_get", "");
	}
	
}
