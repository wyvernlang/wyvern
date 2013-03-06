package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashSet;

import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.extensions.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.declarations.TypeDeclaration;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ClassType extends AbstractTypeImpl implements OperatableType {
	private ClassDeclaration decl;
	
	public ClassType(ClassDeclaration decl) {
		this.decl = decl;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "ClassType";
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

	// FIXME: This is super simplification to get the ball rolling by Alex. :-)
	public boolean subtypeOf(TypeType tt) {
		ClassDeclaration thisD = this.decl;
		TypeDeclaration typeD = tt.getDecl();
		
		HashSet<String> thisDtypes = new HashSet<String>();
		for (Declaration d = thisD.getDecls(); d != null; d = d.getNextDecl()) {
			// System.out.println(d.getName() + " of type " + d.getType());
			thisDtypes.add(d.getType().toString());
		}
		
		// System.out.println("This (" + thisD.getName() + ")" + thisDtypes);
		
		HashSet<String> implDtypes = new HashSet<String>();
		for (Declaration d = typeD.getDecls(); d != null; d = d.getNextDecl()) {
			// System.out.println(d.getName() + " of type " + d.getType());
			implDtypes.add(d.getType().toString());
		}
		
		// System.out.println("Class Implements (" + typeD.getName() + ")" + implDtypes);
		
		return thisDtypes.containsAll(implDtypes);
	}
}