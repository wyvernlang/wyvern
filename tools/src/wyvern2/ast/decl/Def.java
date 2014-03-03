package wyvern2.ast.decl;

import wyvern2.ast.Program;
import wyvern2.ast.decl.util.ParamList;

/**
 * Created by Ben Chung on 2/23/14.
 */
public class Def implements Decl {
	String name;
	ParamList params;
	Program inner;
}
