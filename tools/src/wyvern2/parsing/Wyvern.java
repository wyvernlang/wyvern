/*
 * Built at Fri Mar 14 18:04:24 ADT 2014
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
            case 85:
                RESULT = runSemanticAction_85();
                break;
            case 86:
                RESULT = runSemanticAction_86();
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
            case 140:
                RESULT = runSemanticAction_140();
                break;
            case 141:
                RESULT = runSemanticAction_141();
                break;
            case 142:
                RESULT = runSemanticAction_142();
                break;
            case 143:
                RESULT = runSemanticAction_143();
                break;
            case 144:
                RESULT = runSemanticAction_144();
                break;
            case 145:
                RESULT = runSemanticAction_145();
                break;
            case 146:
                RESULT = runSemanticAction_146();
                break;
            case 147:
                RESULT = runSemanticAction_147();
                break;
            case 148:
                RESULT = runSemanticAction_148();
                break;
            case 149:
                RESULT = runSemanticAction_149();
                break;
            case 150:
                RESULT = runSemanticAction_150();
                break;
            case 151:
                RESULT = runSemanticAction_151();
                break;
            case 152:
                RESULT = runSemanticAction_152();
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
        public java.lang.Object runSemanticAction_74()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_75()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_76()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object mer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = mer;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_77()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_78()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_79()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ter = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = ter;
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
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_83()
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
        public java.lang.Object runSemanticAction_84()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_85()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object type = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_86()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ClassDeclaration((String)id, "", "",
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_87()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = new ClassDeclaration((String)id, "", "", null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_88()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object after = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_89()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_90()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_91()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_92()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_93()
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
        public java.lang.Object runSemanticAction_94()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = aer; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_95()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object pi = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = pi; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_98()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object re = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             ((LinkedList<NameBinding>)re).addFirst(new NameBindingImpl((String)id, (Type)ta)); RESULT = re; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_99()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             LinkedList<NameBinding> llnb = new LinkedList<NameBinding>(); llnb.add(new NameBindingImpl((String)id, (Type)ta)); RESULT = llnb; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_100()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object first = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_101()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object el = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = el; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_102()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_103()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_104()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_105()
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
        public java.lang.Object runSemanticAction_106()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object cds = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_107()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rest = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rest; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_108()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_109()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_110()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_111()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_112()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_113()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_114()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_117()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object de = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = de; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ip = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ip; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new LinkedList<NameBinding>(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object nr = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = nr; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_125()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_126()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = vd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_127()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_128()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_129()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_130()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object type = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_131()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new Variable(new NameBindingImpl((String)id, null), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object t = (java.lang.Object) _children[2];
            java.lang.Object inner = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_133()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_134()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object tgt = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_135()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object op = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)src,(String)op, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_136()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object lit = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DSLLit((String)lit); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_137()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new IntegerConstant((Integer)res); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_138()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new New(new HashMap<String,TypedAST>(), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_139()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_140()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = UnitVal.getInstance(null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_141()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Arrow((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_142()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Tuple((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_143()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_144()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new UnresolvedType((String)id); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_145()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ty = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ty; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_146()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_147()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_148()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object md = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_149()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, (DeclSequence)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_150()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_151()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_152()
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
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\121\333\156\324\060" +
"\024\364\136\273\005\132\220\170\346\027\252\046\331\115\132\361" +
"\124\211\005\125\334\252\266\342\205\207\225\067\366\156\135\071" +
"\116\352\170\167\251\370\022\376\001\176\206\237\100\074\360\017" +
"\330\147\234\302\213\347\034\147\146\074\347\344\373\037\066\332" +
"\130\366\374\363\273\133\276\345\107\232\233\365\321\225\263\312" +
"\254\137\376\370\371\351\327\357\027\137\337\364\031\373\322\060" +
"\306\276\071\066\230\177\174\355\330\341\053\051\244\161\227\262" +
"\341\312\056\234\143\023\134\120\171\156\272\162\377\203\334\151" +
"\145\044\135\137\065\274\224\155\050\367\270\265\365\056\124\217" +
"\112\315\333\366\355\116\204\346\111\251\353\126\136\160\053\015" +
"\261\312\132\327\135\125\125\234\014\103\025\315\307\202\267\067" +
"\241\170\046\144\251\052\256\317\215\223\153\211\070\102\256\242" +
"\353\104\250\255\022\024\141\044\152\007\154\065\175\222\167\033" +
"\256\021\151\145\272\024\052\244\127\053\005\247\103\265\066\265" +
"\225\042\116\342\330\101\045\035\027\334\361\310\037\127\033\115" +
"\256\117\103\241\026\377\045\234\030\271\213\254\307\165\043\315" +
"\303\150\343\106\157\350\331\203\366\246\266\016\333\046\205\173" +
"\130\315\236\123\032\261\367\335\175\043\273\151\266\134\377\053" +
"\155\054\107\127\327\147\227\327\216\365\317\346\376\170\077\017" +
"\213\135\372\255\350\145\055\356\375\347\320\254\010\275\074\242" +
"\015\030\266\357\130\117\320\306\072\372\200\310\075\077\153\177" +
"\125\372\166\125\246\076\204\062\141\176\255\102\064\325\160\313" +
"\053\257\354\207\166\322\155\304\163\215\365\136\243\172\171\133" +
"\172\034\172\214\255\212\210\317\116\232\360\144\035\006\343\255" +
"\177\243\327\204\245\104\323\141\103\056\203\046\234\375\160\014" +
"\035\105\032\072\151\053\257\167\233\106\313\320\172\171\130\124" +
"\347\062\012\225\210\067\176\036\357\101\363\016\150\332\061\055" +
"\351\302\353\026\315\154\006\310\001\005\340\004\160\112\220\037" +
"\023\144\350\246\350\246\011\040\003\244\200\051\101\002\263\304" +
"\233\015\374\045\235\063\072\361\041\205\105\012\213\064\276\011" +
"\136\102\047\010\005\142\244\160\115\221\264\200\252\300\213\263" +
"\214\370\170\011\364\014\274\024\161\263\030\036\252\014\001\062" +
"\074\231\105\001\254\062\214\222\341\265\031\164\063\350\022\060" +
"\223\123\172\047\245\023\364\024\342\024\135\022\351\270\114\216" +
"\211\210\040\111\144\304\025\305\265\203\230\103\226\307\016\314" +
"\034\314\034\314\034\311\163\044\317\221\047\207\165\021\327\005" +
"\135\001\135\021\327\005\135\001\335\064\002\344\323\370\107\301" +
"\234\306\237\023\341\344\057\013\363\005\203\016\005\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\121\313\156\324\060" +
"\024\315\274\332\051\320\202\304\232\035\353\252\111\146\222\126" +
"\254\220\250\120\305\103\210\112\154\220\072\362\304\236\251\053" +
"\307\111\035\317\014\025\137\302\077\300\317\360\023\210\005\377" +
"\200\175\317\115\141\343\163\257\163\316\361\271\067\337\377\044" +
"\223\215\113\236\176\176\173\043\266\342\330\010\273\076\276\364" +
"\116\333\365\213\037\077\077\375\372\375\354\353\353\141\222\174" +
"\151\223\044\371\346\223\301\163\237\034\275\122\122\131\377\121" +
"\265\102\273\205\367\311\024\027\124\136\330\276\074\170\257\166" +
"\106\133\105\327\227\255\250\124\027\313\175\341\134\263\213\325" +
"\203\312\210\256\173\263\223\261\171\124\231\246\123\037\204\123" +
"\226\130\125\143\232\276\252\153\101\206\261\142\363\075\051\272" +
"\353\130\074\221\252\322\265\060\027\326\253\265\102\034\251\126" +
"\354\072\225\172\253\045\105\230\310\306\003\073\103\237\324\355" +
"\106\030\104\132\331\076\205\216\351\365\112\303\351\110\257\155" +
"\343\224\344\111\174\162\130\053\057\244\360\202\371\173\365\306" +
"\220\353\343\130\350\305\177\011\247\126\355\230\365\260\151\225" +
"\275\037\155\257\065\033\172\366\260\273\156\234\307\256\111\341" +
"\357\127\263\357\265\101\354\003\177\327\252\176\232\255\060\377" +
"\112\307\345\340\312\047\303\227\347\341\170\167\036\227\272\014" +
"\033\061\313\106\336\205\131\143\263\042\014\122\106\027\061\156" +
"\076\110\045\155\253\247\217\210\074\010\163\016\127\125\150\127" +
"\125\026\002\150\033\147\067\072\306\322\255\160\242\016\312\141" +
"\154\247\375\066\002\327\272\340\065\151\226\067\125\300\161\100" +
"\156\065\043\076\173\145\343\223\115\034\112\164\341\215\101\033" +
"\027\302\246\343\226\134\106\155\074\207\361\030\173\212\064\366" +
"\312\325\101\357\067\255\121\261\015\362\270\244\336\145\022\053" +
"\311\067\141\236\340\101\363\216\150\332\341\125\330\320\170\321" +
"\316\347\200\002\120\002\116\001\147\004\305\011\101\216\156\206" +
"\156\226\002\162\100\006\230\021\244\060\113\203\331\050\134\322" +
"\071\247\023\037\062\130\144\260\310\370\115\360\122\072\101\050" +
"\021\043\203\153\206\244\045\124\045\136\234\347\304\307\113\240" +
"\347\340\145\210\233\163\170\250\162\004\310\361\144\316\002\130" +
"\345\030\045\307\153\163\350\346\320\245\140\246\147\364\116\106" +
"\047\350\031\304\031\272\224\351\270\114\117\210\210\040\051\063" +
"\170\105\274\166\020\013\310\012\356\300\054\300\054\300\054\220" +
"\274\100\362\002\171\012\130\227\274\056\350\112\350\112\136\027" +
"\164\045\164\063\006\310\147\374\107\301\234\361\317\141\070\375" +
"\013\164\136\166\357\004\005\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\146\062\014\022\240\060\210\261\003\003\003" +
"\023\020\063\103\061\043\032\033\131\216\025\210\131\240\230\025" +
"\052\306\004\125\307\210\246\216\021\015\263\040\231\205\314\007" +
"\141\066\044\163\030\320\364\241\213\243\323\060\173\231\260\350" +
"\101\267\037\104\263\043\251\147\106\123\203\054\216\055\014\140" +
"\352\140\176\007\207\005\000\201\004\257\015\177\002\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\205\303\067\012\002\001" +
"\000\004\300\345\300\177\210\341\314\071\207\063\307\102\260\267" +
"\361\011\042\050\176\310\316\312\322\047\011\376\301\055\266\130" +
"\154\034\230\307\007\261\313\031\301\141\273\173\035\303\353\373" +
"\171\017\200\333\011\300\076\016\044\070\151\123\066\155\103\316" +
"\160\226\163\232\267\005\055\162\211\313\134\341\052\327\264\256" +
"\015\156\152\213\333\266\143\273\266\247\175\035\350\120\043\035" +
"\351\330\116\170\372\347\114\347\077\027\274\264\053\135\363\346" +
"\013\370\262\274\272\133\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\135\131\267\024\065" +
"\020\316\001\161\301\005\121\024\301\255\334\160\305\175\027\265" +
"\161\277\350\005\367\355\272\040\212\270\013\310\105\334\220\305" +
"\015\134\120\101\345\017\370\346\223\217\376\036\237\074\307\147" +
"\137\115\346\116\101\045\135\225\356\364\044\323\231\231\376\316" +
"\311\251\364\127\325\137\162\353\364\364\355\116\272\323\177\374" +
"\253\026\314\156\125\363\147\146\246\226\376\367\367\272\145\007" +
"\366\255\232\247\324\216\315\112\251\303\232\237\067\063\065\375" +
"\327\372\025\333\377\371\363\167\244\247\124\207\112\314\156\121" +
"\073\225\316\230\077\133\240\172\061\034\077\137\227\143\310\366" +
"\202\276\075\126\227\343\230\370\343\165\071\201\341\027\352\162" +
"\242\056\047\365\267\117\326\345\024\046\156\221\056\247\366\353" +
"\213\165\071\115\227\323\373\333\113\234\330\063\074\177\317\231" +
"\244\276\124\227\263\372\365\145\114\354\162\135\316\326\345\234" +
"\172\331\152\002\255\176\156\154\315\266\161\064\133\205\122\373" +
"\014\343\132\204\344\067\026\213\324\012\365\111\261\125\032\071" +
"\040\351\261\165\136\154\315\266\141\035\133\257\030\306\265\010" +
"\311\157\054\026\251\025\352\223\142\253\064\162\100\322\143\353" +
"\374\330\232\155\303\072\266\366\032\306\265\010\311\157\054\026" +
"\251\025\352\223\142\253\064\162\200\225\255\375\206\161\055\102" +
"\362\033\213\105\152\205\372\244\330\052\215\034\060\330\057\021" +
"\364\365\226\263\335\350\172\213\211\143\257\267\230\070\040\365" +
"\045\142\140\115\000\163\275\105\221\364\274\165\101\154\315\266" +
"\221\064\133\027\306\326\154\033\111\263\165\121\154\315\266\141" +
"\235\345\137\062\214\153\021\056\017\112\135\314\305\325\201\336" +
"\367\022\227\063\072\115\264\206\011\053\133\217\030\306\265\010" +
"\311\157\054\050\265\302\327\212\366\137\312\151\271\034\307\347" +
"\004\053\133\237\033\006\055\005\307\041\117\175\124\103\322\361" +
"\361\122\073\271\300\312\326\016\303\240\355\120\206\225\255\335" +
"\206\101\113\301\161\310\123\037\325\220\164\174\274\324\116\056" +
"\260\262\365\241\141\320\166\050\343\150\266\326\267\335\225\021" +
"\200\165\154\175\143\030\327\042\044\277\261\130\244\126\250\117" +
"\212\255\322\310\001\126\266\236\067\014\332\016\145\130\331\172" +
"\271\355\336\344\016\053\133\317\031\006\155\035\100\304\061\010" +
"\250\061\347\303\354\163\031\251\273\163\076\227\327\330\377\012" +
"\147\233\035\203\200\322\234\117\241\164\115\365\262\265\223\211" +
"\146\147\310\114\054\220\031\062\252\301\351\110\274\326\130\050" +
"\371\234\270\105\244\276\330\027\133\027\132\347\112\350\317\220" +
"\011\376\345\130\267\262\265\316\060\256\105\110\176\143\115\321" +
"\252\127\111\055\122\055\035\267\322\325\246\072\222\306\260\000" +
"\112\135\055\371\254\154\355\062\014\132\012\216\103\236\372\250" +
"\206\244\343\343\245\166\162\301\350\317\125\023\137\310\134\365" +
"\065\244\036\171\256\032\306\057\133\327\222\372\200\331\322\236" +
"\353\164\271\136\152\155\122\221\156\354\124\002\050\165\303\260" +
"\332\362\001\224\272\061\164\237\260\154\351\026\156\012\155\141" +
"\124\001\112\335\354\162\365\263\245\367\276\045\101\247\106\012" +
"\342\171\353\126\135\156\153\245\113\031\203\317\126\241\324\166" +
"\056\032\152\374\022\245\175\163\001\050\165\173\315\270\073\134" +
"\256\073\157\111\000\245\126\271\134\236\063\373\375\330\073\003" +
"\373\222\371\314\076\244\315\326\135\201\175\231\340\154\065\350" +
"\113\214\154\335\355\363\167\331\162\064\012\237\337\032\203\370" +
"\304\060\150\073\224\141\145\353\123\303\240\355\120\206\225\255" +
"\147\015\203\266\103\031\126\266\076\063\014\332\072\160\143\251" +
"\006\247\043\361\125\276\134\220\364\371\255\325\261\065\333\106" +
"\320\135\365\075\014\327\366\163\247\367\222\372\222\276\275\257" +
"\152\077\217\136\315\353\255\102\251\257\014\343\132\204\344\067" +
"\026\213\324\012\365\111\261\125\032\071\300\312\326\046\303\240" +
"\355\120\206\225\255\067\014\203\266\103\031\345\363\126\241\324" +
"\073\156\024\050\165\277\244\240\175\017\304\350\211\326\171\120" +
"\227\207\030\176\112\227\065\272\074\334\100\323\074\345\071\255" +
"\313\132\210\060\127\151\035\133\317\030\006\055\005\307\041\117" +
"\175\124\103\322\361\361\122\073\271\040\350\177\342\243\041\312" +
"\072\376\261\206\235\152\014\335\346\343\051\365\273\321\100\011" +
"\240\324\023\056\227\364\352\364\111\201\177\052\166\133\261\001" +
"\112\075\315\361\325\331\202\314\317\045\303\204\165\226\377\322" +
"\060\256\105\110\176\143\261\110\255\120\237\024\133\245\221\003" +
"\006\377\045\102\305\230\005\004\074\075\027\013\320\340\151\120" +
"\275\317\014\303\275\100\267\255\143\353\127\303\240\245\340\070" +
"\344\251\217\152\110\072\076\136\152\047\027\210\263\257\057\066" +
"\125\204\041\274\331\004\055\075\043\053\316\276\316\066\125\034" +
"\144\337\334\061\262\307\326\372\324\155\160\260\316\133\275\373" +
"\060\327\042\044\277\261\020\376\306\135\351\236\317\160\034\237" +
"\023\254\154\255\061\214\153\021\222\277\230\273\343\015\315\326" +
"\032\067\306\160\034\237\023\254\154\255\065\214\153\021\222\337" +
"\330\142\356\036\337\367\114\363\021\055\230\173\246\171\055\027" +
"\303\361\303\006\324\175\246\171\332\060\256\105\110\176\143\213" +
"\271\121\021\137\266\216\150\301\134\266\246\271\030\216\037\066" +
"\240\156\266\276\066\214\153\021\222\337\130\054\122\053\324\047" +
"\305\126\151\344\000\053\133\337\031\306\265\010\311\157\054\026" +
"\251\025\352\223\142\253\064\162\100\375\073\237\102\251\215\306" +
"\102\346\353\030\245\104\320\150\340\206\364\375\311\033\121\356" +
"\252\137\215\327\237\274\221\164\064\360\265\330\232\155\043\151" +
"\266\066\306\326\154\033\111\263\365\172\154\315\266\301\316\047" +
"\276\037\242\020\032\357\323\211\245\225\012\154\266\336\015\121" +
"\010\215\367\351\304\322\112\005\066\133\357\205\050\204\306\373" +
"\164\142\151\245\302\104\314\354\157\202\350\063\373\244\205\356" +
"\251\021\001\354\261\265\045\104\001\062\076\266\210\206\071\266" +
"\336\154\272\077\202\315\326\333\203\252\216\053\302\256\267\012" +
"\245\076\116\333\237\274\321\075\065\042\001\224\172\313\345\202" +
"\306\040\046\376\027\072\262\063\144\245\253\234\141\200\075\313" +
"\377\210\005\267\071\053\201\363\113\373\124\151\345\206\356\371" +
"\055\016\040\334\201\261\307\326\117\130\160\233\263\022\070\077" +
"\010\167\064\220\371\135\264\213\126\126\317\330\074\254\266\142" +
"\043\351\057\061\350\236\140\024\020\077\133\240\324\326\130\132" +
"\271\201\075\157\035\152\257\077\171\203\315\326\101\143\101\251" +
"\017\132\352\124\266\140\107\154\266\265\327\237\274\061\261\253" +
"\225\315\222\372\220\276\103\006\355\277\315\271\235\324\063\131" +
"\075\243\120\352\067\211\247\076\254\273\274\024\357\362\122\073" +
"\271\240\166\266\016\113\074\365\141\335\345\245\170\227\227\332" +
"\311\005\043\377\113\344\336\253\156\274\342\073\204\274\127\375" +
"\255\141\134\213\220\374\306\142\221\132\241\076\051\266\112\043" +
"\007\044\275\363\031\273\057\106\114\364\232\224\037\205\356\063" +
"\221\053\341\325\232\211\201\156\045\274\000\000\263\126\024\173" +
"\237\130\232\353\350\060\007\353\177\142\357\355\330\202\171\113" +
"\226\343\220\247\076\252\041\351\370\170\251\235\134\300\036\133" +
"\043\073\266\231\032\101\117\200\367\236\306\205\011\136\315\054" +
"\170\146\077\370\277\356\070\301\072\157\365\146\134\013\146\346" +
"\225\343\220\247\076\252\041\351\370\170\251\235\134\020\064\263" +
"\337\170\335\065\350\146\310\052\000\243\075\373\312\176\373\245" +
"\313\026\007\020\276\343\230\347\210\115\303\276\304\030\073\335" +
"\345\363\117\364\135\365\356\320\175\362\074\266\240\376\150\340" +
"\036\122\317\144\134\276\242\205\161\136\231\145\057\335\266\256" +
"\267\176\061\014\132\012\216\103\236\372\250\206\244\343\343\245" +
"\166\162\001\073\373\372\105\173\375\311\033\362\057\021\062\137" +
"\335\251\015\004\335\125\217\335\333\231\241\230\350\053\210\340" +
"\165\173\273\221\146\011\300\254\115\041\076\001\336\370\333\276" +
"\172\337\175\115\367\145\264\366\307\322\212\201\354\263\225\325" +
"\154\154\322\273\352\066\126\207\115\272\126\011\073\056\177\000" +
"\013\156\163\126\002\347\227\366\251\322\312\015\154\266\176\300" +
"\202\333\234\225\300\371\245\175\252\264\162\003\233\255\357\261" +
"\340\066\147\045\160\176\230\270\261\123\250\310\022\170\326\337" +
"\162\342\126\326\211\153\013\120\153\375\255\232\112\223\164\275" +
"\125\072\113\260\277\304\155\375\350\221\072\003\327\001\014\370" +
"\316\032\233\255\237\007\354\324\330\302\032\337\352\255\111\134" +
"\070\153\023\113\034\362\324\107\065\044\035\037\057\265\223\013" +
"\254\154\365\306\016\013\146\014\221\343\220\247\076\252\041\351" +
"\370\170\251\235\134\320\235\345\045\000\363\236\145\366\367\211" +
"\131\235\103\047\366\315\225\203\244\076\071\157\256\034\042\365" +
"\026\146\310\012\141\106\136\102\150\274\117\047\226\126\052\260" +
"\331\012\172\116\041\064\336\247\023\113\053\025\202\146\061\066" +
"\244\357\117\336\260\256\267\172\363\210\256\105\110\176\143\261" +
"\110\255\120\237\024\133\245\221\003\330\137\342\036\152\073\034" +
"\005\233\255\356\175\152\001\154\266\306\156\235\322\130\030\331" +
"\065\222\132\171\272\204\075\266\112\337\166\363\041\064\336\247" +
"\023\113\053\025\330\147\154\262\376\136\123\233\140\217\255\336" +
"\127\045\241\346\033\341\105\340\127\050\175\072\261\264\122\201" +
"\315\126\151\245\113\216\253\343\013\201\321\211\245\225\012\154" +
"\266\202\276\006\032\032\357\323\211\245\225\012\154\266\202\146" +
"\344\103\343\175\072\261\264\122\341\177\372\022\052\127\171\263" +
"\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\305\126\275\112\303\140" +
"\024\275\111\023\232\024\012\242\223\333\027\210\120\227\222\241" +
"\202\326\115\034\212\270\071\026\241\025\004\043\052\265\111\244" +
"\203\164\325\311\305\305\261\205\072\351\342\340\163\370\004\076" +
"\202\217\040\230\100\135\344\073\037\234\020\060\103\063\234\336" +
"\237\234\173\356\317\353\227\270\331\130\326\372\207\347\303\233" +
"\141\073\113\343\213\366\136\234\036\235\246\273\227\353\055\347" +
"\343\141\177\144\213\114\106\042\362\224\214\145\345\357\277\256" +
"\276\357\247\073\235\227\240\046\126\137\234\223\070\115\122\261" +
"\373\007\223\121\356\264\170\053\347\175\365\055\173\374\365\141" +
"\311\362\231\044\327\062\025\073\053\176\335\045\120\273\353\236" +
"\315\164\200\130\221\247\264\026\315\371\347\326\177\131\140\127" +
"\365\156\113\353\012\306\340\201\232\257\216\365\131\371\063\220" +
"\156\316\356\263\076\206\362\066\252\261\050\200\200\244\235\166" +
"\225\263\313\245\213\053\010\055\220\022\015\022\125\336\266\026" +
"\160\025\052\255\152\040\300\107\000\210\141\205\215\026\027\003" +
"\112\264\102\300\124\132\016\200\065\307\215\123\357\156\262\003" +
"\200\316\312\122\275\005\331\070\021\054\055\050\224\013\047\203" +
"\362\006\344\007\016\172\172\112\160\272\241\007\164\305\002\170" +
"\054\101\200\236\273\045\046\265\245\232\164\017\352\111\254\162" +
"\266\347\061\302\352\000\255\104\013\371\160\256\240\005\326\156" +
"\211\011\207\164\025\054\232\267\134\177\140\013\070\220\141\241" +
"\102\334\203\220\253\016\107\073\134\054\150\274\226\330\121\320" +
"\025\175\024\031\110\244\127\221\211\022\170\176\040\332\141\073" +
"\353\203\343\030\301\014\110\024\012\016\002\160\101\102\022\361" +
"\112\345\107\270\101\242\021\327\234\045\056\031\176\177\320\355" +
"\314\003\156\300\056\341\122\355\014\057\113\124\050\222\304\022" +
"\175\116\357\017\050\006\343\031\305\161\145\000\320\227\323\073" +
"\252\260\320\146\205\157\070\063\127\077\024\074\237\223\070\017" +
"\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\214\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\025\046\301\250\300\241\060\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\210\044\000\046\367\307\234\070\017\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\214\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\165\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\124\142\124\142\124\142\124\142\124\142\220\112" +
"\000\000\133\025\061\100\070\017\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\312\041\016\202\120" +
"\000\307\341\377\336\360\004\132\250\044\323\013\026\002\315\021" +
"\155\104\022\301\200\323\355\301\336\163\044\216\100\366\020\026" +
"\117\342\146\066\333\364\014\122\150\156\146\266\337\227\277\353" +
"\133\213\320\152\125\226\273\103\165\256\154\360\365\321\156\153" +
"\137\354\175\026\077\076\267\364\365\334\030\251\163\222\056\143" +
"\134\376\170\247\170\035\335\207\334\115\057\161\177\205\106\275" +
"\014\227\313\345\162\271\134\056\227\313\345\162\271\134\056\227" +
"\313\345\162\271\134\056\167\316\367\013\173\074\374\214\153\032" +
"\000\000"
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
        GRAMMAR_SYMBOL_COUNT = 73;
        SYMBOL_COUNT = 153;
        PARSER_STATE_COUNT = 152;
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
