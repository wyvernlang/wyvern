package wyvern.tools.typedAST.core.binding.objects;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ClassBinding extends ValueBinding {
	private final String name;
	private final ClassDeclaration cd;

	public ClassBinding(String name, ClassDeclaration cd) {
		super("classvalue", UnitVal.getInstance(FileLocation.UNKNOWN));
		this.name = name;
		this.cd = cd;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return cd.getType();
	}

	public ClassDeclaration getClassDecl() {
		return cd;
	}

	@Override
	public String toString() {
		return "{" + name + " as class  " + cd + "}";
	}
}
