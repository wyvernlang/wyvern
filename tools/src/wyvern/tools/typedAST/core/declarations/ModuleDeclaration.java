package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.ClassObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class ModuleDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls;
	protected Environment declEvalEnv;
	protected Environment declEnv;

	public static class ImportDeclaration {
		private String src;

		public ImportDeclaration(String src) {

			this.src = src;
		}
	}

	public ModuleDeclaration(String name, DeclSequence decls, FileLocation location) {
		this.decls = decls;
		this.location = location;
		declEnv = null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		//TODO
	}

	@Override
	public Type getType() {
		return null;//this.typeBinding.getType();
	}


	@Override
	public Type doTypecheck(Environment env) {

		// FIXME: Currently allow this and class in both class and object methods. :(

		if (decls != null)
			for (Declaration decl : decls.getDeclIterator()) {
				decl.typecheckSelf(env);
			}
		return Unit.getInstance();
	}

	@Override
	protected Environment doExtend(Environment old) {
		Environment newEnv = old;//old.extend(nameBinding).extend(typeBinding);

		// FIXME: Currently allow this and class in both class and object methods. :(
		//newEnv = newEnv.extend(new TypeBinding("class", typeBinding.getType()));
		//newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType()));

		return newEnv;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		declEvalEnv = declEnv.extend(evalEnv);
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());
	}

	public Declaration getDecl(String opName) {
		for (Declaration d : decls.getDeclIterator()) {
			// TODO: handle fields too
			if (d.getName().equals(opName))
				return d;
		}
		return null;	// can't find it
	}

	public DeclSequence getDecls() {
		return decls;
	}

	@Override
	public String getName() {
		return "";//nameBinding.getName();
	}

	private FileLocation location = FileLocation.UNKNOWN;

	@Override
	public FileLocation getLocation() {
		return location; // TODO: NOT IMPLEMENTED YET.
	}

	public NameBinding lookupDecl(String name) {
		return declEnv.lookup(name);
	}
}
