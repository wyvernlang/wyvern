package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.types.TypeUtils;

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
	private List<Declaration> moduleDecls = new LinkedList<Declaration>();
	private List<DeclType> moduleDeclTypes = new LinkedList<DeclType>();
	private Map<String, Boolean> avoidanceMap = new HashMap<String, Boolean>();
	private GenContext ctx;
	private String receiverName;
	private Map<String, String> topLevelVars = new HashMap<String, String>();
	
	public TopLevelContext(GenContext ctx) {
		this.ctx = ctx;
	}

	public GenContext getContext() {
		return ctx;
	}
	
	/**
	 * Get the anonymous object name corresponding to the supplied variable name.
	 * If the anonymous object name has not been generated then this will return null.
	 * @param varName: variable to look up
	 * @return name of anonymous object encapsulating the variable.
	 */
	public Optional<String> anonymousObjectName (String varName) {
		String result = topLevelVars.get(varName);
		if (result == null) return Optional.empty();
		else return Optional.of(result);
	}
	
	/**
	 * Return an invocation of the anonymous object encapsulating the supplied variable name.
	 * @param varName: variable to look up
	 * @return an invocation of the anonymous object
	 */
	public Optional<wyvern.tools.typedAST.core.expressions.Variable>
	anonymousObjectReference (String varName) {
		Optional<String> anonNameOpt = anonymousObjectName(varName);
		if (!anonNameOpt.isPresent()) return Optional.empty();
		String anonName = anonNameOpt.get();
		return Optional.of(new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl(anonName, null), null));
	}
	
	/**
	 * Generate and store the name of the anonymous object that corresponds to the
	 * given variable name.
	 * @param varName
	 */
	public String anonymousObjectGenerate (String varName) {
		String anonName = "_temp_" + varName;
		this.topLevelVars.put(varName, anonName);
		return "_temp_" + varName;
	}
	
	private static final String anonymousGetterName = "get";
	private static final String anonymousSetterName = "set";
	public static final String anonymousGetterName() { return anonymousGetterName; }
	public static final String anonymousSetterName() { return anonymousSetterName; }
	
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
