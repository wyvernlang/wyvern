package wyvern.tools.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.util.TreeWritable;

public interface Type extends TreeWritable, HasLocation {
	/**
	 * @return whether this type is simple or compound.  Used in toString().
	 */
	public boolean isSimple();

	@Deprecated
	default wyvern.target.corewyvernIL.type.ValueType generateILType() {
        throw new WyvernException("Cannot generate IL form for unresolved type", FileLocation.UNKNOWN);
    }


	public ValueType getILType(GenContext ctx);
}