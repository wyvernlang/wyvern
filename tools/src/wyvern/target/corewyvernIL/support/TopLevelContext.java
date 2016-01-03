package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javafx.util.Pair;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class TopLevelContext {
	private Stack<Pair<String,Expression>> pending = new Stack<Pair<String,Expression>>();
	//private List<Pair<Declaration,DeclType>> moduleDecls = new LinkedList<Pair<Declaration,DeclType>>();
	private List<Declaration> moduleDecls = new LinkedList<Declaration>();
	private List<DeclType> moduleDeclTypes = new LinkedList<DeclType>();
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
		Pair<String,Expression> pair = pending.pop();
		Expression exp = pair.getValue();
		while (!pending.isEmpty()) {
			pair = pending.pop();
			exp = new Let(pair.getKey(), pair.getValue(), exp);
		}
		return exp;
	}

	public Expression getModuleExpression() {
		String newName = GenContext.generateName();
		ValueType vt = new StructuralType(newName, moduleDeclTypes);
		vt = adapt(vt, newName);
		Expression exp = new New(moduleDecls, newName, vt);
		addExpression(exp);
		
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
				newPath = new FieldGet(receiver, e.getKey());
			}
			View view = new ReceiverView(v, newPath);
			vt = vt.adapt(view);
		}
		return vt;
	}
	
	public void addExpression(Expression exp) {
		pending.push(new Pair<String,Expression>(GenContext.generateName(), exp));
		/*if (expression == null) {
			expression = exp;
		} else {
			if (name == null)
				name = GenContext.generateName();
			expression = new Let(name, expression, exp); 
		}
		name = null;*/
	}

	public void addLet(String name, ValueType type, Expression exp, boolean isDeclBlock) {
		pending.push(new Pair<String,Expression>(name, exp));
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
}
