/*
 * Built at Fri Mar 21 17:53:22 EDT 2014
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
        cCurly_t(7),
        classKwd_t(8),
        closeParen_t(9),
        colon_t(10),
        comma_t(11),
        comment_t(12),
        dash_t(13),
        decimalInteger_t(14),
        defKwd_t(15),
        divide_t(16),
        dot_t(17),
        dslLine_t(18),
        dslWhitespace_t(19),
        equals_t(20),
        fnKwd_t(21),
        identifier_t(22),
        ignoredNewline(23),
        metadataKwd_t(24),
        mult_t(25),
        multi_comment_t(26),
        newKwd_t(27),
        notCurly_t(28),
        oCurly_t(29),
        openParen_t(30),
        plus_t(31),
        shortString_t(32),
        tarrow_t(33),
        tilde_t(34),
        typeKwd_t(35),
        valKwd_t(36),
        varKwd_t(37);

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
	cl = 0;
	nextDsl = false;
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
            case 111:
                RESULT = runSemanticAction_111();
                break;
            case 112:
                RESULT = runSemanticAction_112();
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
            case 153:
                RESULT = runSemanticAction_153();
                break;
            case 154:
                RESULT = runSemanticAction_154();
                break;
            case 155:
                RESULT = runSemanticAction_155();
                break;
            case 156:
                RESULT = runSemanticAction_156();
                break;
            case 157:
                RESULT = runSemanticAction_157();
                break;
            case 158:
                RESULT = runSemanticAction_158();
                break;
            case 159:
                RESULT = runSemanticAction_159();
                break;
            case 160:
                RESULT = runSemanticAction_160();
                break;
            case 161:
                RESULT = runSemanticAction_161();
                break;
            case 162:
                RESULT = runSemanticAction_162();
                break;
            case 163:
                RESULT = runSemanticAction_163();
                break;
            case 164:
                RESULT = runSemanticAction_164();
                break;
            case 165:
                RESULT = runSemanticAction_165();
                break;
            case 166:
                RESULT = runSemanticAction_166();
                break;
            case 167:
                RESULT = runSemanticAction_167();
                break;
            case 168:
                RESULT = runSemanticAction_168();
                break;
            case 169:
                RESULT = runSemanticAction_169();
                break;
            case 170:
                RESULT = runSemanticAction_170();
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
            case 7:
                RESULT = runSemanticAction_7(lexeme);
                break;
            case 9:
                RESULT = runSemanticAction_9(lexeme);
                break;
            case 14:
                RESULT = runSemanticAction_14(lexeme);
                break;
            case 18:
                RESULT = runSemanticAction_18(lexeme);
                break;
            case 19:
                RESULT = runSemanticAction_19(lexeme);
                break;
            case 22:
                RESULT = runSemanticAction_22(lexeme);
                break;
            case 29:
                RESULT = runSemanticAction_29(lexeme);
                break;
            case 30:
                RESULT = runSemanticAction_30(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
            return RESULT;
        }
        public java.lang.Object runSemanticAction_83()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_84()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_85()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object mer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = mer;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_86()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_87()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_88()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ter = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = ter;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_89()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
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
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_94()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object type = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_95()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ClassDeclaration((String)id, "", "",
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_96()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = new ClassDeclaration((String)id, "", "", null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_97()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object after = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_98()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_99()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_100()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_101()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_102()
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
        public java.lang.Object runSemanticAction_111()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = aer; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_112()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object pi = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = pi; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object re = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             ((LinkedList<NameBinding>)re).addFirst(new NameBindingImpl((String)id, (Type)ta)); RESULT = re; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             LinkedList<NameBinding> llnb = new LinkedList<NameBinding>(); llnb.add(new NameBindingImpl((String)id, (Type)ta)); RESULT = llnb; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object first = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object el = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = el; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object type = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner, (Type)type); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_125()
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
        public java.lang.Object runSemanticAction_126()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object cds = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_127()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rest = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rest; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_128()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_129()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_130()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_131()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_133()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_134()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_135()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_136()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_137()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_138()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object de = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = de; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_139()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ip = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ip; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_140()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new LinkedList<NameBinding>(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_141()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_142()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object nr = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = nr; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_143()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_144()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = vd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_145()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_146()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_147()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_148()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object type = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_149()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new Variable(new NameBindingImpl((String)id, null), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_150()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object t = (java.lang.Object) _children[2];
            java.lang.Object inner = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_151()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_152()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object tgt = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_153()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object op = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)src,(String)op, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_154()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object lit = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DSLLit((String)lit); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_155()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new IntegerConstant((Integer)res); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_156()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new New(new HashMap<String,TypedAST>(), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_157()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_158()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = UnitVal.getInstance(null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_159()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Arrow((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_160()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Tuple((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_161()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_162()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new UnresolvedType((String)id); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_163()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ty = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ty; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_164()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_165()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_166()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object md = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_167()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, (DeclSequence)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_168()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_169()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_170()
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
        public java.lang.Object runSemanticAction_7(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             cl--; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_9(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
              parenLevel--; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_14(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
 		RESULT = Integer.parseInt(lexeme);
 	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_18(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             nextDsl = false; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_19(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             System.out.println("ws"); nextDsl = true; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_22(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
 		RESULT = lexeme;
 	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_29(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             cl++; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_30(final String lexeme)
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
            else if(match.terms.equals(disambiguationGroups[7])) return disambiguate_7(lexeme);
            else if(match.terms.equals(disambiguationGroups[8])) return disambiguate_8(lexeme);
            else if(match.terms.equals(disambiguationGroups[9])) return disambiguate_9(lexeme);
            else if(match.terms.equals(disambiguationGroups[10])) return disambiguate_10(lexeme);
            else if(match.terms.equals(disambiguationGroups[11])) return disambiguate_11(lexeme);
            else if(match.terms.equals(disambiguationGroups[12])) return disambiguate_12(lexeme);
            else if(match.terms.equals(disambiguationGroups[13])) return disambiguate_13(lexeme);
            else if(match.terms.equals(disambiguationGroups[14])) return disambiguate_14(lexeme);
            else if(match.terms.equals(disambiguationGroups[15])) return disambiguate_15(lexeme);
            else return -1;
        }
        public int disambiguate_0(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Spaces_t = 5;
            @SuppressWarnings("unused") final int notCurly_t = 28;
            
		if (cl > 0) return notCurly_t;
		return Spaces_t;
	
        }
        public int disambiguate_1(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int comment_t = 12;
            @SuppressWarnings("unused") final int notCurly_t = 28;
            
		if (cl > 0) return notCurly_t;
		return comment_t;
	
        }
        public int disambiguate_2(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int multi_comment_t = 26;
            @SuppressWarnings("unused") final int notCurly_t = 28;
            
		if (cl > 0) return notCurly_t;
		return multi_comment_t;
	
        }
        public int disambiguate_3(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            @SuppressWarnings("unused") final int notCurly_t = 28;
            
		if (cl > 0) return notCurly_t;
		return ignoredNewline;
	
        }
        public int disambiguate_4(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int comment_t = 12;
            @SuppressWarnings("unused") final int dslLine_t = 18;
            
		if (nextDsl) return dslLine_t;
		return comment_t;
	
        }
        public int disambiguate_5(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int dslLine_t = 18;
            @SuppressWarnings("unused") final int multi_comment_t = 26;
            
		if (nextDsl) return dslLine_t;
		return dslLine_t;
	
        }
        public int disambiguate_6(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Spaces_t = 5;
            @SuppressWarnings("unused") final int dslLine_t = 18;
            
		if (nextDsl) return dslLine_t;
		return Spaces_t;
	
        }
        public int disambiguate_7(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int dslWhitespace_t = 19;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
		return dslWhitespace_t;
	
        }
        public int disambiguate_8(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int dslWhitespace_t = 19;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
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
			return dslWhitespace_t;
		}
	
        }
        public int disambiguate_9(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		else
		{
			return Newline_t;
		}
	
        }
        public int disambiguate_10(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
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
        public int disambiguate_11(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Dedent_t;
	
        }
        public int disambiguate_12(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Indent_t;
	
        }
        public int disambiguate_13(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int DedentRepair_t = 1;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
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
        public int disambiguate_14(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
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
        public int disambiguate_15(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int Newline_t = 4;
            @SuppressWarnings("unused") final int ignoredNewline = 23;
            
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
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\122\315\156\324\060" +
"\030\114\367\277\205\266\110\234\171\205\252\111\166\223\124\234" +
"\012\024\124\121\240\352\126\160\340\120\271\211\167\353\342\330" +
"\301\361\166\251\170\044\340\145\170\011\304\201\167\300\366\174" +
"\131\070\254\347\363\347\157\306\343\311\176\377\023\015\127\046" +
"\172\374\361\354\226\335\261\003\311\324\362\140\156\215\120\313" +
"\247\077\176\276\377\365\373\311\327\127\275\050\372\322\104\121" +
"\364\315\106\375\223\167\057\155\264\367\202\127\134\331\013\336" +
"\060\141\256\254\215\046\150\204\362\124\165\345\366\133\276\226" +
"\102\361\320\236\067\254\344\255\057\307\314\030\275\016\315\362" +
"\371\312\310\173\137\356\224\222\265\355\353\165\345\067\017\113" +
"\251\133\176\316\014\127\201\120\152\251\273\252\256\131\320\366" +
"\025\335\063\252\130\173\343\213\107\025\057\105\315\344\251\262" +
"\174\311\341\254\342\013\122\235\124\342\116\124\301\315\260\322" +
"\160\130\265\362\214\034\356\273\372\303\215\260\274\365\116\003" +
"\201\177\136\061\011\317\013\325\171\023\376\171\142\041\240\277" +
"\047\226\112\033\136\321\123\155\264\133\163\313\052\146\031\315" +
"\217\352\225\014\167\355\373\102\134\375\347\173\242\370\232\246" +
"\166\224\266\233\054\046\172\123\076\320\015\127\233\040\106\215" +
"\134\005\073\273\355\215\066\026\237\051\060\354\046\323\261\025" +
"\022\217\334\266\367\015\357\336\176\307\344\277\322\120\071\234" +
"\137\036\137\134\332\250\167\174\342\226\067\047\376\063\134\273" +
"\014\345\265\256\356\335\261\337\054\002\072\072\241\361\350\277" +
"\225\215\266\252\220\157\067\336\017\303\023\027\343\063\251\313" +
"\117\050\117\225\342\216\062\246\240\321\234\073\273\356\372\201" +
"\053\135\147\313\375\172\213\322\011\054\312\304\331\026\312\047" +
"\051\205\367\052\074\335\215\071\005\321\060\303\152\167\155\057" +
"\234\164\061\073\232\062\316\310\120\137\337\226\016\007\016\151" +
"\053\010\375\361\104\373\064\130\353\256\331\152\174\222\044\066" +
"\150\002\273\337\370\265\347\227\201\015\357\030\130\156\152\307" +
"\267\253\306\233\034\170\272\117\267\123\031\372\252\242\216\013" +
"\301\151\204\220\372\041\242\121\110\366\334\361\256\232\331\014" +
"\220\001\162\100\001\070\012\220\035\006\110\261\233\142\067\215" +
"\001\051\040\001\114\003\304\020\213\235\130\337\065\303\072\013" +
"\053\016\022\110\044\220\110\160\147\001\211\002\176\012\114\026" +
"\164\206\133\012\310\317\060\071\103\163\006\375\070\254\020\316" +
"\141\077\207\341\002\315\202\156\203\106\202\153\162\064\163\122" +
"\204\010\154\103\043\305\134\002\251\224\222\000\053\205\307\024" +
"\036\123\042\100\052\205\271\224\002\301\131\174\024\224\223\260" +
"\142\040\301\170\202\135\014\341\030\315\370\060\014\342\352\230" +
"\046\110\220\276\032\374\144\240\145\240\145\230\314\060\231\141" +
"\062\203\327\014\136\063\370\311\040\235\123\152\340\345\340\345" +
"\024\020\170\071\170\123\002\320\247\364\207\300\344\224\276\055" +
"\101\361\027\343\303\240\335\273\005\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\122\313\156\324\060" +
"\024\315\274\332\151\241\055\022\153\166\254\253\046\231\111\122" +
"\261\342\121\241\212\202\020\225\140\201\324\312\215\075\123\027" +
"\307\016\216\247\103\305\047\001\077\303\117\040\026\374\003\327" +
"\076\316\300\142\174\256\157\356\071\076\076\236\357\177\222\311" +
"\312\046\017\077\236\335\260\133\166\250\230\136\036\236\073\053" +
"\365\362\311\217\237\357\177\375\176\364\365\345\060\111\276\264" +
"\111\222\174\163\311\340\261\113\366\137\010\056\264\173\047\132" +
"\046\355\245\163\311\024\215\120\236\352\276\334\171\043\326\112" +
"\152\021\332\347\055\253\105\347\313\155\146\255\131\207\146\375" +
"\174\145\325\235\057\167\153\305\272\356\325\232\373\315\375\132" +
"\231\116\274\145\126\350\100\250\215\062\175\325\064\054\150\373" +
"\052\236\263\305\131\167\355\213\007\134\324\262\141\352\124\073" +
"\261\024\160\306\305\042\252\116\271\274\225\074\270\231\160\003" +
"\207\274\123\147\321\341\001\325\037\256\245\023\235\167\032\010" +
"\342\363\212\051\170\136\350\336\233\364\327\223\013\011\375\175" +
"\271\324\306\012\036\257\352\222\275\106\070\306\231\143\161\176" +
"\253\131\251\160\326\201\057\344\345\177\276\247\132\254\343\324" +
"\256\066\156\223\305\324\154\312\173\246\025\172\023\304\126\253" +
"\126\301\316\136\167\155\254\303\043\005\206\333\144\272\355\244" +
"\302\045\167\334\135\053\372\273\337\062\365\257\264\261\034\134" +
"\270\144\370\364\204\226\327\047\376\011\256\050\077\165\145\370" +
"\035\045\344\067\213\200\104\215\150\075\372\167\042\052\017\331" +
"\366\343\243\060\074\245\010\237\051\123\177\102\171\252\265\040" +
"\312\166\014\031\315\163\262\112\107\217\251\244\316\200\176\303" +
"\105\115\002\213\072\043\313\122\373\024\225\364\076\245\247\323" +
"\030\051\310\226\131\326\320\261\303\360\245\217\230\150\332\222" +
"\221\211\271\272\251\011\307\204\161\053\043\372\317\123\343\223" +
"\140\035\035\063\150\175\212\121\154\334\006\366\250\365\353\320" +
"\057\143\027\356\061\166\302\066\304\167\253\326\233\034\173\272" +
"\117\266\127\231\370\212\307\016\205\100\032\041\244\121\210\150" +
"\170\101\261\216\057\333\371\034\120\000\112\100\005\070\016\120" +
"\034\005\310\261\233\141\067\113\001\071\040\003\314\002\244\020" +
"\113\111\154\104\315\260\316\303\212\017\031\044\062\110\144\070" +
"\263\202\104\005\077\025\046\253\370\015\247\124\220\237\143\162" +
"\216\346\034\372\151\130\041\134\302\176\011\303\025\232\125\074" +
"\015\032\031\216\051\321\054\243\042\104\140\033\032\071\346\062" +
"\110\345\061\011\260\162\170\314\341\061\217\004\110\345\060\227" +
"\307\100\360\055\075\016\312\131\130\061\220\141\074\303\056\205" +
"\160\212\146\172\024\006\161\164\032\047\242\140\174\065\370\051" +
"\100\053\100\053\060\131\140\262\300\144\001\257\005\274\026\360" +
"\123\100\272\214\251\201\127\202\127\306\200\300\053\301\233\105" +
"\000\175\026\377\020\230\234\305\267\215\120\375\005\005\347\244" +
"\373\261\005\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\126\063\014\122\240\060\304\260\003\003\003" +
"\023\020\063\103\061\043\032\033\131\216\025\210\131\240\230\025" +
"\052\306\004\125\207\254\226\025\115\077\023\032\033\131\017\043" +
"\222\132\220\076\006\250\371\114\110\342\054\110\372\330\220\364" +
"\063\240\231\203\056\216\054\217\315\255\214\150\230\005\112\263" +
"\043\251\147\166\300\164\053\162\230\240\207\023\114\035\054\014" +
"\300\341\005\000\052\225\330\307\307\002\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\205\303\071\016\001\001" +
"\030\005\340\077\223\270\207\152\060\166\306\062\366\235\301\130" +
"\312\211\306\021\104\102\134\110\247\122\072\222\304\035\274\342" +
"\025\057\032\137\362\075\076\226\270\234\315\071\204\321\353\350" +
"\136\337\317\273\143\166\073\231\131\234\064\163\061\045\323\062" +
"\043\075\314\142\016\363\134\220\105\056\141\031\053\134\105\237" +
"\153\134\307\006\066\061\300\226\154\163\207\273\330\343\076\016" +
"\344\120\216\344\230\047\074\345\031\317\171\041\103\134\376\271" +
"\342\365\317\010\067\162\313\073\334\177\001\254\146\236\337\177" +
"\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\135\331\363\055\065" +
"\021\356\142\021\005\005\134\020\001\221\146\021\124\100\120\120" +
"\221\315\021\144\271\052\233\040\333\145\271\200\053\202\262\136" +
"\066\301\013\310\252\262\243\154\262\137\005\212\047\037\375\173" +
"\170\242\312\147\137\111\377\356\111\321\311\351\044\223\311\144" +
"\046\071\047\137\125\252\347\364\362\165\252\153\316\231\071\111" +
"\046\363\376\377\140\327\333\157\201\235\067\157\336\264\357\377" +
"\077\070\157\277\047\037\073\171\047\200\073\157\002\200\367\224" +
"\176\247\315\233\316\371\357\226\303\267\176\370\237\355\132\375" +
"\063\150\030\005\267\337\014\367\201\252\152\270\242\010\033\176" +
"\222\176\147\325\166\161\330\166\125\355\023\202\176\267\205\374" +
"\244\152\237\022\354\273\253\266\207\152\237\136\174\376\214\152" +
"\173\012\176\173\251\266\367\342\370\263\202\375\163\252\175\136" +
"\265\057\054\076\357\043\370\174\321\321\367\175\331\361\227\124" +
"\333\157\161\274\277\340\173\200\152\137\126\355\300\376\025\035" +
"\012\225\345\053\271\270\113\304\307\025\355\000\136\046\015\111" +
"\335\154\157\356\303\045\217\363\145\163\161\206\342\152\302\044" +
"\347\350\101\271\270\113\304\044\025\305\134\334\045\302\370\326" +
"\077\117\032\222\272\331\336\334\207\113\036\347\313\346\342\014" +
"\305\325\004\243\242\257\220\206\244\156\266\067\367\341\222\307" +
"\371\262\271\070\103\161\065\241\337\267\276\123\216\143\147\106" +
"\200\203\307\346\354\231\367\220\234\374\351\277\243\250\356\107" +
"\075\266\101\367\243\202\277\170\077\332\263\177\207\262\343\245" +
"\373\321\001\174\113\367\243\034\223\134\231\016\313\305\135\042" +
"\046\251\350\127\163\161\227\210\111\052\172\170\056\356\022\141" +
"\134\353\377\100\032\222\272\331\336\334\207\044\002\034\061\106" +
"\057\024\317\327\306\340\051\001\106\105\177\116\032\222\272\331" +
"\336\334\207\113\002\002\174\075\224\115\371\174\103\322\113\271" +
"\152\205\121\321\247\111\243\245\015\237\076\326\306\163\271\354" +
"\076\336\222\141\124\364\161\322\150\331\060\014\106\105\237\045" +
"\215\226\066\174\372\130\033\317\345\262\373\170\113\206\121\321" +
"\307\110\243\145\303\060\310\167\117\010\160\244\152\107\315\322" +
"\245\004\040\300\321\163\367\341\343\212\156\231\273\053\053\002" +
"\343\133\377\022\151\110\352\146\173\163\037\056\171\234\057\233" +
"\213\063\024\127\023\214\212\136\107\032\055\033\206\301\250\350" +
"\115\163\367\146\025\140\124\364\132\322\150\331\027\230\151\064" +
"\017\173\314\056\367\354\337\067\331\261\064\273\174\114\040\376" +
"\130\353\263\070\232\207\113\263\313\035\300\123\144\321\122\210" +
"\020\347\353\311\037\035\363\365\144\223\370\170\056\311\256\370" +
"\166\167\331\054\277\275\330\361\322\174\375\130\120\334\337\302" +
"\305\174\275\303\176\200\076\066\052\172\051\151\110\352\146\107" +
"\162\037\056\365\261\142\376\266\257\147\022\247\212\071\116\322" +
"\227\014\004\070\336\145\063\052\372\014\151\264\264\341\323\307" +
"\332\170\056\227\335\307\133\062\326\143\225\016\373\034\263\112" +
"\347\073\354\070\303\052\035\134\277\212\176\227\035\047\126\124" +
"\131\276\247\332\011\122\246\006\077\362\317\212\204\200\000\337" +
"\237\053\167\010\010\160\142\154\114\174\105\125\226\223\142\263" +
"\254\052\020\340\144\133\327\173\005\304\255\231\172\164\112\016" +
"\336\071\321\257\242\010\160\352\064\375\251\037\161\337\172\004" +
"\370\101\336\376\324\017\347\265\276\123\355\207\263\164\251\162" +
"\310\025\355\000\036\165\105\140\317\053\223\217\243\164\040\300" +
"\151\075\375\116\267\165\355\132\237\002\004\370\221\255\253\143" +
"\155\336\302\367\214\001\335\263\071\052\130\233\207\323\125\364" +
"\314\001\335\263\071\132\105\107\005\216\123\321\263\174\366\126" +
"\321\001\034\147\373\354\343\377\257\127\031\067\215\305\225\002" +
"\004\370\361\034\171\215\021\347\033\111\243\245\015\237\076\326" +
"\306\163\271\354\076\336\222\141\124\364\367\244\321\322\206\117" +
"\037\153\343\271\134\166\037\157\311\060\052\372\067\322\150\331" +
"\060\014\106\105\237\040\215\226\015\303\140\124\364\032\322\150" +
"\331\060\014\106\105\237\044\215\226\175\341\363\047\233\144\347" +
"\271\134\361\076\133\311\230\344\131\221\237\344\342\056\021\321" +
"\343\243\077\025\164\245\077\163\167\016\073\336\207\035\237\073" +
"\220\257\347\277\320\016\340\005\322\220\324\315\366\346\076\134" +
"\362\070\137\066\027\147\050\256\046\030\025\275\207\064\132\066" +
"\014\203\121\321\077\222\106\313\206\141\130\376\035\355\000\266" +
"\111\236\010\160\236\213\105\331\316\037\271\143\304\171\001\012" +
"\277\357\112\167\241\152\027\341\210\117\225\051\256\213\125\273" +
"\004\167\254\206\273\054\205\313\070\107\267\220\106\113\033\076" +
"\175\254\215\347\162\331\175\274\045\043\372\132\177\171\154\006" +
"\025\263\071\066\046\067\020\340\212\134\334\155\236\051\005\010" +
"\160\245\255\233\344\016\377\252\200\375\352\134\271\163\002\035" +
"\277\110\341\212\142\373\237\037\205\265\334\371\045\352\311\215" +
"\130\030\327\372\133\110\103\122\067\333\233\373\160\311\343\174" +
"\331\134\234\241\270\232\140\124\364\037\244\041\251\233\355\315" +
"\175\270\344\161\276\154\056\316\120\134\115\030\347\312\204\075" +
"\236\323\123\076\277\110\311\221\003\010\360\313\110\377\137\011" +
"\272\137\363\317\306\071\372\016\151\264\264\341\323\307\332\170" +
"\056\227\335\307\133\062\234\153\363\176\223\312\254\070\176\233" +
"\312\021\221\353\372\251\162\205\340\134\233\367\110\052\363\030" +
"\034\065\142\145\316\321\337\115\225\053\004\343\167\364\042\322" +
"\220\324\315\366\346\076\134\022\060\155\167\242\245\134\265\302" +
"\250\350\205\244\041\251\233\355\315\175\270\044\140\132\105\227" +
"\162\325\012\243\242\227\220\206\244\156\266\067\367\341\122\037" +
"\143\370\111\333\045\116\334\361\244\355\222\276\144\140\337\047" +
"\155\057\046\015\111\335\154\157\356\303\245\076\306\160\105\227" +
"\070\161\107\105\227\364\045\003\173\125\124\153\272\021\176\345" +
"\125\306\033\122\071\022\162\337\210\063\256\227\132\256\050\012" +
"\073\020\066\364\207\361\255\177\221\064\044\165\263\275\271\017" +
"\227\074\316\227\315\305\031\212\253\011\106\105\137\045\015\111" +
"\335\154\157\356\303\045\217\363\145\163\161\206\342\152\102\334" +
"\110\111\007\160\027\111\154\373\356\070\021\075\163\167\163\336" +
"\376\324\217\321\106\363\126\146\304\070\025\223\314\334\145\171" +
"\222\274\124\114\122\321\333\162\161\227\210\111\052\072\372\254" +
"\137\311\020\377\063\075\030\313\062\044\246\017\147\016\336\334" +
"\020\053\172\177\054\313\220\230\076\234\071\170\163\103\254\350" +
"\003\261\054\103\142\372\160\346\340\315\215\266\332\161\301\111" +
"\343\146\133\161\324\325\216\214\375\216\024\306\165\207\170\216" +
"\076\024\313\202\225\237\243\214\233\316\321\073\123\070\304\212" +
"\376\051\255\137\353\215\370\373\321\016\340\257\371\372\123\077" +
"\332\212\334\024\340\142\054\216\043\172\354\351\356\221\073\265" +
"\162\130\231\025\020\305\074\203\045\136\231\336\324\115\177\346" +
"\066\133\347\202\344\023\212\353\303\133\072\332\072\374\241\100" +
"\307\223\164\342\071\372\226\156\372\063\267\331\072\027\044\037" +
"\325\213\173\003\275\274\057\304\133\072\212\330\043\167\245\356" +
"\177\173\357\077\272\155\222\356\254\000\046\371\035\255\156\104" +
"\056\005\355\016\077\005\050\214\066\212\127\246\177\115\330\251" +
"\225\203\130\321\355\044\261\302\031\211\022\040\216\217\376\171" +
"\276\376\324\217\366\026\014\164\277\005\343\041\166\234\341\275" +
"\042\056\140\301\173\351\250\230\207\061\120\321\001\234\043\355" +
"\350\332\001\274\353\322\307\332\264\316\147\367\361\226\214\250" +
"\212\276\347\322\307\332\264\316\147\367\361\226\214\054\073\272" +
"\256\345\223\141\032\342\335\323\154\153\350\127\001\142\105\307" +
"\030\155\236\363\311\206\107\261\210\047\033\272\305\123\315\235" +
"\365\164\263\206\117\037\153\343\271\134\166\037\157\311\130\355" +
"\273\247\105\254\153\047\302\101\357\100\307\230\235\010\377\111" +
"\032\222\272\331\336\334\207\113\036\347\313\346\342\014\305\325" +
"\204\111\106\363\036\317\305\135\042\212\030\303\057\371\075\167" +
"\177\211\215\151\157\023\222\200\075\327\315\140\173\233\320\310" +
"\100\141\247\173\361\176\264\372\371\310\071\141\134\353\067\346" +
"\315\073\307\374\271\117\037\153\343\271\134\166\037\157\311\020" +
"\317\321\066\342\234\200\350\347\102\067\326\077\143\173\257\203" +
"\023\203\326\217\106\337\121\254\023\214\337\321\215\035\314\072" +
"\141\047\263\220\076\326\306\163\271\354\076\336\222\021\275\176" +
"\064\351\255\024\330\326\075\215\000\134\335\265\171\117\111\372" +
"\126\321\241\100\200\247\045\175\035\243\171\143\001\307\231\013" +
"\175\306\147\137\313\135\207\237\315\311\337\306\236\174\100\200" +
"\347\142\143\362\125\124\365\346\371\261\071\153\100\333\165\070" +
"\322\137\332\165\370\357\374\263\161\207\377\157\322\150\151\303" +
"\247\217\265\361\134\056\273\217\267\144\210\153\363\126\146\337" +
"\357\071\140\234\243\033\337\312\316\361\355\364\351\143\155\074" +
"\227\313\356\343\055\031\342\150\136\362\056\323\143\160\324\012" +
"\261\242\223\075\175\270\212\210\036\037\115\172\232\177\035\320" +
"\356\360\175\300\001\357\210\154\163\241\051\100\141\337\124\347" +
"\323\340\057\215\220\355\345\124\016\201\363\225\261\071\307\106" +
"\165\025\055\176\175\324\044\343\243\045\276\071\060\333\236\274" +
"\342\335\323\033\272\351\317\334\146\353\134\220\174\102\161\175" +
"\170\113\207\130\321\327\165\323\237\271\315\326\271\040\371\204" +
"\342\372\360\226\016\261\242\257\351\246\077\163\233\255\163\101" +
"\362\301\066\163\147\001\003\225\304\300\073\033\034\061\307\305" +
"\306\314\015\214\172\147\203\204\156\315\366\271\115\101\273\303" +
"\117\001\012\277\373\165\314\205\142\332\223\015\157\260\343\031" +
"\236\013\355\000\336\116\315\272\316\060\106\234\067\346\134\072" +
"\307\334\213\117\037\153\343\271\134\166\037\157\311\150\277\243" +
"\051\100\141\017\265\352\376\327\007\167\356\232\033\155\017\010" +
"\164\357\001\361\066\073\156\173\100\350\270\355\310\366\006\302" +
"\271\256\365\127\306\146\031\022\323\207\063\007\157\156\210\025" +
"\365\256\116\224\060\044\246\017\147\016\336\334\210\236\271\333" +
"\232\267\077\365\303\270\037\335\130\277\103\122\067\333\233\373" +
"\160\311\343\174\331\134\234\241\270\232\040\176\353\237\343\262" +
"\041\016\142\105\333\356\327\011\130\231\235\261\213\131\303\047" +
"\236\243\127\304\262\014\211\351\303\231\203\067\067\304\325\216" +
"\357\314\327\237\372\041\236\243\321\157\323\031\022\323\207\063" +
"\007\157\156\070\177\107\217\304\012\367\254\103\200\243\347\356" +
"\203\170\216\212\363\225\056\175\310\066\024\304\231\203\067\067" +
"\304\212\106\257\001\031\022\323\207\063\007\157\156\210\025\275" +
"\074\226\145\110\114\037\316\034\274\271\361\021\267\127\011\043" +
"\235\340\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\255\127\261\116\002\101" +
"\020\035\316\205\372\202\064\122\101\202\011\025\261\240\100\215" +
"\215\271\312\130\030\055\151\304\304\002\203\006\341\316\120\030" +
"\262\052\061\124\006\343\017\130\253\215\205\075\225\255\361\017" +
"\370\001\077\301\304\273\004\033\063\157\315\273\100\041\305\163" +
"\167\166\336\274\231\067\274\174\111\066\352\311\162\163\367\244" +
"\165\321\252\105\141\273\123\333\156\207\007\307\341\346\351\112" +
"\325\174\334\005\135\117\144\320\025\221\247\176\117\374\277\377" +
"\165\366\075\036\256\327\237\313\113\222\151\212\071\152\207\375" +
"\120\274\346\316\240\033\137\232\174\227\314\133\376\065\172\370" +
"\275\043\043\363\317\240\177\056\103\361\242\344\157\166\016\064" +
"\072\263\317\222\006\210\231\372\072\360\176\263\261\305\235\340" +
"\257\202\100\301\372\217\052\320\231\355\221\301\171\140\144\301" +
"\253\156\257\164\040\141\167\125\217\141\175\025\340\117\044\100" +
"\003\001\145\262\202\216\030\352\125\153\061\355\144\036\050\070" +
"\074\001\045\352\320\056\242\044\260\250\346\266\240\003\025\044" +
"\070\203\201\074\371\052\063\051\124\271\127\101\265\057\020\060" +
"\043\235\053\147\241\170\355\162\000\024\034\356\332\316\154\237" +
"\035\144\364\253\214\275\257\220\224\114\241\112\100\315\003\070" +
"\257\254\177\310\015\113\176\356\146\355\204\023\103\054\152\037" +
"\210\232\006\320\170\305\000\237\240\031\373\152\005\023\166\165" +
"\040\105\014\133\104\025\204\142\320\151\137\240\253\305\332\235" +
"\250\011\246\002\364\066\200\044\302\253\060\355\260\077\034\000" +
"\142\027\010\316\313\115\212\227\124\017\072\116\140\307\101\376" +
"\201\373\034\046\210\265\133\247\116\140\113\105\066\221\306\235" +
"\201\261\070\362\000\315\211\247\017\164\065\176\173\305\025\244" +
"\155\333\105\073\164\034\124\132\070\113\364\340\060\206\227\103" +
"\375\001\035\007\002\220\166\150\105\016\166\001\220\302\212\170" +
"\332\151\067\200\103\206\137\071\123\070\047\073\226\170\100\202" +
"\153\274\326\242\105\230\335\136\323\364\007\111\042\077\341\170" +
"\037\304\142\160\255\234\334\244\206\011\362\226\352\074\241\376" +
"\142\201\106\377\017\127\077\220\063\223\322\372\020\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\252\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\025\046\301\322\040\240\060\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\101\065\011\000\146\061\052\017\372" +
"\020\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\252\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\165\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\202\046\022\000\204\105\073\137\372\020\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\312\041\016\202\120" +
"\000\307\341\377\336\360\004\132\270\000\063\275\140\041\320\034" +
"\321\106\044\021\014\070\335\036\354\075\107\342\010\034\304\131" +
"\074\211\233\331\154\323\063\110\241\031\114\244\337\227\277\353" +
"\133\213\320\152\125\226\273\103\165\256\154\360\365\321\156\153" +
"\137\354\175\026\077\076\267\364\365\334\030\251\163\222\056\143" +
"\134\376\170\247\170\035\335\207\334\115\057\161\177\011\215\172" +
"\031\076\237\317\347\363\371\174\076\237\317\347\363\371\174\076" +
"\237\317\347\363\371\174\076\237\317\347\363\371\163\376\057\221" +
"\223\165\151\053\040\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\324\030\006\051\000\000\321\075\264\307\263" +
"\000\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\040\266\372\377\377\377\337\012\000\034\266\072\050\121\000\000" +
"\000"
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\345\124\261\112\303\120" +
"\024\275\357\171\043\035\237\065\203\056\362\012\016\116\235\134" +
"\304\245\110\047\161\020\034\063\125\160\210\124\211\315\213\144" +
"\052\035\073\211\320\317\320\305\301\357\360\113\374\004\301\004" +
"\164\221\167\112\357\153\132\207\056\011\311\311\075\367\276\173" +
"\316\311\353\047\105\305\210\166\223\213\333\301\343\240\133\270" +
"\164\330\075\113\335\325\215\073\275\333\077\342\217\247\176\246" +
"\211\312\214\210\146\371\210\314\337\257\356\277\246\343\223\343" +
"\227\316\026\251\204\370\072\165\271\043\235\234\227\131\105\132" +
"\337\055\277\267\337\212\331\057\007\225\371\003\215\111\027\365" +
"\065\252\236\125\375\322\100\240\017\000\142\232\370\053\270\007" +
"\250\030\120\075\043\000\126\030\326\000\210\374\025\014\251\332" +
"\362\346\123\072\220\355\012\003\334\102\000\132\142\014\250\056" +
"\341\270\001\123\131\004\260\230\212\154\103\123\131\271\120\333" +
"\001\246\226\366\200\301\021\307\000\065\127\041\315\041\140\304" +
"\025\377\071\056\214\132\223\031\244\236\260\342\320\017\150\174" +
"\216\065\344\243\332\256\227\112\371\204\222\112\012\225\023\113" +
"\032\140\233\000\000\270\234\304\366\307\177\204\200\354\201\223" +
"\157\240\100\315\305\167\017\067\237\170\001\213\246\062\342\370" +
"\052\237\120\213\153\074\337\140\042\347\255\322\025\153\237\173" +
"\316\170\122\067\157\206\102\141\321\151\055\277\064\357\012\226" +
"\065\020\074\172\274\232\171\177\326\252\026\337\236\377\113\106" +
"\334\061\324\022\111\306\342\212\235\272\371\067\206\162\167\063" +
"\113\016\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\344\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\120\121\134\310\120\307\300\124\012\042\131\107" +
"\005\106\005\106\005\006\136\200\021\044\310\340\060\150\334\063" +
"\052\060\202\004\160\046\076\052\112\214\012\214\012\014\363\164" +
"\062\330\374\204\160\017\000\264\327\335\365\143\012\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\345\225\261\116\203\140" +
"\024\205\017\370\143\072\376\126\006\135\014\156\116\215\203\213" +
"\161\260\061\114\306\301\304\221\251\106\007\214\032\054\140\230" +
"\232\216\235\214\111\037\103\027\007\237\303\047\361\021\024\301" +
"\304\305\334\103\162\011\326\241\003\020\162\270\367\376\374\347" +
"\174\360\374\016\057\037\143\075\072\271\032\335\217\006\171\026" +
"\137\017\216\342\354\354\062\073\270\331\334\061\157\017\141\342" +
"\002\105\002\140\236\216\141\177\077\165\373\061\233\354\357\075" +
"\155\257\300\211\140\316\343\054\315\340\106\307\105\122\065\255" +
"\257\201\171\355\277\344\363\237\036\050\322\073\114\340\346\365" +
"\331\253\356\235\352\070\054\313\362\123\022\140\103\271\002\060" +
"\230\312\025\146\050\127\130\103\132\075\022\301\241\025\326\270" +
"\262\340\173\126\024\014\155\325\327\017\237\141\213\354\225\043" +
"\127\254\261\115\264\246\307\004\266\211\076\206\242\160\312\226" +
"\273\101\207\137\310\202\153\115\300\126\145\210\020\116\251\347" +
"\201\044\354\262\134\365\150\340\002\036\006\171\006\354\052\253" +
"\150\010\265\062\273\226\020\105\035\244\271\162\330\160\136\301" +
"\206\067\201\043\363\001\137\075\174\041\313\245\250\165\311\040" +
"\041\252\015\070\064\160\177\317\107\265\273\142\053\156\024\230" +
"\037\334\132\352\240\326\332\066\361\151\361\233\040\151\207\026" +
"\003\030\046\370\354\003\100\133\121\324\226\330\250\356\160\346" +
"\324\022\006\003\106\224\125\063\330\140\224\332\101\275\100\255" +
"\245\303\273\113\111\275\050\045\070\172\242\350\014\316\240\026" +
"\316\045\166\120\215\332\042\060\150\310\225\072\160\377\031\206" +
"\026\037\262\016\137\120\077\203\126\174\013\137\063\364\250\247" +
"\263\016\000\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\321\071\112\005\101" +
"\020\006\340\171\243\317\175\337\367\335\347\162\023\003\117\140" +
"\342\021\104\120\274\220\231\221\241\107\022\274\203\377\100\013" +
"\023\250\214\142\044\137\303\107\125\365\122\335\320\117\157\125" +
"\377\356\266\252\257\056\056\137\256\007\367\257\317\217\165\125" +
"\075\334\124\275\252\031\207\035\354\306\154\253\076\351\170\256" +
"\153\357\046\216\175\262\066\023\353\061\036\323\061\022\053\261" +
"\025\147\061\021\253\261\374\215\341\142\273\325\167\055\372\161" +
"\124\352\372\227\366\177\270\277\271\353\264\365\216\217\271\305" +
"\222\317\307\116\314\305\346\027\075\366\142\041\066\112\175\136" +
"\342\122\114\305\161\251\017\242\127\362\321\022\007\345\276\241" +
"\230\374\303\077\004\000\000\000\000\000\000\000\000\000\000\000" +
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
"\000\340\237\171\007\171\373\074\020\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\233\131\123\023\101" +
"\020\307\247\100\274\357\013\361\042\004\041\052\210\367\215\121" +
"\301\203\103\214\007\236\361\300\013\025\025\001\005\361\302\373" +
"\300\013\217\317\340\233\117\076\372\171\174\262\312\147\137\235" +
"\024\323\326\270\263\231\314\314\156\066\323\133\233\252\137\155" +
"\247\173\146\363\247\231\115\166\272\223\157\277\111\111\177\037" +
"\051\116\247\233\113\377\374\114\225\215\014\327\027\021\062\330" +
"\103\010\371\102\375\105\351\346\266\037\035\325\003\277\276\177" +
"\005\167\015\101\364\350\357\045\103\204\052\377\247\272\210\121" +
"\114\031\103\051\241\214\145\276\161\224\361\224\011\224\211\314" +
"\067\211\062\231\062\205\075\237\112\231\306\235\047\163\216\351" +
"\224\031\224\231\224\131\224\331\224\071\224\271\224\122\312\074" +
"\112\031\067\147\076\173\335\014\013\050\013\231\275\210\262\070" +
"\063\106\120\135\316\210\261\143\005\203\267\335\174\161\156\256" +
"\023\347\330\362\054\347\166\316\001\137\314\371\272\202\352\230" +
"\041\136\346\152\043\250\206\277\047\023\255\044\142\176\263\021" +
"\317\342\257\164\034\115\210\071\175\122\325\072\147\316\246\332" +
"\017\302\252\172\011\243\212\263\201\152\027\037\220\220\304\274" +
"\002\132\226\202\057\044\271\126\121\275\214\263\227\243\121\215" +
"\063\327\366\251\256\141\324\162\166\056\126\120\352\064\306\353" +
"\002\132\126\202\117\120\035\147\360\266\012\253\064\307\073\131" +
"\055\211\011\132\254\272\017\131\223\045\026\163\276\256\257\252" +
"\327\172\124\155\176\367\204\363\152\014\207\352\165\050\125\343" +
"\310\265\227\253\161\075\051\324\325\030\304\073\337\006\337\125" +
"\073\377\053\033\025\377\213\321\272\016\166\135\157\042\152\053" +
"\044\074\237\062\233\013\226\153\076\127\133\002\315\165\066\325" +
"\133\065\125\107\125\034\001\337\326\165\275\346\170\035\004\055" +
"\326\134\215\336\124\027\162\127\020\255\153\014\053\304\164\267" +
"\233\117\004\055\041\251\004\343\254\116\126\111\330\046\211\045" +
"\163\314\365\203\355\140\207\344\152\114\060\222\234\015\354\160" +
"\361\005\001\150\331\011\076\101\165\003\243\221\263\201\135\056" +
"\076\040\041\211\351\260\333\305\007\132\366\200\117\120\275\227" +
"\321\304\331\052\304\065\307\353\220\321\122\301\373\004\325\315" +
"\214\026\316\126\241\125\163\274\012\373\070\055\155\174\054\044" +
"\225\340\132\003\366\123\122\206\163\165\070\000\166\110\336\371" +
"\352\030\051\316\316\305\101\215\261\046\200\226\103\340\023\124" +
"\037\146\264\163\166\056\216\260\263\251\216\227\161\324\305\007" +
"\132\216\201\317\232\016\307\161\111\114\320\142\215\152\073\372" +
"\062\047\110\220\073\060\333\366\215\047\225\124\343\174\347\113" +
"\133\226\153\234\053\104\115\065\316\025\022\124\256\117\205\042" +
"\327\166\324\257\063\234\226\344\332\355\334\147\214\163\215\341" +
"\152\074\033\222\332\123\314\020\057\163\265\021\124\047\045\164" +
"\344\210\347\233\163\140\013\252\033\045\234\227\304\222\071\346" +
"\252\162\101\022\273\010\166\110\326\365\045\106\047\147\003\227" +
"\135\174\100\102\022\363\012\150\271\002\276\250\246\132\300\232" +
"\052\316\134\373\255\372\252\343\230\037\325\015\044\252\251\006" +
"\125\123\155\062\304\313\134\025\142\374\363\220\124\202\133\014" +
"\351\362\060\067\033\327\070\373\072\037\023\124\337\140\164\163" +
"\266\012\255\232\343\125\270\311\151\351\341\143\236\127\110\057" +
"\261\141\205\340\134\327\061\103\274\314\325\106\120\235\062\240" +
"\317\160\236\056\267\300\026\124\267\033\160\233\235\315\144\256" +
"\223\176\111\154\000\354\220\354\012\356\060\006\071\073\027\167" +
"\311\150\207\103\165\274\056\240\345\036\370\242\176\143\001\373" +
"\215\070\163\355\267\352\373\216\143\176\124\107\375\106\035\274" +
"\365\033\165\152\252\017\110\141\152\252\371\253\004\077\104\251" +
"\032\147\256\043\325\221\152\121\365\120\301\124\243\170\010\252" +
"\073\045\074\222\304\222\071\346\372\301\143\260\103\122\011\366" +
"\133\365\023\307\061\077\252\161\166\223\160\176\047\030\147\277" +
"\021\147\256\235\335\062\236\250\163\347\157\137\246\333\220\056" +
"\017\163\263\361\224\263\237\361\061\317\125\367\347\304\206\252" +
"\173\324\053\010\256\127\200\163\135\353\346\372\205\025\271\306" +
"\271\102\124\372\062\057\135\174\255\222\361\371\357\313\240\170" +
"\010\252\007\015\170\105\106\053\301\046\163\165\170\015\166\110" +
"\352\327\176\253\036\166\034\363\243\032\147\017\314\371\313\032" +
"\025\012\377\053\037\234\135\122\234\271\216\372\215\301\365\300" +
"\164\252\223\157\310\377\325\111\234\065\125\123\325\157\121\252" +
"\366\236\153\234\125\234\250\022\034\134\045\130\167\157\367\216" +
"\330\260\157\064\175\005\373\253\070\357\135\174\366\327\103\076" +
"\270\370\354\337\355\332\367\055\104\234\167\252\321\156\067\270" +
"\335\056\316\373\020\277\124\217\004\252\132\345\335\377\243\213" +
"\317\376\117\231\250\352\356\317\247\214\155\127\343\047\342\327" +
"\272\266\357\356\311\257\134\177\366\051\327\041\332\067\376\005" +
"\002\142\327\054\253\150\000\000"
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


	Integer parenLevel, cl;
	Stack<Integer> depths;
	Pattern nlRegex;
	boolean nextDsl;


    static
    {
        TERMINAL_COUNT = 38;
        GRAMMAR_SYMBOL_COUNT = 82;
        SYMBOL_COUNT = 171;
        PARSER_STATE_COUNT = 170;
        SCANNER_STATE_COUNT = 147;
        DISAMBIG_GROUP_COUNT = 16;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { ex.printStackTrace(); System.exit(1); }
        catch(java.lang.ClassNotFoundException ex) { ex.printStackTrace(); System.exit(1); }
        disambiguationGroups = new java.util.BitSet[16];
        disambiguationGroups[0] = newBitVec(38,5,28);
        disambiguationGroups[1] = newBitVec(38,12,28);
        disambiguationGroups[2] = newBitVec(38,26,28);
        disambiguationGroups[3] = newBitVec(38,23,28);
        disambiguationGroups[4] = newBitVec(38,12,18);
        disambiguationGroups[5] = newBitVec(38,18,26);
        disambiguationGroups[6] = newBitVec(38,5,18);
        disambiguationGroups[7] = newBitVec(38,19,23);
        disambiguationGroups[8] = newBitVec(38,2,19,23);
        disambiguationGroups[9] = newBitVec(38,4,23);
        disambiguationGroups[10] = newBitVec(38,2,4,23);
        disambiguationGroups[11] = newBitVec(38,2,23);
        disambiguationGroups[12] = newBitVec(38,3,23);
        disambiguationGroups[13] = newBitVec(38,1,4,23);
        disambiguationGroups[14] = newBitVec(38,3,4,23);
        disambiguationGroups[15] = newBitVec(38,2,3,4,23);
    }

}
