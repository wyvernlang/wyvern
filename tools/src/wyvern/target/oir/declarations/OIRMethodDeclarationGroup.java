package wyvern.target.oir.declarations;

import java.util.List;
import java.util.Vector;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class OIRMethodDeclarationGroup  extends OIRAST {

	private List<OIRMethodDeclaration> methodDecls;
	
	public OIRMethodDeclarationGroup() {
		super();
		methodDecls = new Vector<OIRMethodDeclaration> ();
	}
	
	public void addMethodDeclaration (OIRMethodDeclaration declType)
	{
		methodDecls.add(declType);
	}
	
	public int size ()
	{
		return methodDecls.size();
	}
	
	public OIRMethodDeclaration elementAt (int index)
	{
		return methodDecls.get(index);
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}
}
