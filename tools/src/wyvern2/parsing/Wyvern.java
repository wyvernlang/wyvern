/*
 * Built at Tue Mar 11 20:59:05 ADT 2014
 * by Copper version 0.7.1,
 *      revision 1cd57156c790d7c88540b5f453389b9ca39fae06,
 *      build 20131117-2243
 */
package wyvern2.parsing;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyvern.tools.typedAST.core.*;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.values.*;
import wyvern.tools.typedAST.extensions.*;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.abs.*;
import wyvern.tools.types.*;
import wyvern.tools.types.extensions.*;
import java.util.Arrays;




public class Wyvern extends edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine<java.lang.Object,edu.umn.cs.melt.copper.runtime.logging.CopperParserException>
{
    protected String formatError(String error)
    {
    	   String location = "";
        location += "line " + virtualLocation.getLine() + ", column " + virtualLocation.getColumn();
        if(currentState.pos.getFileName().length() > 40) location += "\n         ";
        location += " in file " + virtualLocation.getFileName();
        location += "\n         (parser state: " + currentState.statenum + "; real character index: " + currentState.pos.getPos() + ")";
        return "Error at " + location + ":\n  " + error;
    }
    protected void reportError(String message)
    throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        throw new edu.umn.cs.melt.copper.runtime.logging.CopperParserException(message);
    }
    protected void reportSyntaxError()
    throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
    java.util.ArrayList<String> expectedTerminalsReal = bitVecToRealStringList(getShiftableSets()[currentState.statenum]);
    java.util.ArrayList<String> expectedTerminalsDisplay = bitVecToDisplayStringList(getShiftableSets()[currentState.statenum]);
    java.util.ArrayList<String> matchedTerminalsReal = bitVecToRealStringList(disjointMatch.terms);
    java.util.ArrayList<String> matchedTerminalsDisplay = bitVecToDisplayStringList(disjointMatch.terms);
    throw new edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError(virtualLocation,currentState.pos,currentState.statenum,expectedTerminalsReal,expectedTerminalsDisplay,matchedTerminalsReal,matchedTerminalsDisplay);
    }
    public static enum Terminals implements edu.umn.cs.melt.copper.runtime.engines.CopperTerminalEnum
    {
        DedentRepair_t(1),
        Dedent_t(2),
        Indent_t(3),
        Newline_t(4),
        Spaces_t(5),
        arrow_t(6),
        classKwd_t(7),
        closeParen_t(8),
        colon_t(9),
        comma_t(10),
        comment_t(11),
        dash_t(12),
        decimalInteger_t(13),
        defKwd_t(14),
        divide_t(15),
        dot_t(16),
        dsl_t(17),
        equals_t(18),
        fnKwd_t(19),
        identifier_t(20),
        ignoredNewline(21),
        metadataKwd_t(22),
        mult_t(23),
        multi_comment_t(24),
        openParen_t(25),
        plus_t(26),
        shortString_t(27),
        tarrow_t(28),
        tilde_t(29),
        typeKwd_t(30),
        valKwd_t(31),
        varKwd_t(32);

        private final int num;
        Terminals(int num) { this.num = num; }
        public int num() { return num; }
    }

    public void pushToken(Terminals t,String lexeme)
    {
        java.util.BitSet ts = new java.util.BitSet();
        ts.set(t.num());
        tokenBuffer.offer(new edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData(ts,currentState.pos,currentState.pos,lexeme,new java.util.LinkedList<edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData>()));
    }
    public void setupEngine()
    {
    }
    public int transition(int state,char ch)
    {
         return delta[state][cmap[ch]];
    }
    public class Semantics extends edu.umn.cs.melt.copper.runtime.engines.single.semantics.SingleDFASemanticActionContainer<edu.umn.cs.melt.copper.runtime.logging.CopperParserException>
    {

        public Semantics()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            runInit();
        }

        public void error(edu.umn.cs.melt.copper.runtime.io.InputPosition pos,java.lang.String message)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            reportError("Error at " + pos.toString() + ":\n  " + message);
        }

        public void runDefaultTermAction()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
        }
        public void runDefaultProdAction()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
        }
        public void runInit()
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            
	parenLevel = 0;
	depths = new Stack<Integer>();
	depths.push(0);
	nlRegex = Pattern.compile("\n[\t ]*");
        }
        public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,java.lang.Object[] _children,int _prod)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            this._pos = _pos;
            this._children = _children;
            this._prod = _prod;
            this._specialAttributes = new edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes(virtualLocation);
            java.lang.Object RESULT = null;
            switch(_prod)
            {
            case 67:
                RESULT = runSemanticAction_67();
                break;
            case 68:
                RESULT = runSemanticAction_68();
                break;
            case 69:
                RESULT = runSemanticAction_69();
                break;
            case 70:
                RESULT = runSemanticAction_70();
                break;
            case 71:
                RESULT = runSemanticAction_71();
                break;
            case 72:
                RESULT = runSemanticAction_72();
                break;
            case 73:
                RESULT = runSemanticAction_73();
                break;
            case 74:
                RESULT = runSemanticAction_74();
                break;
            case 75:
                RESULT = runSemanticAction_75();
                break;
            case 76:
                RESULT = runSemanticAction_76();
                break;
            case 77:
                RESULT = runSemanticAction_77();
                break;
            case 78:
                RESULT = runSemanticAction_78();
                break;
            case 80:
                RESULT = runSemanticAction_80();
                break;
            case 81:
                RESULT = runSemanticAction_81();
                break;
            case 86:
                RESULT = runSemanticAction_86();
                break;
            case 87:
                RESULT = runSemanticAction_87();
                break;
            case 89:
                RESULT = runSemanticAction_89();
                break;
            case 90:
                RESULT = runSemanticAction_90();
                break;
            case 103:
                RESULT = runSemanticAction_103();
                break;
            case 104:
                RESULT = runSemanticAction_104();
                break;
            case 107:
                RESULT = runSemanticAction_107();
                break;
            case 108:
                RESULT = runSemanticAction_108();
                break;
            case 109:
                RESULT = runSemanticAction_109();
                break;
            case 110:
                RESULT = runSemanticAction_110();
                break;
            case 111:
                RESULT = runSemanticAction_111();
                break;
            case 113:
                RESULT = runSemanticAction_113();
                break;
            case 114:
                RESULT = runSemanticAction_114();
                break;
            case 115:
                RESULT = runSemanticAction_115();
                break;
            case 116:
                RESULT = runSemanticAction_116();
                break;
            case 117:
                RESULT = runSemanticAction_117();
                break;
            case 118:
                RESULT = runSemanticAction_118();
                break;
            case 119:
                RESULT = runSemanticAction_119();
                break;
            case 120:
                RESULT = runSemanticAction_120();
                break;
            case 121:
                RESULT = runSemanticAction_121();
                break;
            case 122:
                RESULT = runSemanticAction_122();
                break;
            case 123:
                RESULT = runSemanticAction_123();
                break;
            case 124:
                RESULT = runSemanticAction_124();
                break;
            case 125:
                RESULT = runSemanticAction_125();
                break;
            case 126:
                RESULT = runSemanticAction_126();
                break;
            case 132:
                RESULT = runSemanticAction_132();
                break;
            default:
        runDefaultProdAction();
                 break;
            }
            return RESULT;
        }
        public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData _terminal)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            this._pos = _pos;
            this._terminal = _terminal;
            this._specialAttributes = new edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes(virtualLocation);
            String lexeme = _terminal.lexeme;
            java.lang.Object RESULT = null;
            switch(_terminal.firstTerm)
            {
            case 1:
                RESULT = runSemanticAction_1(lexeme);
                break;
            case 2:
                RESULT = runSemanticAction_2(lexeme);
                break;
            case 3:
                RESULT = runSemanticAction_3(lexeme);
                break;
            case 8:
                RESULT = runSemanticAction_8(lexeme);
                break;
            case 13:
                RESULT = runSemanticAction_13(lexeme);
                break;
            case 17:
                RESULT = runSemanticAction_17(lexeme);
                break;
            case 20:
                RESULT = runSemanticAction_20(lexeme);
                break;
            case 25:
                RESULT = runSemanticAction_25(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
            return RESULT;
        }
        public java.lang.Object runSemanticAction_67()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_68()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_69()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object mer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = mer;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_70()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_71()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_72()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ter = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = ter;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_73()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ClassDeclaration((String)id, null, null,
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_74()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = new ClassDeclaration((String)id, null, null, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_75()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_76()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_77()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_78()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_80()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = aer; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_81()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object pi = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = pi; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_86()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object first = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_87()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object el = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject(new TypedAST[] {(TypedAST)el}); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_89()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_90()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_103()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_104()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object de = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = de; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_107()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_108()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_109()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_110()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_111()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_113()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new Variable(new NameBindingImpl((String)id, null), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_114()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object t = (java.lang.Object) _children[2];
            java.lang.Object inner = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_115()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_116()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object tgt = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_117()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object op = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)src,(String)op, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object lit = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DSLLit((String)lit); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new IntegerConstant((Integer)res); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = UnitVal.getInstance(null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Arrow((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Tuple((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_125()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new UnresolvedType((String)id); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_126()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ty = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ty; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_1(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
		pushToken(Terminals.Dedent_t,lexeme);
	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_2(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		depths.pop();
		if(newDepth < depths.peek()) {
			pushToken(Terminals.Dedent_t,output);
		}
	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_3(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		depths.push(newDepth);
	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_8(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
              parenLevel--; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_13(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
 		RESULT = Integer.parseInt(lexeme);
 	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_17(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = lexeme; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_20(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
 		RESULT = lexeme;
 	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_25(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
              parenLevel++; 
            return RESULT;
        }
        public int runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData match)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            String lexeme = match.lexeme;
            if(match.terms.equals(disambiguationGroups[0])) return disambiguate_0(lexeme);
            else if(match.terms.equals(disambiguationGroups[1])) return disambiguate_1(lexeme);
            else if(match.terms.equals(disambiguationGroups[2])) return disambiguate_2(lexeme);
            else if(match.terms.equals(disambiguationGroups[3])) return disambiguate_3(lexeme);
            else if(match.terms.equals(disambiguationGroups[4])) return disambiguate_4(lexeme);
            else if(match.terms.equals(disambiguationGroups[5])) return disambiguate_5(lexeme);
            else if(match.terms.equals(disambiguationGroups[6])) return disambiguate_6(lexeme);
            else return -1;
        }
        public int disambiguate_0(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		else
		{
			return Newline_t;
		}
	
        }
        public int disambiguate_1(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		//Given the lexeme of the terminals, need to treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth < depths.peek()){
			return Dedent_t;
		} else {
			return Newline_t;
		}
	
        }
        public int disambiguate_2(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Dedent_t;
	
        }
        public int disambiguate_3(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Indent_t;
	
        }
        public int disambiguate_4(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int DedentRepair_t = 1;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		//Given the lexeme of the terminals, need to treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth < depths.peek()) {
			return DedentRepair_t;
		} else {
			return Newline_t;
		}
	
        }
        public int disambiguate_5(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}

		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth > depths.peek()){
			return Indent_t;
		} else {
			return Newline_t;
		}
	
        }
        public int disambiguate_6(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 21;
            
		if (parenLevel > 0)
			return ignoredNewline;

		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth > depths.peek()){
			return Indent_t;
		} else if (newDepth < depths.peek()) {
			return Dedent_t;
		} else {
			return Newline_t;
		}
	
        }
    }
    public Semantics semantics;
    public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,java.lang.Object[] _children,int _prod)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        return semantics.runSemanticAction(_pos,_children,_prod);
    }
    public java.lang.Object runSemanticAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData _terminal)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        return semantics.runSemanticAction(_pos,_terminal);
    }
    public int runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData matches)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
        return semantics.runDisambiguationAction(_pos,matches);
    }
    public edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes getSpecialAttributes()
    {
        return semantics.getSpecialAttributes();
    }
    public void startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition initialPos)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
         super.startEngine(initialPos);
         semantics = new Semantics();
    }

