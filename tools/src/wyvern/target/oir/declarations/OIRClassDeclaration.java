package wyvern.target.oir.declarations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.expressions.OIRValue;
import wyvern.target.oir.expressions.OIRVariable;

public class OIRClassDeclaration extends OIRType {
	private String name;
	private List<OIRDelegate> delegates;
	private List<OIRMemberDeclaration> members;
	private String selfName;
	private List<OIRFieldValueInitializePair> fieldValuePairs;
	private int classID;
	private HashSet <String> methods;
	private HashMap <String, String> methodToFieldMap;
	
	public OIRClassDeclaration(OIREnvironment environment, String name, String selfName, List<OIRDelegate> delegates,
			List<OIRMemberDeclaration> members, List<OIRFieldValueInitializePair> fieldValuePairs)
	{
		super(environment);
		this.name = name;
		this.delegates = delegates;
		this.members = members;
		methods = new HashSet <String> ();
		for (OIRMemberDeclaration member : members)
		{
			if (member instanceof OIRMethod)
			{
				methods.add(((OIRMethod) member).getDeclaration().getName());
				environment.addName(((OIRMethod) member).getDeclaration().getName(), 
						((OIRMethod) member).getDeclaration().getReturnType());
			}
			else if (member instanceof OIRFieldDeclaration)
			{
				environment.addName (member.getName(), member.getType());
			}
		}
		this.selfName = selfName;
		this.setFieldValuePairs(fieldValuePairs);
		methodToFieldMap = new HashMap <String, String> ();
		for (OIRDelegate delegate : delegates)
		{
			OIRInterface oirInterface;
			
			oirInterface = (OIRInterface)delegate.getType();
			
			for (OIRMethodDeclaration methDecl : oirInterface.getMethods())
			{
				methodToFieldMap.put(methDecl.getName(), delegate.getField());
			}
		}
	}
	
	public int getDelegateMethodFieldHashMap (String method)
	{
		return getFieldPosition (methodToFieldMap.get(method));
	}
	
	public OIRFieldDeclaration getFieldDeclarationForPos (int fieldPos)
	{
		int i;
		/* Field Positions are numbered from 1, 2, 3, ... */
		i = 0;
		for (OIRMemberDeclaration memDecl : members)
		{
			if (memDecl instanceof OIRFieldDeclaration)
			{
				i++;
				
				if (fieldPos == i)
				{
					return (OIRFieldDeclaration)memDecl;
				}
			}
		}
		
		return null;
	}
	/* This method is for searching the method delegated to field sequentially */
	public int getDelegateMethodFieldPosNaive (String method)
	{
		for (OIRDelegate delegate : delegates)
		{
			OIRInterface oirInterface;
			
			oirInterface = (OIRInterface)delegate.getType();
			
			if (oirInterface.isMethodInClass(method))
				return getFieldPosition (delegate.getField());
		}
		
		return -1;
	}
	
	public boolean isMethodInClass (String method)
	{
		boolean ans = methods.contains(method); 
		return ans;
	}
	
	public String getSelfName ()
	{
		return selfName;
	}
	public OIRType getTypeForMember (String fieldName)
	{		
		for (OIRMemberDeclaration memDecls : members)
		{
			if (memDecls instanceof OIRFieldDeclaration)
			{
				if (fieldName == ((OIRFieldDeclaration)memDecls).getName())
					return ((OIRFieldDeclaration)memDecls).getType();
			}
			else if (memDecls instanceof OIRMethod)
			{
				if (((OIRMethod)memDecls).getDeclaration().getName() == fieldName)
					return ((OIRMethod)memDecls).getDeclaration().getReturnType();
			}
		}
		
		/* Not found here, search in the fields 
		 * that delegate this method */
		
		for (OIRDelegate delegate : delegates)
		{
			OIRInterface type;
			OIRType methodType;
			
			type = (OIRInterface)delegate.getType();
			methodType = type.getTypeForMember(fieldName);
			if (methodType != null)
			{
				return methodType;
			}
		}
		/* TODO Throw field not found error */
		return null;
	}
	
	public int getFieldPosition (String fieldName)
	{
		int i = 0;
		
		for (OIRMemberDeclaration memDecls : members)
		{
			if (memDecls instanceof OIRFieldDeclaration)
			{
				i++;
				if (fieldName == ((OIRFieldDeclaration)memDecls).getName())
					return i;
			}
		}
		
		return 0;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<OIRDelegate> getDelegates() {
		return delegates;
	}
	public void setDelegates(List<OIRDelegate> delegates) {
		this.delegates = delegates;
	}
	public List<OIRMemberDeclaration> getMembers() {
		return members;
	}
	public void setMembers(List<OIRMemberDeclaration> members) {
		this.members = members;
	}

	public List<OIRFieldValueInitializePair> getFieldValuePairs() {
		return fieldValuePairs;
	}

	public void setFieldValuePairs(List<OIRFieldValueInitializePair> fieldValuePairs) {
		this.fieldValuePairs = fieldValuePairs;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
	
	@Override
	public String toString ()
	{
		return getName ();
	}
	
	public void setClassID (int classID)
	{
		this.classID = classID;
	}
	
	public int getClassID ()
	{
		return classID;
	}
}
