package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class ImportDeclaration extends Declaration {
	private String src;
	private String equivName;

	public ImportDeclaration(String src, String equivName) {
		this.src = src;
		this.equivName = equivName;
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
		return null;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return null;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {

	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
