/*
 * Built at Fri Mar 14 17:18:20 ADT 2014
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
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;




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
        newKwd_t(25),
        openParen_t(26),
        plus_t(27),
        shortString_t(28),
        tarrow_t(29),
        tilde_t(30),
        typeKwd_t(31),
        valKwd_t(32),
        varKwd_t(33);

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
            case 79:
                RESULT = runSemanticAction_79();
                break;
            case 80:
                RESULT = runSemanticAction_80();
                break;
            case 81:
                RESULT = runSemanticAction_81();
                break;
            case 82:
                RESULT = runSemanticAction_82();
                break;
            case 83:
                RESULT = runSemanticAction_83();
                break;
            case 84:
                RESULT = runSemanticAction_84();
                break;
            case 87:
                RESULT = runSemanticAction_87();
                break;
            case 88:
                RESULT = runSemanticAction_88();
                break;
            case 89:
                RESULT = runSemanticAction_89();
                break;
            case 90:
                RESULT = runSemanticAction_90();
                break;
            case 91:
                RESULT = runSemanticAction_91();
                break;
            case 92:
                RESULT = runSemanticAction_92();
                break;
            case 93:
                RESULT = runSemanticAction_93();
                break;
            case 94:
                RESULT = runSemanticAction_94();
                break;
            case 95:
                RESULT = runSemanticAction_95();
                break;
            case 96:
                RESULT = runSemanticAction_96();
                break;
            case 97:
                RESULT = runSemanticAction_97();
                break;
            case 98:
                RESULT = runSemanticAction_98();
                break;
            case 99:
                RESULT = runSemanticAction_99();
                break;
            case 100:
                RESULT = runSemanticAction_100();
                break;
            case 101:
                RESULT = runSemanticAction_101();
                break;
            case 102:
                RESULT = runSemanticAction_102();
                break;
            case 103:
                RESULT = runSemanticAction_103();
                break;
            case 104:
                RESULT = runSemanticAction_104();
                break;
            case 105:
                RESULT = runSemanticAction_105();
                break;
            case 106:
                RESULT = runSemanticAction_106();
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
            case 112:
                RESULT = runSemanticAction_112();
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
            case 127:
                RESULT = runSemanticAction_127();
                break;
            case 128:
                RESULT = runSemanticAction_128();
                break;
            case 129:
                RESULT = runSemanticAction_129();
                break;
            case 130:
                RESULT = runSemanticAction_130();
                break;
            case 131:
                RESULT = runSemanticAction_131();
                break;
            case 132:
                RESULT = runSemanticAction_132();
                break;
            case 133:
                RESULT = runSemanticAction_133();
                break;
            case 134:
                RESULT = runSemanticAction_134();
                break;
            case 135:
                RESULT = runSemanticAction_135();
                break;
            case 136:
                RESULT = runSemanticAction_136();
                break;
            case 137:
                RESULT = runSemanticAction_137();
                break;
            case 138:
                RESULT = runSemanticAction_138();
                break;
            case 139:
                RESULT = runSemanticAction_139();
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
            case 26:
                RESULT = runSemanticAction_26(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
            return RESULT;
        }
        public java.lang.Object runSemanticAction_69()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_70()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_71()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object mer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = mer;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_72()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_73()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_74()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ter = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = ter;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_75()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ClassDeclaration((String)id, null, null,
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_76()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = new ClassDeclaration((String)id, null, null, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_77()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object after = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_78()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_79()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_80()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_81()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_82()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object fullType = (java.lang.Object) _children[3];
            java.lang.Object body = (java.lang.Object) _children[4];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, false, null);
            return RESULT;
        }
        public java.lang.Object runSemanticAction_83()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = aer; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_84()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object pi = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = pi; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_87()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object re = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             ((LinkedList<NameBinding>)re).addFirst(new NameBindingImpl((String)id, (Type)ta)); RESULT = re; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_88()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             LinkedList<NameBinding> llnb = new LinkedList<NameBinding>(); llnb.add(new NameBindingImpl((String)id, (Type)ta)); RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_89()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object first = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_90()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object el = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject(new TypedAST[] {(TypedAST)el}); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_91()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_92()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_93()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_94()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[2];
            java.lang.Object argNames = (java.lang.Object) _children[3];
            java.lang.Object fullType = (java.lang.Object) _children[4];
            java.lang.Object body = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, true, null);
            return RESULT;
        }
        public java.lang.Object runSemanticAction_95()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object cds = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_96()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rest = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rest; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_97()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_98()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_99()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_100()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_101()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_102()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_103()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_104()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_105()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_106()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_107()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object de = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = de; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_108()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ip = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ip; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_109()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new LinkedList<NameBinding>(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_110()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_111()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object nr = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = nr; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_112()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_113()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = vd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_114()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_115()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_116()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_117()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object type = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new Variable(new NameBindingImpl((String)id, null), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object t = (java.lang.Object) _children[2];
            java.lang.Object inner = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object tgt = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object op = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)src,(String)op, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object lit = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DSLLit((String)lit); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new IntegerConstant((Integer)res); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_125()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new New(new HashMap<String,TypedAST>(), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_126()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_127()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = UnitVal.getInstance(null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_128()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Arrow((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_129()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Tuple((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_130()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_131()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new UnresolvedType((String)id); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ty = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ty; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_133()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_134()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_135()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object md = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_136()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, (DeclSequence)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_137()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_138()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_139()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object type = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); 
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
        public java.lang.Object runSemanticAction_26(final String lexeme)
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
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\121\113\156\024\061" +
"\020\165\146\246\223\011\220\200\304\232\053\104\351\157\022\261" +
"\212\104\100\021\277\050\023\261\141\061\162\332\236\211\043\267" +
"\273\343\366\314\020\161\005\216\002\227\341\022\210\005\167\300" +
"\256\347\016\154\252\312\135\357\275\172\125\375\375\017\113\126" +
"\226\075\377\374\356\226\257\371\201\346\146\171\060\163\126\231" +
"\345\313\037\077\077\375\372\375\342\353\233\021\143\137\072\306" +
"\330\067\307\306\147\037\137\073\266\377\112\012\151\334\245\354" +
"\270\262\163\347\330\024\037\250\074\067\103\271\373\101\156\264" +
"\062\222\076\317\072\136\313\076\224\073\334\332\166\023\252\107" +
"\265\346\175\377\166\043\302\343\111\255\333\136\136\160\053\015" +
"\241\352\126\267\103\325\064\234\004\103\025\305\267\005\357\157" +
"\102\361\114\310\132\065\134\237\033\047\227\022\166\204\134\104" +
"\325\251\120\153\045\310\102\042\132\207\334\153\152\311\273\025" +
"\327\260\264\060\203\013\025\334\253\205\202\322\276\132\232\326" +
"\112\021\067\161\154\257\221\216\013\356\170\304\157\067\053\115" +
"\252\117\103\241\346\377\071\234\032\271\211\250\307\155\047\315" +
"\303\152\333\235\136\321\330\275\376\246\265\016\327\046\206\173" +
"\070\315\216\123\032\266\167\335\175\047\207\155\326\134\377\053" +
"\155\054\223\331\325\351\345\225\143\243\323\063\037\336\373\220" +
"\320\141\035\333\022\164\214\132\137\267\342\336\377\075\177\027" +
"\377\321\257\061\132\324\376\271\250\063\257\257\114\130\115\253" +
"\060\125\165\334\362\306\063\107\341\071\035\226\365\130\143\275" +
"\126\322\136\337\326\076\117\174\216\117\025\163\150\117\333\340" +
"\225\367\136\173\253\013\173\106\261\111\107\354\161\027\342\050" +
"\204\211\043\053\023\047\155\343\371\156\325\151\031\236\236\036" +
"\166\037\124\222\120\211\370\305\357\341\065\374\005\050\132\257" +
"\117\173\137\170\336\274\053\216\220\216\221\116\050\225\207\110" +
"\051\122\106\051\255\220\074\141\354\221\024\113\212\150\144\040" +
"\145\040\145\121\267\042\104\112\021\200\243\210\053\221\100\256" +
"\162\244\002\054\350\142\022\234\345\021\016\203\071\064\162\314" +
"\312\241\221\143\144\036\011\160\235\103\070\207\160\212\136\172" +
"\102\312\031\105\000\262\230\042\016\302\151\334\374\220\200\030" +
"\235\002\230\106\040\154\025\170\225\350\225\361\205\136\011\167" +
"\045\334\225\160\120\102\254\302\036\025\306\125\030\127\201\127" +
"\305\313\200\127\201\127\201\127\200\120\200\120\304\035\143\057" +
"\336\027\275\354\370\057\126\165\365\233\251\004\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\120\313\156\024\061" +
"\020\364\076\263\001\022\220\070\163\343\034\145\236\111\304\011" +
"\011\204\042\036\102\104\342\202\224\225\063\366\156\034\171\074" +
"\306\343\335\045\342\027\370\024\370\031\176\002\161\340\037\260" +
"\135\075\201\113\127\267\273\253\272\332\337\377\260\331\306\261" +
"\307\237\336\334\360\055\077\322\334\254\217\056\274\123\146\375" +
"\354\307\317\217\277\176\077\371\372\152\314\330\027\313\030\373" +
"\346\331\350\251\147\207\057\244\220\306\177\220\226\053\267\364" +
"\236\055\360\220\322\163\063\244\373\357\344\116\053\043\323\363" +
"\205\345\215\354\143\272\307\235\353\166\061\273\327\150\336\367" +
"\257\167\042\026\017\032\335\365\362\075\167\322\244\251\246\323" +
"\335\220\265\055\117\202\061\043\361\271\340\375\165\114\036\011" +
"\331\250\226\353\163\343\345\132\302\216\220\053\122\135\010\265" +
"\125\042\131\230\211\316\003\173\235\132\362\363\206\153\130\132" +
"\231\301\205\212\356\325\112\101\351\120\255\115\347\244\240\113" +
"\074\073\150\245\347\202\173\116\363\363\166\243\223\352\303\230" +
"\250\345\177\016\027\106\356\150\352\176\147\245\271\073\155\156" +
"\365\046\255\075\350\257\073\347\361\327\211\341\357\276\146\317" +
"\053\015\333\373\376\326\312\341\232\055\327\377\122\107\351\350" +
"\322\263\361\363\227\041\274\015\141\226\076\065\274\212\364\021" +
"\215\276\352\304\255\147\223\360\047\341\061\234\060\136\065\241" +
"\134\065\171\320\126\046\236\245\125\334\250\054\167\274\015\314" +
"\161\054\027\303\241\141\326\270\240\065\353\256\156\232\200\323" +
"\200\124\052\302\330\136\164\321\047\357\203\366\310\306\033\111" +
"\154\152\023\173\142\143\034\307\060\365\311\312\324\113\327\006" +
"\276\337\130\055\143\031\350\361\356\101\145\026\063\101\057\341" +
"\216\240\021\256\117\321\005\245\313\160\364\164\151\313\023\300" +
"\051\340\054\101\165\014\310\000\171\202\254\006\004\302\044\114" +
"\246\130\245\210\106\016\122\016\122\116\272\165\232\310\122\304" +
"\300\011\315\125\000\220\353\002\120\202\005\135\154\202\263\202" +
"\306\141\260\200\106\201\135\005\064\012\254\054\210\000\327\005" +
"\204\013\010\147\350\145\147\111\071\117\021\003\071\001\315\101" +
"\070\243\313\217\323\040\126\147\030\314\150\020\266\112\124\025" +
"\172\025\125\350\125\160\127\301\135\005\007\025\304\152\334\121" +
"\143\135\215\165\065\170\065\375\014\170\065\170\065\170\045\010" +
"\045\010\045\335\110\075\372\137\364\362\323\277\314\231\036\152" +
"\237\004\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\172\030\006\011\120\030\044\330\201\201\201" +
"\011\210\231\241\230\021\215\315\012\145\063\101\371\310\362\254" +
"\110\142\060\314\202\144\036\043\222\136\020\146\103\022\147\100" +
"\323\207\056\316\200\046\207\154\077\223\003\166\173\101\064\073" +
"\222\172\146\007\114\073\220\375\312\214\144\036\262\132\230\237" +
"\101\146\262\000\000\113\240\115\070\113\002\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\205\303\071\012\302\000" +
"\024\004\320\041\340\075\104\115\334\023\343\276\233\245\111\212" +
"\234\040\215\107\020\041\301\013\331\131\131\172\044\301\073\070" +
"\305\024\037\033\037\274\307\007\215\352\012\247\314\213\327\331" +
"\253\337\317\273\003\334\056\000\262\046\320\342\266\331\061\135" +
"\365\314\256\366\270\317\003\036\362\210\307\352\153\300\023\015" +
"\171\152\316\314\271\271\320\245\256\164\255\033\335\232\073\336" +
"\377\171\320\343\317\023\107\146\254\011\247\137\303\123\275\005" +
"\073\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\135\111\227\024\105" +
"\020\216\007\342\202\013\242\050\202\133\270\341\212\373\056\152" +
"\273\302\010\210\273\342\270\214\240\270\313\346\040\212\042\056" +
"\250\350\301\137\341\315\223\107\177\217\047\337\363\354\325\214" +
"\236\016\314\214\211\310\316\352\256\252\314\236\352\357\275\174" +
"\221\365\105\144\144\276\230\254\352\312\255\346\367\177\140\305" +
"\374\001\130\076\073\073\263\366\337\277\166\254\373\365\304\246" +
"\145\000\207\367\001\300\161\307\057\233\235\331\376\347\334\206" +
"\103\177\377\361\033\323\217\303\024\001\346\367\303\121\160\321" +
"\211\107\006\241\157\243\361\313\135\072\305\273\136\061\220\247" +
"\272\164\232\142\177\272\113\147\050\374\112\227\316\164\351\254" +
"\301\365\331\056\235\343\322\052\227\316\035\160\253\135\072\317" +
"\245\363\007\327\153\204\217\013\214\066\136\350\345\327\272\164" +
"\321\040\277\116\261\135\357\322\305\056\135\222\026\231\121\340" +
"\274\137\132\267\317\066\361\177\144\172\000\137\022\043\045\043" +
"\246\247\274\264\327\312\246\330\226\202\106\373\314\145\165\373" +
"\154\023\101\237\171\211\030\051\031\061\075\345\245\275\126\066" +
"\305\266\024\064\332\147\056\257\333\147\233\010\372\314\041\142" +
"\244\144\304\364\224\227\366\132\331\024\333\122\020\104\346\053" +
"\142\244\144\304\364\224\227\366\132\331\024\333\122\060\336\335" +
"\204\356\175\106\134\217\364\076\243\330\365\337\147\024\036\275" +
"\374\032\251\257\002\124\336\147\174\064\372\234\271\242\156\237" +
"\155\242\321\310\134\131\267\317\066\321\150\144\256\252\333\147" +
"\233\010\236\300\317\023\043\045\103\362\010\160\365\250\265\272" +
"\262\327\214\132\266\055\004\221\331\114\214\224\214\230\036\001" +
"\066\304\152\161\372\153\065\137\045\043\210\314\001\142\130\372" +
"\320\070\346\175\235\357\303\362\023\343\255\172\162\040\210\314" +
"\207\304\260\354\072\202\310\174\112\014\113\037\032\307\274\257" +
"\363\175\130\176\142\274\125\117\016\004\221\371\200\030\226\135" +
"\307\377\221\231\313\335\224\302\020\364\231\043\304\110\311\210" +
"\351\051\057\355\265\262\051\266\245\040\210\314\016\142\130\166" +
"\035\101\144\136\310\335\232\222\020\104\346\151\142\130\246\000" +
"\153\034\153\243\262\166\240\330\134\347\345\345\332\301\365\103" +
"\332\172\203\270\126\307\332\270\150\355\240\007\260\237\064\054" +
"\205\265\272\252\102\266\350\255\252\370\076\064\077\026\357\174" +
"\254\324\164\216\137\345\345\127\153\155\250\002\347\343\106\034" +
"\254\252\030\372\365\234\017\042\363\024\061\122\062\142\172\312" +
"\073\257\067\131\065\372\266\316\156\243\364\235\013\010\160\263" +
"\245\013\042\163\220\030\226\076\064\216\171\137\347\373\260\374" +
"\304\170\253\236\034\230\274\065\112\257\114\352\032\345\055\136" +
"\276\346\065\112\234\354\310\334\352\345\307\214\214\323\334\346" +
"\322\355\132\115\135\101\163\163\172\026\020\340\216\266\352\362" +
"\352\274\263\152\231\152\221\161\065\334\125\265\206\111\000\002" +
"\334\055\271\364\310\270\322\367\064\320\250\142\141\076\147\356" +
"\165\351\276\054\115\052\004\172\144\172\000\357\153\326\230\160" +
"\067\131\145\163\000\001\356\117\264\173\100\162\323\347\014\001" +
"\001\066\111\256\314\325\333\201\355\203\103\364\005\257\336\142" +
"\263\221\171\150\210\276\243\221\111\250\173\334\310\074\034\323" +
"\167\072\062\275\230\076\030\153\177\102\014\313\256\043\210\314" +
"\136\142\130\166\035\101\144\266\023\303\262\353\010\042\103\273" +
"\354\201\145\012\244\255\357\103\363\143\361\303\164\071\320\350" +
"\376\231\107\352\366\331\046\052\215\050\037\125\270\266\367\351" +
"\075\346\345\327\014\344\250\277\253\211\357\063\075\200\317\211" +
"\221\222\021\323\123\136\332\153\145\123\154\113\101\020\231\327" +
"\210\141\331\165\004\221\171\235\030\226\135\307\342\347\114\017" +
"\340\055\151\205\000\117\130\036\320\233\041\037\007\350\315\220" +
"\033\372\047\023\174\154\166\151\213\113\063\070\342\232\226\053" +
"\267\325\245\155\101\237\331\106\032\226\076\064\216\171\137\347" +
"\373\260\374\304\170\253\236\034\250\364\333\124\351\015\020\053" +
"\254\217\217\012\154\160\337\306\164\346\212\200\000\317\110\256" +
"\321\067\275\147\015\376\271\272\353\032\007\050\366\075\063\206" +
"\107\006\073\272\257\046\170\002\037\046\106\112\106\114\117\171" +
"\151\257\225\115\261\055\005\343\337\115\010\360\342\020\175\343" +
"\047\003\135\035\057\127\264\177\105\341\166\372\327\101\237\071" +
"\101\014\113\037\032\307\274\257\363\175\130\176\142\274\125\117" +
"\016\230\053\161\257\216\352\321\225\235\035\263\121\232\317\326" +
"\107\054\346\112\334\173\243\172\034\247\154\111\230\230\076\323" +
"\372\130\056\170\316\364\307\045\122\062\142\172\254\176\212\147" +
"\350\030\050\067\202\310\364\107\215\122\062\142\172\254\036\031" +
"\163\204\132\012\202\310\314\020\043\045\043\246\357\055\214\155" +
"\143\173\073\117\332\342\302\336\316\031\313\266\115\140\352\336" +
"\316\055\304\110\311\210\351\173\013\043\377\130\144\116\332\342" +
"\102\144\266\130\266\155\002\123\043\363\005\061\122\062\142\172" +
"\312\113\173\255\154\212\155\051\010\042\363\065\061\122\062\142" +
"\172\312\113\173\255\154\212\155\051\110\037\035\364\006\277\344" +
"\010\360\106\303\215\052\002\225\146\256\336\154\276\075\345\240" +
"\226\021\345\134\155\315\051\010\352\014\371\333\276\354\052\354" +
"\076\203\312\032\102\227\260\070\062\010\260\053\137\173\312\201" +
"\172\067\355\041\211\000\273\063\065\252\010\250\221\231\313\326" +
"\234\202\240\106\146\227\057\273\012\065\062\273\175\331\125\124" +
"\173\237\351\001\174\334\154\173\312\301\164\215\222\200\312\273" +
"\133\245\321\301\073\015\064\252\130\114\314\074\360\236\272\175" +
"\016\203\372\004\376\216\023\137\153\322\202\246\267\312\014\363" +
"\225\023\323\025\177\004\170\127\343\325\076\363\075\047\276\326" +
"\244\005\115\217\306\032\024\026\164\112\114\042\313\331\333\211" +
"\370\046\122\243\167\323\104\177\111\254\376\310\040\300\107\165" +
"\371\312\011\365\071\363\143\276\366\224\003\065\062\077\220\304" +
"\016\215\004\064\250\063\127\323\223\137\320\221\257\254\354\365" +
"\362\055\375\047\010\154\377\254\312\076\057\337\362\331\333\036" +
"\300\317\276\224\171\037\304\153\166\222\267\354\045\157\325\223" +
"\003\152\144\176\361\245\314\373\040\136\263\223\274\145\057\171" +
"\253\236\034\230\270\273\111\073\371\265\350\133\153\051\300\052" +
"\047\277\216\022\043\045\043\246\247\274\264\327\312\246\330\226" +
"\202\106\107\007\305\174\005\171\024\054\311\363\115\007\261\356" +
"\363\115\133\111\303\322\207\306\061\357\353\174\037\226\237\030" +
"\157\325\223\003\152\237\121\147\162\054\140\101\175\306\263\245" +
"\076\063\322\367\251\161\121\237\031\126\242\067\330\253\357\112" +
"\315\217\122\343\244\241\362\172\323\222\230\141\110\101\360\234" +
"\351\317\317\112\051\363\076\210\327\354\044\157\331\113\336\252" +
"\047\007\052\255\067\215\374\077\227\160\072\017\354\001\047\147" +
"\355\340\063\215\237\106\006\215\363\171\145\216\233\022\353\036" +
"\167\026\042\372\155\212\316\174\321\264\362\236\365\062\373\014" +
"\332\143\355\043\136\276\340\257\306\015\152\130\052\047\114\203" +
"\377\253\032\274\317\374\104\014\113\037\032\307\274\257\363\175" +
"\130\176\142\274\125\117\016\250\153\007\305\377\107\304\066\020" +
"\335\051\135\374\354\122\223\250\064\242\334\071\314\146\051\241" +
"\063\277\332\225\317\123\251\363\063\235\072\255\143\101\215\114" +
"\247\166\052\132\150\164\334\324\306\067\213\216\065\345\133\355" +
"\063\337\162\342\153\115\132\320\364\126\231\141\276\162\102\215" +
"\314\067\234\370\132\223\026\064\275\125\146\230\257\234\120\043" +
"\163\214\023\137\153\322\202\246\307\045\075\163\205\103\376\276" +
"\030\071\311\056\354\066\246\330\265\001\114\072\311\236\350\151" +
"\251\236\073\130\364\274\123\357\246\332\167\307\117\042\324\310" +
"\034\317\327\236\162\020\314\102\364\347\155\245\224\171\037\304" +
"\153\166\222\267\354\045\157\325\223\003\101\144\372\337\041\224" +
"\122\346\175\020\257\331\111\336\262\227\274\125\117\016\114\237" +
"\300\004\124\316\314\124\232\205\250\064\243\070\351\010\356\246" +
"\376\212\224\224\214\230\236\362\322\136\053\233\142\133\012\324" +
"\337\246\171\137\166\025\023\163\132\060\172\246\252\011\330\317" +
"\231\136\107\276\300\143\341\077\277\077\002\332\253\222\000\000" +
""
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\305\226\061\113\003\101" +
"\020\205\147\163\071\162\027\010\210\126\026\302\036\234\020\233" +
"\160\105\004\215\235\130\004\261\263\274\046\021\004\043\052\061" +
"\167\047\051\044\235\150\145\143\055\134\040\126\332\010\346\167" +
"\370\113\374\011\202\131\210\215\354\073\170\160\140\212\244\370" +
"\062\073\273\063\157\336\356\333\227\270\331\110\326\342\243\363" +
"\376\115\277\225\245\203\213\326\376\040\075\076\115\367\056\327" +
"\233\325\317\307\203\141\105\144\074\024\221\273\144\044\053\177" +
"\377\165\365\375\060\331\155\277\006\216\250\130\252\047\203\064" +
"\111\245\022\037\216\207\213\105\315\257\256\316\127\337\263\247" +
"\337\065\224\054\077\343\344\132\046\122\311\314\267\273\004\316" +
"\175\347\054\267\001\121\221\247\255\240\061\175\336\376\267\010" +
"\010\152\235\246\175\051\230\203\007\276\336\260\047\367\163\373" +
"\256\114\165\137\354\071\264\267\131\116\204\001\001\131\166\166" +
"\051\147\121\135\162\273\050\071\214\200\112\054\220\250\366\166" +
"\254\300\325\250\265\272\216\200\217\000\310\241\302\172\223\313" +
"\001\045\132\042\050\152\055\007\140\317\361\174\324\072\133\254" +
"\001\320\273\122\272\073\043\007\047\202\255\005\215\162\241\063" +
"\150\257\107\036\260\327\265\056\125\260\335\320\003\272\242\001" +
"\262\045\014\170\337\345\201\322\015\172\006\355\105\064\375\150" +
"\043\020\041\020\322\113\361\021\010\100\367\241\155\011\365\074" +
"\230\065\156\071\121\343\010\350\242\160\076\102\074\070\045\225" +
"\035\337\006\310\023\115\304\007\005\260\275\322\057\031\336\257" +
"\114\004\252\025\034\034\273\174\114\216\271\275\347\071\320\025" +
"\124\011\004\260\126\120\076\370\362\342\315\262\100\127\310\000" +
"\320\311\361\233\001\347\000\200\036\065\036\270\001\173\253\321" +
"\347\200\362\301\272\342\215\254\350\151\000\234\001\336\203\246" +
"\347\077\375\034\172\010\135\015\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\153\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\012\223\140\124\340\120\030\225\030\225\030\225\030\225\030" +
"\225\030\225\030\225\030\225\030\225\030\225\030\225\030\225\030" +
"\212\022\000\020\342\061\205\135\015\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\153\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\072\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\324\045\000\233\366\222" +
"\004\135\015\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\044\072\332\047\053\261\054\121\257\264\044\063\107\317" +
"\051\263\044\070\265\304\132\362\322\273\215\346\317\356\030\061" +
"\061\060\124\024\060\060\060\264\002\025\012\143\121\227\053\251" +
"\301\162\276\317\245\000\246\116\251\200\040\050\055\144\250\143" +
"\140\032\125\073\252\166\124\355\250\332\121\265\243\152\107\325" +
"\216\252\035\125\073\252\166\124\355\250\332\121\265\243\152\107" +
"\325\216\252\035\125\073\252\166\124\355\250\332\121\265\243\152" +
"\007\126\055\000\306\342\220\130\047\027\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\224\030\006\011\000\000\076\234\220\047\243" +
"\000\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\040\146\136\377\377\377\337\012\000\262\057\134\063\121\000\000" +
"\000"
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\114\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\330" +
"\004\030\101\202\014\002\104\253\144\301\045\301\240\200\113\242" +
"\201\170\113\011\030\205\123\007\351\022\015\070\055\147\302\056" +
"\341\200\063\110\030\161\111\050\060\310\021\031\252\100\212\130" +
"\225\002\044\372\224\005\267\121\014\012\130\045\070\250\220\114" +
"\200\326\222\157\004\116\367\072\020\253\222\203\366\111\316\203" +
"\344\044\207\045\071\020\237\116\260\233\051\100\214\112\152\232" +
"\305\010\161\057\311\176\307\225\326\310\320\301\104\025\057\123" +
"\053\345\060\221\036\026\270\322\301\300\104\346\300\330\012\052" +
"\117\261\111\060\342\322\301\104\274\053\032\110\211\222\101\022" +
"\050\330\265\062\342\016\076\206\206\012\000\041\144\017\111\114" +
"\010\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\114\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\243" +
"\002\243\002\164\020\140\004\011\062\010\014\032\367\214\012\214" +
"\306\017\051\301\100\272\304\160\014\142\204\027\000\372\257\233" +
"\102\264\006\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\325\224\261\116\303\060" +
"\020\206\177\273\256\324\061\152\047\026\344\156\114\235\272\124" +
"\135\242\212\011\261\261\040\205\245\225\030\202\150\225\066\116" +
"\225\251\312\330\211\205\307\200\205\201\347\340\111\170\004\010" +
"\116\245\056\350\376\301\250\103\030\142\313\372\174\167\072\373" +
"\163\136\077\321\055\066\030\044\327\017\363\355\174\124\270\364" +
"\161\064\113\335\315\275\233\056\317\056\314\307\323\145\246\201" +
"\062\003\160\227\157\020\375\336\265\372\332\357\046\343\227\141" +
"\007\052\201\131\244\056\167\320\311\125\231\371\244\315\154\315" +
"\173\377\255\170\076\346\100\231\257\261\203\056\232\261\353\327" +
"\312\177\235\272\256\277\045\000\104\162\004\240\031\060\210\145" +
"\000\053\107\050\354\145\120\321\342\054\225\246\021\267\014\104" +
"\250\144\120\321\342\132\006\061\071\335\303\050\003\213\163\021" +
"\104\064\225\237\302\042\254\107\244\363\130\006\206\027\207\025" +
"\101\217\171\305\365\141\251\232\362\247\062\221\002\020\105\025" +
"\357\274\327\106\105\231\076\341\136\375\063\140\351\105\061\251" +
"\231\160\177\121\224\171\105\365\071\041\010\067\221\013\027\354" +
"\125\073\145\150\047\140\046\052\046\234\246\046\122\031\370\337" +
"\207\135\155\073\317\052\034\360\367\321\164\376\003\300\057\073" +
"\370\124\011\000\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\321\111\112\004\061" +
"\024\006\340\164\151\073\265\163\073\117\355\330\266\107\161\341" +
"\011\334\170\004\021\024\057\324\073\127\056\075\222\340\035\174" +
"\005\117\250\205\112\051\256\344\013\174\374\171\125\311\113\040" +
"\117\157\245\173\177\127\252\353\313\253\227\233\341\303\353\363" +
"\270\052\345\361\266\164\112\075\106\055\034\206\365\106\075\154" +
"\271\257\155\357\072\067\076\371\267\033\016\302\126\130\012\107" +
"\341\064\354\205\325\060\025\272\241\377\215\231\264\334\350\073" +
"\031\316\302\171\326\235\137\132\374\341\372\372\254\223\306\075" +
"\076\276\315\347\174\041\354\207\355\320\373\242\307\116\346\154" +
"\346\132\346\040\154\206\351\254\217\303\104\316\347\062\253\074" +
"\357\042\254\374\341\033\002\000\000\000\000\000\000\000\000\000" +
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
"\000\000\000\360\317\274\003\050\057\060\323\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\132\111\123\024\061" +
"\024\116\015\202\042\213\310\342\060\056\210\333\260\072\040\210" +
"\053\040\040\056\243\054\262\213\343\201\237\000\124\015\345\211" +
"\073\047\176\005\067\117\036\375\075\234\254\362\314\225\327\125" +
"\335\226\046\235\351\164\346\245\175\311\364\127\365\125\167\177" +
"\057\171\365\125\052\323\111\136\317\367\337\254\276\274\317\352" +
"\112\245\142\366\354\164\071\167\174\064\231\141\354\333\056\143" +
"\354\053\350\231\122\161\361\347\116\376\340\327\217\223\100\036" +
"\144\226\240\274\307\016\031\270\376\343\330\273\257\363\257\027" +
"\200\365\300\006\340\105\340\045\140\243\037\273\354\137\233\374" +
"\153\063\260\005\330\012\274\342\267\365\364\066\340\125\377\276" +
"\335\277\172\354\000\166\002\273\200\327\174\055\013\354\006\346" +
"\200\327\201\067\224\034\337\254\160\255\104\036\274\316\347\011" +
"\323\171\055\024\202\343\133\310\304\312\331\023\334\013\216\251" +
"\341\066\057\220\167\054\100\311\361\035\205\104\275\070\176\242" +
"\241\365\313\273\313\050\375\362\170\334\223\334\107\041\116\333" +
"\130\110\374\355\166\137\242\353\217\161\224\343\007\125\072\306" +
"\237\025\111\254\040\171\124\307\344\221\072\066\217\324\261\200" +
"\276\030\155\373\125\032\011\216\007\220\351\345\035\322\350\067" +
"\054\213\071\060\053\036\042\263\040\321\107\042\372\215\312\142" +
"\344\307\370\021\057\044\276\346\215\111\364\164\315\113\006\343" +
"\052\215\110\071\126\102\125\363\370\061\213\077\217\315\237\101" +
"\252\305\004\166\302\032\233\025\064\316\040\366\325\204\250\073" +
"\176\342\300\074\176\212\314\027\010\071\236\001\237\007\317\202" +
"\343\227\310\304\310\371\117\016\243\357\212\111\356\371\357\074" +
"\141\072\257\205\302\241\052\326\024\367\114\367\175\154\337\012" +
"\022\307\361\064\011\307\330\210\163\372\177\245\322\210\224\343" +
"\132\251\127\014\032\340\214\106\237\131\131\314\201\175\305\234" +
"\006\137\127\210\171\171\347\065\162\276\221\305\034\250\011\025" +
"\022\342\333\210\370\073\131\314\201\171\374\136\203\305\012\261" +
"\202\104\377\020\221\363\243\054\346\300\030\253\356\053\026\270" +
"\147\036\366\356\053\342\100\257\356\226\304\156\163\061\104\137" +
"\012\321\324\034\223\207\165\365\012\261\302\142\337\351\277\166" +
"\034\057\133\347\370\377\215\161\155\327\204\302\336\307\237\044" +
"\172\132\257\250\336\361\212\061\307\330\260\257\136\201\016\301" +
"\361\052\062\275\274\153\032\375\326\145\061\007\052\054\174\213" +
"\015\115\247\101\277\344\035\323\037\143\373\052\054\344\041\070" +
"\336\104\146\101\242\157\105\364\373\054\213\071\120\305\342\133" +
"\154\153\072\015\372\045\357\230\376\030\333\127\305\112\167\233" +
"\251\143\265\177\053\174\221\350\064\116\246\366\215\261\051\307" +
"\045\143\216\355\373\236\147\237\343\364\324\144\176\107\157\337" +
"\367\074\373\034\247\073\172\363\273\115\373\352\307\344\127\351" +
"\163\103\227\200\260\041\100\000\000"
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
        TERMINAL_COUNT = 34;
        GRAMMAR_SYMBOL_COUNT = 68;
        SYMBOL_COUNT = 140;
        PARSER_STATE_COUNT = 133;
        SCANNER_STATE_COUNT = 92;
        DISAMBIG_GROUP_COUNT = 7;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { ex.printStackTrace(); System.exit(1); }
        catch(java.lang.ClassNotFoundException ex) { ex.printStackTrace(); System.exit(1); }
        disambiguationGroups = new java.util.BitSet[7];
        disambiguationGroups[0] = newBitVec(34,4,21);
        disambiguationGroups[1] = newBitVec(34,2,4,21);
        disambiguationGroups[2] = newBitVec(34,2,21);
        disambiguationGroups[3] = newBitVec(34,3,21);
        disambiguationGroups[4] = newBitVec(34,1,4,21);
        disambiguationGroups[5] = newBitVec(34,3,4,21);
        disambiguationGroups[6] = newBitVec(34,2,3,4,21);
    }

}
