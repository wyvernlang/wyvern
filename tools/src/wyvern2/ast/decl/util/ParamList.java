package wyvern2.ast.decl.util;

import wyvern2.ast.type.Type;

import java.util.List;

/**
 * Created by Ben Chung on 2/23/14.
 */
public class ParamList {
	public static class Param {
		String name;
		Type asc;
	}
	private List<Param> params;
}
