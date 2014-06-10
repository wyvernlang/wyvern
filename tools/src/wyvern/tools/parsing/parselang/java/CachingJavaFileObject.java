package wyvern.tools.parsing.parselang.java;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;

//Largely from http://www.ibm.com/developerworks/java/library/j-jcomp/index.html
public class CachingJavaFileObject extends SimpleJavaFileObject {
	private ByteArrayOutputStream byteCode;


	protected CachingJavaFileObject(String name, Kind kind) {
		super(URI.create(name), kind);
	}

	protected CachingJavaFileObject(String name, byte[] inner) {
		super(URI.create(name), Kind.CLASS);
		try {
			byteCode = new ByteArrayOutputStream(inner.length);
			byteCode.write(inner);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream openInputStream() {
		return new ByteArrayInputStream(getByteCode());
	}

	@Override
	public OutputStream openOutputStream() {
		byteCode = new ByteArrayOutputStream();
		return byteCode;
	}

	public byte[] getByteCode() {
		return byteCode.toByteArray();
	}
}
