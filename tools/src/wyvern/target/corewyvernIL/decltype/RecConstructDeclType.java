package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;

public class RecConstructDeclType extends DeclTypeWithResult implements IASTNode {

  public RecConstructDeclType(String field, ValueType type) {
    super(field, type);
  }

  @Override
  public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSubtypeOf(DeclType dt, TypeContext ctx, FailureReason reason) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public DeclType adapt(View v) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void checkWellFormed(TypeContext ctx) {
    // TODO Auto-generated method stub

  }

  @Override
  public DeclType doAvoid(String varName, TypeContext ctx, int count) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isTypeOrEffectDecl() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEffectAnnotated(TypeContext ctx) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEffectUnannotated(TypeContext ctx) {
    // TODO Auto-generated method stub
    return false;
  }

}
