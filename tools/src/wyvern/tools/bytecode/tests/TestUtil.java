package wyvern.tools.bytecode.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import wyvern.DSL.DSL;
import wyvern.targets.Common.WyvernIL.ExnFromAST;
import wyvern.targets.Common.WyvernIL.TLFromAST;
import wyvern.targets.Common.WyvernIL.Def.Def.Param;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.core.Interpreter;
import wyvern.tools.bytecode.values.BytecodeClass;
import wyvern.tools.bytecode.values.BytecodeClassDef;
import wyvern.tools.bytecode.values.BytecodeEmptyVal;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeValue;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * This class defines some helper functions for the tests as well as some
 * common variables and common setUp/tearDown for them
 * 
 * every test extends this class
 * @author Tal
 *
 */
public class TestUtil {

	protected boolean PRINTS_ON = false; 	// add PRINTS_ON = true at the
											// beginning of a test for some
	protected String s;						// instruction/context prints
	protected Interpreter interperter;
	protected BytecodeValue empty;
	protected BytecodeValue func,clas,clasDef;	// empty type declarations
												// to be used as values in
												// the isInContext method
	
	protected String List = "";				// holds a simple linked list
											// implementation
	
	protected String Mod;			// holds a simple modulus implementation
	

	@Before
	public void setUp() throws Exception {
		Mod = "def mod(num : Int, over : Int)	: Int		\n"
		+ 	"	num - (over * (num / over))					\n";
		List<Param> params = new ArrayList<Param>();
		List<Statement> statements = new ArrayList<Statement>();
		func = new BytecodeFunction(params,statements,new BytecodeContextImpl(), "");
		clas = new BytecodeClass(new BytecodeContextImpl());
		clasDef = new BytecodeClass(new BytecodeContextImpl());
		empty = new BytecodeEmptyVal();
		setupList();
	}

	@After
	public void tearDown() throws Exception {
		if(PRINTS_ON) {
			System.out.println("\n ================================= \n");
			PRINTS_ON = false;
		}
	}
	
	/*
	 * method copied exactly from wyvern.targets.Common.WyvernIL.tests.TestIL
	 */
	private List<Statement> getResult(TypedAST input) {
		if (!(input instanceof CoreAST))
			throw new RuntimeException();
		CoreAST cast = (CoreAST) input;
		TLFromAST.flushInts();
		New.resetGenNum();
		ExnFromAST visitor = new ExnFromAST();
		cast.accept(visitor);
		return visitor.getStatments();
	}
	
