package wyvern.tools.types.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.util.LinkedList;

/**
 * Useful type functionality
 */
public class TypeDeclUtils {
	public static Environment getTypeEquivalentEnvironment(Environment src) {
		Environment tev = Environment.getEmptyEnvironment();

		for (Binding b : src.getBindings()) {
			// System.out.println("Processing: " + b + " which class is " + b.getClass());
			
			if (b instanceof AssignableNameBinding) {
				//Indicates that there is a settable value
				String name = b.getName();
				Type type = b.getType();
				tev = tev.extend(
						new NameBindingImpl("set" + name.substring(0,1).toUpperCase() + name.substring(1),
						new Arrow(type, Unit.getInstance())));
				continue;
			}

			if (b instanceof TypeBinding) {
				if (b.getType() instanceof TypeType) {
					tev = tev.extend(b);
					continue;
				}
				if (b.getType() instanceof ClassType) {
					TypeType tt = ((ClassType) b.getType()).getEquivType();
					tev = tev.extend(new NameBindingImpl(b.getName(), tt));
					continue;
				}
				continue;
			}

			if (!(b instanceof NameBinding))
				continue;

			if (b.getType() instanceof Arrow) {
				tev = tev.extend(b);
				continue;
			}
			
			if (b.getType() instanceof TypeType || b.getType() instanceof ClassType) {
				tev = tev.extend(b);
				continue;
			}
			
			// System.out.println("Assume it is a getter even if it is wrong! :-)");
			
			String propName = b.getName();
			Type type = b.getType();

			DefDeclaration getter = new DefDeclaration(propName, type,
					new LinkedList<NameBinding>(), null, false, FileLocation.UNKNOWN);

			tev = getter.extend(tev, tev);
		}
		return tev;
	}

	public static Environment getTypeEquivalentEnvironment(DeclSequence decls, boolean useClassMembers) {
		LinkedList<Declaration> seq = new LinkedList<>();

		Environment newEnv = Environment.getEmptyEnvironment();
		// Generate an appropriate type member for every class member.
		for (Declaration d : decls.getDeclIterator()) {
			if (d instanceof DefDeclaration) {
				if (((DefDeclaration) d).isClassMember() != useClassMembers)
					continue;
				newEnv = d.extend(newEnv, newEnv);
			} else if (d instanceof VarDeclaration) {
				if (((VarDeclaration) d).isClassMember() != useClassMembers)
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
				if (((ValDeclaration) d).isClassMember() != useClassMembers)
					continue;

				ValDeclaration vd = (ValDeclaration) d;
				String propName = vd.getName();
				Type type = vd.getType();
				FileLocation line = vd.getLocation();

				DefDeclaration getter = new DefDeclaration(propName, type,
						new LinkedList<NameBinding>(), null, false, line);

				newEnv = getter.extend(newEnv, newEnv);
			} else if (d instanceof TypeDeclaration) {
				newEnv = d.extend(newEnv, newEnv);
			} else if (d instanceof ClassDeclaration) {
				ClassDeclaration cd = (ClassDeclaration) d;
				TypeType tt = ((ClassType) cd.getType()).getEquivType();
				newEnv = newEnv.extend(new NameBindingImpl(cd.getName(), tt));
			} else {
				System.out.println("Unsupported class member in class to type converter: " + d.getClass());
			}
		}
		return newEnv;
	}
}
