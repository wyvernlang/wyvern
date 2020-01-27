package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class Variable extends Expression implements Path {

    private String name;
    private BindingSite site;
    private boolean isExplicitConversion;

    public Variable(BindingSite site) {
        this(site.getName());
        this.site = site;
        this.isExplicitConversion = false;
    }
    
    public Variable(BindingSite site, FileLocation loc) {
        super(loc);
        this.name = site.getName();
        this.site = site;
        this.isExplicitConversion = false;
    }
    
    public Variable(String name, FileLocation loc) {
        super(loc);
        this.name = name;
        this.isExplicitConversion = false;
    }

    public Variable(String name) {
        super();
        this.name = name;
        this.isExplicitConversion = false;
    }
    
    public void setExplicitConversionFlag() {
      this.isExplicitConversion = true;
    }

    public boolean getExplicitConversionFlag() {
      return this.isExplicitConversion;
    }
    
    @Override
    public Variable locationHint(FileLocation loc) {
        if (getLocation() == null) {
            return (site == null) ? new Variable(name, loc) : new Variable(site, loc);
        } else {
            return this;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
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
        Variable other = (Variable) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        // if sites are non-null, compare sites
        if (site != null && other.site != null && site != other.site) {
            return false;
        }
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(name);
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        return BytecodeOuterClass.Expression.newBuilder().setVariable(name).build();
    }

    public String getName() {
        return name;
    }

    public BindingSite getSite() {
        return site;
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        ValueType matchedType = env.lookupTypeOf(this);
        if (getExplicitConversionFlag()) {

          // obtain the option type of the matched type
          ValueType optionType = wyvern.tools.typedAST.core.expressions.Assignment.getOptionType(matchedType);

          // return the option type if null is not returned. Otherwise throw RuntimeException
          if (optionType != null) {
            return optionType;
          } else {
            throw new RuntimeException("Explicit type conversion only applies to Wyvern option types, not " + matchedType.getClass());
          }
        }
        return matchedType;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Value exp =  ctx.lookupValue(name);
        if (getExplicitConversionFlag()) {
          exp = ((wyvern.target.corewyvernIL.expression.ObjectValue) exp).getField("content");
          // System.out.println(exp);
        }

        return exp;
    }

    @Override
    public boolean isPath() {
        return true;
    }

    @Override
    public Path adapt(View v) {
        return v.adapt(this);
    }

    public Set<String> getFreeVariables() {
        Set<String> freeVars = new HashSet<>();
        freeVars.add(this.getName());
        return freeVars;
    }
    public void siteFound(BindingSite site2) {
        if (this.site == null) {
            /*if ("ASTIDENT$1".equals(this.name)) {
                System.out.flush();
            }*/
            this.site = site2;
        }
    }
    @Override
    public Path adaptVariables(GenContext ctx) {
        IExpr expr = ctx.lookupExp(name, this.getLocation());
        if (!(expr instanceof Path)) {
            throw new RuntimeException("invariant violated");
        }
        if (expr.equals(this)) {
            return this;
        } else {
            return (Path) expr;
        }
    }
    @Override
    public boolean hasFreeVariable(String name) {
        return this.name.equals(name);
    }
    @Override
    public void canonicalize(TypeContext ctx) {
        if (site == null) {
            site = ctx.find(name);
        }
    }
}
