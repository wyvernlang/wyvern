package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.evaluation.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;










import java.util.*;
import java.util.stream.Collectors;

public class Fn extends CachingTypedAST implements CoreAST, BoundCode {
	private List<NameBinding> bindings;
	ExpressionAST body;

	public Fn(List<NameBinding> bindings, TypedAST body) {
		this.bindings = bindings;
		this.body = (ExpressionAST) body;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(bindings, body);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		Type argType = null;
		for (int i = 0; i < bindings.size(); i++) {
			NameBinding bdgs = bindings.get(i);
			bindings.set(i, new NameBindingImpl(bdgs.getName(), TypeResolver.resolve(bdgs.getType(), env)));
		}

		if (bindings.size() == 0)
			argType = new Unit();
		else if (bindings.size() == 1)
			argType = bindings.get(0).getType();
		else
			// TODO: implement multiple args
			throw new RuntimeException("tuple args not implemented");
		
		Environment extEnv = env;
		for (NameBinding bind : bindings) {
			extEnv = extEnv.extend(bind);
		}

		Type resultType = body.typecheck(extEnv, expected.map(exp -> ((Arrow)exp).getResult()));
		return new Arrow(argType, resultType);
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return new Closure(this, env);
	}

	@Override
	public List<NameBinding> getArgBindings() {
		return bindings;
	}

	@Override
	public TypedAST getBody() {
		return body;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("body", body);
		return children;
	}

	@Override
	public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
		writer.write(new New(Arrays.asList(new DefDeclaration("call",
				bindings.stream().map(b->new FormalArg(b.getName(), (ValueType)b.getType().generateILType())).collect(Collectors.toList()),
                (ValueType)getType().generateILType(), ExpressionWriter.generate(iwriter->body.codegenToIL(new GenerationEnvironment(environment), iwriter)))), null, null));
	}

	@Override
	public ExpressionAST doClone(Map<String, TypedAST> nc) {
		return new Fn(bindings, nc.get("body"));
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
    /**
     * @param GenContext The type context of the lambda declaration
     * @return The Intermediate Representation of the inline function decl
     */
	public Expression generateIL(GenContext ctx, ValueType expectedType) {
        /*
         * First, map the NameBindings to Formal Arguments, dropping the parameters into the IR.
         * Next, find the type of the body. The type of the body is the return type of the function.
         * This allows the creation of the DefDeclaration
         *
         * Next, create a new StructuralType, duplicating the DefDecl as a DeclType.
         * Use the StructualType and the DefDeclaration to make a New. Return.
         */

		// Convert the bindings into formals
        List<FormalArg> intermediateArgs = convertBindingToArgs(this.bindings, ctx, expectedType);
		
        // Extend the generalContext to include the parameters passed into the function.
        ctx = extendCtxWithParams(ctx, intermediateArgs);

        

        // Generate the IL for the body, and get it's return type.
        Expression il = this.body.generateIL(ctx, null);
        ValueType bodyReturnType = il.typeCheck(ctx);

        // Create a new list of function declaration, which is a singleton, containing only "apply"
        DefDeclaration applyDef = new DefDeclaration("apply", intermediateArgs, bodyReturnType, il);
        List<Declaration> declList = new LinkedList<>();
        declList.add(applyDef);

        // Store a redundency of the function declaration
        DeclType ddecl = new DefDeclType("apply", bodyReturnType, intermediateArgs);
        List<DeclType> declTypes = new LinkedList<>();
        declTypes.add(ddecl);

        ValueType newType = new StructuralType("@lambda-structual-decl", declTypes);

        return new New(declList, "@lambda-decl", newType);
	}
	
	public  void genTopLevel(TopLevelContext topLevelContext, ValueType expectedType) {
		topLevelContext.addExpression(generateIL(topLevelContext.getContext(), expectedType));
	}

    private List<FormalArg> convertBindingToArgs(List<NameBinding> bindings, GenContext ctx, ValueType declType) {

    	List<FormalArg> expectedFormals = declType == null?null:getExpectedFormls(ctx, declType);
    	
        List<FormalArg> result = new LinkedList<FormalArg>();

        if (expectedFormals != null && expectedFormals.size() != bindings.size()) {
        	//TODO: will replace with ToolError in the future
			throw new RuntimeException("args count does not map between declType and lambda expression");
			
		}
        
        for (int i = 0; i < bindings.size(); i++) {
        	NameBinding binding = bindings.get(i);
        	
        	ValueType argType = null;        	
        	if (binding.getType() != null) {
				argType = binding.getType().getILType(ctx);
			}
        	else {
        		if (expectedFormals == null)
        			ToolError.reportError(ErrorMessage.CANNOT_INFER_ARG_TYPE, this);
				argType = expectedFormals.get(i).getType();
			}
        	
        	result.add( new FormalArg(
                    binding.getName(),
                    argType
                ));
		}
        
        

        return result;
    }

	private static List<FormalArg> getExpectedFormls(GenContext ctx, ValueType declType) {
		StructuralType declStructuralType = declType.getStructuralType(ctx);
    	
    	
    	DeclType applyDecl = declStructuralType.findDecl("apply", ctx);
    	
    	if (applyDecl == null || !(applyDecl instanceof DefDeclType)) {
			//TODO: will replace with ToolError in the future
    		throw new RuntimeException("the declType is not a lambda type(it has no apply method)");
		}
    	
    	DefDeclType applyDef = (DefDeclType) applyDecl;
    	
    	return applyDef.getFormalArgs();
	}

    private static GenContext extendCtxWithParams(GenContext ctx, List<FormalArg> formalArgs) {
        for(FormalArg binding : formalArgs) {
            ctx = ctx.extend(
                binding.getName(),
                new wyvern.target.corewyvernIL.expression.Variable(binding.getName()),
                binding.getType()
            );
        }
        return ctx;
    }
}
