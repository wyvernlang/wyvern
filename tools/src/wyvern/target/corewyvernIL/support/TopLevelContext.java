package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.util.Pair;

public class TopLevelContext {
	private Stack<VarBinding> pending = new Stack<VarBinding>();
	//private List<Pair<Declaration,DeclType>> moduleDecls = new LinkedList<Pair<Declaration,DeclType>>();
	private List<Declaration> moduleDecls = new LinkedList<Declaration>();
	private List<DeclType> moduleDeclTypes = new LinkedList<DeclType>();
	private List<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
	private Map<String, Boolean> avoidanceMap = new HashMap<String, Boolean>();
	private GenContext ctx;
	private String receiverName;
	//private Expression expression;
	//private String name;			// null if the current expression was not let-bound
	//private ValueType type;

	public TopLevelContext(GenContext ctx) {
		this.ctx = ctx;
	}

	public GenContext getContext() {
		return ctx;
	}

	public Expression getExpression() {
		VarBinding binding = pending.pop();
		Expression exp = binding.getExpression();
		while (!pending.isEmpty()) {
			binding = pending.pop();
			exp = new Let(binding, exp);
		}
		//exp = (Expression) ctx.getInterpreterState().getResolver().wrap(exp, dependencies);
		
		return exp;
	}

	public Expression getModuleExpression() {
		String newName = GenContext.generateName();
		
		// determine if we need to be a resource type
		boolean isModule = false;
		for (Declaration d: moduleDecls) {
			d.typeCheck(ctx, ctx);
			if (d.containsResource(ctx)) {
				isModule = true;
				break;
			}
		}
		
		ValueType vt = new StructuralType(newName, moduleDeclTypes, isModule);
		vt = adapt(vt, newName);
		
		Expression exp = new New(moduleDecls, newName, vt, null);
		addExpression(exp, vt);
		
		return getExpression();
	}
	/** Adapts the type vt to account for the names we have to
	 * avoid.
	 */
	private ValueType adapt(ValueType vt, String thisName) {
		for (Map.Entry<String, Boolean> e : avoidanceMap.entrySet()) {
			Variable v = new Variable(e.getKey());
			boolean isDeclBlock = e.getValue();
			Variable receiver = new Variable(thisName);
			Path newPath = receiver;
			if (!isDeclBlock) {
				newPath = new FieldGet(receiver, e.getKey(), receiver.getLocation());
			}
			View view = new ReceiverView(v, newPath);
			vt = vt.adapt(view);
		}
		return vt;
	}
	
	public void addExpression(Expression exp, ValueType type) {
		pending.push(new VarBinding(GenContext.generateName(), type, exp));
		/*if (expression == null) {
			expression = exp;
		} else {
			if (name == null)
				name = GenContext.generateName();
			expression = new Let(name, expression, exp); 
		}
		name = null;*/
	}
	/**
	 * Adds a binding to the sequence being generated
	 * 
	 * @param name	the name of the variable being bound
	 * @param type	the variable's type
	 * @param exp	the right-hand side of the binding
	 * @param isDeclBlock flags a let statement that represents a block of recursive declarations, or a var
	 */
	public void addLet(String name, ValueType type, Expression exp, boolean isDeclBlock) {
		pending.push(new VarBinding(name, type, exp));
		ctx = ctx.extend(name, new Variable(name), type);
		avoidanceMap.put(name, isDeclBlock);
	}

	public void updateContext(GenContext newCtx) {
		ctx = newCtx;
	}

	public void addModuleDecl(Declaration decl, DeclType dt) {
		//moduleDecls.add(new Pair<Declaration,DeclType>(decl, dt));
		moduleDecls.add(decl);
		moduleDeclTypes.add(dt);
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
