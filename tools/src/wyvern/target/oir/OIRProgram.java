package wyvern.target.oir;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.OIRExpression;
import wyvern.tools.errors.WyvernException;

class MethodAddress
{
	private String className;
	private long objectAddress;
	public String getClassName() {
		return className;
	}
	
	public long getObjectAddress() {
		return objectAddress;
	}
	
	public MethodAddress(String className, long objectAddress) {
		super();
		this.className = className;
		this.objectAddress = objectAddress;
	}
}

public class OIRProgram extends OIRAST {
	private List<OIRType> typeDeclarations;
	private OIRExpression mainExpression;
	private static int classID = 0;
	private PIC[] picArray;
	private int totalCallSites;
	
	public static OIRProgram program = new OIRProgram ();
	
	public static enum DelegateImplementation
	{
		HASH_TABLE_NAIVE, /* Every call will do Hash Table Lookup */
		PIC, /* Using PIC */
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

	public void typeCheck (OIREnvironment environment)
	{
		for (OIRType oirType : typeDeclarations)
		{
			if (oirType instanceof OIRClassDeclaration)
			{
				OIRClassDeclaration classDecl;
				
				classDecl = (OIRClassDeclaration) oirType;
				
				for (OIRMemberDeclaration memDecl : classDecl.getMembers())
				{
					if (memDecl instanceof OIRMethod)
					{
						OIRMethod methDecl;
						
						methDecl = (OIRMethod)memDecl; 
						methDecl.getBody().typeCheck(methDecl.getEnvironment ());
					}
				}
			}
		}
		
		mainExpression.typeCheck(environment);
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
		totalCallSites = 0;
	}

