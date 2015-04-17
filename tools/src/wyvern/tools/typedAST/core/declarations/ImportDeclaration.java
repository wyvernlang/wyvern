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
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ImportDeclaration extends Declaration implements CoreAST {
	private URI uri;
	private ImportBinder binder;
	private FileLocation location;

	public ImportDeclaration(URI inputURI, FileLocation location) {
		this.uri = inputURI;
		this.location = location;
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
}
