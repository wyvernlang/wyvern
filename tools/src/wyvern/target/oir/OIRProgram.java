package wyvern.target.oir;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.OIRExpression;

public class OIRProgram extends OIRAST {
	private List<OIRType> typeDeclarations;
	private OIRExpression mainExpression;
	private static int classID = 0;
	private HashMap<String, OIRStaticPIC> methodStaticPICMap;
	
	public static OIRProgram program = new OIRProgram ();
	
	public static enum DelegateImplementation
	{
		HASH_TABLE_NAIVE, /* Every call will do Hash Table Lookup */
		ONLY_STATIC_PIC, /* Every call will do Hash Table Lookup in Static PIC */
		PIC_WITH_STATIC_PIC, /* Cache calls using PIC and search only in Static PIC*/
		DYNAMIC_FIELDS, /* Use in Dynamic Fields also */
	}
	
	private static DelegateImplementation delegateImplementation = 
			DelegateImplementation.HASH_TABLE_NAIVE;
	
	public static void setDelegateImplementation (DelegateImplementation delegateImpl)
	{
		delegateImplementation = delegateImpl;
	}

	public OIRExpression getMainExpression() {
		return mainExpression;
	}

	public void setMainExpression(OIRExpression mainExpression) {
		this.mainExpression = mainExpression;
	}

	public void addTypeDeclaration (OIRType typeDeclaration)
	{
		typeDeclarations.add(typeDeclaration);
		if (typeDeclaration instanceof OIRClassDeclaration)
		{
			((OIRClassDeclaration)typeDeclaration).setClassID (classID);
			classID++;
		}
	}
	
	public List<OIRType> typeDeclarations ()
	{
		return typeDeclarations;
	}
	private OIRProgram ()
	{
		typeDeclarations = new Vector<OIRType> ();
		mainExpression = null;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
	
	public int getFieldPositionInClass (int classID, String fieldName)
	{
		OIRClassDeclaration classDecl;
		
		classDecl = getClassDeclaration (classID);
		return classDecl.getFieldPosition(fieldName);
	}
	
	public OIRClassDeclaration getClassDeclaration (int classID)
	{
		for (OIRType decl : typeDeclarations)
		{
			if (decl instanceof OIRClassDeclaration)
			{
				OIRClassDeclaration classDecl = (OIRClassDeclaration)decl;
				if (classDecl.getClassID() == classID)
					return classDecl;
			}
		}
		
		return null;
	}
	
	public String getClassName (int classID)
	{
		for (OIRType decl : typeDeclarations)
		{
			if (decl instanceof OIRClassDeclaration)
			{
				OIRClassDeclaration classDecl = (OIRClassDeclaration)decl;
				if (classDecl.getClassID() == classID)
					return classDecl.getName();
			}
		}
		
		return "";
	}
	
	public String getClassNameForCallSite (long objectAddress, int classID, 
			int callSiteID, String methodName)
	{
		if (delegateImplementation == DelegateImplementation.HASH_TABLE_NAIVE)
		{
			return delegateHashTableNaive (objectAddress, classID, methodName);
		}
		
		for (OIRType decl : typeDeclarations)
		{
			if (decl instanceof OIRClassDeclaration)
			{
				OIRClassDeclaration classDecl = (OIRClassDeclaration)decl;
				if (classDecl.getClassID() == classID)
					return classDecl.getName();
			}
		}
		
		return "";
	}
	
	public String delegateHashTableNaive (long objectAddress, int classID, String methodName)
	{
		OIRClassDeclaration oirClassDecl;

		oirClassDecl = getClassDeclaration (classID);
		
		if (oirClassDecl.isMethodInClass(methodName))
		{
			return oirClassDecl.getName();
		}
		
		while (true)
		{
			int fieldPos;
			
			fieldPos = oirClassDecl.getDelegateMethodFieldPosNaive (methodName);
			
			if (fieldPos == -1)
			{
				System.out.println("Error: Cannot find method");
				System.exit(-1);
			}
			
			objectAddress = DelegateNative.getFieldAddress(objectAddress, fieldPos);
			classID = DelegateNative.getObjectClassID(objectAddress);
			oirClassDecl = getClassDeclaration (classID);

			if (oirClassDecl.isMethodInClass(methodName))
			{
				return oirClassDecl.getName();
			}
		}		
	}
	
	public void buildStaticPIC ()
	{
		methodStaticPICMap = new HashMap<String, OIRStaticPIC> ();
		
		for (OIRType decl : typeDeclarations)
		{
			if (!(decl instanceof OIRClassDeclaration))
				continue;
			
			OIRClassDeclaration classDecl;
			
			classDecl = (OIRClassDeclaration)decl;
			
			for (OIRMemberDeclaration memDecl : classDecl.getMembers())
			{
				if (!(memDecl instanceof OIRMethod))
					continue;
				
				OIRMethod method = (OIRMethod)memDecl;
				String name = method.getDeclaration().getName();
				OIRStaticPIC pic;
				
				if (methodStaticPICMap.containsKey(name))
				{					
					pic = methodStaticPICMap.get(name);
				}
				else
				{					
					pic = new OIRStaticPIC (name);
					methodStaticPICMap.put(name, pic);
				}
				
				pic.addClassName(classDecl.getName());					
			}
		}
	}
}
