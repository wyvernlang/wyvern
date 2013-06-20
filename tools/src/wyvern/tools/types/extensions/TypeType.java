package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.MethDeclaration;
import wyvern.tools.typedAST.core.declarations.PropDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
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

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (super.subtype(other, subtypes)) {
			return true;
		}
		
		if (other instanceof TypeType) {
			// HashSet<Arrow> thisMeths = new HashSet<Arrow>();
			HashSet<Type> thisMembers = new HashSet<Type>();
			for (TypedAST d : this.decl.getDecls()) {
				if (d instanceof MethDeclaration) {
					Arrow a = (Arrow) ((MethDeclaration) d).getType();
					thisMembers.add(a);
				} else if (d instanceof PropDeclaration) {
					Type t = (Type) ((PropDeclaration) d).getType();
					thisMembers.add(t);
				} else {
					// FIXME: Can type contains more than meth? Props?
					System.out.println("Unsupported type member in subtype: " + d.getClass());
				}
			}
			
			// HashSet<Arrow> otherMeths = new HashSet<Arrow>();
			HashSet<Type> otherMembers = new HashSet<Type>();
			for (TypedAST d : ((TypeType) other).decl.getDecls()) {
				if (d instanceof MethDeclaration) {
					Arrow a = (Arrow) ((MethDeclaration) d).getType();
					otherMembers.add(a);
				} else if (d instanceof PropDeclaration) {
					Type t = (Type) ((PropDeclaration) d).getType();
					otherMembers.add(t);
				} else {
					// FIXME: Can type contains more than meth? Props?
					System.out.println("Unsupported type member in subtype: " + d.getClass());
				}
			}
			
			// FIXME: Allows to have multiple methods that match to implement several methods
			// from supertype - is this OK? Seems OK to me but not sure. :-)
			boolean subset = true;
			for (Type aOther : otherMembers) {
				boolean hasImplementingCandidate = false;
				for (Type aThis : thisMembers) {
					// Apply S-Amber rule here!
					SubtypeRelation sr = new SubtypeRelation(this, (TypeType) other);
					if (!subtypes.contains(sr)) { // Avoid infinite recursion! :)
						subtypes.add(sr);
						boolean result = aThis.subtype(aOther, subtypes);
						subtypes.remove(sr);
								
						if (result) {
							hasImplementingCandidate = true;
							break;
						}
					}		
				}
				if (!hasImplementingCandidate) {
					subset = false;
					break;
				}
			}

			if (subset) return true;
		}
		
		return false;
	}
}