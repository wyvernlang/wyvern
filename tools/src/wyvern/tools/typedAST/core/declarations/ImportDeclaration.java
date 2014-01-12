package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.ImportResolver;
import wyvern.tools.parsing.resolvers.ImportEnvResolver;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ImportDeclaration extends Declaration implements CoreAST {
	private String src;
	private String equivName;
	private ImportResolver res;
	private FileLocation location;
	private TypeType equivType;
	private Reference<Environment> declEnv = new Reference<>();
	private Reference<TypedAST> ref = new Reference<>();
    private Reference<Environment> internalEnv = new Reference<>();
    private ValueBinding vb;
	private boolean typechecking = false;
    private boolean typechecked = false;
	private boolean defaultN = false;

	public ImportDeclaration(String src, String equivName, Environment externalEnv, ImportResolver res, FileLocation location) {
		this.src = src;
		this.equivName = equivName;
		this.res = res;
		this.location = location;
		if (equivName == null) {
			this.equivName = res.getDefaultName(URI.create(src));
				if (this.equivName == null)
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, this);
			defaultN = true;
		}
		declEnv = new Reference<>(externalEnv);
		equivType = new TypeType(declEnv);
        vb = new ValueBinding(equivName, equivType);
	}

	protected void setDeclEnv(Environment env) {
		declEnv.set(env);
	}

	public void setASTRef(final Reference<TypedAST> ref) {
		final Reference<TypedAST> oRef = this.ref;
		Reference<TypedAST> nRef = new Reference<TypedAST>() {
			@Override
			public void set(TypedAST nAst) {
				ref.set(nAst);
				oRef.set(nAst);
			}

			@Override
			public TypedAST get() {
				return ref.get();
			}
		};
		oRef.set(nRef.get());
		this.ref = nRef;
	}

	public String getSrc() {
		return src;
	}

	public String getEquivName() {
		return equivName;
	}

	@Override
	public String getName() {
		return equivName;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (typechecking || ref.get() == null || typechecked) //Recursive reference hasn't been resolved yet
			return Unit.getInstance();
		typechecking = true;
		ref.get().typecheck(env);
		typechecking = false;
        typechecked = true;
		return Unit.getInstance();
	}

	@Override
	protected Environment doExtend(Environment old) {
		if (res instanceof ImportEnvResolver && defaultN) {
			return ((ImportEnvResolver) res).doExtend(old, equivName, ref);
		}
		return old
				.extend(new TypeBinding(equivName, equivType))
                .extend(vb)
				.extend(new NameBindingImpl(equivName, equivType));
	}

	@Override
	public Environment extendWithValue(Environment old) {
		if (res instanceof ImportEnvResolver && defaultN) {
			return ((ImportEnvResolver) res).extendWithValue(old, ref.get());
		}
		return old.extend(new ValueBinding(equivName, equivType));
	}

    private boolean evaluating = false;
	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
        typecheck(evalEnv);
		if (res instanceof ImportEnvResolver && defaultN) {
			((ImportEnvResolver) res).evalDecl(evalEnv, declEnv, ref.get());
			return;
		}
		Environment intEnv = Environment.getEmptyEnvironment();
        if (!evaluating) {
            evaluating = true;
            if (ref.get() instanceof EnvironmentExtender)
                internalEnv.set(((EnvironmentExtender) ref.get()).evalDecl(intEnv));
            evaluating = false;
        }

		ValueBinding vb = (ValueBinding) declEnv.lookup(equivName);
		vb.setValue(new Obj(internalEnv));
        this.vb.setValue(new Obj(internalEnv));
		return;
	}

	@Override
	public Type getType() {
		return Unit.getInstance();
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}

	public Reference<TypedAST> getAST() {
		return ref;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		childMap.put("refb", ref.get());
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		ImportDeclaration importDeclaration = new ImportDeclaration(src, equivName, declEnv.get(), res, location);
		importDeclaration.setASTRef(new Reference<TypedAST>(newChildren.get("refb")));
		return importDeclaration;
	}
}
