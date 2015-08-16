package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.ImportBinder;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.compiler.ImportResolverBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static wyvern.tools.errors.ErrorMessage.MODULE_TYPE_ERROR;
import static wyvern.tools.errors.ToolError.reportError;

public class ImportDeclaration extends Declaration implements CoreAST {
	private URI uri;
	private ImportBinder binder;
	private FileLocation location;
	private boolean requireFlag;

	public ImportDeclaration(URI inputURI, FileLocation location, boolean isRequire) {
		this.uri = inputURI;
		this.location = location;
		this.requireFlag = isRequire;
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
		//System.out.println(uri);
		//System.out.println("uri is " + uri.getSchemeSpecificPart());
		//System.out.println(env);
		//System.out.println(uri.getScheme());
		if(uri.getScheme().equals("wyv")) {
			ClassType moduleType = (ClassType) env.lookup(uri.getSchemeSpecificPart()).getType();
			if(!moduleType.isModule()) {
				reportError(MODULE_TYPE_ERROR, this, uri.getSchemeSpecificPart());
			} else if (!isRequire() && moduleType.isResource()) {
				reportError(MODULE_TYPE_ERROR, this, uri.getSchemeSpecificPart());
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
	
	public boolean isRequire() {
		return this.requireFlag;
	}
}
