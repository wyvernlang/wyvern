package wyvern.targets.java.annotations;

import java.lang.annotation.Annotation;

public @interface Val {
	/**
	 * Represents the actual name of the val in question
	 * A method with this parameter should have the name get[name()].
	 * @return The real name for the value represented by the getter.
	 */
	String name();
}
