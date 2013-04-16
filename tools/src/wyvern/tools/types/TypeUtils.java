package wyvern.tools.types;

import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;

public class TypeUtils {
	public static Arrow arrow(Type t1, Type t2) {
		return new Arrow(t1, t2);
	}
	
	public static Unit unit = Unit.getInstance();
	public static Int integer = Int.getInstance();
	public static Str str = Str.getInstance();
	
	public class SubtypeRelation {
		private Type subtype;
		private Type supertype;
		
		public SubtypeRelation(Type subtype, Type supertype) {
			this.subtype = subtype; 
			this.supertype = supertype;
		}
	}
}