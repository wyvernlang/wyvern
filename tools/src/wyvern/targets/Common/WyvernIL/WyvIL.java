package wyvern.targets.Common.WyvernIL;

import wyvern.targets.Common.WyvernIL.visitor.ILVisitor;

/**
 * O := Immediate
 * 	  | Var
 * 	  | (O, ...)
 *
 * Exn := O
 *    |   O op O
 *    |   O(O, ...)
 *    |   O.id
 *    |   new id
 *
 * E := D
 * 	  | id = Exn
 * 	  | Exn
 *    | L
 *    | Goto L
 *    | return O
 *
 * L := Label
 * P := id : type
 *
 * D := def id(P, ...) { E ... }
 *    | val id = Exn
 *    | var id = Exn
 *    | class id { D, ... }
 *    | type id { D, ... }
 */
public interface WyvIL {
	public void accept(ILVisitor v);
}
