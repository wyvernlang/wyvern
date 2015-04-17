package wyvern.tools.types;

import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;

public class TypeUtils {
	public static Arrow arrow(Type t1, Type t2) {
		return new Arrow(t1, t2);
	}

	public static Unit unit = new Unit();
	public static Int integer = new Int();
	public static Str str = new Str();
}