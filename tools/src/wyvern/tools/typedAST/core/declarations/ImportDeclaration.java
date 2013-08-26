package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.ClassObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

import java.util.concurrent.atomic.AtomicReference;

public class ImportDeclaration extends Declaration {
	private String src;
	private String equivName;
	private FileLocation location;
	private TypeType equivType;
	private AtomicReference<Environment> declEnv = new AtomicReference<>();
	private AtomicReference<TypedAST> ref = new AtomicReference<>();

	public ImportDeclaration(String src, String equivName, Environment externalEnv, FileLocation location) {
		this.src = src;
		this.equivName = equivName;
		this.location = location;
		declEnv = new AtomicReference<>(externalEnv);
		equivType = new TypeType(declEnv);
	}

	protected void setDeclEnv(Environment env) {
		declEnv.set(env);
	}

	public void setASTRef(AtomicReference<TypedAST> ref) {
		this.ref = ref;
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
		ref.get().typecheck(env);
		return Unit.getInstance();
	}

	@Override
	protected Environment doExtend(Environment old) {
		return old
				.extend(new TypeBinding(equivName, equivType))
				.extend(new NameBindingImpl(equivName, equivType));
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old.extend(new ValueBinding(equivName, equivType));
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		Environment intEnv = Environment.getEmptyEnvironment();
		if (ref.get() instanceof EnvironmentExtender)
			intEnv = ((EnvironmentExtender) ref.get()).evalDecl(intEnv);

		ValueBinding vb = (ValueBinding) declEnv.lookup(equivName);
		vb.setValue(new Obj(intEnv));
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

	public AtomicReference<TypedAST> getAST() {
		return ref;
	}
}
