package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.OperatableType;

public class TypeType extends AbstractTypeImpl implements OperatableType {
	private TypeDeclaration decl;

	public TypeType(TypeDeclaration decl) {
		this.decl = decl;
	}

	public TypeDeclaration getDecl() {
		return this.decl;
	}

	private boolean toStringing = false;
	@Override
	public String toString() {
		if (toStringing)
			return "TYPE(Repeated)";
		toStringing = true;
		String res = "TYPE(" + decl.getName() + ")";
		toStringing = false;
		return res;
	}

    @Override
    public ValueType generateILType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueType getILType(GenContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

}
