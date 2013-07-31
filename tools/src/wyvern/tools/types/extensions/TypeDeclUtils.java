package wyvern.tools.types.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Useful type functionality
 */
public class TypeDeclUtils {
	public static Environment getTypeEquivalentEnvironment(DeclSequence decls, boolean useClassMembers) {
		LinkedList<Declaration> seq = new LinkedList<>();

		Environment newEnv = Environment.getEmptyEnvironment();
		// Generate an appropriate type member for every class member.
		for (Declaration d : decls.getDeclIterator()) {
			if (d instanceof DefDeclaration) {
				if (((DefDeclaration) d).isClass() != useClassMembers)
					continue;
				newEnv = d.extend(newEnv);
			} else if (d instanceof VarDeclaration) {
				if (((VarDeclaration) d).isClass() != useClassMembers)
					continue;

				VarDeclaration vd = (VarDeclaration) d;
				String propName = vd.getName();
				Type type = vd.getType();
				FileLocation line = vd.getLocation();


				newEnv = newEnv.extend(new NameBindingImpl(propName, type));
				newEnv = newEnv.extend(
						new NameBindingImpl(
								"set" + propName.substring(0,1).toUpperCase() + propName.substring(1),
								new Arrow(type, Unit.getInstance())));
			} else if (d instanceof ValDeclaration) {
				if (((ValDeclaration) d).isClass() != useClassMembers)
					continue;

				ValDeclaration vd = (ValDeclaration) d;
				String propName = vd.getName();
				Type type = vd.getType();
				FileLocation line = vd.getLocation();

				DefDeclaration getter = new DefDeclaration(propName, type,
						new LinkedList<NameBinding>(), null, false, line);

				newEnv = getter.extend(newEnv);
			} else if (d instanceof TypeDeclaration) {
				newEnv = d.extend(newEnv);
			} else if (d instanceof ClassDeclaration) {
				ClassDeclaration cd = (ClassDeclaration) d;
				TypeType tt = ((ClassType) cd.getType()).getEquivType();
				HashSet<Pair<String, Type>> mems = tt.getMembers();
				newEnv = newEnv.extend(new NameBindingImpl(cd.getName(), tt));
			} else {
				System.out.println("Unsupported class member in class to type converter: " + d.getClass());
			}
		}
		return newEnv;
	}
}
