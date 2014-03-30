package wyvern.targets.Common.wyvernIL.IL;

import java.util.List;

/**
 * O := Immediate
 * 	  | Var
 * 	  | (O, ...)
 *    | ()
 *
 * Exn := O
 *    |   O op O
 *    |   O(O)
 *    |   O.id
 *    |   new id
 *
 * E := D
 * 	  | id = Exn
 * 	  | Exn
 *    | L
 *    | Goto L
 *    | if O goto L
 *    | return O
 *
 * L := Label
 * P := id : type
 *
 * D := E
 * 	  | def id(P, ...) { E ... }
 *    | val id = Exn
 *    | var id = Exn
 *    | class id { D, ... }
 *    | type id { D, ... }
 */
public class WyvIL {

	public static String join(List<String> list, String delim) {

		StringBuilder sb = new StringBuilder();

		String loopDelim = "";

		for(String s : list) {

			sb.append(loopDelim);
			sb.append(s);

			loopDelim = delim;
		}

		return sb.toString();
	}
}
