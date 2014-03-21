/*
 * Built at Fri Mar 21 12:24:20 EDT 2014
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
        public java.lang.Object runSemanticAction_73()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_74()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_75()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object mer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = mer;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_76()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_77()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_78()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ter = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = ter;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_79()
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
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_84()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object type = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_85()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ClassDeclaration((String)id, "", "",
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_86()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = new ClassDeclaration((String)id, "", "", null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_87()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object after = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_88()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
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
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
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
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object fullType = (java.lang.Object) _children[3];
            java.lang.Object body = (java.lang.Object) _children[4];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, false, null);
            return RESULT;
        }
        public java.lang.Object runSemanticAction_93()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = aer; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_94()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object pi = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = pi; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_97()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object re = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             ((LinkedList<NameBinding>)re).addFirst(new NameBindingImpl((String)id, (Type)ta)); RESULT = re; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_98()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             LinkedList<NameBinding> llnb = new LinkedList<NameBinding>(); llnb.add(new NameBindingImpl((String)id, (Type)ta)); RESULT = llnb; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_99()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object first = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_100()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object el = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = el; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_101()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object type = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner, (Type)type); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_102()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
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
            java.lang.Object name = (java.lang.Object) _children[2];
            java.lang.Object argNames = (java.lang.Object) _children[3];
            java.lang.Object fullType = (java.lang.Object) _children[4];
            java.lang.Object body = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, true, null);
            return RESULT;
        }
        public java.lang.Object runSemanticAction_105()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object cds = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_106()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rest = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rest; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_107()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_108()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
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
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_112()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_113()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_114()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_115()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_116()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_117()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object de = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = de; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ip = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ip; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new LinkedList<NameBinding>(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object nr = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = nr; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = vd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_125()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_126()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_127()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object type = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_128()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new Variable(new NameBindingImpl((String)id, null), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_129()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object t = (java.lang.Object) _children[2];
            java.lang.Object inner = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_130()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_131()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object tgt = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object op = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)src,(String)op, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_133()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object lit = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DSLLit((String)lit); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_134()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new IntegerConstant((Integer)res); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_135()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new New(new HashMap<String,TypedAST>(), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_136()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_137()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = UnitVal.getInstance(null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_138()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Arrow((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_139()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Tuple((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_140()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_141()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new UnresolvedType((String)id); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_142()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ty = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ty; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_143()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_144()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_145()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object md = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_146()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, (DeclSequence)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_147()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_148()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_149()
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
"\116\352\170\167\251\370\017\276\002\176\206\237\100\074\360\017" +
"\330\236\223\302\213\347\234\170\146\074\347\344\373\037\066\332" +
"\130\366\374\363\273\133\276\345\107\232\233\365\321\225\263\312" +
"\254\137\376\370\371\351\327\357\027\137\337\364\031\373\322\060" +
"\306\276\071\066\230\177\174\355\330\341\053\051\244\161\227\262" +
"\341\312\056\234\143\023\174\210\345\271\351\312\375\017\162\247" +
"\225\221\361\363\125\303\113\331\206\162\217\133\133\357\102\365" +
"\250\324\274\155\337\356\104\150\236\224\272\156\345\005\267\322" +
"\104\126\131\353\272\253\252\212\107\303\120\221\371\130\360\366" +
"\046\024\317\204\054\125\305\365\271\161\162\055\021\107\310\025" +
"\271\116\204\332\052\021\043\214\104\355\200\255\216\127\362\156" +
"\303\065\042\255\114\227\102\205\364\152\245\340\164\250\326\246" +
"\266\122\320\044\216\035\124\322\161\301\035\047\376\270\332\350" +
"\350\372\064\024\152\361\137\302\211\221\073\142\075\256\033\151" +
"\036\106\033\067\172\023\237\075\150\157\152\353\260\355\250\160" +
"\017\253\331\163\112\043\366\276\273\157\144\067\315\226\353\177" +
"\245\245\162\164\165\175\166\171\355\130\377\154\356\217\367\363" +
"\260\330\245\337\212\136\326\342\336\137\207\146\025\321\313\011" +
"\155\300\260\175\307\172\042\156\254\243\017\042\271\347\147\355" +
"\257\112\337\256\312\324\207\120\046\314\257\125\210\246\032\156" +
"\171\345\225\375\320\116\272\215\170\256\261\336\153\124\057\157" +
"\113\217\103\217\324\052\302\160\075\251\303\100\274\365\336\275" +
"\046\054\203\314\206\115\124\017\232\160\366\303\061\164\061\312" +
"\320\111\133\171\275\333\064\132\206\326\313\303\202\072\227\121" +
"\250\004\175\361\163\170\217\070\347\040\116\071\216\313\271\360" +
"\272\105\063\313\000\123\300\014\220\003\012\300\111\204\354\064" +
"\302\364\030\220\000\040\237\246\000\270\044\220\047\136\076\360" +
"\037\343\071\213\047\056\122\130\244\260\110\351\225\064\062\222" +
"\170\202\120\020\035\256\051\262\345\210\121\200\062\003\037\057" +
"\121\116\360\122\360\062\360\062\274\225\301\061\303\223\031\011" +
"\020\076\303\050\031\315\200\273\344\064\072\043\033\010\051\350" +
"\051\272\004\306\011\076\046\307\221\210\247\023\142\220\041\255" +
"\226\162\203\222\243\313\141\222\303\044\207\056\207\056\247\261" +
"\221\074\107\362\034\351\012\350\012\350\012\350\012\350\012\350" +
"\246\020\114\041\230\322\077\244\073\332\057\301\311\137\212\234" +
"\153\113\370\004\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\121\313\156\023\061" +
"\024\235\114\222\066\005\132\220\130\263\143\135\165\036\231\151" +
"\305\012\211\012\125\074\204\250\304\006\251\221\063\166\122\127" +
"\036\217\353\161\022\052\376\203\257\200\237\341\047\020\013\376" +
"\001\333\347\116\141\343\163\257\347\234\343\163\357\174\377\223" +
"\114\067\066\171\372\371\355\015\333\262\143\305\364\372\370\322" +
"\131\251\327\057\176\374\374\364\353\367\263\257\257\323\044\371" +
"\142\222\044\371\346\222\321\163\227\034\275\022\134\150\367\121" +
"\030\046\355\302\271\144\206\213\130\136\350\241\074\170\057\166" +
"\112\152\021\257\057\015\153\104\037\312\175\146\155\267\013\325" +
"\203\106\261\276\177\263\343\241\171\324\250\256\027\037\230\025" +
"\072\262\232\116\165\103\325\266\054\032\206\212\314\367\070\353" +
"\257\103\361\204\213\106\266\114\135\150\047\326\002\161\270\130" +
"\221\353\214\313\255\344\061\302\224\167\016\330\253\370\111\334" +
"\156\230\102\244\225\036\122\310\220\136\256\044\234\216\344\132" +
"\167\126\160\232\304\045\207\255\160\214\063\307\210\277\327\156" +
"\124\164\175\034\012\271\370\057\341\114\213\035\261\036\166\106" +
"\350\373\321\366\214\332\304\147\017\373\353\316\072\354\072\052" +
"\334\375\152\366\235\124\210\175\340\356\214\030\246\331\062\365" +
"\257\264\124\216\256\134\222\276\074\367\307\273\363\260\324\245" +
"\337\210\132\166\374\316\317\032\232\125\104\057\045\264\001\303" +
"\346\275\224\307\155\015\364\161\044\217\374\234\351\252\361\355" +
"\252\311\175\000\251\303\354\112\206\130\322\060\313\132\257\114" +
"\103\073\033\266\341\271\332\172\257\151\267\274\151\074\116\074" +
"\122\053\011\303\347\131\027\206\141\275\367\036\231\260\010\062" +
"\233\230\250\036\233\160\246\341\230\270\030\145\342\204\155\275" +
"\336\155\214\022\241\365\362\260\234\301\145\032\052\116\067\176" +
"\016\357\021\347\034\307\051\323\053\277\231\311\302\314\013\100" +
"\011\230\003\052\100\015\070\215\120\234\105\050\117\000\031\000" +
"\362\062\007\300\045\203\074\363\362\261\277\214\347\074\236\370" +
"\220\303\042\207\105\116\257\344\221\221\305\023\204\232\350\160" +
"\315\221\255\102\214\032\224\071\370\170\211\162\202\227\203\127" +
"\200\127\340\255\002\216\005\236\054\110\200\360\005\106\051\150" +
"\006\174\313\316\242\063\262\201\220\203\236\243\313\140\234\341" +
"\062\073\211\104\074\235\021\203\014\151\265\224\033\224\012\135" +
"\005\223\012\046\025\164\025\164\025\215\215\344\025\222\127\110" +
"\127\103\127\103\127\103\127\103\127\103\127\102\120\102\120\322" +
"\077\244\157\264\137\202\323\277\022\122\057\210\356\004\000\000" +
""
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\246\061\014\022\240\060\110\261\003\003\003" +
"\023\020\063\103\061\043\032\033\131\216\025\210\131\240\230\025" +
"\052\306\004\125\307\210\246\216\021\015\263\040\231\205\314\007" +
"\141\066\044\163\030\320\364\241\213\043\313\243\333\317\344\200" +
"\335\136\020\315\216\244\236\031\115\015\262\070\066\277\303\324" +
"\301\374\014\016\003\000\372\226\224\167\163\002\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\205\303\271\011\002\001" +
"\000\004\300\345\300\076\304\347\374\317\377\377\316\037\003\155" +
"\300\304\022\104\120\154\310\314\310\320\222\004\173\160\203\015" +
"\026\023\007\346\361\101\342\162\106\160\330\356\136\307\360\372" +
"\176\336\003\340\166\002\260\117\002\051\116\333\214\315\332\220" +
"\163\234\347\202\026\155\111\313\134\341\210\253\134\343\272\066" +
"\264\311\055\155\163\307\166\155\317\366\165\240\103\035\351\130" +
"\047\166\312\361\237\063\235\377\134\360\322\256\164\315\233\057" +
"\052\275\232\346\123\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\135\131\267\024\065" +
"\020\316\001\161\301\005\121\024\301\255\334\160\305\175\027\265" +
"\335\275\310\025\367\355\272\134\024\305\135\100\057\342\006\270" +
"\201\054\212\202\313\057\360\315\047\037\375\075\076\171\216\317" +
"\276\232\314\114\101\045\267\052\335\231\111\046\231\231\376\316" +
"\311\251\356\257\252\277\344\324\351\351\351\116\322\351\077\376" +
"\125\213\346\266\252\205\063\063\123\313\377\373\173\375\212\203" +
"\173\327\054\120\152\373\146\245\324\157\232\137\060\063\065\375" +
"\327\354\252\155\377\374\371\073\322\017\253\026\136\314\155\121" +
"\073\224\316\226\077\123\240\072\061\034\277\120\227\143\310\376" +
"\242\236\075\126\227\343\230\370\343\165\071\201\341\027\353\162" +
"\242\056\047\365\366\117\326\345\024\046\156\211\056\247\366\266" +
"\227\352\162\232\056\247\367\366\227\071\261\147\010\155\076\223" +
"\154\057\327\345\254\336\366\012\046\166\245\056\147\353\162\116" +
"\263\114\365\003\255\176\156\154\315\234\070\232\251\112\251\335" +
"\206\161\055\102\362\033\213\105\252\205\372\244\330\072\215\334" +
"\110\172\116\235\027\133\063\047\254\163\152\326\060\256\105\110" +
"\176\143\261\110\265\120\237\024\133\247\221\033\111\317\251\363" +
"\143\153\346\204\165\116\355\062\214\153\021\222\337\130\054\122" +
"\055\324\047\305\326\151\344\206\225\251\075\206\161\055\102\362" +
"\033\213\105\252\205\372\244\330\072\215\334\030\354\327\007\372" +
"\176\312\331\357\353\176\212\211\143\357\247\230\070\040\333\313" +
"\304\300\006\000\346\176\212\042\351\165\352\202\330\232\071\221" +
"\064\123\027\306\326\314\211\244\231\272\050\266\146\116\130\127" +
"\364\227\014\343\132\204\313\203\122\027\163\161\115\240\217\275" +
"\304\345\214\116\077\132\303\202\225\251\107\014\343\132\204\344" +
"\067\026\224\132\345\253\105\373\057\345\264\134\216\343\113\201" +
"\225\251\317\015\203\226\202\343\220\247\076\252\041\351\370\170" +
"\251\236\022\140\145\152\316\060\150\133\330\260\062\265\303\060" +
"\150\051\070\016\171\352\243\032\222\216\217\227\352\051\001\126" +
"\246\076\062\014\332\026\066\216\146\152\066\167\123\012\207\165" +
"\116\175\153\030\327\042\044\277\261\130\244\132\250\117\212\255" +
"\323\310\015\053\123\317\033\006\155\013\033\126\246\136\316\335" +
"\232\222\141\145\352\071\303\240\155\002\210\330\227\000\015\306" +
"\146\230\143\056\043\333\356\330\314\345\065\307\136\341\354\263" +
"\175\011\060\157\154\246\122\352\063\343\101\353\104\263\243\130" +
"\046\026\310\050\026\325\340\164\044\136\153\054\226\174\116\334" +
"\022\262\275\324\027\333\004\132\343\112\350\215\142\011\376\225" +
"\270\155\145\352\121\303\270\026\041\371\215\065\105\253\136\045" +
"\325\110\265\164\334\152\127\233\352\110\032\303\000\050\165\265" +
"\344\263\062\365\205\141\320\122\160\034\362\324\107\065\044\035" +
"\037\057\325\123\002\106\177\014\231\370\232\216\041\137\103\266" +
"\043\217\041\303\170\145\352\132\262\075\140\246\264\347\072\135" +
"\256\347\152\232\124\244\353\363\224\000\112\335\060\254\272\074" +
"\155\270\061\364\230\260\114\351\032\156\012\255\141\024\001\112" +
"\335\354\162\315\063\245\217\276\045\101\243\106\006\342\165\352" +
"\126\135\156\313\322\244\102\301\147\252\122\352\103\056\032\032" +
"\374\372\244\143\113\000\050\165\173\303\270\073\134\256\275\116" +
"\161\000\245\326\270\134\231\243\355\275\330\073\003\333\122\360" +
"\150\073\244\315\324\135\201\155\231\320\114\365\321\226\101\063" +
"\165\267\317\337\146\212\034\137\371\374\126\137\302\166\303\240" +
"\155\141\303\312\324\047\206\101\333\302\206\225\251\147\015\203" +
"\266\205\015\053\123\237\032\006\155\023\270\261\124\203\323\221" +
"\370\072\137\011\110\072\177\352\236\330\232\071\021\364\204\174" +
"\057\303\345\236\347\171\037\331\136\326\263\367\327\035\047\150" +
"\065\274\237\252\224\372\332\060\256\105\110\176\143\261\110\265" +
"\120\237\024\133\247\221\033\126\246\336\060\014\332\026\066\254" +
"\114\155\062\014\332\026\066\346\137\247\052\245\336\161\243\100" +
"\251\007\044\005\355\173\060\106\113\264\316\103\300\134\057\065" +
"\067\245\313\132\350\143\146\243\076\146\235\056\323\320\035\217" +
"\134\077\110\373\254\163\352\031\303\240\245\340\070\344\251\217" +
"\152\110\072\076\136\252\247\004\004\375\367\075\026\242\254\343" +
"\037\357\263\121\175\103\327\371\104\052\355\266\047\217\003\050" +
"\365\244\313\045\275\363\174\112\340\237\216\135\127\114\200\160" +
"\005\250\317\024\264\317\201\035\130\127\364\257\014\343\132\204" +
"\344\067\026\213\124\013\365\111\261\165\032\271\061\370\257\017" +
"\152\146\246\101\206\331\220\272\316\027\002\343\147\030\356\105" +
"\272\157\235\123\207\015\203\226\202\343\220\247\076\252\041\351" +
"\370\170\251\236\022\040\216\214\366\375\246\017\014\141\276\250" +
"\256\343\225\324\165\270\020\107\106\267\366\253\070\310\261\045" +
"\143\144\317\251\331\324\165\270\260\256\123\153\015\343\132\204" +
"\344\257\272\317\144\241\157\255\255\165\143\014\307\361\245\300" +
"\312\324\224\141\134\213\220\374\125\367\011\066\064\123\123\156" +
"\214\341\070\276\024\130\231\232\066\214\153\021\222\337\330\252" +
"\373\274\356\233\073\174\104\013\272\163\207\247\271\030\216\037" +
"\046\240\351\334\341\165\206\161\055\102\362\033\133\165\173\067" +
"\174\231\072\242\005\335\114\255\343\142\070\176\230\200\246\231" +
"\372\306\060\256\105\110\176\143\261\110\265\120\237\024\133\247" +
"\221\033\126\246\366\032\306\265\010\311\157\054\026\251\026\352" +
"\223\142\353\064\162\243\371\323\114\245\324\106\143\101\251\015" +
"\211\033\125\044\202\172\362\136\115\337\236\162\021\345\011\371" +
"\265\170\355\051\027\111\173\362\066\306\326\314\211\244\231\172" +
"\075\266\146\116\044\315\324\130\215\260\262\343\175\357\207\050" +
"\204\306\373\164\142\151\245\000\233\251\167\103\024\102\343\175" +
"\072\261\264\122\200\315\324\173\041\012\241\361\076\235\130\132" +
"\051\060\021\243\355\233\040\352\150\073\121\177\163\020\305\161" +
"\005\173\116\155\016\121\200\202\317\051\242\141\316\251\267\372" +
"\075\336\200\315\324\333\203\050\216\053\302\356\247\052\245\076" +
"\116\333\236\162\321\316\340\340\000\314\357\052\250\057\141\336" +
"\177\342\044\141\144\107\261\206\176\207\312\136\321\277\307\202" +
"\373\234\225\300\371\245\143\352\264\112\102\073\177\312\005\010" +
"\317\011\354\071\365\003\026\334\347\254\004\316\017\302\223\257" +
"\346\077\250\151\170\061\310\262\262\104\320\235\155\051\110\372" +
"\353\333\022\133\063\047\332\373\051\016\300\314\327\141\257\123" +
"\077\015\261\121\043\003\066\123\077\032\013\005\257\020\221\003" +
"\154\257\113\273\106\054\203\211\134\251\153\216\154\017\351\273" +
"\130\220\377\115\310\155\144\273\200\225\045\052\245\176\226\170" +
"\352\303\155\227\227\342\135\136\252\247\004\064\316\324\057\022" +
"\117\175\270\355\362\122\274\313\113\365\224\200\221\377\365\161" +
"\357\041\367\325\333\010\041\357\041\177\147\030\327\042\044\277" +
"\261\130\244\132\250\117\212\255\323\310\215\244\117\063\143\265" +
"\352\311\304\256\275\030\274\042\313\304\255\376\326\150\215\017" +
"\150\127\177\153\010\140\326\324\146\237\373\006\032\101\034\127" +
"\130\377\175\235\136\333\212\351\275\345\070\344\251\217\152\110" +
"\072\076\136\252\247\004\260\347\324\310\364\330\016\023\101\263" +
"\254\073\163\207\241\340\057\062\245\104\360\150\373\266\332\240" +
"\061\205\165\235\352\274\167\133\071\357\337\112\034\362\324\107" +
"\065\044\035\037\057\325\123\002\202\106\333\373\376\162\000\264" +
"\243\130\036\300\350\216\214\262\137\061\153\063\345\002\224\332" +
"\311\361\145\366\272\364\331\226\101\373\074\167\371\374\023\373" +
"\204\374\145\350\061\361\063\005\005\257\070\062\010\332\325\112" +
"\172\361\334\152\045\326\352\206\326\375\324\041\303\240\245\340" +
"\070\344\251\217\152\110\072\076\136\252\247\004\260\043\243\305" +
"\276\013\234\023\362\257\017\012\376\076\143\016\004\075\041\117" +
"\304\273\241\022\046\366\056\141\167\350\061\155\357\060\007\120" +
"\152\217\313\211\263\254\373\036\167\203\210\153\036\150\255\175" +
"\261\264\006\105\361\231\332\037\113\153\120\044\175\102\316\261" +
"\362\351\201\124\332\154\077\372\001\054\270\317\131\011\234\137" +
"\072\246\116\253\044\260\231\332\217\005\367\071\053\201\363\113" +
"\307\324\151\225\004\066\123\373\260\340\076\147\045\160\176\230" +
"\250\076\117\250\171\307\005\074\353\117\071\161\253\233\304\345" +
"\000\064\132\177\252\241\322\244\334\117\315\173\243\245\314\076" +
"\117\150\076\323\354\040\331\036\362\074\317\212\324\336\342\050" +
"\254\376\251\116\157\126\305\364\152\161\034\362\324\107\065\044" +
"\035\037\057\325\123\002\254\114\165\372\011\053\246\277\220\343" +
"\220\247\076\252\041\351\370\170\251\236\022\320\136\321\071\100" +
"\357\215\030\212\342\237\373\212\171\333\151\042\337\006\071\104" +
"\266\047\347\155\220\303\144\173\370\167\011\363\276\213\341\103" +
"\150\274\117\047\226\126\012\260\231\142\347\023\110\010\215\367" +
"\351\304\322\112\201\240\021\207\015\351\333\123\056\254\373\251" +
"\316\130\275\153\021\222\337\130\054\122\055\324\047\305\326\151" +
"\344\006\373\353\333\111\155\213\056\330\114\215\325\072\234\261" +
"\060\262\053\340\014\375\215\111\366\234\012\372\136\131\150\274" +
"\117\047\226\126\012\260\063\070\212\175\157\063\047\330\163\252" +
"\263\232\043\050\365\153\023\205\152\300\325\037\251\116\054\255" +
"\024\140\063\065\157\225\112\216\153\342\013\201\321\211\245\225" +
"\002\154\246\202\106\064\103\343\175\072\261\264\122\200\315\124" +
"\320\127\062\103\343\175\072\261\264\122\340\177\162\305\364\060" +
"\357\257\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\305\126\275\112\303\120" +
"\030\375\322\066\064\055\024\104\047\267\033\210\020\227\322\241" +
"\202\306\115\034\212\270\071\006\241\025\004\053\052\261\115\044" +
"\203\164\325\311\305\301\065\205\072\351\342\340\163\370\004\076" +
"\202\217\040\230\100\135\344\236\013\047\004\354\320\016\247\337" +
"\317\075\337\371\176\136\277\304\116\046\262\026\036\236\217\156" +
"\106\335\044\036\137\164\367\306\361\321\151\274\173\271\356\067" +
"\076\036\366\243\232\110\032\211\310\323\164\042\053\177\377\165" +
"\365\175\077\333\351\277\270\165\261\102\151\234\214\343\151\054" +
"\265\360\040\215\162\247\305\257\152\274\257\276\045\217\277\076" +
"\054\131\176\322\351\265\314\244\226\024\337\366\022\250\337\005" +
"\147\231\016\020\253\347\050\255\105\147\376\271\365\137\026\330" +
"\125\063\360\265\256\140\014\036\250\267\324\261\076\253\126\006" +
"\322\315\331\175\326\307\120\316\106\065\026\005\340\222\264\323" +
"\256\162\166\271\164\161\005\241\005\122\242\101\242\312\331\326" +
"\002\266\102\245\125\155\004\264\020\000\142\130\136\333\347\142" +
"\100\211\126\010\230\112\313\001\260\346\270\161\232\301\046\073" +
"\000\350\254\054\065\130\220\215\323\203\245\005\205\262\341\144" +
"\120\316\220\174\340\160\240\247\004\247\353\071\100\127\054\200" +
"\307\022\004\350\271\133\142\122\133\252\103\367\240\236\304\052" +
"\147\173\036\303\253\016\320\112\264\220\017\347\012\132\140\355" +
"\226\230\160\110\127\356\242\163\313\365\007\266\200\003\031\267" +
"\032\174\007\244\304\351\163\264\303\305\202\306\153\211\035\005" +
"\135\321\107\221\201\104\172\025\231\050\201\347\007\242\035\266" +
"\263\076\070\216\341\146\100\242\120\160\020\200\013\322\100\042" +
"\002\370\021\156\220\150\217\153\316\022\227\014\277\077\350\166" +
"\346\001\333\145\227\260\351\304\341\264\153\052\024\111\142\211" +
"\076\247\367\007\024\203\361\214\342\106\037\176\040\275\212\012" +
"\013\155\160\174\252\231\051\371\001\206\214\361\150\037\017\000" +
"\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\364\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\025\046\301\250\300\241\060\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\101\177\011\000\177\324\250\141\037\017\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\364\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\165\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\124\142\124\142\124\142\124\142\124\142\140\045" +
"\000\350\370\174\243\037\017\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\312\041\016\202\120" +
"\000\307\341\377\336\360\004\132\250\044\323\013\026\002\315\021" +
"\155\104\022\301\200\323\355\301\336\163\044\216\100\365\022\026" +
"\117\342\146\066\333\364\014\122\150\156\126\302\357\313\337\365" +
"\255\105\150\265\052\313\335\241\072\127\066\370\372\150\267\265" +
"\057\366\076\213\037\237\133\372\172\156\214\324\071\111\227\061" +
"\056\177\274\123\274\216\356\103\356\246\227\270\277\102\243\136" +
"\206\313\345\162\271\134\056\227\313\345\162\271\134\056\227\313" +
"\345\162\271\134\056\227\073\267\373\005\221\350\021\373\077\032" +
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
"\103\105\001\003\003\103\112\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\104" +
"\011\060\202\004\031\004\210\125\311\102\271\021\014\014\015\330" +
"\045\034\160\353\120\040\336\122\002\022\014\114\304\072\223\221" +
"\172\256\121\140\220\303\141\024\043\251\106\261\340\220\340\300" +
"\035\170\054\270\044\004\250\026\252\015\070\055\147\120\300\052" +
"\041\100\126\142\045\071\262\161\330\116\124\142\305\145\046\007" +
"\321\052\111\116\047\034\144\073\213\250\360\044\067\225\342\224" +
"\300\023\360\016\130\045\024\110\167\025\311\221\113\315\244\105" +
"\215\140\145\242\044\045\221\353\160\046\032\373\231\170\255\344" +
"\046\072\334\361\356\200\125\202\021\227\253\230\350\342\365\006" +
"\352\104\036\325\302\025\227\004\043\351\106\001\075\107\255\074" +
"\353\000\224\000\000\353\166\161\247\024\011\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\112\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\243" +
"\002\243\002\103\125\200\021\044\310\040\060\150\334\063\052\060" +
"\002\243\024\247\213\111\227\030\015\100\262\265\002\000\031\230" +
"\010\126\104\007\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\315\224\261\112\303\120" +
"\024\206\377\173\173\013\216\105\227\272\310\165\163\352\344\042" +
"\056\101\234\304\101\160\314\124\321\041\242\022\233\033\311\124" +
"\062\166\352\342\143\350\342\320\347\350\223\370\010\032\157\013" +
"\056\162\176\341\224\332\070\044\341\362\345\376\347\162\316\227" +
"\274\276\243\133\216\260\223\236\337\016\237\206\203\062\144\167" +
"\203\223\054\134\336\204\343\373\335\003\067\237\236\346\026\250" +
"\162\000\327\305\010\275\237\157\075\174\114\306\107\207\057\373" +
"\035\230\024\356\052\013\105\200\115\317\252\074\206\056\236\336" +
"\315\266\337\312\347\357\014\124\305\043\306\260\345\342\336\215" +
"\153\023\257\116\323\064\237\022\100\217\354\130\022\031\000\136" +
"\004\216\105\325\064\312\022\140\060\221\101\102\217\013\057\003" +
"\113\213\367\151\224\225\001\357\225\321\236\012\027\154\207\307" +
"\036\211\062\164\120\265\014\034\022\021\154\361\046\072\031\370" +
"\130\204\024\117\144\341\270\076\265\326\053\332\366\065\002\275" +
"\355\172\251\071\210\023\041\237\301\337\333\276\221\356\352\155" +
"\247\200\332\016\142\273\347\063\127\273\333\246\242\172\260\202" +
"\242\152\023\327\010\250\273\377\263\273\053\110\115\335\045\046" +
"\032\146\242\245\212\266\352\025\035\155\273\363\140\155\347\277" +
"\127\165\024\337\361\053\370\002\121\232\063\263\034\012\000\000" +
""
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\321\071\112\005\101" +
"\020\006\340\171\243\317\175\337\367\335\347\162\023\003\117\140" +
"\342\021\104\120\274\220\231\221\241\107\022\274\203\377\100\013" +
"\023\250\214\142\044\137\303\107\125\365\122\335\320\117\157\125" +
"\377\356\266\252\257\056\056\137\256\007\367\257\317\217\165\125" +
"\075\334\124\275\252\031\347\035\034\307\162\253\076\355\170\256" +
"\153\357\046\366\076\131\233\215\341\130\215\303\330\216\335\330" +
"\213\371\030\217\205\070\372\306\144\061\323\352\273\025\047\161" +
"\126\352\376\057\255\377\160\177\163\327\120\353\035\037\163\233" +
"\045\337\210\375\130\211\351\057\172\254\305\142\114\225\272\056" +
"\161\047\346\142\244\324\007\061\126\362\245\022\107\313\175\203" +
"\230\370\303\077\004\000\000\000\000\000\000\000\000\000\000\000" +
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
"\000\340\237\171\007\265\173\127\215\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\232\311\156\024\061" +
"\020\206\255\204\260\357\133\010\353\014\113\130\002\303\276\007" +
"\110\330\007\010\201\260\005\206\045\054\017\000\110\101\234\270" +
"\163\342\051\270\161\342\310\363\160\102\342\314\225\152\311\214" +
"\172\051\167\333\161\331\343\062\375\113\237\354\056\117\227\177" +
"\131\156\167\247\340\333\157\061\060\373\136\364\167\072\355\301" +
"\077\077\047\207\276\174\036\355\023\342\343\133\041\304\033\210" +
"\367\165\332\023\077\146\206\077\374\372\376\365\137\170\104\060" +
"\322\354\073\361\111\200\363\256\353\244\337\017\314\003\006\144" +
"\177\076\260\100\366\027\002\213\200\305\300\022\140\051\260\014" +
"\130\056\307\127\000\053\201\125\362\172\265\154\327\310\066\141" +
"\255\154\327\001\353\145\177\020\330\000\014\001\033\201\115\162" +
"\216\315\132\256\267\000\133\201\155\262\265\245\101\224\047\103" +
"\301\165\242\146\256\337\054\211\347\143\330\030\026\307\356\053" +
"\233\057\043\324\065\245\266\273\110\352\334\265\023\071\335\041" +
"\073\024\161\372\035\262\223\030\352\234\273\222\326\170\207\014" +
"\153\376\316\126\273\313\006\043\332\327\072\332\063\307\061\222" +
"\071\274\234\327\173\221\070\375\171\255\353\172\237\246\153\176" +
"\157\031\047\052\270\036\221\354\117\365\165\070\240\210\267\014" +
"\363\150\021\311\132\263\220\227\063\344\040\022\017\373\151\074" +
"\144\171\377\141\054\130\357\220\312\363\372\110\305\175\145\363" +
"\125\273\016\121\107\323\027\154\134\147\124\273\166\252\143\351" +
"\013\057\117\343\161\044\336\273\157\076\323\063\044\035\077\101" +
"\356\072\170\005\357\372\044\026\014\336\065\252\110\352\174\247" +
"\200\323\300\031\331\332\322\310\135\237\245\310\133\160\075\052" +
"\147\072\047\333\040\251\153\252\376\204\272\076\237\352\363\130" +
"\153\236\325\311\020\135\137\250\164\075\106\014\145\316\156\256" +
"\202\353\161\142\056\023\346\272\010\134\112\372\377\101\115\365" +
"\012\022\017\343\014\321\315\022\226\153\236\325\311\253\222\153" +
"\251\276\016\327\025\361\126\356\272\155\230\027\045\222\067\172" +
"\113\162\043\325\327\341\246\341\357\255\320\172\032\157\051\342" +
"\341\074\215\324\262\255\251\116\140\301\340\135\353\127\202\175" +
"\234\327\267\015\347\253\166\035\274\042\162\155\262\103\046\221" +
"\373\342\372\016\271\243\270\357\056\211\353\220\244\127\061\343" +
"\131\173\342\351\232\147\305\214\142\255\247\210\326\172\112\065" +
"\026\374\323\210\252\340\372\036\160\037\170\040\133\133\032\104" +
"\171\062\170\071\257\037\042\161\332\267\114\210\265\247\352\212" +
"\331\030\061\224\071\273\271\042\251\075\325\256\155\134\077\142" +
"\351\332\154\255\023\065\163\375\146\111\074\037\303\306\260\070" +
"\166\137\331\174\031\325\025\263\036\126\314\170\256\065\205\353" +
"\151\042\327\323\252\261\202\353\307\222\047\251\276\016\035\105" +
"\274\145\230\107\213\110\276\124\023\371\070\371\236\032\316\127" +
"\355\232\122\376\252\223\163\135\353\147\212\061\367\157\031\033" +
"\327\275\173\067\326\256\375\271\346\131\305\341\371\077\210\170" +
"\272\256\353\041\376\236\306\361\012\236\153\374\046\215\237\277" +
"\300\170\176\137\363\374\027\151\236\337\327\074\327\072\121\057" +
"\317\220\027\212\371\062\042\165\075\103\340\332\315\311\147\112" +
"\134\265\247\227\206\363\145\024\334\276\246\163\375\212\245\153" +
"\027\153\375\232\245\153\253\265\376\013\342\223\343\045\101\107" +
"\000\000"
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
        GRAMMAR_SYMBOL_COUNT = 72;
        SYMBOL_COUNT = 150;
        PARSER_STATE_COUNT = 151;
        SCANNER_STATE_COUNT = 100;
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
