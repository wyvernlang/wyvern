package wyvern.stdlib.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.expression.ObjectValue;

public class Regex {
	public static Regex utils = new Regex();

	public ObjectValue findPrefixOf(String regex, String source) {
		Matcher m = Pattern.compile(regex).matcher(source);
		if (m.find() && m.start() == 0) {
			String matchedString = m.group(); 
			throw new RuntimeException();
		} else {
			
		}
		//return null;
		throw new RuntimeException();
	}
}
