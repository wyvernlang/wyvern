package wyvern.target.corewyvernIL.decltype;

import java.util.List;
import java.util.Vector;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class DeclTypeGroup extends DeclType {

	private List<DeclType> declTypes;
	
	public DeclTypeGroup() {
		super();
		declTypes = new Vector<DeclType> ();
	}
	
	public void addDeclType (DeclType declType)
	{
		declTypes.add(declType);
	}
	
	public int size ()
	{
		return declTypes.size();
	}
	
	public DeclType elementAt (int index)
	{
		return declTypes.get(index);
	}
	
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor, Environment env,
			OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}
}