	/**
	 * sets up the statements and runs the test, then prints results and
	 * the simplified context if the PRINTS_ON flag is on
	 * @param s
	 * 		the string representing the high level language
	 * @return
	 * 		the BytecodeValue of the final evaluated value
	 */
	public BytecodeValue runTest(String s) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(s);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);
		interperter = new Interpreter(statements);
		
		if(PRINTS_ON) {
			System.out.println("Instructions:");
			for (Statement statement : statements) {
				System.out.println(statement.getClass().getSimpleName() + " : "
						+ statement.toString());
			}
		}

		BytecodeValue res = interperter.execute();
		
		// currently printing only the simplified context (no variables with
		// '$' in their names, for full context print needs to change the
		// Interpreter printContext() method to use toString instead of
		// toSimpleString
		if(PRINTS_ON) {
			interperter.printContext();
			System.out.println("		DONE");	
		}
		return res;
	}
	
	/**
	 * checks if an array of names is in the context with the correct 
	 * corresponding values associated to them
	 * @param names
	 * 		expected names in the context
	 * @param vals
	 * 		expected values for the names
	 * @return
	 * 		whether all the name/value pairs have been found correctly
	 */
	public boolean isInContext(String[] names, BytecodeValue[] vals) {
		BytecodeContext context = interperter.getCurrentContext();
		for(int i = 0 ; i < names.length ; i++) {
			try {
				BytecodeValue val = context.getValue(names[i]).dereference();
				if(val instanceof BytecodeFunction) {
					if(vals[i] instanceof BytecodeFunction) {
						continue;
					} else {
						return false;
					}
				}
				if(val instanceof BytecodeClass) {
					if(vals[i] instanceof BytecodeClass) {
						continue;
					} else {
						return false;
					}
				}
				if(val instanceof BytecodeClassDef) {
					if(vals[i] instanceof BytecodeClassDef) {
						continue;
					} else {
						return false;
					}
				}
				if(!val.equals(vals[i])) {
					System.err.println("in context: " + val + " given: " + vals[i]);
					return false;
				}
			} catch(RuntimeException e) {
				return false;
			}
		}
		return true;
	}


	private void setupList() {
		if(List != "") {
			return;
		}
		List = "type IntNode     											\n"
		+"	def getNext() : IntNode                                         \n"
		+"	def setNext(next : IntNode) : Unit                           	\n"
		+"	def getValue() : Int                                            \n"
		+"	def hasNext() : Bool                                            \n"
		+"                                                                  \n"
		+"class DummyNode                                                   \n"
		+"	implements IntNode                                              \n"
		+"                                                                  \n"
		+"	class def create() : DummyNode                                  \n"
		+"		new                                                         \n"
		+"                                                                  \n"
		+"	def getNext() : IntNode = this                                  \n"
		+"	def setNext(nxt : IntNode) : Unit = ()                       	\n"
		+"	def getValue() : Int = 0	                                    \n"
		+"	def hasNext() : Bool = false                                    \n"
		+"                                                                  \n"
		+"class Node                                                        \n"
		+"	implements IntNode                                              \n"
		+"                                                                  \n"
		+"	var value : Int                                                 \n"
		+"	var nxt : IntNode                                              	\n"
		+"                                                                  \n"
		+"	class def create(value : Int, nxtNode : IntNode) : Node  		\n"
		+"		val n : Node = new                                          \n"
		+"		n.value = value												\n"
		+"		n.nxt = nxtNode												\n"
		+"		n															\n"
		+"                                                                  \n"
		+"	def getNext() : IntNode = this.nxt                             	\n"
		+"	def setNext(nxt : IntNode) : Unit                          		\n"
		+"		this.nxt = nxt                                            	\n"
		+"	def getValue() : Int = this.value                               \n"
		+"	def hasNext() : Bool = true                                     \n"
		+"                                                                  \n"
		+"class IntList                                                     \n"
		+"	                                                                \n"
		+"	var first : IntNode                                             \n"
		+"	var size : Int                                                  \n"
		+"                                                                  \n"
		+"	class def create() : IntList                                    \n"
		+"		val l : IntList = new                                       \n"
		+"		l.first = DummyNode.create()								\n"
		+"		l.size = 0													\n"
		+"		l															\n"
		+"                                                                  \n"
		+"	def insert(value : Int, index : Int) : Bool		                \n"
		+"		if this.size < index || index < 0                           \n"
		+"			then                                                    \n"
		+"				false				                                \n"
		+"			else                                                    \n"
		+"				var cur : IntNode = this.first                      \n"
		+"				var prev : IntNode = this.first                     \n"
		+"				var curIndex : Int = 0                              \n"
		+"				this.size = this.size + 1							\n"				
		+"				var node : Node				                    	\n"
		+"				if index == 0 				                        \n"
		+"					then                                            \n"
		+"						node = Node.create(value,this.first)		\n"
		+"						this.first = node                           \n"
		+"					else                                            \n"
		+"						while curIndex < index                      \n"
		+"							curIndex = curIndex + 1                 \n"
		+"							prev = cur                              \n"
		+"							cur = cur.getNext()                     \n"
		+"						node = Node.create(value,cur)				\n"
		+"						prev.setNext(node)                          \n"
		+"				true						 						\n"
		+"                                                                  \n"
		+"	def getSize() : Int                                             \n"
		+"		this.size                                                   \n"
		+"                                                                  \n"
		+"	def get(index : Int) : Int                                      \n"
		+"		if this.size < index || index < 0                           \n"
		+"			then                                                    \n"
		+"				0                                                   \n"
		+"			else                                                    \n"
		+"				var curIndex : Int = 0                              \n"
		+"				var cur : IntNode = this.first                      \n"
		+"				while curIndex < index                              \n"
		+"					curIndex = curIndex +1 							\n"
		+"					cur = cur.getNext()                             \n"
		+"				cur.getValue()                                      \n"
		+"																	\n"
		+"	def remove(index : Int)                                        	\n"
		+"		if this.size  < index || index < 0                         	\n"
		+"			then                                                   	\n"
		+"				()                                                 	\n"
		+"			else                                                   	\n"
		+"				this.size = this.size - 1                          	\n"
		+"				if index == 0                                      	\n"
		+"					then                                           	\n"
		+"						this.first = this.first.getNext()          	\n"
		+"					else                                           	\n"
		+"						var curIndex : Int = 0                     	\n"     
		+"						var cur : IntNode = this.first             	\n"
		+"						var prev : IntNode = this.first            	\n"
		+"						while curIndex < index                     	\n"
		+"							curIndex = curIndex +1                 	\n"
		+"							prev = cur 						       	\n"
		+"							cur = cur.getNext()                    	\n"
		+"						prev.setNext(cur.getNext())                	\n"
		+"																	\n";
	}
}
