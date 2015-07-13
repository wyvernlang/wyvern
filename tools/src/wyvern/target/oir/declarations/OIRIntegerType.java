package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRIntegerType extends OIRInterface {

	private static OIRIntegerType type = new OIRIntegerType ();
	private static String stringRep = "int";
	
	protected OIRIntegerType ()
	{
		super ("", "x", null);
	}
	
	public static OIRIntegerType getIntegerType ()
	{
		return type;
	}
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString ()
	{
		return OIRIntegerType.stringRep;
	}
	
	@Override
	public String getName() {
		return toString ();
	}
}
