package wyvern.tools.typedAST.core.declarations;

import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;

public class RecConstructDeclaration extends Declaration implements CoreAST {
    private ExpressionAST definition;
    private NameBinding binding;
    private Type declaredType;
    private String variableName;
    private ValueType cachedValueType;
    private FileLocation location = FileLocation.UNKNOWN;

    public RecConstructDeclaration(String name, Type type, TypedAST definition, FileLocation location) {
        if (type instanceof UnresolvedType) {
            this.variableName = name;
            this.declaredType = type;
        }

        this.definition = (ExpressionAST) definition;
        this.binding = new NameBindingImpl(name, type);
        this.location = location;
    }

  @Override
  public Type getType() {
    return binding.getType();
  }

  @Override
  public String getName() {
    return binding.getName();
  }

  @Override
  public FileLocation getLocation() {
    return this.location;
  }

  public ExpressionAST getDefinition() {
    return this.definition;
  }

  @Override
  public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
    return visitor.visit(state, this);
  }

  @Override
  public void genTopLevel(TopLevelContext tlc) {
    ValueType declType = getILValueType(tlc.getContext());
    tlc.addLet(new BindingSite(getName()), getILValueType(tlc.getContext()),
        this.definition.generateIL(tlc.getContext(), declType, tlc.getDependencies()), false);
  }

  @Override
  public void checkAnnotated(GenContext ctxWithoutThis) {
    if (binding.getType() == null) {
      try {
        ValueType vt = getILValueType(ctxWithoutThis);
      } catch (RuntimeException e) {
        ToolError.reportError(ErrorMessage.REC_NEEDS_TYPE, this, binding.getName());
      }
    }
  }

  @Override
  public DeclType genILType(GenContext ctx) {
    ValueType vt = getILValueType(ctx);
    return new ValDeclType(getName(), vt);
  }

  private ValueType getILValueType(GenContext ctx) {
    if (cachedValueType != null) {
      return cachedValueType;
    }

    ValueType vt;
    if (declaredType != null) {
      vt = declaredType.getILType(ctx);
    } else {

      final Type type = this.binding.getType();
      if (type != null) {
        vt = type.getILType(ctx);
      } else {
        vt = definition.generateIL(ctx, null, null).typeCheck(ctx, null);
      }
    }
    cachedValueType = vt;
    return vt;
  }

  @Override
  public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {

    ValueType expectedType = getILValueType(thisContext);
    return new wyvern.target.corewyvernIL.decl.RecConstructDeclaration(getName(), expectedType,
        definition.generateIL(ctx, expectedType, null), location);
  }

  @Override
  public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addModuleDecl(TopLevelContext tlc) {
    wyvern.target.corewyvernIL.decl.Declaration decl =
        new wyvern.target.corewyvernIL.decl.RecConstructDeclaration(getName(), getILValueType(tlc.getContext()),
            new wyvern.target.corewyvernIL.expression.Variable(getName()), location);
    DeclType dt = genILType(tlc.getContext());
    tlc.addModuleDecl(decl, dt);
  }

  public String toString() {
    return variableName + ": " + declaredType.toString() + " = ...";
  }

  @Override
  public StringBuilder prettyPrint() {
    StringBuilder sb = new StringBuilder();
    sb.append(variableName);
    sb.append(" : ");
    if (declaredType != null) {
      sb.append(declaredType.toString());
    } else {
      sb.append("null");
    }
    sb.append(" = ");
    if (definition != null) {
      sb.append(definition.prettyPrint());
    } else {
      sb.append("null");
    }
    return sb;
  }
}
