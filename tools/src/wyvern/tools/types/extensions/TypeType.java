package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.DeclSequence;
import wyvern.tools.typedAST.extensions.declarations.MethDeclaration;
import wyvern.tools.typedAST.extensions.declarations.TypeDeclaration;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeUtils;
import wyvern.tools.util.TreeWriter;

public class TypeType extends AbstractTypeImpl implements OperatableType {
	private TypeDeclaration decl;
	
	public TypeType(TypeDeclaration decl) {
		this.decl = decl;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return /*"TYPE " +*/ decl.getName();
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

	public TypeDeclaration getDecl() {
		return this.decl;
	}
	
	public boolean subtypeOf(TypeType tt) {
		// TODO: This is my current platform for implementing the recursive subtype check right as types only have methods and props to worry about.
		
		DeclSequence thisDs = this.decl.getDecls();
		String thisName = this.decl.getName();
		DeclSequence otherDs = tt.decl.getDecls();
		String otherName = tt.decl.getName();
		
		// FIXME: In reality, do a building of the subtype relations to see if this can be resolved to be a subtype of other.
		
		
		HashSet<String> thisMembers = new HashSet<String>();
		for (TypedAST t: thisDs) {
			if (t instanceof MethDeclaration) {
				MethDeclaration m = (MethDeclaration) t;
				Arrow a = (Arrow) m.getType();
				thisMembers.add(a.toString());
			} else {
				System.out.println("Unsupported type member in subtypeOf: " + t.getClass());
			}
		}
		
		HashSet<String> otherMembers = new HashSet<String>();
		for (TypedAST t: otherDs) {
			if (t instanceof MethDeclaration) {
				MethDeclaration m = (MethDeclaration) t;
				Arrow a = (Arrow) m.getType();
				otherMembers.add(a.toString());
			} else {
				System.out.println("Unsupported type member in subtypeOf: " + t.getClass());
			}
		}

		// System.out.println("THIS (" + thisName + ": " + thisMembers);
		// System.out.println("OTHER (" + otherName + ": " + otherMembers);
		
		boolean result = false;
		
		result = thisMembers.containsAll(otherMembers);
		
		if (!result) {
			// Perform Amber rule for recursive subtyping assuming there is recursion in the type.
			boolean thisIsRec = false;
			HashSet<String> thisMembersReplaced = new HashSet<String>();
			for (String type : thisMembers) {
				if (type.contains(thisName)) {
					thisIsRec = true;
					thisMembersReplaced.add(type.replaceAll(thisName, otherName));
				} else {
					thisMembersReplaced.add(type);
				}
			}
			
			// System.out.println("THIS WITH REPLACEMENT: " + thisMembersReplaced);
			
			boolean otherIsRec = false;
			for (String type : otherMembers) {
				if (type.contains(otherName)) {
					otherIsRec = true;
					break;
				}
			}
			
			if (thisIsRec && otherIsRec) {
				// Both are recursive, it is possible to try and see if amber rule applies.
				// FIXME: Really need to do subtype properly with environments and all...
				result = thisMembersReplaced.containsAll(otherMembers);
			}
		}
		
		// System.out.println("Result of " + thisName + " subtypeOf " + otherName + ": " + result);
		
		return result;
	}

	@Override
	public boolean subtype(Type other, HashSet<TypeUtils.SubtypeRelation> subtypes) {
		return super.subtype(other, subtypes);
	}
}