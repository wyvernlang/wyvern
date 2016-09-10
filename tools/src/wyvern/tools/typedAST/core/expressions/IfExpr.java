package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class IfExpr extends CachingTypedAST implements CoreAST {

    private Iterable<IfClause> clauses;
    private FileLocation location;

    public IfExpr(Iterable<IfClause> clauses, FileLocation location) {
        this.clauses = clauses;
        this.location = location;
    }

    @Override
    public Value evaluate(EvaluationEnvironment env) {
        for (IfClause clause : clauses) {
            if (clause.satisfied(env)) {
                return clause.evaluate(env);
            }
        }
        return UnitVal.getInstance(location);
    }

    @Override
    public Map<String, TypedAST> getChildren() {
        Map<String, TypedAST> childMap = new HashMap<>();
        int i = 0;
        for (IfClause clause : clauses) {
            childMap.put(i++ + "", clause);
        }
        return childMap;
    }

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        //TODO: compile to a match
    }

    @Override
    public ExpressionAST doClone(Map<String, TypedAST> newChildren) {
        ArrayList<IfClause> clauses = new ArrayList<>(newChildren.size());
        int i = 0;
        for (String s : newChildren.keySet()) {
            clauses.add(Integer.parseInt(s), (IfClause)newChildren.get(s));
        }
        return new IfExpr(clauses, location);
    }

    @Override
    public void writeArgsToTree(TreeWriter writer) {
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public void accept(CoreASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected Type doTypecheck(Environment env, Optional<Type> expected) {
        Type lastType = null;
        for (IfClause clause : clauses) {
            Type clauseType = clause.typecheck(env, expected);
            if (lastType == null) {
                lastType = clauseType;
                continue;
            }

            // FIXME:
            // System.out.println("clauseType = " + clauseType);
            // System.out.println("lastType = " + lastType);
            if (!clauseType.subtype(lastType) && !lastType.subtype(clauseType)) {
                ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, clause);
            }
        }
        if (lastType == null) {
            ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, this);
        }
        return lastType;
    }

    public Iterable<IfClause> getClauses() {
        return clauses;
    }

    @Override
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {
        // TODO Auto-generated method stub
        return null;
    }

    public abstract static class IfClause extends CachingTypedAST {
        public abstract boolean satisfied(EvaluationEnvironment env);

        public abstract TypedAST getClause();

        public abstract TypedAST getBody();

        protected abstract ExpressionAST createInstance(TypedAST clause, TypedAST body);

        @Override
        public Map<String, TypedAST> getChildren() {
            Map<String, TypedAST> childMap = new HashMap<>();
            childMap.put("clause", getClause());
            childMap.put("body", getBody());
            return childMap;
        }

        @Override
        public ExpressionAST doClone(Map<String, TypedAST> newChildren) {
            return createInstance(newChildren.get("clause"), newChildren.get("body"));
        }
    }

    public static class CondClause extends IfClause {
        private final TypedAST cond;
        private final TypedAST body;
        private FileLocation location;

        /**
          * Represents a conditional clause with the given condition and execution body.
          *
          * @param cond the guard of the if-statement
          * @param body the block to be executed if the guard is true
          * @param location the location in the source where this statement is located
          */
        public CondClause(TypedAST cond, TypedAST body, FileLocation location) {
            this.cond = cond;
            this.body = body;
            this.location = location;
        }

        @Override
        public boolean satisfied(EvaluationEnvironment env) {
            return ((BooleanConstant)cond.evaluate(env)).getValue();
        }

        @Override
        public TypedAST getClause() {
            return cond;
        }

        @Override
        public TypedAST getBody() {
            return body;
        }

        @Override
        protected ExpressionAST createInstance(TypedAST clause, TypedAST body) {
            return new CondClause(clause, body, location);
        }

        @Override
        protected Type doTypecheck(Environment env, Optional<Type> expected) {
            if (!(cond.typecheck(env, Optional.of(new Bool())).equals(new Bool()))) {
                throw new RuntimeException();
            }
            return body.typecheck(env, expected);
        }

        @Override
        public Value evaluate(EvaluationEnvironment env) {
            return body.evaluate(env);
        }

        @Override
        public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
            //TODO: match case
        }

        @Override
        public FileLocation getLocation() {
            return location;
        }

        @Override
        public void writeArgsToTree(TreeWriter writer) {

        }

        @Override
        public Expression generateIL(
                GenContext ctx,
                ValueType expectedType,
                List<TypedModuleSpec> dependencies) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class UncondClause extends IfClause {

        private final TypedAST body;
        private final FileLocation location;

        public UncondClause(TypedAST body, FileLocation location) {
            this.body = body;
            this.location = location;
        }

        @Override
        public boolean satisfied(EvaluationEnvironment env) {
            return true;
        }

        @Override
        public TypedAST getClause() {
            return new IntegerConstant(1337);
        }

        @Override
        public TypedAST getBody() {
            return body;
        }

        @Override
        protected ExpressionAST createInstance(TypedAST clause, TypedAST body) {
            return new UncondClause(body, location);
        }

        @Override
        protected Type doTypecheck(Environment env, Optional<Type> expected) {
            return body.typecheck(env, expected);
        }

        @Override
        public Value evaluate(EvaluationEnvironment env) {
            return body.evaluate(env);
        }

        @Override
        public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
            //TODO: match case
        }

        @Override
        public FileLocation getLocation() {
            return location;
        }

        @Override
        public void writeArgsToTree(TreeWriter writer) {

        }

        @Override
        public Expression generateIL(
                GenContext ctx,
                ValueType expectedType,
                List<TypedModuleSpec> dependencies) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
