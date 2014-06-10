package wyvern.tools.parsing.parselang.java;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

//Largely from http://www.ibm.com/developerworks/java/library/j-jcomp/index.html
public class StringFileObject extends SimpleJavaFileObject {
	private final String src;

	public StringFileObject(String name, String src) {
		super(URI.create("string:///"+name+Kind.SOURCE.extension), Kind.SOURCE);
		this.src = src;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return src;
	}
}
