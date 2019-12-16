package wyvern.target.corewyvernIL.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ForwardDeclaration;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.RuntimeError;
import wyvern.tools.errors.ToolError;

public class ObjectValue extends New implements Invokable {
    private static final ThreadLocal<List<String>> stack = ThreadLocal.withInitial(() -> new ArrayList<String>());
    private final EvalContext evalCtx; // captured eval context
    private final boolean hasForward;
    private ObjectValue forwardTarget;

    /** Precondition: the decls argument must be unique.
     * It is owned by this ObjectValue after the constructor call.
     */
    public ObjectValue(List<Declaration> decls, BindingSite selfSite, ValueType exprType, ForwardDeclaration forwardDecl, FileLocation loc, EvalContext ctx) {
        super(decls, selfSite, exprType, loc);
        
        if (selfSite == null) {
            throw new RuntimeException("selfName invariant violated");
        }
        evalCtx = ctx.extend(selfSite, this);
        hasForward = (forwardDecl != null);
        if (hasForward) {
            forwardTarget = (ObjectValue) ctx.lookupValue(forwardDecl.getFieldName());
        }
        // assert that this ObjectValue is well-formed
        checkWellFormed();
    }

    /** already a value */
    @Override
    public Value interpret(EvalContext ctx) {
        return this;
    }
    
    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return getType();
    }
    
    public Tag getTag() {
        ValueType vt = this.getType();
        while (vt instanceof RefinementType) {
            vt = ((RefinementType) vt).getBase();
        }
        if (!(vt instanceof NominalType)) {
            throw new RuntimeError("internal invariant: can only get the tag of a nominal type, did this typecheck?");
        }
        NominalType nt = (NominalType) vt;
        return nt.getTag(this.getEvalCtx());
    }

    @Override
    public Value invoke(String methodName, List<Value> args, FileLocation loc) {
        List<String> theStack = stack.get();
        try {
            theStack.add(methodName);
            EvalContext methodCtx = evalCtx;
            DefDeclaration dd = (DefDeclaration) findDecl(methodName, false);
            if (dd != null) {
                if (args.size() != dd.getFormalArgs().size()) {
                    throw new RuntimeException("invoke called on " + methodName + " with " + args.size() + " arguments, "
                            + "but " + dd.getFormalArgs().size() + " were expected");
                }
                for (int i = 0; i < args.size(); ++i) {
                    methodCtx = methodCtx.extend(dd.getFormalArgs().get(i).getSite(), args.get(i));
                }
                return dd.getBody().interpret(methodCtx);
            } else if (hasForward) {
                return forwardTarget.invoke(methodName, args);
            } else {
                if (Util.isJavaNull(this)) {
                    ToolError.reportError(ErrorMessage.JAVA_NULL_EXCEPTION, loc, methodName);
                }
                ToolError.reportError(ErrorMessage.DYNAMIC_METHOD_ERROR, loc, methodName);
                throw new RuntimeException("can't reach here");
            }
        } catch (StackOverflowError e) {
            System.err.println("Stack overflow.  Method stack:");
            for (int i = theStack.size() - 1; i >= 0; --i) {
                System.err.println(theStack.get(i) + "(...)");
            }
            ToolError.reportError(ErrorMessage.STACK_OVERFLOW, loc);
            throw new RuntimeException("stack overflow"); // never get here
        } finally {
            theStack.remove(theStack.size() - 1);
        }
    }

    @Override
    public Value getField(String fieldName) {
        DeclarationWithRHS decl = (DeclarationWithRHS) findDecl(fieldName, false);
        if (decl != null) {
            return (Value) decl.getDefinition();
        } else if (forwardTarget != null && forwardTarget.findDecl(fieldName, false) != null) {
            return forwardTarget.getField(fieldName);
        }

        throw new RuntimeException("can't find field: " + fieldName);
    }

    public void setDecl(Declaration decl) {
        List<Declaration> decls = this.getDecls();
        for (int i = 0; i < decls.size(); ++i) {
            if (decl.getName().equals(decls.get(i).getName())) {
                decls.set(i, decl);
                return;
            }
        }
        throw new RuntimeException("cannot set decl " + decl.getName());
    }

    public EvalContext getEvalCtx() {
        return this.evalCtx;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ObjectValue other = (ObjectValue) obj;

        // Other ObjectValue needs the same declarations, in the same order.
        if (this.getDecls().size() != other.getDecls().size()) {
            return false;
        }
        return this.getDecls().equals(other.getDecls());

    }

    @Override
    public int hashCode() {
        return evalCtx.hashCode() + forwardTarget.hashCode();
    }

    /** make sure all free variables are captured in the evalCtx */
    public void checkWellFormed() {
        Set<String> freeVars = this.getFreeVariables();
        for (String varName : freeVars) {
            evalCtx.lookupValue(varName);
        }
    }

    /** no free variables because each ObjectValue closes over its environment */
    @Override
    public Set<String> getFreeVariables() {
        return (Set<String>) Collections.EMPTY_SET;
    }
}
