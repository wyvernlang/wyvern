package wyvern.target.oir;

import java.util.HashMap;
import java.util.LinkedList;

import wyvern.target.oir.declarations.OIRClassDeclaration;

public class PIC {
	private static byte linkedListMapThreshold = 10;
	private int callSiteNum;
	private String methodName;
	/* To represents PICEntry for different classIDs */
	private HashMap <Integer, PICEntry> classIDEntryMap;
	
	public PIC (int callSiteNum, String methodName)
	{
		this.callSiteNum = callSiteNum;
		this.methodName = methodName;
		classIDEntryMap = new HashMap<Integer, PICEntry> ();
	}
	
	public PIC (int callSiteNum)
	{
		this.callSiteNum = callSiteNum;
		this.methodName = "";
		classIDEntryMap = new HashMap<Integer, PICEntry> ();
	}
	
	public void setMethodName (String methName)
	{
		this.methodName = methName;
	}
	
	public void addPICEntry (int classID, PICEntry entry)
	{
		classIDEntryMap.put(classID, entry);
	}
	
	/* Returns classID if found the hit 
	 * Otherwise returns empty string 
	 * */
	private MethodAddress searchMethod (int classID, PICEntry entry, long objectAddress)
	{
		OIRClassDeclaration classDecl;
		
		classDecl = OIRProgram.program.getClassDeclaration(classID);
		
		while (true)
		{
			String className;
			PICEntry fieldEntry;
			int fieldPos;
			long fieldAddress = 0;
			int fieldClassID = 0;
			
			if (entry.getIsFinal ())
			{
				/* Entry is of the type FinalPICEntry */
				FinalPICNode finalNode;
				
				/* First search for an object address */
				finalNode = entry.containsObjectAddress(objectAddress);
				if (finalNode != null)
				{
					/* this object address was present */
					entry = finalNode.getPicEntry();
					classID = entry.getClassID();
					classDecl = entry.getClassDecl();
				}
			}
			
			fieldEntry = null;
			className = classDecl.getName();
			fieldPos = entry.getFieldPos();
			
			if (fieldPos == -1)
			{
				/* fieldPos is NULL would mean, we have found the 
				 * last class */
				return new MethodAddress (className, objectAddress);
			}

			fieldAddress = DelegateNative.getFieldAddress (className, 
														   objectAddress, 
														   fieldPos);
			fieldClassID = DelegateNative.getObjectClassID(fieldAddress);
			fieldEntry = entry.getEntry(fieldClassID);
			
			if (fieldEntry == null)
			{
				/* Couldn't find method, do HashTable search */
				return OIRProgram.program.delegateHashTableBuildPICEntry(objectAddress, classID, classDecl,
						methodName, entry, fieldAddress, fieldPos, fieldClassID);
			}
			else
			{
				entry = fieldEntry;
				classID = fieldClassID;
				classDecl = fieldEntry.getClassDecl();
				objectAddress = fieldAddress;
			}
			
		}
	}
	
	public MethodAddress search (int classID, long objectAddress)
	{
		PICEntry entry;
		
		entry = classIDEntryMap.get(classID);
		if (entry == null)
		{
			/* PICEntry for this class not present. 
			 * Find the method
			 * */
			OIRClassDeclaration classDecl;
			
			classDecl = OIRProgram.program.getClassDeclaration(classID);
			entry = new PICEntry (classID, classDecl);
			return OIRProgram.program.delegateHashTableBuildPICEntry(objectAddress, classID, classDecl,
					methodName, entry, -1, -1, -1);
		}
		else
		{
			return searchMethod (classID, entry, objectAddress);
		}
	}

	public static byte getLinkedListMapThreshold() {
		return linkedListMapThreshold;
	}
}
