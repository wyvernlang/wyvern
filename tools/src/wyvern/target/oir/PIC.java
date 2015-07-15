package wyvern.target.oir;

import java.util.HashMap;
import java.util.LinkedList;

public class PIC {
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
	
	public void addPICEntry (int classID, PICEntry entry)
	{
		classIDEntryMap.put(classID, entry);
	}
	
	/* Returns classID if found the hit 
	 * Otherwise returns empty string 
	 * */
	private int searchMethod (PICEntry entry, long objectAddress)
	{
		int classID;
		long objectAddressToContinueIn;
		
		objectAddressToContinueIn = objectAddress;
		
		for (Integer fieldPos : entry.getFieldPosSet())
		{
			LinkedList<PICEntry> listEntries;
			long fieldAddress;
			int fieldClassID;
			
			listEntries = entry.getPICEntriesForField(fieldPos);
			fieldAddress = DelegateNative.getFieldAddress(objectAddress, 
					fieldPos);
			fieldClassID = DelegateNative.getObjectClassID(fieldAddress); 
			
			for (PICEntry _entry : listEntries)
			{				
				if (fieldClassID == _entry.getClassID())
				{
					if (_entry.getFieldPosSet().isEmpty())
					{
						return fieldClassID;
					}
					
					classID = searchMethod (_entry, fieldAddress);

					if (classID != -1)
					{
						return classID;
					}
				}
			}
		}
		
		/* Couldn't find Delegated Method 
		 * in this field */
		
		return -1;
	}
	
	public String search (int classID, long objectAddress)
	{
		PICEntry entry;
		int _classID;
		
		entry = classIDEntryMap.get(classID);
		if (entry == null)
		{
			/* PICEntry for this class not present. 
			 * Find the method
			 * */
			entry = new PICEntry (classID);
			return OIRProgram.program.delegateHashTableBuildPICEntry(objectAddress, classID, 
					methodName, entry);
		}
		else
		{
			_classID = searchMethod (entry, objectAddress);
			if (_classID == -1)
			{
				/* Method cannot be find in PIC 
				 * Do HashTable Lookup 
				 * */
				
				return OIRProgram.program.delegateHashTableBuildPICEntry(objectAddress, classID, 
						methodName, entry);
			}
			else
			{
				return OIRProgram.program.getClassName(_classID);
			}
		}
	}
}
