package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class DefDeclaration extends NamedDeclaration {
	private List<FormalArg> formalArgs;
	private ValueType type;
	private IExpr body;
	private boolean hasResource = false;
	private EffectSet effectSet;

	public DefDeclaration(String methodName, List<FormalArg> formalArgs,
			ValueType type, IExpr iExpr, FileLocation loc) {
		this(methodName, formalArgs, type, iExpr, loc, null);
	}
	
	public DefDeclaration(String methodName, List<FormalArg> formalArgs,
			ValueType type, IExpr iExpr, FileLocation loc, EffectSet effectSet) {
		super(methodName, loc);
		this.formalArgs = formalArgs;
		if (type == null) throw new RuntimeException();
		this.type = type;
		this.body = iExpr;
		this.effectSet = effectSet;
	}

	@Override
	public boolean containsResource(TypeContext ctx) {
		return this.hasResource;
	}

	private void setHasResource(boolean hasResource) {
		this.hasResource = hasResource;
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("def ").append(getName()).append('(');
		boolean first = true;
		for (FormalArg arg: formalArgs) {
			if (first)
				first = false;
			else
				dest.append(", ");
			arg.doPrettyPrint(dest, indent);
		}
		String newIndent = indent+"    ";
		dest.append(") : ");
		if (effectSet != null) {dest.append(effectSet.toString());}
		type.doPrettyPrint(dest, newIndent);
		dest.append('\n').append(newIndent);
		body.doPrettyPrint(dest,newIndent);
		dest.append('\n');
	}

	/*@Override
	public String toString() {
		return "DefDeclaration[" + getName() + "(...) : " + type + " = " + body + "]";
	}*/

	public List<FormalArg> getFormalArgs() {
		return formalArgs;
	}

	public ValueType getType() {
		return type;
	}

	public IExpr getBody() {
		return body;
	}
	
	public EffectSet getEffectSet() {
		return effectSet;
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		TypeContext methodCtx = thisCtx;
		for (FormalArg arg : formalArgs) {
			methodCtx = methodCtx.extend(arg.getName(), arg.getType());
		}
		if (!this.containsResource(methodCtx)) {
			for (String freeVar : this.getFreeVariables()) {
				ValueType t = (new Variable(freeVar)).typeCheck(methodCtx, null);
				if (t != null && t.isResource(methodCtx)) {
					this.setHasResource(true);
					break;
				}
			}
		}
		
		// if the method makes no claim about the effects it has, do not check its calls for effects (i.e. null)
		EffectAccumulator effectAccumulator = (effectSet==null) ? null : new EffectAccumulator();
		
		ValueType bodyType = body.typeCheck(methodCtx, effectAccumulator);
		
		if (effectSet != null){ effectsCheck(methodCtx, effectAccumulator); }	
		
		if (!bodyType.isSubtypeOf(getType(), methodCtx)) {
			// for debugging
			ValueType resultType = getType();
			bodyType.isSubtypeOf(resultType, methodCtx);
			ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, "method body's type", "declared type");;
			
		}
		return new DefDeclType(getName(), type, formalArgs, effectSet);
	}
	
	/** check that all effects in annotation exist (assume that those from method calls are valid). */
	private void effectsCheck(TypeContext methodCtx, EffectAccumulator effectAccumulator) {
		// TODO: make uniform, regardless of whether we're in an obj definition or module def
		if (effectSet.getEffects() != null) {
				ValueType vt = null;
				try { // if we're currently in an object
					vt = methodCtx.lookupTypeOf("this");
				} catch (RuntimeException ex) { // might be a module def instead
					effectSet.effectsCheck(methodCtx);
				}
				
				// finish effect-checking for effect in instantiated obj; set its path as "this" if successful
				if (vt != null) { 
					for (Effect e : effectSet.getEffects()) {
						e.findEffectDeclType(methodCtx, vt);
//						e.setPath(new Variable("this"));
					}
				}
				
				Set<Effect> actualEffectSet = effectAccumulator.getEffectSet();
				
				// compare method call effects with annotated ones
				EffectDeclType actualEffects = new EffectDeclType(getName()+"-actualEffects", new EffectSet(actualEffectSet), getLocation());
				EffectDeclType annotatedEffects = new EffectDeclType(getName()+"-annotatedEffects", effectSet, getLocation());
				if (!actualEffects.isSubtypeOf(annotatedEffects, methodCtx)) { // changed from ctx
					ToolError.reportError(ErrorMessage.NOT_SUBTYPE, getLocation(), 
							"set of effects from the method calls "+actualEffectSet.toString(),
							"set of effects specified by "+getName()+effectSet.toString());
				}
		}
	}

	@Override
	public Set<String> getFreeVariables() {
		// Get all free variables in the body of the method.
		Set<String> freeVars = body.getFreeVariables();
		
		// Remove variables that became bound in this method's scope.
		for (FormalArg farg : formalArgs) {
			freeVars.remove(farg.getName());
		}
		return freeVars;
	}
	
	@Override
	public DeclType getDeclType() {
		return new DefDeclType(getName(), type, formalArgs, getEffectSet());
	}
}
