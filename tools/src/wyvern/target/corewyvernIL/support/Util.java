package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;

public class Util {
	public static ValueType intType() { return theIntType; }
	private static ValueType theIntType = new NominalType("system","Int");
}
