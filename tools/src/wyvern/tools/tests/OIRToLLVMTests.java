package wyvern.tools.tests;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

import wyvern.target.oir.EmitLLVMNative;
import wyvern.target.oir.EmitLLVMVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRNameBinding;
import wyvern.target.oir.OIRProgram;
import wyvern.target.oir.OIRTypeBinding;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRDelegate;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRFieldValueInitializePair;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRIntegerType;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.expressions.OIRExpression;
import wyvern.target.oir.expressions.OIRFieldGet;
import wyvern.target.oir.expressions.OIRFieldSet;
import wyvern.target.oir.expressions.OIRIfThenElse;
import wyvern.target.oir.expressions.OIRInteger;
import wyvern.target.oir.expressions.OIRLet;
import wyvern.target.oir.expressions.OIRMethodCall;
import wyvern.target.oir.expressions.OIRNew;
import wyvern.target.oir.expressions.OIRVariable;

public class OIRToLLVMTests {
	@Test
	public void ifThenElseTest ()
	{
		OIRInteger oirInteger;
		OIRIfThenElse oirIfThenElse;
		
		oirInteger = new OIRInteger (1);
		
		oirIfThenElse = new OIRIfThenElse (oirInteger, 
				new OIRIfThenElse (new OIRInteger (2), new OIRInteger (4), new OIRInteger (5)),
				//new OIRInteger (2),
				new OIRInteger (3));
		EmitLLVMNative.createMainFunction();
	    String toReturn = oirIfThenElse.acceptVisitor(new EmitLLVMVisitor (), OIREnvironment.getRootEnvironment());
	    EmitLLVMNative.functionCreated(toReturn);
		System.out.println ("");
	}
	
	@Test
	public void InterfaceTest ()
	{
		OIRInterface oirInterface;
		
		oirInterface = new OIRInterface ("interface1", "this", new Vector<OIRMethodDeclaration> ());
		
		oirInterface.acceptVisitor(new EmitLLVMVisitor (), OIREnvironment.getRootEnvironment());
		EmitLLVMNative.createMainFunction();
		EmitLLVMNative.functionCreated("");
		System.out.println ("");
	}
	
