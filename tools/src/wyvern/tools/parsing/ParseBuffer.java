package wyvern.tools.parsing;

import edu.umn.cs.melt.copper.runtime.io.CircleTokenBuffer;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;

/*
 * Shamelessly taken from the implementation of SlidingWindowScannerBuffer in Copper
 *
 * @author Benjamin Chung
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */

public class ParseBuffer extends ScannerBuffer {

	private final ScannerBuffer ibuffer;
	private String srcString;

	protected ParseBuffer(String str)
	{
		this.srcString = str;
		this.ibuffer = ScannerBuffer.instantiate(new StringReader(str));
	}

	@Override
	public char charAt(long l) throws IOException {
		return ibuffer.charAt(l);
	}

	@Override
	public void advanceBufferTo(long l) throws IOException {
		ibuffer.advanceBufferTo(l);
	}

	@Override
	public String readStringFromBuffer(long l, long l2) throws IOException {
		return ibuffer.readStringFromBuffer(l, l2);
	}

	public ParseBuffer readBufferFromBuffer(long begin, long end) throws IOException {
		String res = readStringFromBuffer(begin, end);
		return new ParseBuffer(res);
	}

	public String getSrcString() {
		return srcString;
	}
}
