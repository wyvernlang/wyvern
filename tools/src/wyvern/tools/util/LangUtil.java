package wyvern.tools.util;

import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.types.Type;

public class LangUtil {

	public static Type getType(Object src) {
		return Util.javaToWyvType(src.getClass());
	}

	public static boolean isSubtype(Type check, Type against) {
		return check.subtype(against);
	}
}
