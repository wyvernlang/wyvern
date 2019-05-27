package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.FloatLiteral;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.BottomType;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;

public final class Util {
    public static final String NULL_SELF = "nullSelf";

    private Util() { }
    private static final ValueType theBooleanType = new NominalType("system", "Boolean");
    private static final ValueType theEmptyType = new StructuralType("empty", new LinkedList<>());
    private static final ValueType theIntType = new NominalType("system", "Int");
    private static final ValueType theFloatType = new NominalType("system", "Float");
    private static final ValueType theCharType = new NominalType("system", "Character");
    private static final ValueType theStringType = new NominalType("system", "String");
    private static final ValueType theUnitType = new StructuralType("unitSelf", new LinkedList<DeclType>());
    private static final ValueType theAnyType = new StructuralType("anySelf", new LinkedList<DeclType>(), true);
    private static final ValueType theDynType = new DynamicType();
    private static final ValueType theBottomType = new BottomType();

    public static ValueType booleanType() {
        return theBooleanType;
    }
    public static ValueType emptyType() {
        return theEmptyType;
    }
    public static ValueType floatType() {
      return theFloatType;
    }
    public static ValueType intType() {
        return theIntType;
    }
    public static ValueType charType() {
        return theCharType;
    }
    public static ValueType stringType() {
        return theStringType;
    }
    public static ValueType unitType() {
        return theUnitType;
    }
    public static Type anyType() {
        return theAnyType;
    }
    public static ValueType dynType() {
        return theDynType;
    }
    public static ValueType bottomType() {
        return theBottomType;
    }
    public static ValueType unitToDynType() {
        LinkedList<DeclType> arrowDecls = new LinkedList<>();
        arrowDecls.add(new DefDeclType("apply", new DynamicType(), new LinkedList<FormalArg>()));
        return new StructuralType("arrow", arrowDecls, true);
    }
    public static ValueType listType() {
        return new NominalType("list", "List");
    }
    public static ObjectValue unitValue() {
        return new ObjectValue(new LinkedList<Declaration>(), new BindingSite("unitSelf"), theUnitType, null, null, EvalContext.empty());
    }
    public static ObjectValue nullValue() {
        return new ObjectValue(new LinkedList<Declaration>(), new BindingSite(NULL_SELF), theUnitType, null, null, EvalContext.empty());
    }
    public static final String APPLY_NAME = "apply";

    public static boolean isDynamicType(ValueType type) {
        return type.equals(new NominalType("system", "Dyn"))
                || type instanceof DynamicType;
    }
    public static Value intValue(int i) {
        return new IntegerLiteral(i);
    }
    public static Value floatValue(double d)  {
        return new FloatLiteral(d);
    }
    public static boolean isJavaNull(ObjectValue objectValue) {
        return objectValue.getSelfName().equals(Util.NULL_SELF);
    }
}
