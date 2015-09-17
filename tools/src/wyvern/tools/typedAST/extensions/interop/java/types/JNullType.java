package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JNullType extends AbstractTypeImpl implements Type {
	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return other instanceof JavaClassType;
	}

	@Override
	public boolean subtype(Type other) {
		return other instanceof JavaClassType;
	}
	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}

    @Override
    public ValueType generateILType() {
        throw new WyvernException("Cannot generate IL for Java interop object", FileLocation.UNKNOWN);
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
