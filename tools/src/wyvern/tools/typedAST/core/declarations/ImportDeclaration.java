package wyvern.tools.typedAST.core.declarations;

import static wyvern.tools.errors.ErrorMessage.MODULE_TYPE_ERROR;
import static wyvern.tools.errors.ToolError.reportError;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FFIImport;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.IntegerType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.ImportBinder;
import wyvern.tools.interop.FObject;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.compiler.ImportResolverBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class ImportDeclaration extends Declaration implements CoreAST {
	private URI uri;
	private ImportBinder binder;
	private FileLocation location;
	private boolean requireFlag;
	private String asName;

	public ImportDeclaration(URI inputURI, FileLocation location, String image, boolean isRequire) {
		this.uri = inputURI;
		this.location = location;
		this.requireFlag = isRequire;
		this.asName = image;
	}


	public URI getUri() {
		return uri;
	}
	@Override
	public Environment extendType(Environment env, Environment against) {
		if (binder == null)
			binder = against.lookupBinding(uri.getScheme(), ImportResolverBinding.class).get().getBound().resolveImport(uri);
		return binder.extendTypes(env);
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		return binder.extendNames(env);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if(uri.getScheme().equals("wyv")) {
			String schemeSpecificPart = uri.getSchemeSpecificPart();
			NameBinding envModule = env.lookup(schemeSpecificPart);
			if (envModule == null)
				return binder.typecheck(env);				
			ClassType moduleType = (ClassType) envModule.getType();
			if(!moduleType.isModule()) {
				reportError(MODULE_TYPE_ERROR, this, schemeSpecificPart);
			} else if (!isRequire() && moduleType.isResource()) {
				reportError(MODULE_TYPE_ERROR, this, schemeSpecificPart);
			}
		}
		return binder.typecheck(env);
	}

	@Override
	public Type getType() {
		return new Unit();
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new RuntimeException("I'm scared"); //TODO implement me
    }

    @Override
	public String getName() {
		return "";
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return binder.extend(old);
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		return binder.extendVal(old);
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		binder.bindVal(evalEnv);
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isRequire() {
		return this.requireFlag;
	}


	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Pair<VarBinding,GenContext> genBinding(GenContext ctx) {
		// add the import's type to the context, and get the import value
		Expression importExp = null;
		String importName = this.getUri().getSchemeSpecificPart();
		if (importName.contains(".")) {
			importName = importName.substring(importName.lastIndexOf(".")+1);
		}
		if (this.getUri().getScheme().equals("java")) {
			String importPath = this.getUri().getRawSchemeSpecificPart();
			try {
				FObject obj = wyvern.tools.interop.Default.importer().find(importPath);
				ctx = GenUtil.ensureJavaTypesPresent(ctx);
				ValueType type = GenUtil.javaClassToWyvernType(obj.getJavaClass(), ctx);
				importExp = new JavaValue(obj, type);
				ctx = ctx.extend(importName, new Variable(importName), type);
			} catch (ReflectiveOperationException e1) {
				throw new RuntimeException(e1);
			}
		} else if (this.getUri().getScheme().equals("python")) {
      String moduleName = this.getUri().getRawSchemeSpecificPart();
      System.out.println("Python import: " + moduleName);
      importExp = new FFIImport(new NominalType("system", "Python"), moduleName);
      // how to handle arg lists?
      // does wyvern have a list type yet?
      // ArrayList<DeclType> declTypes = new ArrayList();
      // NominalType intType = new NominalType("system", "Int");
      // ArrayList<FormalArg> args = new ArrayList();
      // args.add(new FormalArg("x", intType));
      // declTypes.add(new DefDeclType("factorial", intType, args));
      // StructuralType importType = new StructuralType("this", declTypes);
      ctx = ctx.extend(importName, new Variable(importName), new NominalType("system", "Dyn"));
    } else {
			// TODO: need to add types for non-java imports
			String moduleName = this.getUri().getSchemeSpecificPart();
			if (ctx.isPresent(moduleName)) {
				importExp = new Variable(moduleName);
			} else {
				importExp = ctx.getInterpreterState().getResolver().resolveModule(moduleName);
			}
			ctx = ctx.extend(importName, new Variable(importName), importExp.typeCheck(ctx));
		}
		return new Pair<VarBinding, GenContext>(new VarBinding(importName, importExp), ctx);
	}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		// no action needed
	}

	@Override
	public void genTopLevel(TopLevelContext tlc) {
		Pair<VarBinding, GenContext> bindingAndCtx = genBinding(tlc.getContext());
		VarBinding binding = bindingAndCtx.first;
		GenContext newCtx = bindingAndCtx.second;
		ValueType type = binding.getExpression().typeCheck(newCtx);
		tlc.addLet(binding.getVarName(), type, binding.getExpression(), false);
		tlc.updateContext(newCtx);
		
		/*wyvern.target.corewyvernIL.expression.Variable variable = new wyvern.target.corewyvernIL.expression.Variable(binding.getVarName());
		wyvern.target.corewyvernIL.decl.Declaration decl =
				new wyvern.target.corewyvernIL.decl.ValDeclaration(binding.getVarName(),
						type,
						variable, location);
		DeclType dt = new VarDeclType(binding.getVarName(), type);
		tlc.addModuleDecl(decl,dt);*/
	}

	public String getAsName() {
		return asName;
	}
}
