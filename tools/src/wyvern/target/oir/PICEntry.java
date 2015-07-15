package wyvern.target.oir;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

class PICNode
{
	private int classID;
	//private int fieldPos;
		
	public PICNode(int type) {
		super();
		this.classID = type;
		//this.fieldPos = fieldPos;
		//children = new HashMap<Integer, LinkedList<PICEntry>> ();
	}
	
	public int getType() {
		return classID;
	}
	public void setType(int type) {
		this.classID = type;
	}
	public int getClassID() {
		return 0;//return fieldPos;
	}
	public void setFieldPos(int fieldPos) {
		//this.fieldPos = fieldPos;
	}
}

public class PICEntry {

	/* Map for one field to many PICEntry*/
	private HashMap<Integer, LinkedList<PICEntry>> entryMap;
	private PICNode node;
	
	public PICEntry (int classID)
	{
		entryMap = new HashMap<Integer, LinkedList<PICEntry>> ();
		node = new PICNode (classID);
	}
	
	public void addNode (int type, int fieldPos)
	{
		LinkedList<PICEntry> listEntry;
		
		listEntry = entryMap.get(fieldPos);
		
		if (listEntry == null)
		{
			listEntry = new LinkedList<PICEntry> ();
			entryMap.put(fieldPos, listEntry);
		}
		
		listEntry.add(new PICEntry (type));	
	}
	
	public void addNode (PICEntry entry, int fieldPos)
	{
		LinkedList<PICEntry> listEntry;
		
		listEntry = entryMap.get(fieldPos);
		
		if (listEntry == null)
		{
			listEntry = new LinkedList<PICEntry> ();
			entryMap.put(fieldPos, listEntry);
		}
		
		listEntry.add(entry);	
	}
	
	public LinkedList<PICEntry> getPICEntriesForField (int fieldPos)
	{
		return entryMap.get(fieldPos);
	}
	
	public Set<Integer> getFieldPosSet ()
	{
		return entryMap.keySet();
	}
	
	public int getClassID ()
	{
		return node.getClassID();
	}
}
