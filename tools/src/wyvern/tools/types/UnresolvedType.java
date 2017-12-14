package wyvern.tools.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.errors.WyvernException;

public class UnresolvedType extends AbstractTypeImpl implements NamedType {
	private String typeName;

	public UnresolvedType(String typeName, FileLocation loc) {
		super(loc);
		this.typeName = typeName;
	}
	
	@Override
	public String toString() {
		return "UNRESOLVED: " + typeName;
	}

    @Override
	public boolean isSimple() {
		return true;
	}

    @Override
    public String getName() {
		return typeName;
	}

    @Override
    public String getFullName() {
        return typeName;
    }

	@Override
	public ValueType getILType(GenContext ctx) {
		return ctx.lookupType(typeName, this.getLocation());
	}
}
