package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class ClassType extends AbstractTypeImpl implements OperatableType {
	private ClassDeclaration decl;
	private TypeType implementsType;
	
	public ClassType(ClassDeclaration decl) {
		this.decl = decl;
		this.implementsType = decl.getTypeType();
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "CLASS " + decl.getName();
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		// should not be any arguments - that is in a separate application at present
		assert opExp.getArgument() == null;
		
		// the operation should exist
		String opName = opExp.getOperationName();
		Declaration m = decl.getDecl(opName);

		if (m == null)
			reportError(OPERATOR_DOES_NOT_APPLY, opName, this.toString(), opExp);
		
		// TODO Auto-generated method stub
		return m.getType();
	}

	public ClassDeclaration getDecl() {
		return this.decl;
	}
	
	public TypeType convertToType(boolean useClassMembers) {
		LinkedList<Declaration> seq = new LinkedList<>();
		
		// Generate an appropriate type member for every class member.
		for (Declaration d : decl.getDecls().getDeclIterator()) {
			if (d instanceof DefDeclaration) {
				if (((DefDeclaration) d).isClass() != useClassMembers)
					continue;
				
				seq.add(d);
			} else if (d instanceof VarDeclaration) {
				if (((VarDeclaration) d).isClass() != useClassMembers)
					continue;
					
				VarDeclaration vd = (VarDeclaration) d;
				String propName = vd.getName();
				Type type = vd.getType();
				FileLocation line = vd.getLocation();
				
				DefDeclaration getter = new DefDeclaration(propName, type,
						new LinkedList<NameBinding>(), null, false, line);
				
				List<NameBinding> args = new ArrayList<NameBinding>();
				args.add(new NameBindingImpl("new" + propName.substring(0,1).toUpperCase() + propName.substring(1), type));
				DefDeclaration setter = new DefDeclaration("set" + propName.substring(0,1).toUpperCase() + propName.substring(1),
					new Arrow(type, Unit.getInstance()), args, null, false, line);
				
				seq.add(getter);
				seq.add(setter);
			} else if (d instanceof ValDeclaration) {
				if (((ValDeclaration) d).isClass() != useClassMembers)
					continue;
				
				ValDeclaration vd = (ValDeclaration) d;
				String propName = vd.getName();
				Type type = vd.getType();
				FileLocation line = vd.getLocation();
				
				DefDeclaration getter = new DefDeclaration(propName, type,
						new LinkedList<NameBinding>(), null, false, line);

				seq.add(getter);
			} else {
				System.out.println("Unsupported class member in class to type converter: " + d.getClass());
			}
		}
		
		return new TypeType(new TypeDeclaration(decl.getName(), new DeclSequence(seq), decl.getLocation()));
	}

	// FIXME: Do something similar here to TypeType maybe and maybe try to integrate the above
	// implements checks into here and change ClassDeclaration to use this instead.
	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (super.subtype(other, subtypes)) {
			return true;
		}

		if (other instanceof TypeType) {
			return this.convertToType(false).subtype(other);
		} else if (other instanceof ClassType) {
			return this.convertToType(false).subtype(((ClassType) other).convertToType(false));
		}
		
		return false;
	}
}