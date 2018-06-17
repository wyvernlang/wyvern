/*
 * Built at Sun Jun 17 04:09:59 GMT 2018
 * by Copper version 0.7.1,
 *      revision unknown,
 *      build 20140605-2206
 */
package wyvern.tools.arch.lexing;

import static wyvern.tools.parsing.coreparser.arch.ArchParserConstants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.tools.arch.lexing.LexerUtils;
import wyvern.tools.parsing.coreparser.arch.ArchParserConstants;
import wyvern.tools.parsing.coreparser.Token;



public class ArchLexer extends edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine<List< Token >,edu.umn.cs.melt.copper.runtime.logging.CopperParserException>
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
        andKwd_t(1),
        architectureKwd_t(2),
        attachmentsKwd_t(3),
        bindingsKwd_t(4),
        colon_t(5),
        comma_t(6),
        comment_t(7),
        componentKwd_t(8),
        componentsKwd_t(9),
        connectKwd_t(10),
        connectorKwd_t(11),
        connectorsKwd_t(12),
        continue_line_t(13),
        dot_t(14),
        entryPointsKwd_t(15),
        externalKwd_t(16),
        identifier_t(17),
        indent_t(18),
        isKwd_t(19),
        multi_comment_t(20),
        newline_t(21),
        portKwd_t(22),
        providesKwd_t(23),
        requiresKwd_t(24),
        valKwd_t(25),
        whitespace_t(26),
        withKwd_t(27);

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
            
    // start with the baseline indentation level
    indents.push("");
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
            case 38:
                RESULT = runSemanticAction_38();
                break;
            case 39:
                RESULT = runSemanticAction_39();
                break;
            case 40:
                RESULT = runSemanticAction_40();
                break;
            case 41:
                RESULT = runSemanticAction_41();
                break;
            case 42:
                RESULT = runSemanticAction_42();
                break;
            case 43:
                RESULT = runSemanticAction_43();
                break;
            case 44:
                RESULT = runSemanticAction_44();
                break;
            case 45:
                RESULT = runSemanticAction_45();
                break;
            case 46:
                RESULT = runSemanticAction_46();
                break;
            case 47:
                RESULT = runSemanticAction_47();
                break;
            case 48:
                RESULT = runSemanticAction_48();
                break;
            case 49:
                RESULT = runSemanticAction_49();
                break;
            case 50:
                RESULT = runSemanticAction_50();
                break;
            case 51:
                RESULT = runSemanticAction_51();
                break;
            case 52:
                RESULT = runSemanticAction_52();
                break;
            case 53:
                RESULT = runSemanticAction_53();
                break;
            case 54:
                RESULT = runSemanticAction_54();
                break;
            case 55:
                RESULT = runSemanticAction_55();
                break;
            case 56:
                RESULT = runSemanticAction_56();
                break;
            case 57:
                RESULT = runSemanticAction_57();
                break;
            case 58:
                RESULT = runSemanticAction_58();
                break;
            case 59:
                RESULT = runSemanticAction_59();
                break;
            case 60:
                RESULT = runSemanticAction_60();
                break;
            case 61:
                RESULT = runSemanticAction_61();
                break;
            case 62:
                RESULT = runSemanticAction_62();
                break;
            case 63:
                RESULT = runSemanticAction_63();
                break;
            case 64:
                RESULT = runSemanticAction_64();
                break;
            case 65:
                RESULT = runSemanticAction_65();
                break;
            case 66:
                RESULT = runSemanticAction_66();
                break;
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
            case 4:
                RESULT = runSemanticAction_4(lexeme);
                break;
            case 5:
                RESULT = runSemanticAction_5(lexeme);
                break;
            case 6:
                RESULT = runSemanticAction_6(lexeme);
                break;
            case 7:
                RESULT = runSemanticAction_7(lexeme);
                break;
            case 8:
                RESULT = runSemanticAction_8(lexeme);
                break;
            case 9:
                RESULT = runSemanticAction_9(lexeme);
                break;
            case 10:
                RESULT = runSemanticAction_10(lexeme);
                break;
            case 11:
                RESULT = runSemanticAction_11(lexeme);
                break;
            case 12:
                RESULT = runSemanticAction_12(lexeme);
                break;
            case 13:
                RESULT = runSemanticAction_13(lexeme);
                break;
            case 14:
                RESULT = runSemanticAction_14(lexeme);
                break;
            case 15:
                RESULT = runSemanticAction_15(lexeme);
                break;
            case 16:
                RESULT = runSemanticAction_16(lexeme);
                break;
            case 17:
                RESULT = runSemanticAction_17(lexeme);
                break;
            case 18:
                RESULT = runSemanticAction_18(lexeme);
                break;
            case 19:
                RESULT = runSemanticAction_19(lexeme);
                break;
            case 20:
                RESULT = runSemanticAction_20(lexeme);
                break;
            case 21:
                RESULT = runSemanticAction_21(lexeme);
                break;
            case 22:
                RESULT = runSemanticAction_22(lexeme);
                break;
            case 23:
                RESULT = runSemanticAction_23(lexeme);
                break;
            case 24:
                RESULT = runSemanticAction_24(lexeme);
                break;
            case 25:
                RESULT = runSemanticAction_25(lexeme);
                break;
            case 26:
                RESULT = runSemanticAction_26(lexeme);
                break;
            case 27:
                RESULT = runSemanticAction_27(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
            return RESULT;
        }
        public List< Token > runSemanticAction_38()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_39()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > n = (List< Token >) _children[0];
            List< Token > RESULT = null;
             RESULT = n; 
            return RESULT;
        }
        public Token runSemanticAction_40()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_41()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_42()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_43()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_44()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_45()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_46()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_47()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_48()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_49()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_50()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_51()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_52()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_53()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_54()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_55()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_56()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public List< Token > runSemanticAction_57()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > list = (List< Token >) _children[0];
            @SuppressWarnings("unchecked") List< Token > n = (List< Token >) _children[1];
            List< Token > RESULT = null;
             list.addAll(n); RESULT = list; 
            return RESULT;
        }
        public List< Token > runSemanticAction_58()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_59()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > n = (List< Token >) _children[0];
            List< Token > RESULT = null;
            
                                // handles lines that start without any indent
                                RESULT = n;
                            
            return RESULT;
        }
        public List< Token > runSemanticAction_60()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > line = (List< Token >) _children[0];
            List< Token > RESULT = null;
             RESULT = line; 
            return RESULT;
        }
        public List< Token > runSemanticAction_61()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > p = (List< Token >) _children[0];
            @SuppressWarnings("unchecked") List< Token > line = (List< Token >) _children[1];
            List< Token > RESULT = null;
             p.addAll(line); RESULT = p; 
            return RESULT;
        }
        public List< Token > runSemanticAction_62()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > list = (List< Token >) _children[0];
            Token n = (Token) _children[1];
            List< Token > RESULT = null;
            
                        list.add(n);
                        RESULT = LexerUtils.<ArchParserConstants>adjustLogicalLine((LinkedList<Token>)list,
                                            virtualLocation.getFileName(), indents, ArchParserConstants.class);
                    
            return RESULT;
        }
        public List< Token > runSemanticAction_63()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_64()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_65()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_66()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_67()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(t); 
            return RESULT;
        }
        public List< Token > runSemanticAction_68()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(t); 
            return RESULT;
        }
        public List< Token > runSemanticAction_69()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(t); 
            return RESULT;
        }
        public Token runSemanticAction_70()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_71()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public Token runSemanticAction_72()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public List< Token > runSemanticAction_73()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > p = (List< Token >) _children[0];
            List< Token > RESULT = null;
            
                    RESULT = p;
                    Token t = ((LinkedList<Token>)p).getLast();
                    RESULT.addAll(LexerUtils.<ArchParserConstants>possibleDedentList(
                        t, virtualLocation.getFileName(), indents, ArchParserConstants.class));
                
            return RESULT;
        }
        public List< Token > runSemanticAction_74()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > p = (List< Token >) _children[0];
            @SuppressWarnings("unchecked") List< Token > list = (List< Token >) _children[1];
            List< Token > RESULT = null;
            
                    // handle the case of ending in an incomplete line
                    RESULT = p;
                    List<Token> adjustedList = LexerUtils.<ArchParserConstants>adjustLogicalLine(
                        (LinkedList<Token>)list, virtualLocation.getFileName(), indents, ArchParserConstants.class);
                    RESULT.addAll(adjustedList);
                    Token t = ((LinkedList<Token>)adjustedList).getLast();
                    RESULT.addAll(LexerUtils.<ArchParserConstants>possibleDedentList(
                        t, virtualLocation.getFileName(), indents, ArchParserConstants.class));
                
            return RESULT;
        }
        public List< Token > runSemanticAction_75()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > list = (List< Token >) _children[0];
            List< Token > RESULT = null;
             RESULT = list; 
            return RESULT;
        }
        public List< Token > runSemanticAction_76()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            List< Token > RESULT = null;
             RESULT = LexerUtils.emptyList(); 
            return RESULT;
        }
        public Token runSemanticAction_1(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(AND, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_2(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(ARCHITECTURE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_3(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(ATTACHMENTS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_4(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(BINDINGS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_5(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(COLON, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_6(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(COMMA, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_7(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(SINGLE_LINE_COMMENT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_8(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(COMPONENT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_9(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(COMPONENTS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_10(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(CONNECT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_11(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(CONNECTOR, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_12(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(CONNECTORS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_13(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_14(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(DOT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_15(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(ENTRYPOINTS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_16(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(EXTERNAL, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_17(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
            
        RESULT = token(IDENTIFIER, lexeme);
 	
            return RESULT;
        }
        public Token runSemanticAction_18(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_19(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(IS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_20(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(MULTI_LINE_COMMENT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_21(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_22(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(PORT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_23(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(PROVIDES, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_24(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(REQUIRES, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_25(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(VAL, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_26(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_27(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WITH, lexeme); 
            return RESULT;
        }
        public int runDisambiguationAction(edu.umn.cs.melt.copper.runtime.io.InputPosition _pos,edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData match)
        throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            return -1;
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
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\125\220\301\156\023\061" +
"\020\206\055\332\320\104\320\122\324\063\257\120\341\270\055\255" +
"\070\161\150\071\120\324\252\251\340\300\141\145\166\115\142\360" +
"\216\027\147\066\333\210\127\152\137\206\227\100\075\360\016\214" +
"\075\336\044\134\346\367\314\356\377\375\143\337\377\025\203\066" +
"\210\203\057\227\337\365\102\037\072\015\323\303\011\006\013\323" +
"\267\017\277\077\375\171\174\365\353\375\023\041\356\032\041\304" +
"\107\024\133\347\127\027\050\206\032\252\017\135\125\040\212\227" +
"\072\224\063\213\246\304\066\230\074\333\327\210\272\234\325\006" +
"\160\236\107\273\137\055\124\304\354\373\235\322\073\017\371\124" +
"\327\072\236\106\361\104\236\170\336\243\163\343\201\272\154\170" +
"\261\032\364\210\347\245\007\240\334\334\356\345\326\207\265\043" +
"\017\346\033\023\264\320\232\302\131\060\161\062\250\174\212\333" +
"\047\156\130\136\173\273\261\261\271\103\023\100\273\076\316\126" +
"\364\223\375\146\115\210\355\220\356\223\167\335\261\253\200\272" +
"\165\150\213\215\173\214\300\164\175\330\250\361\241\337\166\267" +
"\011\176\101\304\125\132\060\077\133\033\126\375\160\261\016\356" +
"\342\373\316\033\135\062\245\263\070\313\237\006\223\333\167\067" +
"\267\164\167\015\313\113\112\071\167\046\006\323\112\077\314\262" +
"\363\241\102\161\340\326\363\011\145\030\050\015\031\343\164\216" +
"\342\231\363\123\133\152\027\315\364\012\340\341\363\344\077\320" +
"\320\067\046\150\172\103\142\322\312\323\240\153\024\117\123\354" +
"\065\212\355\242\121\157\130\116\223\214\137\263\110\226\061\213" +
"\142\071\142\071\146\071\141\141\373\070\333\317\030\306\024\305" +
"\024\305\024\305\024\305\024\305\024\305\024\111\206\255\242\071" +
"\115\365\054\325\243\124\217\123\075\111\225\223\044\103\045\103" +
"\045\103\045\103\045\103\145\206\146\003\257\046\031\313\101\062" +
"\325\161\252\352\037\300\064\261\103\101\003\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\125\220\301\116\033\061" +
"\020\206\015\024\221\250\100\251\070\163\343\214\352\030\050\250" +
"\147\324\003\124\252\024\011\016\225\130\231\135\067\161\353\035" +
"\157\235\331\054\021\257\324\276\014\057\201\172\350\073\060\366" +
"\170\223\364\062\377\314\354\316\367\317\370\367\077\261\335\006" +
"\161\370\355\346\207\236\353\023\247\141\162\062\306\140\141\362" +
"\351\317\363\355\313\337\243\247\317\233\102\074\066\102\210\057" +
"\050\066\216\121\014\064\124\327\135\125\040\212\367\072\224\123" +
"\213\246\304\066\230\334\073\320\210\272\234\326\006\160\226\133" +
"\173\017\026\052\042\366\365\116\351\235\207\234\325\265\216\331" +
"\060\146\064\023\363\175\312\033\017\124\345\201\167\313\106\217" +
"\330\055\075\000\371\346\162\077\227\076\254\046\162\143\266\326" +
"\101\013\255\051\234\005\023\073\333\225\117\166\007\304\015\213" +
"\257\336\256\155\154\036\321\004\320\256\267\263\025\375\144\277" +
"\133\023\142\071\240\173\362\256\073\166\151\120\267\016\155\261" +
"\166\307\020\114\327\233\015\033\037\372\155\367\232\340\347\104" +
"\134\272\005\363\253\265\141\131\017\346\053\343\056\276\357\254" +
"\321\045\123\072\213\323\374\151\343\236\356\326\260\270\041\207" +
"\053\147\242\051\255\363\323\054\072\037\052\024\207\156\325\037" +
"\023\337\100\151\350\346\330\235\241\170\353\374\304\226\332\305" +
"\141\172\001\360\160\067\376\017\064\360\215\011\232\336\217\230" +
"\264\356\044\350\032\305\346\075\171\276\051\032\365\221\345\042" +
"\311\350\003\213\144\031\261\050\226\123\226\063\226\163\026\036" +
"\037\345\361\113\206\061\105\061\105\061\105\061\105\061\105\061" +
"\105\061\105\322\300\126\321\134\244\170\231\342\151\212\147\051" +
"\236\247\310\116\222\241\222\241\222\241\222\241\222\241\062\103" +
"\363\000\257\046\031\313\106\062\305\121\212\352\025\120\366\151" +
"\175\067\003\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\174\031\350\014\024\210\300\016\014\014\114" +
"\100\314\110\105\214\156\036\023\031\166\300\324\063\000\000\245" +
"\225\345\332\117\001\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\064\024\030\030\144\200\130\026\212\345\250" +
"\200\345\221\260\002\024\053\102\261\022\016\254\214\204\125\220" +
"\061\000\333\175\050\216\273\000\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\315\330\311\116\024\121" +
"\030\305\361\233\106\024\305\011\145\020\005\271\210\335\200\014" +
"\212\003\052\363\340\100\073\304\007\350\015\217\000\044\030\126" +
"\354\135\361\024\356\134\271\364\171\130\231\260\166\313\251\344" +
"\164\264\145\301\371\310\111\205\112\176\251\246\163\322\367\237" +
"\332\220\324\217\243\324\276\273\223\332\032\215\172\337\237\303" +
"\057\375\007\337\026\052\051\355\155\245\224\306\361\175\245\121" +
"\377\374\153\263\366\365\367\317\357\315\257\253\351\034\136\273" +
"\333\151\077\241\060\125\127\122\372\224\123\252\100\033\134\200" +
"\166\270\010\227\240\003\056\303\025\350\204\253\160\015\256\303" +
"\015\270\011\135\160\013\156\103\067\364\100\057\364\301\035\350" +
"\207\273\305\251\270\337\153\026\340\363\000\014\302\175\030\202" +
"\014\303\360\240\245\356\271\103\161\242\272\013\075\273\307\016" +
"\254\223\166\241\272\247\016\254\223\166\241\272\227\016\254\223" +
"\166\241\272\167\016\254\223\166\241\272\015\007\326\111\273\120" +
"\335\252\003\353\244\135\250\156\314\201\165\322\056\124\367\304" +
"\201\165\322\056\124\367\314\201\165\322\056\124\067\345\300\072" +
"\151\027\252\233\161\140\235\264\013\325\255\073\260\116\332\205" +
"\352\336\073\260\116\332\205\352\146\035\130\047\355\102\165\343" +
"\016\254\223\166\241\272\025\007\326\111\273\120\335\234\003\353" +
"\244\135\250\356\225\003\353\244\135\250\156\315\201\165\322\056" +
"\124\267\134\006\326\057\207\353\036\071\360\164\151\027\252\233" +
"\160\140\235\264\013\325\115\072\260\116\332\205\352\246\035\130" +
"\047\355\102\165\057\034\130\047\355\102\165\157\035\130\047\355" +
"\102\165\037\263\341\075\112\361\253\371\357\173\224\221\374\337" +
"\173\024\170\230\371\036\005\367\052\014\374\133\204\277\153\060" +
"\174\242\256\356\250\313\147\173\313\063\312\317\143\231\157\171" +
"\116\324\055\224\241\070\265\171\077\355\152\251\233\167\340\351" +
"\322\056\124\367\306\201\165\322\116\257\333\124\346\045\137\055" +
"\317\156\251\014\305\251\315\173\250\256\346\300\323\245\135\250" +
"\356\265\003\353\244\135\250\156\324\201\165\322\056\124\367\041" +
"\237\347\377\025\213\145\340\263\135\124\236\335\061\161\121\140" +
"\204\167\031\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\203\146\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\012\223\340\376\377\377\077\126\011\376\337\243\022\043\115" +
"\142\270\044\006\234\376\240\242\007\101\364\340\013\104\260\345" +
"\000\347\075\021\027\141\004\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\203\146\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\072\052\061\052\061\052\061\074\045\000\306\102\154\302\141" +
"\004\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\203\146\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\072\052\061\052\061\052\061\074\045\000\306\102\154\302\141" +
"\004\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\044\072\332\047\053\261\054\121\257\264\044\063\107\317" +
"\051\263\044\070\265\304\132\362\322\273\215\346\317\356\030\061" +
"\061\060\124\024\060\060\060\150\002\025\012\143\121\227\053\251" +
"\301\162\276\317\245\000\246\116\246\000\017\050\055\144\250\143" +
"\140\032\125\065\252\152\124\325\250\252\121\125\303\135\025\000" +
"\304\217\370\202\141\006\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\144\030\350\014\000\325\317\154\352\213\000" +
"\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\040\146\340\377\377\377\177\005\000\010\240\331\367\121\000\000" +
"\000"
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\220\050\056\142\020\100\127\225\367\267\243\316" +
"\322\144\265\042\063\003\143\064\003\113\122\146\111\161\011\003" +
"\123\264\127\105\001\320\120\020\255\300\262\125\150\143\351\144" +
"\230\031\100\103\012\031\352\030\230\112\101\044\053\066\001\106" +
"\220\040\003\023\056\011\006\006\005\242\165\220\152\066\036\113" +
"\035\110\324\241\100\103\327\220\142\051\176\035\054\054\070\175" +
"\354\100\173\347\342\016\156\005\122\045\160\032\305\101\275\100" +
"\144\140\150\240\177\224\222\054\201\073\112\007\322\125\070\143" +
"\216\211\364\100\044\046\036\006\143\020\120\063\064\031\230\110" +
"\324\341\100\202\035\070\124\012\320\336\143\034\303\076\346\106" +
"\045\206\272\004\003\313\000\132\336\200\273\104\020\300\052\001" +
"\042\250\145\071\043\075\102\227\021\227\004\007\035\054\147\302" +
"\045\041\100\007\313\033\160\106\055\007\311\325\003\113\005\000" +
"\346\026\163\154\330\013\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\220\050\056\142\020\100\127\225\367\267\243\316" +
"\322\144\265\042\063\003\143\064\003\113\122\146\111\161\011\003" +
"\123\264\127\105\001\320\120\020\255\300\262\125\150\143\351\144" +
"\230\031\100\103\012\031\352\030\230\112\101\044\353\250\300\320" +
"\021\140\004\011\062\060\015\032\367\014\132\201\021\021\120\304" +
"\173\162\200\203\143\104\304\306\310\022\240\151\224\342\064\234" +
"\164\011\372\372\204\026\356\033\262\226\040\124\002\000\270\353" +
"\346\045\340\010\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\220\050\056\142\020\100\127\225\367\267\243\316" +
"\322\144\265\042\063\003\143\064\003\113\122\146\111\161\011\003" +
"\123\264\127\105\001\320\120\020\255\300\262\125\150\143\351\144" +
"\230\031\100\103\012\031\352\030\230\112\101\044\053\220\317\010" +
"\022\344\377\377\377\037\126\011\006\046\034\072\200\100\001\273" +
"\304\041\234\072\024\260\113\060\341\264\203\013\267\345\016\270" +
"\234\053\200\135\102\001\227\121\002\014\015\130\045\030\161\272" +
"\212\111\036\273\004\007\116\035\070\055\147\156\300\056\301\302" +
"\202\323\347\016\270\134\305\300\107\142\014\066\341\222\160\042" +
"\065\006\251\031\265\244\307\040\003\216\030\004\106\055\166\035" +
"\324\214\132\146\234\106\221\034\265\114\014\034\270\044\130\160" +
"\111\060\321\072\152\161\307\040\316\210\042\071\076\310\211\050" +
"\146\134\022\062\064\217\301\201\215\050\234\301\076\162\103\027" +
"\147\040\342\012\053\052\372\174\324\203\243\036\034\054\036\224" +
"\030\310\022\216\144\127\121\323\162\046\134\106\011\320\301\162" +
"\152\126\023\054\025\000\214\207\266\354\020\014\000\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\315\071\112\104\101" +
"\024\005\320\352\337\266\363\320\016\355\074\266\340\136\014\134" +
"\101\047\056\101\004\245\067\144\146\144\350\222\004\367\340\375" +
"\360\203\217\201\226\230\236\202\303\245\250\127\367\275\176\226" +
"\321\323\143\151\146\267\167\357\367\067\317\037\157\057\115\051" +
"\363\207\062\050\355\271\256\160\022\107\275\373\146\345\277\332" +
"\356\232\271\151\227\153\135\236\305\166\134\125\152\176\350\076" +
"\377\207\305\077\314\266\273\106\337\366\266\171\021\353\261\037" +
"\113\161\334\275\355\304\156\254\366\072\016\142\034\247\161\031" +
"\303\330\213\111\054\304\126\254\304\162\034\306\106\014\172\273" +
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
"\000\000\000\000\000\000\000\000\000\000\000\000\000\340\127\137" +
"\265\066\217\240\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\226\107\163\024\061" +
"\020\205\125\066\046\147\014\306\344\150\162\316\140\300\144\026" +
"\060\046\247\045\230\234\301\066\254\341\304\235\023\277\202\033" +
"\047\216\374\036\116\124\161\346\212\004\263\324\324\254\342\214" +
"\244\176\263\154\127\365\112\253\171\032\175\365\244\236\322\227" +
"\237\254\243\066\302\332\253\325\112\327\257\357\003\335\237\076" +
"\366\266\061\366\176\210\361\037\076\336\126\255\364\177\033\354" +
"\031\375\361\365\163\175\170\005\003\213\332\060\373\300\070\335" +
"\077\062\321\157\347\071\046\351\167\044\255\310\261\111\073\216" +
"\347\170\236\023\222\377\023\123\232\111\251\276\310\311\074\247" +
"\044\375\251\074\247\361\234\316\163\106\062\066\063\151\147\045" +
"\255\232\254\063\325\146\373\235\212\161\033\175\366\271\154\255" +
"\264\246\221\014\046\202\173\066\133\061\117\266\326\034\346\333" +
"\263\056\007\355\134\133\041\314\071\353\316\150\364\144\363\042" +
"\222\065\173\005\314\327\150\144\372\162\170\266\300\126\350\235" +
"\154\241\207\167\054\022\077\044\265\271\130\261\226\175\155\306" +
"\374\152\210\134\122\230\154\151\040\262\146\376\152\244\307\227" +
"\131\352\145\236\055\127\350\345\144\261\302\270\136\023\357\146" +
"\275\277\122\241\357\121\314\023\261\112\361\176\277\144\341\153" +
"\223\222\154\165\124\262\065\016\144\264\265\271\326\126\370\037" +
"\324\246\111\277\216\025\335\315\126\155\212\130\357\104\006\023" +
"\015\144\033\162\244\313\074\153\155\003\331\106\200\334\044\332" +
"\240\347\154\263\146\236\154\255\160\025\260\105\243\227\221\155" +
"\215\102\266\315\240\247\363\154\073\054\331\216\340\144\261\302" +
"\375\266\235\327\263\235\006\075\155\155\252\364\273\202\222\355" +
"\056\100\026\336\263\075\260\144\072\317\366\006\047\203\211\046" +
"\270\071\356\043\047\153\335\317\334\357\147\275\005\163\277\207" +
"\167\374\111\347\012\070\140\320\144\365\376\053\340\240\141\045" +
"\072\262\030\137\215\076\022\262\103\026\144\170\236\035\206\040" +
"\073\242\230\107\117\146\273\233\107\243\221\035\163\044\303\365" +
"\314\017\331\161\130\062\134\317\116\220\220\121\337\063\324\167" +
"\015\230\310\265\233\047\065\032\231\276\374\025\120\041\047\073" +
"\245\130\013\327\263\270\144\247\275\221\235\361\114\206\353\231" +
"\310\376\150\144\147\035\311\302\172\066\240\130\201\236\054\304" +
"\071\073\107\112\166\136\103\206\353\331\005\162\262\213\012\062" +
"\134\317\056\025\046\273\034\210\214\306\263\053\060\144\127\015" +
"\172\034\317\164\144\327\310\310\256\033\310\160\075\273\001\113" +
"\126\215\112\166\323\201\014\167\067\145\355\055\130\062\221\267" +
"\203\221\335\051\110\106\357\331\140\141\262\273\201\310\212\173" +
"\166\017\226\354\076\054\131\250\163\126\157\037\300\222\145\365" +
"\017\111\310\036\131\220\025\367\354\161\016\262\162\234\263\047" +
"\060\144\117\045\317\061\310\312\263\233\055\262\277\361\014\226" +
"\114\245\177\016\113\206\353\231\216\354\005\054\331\313\050\144" +
"\257\162\220\345\337\315\327\016\144\161\317\331\020\054\231\313" +
"\156\016\303\222\215\220\223\275\061\150\350\310\312\167\316\342" +
"\222\275\205\045\253\301\222\371\330\315\121\130\062\134\317\312" +
"\105\366\016\226\314\312\263\337\323\355\205\302\131\110\000\000" +
""
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
public ArchLexer() {}

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
    public List< Token > parse(java.io.Reader input,String inputName)
    throws java.io.IOException,edu.umn.cs.melt.copper.runtime.logging.CopperParserException
    {
    this.charBuffer = edu.umn.cs.melt.copper.runtime.io.ScannerBuffer.instantiate(input);
    setupEngine();
    startEngine(edu.umn.cs.melt.copper.runtime.io.InputPosition.initialPos(inputName));
    List< Token > parseTree = (List< Token >) runEngine();
    return parseTree;
    }


    /*************************** LEXER  STATE ***************************/
    Stack<String> indents = new Stack<String>();

    /************************* HELPER FUNCTIONS *************************/

	/** Wraps the lexeme s in a Token, setting the begin line/column and kind appropriately
	 *  The current lexical location is used.
	 */
	Token token(int kind, String s) {
		// Copper starts counting columns at 0, but we want to follow convention and count columns starting at 1
		return LexerUtils.makeToken(kind, s, virtualLocation.getLine(), virtualLocation.getColumn()+1);
	}



    static
    {
        TERMINAL_COUNT = 28;
        GRAMMAR_SYMBOL_COUNT = 37;
        SYMBOL_COUNT = 77;
        PARSER_STATE_COUNT = 41;
        SCANNER_STATE_COUNT = 120;
        DISAMBIG_GROUP_COUNT = 1;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { ex.printStackTrace(); System.exit(1); }
        catch(java.lang.ClassNotFoundException ex) { ex.printStackTrace(); System.exit(1); }
        disambiguationGroups = new java.util.BitSet[1];
    }

}
