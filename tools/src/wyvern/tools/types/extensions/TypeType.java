package wyvern.tools.types.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

public class TypeType extends AbstractTypeImpl implements OperatableType, RecordType {
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

    @Override
    public TypeBinding getInnerType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeType getEquivType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TaggedInfo getTaggedInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type checkOperator(Invocation opExp, Environment env) {
        // TODO Auto-generated method stub
        return null;
    }


}