public static final byte[] symbolNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\121\313\156\023\061" +
"\024\165\223\114\232\002\055\110\254\371\205\212\231\311\014\105" +
"\254\052\121\120\305\253\152\053\066\054\042\167\354\244\256\074" +
"\366\340\161\022\052\366\174\015\374\014\077\201\130\360\017\134" +
"\373\170\012\233\373\362\075\307\347\330\337\377\260\154\355\330" +
"\343\117\157\157\370\206\037\152\156\126\207\027\336\051\263\172" +
"\361\343\347\307\137\277\237\174\175\075\142\354\113\307\030\373" +
"\346\331\370\344\303\053\317\016\136\112\041\215\077\227\035\127" +
"\156\341\075\233\141\020\313\123\063\224\173\357\345\126\053\043" +
"\343\370\242\343\215\354\103\271\313\235\263\333\120\335\153\064" +
"\357\373\067\133\021\232\007\215\266\275\074\343\116\232\270\325" +
"\130\155\207\252\155\171\044\014\125\042\237\012\336\137\207\342" +
"\221\220\215\152\271\076\065\136\256\044\344\010\271\114\254\063" +
"\241\066\112\104\011\231\260\036\271\327\361\110\176\136\163\015" +
"\111\113\063\250\120\101\275\132\052\060\035\250\225\261\116\212" +
"\344\304\263\375\126\172\056\270\347\151\177\332\256\165\144\175" +
"\030\012\265\370\117\341\175\333\111\163\347\147\332\351\165\274" +
"\153\277\277\266\316\343\211\243\014\177\367\036\273\136\151\150" +
"\335\363\267\235\034\054\154\270\376\127\272\124\146\027\227\307" +
"\347\227\236\215\216\117\050\274\243\220\305\327\364\154\107\304" +
"\027\150\364\225\025\267\364\145\364\030\064\044\355\243\145\103" +
"\355\262\051\210\137\231\340\107\253\160\253\352\270\343\055\041" +
"\107\241\235\015\016\151\327\070\342\312\354\325\115\103\171\102" +
"\071\265\052\345\160\074\263\101\053\357\211\173\247\013\076\023" +
"\331\270\013\247\243\020\046\076\152\230\170\351\132\002\372\165" +
"\247\145\150\011\027\114\017\360\054\124\042\115\310\000\161\220" +
"\365\030\035\021\107\303\147\204\133\164\363\071\122\205\124\043" +
"\075\103\072\102\172\036\123\136\042\021\140\114\303\030\201\312" +
"\261\236\143\275\110\214\145\334\310\143\174\032\107\065\330\213" +
"\002\011\174\025\330\153\254\314\213\270\137\307\010\332\062\255" +
"\047\060\206\005\356\052\301\121\342\312\022\162\012\060\226\140" +
"\054\163\210\113\122\301\214\133\000\056\260\127\100\351\121\214" +
"\311\061\116\362\304\000\035\163\164\025\316\252\324\341\254\112" +
"\226\040\247\302\225\025\204\127\020\136\101\170\015\134\015\134" +
"\015\134\015\134\015\134\231\054\102\313\074\271\001\131\231\136" +
"\034\147\105\365\027\243\037\362\330\173\004\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\120\113\156\024\061" +
"\024\364\174\063\001\022\220\130\263\143\035\321\335\323\115\020" +
"\053\044\020\212\370\010\021\211\015\122\106\116\333\063\161\344" +
"\266\035\267\047\103\304\236\323\300\145\270\004\142\301\035\170" +
"\166\271\003\233\367\257\162\225\277\377\141\263\255\147\017\077" +
"\277\275\344\327\374\110\163\263\071\072\015\136\231\315\363\037" +
"\077\077\375\372\375\350\353\353\061\143\137\034\143\354\133\140" +
"\243\307\201\035\276\224\102\232\360\121\072\256\374\052\004\266" +
"\300\040\225\047\146\050\367\337\313\235\126\106\246\361\251\343" +
"\255\354\143\271\307\275\267\273\130\335\151\065\357\373\067\073" +
"\021\233\173\255\266\275\374\300\275\064\351\252\265\332\016\125" +
"\327\361\104\030\253\114\076\027\274\277\210\305\003\041\133\325" +
"\161\175\142\202\334\110\310\021\162\235\131\027\102\135\053\221" +
"\044\314\204\015\310\275\116\053\171\265\345\032\222\326\146\120" +
"\241\242\172\265\126\140\072\124\033\143\275\024\331\111\140\007" +
"\235\014\134\360\300\363\375\274\333\352\304\172\077\026\152\365" +
"\237\302\273\326\111\163\353\147\356\364\066\275\165\320\137\130" +
"\037\360\301\111\106\270\375\217\275\240\064\264\356\207\033\047" +
"\007\013\327\134\377\053\175\056\107\147\201\215\137\274\242\360" +
"\216\302\054\375\044\115\105\162\337\352\163\053\156\002\233\320" +
"\107\320\220\164\217\327\055\265\353\266\044\156\145\242\027\255" +
"\342\213\312\161\317\073\102\216\143\273\030\334\321\255\361\304" +
"\065\263\347\227\055\345\051\345\334\252\234\343\172\141\243\116" +
"\336\023\367\310\105\217\231\154\342\342\166\034\303\064\044\015" +
"\323\040\175\107\300\260\165\132\306\226\160\321\360\000\237\305" +
"\112\344\011\031\040\016\262\235\242\047\246\063\162\073\135\271" +
"\345\022\251\106\152\220\236\042\035\043\075\113\251\250\220\010" +
"\060\241\141\212\100\025\070\057\160\136\146\306\052\135\024\051" +
"\076\111\243\006\354\145\211\004\276\032\354\015\116\226\145\272" +
"\157\122\004\155\225\317\063\030\303\022\157\125\340\250\360\144" +
"\005\071\045\030\053\060\126\005\304\145\251\140\306\053\000\227" +
"\270\053\241\364\070\305\354\030\233\042\063\100\307\022\135\215" +
"\135\235\073\354\352\154\011\162\152\074\131\103\170\015\341\065" +
"\204\067\300\065\300\065\300\065\300\065\300\125\331\042\264\054" +
"\263\033\220\125\371\307\261\053\353\277\360\062\246\067\161\004" +
"\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\332\030\006\001\120\030\004\330\201\201\201" +
"\011\210\231\241\230\021\215\315\012\225\147\204\142\230\132\126" +
"\044\061\030\146\101\222\107\066\207\031\115\234\001\115\037\272" +
"\070\003\232\034\272\373\220\355\003\321\354\130\354\300\246\027" +
"\046\207\256\016\346\107\220\171\054\000\012\016\320\050\063\002" +
"\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\175\303\071\016\202\000" +
"\024\004\320\011\211\367\160\305\015\101\005\305\005\134\022\032" +
"\012\117\140\343\021\214\211\206\013\321\121\121\172\044\023\357" +
"\300\024\123\374\130\370\222\127\175\321\172\076\340\134\363\313" +
"\373\346\276\076\165\351\000\305\035\100\326\006\072\334\065\173" +
"\146\137\007\352\352\220\107\074\346\011\117\331\323\231\372\034" +
"\350\234\027\346\322\014\315\110\127\272\326\130\067\346\226\167" +
"\177\356\065\371\231\362\301\074\352\211\317\015\350\300\006\304" +
"\053\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\235\111\223\335\064" +
"\020\200\273\262\260\357\003\204\260\066\133\140\330\367\065\200" +
"\011\044\303\004\046\023\366\145\200\014\204\035\022\046\201\204" +
"\041\201\141\077\160\342\127\160\343\304\221\337\303\211\052\316" +
"\134\221\336\130\025\251\137\113\266\374\254\305\117\363\125\251" +
"\132\356\156\311\252\056\331\117\226\045\277\077\376\205\255\307" +
"\216\302\346\245\245\371\155\377\375\275\270\375\267\137\167\156" +
"\002\130\135\001\200\065\241\337\264\064\277\360\327\362\216\343" +
"\377\374\371\273\122\357\202\015\106\034\073\002\153\040\242\342" +
"\216\010\302\310\207\323\157\026\151\213\166\274\265\226\247\211" +
"\164\072\343\177\006\243\073\123\244\263\104\072\273\076\076\107" +
"\244\163\105\072\117\244\363\153\335\005\042\135\050\322\105\365" +
"\361\014\251\343\142\113\373\056\251\345\245\042\155\253\363\227" +
"\061\176\333\105\272\134\244\053\332\105\244\013\242\366\053\373" +
"\256\063\006\247\042\122\001\174\045\065\124\052\134\166\231\247" +
"\376\266\262\056\277\034\010\332\107\256\352\273\316\030\030\175" +
"\344\105\251\241\122\341\262\313\074\365\267\225\165\371\345\100" +
"\320\076\162\165\337\165\306\300\350\043\107\244\206\112\205\313" +
"\056\363\324\337\126\326\345\227\003\223\365\021\024\277\276\344" +
"\330\373\327\227\361\031\375\372\062\372\153\264\374\014\265\267" +
"\005\231\137\137\235\240\127\015\366\135\147\014\202\106\344\332" +
"\276\353\214\101\320\210\134\327\167\235\061\060\356\254\373\245" +
"\206\112\005\325\043\300\365\135\316\050\312\335\320\251\251\221" +
"\060\042\262\133\152\250\124\270\354\010\160\243\353\054\302\276" +
"\203\326\223\053\366\253\246\002\070\024\277\075\351\061\372\310" +
"\107\122\243\144\251\070\373\310\347\361\333\223\036\243\217\174" +
"\050\065\112\226\312\251\210\054\247\156\112\046\030\175\344\270" +
"\324\120\251\160\331\145\236\372\333\312\272\374\162\300\210\310" +
"\163\122\243\144\251\030\021\171\076\161\143\262\300\210\310\263" +
"\122\243\144\251\330\177\175\021\340\246\370\355\111\217\321\107" +
"\236\221\032\052\025\056\273\314\043\300\315\266\263\050\137\341" +
"\063\113\353\115\201\150\307\055\066\233\163\204\166\070\140\243" +
"\262\305\171\325\334\032\277\075\351\161\106\344\266\370\355\111" +
"\017\037\021\004\270\275\002\330\233\244\111\211\011\067\207\306" +
"\201\000\167\304\070\017\071\347\235\076\376\176\021\021\265\337" +
"\325\241\121\331\202\000\167\123\135\373\210\210\322\367\004\150" +
"\124\166\130\357\043\367\126\000\077\045\151\122\142\370\210\124" +
"\000\037\160\336\330\342\252\261\225\215\015\002\334\327\302\347" +
"\176\252\053\376\076\362\000\325\345\367\226\263\366\173\260\301" +
"\236\351\133\116\014\027\221\207\032\354\205\105\244\305\171\047" +
"\211\310\303\056\173\221\021\171\304\145\037\326\132\105\255\114" +
"\333\265\212\217\326\162\047\346\260\126\361\261\276\353\214\201" +
"\327\230\365\161\106\027\163\105\315\023\132\176\246\226\125\123" +
"\175\114\075\055\357\254\242\352\057\245\206\112\205\313\056\363" +
"\324\337\126\326\345\227\003\123\177\037\171\262\226\075\257\171" +
"\306\341\106\144\127\055\047\210\110\005\360\066\123\342\051\356" +
"\214\265\155\213\315\326\026\324\042\142\261\077\335\242\216\335" +
"\042\355\021\151\016\073\314\156\213\062\363\042\355\365\272\263" +
"\172\275\307\301\300\357\006\105\375\013\041\352\055\376\111\157" +
"\037\325\005\035\217\054\062\272\375\343\236\151\100\313\133\335" +
"\346\210\210\222\057\004\152\124\226\030\343\221\057\244\206\112" +
"\205\313\056\363\324\337\126\326\345\227\003\223\137\065\330\260" +
"\027\102\330\137\352\132\167\313\363\277\354\341\373\012\243\173" +
"\125\077\266\316\263\276\326\251\171\353\145\137\357\132\326\122" +
"\337\033\175\326\327\204\165\236\365\375\256\065\116\122\066\007" +
"\006\321\107\226\372\254\257\011\343\316\072\032\027\122\251\160" +
"\331\321\157\315\163\343\370\063\045\106\104\106\043\165\052\025" +
"\056\073\372\105\304\372\104\220\003\106\104\346\244\206\112\205" +
"\313\136\255\077\113\270\126\324\214\174\161\175\105\315\234\315" +
"\057\026\330\152\105\115\045\236\222\244\206\112\205\313\136\255" +
"\077\141\271\042\262\247\156\311\054\255\067\005\330\066\042\237" +
"\111\215\222\245\262\261\126\221\142\104\344\230\324\120\251\160" +
"\331\145\236\372\333\312\272\374\162\300\210\310\327\122\103\245" +
"\302\145\227\171\352\157\053\353\362\313\201\366\317\065\125\075" +
"\376\107\200\067\003\067\052\051\136\163\150\157\205\157\117\172" +
"\214\253\146\064\372\256\172\036\205\017\015\043\042\243\147\314" +
"\052\362\263\146\156\364\062\077\062\361\134\174\213\163\214\275" +
"\037\010\005\373\166\342\035\135\226\206\163\315\363\201\370\355" +
"\111\317\170\104\020\140\071\131\163\062\200\275\152\016\112\211" +
"\033\127\115\115\125\310\270\303\006\033\221\003\272\054\015\066" +
"\042\313\272\054\015\277\361\110\005\360\111\330\366\244\247\370" +
"\067\341\357\122\235\327\223\336\301\000\215\312\216\101\274\257" +
"\171\257\317\372\232\140\357\254\337\252\244\216\071\151\203\263" +
"\267\325\345\100\321\353\107\330\267\261\154\037\131\123\111\035" +
"\163\322\006\147\107\146\317\015\146\372\235\223\350\073\027\263" +
"\377\042\120\320\253\346\343\276\353\214\101\377\021\301\201\217" +
"\342\330\373\310\017\351\332\223\036\066\042\337\113\211\000\237" +
"\246\151\123\132\330\031\243\215\367\276\215\367\021\034\356\272" +
"\370\103\265\214\364\165\164\214\273\277\346\260\226\217\270\163" +
"\261\002\370\131\227\245\301\106\344\027\135\226\306\240\256\032" +
"\156\127\232\367\267\037\321\147\127\332\252\324\120\251\160\331" +
"\145\236\372\333\312\272\374\162\300\371\145\247\250\353\110\163" +
"\141\352\366\140\255\140\260\075\130\225\360\360\255\165\032\140" +
"\373\210\327\054\036\146\322\107\064\137\331\107\274\377\267\000" +
"\275\367\351\125\365\256\015\121\352\250\357\331\206\204\367\373" +
"\232\101\316\171\370\300\136\065\213\272\054\015\257\367\065\235" +
"\167\223\341\306\074\053\014\142\056\236\375\176\101\321\021\141" +
"\327\136\347\367\134\323\362\274\223\314\006\070\277\274\135\304" +
"\167\025\275\376\233\055\277\076\202\366\147\337\125\055\237\351" +
"\267\256\352\063\014\175\277\257\261\163\301\371\134\363\243\167" +
"\363\246\000\166\056\376\104\272\366\244\207\035\263\056\350\262" +
"\064\274\236\364\306\256\301\151\204\355\043\123\275\243\250\011" +
"\066\042\143\253\325\112\042\350\050\076\364\167\214\116\206\250" +
"\227\355\043\337\250\244\216\071\151\203\263\267\325\345\000\033" +
"\221\223\052\251\143\116\332\340\354\155\165\071\300\106\344\204" +
"\112\352\230\223\066\070\073\116\345\374\010\066\364\162\164\174" +
"\067\100\363\231\155\333\262\220\140\247\377\046\261\324\064\155" +
"\253\300\307\326\324\262\127\115\347\365\316\323\000\033\221\357" +
"\322\265\047\075\154\104\366\351\262\064\274\236\153\202\316\163" +
"\344\202\261\132\142\364\256\216\112\205\313\056\363\324\337\126" +
"\326\345\227\003\354\125\263\242\313\322\370\037\265\024\360\120" +
"\005\207\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\305\225\277\112\303\120" +
"\024\306\317\315\037\274\026\012\126\047\007\341\006\042\304\245" +
"\164\210\240\161\023\207\042\156\216\131\132\101\060\242\022\233" +
"\033\311\040\001\067\047\027\147\041\205\156\272\070\370\004\076" +
"\200\117\342\043\010\132\250\213\334\057\160\102\300\016\355\360" +
"\353\071\347\336\163\316\367\335\227\117\162\363\011\255\305\107" +
"\347\343\233\161\077\327\311\105\177\077\321\307\247\172\357\162" +
"\075\160\076\036\016\122\213\250\110\211\250\314\046\264\362\367" +
"\137\127\137\367\345\156\370\354\331\044\142\162\116\022\235\151" +
"\262\342\303\042\375\111\072\377\125\316\333\352\153\376\370\233" +
"\103\320\342\123\144\327\124\222\225\317\277\335\005\020\357\321" +
"\131\145\002\044\006\122\031\301\322\364\151\373\337\042\354\050" +
"\060\107\300\124\174\340\252\015\163\161\267\102\367\120\162\200" +
"\300\146\173\021\036\263\273\354\124\374\343\326\025\207\027\344" +
"\203\035\363\062\050\064\132\325\101\140\031\001\120\103\370\235" +
"\200\127\003\256\150\173\240\201\152\355\150\213\047\065\130\003" +
"\203\073\065\234\201\123\301\266\243\046\102\325\052\071\142\156" +
"\342\150\010\122\371\022\214\226\015\240\063\100\000\217\333\110" +
"\316\146\320\244\106\227\057\034\346\251\204\102\363\120\062\144" +
"\073\203\317\116\305\217\100\000\232\014\333\175\320\136\365\146" +
"\335\133\236\076\160\004\064\113\050\065\037\153\260\245\266\143" +
"\043\303\017\075\173\120\060\025\373\361\252\161\270\232\010\324" +
"\053\050\065\270\076\340\202\275\012\354\025\334\022\014\340\123" +
"\204\326\007\017\012\372\156\233\162\306\266\004\123\001\300\126" +
"\024\037\330\036\367\035\154\160\017\166\257\352\144\020\026\337" +
"\060\010\205\023\256\014\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\135\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\012\223\140\124\340\120\030\225\030\225\030\225\030\225\030" +
"\225\030\225\030\225\030\225\030\225\030\225\030\225\030\225\000" +
"\113\000\000\004\336\022\063\256\014\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\135\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\072\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\201\041\001\000\250\226\165" +
"\327\256\014\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\044\072\332\047\053\261\054\121\257\264\044\063\107\317" +
"\051\263\044\070\265\304\132\362\322\273\215\346\317\356\030\061" +
"\061\060\124\024\060\060\060\324\001\025\012\143\121\227\053\251" +
"\301\162\276\317\245\000\246\116\261\200\020\050\055\144\250\143" +
"\140\032\125\072\252\164\124\351\250\322\121\245\243\112\107\225" +
"\216\052\035\125\072\252\164\124\351\250\322\121\245\243\112\107" +
"\225\216\052\035\125\072\252\164\124\351\250\322\141\247\024\000" +
"\130\272\065\261\165\025\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\024\031\006\001\000\000\253\241\253\202\237" +
"\000\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\020\276\376\377\377\337\012\000\267\045\066\267\121\000\000\000" +
""
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\144\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\330" +
"\004\030\101\202\014\002\044\113\050\060\310\141\227\150\300\245" +
"\203\201\011\207\004\043\116\035\012\124\163\056\013\321\101\001" +
"\064\203\064\263\131\160\031\305\204\333\016\006\005\034\022\054" +
"\104\073\023\127\330\000\051\254\022\012\270\215\152\040\154\051" +
"\325\042\202\364\004\205\123\202\212\311\203\201\301\201\264\230" +
"\022\040\075\110\260\030\105\164\144\163\220\021\103\104\032\316" +
"\101\143\263\040\076\360\240\132\172\150\300\141\007\043\031\251" +
"\224\301\001\207\004\023\235\143\142\140\242\146\040\023\004\256" +
"\070\161\030\362\341\007\112\243\130\045\100\104\005\000\007\166" +
"\346\020\001\010\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\144\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\243" +
"\002\243\002\243\002\124\026\140\004\011\062\010\120\125\053\361" +
"\146\122\140\373\250\300\260\013\133\074\056\006\000\017\376\374" +
"\373\171\006\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\335\222\261\152\302\120" +
"\024\206\377\173\275\202\143\320\311\245\134\067\047\047\027\351" +
"\022\304\111\272\165\151\311\244\320\041\105\045\065\067\222\111" +
"\062\072\165\351\143\264\113\207\076\107\237\244\217\120\143\042" +
"\164\221\363\027\002\031\124\102\162\011\137\356\311\075\347\377" +
"\076\176\320\114\326\350\004\167\317\263\315\154\220\270\160\061" +
"\030\207\356\376\311\335\056\273\175\363\375\072\211\064\220\106" +
"\000\036\343\065\274\323\257\126\277\273\355\150\370\336\153\100" +
"\005\060\363\320\305\016\072\230\246\121\121\264\134\255\371\152" +
"\177\046\157\177\065\220\306\057\330\102\047\345\263\131\274\253" +
"\362\316\363\174\057\001\300\103\306\200\130\012\260\270\221\101" +
"\306\166\100\023\240\350\016\053\367\221\261\123\371\364\270\006" +
"\076\371\007\254\010\274\342\042\043\361\145\140\310\330\213\276" +
"\151\203\344\347\145\061\031\350\212\263\072\056\042\260\354\124" +
"\012\073\031\264\330\016\012\360\300\000\015\212\232\110\205\243" +
"\240\106\175\120\135\037\332\040\321\207\313\100\063\347\062\264" +
"\130\264\125\023\274\060\360\237\160\025\275\142\226\050\352\056" +
"\327\207\146\136\137\202\065\202\363\214\366\074\001\365\212\312" +
"\160\365\143\247\043\071\166\176\000\076\031\277\064\011\011\000" +
"\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\321\113\112\003\101" +
"\020\006\340\166\064\032\337\232\030\337\032\237\061\127\161\341" +
"\011\334\170\204\040\050\136\310\235\053\227\036\111\360\016\326" +
"\100\011\263\120\031\305\225\174\015\037\377\124\367\164\165\103" +
"\077\275\225\316\335\155\251\256\057\257\136\156\106\367\257\317" +
"\217\125\051\017\223\062\125\352\161\321\102\077\114\067\352\363" +
"\226\373\332\366\256\363\360\223\265\203\320\013\073\141\046\254" +
"\207\101\130\011\263\141\057\154\207\271\157\014\123\247\321\167" +
"\051\234\205\121\326\233\277\264\366\303\377\353\263\226\033\367" +
"\370\230\073\315\357\372\105\126\303\142\070\371\242\107\225\171" +
"\224\171\234\271\021\026\302\174\326\335\306\236\375\314\255\074" +
"\157\034\166\377\360\015\001\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000" +
"\000\000\000\370\147\336\001\334\232\172\061\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\230\113\123\023\101" +
"\020\307\247\002\101\101\100\024\020\102\170\210\050\050\104\015" +
"\357\367\103\120\061\012\004\224\227\225\213\037\101\255\302\362" +
"\304\235\023\237\202\033\047\216\174\036\116\124\161\346\112\047" +
"\065\113\115\166\147\206\231\315\046\351\016\333\125\277\332\316" +
"\114\117\347\137\135\223\354\364\234\136\261\350\301\137\126\225" +
"\311\244\332\256\057\322\261\343\243\271\010\143\377\177\063\306" +
"\176\302\170\044\223\132\077\377\325\377\357\362\354\304\031\176" +
"\303\010\330\301\037\166\310\100\361\255\332\254\137\005\124\003" +
"\121\240\006\170\000\074\004\152\201\072\076\237\345\021\120\017" +
"\064\000\215\174\354\061\320\004\074\341\237\237\362\147\063\320" +
"\002\264\002\317\200\066\076\336\016\304\170\176\047\157\007\177" +
"\336\255\066\016\164\112\002\343\022\144\363\356\061\331\323\075" +
"\157\222\137\255\126\026\211\123\255\143\335\262\150\013\353\052" +
"\160\275\324\224\152\121\132\250\266\170\146\254\266\047\200\057" +
"\323\345\060\312\217\366\077\341\071\072\265\275\026\371\345\152" +
"\061\331\013\367\200\122\155\137\151\004\331\031\352\332\276\164" +
"\017\370\336\267\257\024\363\356\061\331\323\075\057\313\117\157" +
"\337\172\054\124\233\263\376\200\363\345\214\170\155\007\070\242" +
"\137\156\136\073\076\361\332\242\266\012\122\073\130\304\057\366" +
"\125\035\217\332\041\116\102\360\165\274\065\214\123\361\316\046" +
"\336\243\366\075\047\051\370\072\206\015\343\164\214\230\306\242" +
"\072\215\217\152\362\347\014\225\132\272\267\037\143\106\152\035" +
"\243\165\373\121\250\332\361\002\327\113\255\002\072\364\011\111" +
"\240\237\175\073\051\314\061\311\074\375\137\331\224\225\132\324" +
"\026\252\055\236\241\333\267\131\246\025\032\250\167\221\264\372" +
"\062\314\152\147\074\152\147\071\242\137\116\362\164\170\324\316" +
"\161\346\005\277\234\054\000\213\316\147\342\235\116\302\222\045" +
"\037\153\104\076\330\304\173\324\056\163\022\202\257\143\105\360" +
"\077\032\256\021\371\144\023\117\374\135\106\253\347\115\132\362" +
"\331\307\032\067\253\246\261\036\265\137\070\111\301\327\221\022" +
"\374\257\206\153\334\174\063\215\045\276\157\061\234\301\202\355" +
"\164\326\320\250\165\214\106\207\136\171\073\201\236\332\165\122" +
"\152\161\324\026\163\357\340\355\164\244\147\366\062\222\247\203" +
"\170\357\020\252\055\215\332\015\364\152\323\234\204\340\353\330" +
"\064\214\123\261\145\023\117\374\304\110\253\103\247\325\363\252" +
"\152\373\075\200\332\312\162\024\126\133\132\073\341\007\047\051" +
"\370\072\266\015\343\164\354\230\306\022\377\117\240\165\373\101" +
"\353\076\101\125\333\335\000\152\253\312\341\277\266\264\166\202" +
"\111\247\263\047\211\011\373\262\273\325\226\352\174\273\157\030" +
"\247\077\061\322\272\311\167\316\222\064\316\267\264\152\113\353" +
"\124\103\353\046\237\326\031\214\126\155\053\357\315\173\377\336" +
"\145\276\156\077\156\000\166\276\057\176\247\074\000\000"
});

