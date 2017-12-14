package wyvern.tools.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import wyvern.tools.errors.FileLocation;

public abstract class AbstractTypeImpl implements Type {
	private final FileLocation location;
	
	protected AbstractTypeImpl(FileLocation location) {
		this.location = location;
	}

	protected AbstractTypeImpl() {
		this.location = null;
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}
	
	public boolean isSimple() {
		return true; // default is correct for most types
	}
}