package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;
import java.util.LinkedList;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.FunDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
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

	public boolean checkImplements(TypeType other) {
		HashSet<Pair<String, Type>> thisMembers = new HashSet<Pair<String, Type>>();
		for (TypedAST d : this.decl.getDecls().getDeclIterator()) {
			if (d instanceof FunDeclaration) {
				String n = ((FunDeclaration) d).getName();
				Arrow t = (Arrow) ((FunDeclaration) d).getType();
				thisMembers.add(new Pair<String, Type>(n, t));
			} else {
				System.out.println("Unsupported type member in checkImplements: " + d.getClass());
			}
		}
		
		System.out.println("thisMembers (checkImplements) = " + thisMembers);
		
		HashSet<Pair<String, Type>> otherMembers = new HashSet<Pair<String, Type>>();
		for (TypedAST d : other.getDecl().getDecls()) {
			if (d instanceof FunDeclaration) {
				String n = ((FunDeclaration) d).getName();
				Arrow t = (Arrow) ((FunDeclaration) d).getType();
				otherMembers.add(new Pair<String, Type>(n, t));
			} else {
				System.out.println("Unsupported type member in checkImplements: " + d.getClass());
			}
		}
		
		System.out.println("otherMembers (checkImplements) = " + otherMembers);
		
		return TypeType.checkSubtypeRecursively(this, other, thisMembers, otherMembers, new HashSet<SubtypeRelation>());
	}

	public boolean checkImplementsClass(TypeType other) {
		HashSet<Pair<String, Type>> thisMembers = new HashSet<Pair<String, Type>>();
		for (TypedAST d : this.decl.getDecls().getDeclIterator()) {
			if (d instanceof FunDeclaration && ((FunDeclaration) d).isClassFun()) {
				String n = ((FunDeclaration) d).getName();
				Arrow t = (Arrow) ((FunDeclaration) d).getType();
				thisMembers.add(new Pair<String, Type>(n, t));
			} else {
				System.out.println("Unsupported type member in checkImplementsClass: " + d.getClass());
			}
		}
		
		System.out.println("thisMembers (checkImplementsClass) = " + thisMembers);
		
		HashSet<Pair<String, Type>> otherMembers = new HashSet<Pair<String, Type>>();
		for (TypedAST d : other.getDecl().getDecls()) {
			if (d instanceof FunDeclaration) {
				String n = ((FunDeclaration) d).getName();
				Arrow t = (Arrow) ((FunDeclaration) d).getType();
				otherMembers.add(new Pair<String, Type>(n, t));
			} else {
				System.out.println("Unsupported type member in checkImplementsClass: " + d.getClass());
			}
		}
		
		System.out.println("otherMembers (checkImplementsClass) = " + otherMembers);
		
		return TypeType.checkSubtypeRecursively(this, other, thisMembers, otherMembers, new HashSet<SubtypeRelation>());
	}
	
	public TypeType convertToType() {
		LinkedList<Declaration> seq = new LinkedList<>();
		
		// Generate an appropriate type member for every class member.
		for (Declaration d : decl.getDecls().getDeclIterator()) {
			if (d instanceof FunDeclaration) {
				// TODO:
				System.out.println("Unsupported class member in class to type converter: " + d.getClass());
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
			return this.checkImplements((TypeType) other);
		} else if (other instanceof ClassType) {
			return this.convertToType().subtype(((ClassType) other).convertToType());
		}
		
		return false;
	}
}