	public void setCallSites (int callSitesNum, String[] methodArray)
	{
		totalCallSites = callSitesNum;
		picArray = new PIC[callSitesNum];
		
		for (int i = 0; i < callSitesNum; i++)
		{
			picArray[i] = new PIC (i, methodArray[i]);
		}
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
	
	public MethodAddress getClassNameForCallSite (long objectAddress, int classID, 
			int callSiteID, String methodName)
	{
		if (delegateImplementation == DelegateImplementation.HASH_TABLE_NAIVE)
		{
			return delegateHashTableNaive (objectAddress, classID, methodName);
		}
		else if (delegateImplementation == DelegateImplementation.PIC)
		{
			PIC pic;
			String className;
			OIRClassDeclaration oirClassDecl;

			oirClassDecl = getClassDeclaration (classID);
			pic = picArray [callSiteID];
			return pic.search(classID, objectAddress);
		}
		
		throw new WyvernException ("Invalid Delegate Implementation selected");
	}
	
	public MethodAddress delegateHashTableBuildPICEntry (long objectAddress, 
			int classID, OIRClassDeclaration oirClassDecl, String methodName, PICEntry classPICEntry, 
			long fieldAddress, int fieldPos, int fieldClassID)
	{
		PICEntry _entry;
		PICEntry lastFinalEntry;
		long lastFinalObjAddress;
		
		_entry = classPICEntry;
		if (_entry.isFinal == true)
		{
			lastFinalEntry = _entry;
			lastFinalObjAddress = objectAddress;
		}
		else
		{
			lastFinalObjAddress = -1;
			lastFinalEntry = null;
		}
		
		if (fieldAddress != -1 && fieldPos != -1 && fieldClassID != -1)
		{
			/* This means PIC's search method have already found the field's 
			 * classID. So let us first search in the field. 
			 * Then go to the object's field
			 * */
			PICEntry fieldPICEntry;
			OIRClassDeclaration fieldClassDecl;
			
			oirClassDecl = getClassDeclaration (classID);
			fieldPos = oirClassDecl.getDelegateMethodFieldHashMap (methodName);
			
			if (fieldPos == -1)
			{
				System.out.println("Error: Cannot find method in any of the fields");
				System.exit(-1);
			}
			
			fieldAddress = DelegateNative.getFieldAddress(oirClassDecl.getName(),
					objectAddress, fieldPos);
			fieldClassID = DelegateNative.getObjectClassID(fieldAddress);
			fieldClassDecl = getClassDeclaration (fieldClassID);
			fieldPICEntry = new PICEntry (fieldClassID, fieldClassDecl);
			
			if (oirClassDecl.getFieldDeclarationForPos(fieldPos).isFinal())
			{
				_entry.setIsFinal(true);
				if (lastFinalEntry == null)
				{
					lastFinalEntry = _entry;
					lastFinalObjAddress = objectAddress;
				}
			}
			else
			{
				if (lastFinalEntry != null && lastFinalObjAddress != -1)
				{
					lastFinalEntry.setFinalObjectAddress (lastFinalObjAddress, fieldAddress, fieldPICEntry);
				}
				
				lastFinalEntry = null;
				lastFinalObjAddress = -1;
			}
			
			_entry.setFeildPos(fieldPos);
			_entry.addChildEntry(fieldClassID, fieldPICEntry);
			_entry = fieldPICEntry;
			classID = fieldClassID;
			objectAddress = fieldAddress;
            oirClassDecl = fieldClassDecl;
			
			if (oirClassDecl.isMethodInClass(methodName))
			{
				return new MethodAddress (oirClassDecl.getName(), objectAddress);
			}
		}
		
		/* Now start looking in the object's fields */
		while (true)
		{
			PICEntry fieldPICEntry;
			OIRClassDeclaration fieldClassDecl;
			
			oirClassDecl = getClassDeclaration (classID);
			fieldPos = oirClassDecl.getDelegateMethodFieldHashMap (methodName);
			
			if (fieldPos == -1)
			{
				System.out.println("Error: Cannot find method in any of the fields");
				System.exit(-1);
			}
			
			fieldAddress = DelegateNative.getFieldAddress(oirClassDecl.getName(),
					objectAddress, fieldPos);
			fieldClassID = DelegateNative.getObjectClassID(fieldAddress);
			fieldClassDecl = getClassDeclaration (fieldClassID);
			fieldPICEntry = new PICEntry (fieldClassID, fieldClassDecl);
			
			if (oirClassDecl.getFieldDeclarationForPos(fieldPos).isFinal())
			{
				_entry.setIsFinal(true);
				if (lastFinalEntry == null)
				{
					lastFinalEntry = _entry;
					lastFinalObjAddress = objectAddress;
				}
			}
			else
			{
				if (lastFinalEntry != null && lastFinalObjAddress != -1)
				{
					lastFinalEntry.setFinalObjectAddress (lastFinalObjAddress, fieldAddress, fieldPICEntry);
				}
				
				lastFinalEntry = null;
				lastFinalObjAddress = -1;
			}
			
			_entry.setFeildPos(fieldPos);
			_entry.addChildEntry(fieldClassID, fieldPICEntry);
			_entry = fieldPICEntry;
			classID = fieldClassID;
			objectAddress = fieldAddress;
            oirClassDecl = fieldClassDecl;
			
			if (oirClassDecl.isMethodInClass(methodName))
			{
				return new MethodAddress (oirClassDecl.getName(), objectAddress);
			}
		}
	}
	
	public MethodAddress delegateHashTableNaive (long objectAddress, int classID, String methodName)
	{
		OIRClassDeclaration oirClassDecl;

		oirClassDecl = getClassDeclaration (classID);
		boolean ans = oirClassDecl.isMethodInClass(methodName);
		if (ans)
		{
			return new MethodAddress (oirClassDecl.getName(), objectAddress);
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
			
			objectAddress = DelegateNative.getFieldAddress(oirClassDecl.getName(), 
					objectAddress, fieldPos);
			classID = DelegateNative.getObjectClassID(objectAddress);
			oirClassDecl = getClassDeclaration (classID);

			if (oirClassDecl.isMethodInClass(methodName))
			{
				return new MethodAddress (oirClassDecl.getName(), objectAddress);
			}
		}		
	}
	
//	public void buildStaticPIC ()
//	{
//		methodStaticPICMap = new HashMap<String, OIRStaticPIC> ();
//		
//		for (OIRType decl : typeDeclarations)
//		{
//			if (!(decl instanceof OIRClassDeclaration))
//				continue;
//			
//			OIRClassDeclaration classDecl;
//			
//			classDecl = (OIRClassDeclaration)decl;
//			
//			for (OIRMemberDeclaration memDecl : classDecl.getMembers())
//			{
//				if (!(memDecl instanceof OIRMethod))
//					continue;
//				
//				OIRMethod method = (OIRMethod)memDecl;
//				String name = method.getDeclaration().getName();
//				OIRStaticPIC pic;
//				
//				if (methodStaticPICMap.containsKey(name))
//				{					
//					pic = methodStaticPICMap.get(name);
//				}
//				else
//				{					
//					pic = new OIRStaticPIC (name);
//					methodStaticPICMap.put(name, pic);
//				}
//				
//				pic.addClassName(classDecl.getName());					
//			}
//		}
//	}
}