	@Test
	public void MethodCallTest ()
	{
		OIRInterface oirInterface;
		OIRClassDeclaration oirClass;
		List<OIRMemberDeclaration> oirMembers;
		EmitLLVMVisitor visitor;
		List<OIRFormalArg> args;
		OIRNew oirNew;
		OIRLet oirLet;
		
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirInterface = new OIRInterface ("interface1", "this", new Vector<OIRMethodDeclaration> ());
		oirMembers.add(new OIRFieldDeclaration ("field", oirInterface));
		args.add(new OIRFormalArg ("x", oirInterface));
		oirMembers.add(
				new OIRMethod (
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
						
		oirClass = new OIRClassDeclaration ("class1", "this", new Vector<OIRDelegate> (), oirMembers, null);
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, 
				new OIRMethodCall (new OIRVariable ("o"), "method", 
						new Vector<OIRExpression> ()));
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIRProgram.program.addTypeDeclaration(oirClass);
		OIREnvironment.getRootEnvironment().setBinding(new OIRTypeBinding("class1", oirClass));
		OIREnvironment.getRootEnvironment().extend(new OIRTypeBinding("interface1", oirInterface));
		OIREnvironment.getRootEnvironment().extend(new OIRNameBinding ("o", oirClass));
		oirLet.typeCheck(OIREnvironment.getRootEnvironment());
		EmitLLVMNative.oirProgramToLLVMIR(OIRProgram.program);
		oirInterface.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		oirClass.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		
		EmitLLVMNative.createMainFunction();
		String s = oirLet.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		EmitLLVMNative.functionCreated(s);
		EmitLLVMNative.executeLLVMJIT();
		System.out.println ("");
	}
	
	@Test
	public void DelegateCallTest ()
	{
		OIRInterface oirInterface;
		OIRClassDeclaration oirClass1, oirClass2;
		List<OIRMemberDeclaration> oirMembers;
		EmitLLVMVisitor visitor;
		List<OIRFormalArg> args;
		OIRNew oirNew;
		OIRLet oirLet;
		List<OIRDelegate> oirDelegate;
		List<OIRMethodDeclaration> oirMethDeclForInterface;
		
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirMethDeclForInterface = new Vector<OIRMethodDeclaration> ();
		args.add(new OIRFormalArg ("x", OIRIntegerType.getIntegerType()));
		oirMethDeclForInterface.add(new OIRMethodDeclaration (OIRIntegerType.getIntegerType(), "me2",  args));
		oirInterface = new OIRInterface ("interface1", "this", oirMethDeclForInterface);
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIREnvironment.getRootEnvironment().extend(new OIRTypeBinding("interface1", oirInterface));
		
		//Class2
		oirMembers.add(
				new OIRMethod (
						new OIRMethodDeclaration (OIRIntegerType.getIntegerType(), "me2",  args),
		     			new OIRInteger (1234)));
						
		oirClass2 = new OIRClassDeclaration ("class2", "this", new Vector<OIRDelegate> (), oirMembers, null);
		OIRProgram.program.addTypeDeclaration(oirClass2);
		OIREnvironment.getRootEnvironment().setBinding(new OIRTypeBinding("class2", oirClass2));
		
		//Class1
		oirMembers = new Vector<OIRMemberDeclaration> ();
		oirMembers.add(new OIRFieldDeclaration ("field", oirInterface));
		args = new Vector<OIRFormalArg> ();
		args.add(new OIRFormalArg ("x", oirInterface));
		oirMembers.add(
				new OIRMethod (
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		oirDelegate = new Vector<OIRDelegate> ();
		oirDelegate.add(new OIRDelegate (oirInterface, "field"));
		
		List<OIRFieldValueInitializePair> oirInit;
		oirInit = new Vector<OIRFieldValueInitializePair> ();
		oirInit.add(new OIRFieldValueInitializePair((OIRFieldDeclaration)oirMembers.get(0), 
				new OIRNew (new Vector<OIRExpression> (), "class2")));
		oirClass1 = new OIRClassDeclaration ("class1", "this", oirDelegate, oirMembers, oirInit);
		OIRProgram.program.addTypeDeclaration(oirClass1);
		OIREnvironment.getRootEnvironment().extend(new OIRTypeBinding("class1", oirClass1));
		
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, 
				new OIRMethodCall (new OIRVariable ("o"), "me2", 
						new Vector<OIRExpression> ()));
		
		OIREnvironment.getRootEnvironment().extend(new OIRNameBinding ("o", oirClass1));
		oirLet.typeCheck(OIREnvironment.getRootEnvironment());
		EmitLLVMNative.oirProgramToLLVMIR(OIRProgram.program);
		oirInterface.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		oirClass2.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		oirClass1.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		
		EmitLLVMNative.createMainFunction();
		String s = oirLet.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		EmitLLVMNative.functionCreated(s);
		EmitLLVMNative.executeLLVMJIT();
		System.out.println ("");
	}
	
	@Test
	public void FieldGetTest ()
	{
		OIRInterface oirInterface;
		OIRClassDeclaration oirClass;
		List<OIRMemberDeclaration> oirMembers;
		EmitLLVMVisitor visitor;
		List<OIRFormalArg> args;
		List<OIRFieldValueInitializePair> oirInits;
		OIRNew oirNew;
		OIRLet oirLet;
		OIRFieldDeclaration fieldDecl;
		
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		fieldDecl = new OIRFieldDeclaration ("field", OIRIntegerType.getIntegerType());
		oirInterface = new OIRInterface ("interface1", "this", new Vector<OIRMethodDeclaration> ());
		oirMembers.add(fieldDecl);
		args.add(new OIRFormalArg ("x", oirInterface));
		oirMembers.add(
				new OIRMethod (
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		oirInits = new Vector<OIRFieldValueInitializePair> ();
		oirInits.add(new OIRFieldValueInitializePair (fieldDecl, new OIRInteger(12)));
		oirClass = new OIRClassDeclaration ("class1", "this", new Vector<OIRDelegate> (), oirMembers, oirInits);
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, new OIRFieldGet (new OIRVariable ("o"), "field"));
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIRProgram.program.addTypeDeclaration(oirClass);
		OIREnvironment.getRootEnvironment().setBinding(new OIRTypeBinding("class1", oirClass));
		OIREnvironment.getRootEnvironment().extend(new OIRTypeBinding("interface1", oirInterface));
		OIREnvironment.getRootEnvironment().extend(new OIRNameBinding ("o", oirClass));
		oirLet.typeCheck(OIREnvironment.getRootEnvironment());
		EmitLLVMNative.oirProgramToLLVMIR(OIRProgram.program);
		oirInterface.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		oirClass.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		
		EmitLLVMNative.createMainFunction();
		String s = oirLet.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		EmitLLVMNative.functionCreated(s);
		EmitLLVMNative.executeLLVMJIT();
		System.out.println ("");
	}
	
	@Test
	public void FieldSetTest ()
	{
		OIRInterface oirInterface;
		OIRClassDeclaration oirClass;
		List<OIRMemberDeclaration> oirMembers;
		EmitLLVMVisitor visitor;
		List<OIRFormalArg> args;
		OIRNew oirNew;
		OIRLet oirLet;
		
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirInterface = new OIRInterface ("interface1", "this", new Vector<OIRMethodDeclaration> ());
		oirMembers.add(new OIRFieldDeclaration ("field", OIRIntegerType.getIntegerType()));
		args.add(new OIRFormalArg ("x", oirInterface));
		oirMembers.add(
				new OIRMethod (
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
						
		oirClass = new OIRClassDeclaration ("class1", "this", new Vector<OIRDelegate> (), oirMembers, null);
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, new OIRFieldSet (new OIRVariable ("o"), "field", new OIRInteger (1)));
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIRProgram.program.addTypeDeclaration(oirClass);
		OIREnvironment.getRootEnvironment().setBinding(new OIRTypeBinding("class1", oirClass));
		OIREnvironment.getRootEnvironment().extend(new OIRTypeBinding("interface1", oirInterface));
		OIREnvironment.getRootEnvironment().extend(new OIRNameBinding ("o", oirClass));
		oirLet.typeCheck(OIREnvironment.getRootEnvironment());
		EmitLLVMNative.oirProgramToLLVMIR(OIRProgram.program);
		oirInterface.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		oirClass.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		
		EmitLLVMNative.createMainFunction();
		String s = oirLet.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		EmitLLVMNative.functionCreated(s);
		EmitLLVMNative.executeLLVMJIT();
		System.out.println ("");
	}
}
