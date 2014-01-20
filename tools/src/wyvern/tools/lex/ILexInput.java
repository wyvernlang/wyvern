package wyvern.tools.lex;

import wyvern.tools.errors.FileLocation;

interface ILexInput {
	boolean hasNext();
	char peek();
	char read();
	FileLocation getLocation();
}
