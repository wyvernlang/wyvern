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

	//Java: why did you have to erase generics?
	public static String castString(Object src) {
		return (String) src;
	}

	public static int strCharInt(String src) {
		if (src.length() != 1)
			throw new RuntimeException();
		return src.charAt(0);
	}

	public static int doubleToInt(double dble) {
		return (int)dble;
	}

	public static String intToStr(int charCode) {
		return new String(new char[] {(char)charCode});
	}
}
