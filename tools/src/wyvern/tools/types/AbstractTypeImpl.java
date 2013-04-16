package wyvern.tools.types;

import java.util.HashSet;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public abstract class AbstractTypeImpl implements Type {

	@Override
	public boolean subtype(Type other, HashSet<TypeUtils.SubtypeRelation> subtypes) {
		ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this.toString(), other.toString(),
				HasLocation.UNKNOWN);
		return false; // Unreachable.
	}

}