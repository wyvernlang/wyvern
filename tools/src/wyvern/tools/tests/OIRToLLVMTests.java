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
		OIREnvironment rootEnv;
		OIREnvironment interfaceEnv;
		
		rootEnv = OIREnvironment.getRootEnvironment();
		interfaceEnv = new OIREnvironment (rootEnv);
		oirInterface = new OIRInterface (interfaceEnv, "interface1", 
				"this", new Vector<OIRMethodDeclaration> ());
		rootEnv.addName("interface1", oirInterface);
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
		OIREnvironment rootEnv;
		OIREnvironment interfaceEnv;
		OIREnvironment classEnv;
		OIREnvironment methodEnv;
		
		rootEnv = OIREnvironment.getRootEnvironment();
		interfaceEnv = new OIREnvironment (rootEnv);
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirInterface = new OIRInterface (interfaceEnv, 
				"interface1", "this", new Vector<OIRMethodDeclaration> ());
		rootEnv.addName("interface1", oirInterface);
		classEnv = new OIREnvironment (rootEnv);
		oirMembers.add(new OIRFieldDeclaration ("field", oirInterface));
		classEnv.addName("field", oirInterface);
		methodEnv = new OIREnvironment (classEnv);
		args.add(new OIRFormalArg ("x", oirInterface));
		methodEnv.addName("x", oirInterface);
		oirMembers.add(
				new OIRMethod (methodEnv, 
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		classEnv.addName("method", oirInterface);
		oirClass = new OIRClassDeclaration (classEnv, "class1", "this", new Vector<OIRDelegate> (), oirMembers, null);
		rootEnv.addName("class1", oirClass);
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, 
				new OIRMethodCall (new OIRVariable ("o"), "method", 
						new Vector<OIRExpression> ()));
		OIRProgram.program.setMainExpression(oirLet);
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIRProgram.program.addTypeDeclaration(oirClass);
		rootEnv.addName ("o", oirNew.typeCheck(rootEnv));
		
		OIRProgram.program.typeCheck(rootEnv);
		
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
		OIREnvironment rootEnv;
		OIREnvironment interfaceEnv;
		OIREnvironment classEnv;
		OIREnvironment methodEnv;
		
		rootEnv = OIREnvironment.getRootEnvironment();
		interfaceEnv = new OIREnvironment (rootEnv);
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirMethDeclForInterface = new Vector<OIRMethodDeclaration> ();
		methodEnv = new OIREnvironment (interfaceEnv);
		args.add(new OIRFormalArg ("x", OIRIntegerType.getIntegerType()));
		methodEnv.addName("x", OIRIntegerType.getIntegerType());
		oirMethDeclForInterface.add(new OIRMethodDeclaration (OIRIntegerType.getIntegerType(), "me2",  args));
		interfaceEnv.addName("me2", OIRIntegerType.getIntegerType());
		oirInterface = new OIRInterface (interfaceEnv, "interface1", "this", oirMethDeclForInterface);
		OIRProgram.program.addTypeDeclaration(oirInterface);
		rootEnv.addName("interface1", oirInterface);
		
		//Class2
		classEnv = new OIREnvironment (rootEnv);
		methodEnv = new OIREnvironment (classEnv);
		methodEnv.addName("x", OIRIntegerType.getIntegerType());
		oirMembers.add(
				new OIRMethod (methodEnv,
						new OIRMethodDeclaration (OIRIntegerType.getIntegerType(), "me2",  args),
		     			new OIRInteger (1234)));
		classEnv.addName ("me2", OIRIntegerType.getIntegerType());				
		oirClass2 = new OIRClassDeclaration (classEnv, "class2", "this", new Vector<OIRDelegate> (), oirMembers, null);
		OIRProgram.program.addTypeDeclaration(oirClass2);
		rootEnv.addName("class2", oirClass2);
		
		//Class1
		classEnv = new OIREnvironment (rootEnv);
		methodEnv = new OIREnvironment (classEnv);
		oirMembers = new Vector<OIRMemberDeclaration> ();
		oirMembers.add(new OIRFieldDeclaration ("field", oirInterface));
		classEnv.addName("field", oirInterface);
		args = new Vector<OIRFormalArg> ();
		args.add(new OIRFormalArg ("x", oirInterface));
		methodEnv.addName("x", oirInterface);
		oirMembers.add(
				new OIRMethod (methodEnv,
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		classEnv.addName("method", oirInterface);
		oirDelegate = new Vector<OIRDelegate> ();
		oirDelegate.add(new OIRDelegate (oirInterface, "field"));
		
		List<OIRFieldValueInitializePair> oirInit;
		oirInit = new Vector<OIRFieldValueInitializePair> ();
		oirInit.add(new OIRFieldValueInitializePair((OIRFieldDeclaration)oirMembers.get(0), 
				new OIRNew (new Vector<OIRExpression> (), "class2")));
		oirClass1 = new OIRClassDeclaration (classEnv, "class1", "this", oirDelegate, oirMembers, oirInit);
		OIRProgram.program.addTypeDeclaration(oirClass1);
		rootEnv.addName("class1", oirClass1);
		
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, 
				new OIRMethodCall (new OIRVariable ("o"), "me2", 
						new Vector<OIRExpression> ()));
		rootEnv.addName ("o", oirNew.typeCheck(rootEnv));
		OIRProgram.program.setMainExpression(oirLet);
		OIRProgram.program.typeCheck(rootEnv);
		EmitLLVMNative.oirProgramToLLVMIR(OIRProgram.program);
		oirInterface.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		System.out.println("About to crash on a Mac with:");
		System.out.println("Assertion failed: (!empty() && \"Called front() on empty list!\"), function front, file /usr/local/include/llvm/ADT/ilist.h, line 391.");
		oirClass2.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		System.out.println("Congratulations for not using a Mac or fixing this bug!");
		oirClass1.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		
		EmitLLVMNative.createMainFunction();
		String s = oirLet.acceptVisitor(visitor, OIREnvironment.getRootEnvironment());
		EmitLLVMNative.functionCreated(s);
		EmitLLVMNative.executeLLVMJIT();
		System.out.println ("");
	}
	
	public void PICTest ()
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
		OIREnvironment rootEnv;
		OIREnvironment interfaceEnv;
		OIREnvironment classEnv;
		OIREnvironment methodEnv;
		
		rootEnv = OIREnvironment.getRootEnvironment();
		interfaceEnv = new OIREnvironment (rootEnv);
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirMethDeclForInterface = new Vector<OIRMethodDeclaration> ();
		methodEnv = new OIREnvironment (interfaceEnv);
		args.add(new OIRFormalArg ("x", OIRIntegerType.getIntegerType()));
		methodEnv.addName("x", OIRIntegerType.getIntegerType());
		oirMethDeclForInterface.add(new OIRMethodDeclaration (OIRIntegerType.getIntegerType(), "me2",  args));
		interfaceEnv.addName("me2", OIRIntegerType.getIntegerType());
		oirInterface = new OIRInterface (interfaceEnv, "interface1", "this", oirMethDeclForInterface);
		OIRProgram.program.addTypeDeclaration(oirInterface);
		rootEnv.addName("interface1", oirInterface);
		
		//Class2
		classEnv = new OIREnvironment (rootEnv);
		methodEnv = new OIREnvironment (classEnv);
		methodEnv.addName("x", OIRIntegerType.getIntegerType());
		oirMembers.add(
				new OIRMethod (methodEnv,
						new OIRMethodDeclaration (OIRIntegerType.getIntegerType(), "me2",  args),
		     			new OIRInteger (1234)));
		classEnv.addName ("me2", OIRIntegerType.getIntegerType());				
		oirClass2 = new OIRClassDeclaration (classEnv, "class2", "this", new Vector<OIRDelegate> (), oirMembers, null);
		OIRProgram.program.addTypeDeclaration(oirClass2);
		rootEnv.addName("class2", oirClass2);
		
		//Class1
		classEnv = new OIREnvironment (rootEnv);
		methodEnv = new OIREnvironment (classEnv);
		oirMembers = new Vector<OIRMemberDeclaration> ();
		oirMembers.add(new OIRFieldDeclaration ("field", oirInterface));
		classEnv.addName("field", oirInterface);
		args = new Vector<OIRFormalArg> ();
		args.add(new OIRFormalArg ("x", oirInterface));
		methodEnv.addName("x", oirInterface);
		oirMembers.add(
				new OIRMethod (methodEnv,
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		classEnv.addName("method", oirInterface);
		oirDelegate = new Vector<OIRDelegate> ();
		oirDelegate.add(new OIRDelegate (oirInterface, "field"));
		
		List<OIRFieldValueInitializePair> oirInit;
		oirInit = new Vector<OIRFieldValueInitializePair> ();
		oirInit.add(new OIRFieldValueInitializePair((OIRFieldDeclaration)oirMembers.get(0), 
				new OIRNew (new Vector<OIRExpression> (), "class2")));
		oirClass1 = new OIRClassDeclaration (classEnv, "class1", "this", oirDelegate, oirMembers, oirInit);
		OIRProgram.program.addTypeDeclaration(oirClass1);
		rootEnv.addName("class1", oirClass1);
		
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, 
				new OIRMethodCall (new OIRVariable ("o"), "me2", 
						new Vector<OIRExpression> ()));
		rootEnv.addName ("o", oirNew.typeCheck(rootEnv));
		OIRProgram.program.setMainExpression(oirLet);
		OIRProgram.program.typeCheck(rootEnv);
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
		OIREnvironment rootEnv;
		OIREnvironment interfaceEnv;
		OIREnvironment classEnv;
		OIREnvironment methodEnv;
		
		rootEnv = OIREnvironment.getRootEnvironment();
		interfaceEnv = new OIREnvironment (rootEnv);
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirInterface = new OIRInterface (interfaceEnv, "interface1", "this", new Vector<OIRMethodDeclaration> ());
		rootEnv.addName("interface1", oirInterface);
		classEnv = new OIREnvironment (rootEnv);
		methodEnv = new OIREnvironment (classEnv);
		fieldDecl = new OIRFieldDeclaration ("field", OIRIntegerType.getIntegerType());
		classEnv.addName("field", OIRIntegerType.getIntegerType());
		oirMembers.add(fieldDecl);
		args.add(new OIRFormalArg ("x", oirInterface));
		methodEnv.addName("x", oirInterface);
		oirMembers.add(
				new OIRMethod (methodEnv,
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		classEnv.addName("method", oirInterface);
		oirInits = new Vector<OIRFieldValueInitializePair> ();
		oirInits.add(new OIRFieldValueInitializePair (fieldDecl, new OIRInteger(12)));
		oirClass = new OIRClassDeclaration (classEnv, "class1", "this", new Vector<OIRDelegate> (), oirMembers, oirInits);
		rootEnv.addName("class1", oirClass);
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, new OIRFieldGet (new OIRVariable ("o"), "field"));
		rootEnv.addName ("o", oirNew.typeCheck(rootEnv));
		OIRProgram.program.setMainExpression(oirLet);
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIRProgram.program.addTypeDeclaration(oirClass);
		
		OIRProgram.program.typeCheck(rootEnv);
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
		OIREnvironment rootEnv;
		OIREnvironment interfaceEnv;
		OIREnvironment classEnv;
		OIREnvironment methodEnv;
		
		rootEnv = OIREnvironment.getRootEnvironment();
		interfaceEnv = new OIREnvironment (rootEnv);
		args = new Vector<OIRFormalArg> ();
		visitor = new EmitLLVMVisitor ();
		oirMembers = new Vector <OIRMemberDeclaration> ();
		oirInterface = new OIRInterface (interfaceEnv, "interface1", "this", new Vector<OIRMethodDeclaration> ());
		rootEnv.addName("interface1", oirInterface);
		
		classEnv = new OIREnvironment (rootEnv);
		oirMembers.add(new OIRFieldDeclaration ("field", OIRIntegerType.getIntegerType()));
		args.add(new OIRFormalArg ("x", oirInterface));
		methodEnv = new OIREnvironment (classEnv);
		oirMembers.add(
				new OIRMethod (methodEnv,
						new OIRMethodDeclaration (oirInterface, "method",  args),
						new OIRInteger (34)));
		oirClass = new OIRClassDeclaration (classEnv, "class1", "this", new Vector<OIRDelegate> (), oirMembers, null);
		rootEnv.addName("class1", oirClass);
		oirNew = new OIRNew (new Vector<OIRExpression> (), "class1");
		oirLet = new OIRLet ("o", oirNew, new OIRFieldSet (new OIRVariable ("o"), "field", new OIRInteger (1)));
		rootEnv.addName ("o", oirNew.typeCheck(rootEnv));
		OIRProgram.program.setMainExpression(oirLet);
		OIRProgram.program.addTypeDeclaration(oirInterface);
		OIRProgram.program.addTypeDeclaration(oirClass);

		OIRProgram.program.typeCheck(rootEnv);
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
