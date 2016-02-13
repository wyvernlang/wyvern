package wyvern.tools.parsing.parselang.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

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
