package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class Util {
	private static ValueType theBooleanType = new NominalType("system","Boolean");
	private static ValueType theEmptyType = new StructuralType("empty",new LinkedList<>());
	private static ValueType theIntType = new NominalType("system","Int");
	private static ValueType theStringType = new NominalType("system","String");
	private static ValueType theUnitType = new StructuralType("unitSelf", new LinkedList<DeclType>());
	public static ValueType booleanType() { return theBooleanType; }
	public static ValueType emptyType() { return theEmptyType; }
	public static ValueType intType() { return theIntType; }
	public static ValueType stringType() { return theStringType; }
	public static ValueType unitType() { return theUnitType; }
	public static Value unitValue() {
		return new ObjectValue(new LinkedList<Declaration>(), "unitSelf", theUnitType, null, EvalContext.empty());
	}
	public static final String APPLY_NAME = "apply";
}
