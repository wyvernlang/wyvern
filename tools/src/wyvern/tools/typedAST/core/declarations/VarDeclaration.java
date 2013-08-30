package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.binding.VarBinding;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class VarDeclaration extends Declaration implements CoreAST {
	TypedAST definition;
	NameBinding binding;
	VarBinding varBinding;

	private boolean isClass;
	public boolean isClass() {
		return isClass;
	}

	public VarDeclaration(String varName, Type parsedType, TypedAST definition) {
		this.definition=definition;
		binding = new NameBindingImpl(varName, parsedType);
		varBinding = new VarBinding(varName, parsedType);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName(), definition);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return binding.getType();
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	public NameBinding getBinding() {
		return binding;
	}

	@Override
	public Type getType() {
		return binding.getType();
	}

	@Override
	public String getName() {
		return binding.getName();
	}
	
	public TypedAST getDefinition() {
		return definition;
	}

	@Override
	protected Environment doExtend(Environment old) {
		return old.extend(binding).extend(varBinding);
	}

	@Override
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(binding.getName(), binding.getType()));
		return newEnv;
		//Environment newEnv = old.extend(new ValueBinding(binding.getName(), defValue));
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		Value exVal = declEnv.getValue(binding.getName());
		if (exVal != null) {
			if (exVal.getType() instanceof VarValue)
				return;

			ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
			vb.setValue(new VarValue(exVal));
			return;
		}
		
		if (definition == null) {
            ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
            vb.setValue(new VarValue(null));
			return;
		}
		Value defValue = definition.evaluate(evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
		vb.setValue(new VarValue(defValue));
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location; //TODO
	}
}