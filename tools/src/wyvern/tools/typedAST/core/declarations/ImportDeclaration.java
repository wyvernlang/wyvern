package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
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
	private TypedAST ref;

	public ImportDeclaration(String src, String equivName, Environment externalEnv, FileLocation location) {
		this.src = src;
		this.equivName = equivName;
		this.location = location;
		equivType = new TypeType(equivName, externalEnv);
	}

	public void setAST(TypedAST ref) {
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
		return;//TODO: Think about this more
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
}
