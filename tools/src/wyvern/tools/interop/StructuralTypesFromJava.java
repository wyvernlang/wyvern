package wyvern.tools.interop;

import java.util.ArrayList;
import java.util.List;

import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class StructuralTypesFromJava extends StructuralType {

    public StructuralTypesFromJava() {
        super("java_types", new ArrayList<DeclType>());
    }

    public ValueType getJavaType(Class<?> javaClass, TypeContext ctx) {
        Package package1 = javaClass.getPackage();
        String packageName = package1 == null ? "noPackage" : package1.getName();
        String className = javaClass.getSimpleName();
        DeclType packageDecl = findDecl(packageName, ctx);
        if (packageDecl == null) {
            packageDecl = new ValDeclType(packageName, new StructuralTypesFromJava());

            List<DeclType> newDeclTypes = new ArrayList<DeclType>(this.getDeclTypes().size() + 1);
            newDeclTypes.addAll(this.getDeclTypes());
            newDeclTypes.add(packageDecl);
            this.setDeclTypes(newDeclTypes);
        }
        StructuralTypesFromJava packageType = (StructuralTypesFromJava) ((ValDeclType) packageDecl).getRawResultType();
        ConcreteTypeMember classDecl = (ConcreteTypeMember) packageType.findDecl(className, ctx);
        if (classDecl == null) {
            LazyStructuralType classType = new LazyStructuralType(javaClass, ctx);
            classDecl = new ConcreteTypeMember(className, classType);

            List<DeclType> newDeclTypes = new ArrayList<DeclType>(packageType.getDeclTypes().size() + 1);
            newDeclTypes.addAll(packageType.getDeclTypes());
            newDeclTypes.add(classDecl);
            packageType.setDeclTypes(newDeclTypes);
        }
        Path path = new FieldGet(GenUtil.getJavaTypesObject(), packageName, null);
        ValueType type = new NominalType(path, className);
        return type;
    }
}
