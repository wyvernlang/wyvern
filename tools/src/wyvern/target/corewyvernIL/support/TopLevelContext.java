package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.Stack;

import wyvern.target.corewyvernIL.BindingSite;
//import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
//import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class TopLevelContext {

    //private Stack<VarBinding> pending = new Stack<VarBinding>();
    private LinkedList<Declaration> moduleDecls = new LinkedList<Declaration>();
    private LinkedList<DeclType> moduleDeclTypes = new LinkedList<DeclType>();
    private List<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
    private Map<BindingSite, Boolean> avoidanceMap = new HashMap<BindingSite, Boolean>();
    private GenContext ctx;
    private String receiverName;
    private SeqExpr expr;
    //private GenContext origCtx;

    public TopLevelContext(GenContext ctx, ValueType expectedType) {
        this.ctx = ctx;
        this.expr = new SeqExpr(expectedType);
        //origCtx = ctx;
    }

    public GenContext getContext() {
        return ctx;
    }

    public IExpr getExpression() {
        /*VarBinding binding = pending.pop();
        IExpr exp = binding.getExpression();
        while (!pending.isEmpty()) {
            binding = pending.pop();
            exp = new Let(binding, exp);
        }
        return exp;*/
        //expr.typeCheck(origCtx, null);
        return expr;
    }

    public IExpr getModuleExpression() {
        String newName = GenContext.generateName();

        // Determine if we need to be a resource type.
        boolean isModule = false;
        for (Declaration d: moduleDecls) {
            d.typeCheck(ctx, ctx);
            if (d.containsResource(ctx)) {
                isModule = true;
                break;
            }
        }

        StructuralType vt = new StructuralType(newName, moduleDeclTypes, isModule);
        vt = (StructuralType) adapt(vt, newName);

        New exp = new New(moduleDecls, vt.getSelfSite(), vt, null);
        exp.setModuleFlag();
        addExpression(exp, vt);

        return getExpression();
    }
    /** Adapts the type vt to account for the names we have to
     * avoid.
     */
    private ValueType adapt(ValueType vt, String thisName) {
        for (Map.Entry<BindingSite, Boolean> e : avoidanceMap.entrySet()) {
            Variable v = new Variable(e.getKey());
            boolean isDeclBlock = e.getValue();
            Variable receiver = new Variable(thisName);
            Path newPath = receiver;
            if (!isDeclBlock) {
                newPath = new FieldGet(receiver, e.getKey().getName(), receiver.getLocation());
            }
            View view = new ReceiverView(v, newPath);
            vt = vt.adapt(view);
        }
        return vt;
    }

    public void addExpression(IExpr exp, ValueType type) {
        //pending.push(new VarBinding(GenContext.generateName(), type, exp));
        expr.addExpr(exp);
    }

    /**
     * Adds a binding to the sequence being generated
     *
     * @param name  the name of the variable being bound
     * @param type  the variable's type
     * @param iExpr the right-hand side of the binding
     * @param isDeclBlock flags a let statement that represents a block of recursive declarations, or a var
     */
    public void addLet(BindingSite site, ValueType type, IExpr iExpr, boolean isDeclBlock) {
        //pending.push(new VarBinding(name, type, iExpr));
        ctx = ctx.extend(site, new Variable(site), type);
        avoidanceMap.put(site, isDeclBlock);
        expr.addBindingLast(site, type, iExpr);
    }

    public void updateContext(GenContext newCtx) {
        ctx = newCtx;
    }

    public void addModuleDecl(Declaration decl, DeclType dt) {
        decl = copyTypes(decl);
        moduleDecls.add(decl);
        moduleDeclTypes.add(dt);
    }
    
    /** Undoes the last 'count' adds
     * 
     * @param count
     */
    public void undoAddModuleDecls(int count) {
        for (int i = 0; i < count; ++i) {
            moduleDecls.removeLast();
            moduleDeclTypes.removeLast();
        }
    }

    private Declaration copyTypes(Declaration decl) {
        if (decl instanceof TypeDeclaration) {
            TypeDeclaration td = (TypeDeclaration) decl;
            return new TypeDeclaration(
                    td.getName(),
                    new NominalType(new Variable(getReceiverName()), td.getName(), decl.getLocation()),
                    td.getMeta(),
                    decl.getLocation());
        }
        return decl;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String rn) {
        receiverName = rn;
    }

    public List<TypedModuleSpec> getDependencies() {
        return dependencies;
    }
}
