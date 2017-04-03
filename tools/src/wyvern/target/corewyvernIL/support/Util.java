package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class Util {
	private static ValueType theBooleanType = new NominalType("system","Boolean");
	private static ValueType theEmptyType = new StructuralType("empty",new LinkedList<>());
	private static ValueType theIntType = new NominalType("system","Int");
	private static ValueType theStringType = new NominalType("system","String");
	private static ValueType theUnitType = new StructuralType("unitSelf", new LinkedList<DeclType>());
	private static ValueType theDynType = new DynamicType();

	public static ValueType booleanType() { return theBooleanType; }
	public static ValueType emptyType() { return theEmptyType; }
	public static ValueType intType() { return theIntType; }
	public static ValueType stringType() { return theStringType; }
	public static ValueType unitType() { return theUnitType; }
	public static ValueType dynType() { return theDynType; }
	public static ValueType unitToDynType() {
		LinkedList<DeclType> arrowDecls = new LinkedList<>();
		arrowDecls.add(new DefDeclType("apply", new DynamicType(), new LinkedList<FormalArg>()));
		return new StructuralType("arrow", arrowDecls, true);
	}
	public static ValueType listType() {
      // LinkedList<DeclType> listDecls = new LinkedList<DeclType>();
      // listDecls.add(new ValDeclType("length", new IntegerType()));
      // listDecls.add(new DefDeclType("getVal", theEmptyType, new LinkedList<FormalArg>()));
      // listDecls.add(new DefDeclType("getVal", new NominalType("system", "List"), new LinkedList<FormalArg>()));
      // return new StructuralType("list", listDecls);
      // return new NominalType("Lists", "List");

      // LinkedList<DeclType> listDecls = new LinkedList<>();
      // listDecls.add(new DefDeclType("length", theIntType, new LinkedList<FormalArg>()));

      // LinkedList<FormalArg> getArgs = new LinkedList<>();
      // getArgs.add(new FormalArg("n", theIntType));
      // listDecls.add(new DefDeclType("get", theDynType, getArgs));

      // LinkedList<FormalArg> appendArgs = new LinkedList<>();
      // appendArgs.add(new FormalArg("value", theDynType));
      // listDecls.add(new DefDeclType("append", theUnitType, appendArgs));
      // return new StructuralType("list", listDecls);

      return new NominalType("list", "List");
	}
	public static ObjectValue unitValue() {
		return new ObjectValue(new LinkedList<Declaration>(), "unitSelf", theUnitType, null, null, EvalContext.empty());
	}
	public static final String APPLY_NAME = "apply";
	
	public static boolean isDynamicType(ValueType type) {
		return type.equals(new NominalType("system", "Dyn"))
				|| type instanceof DynamicType;
	}
	
}
