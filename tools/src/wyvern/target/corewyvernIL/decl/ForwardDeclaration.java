package wyvern.target.corewyvernIL.decl;

import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class ForwardDeclaration extends Declaration {

    private ValueType valueType;
    private String fieldName;

    public ForwardDeclaration(ValueType valueType, String fieldName, FileLocation loc) {
        super(loc);
        this.valueType = valueType;
        this.fieldName = fieldName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }
}