public static void initArrays()
throws java.io.IOException,java.lang.ClassNotFoundException
{
    symbolNames = (String[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(symbolNamesHash);
    symbolDisplayNames = (String[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(symbolDisplayNamesHash);
    symbolNumbers = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(symbolNumbersHash);
    productionLHSs = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(productionLHSsHash);
    parseTable = (int[][]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(parseTableHash);
    shiftableSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(shiftableSetsHash);
    layoutSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(layoutSetsHash);
    prefixSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(prefixSetsHash);
    prefixMaps = (java.util.BitSet[][]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(prefixMapsHash);
    terminalUses = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(terminalUsesHash);
    shiftableUnion = (java.util.BitSet) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(shiftableUnionHash);
    acceptSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(acceptSetsHash);
    rejectSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(rejectSetsHash);
    possibleSets = (java.util.BitSet[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(possibleSetsHash);
    cmap = (int[]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(cMapHash);
    delta = (int[][]) edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.readHash(deltaHash);
    }
public Wyvern() {}

		private static int TERMINAL_COUNT;
		private static int GRAMMAR_SYMBOL_COUNT;
		private static int SYMBOL_COUNT;
		private static int PARSER_STATE_COUNT;
		private static int SCANNER_STATE_COUNT;
		private static int DISAMBIG_GROUP_COUNT;
		
		private static int SCANNER_START_STATENUM;
		private static int PARSER_START_STATENUM;
		private static int EOF_SYMNUM;
		private static int EPS_SYMNUM;
		
		private static String[] symbolNames;
		private static String[] symbolDisplayNames;
		private static int[] symbolNumbers;
		private static int[] productionLHSs;
		
		private static int[][] parseTable;
		private static java.util.BitSet[] shiftableSets;
		private static java.util.BitSet[] layoutSets;
		private static java.util.BitSet[] prefixSets;
		private static java.util.BitSet[][] prefixMaps;
		private static int[] terminalUses;
		
		private static java.util.BitSet[] disambiguationGroups;
		
		private static java.util.BitSet shiftableUnion;
		
		private static java.util.BitSet[] acceptSets,rejectSets,possibleSets;
		
		private static int[][] delta;
		private static int[] cmap;
		
		public int getTERMINAL_COUNT() {
			return TERMINAL_COUNT;
		}
		public int getGRAMMAR_SYMBOL_COUNT() {
			return GRAMMAR_SYMBOL_COUNT;
		}
		public int getSYMBOL_COUNT() {
			return SYMBOL_COUNT;
		}
		public int getPARSER_STATE_COUNT() {
			return PARSER_STATE_COUNT;
		}
		public int getSCANNER_STATE_COUNT() {
			return SCANNER_STATE_COUNT;
		}
		public int getDISAMBIG_GROUP_COUNT() {
			return DISAMBIG_GROUP_COUNT;
		}
		public int getSCANNER_START_STATENUM() {
			return SCANNER_START_STATENUM;
		}
		public int getPARSER_START_STATENUM() {
			return PARSER_START_STATENUM;
		}
		public int getEOF_SYMNUM() {
			return EOF_SYMNUM;
		}
		public int getEPS_SYMNUM() {
			return EPS_SYMNUM;
		}
		public String[] getSymbolNames() {
			return symbolNames;
		}
		public String[] getSymbolDisplayNames() {
			return symbolDisplayNames;
		}
		public int[] getSymbolNumbers() {
			return symbolNumbers;
		}
		public int[] getProductionLHSs() {
			return productionLHSs;
		}
		public int[][] getParseTable() {
			return parseTable;
		}
		public java.util.BitSet[] getShiftableSets() {
			return shiftableSets;
		}
		public java.util.BitSet[] getLayoutSets() {
			return layoutSets;
		}
		public java.util.BitSet[] getPrefixSets() {
			return prefixSets;
		}
		public java.util.BitSet[][] getLayoutMaps() {
			return null;
		}
		public java.util.BitSet[][] getPrefixMaps() {
			return prefixMaps;
		}
		public int[] getTerminalUses() {
			return terminalUses;
		}
		public java.util.BitSet[] getDisambiguationGroups() {
			return disambiguationGroups;
		}
		public java.util.BitSet getShiftableUnion() {
			return shiftableUnion;
		}
		public java.util.BitSet[] getAcceptSets() {
			return acceptSets;
		}
		public java.util.BitSet[] getRejectSets() {
			return rejectSets;
		}
		public java.util.BitSet[] getPossibleSets() {
			return possibleSets;
		}
		public int[][] getDelta() {
			return delta;
		}
		public int[] getCmap() {
			return cmap;
		}	
    public java.lang.Object parse(java.io.Reader input,String inputName)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
    this.charBuffer = edu.umn.cs.melt.copper.runtime.io.ScannerBuffer.instantiate(input);
    setupEngine();
    startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition.initialPos(inputName));
    java.lang.Object parseTree = (java.lang.Object) runEngine();
    return parseTree;
    }


	Integer parenLevel;
	Stack<Integer> depths;
	Pattern nlRegex;


    static
    {
        TERMINAL_COUNT = 33;
        GRAMMAR_SYMBOL_COUNT = 66;
        SYMBOL_COUNT = 134;
        PARSER_STATE_COUNT = 126;
        SCANNER_STATE_COUNT = 89;
        DISAMBIG_GROUP_COUNT = 7;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { ex.printStackTrace(); System.exit(1); }
        catch(java.lang.ClassNotFoundException ex) { ex.printStackTrace(); System.exit(1); }
        disambiguationGroups = new java.util.BitSet[7];
        disambiguationGroups[0] = newBitVec(33,4,21);
        disambiguationGroups[1] = newBitVec(33,2,4,21);
        disambiguationGroups[2] = newBitVec(33,2,21);
        disambiguationGroups[3] = newBitVec(33,3,21);
        disambiguationGroups[4] = newBitVec(33,1,4,21);
        disambiguationGroups[5] = newBitVec(33,3,4,21);
        disambiguationGroups[6] = newBitVec(33,2,3,4,21);
    }

}
