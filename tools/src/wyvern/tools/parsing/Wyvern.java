/*
 * Built at Fri Mar 28 18:25:55 EDT 2014
 * by Copper version 0.7.1,
 *      revision 1cd57156c790d7c88540b5f453389b9ca39fae06,
 *      build 20131117-2243
 */
package wyvern.tools.parsing;
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
import wyvern.tools.parsing.transformers.ASTExplorer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.*;




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
        caseSignal_t(8),
        classKwd_t(9),
        closeParen_t(10),
        colon_t(11),
        comma_t(12),
        comment_t(13),
        dash_t(14),
        decimalInteger_t(15),
        defKwd_t(16),
        divide_t(17),
        dot_t(18),
        dslLine_t(19),
        dslSignal_t(20),
        dslWhitespace_t(21),
        equals_t(22),
        fnKwd_t(23),
        identifier_t(24),
        ignoredNewline(25),
        metadataKwd_t(26),
        mult_t(27),
        multi_comment_t(28),
        newKwd_t(29),
        newSignal_t(30),
        notCurly_t(31),
        oCurly_t(32),
        openParen_t(33),
        plus_t(34),
        shortString_t(35),
        tarrow_t(36),
        tilde_t(37),
        typeKwd_t(38),
        valKwd_t(39),
        varKwd_t(40);

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
            case 171:
                RESULT = runSemanticAction_171();
                break;
            case 172:
                RESULT = runSemanticAction_172();
                break;
            case 173:
                RESULT = runSemanticAction_173();
                break;
            case 174:
                RESULT = runSemanticAction_174();
                break;
            case 175:
                RESULT = runSemanticAction_175();
                break;
            case 176:
                RESULT = runSemanticAction_176();
                break;
            case 177:
                RESULT = runSemanticAction_177();
                break;
            case 178:
                RESULT = runSemanticAction_178();
                break;
            case 179:
                RESULT = runSemanticAction_179();
                break;
            case 180:
                RESULT = runSemanticAction_180();
                break;
            case 181:
                RESULT = runSemanticAction_181();
                break;
            case 182:
                RESULT = runSemanticAction_182();
                break;
            case 183:
                RESULT = runSemanticAction_183();
                break;
            case 184:
                RESULT = runSemanticAction_184();
                break;
            case 185:
                RESULT = runSemanticAction_185();
                break;
            case 186:
                RESULT = runSemanticAction_186();
                break;
            case 187:
                RESULT = runSemanticAction_187();
                break;
            case 188:
                RESULT = runSemanticAction_188();
                break;
            case 189:
                RESULT = runSemanticAction_189();
                break;
            case 190:
                RESULT = runSemanticAction_190();
                break;
            case 191:
                RESULT = runSemanticAction_191();
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
            case 10:
                RESULT = runSemanticAction_10(lexeme);
                break;
            case 15:
                RESULT = runSemanticAction_15(lexeme);
                break;
            case 19:
                RESULT = runSemanticAction_19(lexeme);
                break;
            case 21:
                RESULT = runSemanticAction_21(lexeme);
                break;
            case 24:
                RESULT = runSemanticAction_24(lexeme);
                break;
            case 31:
                RESULT = runSemanticAction_31(lexeme);
                break;
            case 32:
                RESULT = runSemanticAction_32(lexeme);
                break;
            case 33:
                RESULT = runSemanticAction_33(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
            return RESULT;
        }
        public java.lang.Object runSemanticAction_92()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_93()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_94()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object mer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = mer;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_95()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_96()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object l = (java.lang.Object) _children[0];
            java.lang.Object r = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_97()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ter = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            RESULT = ter;
            return RESULT;
        }
        public java.lang.Object runSemanticAction_98()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_99()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_100()
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
        public java.lang.Object runSemanticAction_101()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_102()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object type = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_103()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ClassDeclaration((String)id, "", "",
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_104()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = new ClassDeclaration((String)id, "", "", null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_105()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object after = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_106()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_107()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_108()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_109()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object r = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = r; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_110()
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
            java.lang.Object dsl = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = dsl; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_112()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object i = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = i; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_113()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object i = (java.lang.Object) _children[0];
            java.lang.Object n = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = (String)i + (String)n; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_114()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ws = (java.lang.Object) _children[0];
            java.lang.Object ln = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = (String)ws + (String)ln; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_115()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object s = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = s; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_116()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object st = (java.lang.Object) _children[0];
            java.lang.Object i = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = (String)st + (String)i; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_117()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object exn = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = exn; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_118()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object exn = (java.lang.Object) _children[0];
            java.lang.Object dsl = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
            
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) exn);
		if (!exp.foundTilde())
			throw new RuntimeException();
		((DSLLit)exp.getRef()).setText((String)dsl);
		RESULT = exn;
	 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_119()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object exn = (java.lang.Object) _children[0];
            java.lang.Object blk = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
            
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) exn);
		if (!exp.foundNew())
			throw new RuntimeException();
		((New)exp.getRef()).setBody((blk instanceof DeclSequence) ? (DeclSequence)blk : new DeclSequence(Arrays.asList((TypedAST)blk)));
		RESULT = exn;
	 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_120()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object exn = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = exn; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_121()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object exn = (java.lang.Object) _children[0];
            java.lang.Object dsl = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
            
    	ASTExplorer exp = new ASTExplorer();
    	exp.transform((TypedAST) exn);
    	if (!exp.foundTilde())
			throw new RuntimeException();
		((DSLLit)exp.getRef()).setText((String)dsl);
    	RESULT = exn;
    
            return RESULT;
        }
        public java.lang.Object runSemanticAction_122()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object exn = (java.lang.Object) _children[0];
            java.lang.Object blk = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
            
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) exn);
		if (!exp.foundNew())
			throw new RuntimeException();
		((New)exp.getRef()).setBody((blk instanceof DeclSequence) ? (DeclSequence)blk : new DeclSequence(Arrays.asList((TypedAST)blk)));
		RESULT = exn;
	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_123()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            
    	RESULT = aer;
    
            return RESULT;
        }
        public java.lang.Object runSemanticAction_124()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object pi = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = pi; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_126()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object idsl = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = idsl; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_127()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object str = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = str; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_128()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object str = (java.lang.Object) _children[0];
            java.lang.Object idsl = (java.lang.Object) _children[2];
            java.lang.Object stre = (java.lang.Object) _children[4];
            java.lang.Object RESULT = null;
             RESULT = str + "{" + idsl + "}" + stre; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_129()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = ""; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_130()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object re = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             ((LinkedList<NameBinding>)re).addFirst(new NameBindingImpl((String)id, (Type)ta)); RESULT = re; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_131()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             LinkedList<NameBinding> llnb = new LinkedList<NameBinding>(); llnb.add(new NameBindingImpl((String)id, (Type)ta)); RESULT = llnb; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_132()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object first = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_133()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object el = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = el; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_134()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object type = (java.lang.Object) _children[1];
            java.lang.Object inner = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner, (Type)type); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_135()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_136()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_137()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_138()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_139()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_140()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_141()
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
        public java.lang.Object runSemanticAction_142()
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
        public java.lang.Object runSemanticAction_143()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object cds = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_144()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ld = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ld; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_145()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rest = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rest; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_146()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_147()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_148()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_149()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object rst = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_150()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object rd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = rd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_151()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_152()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_153()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object va = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = va; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_154()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_155()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = null; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_156()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_157()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object de = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = de; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_158()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ip = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ip; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_159()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new LinkedList<NameBinding>(); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_160()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ex = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = ex; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_161()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object nr = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = nr; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_162()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object re = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_163()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object vd = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = vd; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_164()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_165()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_166()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_167()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object argNames = (java.lang.Object) _children[2];
            java.lang.Object type = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_168()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new Variable(new NameBindingImpl((String)id, null), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_169()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object t = (java.lang.Object) _children[2];
            java.lang.Object inner = (java.lang.Object) _children[5];
            java.lang.Object RESULT = null;
             RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_170()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object inner = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = inner; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_171()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object tgt = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_172()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object src = (java.lang.Object) _children[0];
            java.lang.Object op = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Invocation((TypedAST)src,(String)op, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_173()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object lit = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DSLLit(Optional.of((String)lit)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_174()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new IntegerConstant((Integer)res); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_175()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new New(new HashMap<String,TypedAST>(), null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_176()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = new DSLLit(Optional.empty()); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_177()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object aer = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
            
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) aer);
		if (exp.foundNew()){
			pushToken(Terminals.newSignal_t,"");
		}else if (exp.foundTilde()) {
			pushToken(Terminals.dslSignal_t,"");
		}

		RESULT = aer;
	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_178()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object res = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
            RESULT = res; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_179()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = UnitVal.getInstance(null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_180()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Arrow((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_181()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object t1 = (java.lang.Object) _children[0];
            java.lang.Object t2 = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new Tuple((Type)t1,(Type)t2); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_182()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ta = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ta; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_183()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new UnresolvedType((String)id); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_184()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object ty = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = ty; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_185()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object rest = (java.lang.Object) _children[2];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_186()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object def = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_187()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object md = (java.lang.Object) _children[0];
            java.lang.Object RESULT = null;
             RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_188()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, (DeclSequence)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_189()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object name = (java.lang.Object) _children[1];
            java.lang.Object RESULT = null;
             RESULT = new TypeDeclaration((String)name, null, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_190()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object id = (java.lang.Object) _children[1];
            java.lang.Object ty = (java.lang.Object) _children[2];
            java.lang.Object body = (java.lang.Object) _children[3];
            java.lang.Object RESULT = null;
             RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_191()
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
        public java.lang.Object runSemanticAction_10(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
              parenLevel--; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_15(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
 		RESULT = Integer.parseInt(lexeme);
 	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_19(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             nextDsl = false; RESULT = lexeme; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_21(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             nextDsl = true; RESULT = "\n"+lexeme.substring(depths.peek()+1); 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_24(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
            
 		RESULT = lexeme;
 	
            return RESULT;
        }
        public java.lang.Object runSemanticAction_31(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             RESULT = lexeme; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_32(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            java.lang.Object RESULT = null;
             cl++; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_33(final String lexeme)
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
            else if(match.terms.equals(disambiguationGroups[16])) return disambiguate_16(lexeme);
            else if(match.terms.equals(disambiguationGroups[17])) return disambiguate_17(lexeme);
            else return -1;
        }
        public int disambiguate_0(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Spaces_t = 5;
            @SuppressWarnings("unused") final int notCurly_t = 31;
            
		if (cl > 0) return notCurly_t;
		return Spaces_t;
	
        }
        public int disambiguate_1(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int comment_t = 13;
            @SuppressWarnings("unused") final int notCurly_t = 31;
            
		if (cl > 0) return notCurly_t;
		return comment_t;
	
        }
        public int disambiguate_2(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int multi_comment_t = 28;
            @SuppressWarnings("unused") final int notCurly_t = 31;
            
		if (cl > 0) return notCurly_t;
		return multi_comment_t;
	
        }
        public int disambiguate_3(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            @SuppressWarnings("unused") final int notCurly_t = 31;
            
		if (cl > 0) return notCurly_t;
		return ignoredNewline;
	
        }
        public int disambiguate_4(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int comment_t = 13;
            @SuppressWarnings("unused") final int dslLine_t = 19;
            
		if (nextDsl) return dslLine_t;
		return comment_t;
	
        }
        public int disambiguate_5(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int dslLine_t = 19;
            @SuppressWarnings("unused") final int multi_comment_t = 28;
            
		if (nextDsl) return dslLine_t;
		return dslLine_t;
	
        }
        public int disambiguate_6(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Spaces_t = 5;
            @SuppressWarnings("unused") final int dslLine_t = 19;
            
		if (nextDsl) return dslLine_t;
		return Spaces_t;
	
        }
        public int disambiguate_7(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int dslWhitespace_t = 21;
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
		return dslWhitespace_t;
	
        }
        public int disambiguate_8(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int dslWhitespace_t = 21;
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Dedent_t;
	
        }
        public int disambiguate_12(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
        public int disambiguate_16(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Dedent_t = 2;
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int ignoredNewline = 25;
            
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
		} else {
			return Dedent_t;
		}
	
        }
        public int disambiguate_17(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int dslSignal_t = 20;
            @SuppressWarnings("unused") final int newSignal_t = 30;
            
		//Should never be used.
		throw new RuntimeException();
	
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
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\122\113\162\324\060" +
"\020\165\346\227\011\041\204\052\326\134\041\025\333\063\261\135" +
"\254\102\010\124\212\000\251\114\012\026\054\122\212\255\231\050" +
"\150\144\043\153\062\244\270\022\334\201\063\160\011\212\005\167" +
"\240\245\327\063\311\302\172\355\126\277\327\255\047\375\370\027" +
"\365\027\066\172\366\371\364\106\334\212\075\055\314\154\157\342" +
"\254\062\263\027\077\177\177\374\363\367\371\367\067\235\050\372" +
"\326\104\121\364\313\105\335\343\017\257\135\364\344\225\254\244" +
"\161\347\262\021\312\136\072\027\015\221\010\341\211\131\205\133" +
"\357\345\122\053\043\103\172\322\210\122\266\076\334\024\326\326" +
"\313\220\054\217\026\126\337\371\360\161\051\132\071\121\063\043" +
"\264\377\175\124\152\321\266\157\227\025\366\164\335\312\063\141" +
"\245\011\374\262\326\365\052\232\317\105\150\345\043\156\073\250" +
"\104\173\355\203\247\225\054\325\134\350\023\343\344\114\142\320" +
"\112\116\131\165\130\251\133\125\205\341\372\125\215\201\253\126" +
"\237\362\300\333\024\337\317\263\113\177\237\256\225\223\255\077" +
"\106\240\313\257\013\241\161\240\251\131\115\252\374\331\325\124" +
"\241\333\023\342\327\126\126\354\203\213\166\346\322\211\112\070" +
"\301\365\203\371\102\207\316\273\076\120\227\017\116\061\064\162" +
"\311\125\333\024\076\260\306\324\156\155\333\260\136\207\333\165" +
"\043\315\332\244\101\243\027\141\270\235\366\272\266\016\067\032" +
"\030\156\155\377\246\123\032\006\154\271\273\106\256\174\271\025" +
"\372\076\264\034\366\047\027\207\347\027\056\352\034\036\323\362" +
"\356\330\137\321\025\371\253\257\352\352\216\266\375\317\064\040" +
"\321\031\255\107\177\217\056\332\250\202\367\253\362\156\050\036" +
"\222\251\057\165\135\176\101\170\142\214\044\312\046\137\002\222" +
"\023\032\067\334\120\253\113\112\365\010\011\066\350\353\114\113" +
"\022\232\226\011\215\257\214\367\127\053\077\263\362\062\124\106" +
"\112\252\021\126\314\251\175\047\354\254\314\207\271\334\171\213" +
"\302\043\216\273\306\322\234\375\372\352\246\044\034\170\324\024" +
"\364\050\340\274\142\264\330\327\041\061\254\275\175\242\245\171" +
"\066\032\157\075\167\355\065\101\257\333\370\265\343\227\236\013" +
"\007\357\071\151\347\264\341\374\131\372\156\321\170\354\171\021" +
"\177\051\053\255\276\217\052\316\220\167\104\010\336\166\203\263" +
"\203\160\041\147\304\273\154\016\022\100\012\030\001\306\200\003" +
"\100\026\140\024\003\100\030\241\144\004\302\010\225\061\003\021" +
"\272\224\014\353\070\254\330\110\366\001\120\112\240\133\040\131" +
"\140\202\002\202\005\344\013\124\026\350\071\006\141\234\003\012" +
"\000\010\143\020\306\074\162\034\272\142\205\176\216\235\034\032" +
"\071\064\162\150\044\320\110\240\221\041\231\203\067\346\271\100" +
"\057\170\146\320\213\042\064\300\161\221\112\331\020\260\022\110" +
"\245\220\112\161\232\024\155\122\050\246\120\114\161\304\024\274" +
"\224\305\100\037\201\036\043\031\243\151\022\126\156\003\162\202" +
"\277\030\155\142\044\343\375\120\010\245\230\053\060\144\314\236" +
"\361\023\200\374\001\052\063\364\314\240\225\241\044\003\075\003" +
"\075\143\273\330\162\046\340\070\031\373\013\172\016\172\016\172" +
"\016\172\316\057\210\257\221\335\346\107\306\117\016\052\011\277" +
"\236\374\077\025\067\145\041\161\006\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\122\115\157\324\060" +
"\020\315\176\166\113\151\213\304\231\033\347\252\111\166\233\104" +
"\075\101\251\120\105\101\210\225\340\200\324\312\115\274\133\027" +
"\257\023\034\157\227\212\277\004\377\201\337\300\237\100\034\370" +
"\017\214\375\146\267\075\304\157\062\236\367\146\374\354\037\377" +
"\242\301\322\106\117\077\237\337\210\133\161\240\205\231\037\114" +
"\235\125\146\176\374\363\367\307\077\177\237\175\177\335\215\242" +
"\157\115\024\105\277\134\324\171\356\242\275\127\262\222\306\175" +
"\220\215\120\366\322\271\150\204\104\010\317\314\072\334\176\047" +
"\127\132\031\031\322\323\106\224\262\365\341\226\260\266\136\205" +
"\144\171\262\264\372\316\207\217\113\321\312\251\232\033\241\375" +
"\357\243\122\213\266\175\263\252\260\247\353\126\276\027\126\232" +
"\300\057\153\135\257\243\305\102\204\126\076\342\266\303\112\264" +
"\327\076\170\122\311\122\055\204\076\063\116\316\045\006\255\344" +
"\214\125\107\225\272\125\125\030\156\120\325\030\270\152\365\071" +
"\017\274\103\361\375\074\373\364\367\351\132\071\331\372\143\004" +
"\272\374\272\024\032\007\232\231\365\244\312\237\135\315\024\272" +
"\355\021\277\266\262\142\037\134\264\273\220\116\124\302\011\256" +
"\037\056\226\072\164\336\367\201\272\174\160\212\221\221\053\256" +
"\332\241\360\201\065\246\166\033\333\106\365\046\334\251\033\151" +
"\066\046\015\033\275\014\303\355\266\327\265\165\270\317\300\160" +
"\033\373\267\234\322\060\140\333\335\065\162\355\313\255\320\367" +
"\241\345\260\163\341\242\356\213\123\132\336\236\372\353\271\042" +
"\157\365\125\135\335\221\173\376\147\026\220\250\214\326\243\277" +
"\103\242\126\301\367\165\171\057\024\217\310\320\227\272\056\277" +
"\040\074\063\106\022\145\213\057\000\311\051\215\032\156\247\325" +
"\045\245\372\204\004\035\372\272\263\222\204\146\145\102\243\053" +
"\343\275\325\312\317\253\274\014\225\221\222\152\204\025\013\152" +
"\337\015\073\153\343\141\054\167\336\246\360\204\343\236\261\064" +
"\347\240\276\272\051\011\207\036\065\005\175\012\070\257\030\055" +
"\366\165\110\214\152\157\235\150\151\236\116\343\155\347\256\375" +
"\046\350\365\032\277\166\375\322\167\341\340\175\047\355\202\066" +
"\234\077\313\300\055\033\217\175\057\342\057\144\255\065\360\121" +
"\305\031\362\216\010\301\333\136\160\266\173\101\267\321\277\154" +
"\216\022\100\012\030\003\046\200\043\100\026\140\034\003\100\030" +
"\243\144\014\302\030\225\061\003\021\172\224\014\353\044\254\330" +
"\110\016\001\120\112\240\133\040\131\140\202\002\202\005\344\013" +
"\124\026\350\071\001\141\222\003\012\000\010\023\020\046\074\162" +
"\034\272\142\205\176\216\235\034\032\071\064\162\150\044\320\110" +
"\240\221\041\231\203\067\341\271\100\057\170\146\320\213\042\064" +
"\300\161\221\112\331\020\260\022\110\245\220\112\161\232\024\155" +
"\122\050\246\120\114\161\304\024\274\224\305\100\037\203\036\043" +
"\031\243\151\022\126\156\003\162\202\277\030\155\142\044\343\303" +
"\120\010\245\230\053\060\144\314\236\361\023\200\374\021\052\063" +
"\364\314\240\225\241\044\003\075\003\075\143\273\330\162\046\340" +
"\070\031\373\013\172\016\172\016\172\016\172\316\057\210\257\221" +
"\335\346\107\306\117\016\052\011\277\236\374\077\215\370\345\325" +
"\147\006\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\016\060\014\001\240\060\114\260\003\003\003" +
"\023\020\063\103\061\043\032\033\046\307\012\304\054\120\314\012" +
"\025\143\202\252\101\127\207\254\227\011\211\215\154\066\272\235" +
"\214\110\362\214\120\163\030\240\366\061\041\211\263\240\271\023" +
"\346\176\020\146\203\142\046\064\063\221\335\210\115\234\001\315" +
"\176\164\177\241\253\147\201\322\354\150\176\301\146\066\272\137" +
"\321\303\025\246\016\026\156\340\060\006\000\231\306\274\363\033" +
"\003\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\215\303\067\012\102\001" +
"\024\004\300\207\340\075\254\004\163\316\071\373\315\071\066\066" +
"\366\042\050\136\310\316\312\322\043\011\336\301\055\266\130\254" +
"\034\230\307\307\334\227\263\271\366\316\360\165\360\136\337\317" +
"\273\313\354\166\062\263\243\307\314\207\176\031\220\101\016\141" +
"\030\043\030\345\230\214\163\002\223\230\342\064\146\070\053\163" +
"\062\217\005\054\142\011\313\262\302\125\256\141\235\033\334\344" +
"\026\266\261\043\273\322\341\236\354\363\200\207\074\342\061\117" +
"\344\024\147\177\234\343\202\227\077\127\270\226\033\336\342\356" +
"\013\242\044\244\360\257\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\235\367\327\046\065" +
"\025\307\357\001\261\141\301\006\042\242\327\206\155\055\273\166" +
"\104\035\305\266\052\040\166\175\001\227\146\057\200\356\272\240" +
"\002\026\100\020\104\354\005\224\266\200\322\301\002\213\330\365" +
"\057\361\047\317\361\147\177\065\367\331\211\233\344\275\067\231" +
"\311\044\063\117\146\362\071\233\223\074\337\334\173\223\163\317" +
"\354\274\363\314\223\231\334\366\037\070\150\347\071\160\340\306" +
"\306\366\303\376\373\257\023\016\277\362\322\143\016\000\330\175" +
"\026\000\374\123\351\007\154\154\077\156\357\216\243\166\375\373" +
"\236\075\132\336\200\312\150\354\074\033\316\007\225\371\156\131" +
"\107\130\331\162\372\201\252\074\304\343\167\220\052\017\145\364" +
"\207\031\355\207\253\362\010\247\377\221\252\034\254\312\243\124" +
"\171\164\253\075\106\225\307\062\261\016\121\345\161\155\373\361" +
"\114\377\023\332\372\211\252\074\251\155\037\352\231\363\141\122" +
"\137\333\377\144\243\175\270\052\117\151\333\107\250\362\124\306" +
"\376\110\125\236\246\312\323\373\145\175\010\270\372\127\041\366" +
"\147\275\001\270\203\024\267\166\361\331\121\073\344\317\371\160" +
"\376\163\146\324\143\375\031\271\307\050\205\121\263\376\314\334" +
"\143\224\202\165\206\271\205\024\267\166\361\331\121\073\344\317" +
"\371\160\376\163\306\312\372\235\244\270\265\213\317\216\332\041" +
"\177\316\207\363\237\063\335\316\060\015\300\327\162\316\002\001" +
"\236\225\063\176\140\354\147\217\075\146\232\363\072\252\353\365" +
"\100\177\364\365\072\343\303\136\257\167\105\371\076\307\150\213" +
"\327\353\003\342\037\021\262\261\316\060\167\221\342\326\056\076" +
"\073\152\207\374\071\037\316\177\316\214\172\015\163\124\356\061" +
"\112\141\324\254\077\067\367\030\245\060\152\326\237\227\173\214" +
"\122\260\316\353\137\046\305\255\135\334\176\004\170\276\317\056" +
"\204\153\247\342\275\240\213\137\311\130\131\077\205\024\267\166" +
"\361\331\041\300\013\135\073\016\145\267\105\262\363\371\315\005" +
"\053\353\067\222\242\153\016\251\217\364\220\037\327\157\152\234" +
"\215\326\102\361\113\303\312\372\065\244\350\272\222\017\053\353" +
"\067\221\242\153\016\251\217\364\220\037\327\157\152\234\215\326" +
"\102\361\113\303\312\372\325\244\350\272\222\017\053\353\167\223" +
"\342\326\261\164\365\037\072\116\211\354\317\372\216\251\247\262" +
"\040\254\143\375\166\122\334\332\305\147\107\355\220\077\347\303" +
"\371\317\031\053\353\237\046\105\327\225\174\130\131\377\312\324" +
"\263\131\012\126\326\077\105\212\256\373\200\231\357\257\143\207" +
"\365\060\075\346\372\042\243\355\133\017\363\342\216\361\136\342" +
"\174\026\357\257\343\246\365\060\015\300\015\324\243\153\301\213" +
"\135\205\104\076\350\131\205\104\375\134\134\123\343\154\332\270" +
"\007\113\376\316\334\016\061\332\233\126\041\345\104\215\367\122" +
"\154\127\041\171\154\216\324\155\053\353\247\222\342\326\056\076" +
"\073\152\253\350\133\175\376\234\217\061\263\155\076\277\222\100" +
"\200\227\111\175\126\326\167\222\242\353\004\243\276\274\243\335" +
"\053\122\214\127\022\126\326\367\220\242\153\016\251\217\364\220" +
"\037\327\157\152\234\215\326\102\361\113\143\231\253\113\015\155" +
"\310\352\322\127\032\355\214\253\113\261\146\335\354\177\225\321" +
"\116\220\165\325\363\152\125\136\343\033\265\022\317\170\277\233" +
"\166\001\001\216\236\172\016\175\100\200\327\306\370\305\145\135" +
"\215\166\114\314\150\113\004\001\136\347\152\235\127\334\135\220" +
"\151\122\377\007\001\136\237\173\214\165\241\133\326\021\340\015" +
"\343\314\147\031\364\077\303\340\352\320\257\014\101\274\206\171" +
"\243\052\157\232\142\106\113\200\317\172\003\360\163\237\027\366" +
"\370\153\032\212\125\042\010\160\154\017\333\067\273\132\275\206" +
"\311\015\002\274\305\325\312\133\277\336\352\157\355\073\107\041" +
"\316\304\353\327\007\216\064\166\326\337\326\167\216\102\234\232" +
"\365\316\131\117\005\346\311\372\333\103\066\065\353\031\142\156" +
"\017\331\130\367\327\257\045\105\327\225\174\130\131\277\216\024" +
"\135\127\362\141\145\375\223\244\350\272\222\017\053\353\327\223" +
"\242\353\076\204\174\250\237\263\061\065\316\306\234\123\314\274" +
"\326\225\121\237\113\172\107\356\061\112\041\352\356\327\073\031" +
"\255\244\247\174\337\145\264\017\065\332\307\305\306\164\342\367" +
"\173\312\367\126\122\334\332\305\147\107\355\220\077\347\303\371" +
"\317\031\361\236\343\361\323\314\047\035\010\160\302\324\163\220" +
"\260\216\365\313\110\241\032\001\336\075\345\254\246\006\001\116" +
"\314\031\337\312\372\267\111\321\165\045\037\126\326\057\047\105" +
"\327\225\174\364\136\205\364\036\117\337\173\023\115\212\213\375" +
"\076\125\336\317\350\037\120\345\203\252\174\310\321\077\234\150" +
"\334\217\250\262\241\312\111\252\234\254\312\051\252\174\164\100" +
"\274\035\252\234\152\035\353\237\240\036\135\163\110\175\244\207" +
"\374\270\176\123\343\154\264\026\212\137\032\121\327\353\247\305" +
"\214\244\374\116\217\361\033\003\004\070\143\314\361\352\057\170" +
"\271\101\200\063\135\155\324\073\002\037\353\140\363\361\334\363" +
"\030\023\024\316\212\341\254\143\275\007\231\234\372\216\073\214" +
"\170\372\155\050\326\065\314\371\244\270\265\213\317\216\332\041" +
"\177\316\207\363\237\063\126\326\177\115\212\133\273\370\354\250" +
"\035\362\347\174\070\377\071\223\356\257\051\166\174\046\133\331" +
"\175\146\350\130\271\100\200\317\016\364\377\234\240\177\336\374" +
"\154\035\353\017\222\242\153\016\251\217\364\220\037\327\157\152" +
"\234\215\326\102\361\113\103\274\323\173\126\252\021\124\254\263" +
"\123\305\352\070\336\071\143\216\027\203\270\272\364\147\251\106" +
"\110\031\153\056\314\362\130\377\302\230\343\305\140\235\327\117" +
"\046\305\255\135\174\166\150\277\131\220\365\157\355\266\110\166" +
"\076\277\271\140\145\375\044\122\334\332\305\147\207\166\326\131" +
"\377\326\156\213\144\347\363\233\013\126\326\167\220\342\326\056" +
"\076\273\146\337\375\343\255\256\056\305\160\333\270\357\355\015" +
"\242\137\111\140\327\267\067\254\356\326\273\265\213\317\216\332" +
"\270\077\353\342\335\177\327\307\230\351\066\237\137\111\140\327" +
"\254\337\106\212\133\273\370\354\250\035\362\347\174\070\377\071" +
"\143\145\375\136\122\334\332\305\147\107\355\220\077\347\303\371" +
"\317\231\376\167\004\032\200\213\250\106\200\057\146\232\324\354" +
"\211\372\005\057\311\133\173\226\114\236\337\222\020\140\127\312" +
"\170\103\101\200\057\115\075\007\023\353\274\276\312\124\263\146" +
"\031\233\043\111\357\364\316\156\075\214\023\223\326\303\354\306" +
"\324\353\141\126\377\013\233\065\373\337\070\107\222\036\353\347" +
"\016\237\317\062\030\165\145\306\171\271\307\050\205\121\263\336" +
"\151\117\231\045\060\152\326\353\333\227\133\066\147\275\001\370" +
"\351\164\363\131\006\154\326\177\074\335\174\226\001\233\365\237" +
"\114\067\237\145\220\356\274\336\000\374\140\370\174\344\330\071" +
"\343\217\115\175\126\043\060\256\176\126\343\253\230\345\131\015" +
"\335\323\000\174\167\310\114\053\141\066\147\035\027\260\272\163" +
"\152\352\335\257\036\163\240\063\314\005\230\353\014\163\325\300" +
"\011\126\002\260\131\377\321\164\363\131\006\111\257\034\277\067" +
"\174\076\162\354\234\361\307\046\151\326\277\077\174\076\162\354" +
"\234\361\307\046\056\353\015\300\057\363\314\147\031\324\347\115" +
"\163\203\000\027\272\132\324\312\214\254\317\343\055\201\131\256" +
"\137\377\372\230\343\305\300\136\071\376\136\027\375\331\364\220" +
"\164\011\316\256\213\157\327\370\045\122\237\150\317\011\002\174" +
"\203\323\331\143\375\076\135\364\147\323\103\322\045\070\073\065" +
"\233\157\166\230\361\105\135\342\227\310\332\355\227\164\361\324" +
"\163\030\203\316\173\310\134\062\316\174\226\301\250\347\365\157" +
"\345\036\243\024\352\267\244\334\040\300\245\256\306\376\065\175" +
"\140\304\111\055\022\066\353\173\251\306\366\255\232\225\364\260" +
"\277\340\325\267\150\146\246\356\052\353\231\163\150\127\331\313" +
"\215\166\306\275\174\075\063\050\342\115\340\312\357\012\354\230" +
"\365\130\160\137\326\277\343\263\351\227\365\006\340\217\222\056" +
"\365\371\372\115\215\263\321\132\050\176\151\364\316\372\237\044" +
"\135\352\363\365\233\032\147\243\265\120\374\322\130\326\031\246" +
"\365\227\366\032\270\062\066\246\023\277\337\136\003\367\220\342" +
"\326\056\076\073\152\207\374\071\037\316\177\316\260\327\353\311" +
"\336\152\203\023\257\336\123\343\137\205\153\270\266\200\275\136" +
"\237\315\157\361\353\012\233\365\331\254\130\136\127\106\275\347" +
"\370\303\334\143\224\302\332\375\252\161\364\324\163\350\003\106" +
"\256\116\254\373\126\307\200\075\236\335\302\272\157\365\004\040" +
"\363\234\027\173\345\230\344\333\102\105\306\372\226\264\132\025" +
"\321\170\126\107\110\175\244\207\374\270\176\123\343\154\264\026" +
"\212\137\032\354\261\136\257\065\062\023\365\336\257\325\312\032" +
"\254\317\140\107\023\275\222\372\027\171\346\263\014\254\363\372" +
"\352\215\355\215\360\346\166\137\037\351\041\077\256\337\324\070" +
"\033\255\205\342\227\106\324\112\352\301\157\366\306\272\342\256" +
"\256\056\315\006\012\337\020\153\326\163\202\000\127\163\172\171" +
"\277\045\245\004\363\374\132\175\115\310\246\356\122\205\023\134" +
"\217\325\173\216\103\300\310\147\021\363\147\035\001\256\315\025" +
"\273\124\352\056\125\046\230\157\227\252\353\314\317\326\267\244" +
"\077\220\242\153\016\251\217\364\220\037\327\157\152\234\215\326" +
"\102\361\113\203\375\335\364\372\351\346\263\014\254\143\175\365" +
"\077\277\361\234\001\244\076\322\103\176\134\277\251\161\066\132" +
"\013\305\057\015\371\274\216\000\067\014\211\254\374\157\354\150" +
"\267\147\310\070\045\022\165\247\067\170\017\245\342\047\333\136" +
"\003\067\245\214\067\067\330\337\222\352\056\045\231\141\263\236" +
"\362\315\015\123\257\163\274\031\327\172\235\143\323\356\067\333" +
"\070\373\316\232\110\175\244\207\374\270\176\123\343\154\264\026" +
"\212\137\032\126\326\127\317\105\066\314\363\221\225\264\324\273" +
"\137\103\100\200\133\142\374\352\332\257\334\040\300\257\134\115" +
"\174\027\122\262\335\351\125\254\133\123\305\162\342\262\273\024" +
"\142\261\373\126\143\031\131\277\135\320\013\332\101\071\367\110" +
"\010\160\172\356\061\142\101\200\073\306\034\217\275\136\377\235" +
"\056\372\263\351\041\351\022\234\135\027\337\256\361\113\204\315" +
"\372\157\165\321\237\115\017\111\227\340\354\272\370\166\215\137" +
"\042\154\326\177\243\213\376\154\172\110\272\004\147\207\165\025" +
"\122\377\265\137\167\006\372\267\306\316\106\371\156\213\365\135" +
"\067\260\323\276\325\276\010\015\363\326\323\112\074\365\133\122" +
"\156\020\340\056\127\053\157\355\027\106\276\107\100\371\334\335" +
"\326\143\274\251\144\323\373\141\114\330\277\246\367\247\236\111" +
"\305\306\272\347\270\132\013\322\170\326\204\110\175\244\207\374" +
"\270\176\123\343\154\264\026\212\137\032\126\326\167\223\242\153" +
"\016\251\217\364\220\037\327\157\152\234\215\326\102\361\113\103" +
"\274\017\163\374\064\363\111\007\256\361\033\117\304\254\337\213" +
"\035\277\005\315\015\034\341\073\061\373\327\064\331\235\322\224" +
"\261\346\004\233\365\121\367\006\130\042\365\133\122\156\220\271" +
"\167\132\364\257\032\354\016\020\130\344\276\032\076\260\276\273" +
"\324\354\277\317\150\327\167\227\112\163\270\037\307\173\167\351" +
"\136\237\015\373\327\364\314\324\063\251\330\260\131\017\076\027" +
"\132\031\106\324\112\352\265\377\153\265\356\130\367\141\126\253" +
"\145\334\332\305\147\107\355\220\077\347\303\371\317\031\366\014" +
"\163\263\131\127\322\303\146\175\021\173\026\115\211\165\206\071" +
"\227\024\135\163\110\175\244\207\374\270\176\123\343\154\264\026" +
"\212\137\032\165\217\366\036\163\240\075\332\037\300\244\173\264" +
"\067\355\056\122\215\147\067\051\251\217\364\220\037\327\157\152" +
"\234\215\326\102\361\113\303\312\372\171\244\350\232\103\352\043" +
"\075\344\307\365\233\032\147\243\265\120\374\322\050\372\356\027" +
"\373\204\273\322\037\314\061\136\112\330\153\230\063\246\233\317" +
"\062\140\337\043\060\233\275\132\326\025\366\130\077\155\272\371" +
"\054\003\066\353\073\251\306\341\357\021\350\264\323\216\262\373" +
"\363\220\161\112\204\075\303\374\145\272\371\054\003\366\130\367" +
"\356\046\126\031\116\357\275\301\256\210\351\033\012\305\316\031" +
"\177\154\330\143\135\174\152\253\211\130\003\231\202\146\266\353" +
"\034\115\025\347\261\342\356\257\123\317\101\202\075\326\057\103" +
"\200\277\341\202\127\334\251\162\142\316\061\254\373\060\227\220" +
"\242\153\016\251\217\364\220\037\327\157\152\234\215\326\102\361" +
"\113\203\075\326\167\111\326\115\304\275\367\024\064\365\376\272" +
"\000\056\343\376\372\337\061\351\375\165\243\347\037\003\047\130" +
"\011\300\236\141\304\267\362\064\021\347\374\024\064\063\073\257" +
"\377\017\135\366\010\163\155\041\001\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\265\127\261\112\003\101" +
"\020\235\073\057\142\171\154\040\140\252\213\106\270\306\040\344" +
"\004\105\213\040\126\142\025\273\244\061\202\105\104\045\046\167" +
"\222\102\202\166\126\026\372\003\101\355\264\261\360\027\154\375" +
"\010\153\077\101\160\057\306\106\346\055\314\262\246\110\212\227" +
"\235\331\231\171\063\363\366\371\223\012\131\237\212\355\335\243" +
"\316\171\247\226\245\335\343\332\126\067\335\073\114\067\116\346" +
"\343\340\375\146\273\347\023\015\173\104\364\066\350\123\370\367" +
"\137\247\137\327\243\365\344\251\062\103\136\233\202\203\156\072" +
"\110\311\157\357\014\173\332\150\376\033\005\257\352\045\273\373" +
"\265\341\321\364\063\034\234\321\210\374\054\377\056\374\000\336" +
"\107\375\352\076\142\000\242\042\105\054\060\333\052\245\253\242" +
"\023\162\123\330\307\055\105\143\016\130\254\137\216\377\373\272" +
"\020\010\232\041\357\043\150\051\026\230\244\175\211\365\241\050" +
"\342\000\371\011\242\146\030\361\221\353\023\025\131\256\240\017" +
"\140\312\173\320\365\220\305\201\235\353\070\104\246\040\251\015" +
"\154\327\161\254\161\200\257\020\031\024\305\054\340\051\100\121" +
"\062\000\125\331\255\110\065\342\130\164\053\330\037\016\001\043" +
"\113\144\154\107\364\301\075\250\157\365\050\034\000\332\071\112" +
"\142\264\354\046\216\113\345\055\100\046\242\000\041\031\370\322" +
"\372\160\136\351\133\355\213\246\250\305\100\056\171\254\163\323" +
"\274\152\240\264\113\001\064\136\061\040\017\320\042\045\212\022" +
"\124\017\124\332\071\134\101\304\053\331\011\015\240\023\356\366" +
"\140\356\274\352\004\200\001\302\216\162\350\303\361\165\205\373" +
"\303\004\040\372\200\376\010\313\215\344\102\064\062\360\011\303" +
"\036\004\133\315\300\022\030\040\116\173\042\252\007\134\364\150" +
"\171\131\150\006\013\311\251\042\107\375\201\263\153\050\224\124" +
"\063\030\263\013\367\040\212\003\316\104\326\071\366\021\226\101" +
"\033\100\122\103\000\053\031\264\040\115\331\005\200\305\202\024" +
"\247\135\053\344\025\167\033\007\124\220\347\256\305\303\104\256" +
"\251\055\064\203\170\302\311\001\137\005\130\267\043\245\057\224" +
"\347\142\062\230\353\001\365\056\170\146\130\314\053\131\005\055" +
"\306\253\225\312\340\013\145\174\033\310\067\216\110\252\131\125" +
"\020\266\201\053\341\065\231\045\242\310\215\242\210\177\374\344" +
"\000\064\005\003\334\224\246\235\365\041\127\175\123\123\337\332" +
"\025\266\116\266\023\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\261\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\025\046\041\304\240\240\060\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\052\061\342\045\000\006" +
"\105\110\250\266\023\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\261\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\310\000\005\025\305\205\014\165\014\114\245\040" +
"\222\165\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\124\142\124\142\124\142\124\142\124\142\124\142" +
"\124\142\124\142\124\142\124\002\111\002\000\202\022\372\136\266" +
"\023\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\313\041\022\001\001" +
"\030\200\321\177\166\070\001\145\157\100\331\240\010\232\021\065" +
"\161\323\006\141\015\063\313\354\032\311\021\234\104\161\011\325" +
"\214\054\153\234\201\242\011\272\367\345\357\035\037\321\156\066" +
"\321\315\363\351\242\330\026\131\123\227\313\154\134\326\263\171" +
"\075\112\257\317\323\360\176\033\044\021\273\052\042\316\357\261" +
"\363\345\133\245\275\326\345\060\251\076\137\277\372\265\146\035" +
"\373\110\020\004\101\020\004\101\020\004\101\020\004\101\020\004" +
"\101\020\004\101\020\004\101\020\004\101\020\004\101\020\344\257" +
"\310\013\156\206\240\054\275\047\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\064\031\206\000\000\000\141\327\006\373\277" +
"\000\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\040\372\376\377\377\277\277\025\000\365\332\371\042\121\000\000" +
"\000"
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\345\124\261\112\304\100" +
"\020\235\215\173\260\047\026\213\126\066\222\353\254\256\262\021" +
"\233\040\026\042\166\226\251\116\260\210\250\304\113\042\251\216" +
"\125\054\122\311\301\375\200\275\066\026\366\376\201\137\342\047" +
"\010\046\240\215\314\073\156\366\162\132\134\223\220\274\314\314" +
"\333\171\357\345\371\203\072\305\220\066\342\343\363\301\315\240" +
"\137\344\311\105\177\077\311\117\316\362\275\313\315\155\375\376" +
"\160\220\006\104\145\112\104\223\154\110\366\367\127\127\237\325" +
"\150\167\347\251\267\102\052\046\175\232\344\131\116\101\174\124" +
"\246\165\323\346\036\352\327\365\227\142\362\323\203\312\354\232" +
"\106\024\024\315\265\123\077\253\346\345\233\125\074\160\153\100" +
"\005\031\162\054\340\214\343\053\034\150\245\021\000\053\152\100" +
"\003\140\225\257\010\141\253\112\074\374\056\244\055\331\256\040" +
"\340\214\105\000\132\342\041\337\052\200\164\175\130\105\010\060" +
"\342\126\024\266\303\112\221\334\045\153\322\223\173\314\100\211" +
"\022\307\000\016\067\076\303\041\020\212\053\376\221\056\214\132" +
"\253\031\244\110\130\121\361\200\225\147\260\275\174\064\333\145" +
"\133\031\116\050\251\244\120\071\261\244\036\266\361\000\200\313" +
"\111\154\177\374\107\360\310\036\070\371\362\011\324\142\174\037" +
"\341\360\056\013\050\310\312\211\343\153\070\241\146\327\170\272" +
"\301\104\316\133\244\053\376\234\367\024\172\122\067\057\207\102" +
"\176\321\211\346\137\032\273\202\171\015\004\217\076\136\014\337" +
"\357\265\006\263\157\217\377\322\242\336\143\124\001\045\263\342" +
"\212\373\006\370\002\311\165\237\366\113\016\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\303\344\342\042\006\001\164\125\171\177\073" +
"\352\054\115\126\053\062\063\060\106\063\260\044\145\226\024\227" +
"\060\060\105\173\125\024\000\015\005\321\012\054\133\205\066\226" +
"\116\206\231\301\120\121\134\310\120\307\300\124\012\042\131\107" +
"\005\106\005\106\005\006\136\200\221\001\112\014\022\367\214\012" +
"\214\040\001\234\211\217\212\022\243\002\243\002\303\074\235\014" +
"\066\077\041\334\003\000\160\263\322\264\143\012\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\345\225\077\113\303\120" +
"\024\305\117\142\012\117\161\010\072\271\110\334\234\072\271\210" +
"\113\020\007\021\067\307\116\025\034\042\052\261\111\044\123\171" +
"\025\204\116\022\350\047\160\326\305\301\317\341\047\361\043\370" +
"\347\105\160\221\173\002\067\304\072\064\224\206\162\172\357\175" +
"\357\235\363\113\236\336\320\053\106\130\037\034\237\017\157\206" +
"\375\042\117\056\372\373\111\176\162\226\357\135\156\154\007\257" +
"\367\007\251\017\224\051\200\131\066\102\370\373\137\127\357\323" +
"\361\356\316\343\326\022\274\001\202\323\044\317\162\370\203\243" +
"\062\165\115\353\173\024\274\254\075\027\263\237\036\050\263\153" +
"\214\341\027\365\167\317\375\366\334\347\323\135\037\202\000\114" +
"\214\130\341\056\003\053\012\326\130\271\302\222\126\001\023\014" +
"\023\134\253\100\026\252\225\110\024\042\332\152\252\036\176\033" +
"\141\223\234\225\057\127\334\361\175\204\114\140\207\170\210\130" +
"\022\174\272\334\007\046\114\246\262\020\132\023\263\125\031\322" +
"\152\231\156\020\221\024\070\313\162\025\063\301\003\077\104\161" +
"\206\023\126\331\316\033\102\255\313\056\254\114\024\167\220\346" +
"\312\260\341\274\202\015\157\002\107\346\003\225\172\370\074\226" +
"\113\121\353\224\101\231\250\126\340\260\212\277\347\243\116\242" +
"\210\001\067\012\314\017\156\055\165\120\153\155\233\370\264\170" +
"\115\220\264\103\213\001\102\046\124\354\001\100\133\121\324\026" +
"\327\250\016\161\346\324\312\014\172\364\155\240\147\260\301\050" +
"\265\203\172\201\132\113\207\167\227\022\204\014\003\012\216\236" +
"\050\072\203\063\250\205\163\201\035\124\243\066\017\014\032\162" +
"\245\016\334\177\206\241\305\203\254\303\015\352\147\320\212\157" +
"\341\013\107\014\274\040\263\016\000\000"
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
        TERMINAL_COUNT = 41;
        GRAMMAR_SYMBOL_COUNT = 91;
        SYMBOL_COUNT = 192;
        PARSER_STATE_COUNT = 198;
        SCANNER_STATE_COUNT = 147;
        DISAMBIG_GROUP_COUNT = 18;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { ex.printStackTrace(); System.exit(1); }
        catch(java.lang.ClassNotFoundException ex) { ex.printStackTrace(); System.exit(1); }
        disambiguationGroups = new java.util.BitSet[18];
        disambiguationGroups[0] = newBitVec(41,5,31);
        disambiguationGroups[1] = newBitVec(41,13,31);
        disambiguationGroups[2] = newBitVec(41,28,31);
        disambiguationGroups[3] = newBitVec(41,25,31);
        disambiguationGroups[4] = newBitVec(41,13,19);
        disambiguationGroups[5] = newBitVec(41,19,28);
        disambiguationGroups[6] = newBitVec(41,5,19);
        disambiguationGroups[7] = newBitVec(41,21,25);
        disambiguationGroups[8] = newBitVec(41,2,21,25);
        disambiguationGroups[9] = newBitVec(41,4,25);
        disambiguationGroups[10] = newBitVec(41,2,4,25);
        disambiguationGroups[11] = newBitVec(41,2,25);
        disambiguationGroups[12] = newBitVec(41,3,25);
        disambiguationGroups[13] = newBitVec(41,1,4,25);
        disambiguationGroups[14] = newBitVec(41,3,4,25);
        disambiguationGroups[15] = newBitVec(41,2,3,4,25);
        disambiguationGroups[16] = newBitVec(41,2,3,25);
        disambiguationGroups[17] = newBitVec(41,20,30);
    }

}
