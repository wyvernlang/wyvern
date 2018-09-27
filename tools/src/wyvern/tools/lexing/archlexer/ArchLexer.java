/*
 * Built at Thu Sep 27 18:05:02 NZST 2018
 * by Copper version 0.7.1,
 *      revision unknown,
 *      build 20140605-2206
 */
package wyvern.tools.lexing.archlexer;

import static wyvern.tools.parsing.coreparser.ArchParserConstants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.tools.lexing.LexerUtils;
import wyvern.tools.parsing.coreparser.ArchParserConstants;
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
        comment_t(6),
        componentKwd_t(7),
        componentsKwd_t(8),
        connectKwd_t(9),
        connectorKwd_t(10),
        connectorsKwd_t(11),
        continue_line_t(12),
        dot_t(13),
        entryPointsKwd_t(14),
        externalKwd_t(15),
        identifier_t(16),
        indent_t(17),
        isKwd_t(18),
        multi_comment_t(19),
        newline_t(20),
        portKwd_t(21),
        providesKwd_t(22),
        requiresKwd_t(23),
        targetKwd_t(24),
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
        public Token runSemanticAction_57()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            Token RESULT = null;
             RESULT = t; 
            return RESULT;
        }
        public List< Token > runSemanticAction_58()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > list = (List< Token >) _children[0];
            @SuppressWarnings("unchecked") List< Token > n = (List< Token >) _children[1];
            List< Token > RESULT = null;
             list.addAll(n); RESULT = list; 
            return RESULT;
        }
        public List< Token > runSemanticAction_59()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
            return RESULT;
        }
        public List< Token > runSemanticAction_60()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > n = (List< Token >) _children[0];
            List< Token > RESULT = null;
            
                                // handles lines that start without any indent
                                RESULT = n;
                            
            return RESULT;
        }
        public List< Token > runSemanticAction_61()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > line = (List< Token >) _children[0];
            List< Token > RESULT = null;
             RESULT = line; 
            return RESULT;
        }
        public List< Token > runSemanticAction_62()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unchecked") List< Token > p = (List< Token >) _children[0];
            @SuppressWarnings("unchecked") List< Token > line = (List< Token >) _children[1];
            List< Token > RESULT = null;
             p.addAll(line); RESULT = p; 
            return RESULT;
        }
        public List< Token > runSemanticAction_63()
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
            Token n = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(n); 
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
        public List< Token > runSemanticAction_70()
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token t = (Token) _children[0];
            List< Token > RESULT = null;
             RESULT = LexerUtils.makeList(t); 
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
             RESULT = token(SINGLE_LINE_COMMENT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_7(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(COMPONENT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_8(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(COMPONENTS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_9(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(CONNECT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_10(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(CONNECTOR, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_11(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(CONNECTORS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_12(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_13(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(DOT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_14(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(ENTRYPOINTS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_15(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(EXTERNAL, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_16(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
            
        RESULT = token(IDENTIFIER, lexeme);
 	
            return RESULT;
        }
        public Token runSemanticAction_17(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_18(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(IS, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_19(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(MULTI_LINE_COMMENT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_20(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(WHITESPACE, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_21(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(PORT, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_22(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(PROVIDES, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_23(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(REQUIRES, lexeme); 
            return RESULT;
        }
        public Token runSemanticAction_24(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            Token RESULT = null;
             RESULT = token(TARGET, lexeme); 
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
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\125\220\301\122\024\061" +
"\020\206\123\342\312\156\051\210\305\231\127\240\314\004\020\312" +
"\223\007\360\000\224\224\113\311\201\303\124\314\304\331\150\066" +
"\031\263\075\073\154\361\112\360\062\274\004\345\301\167\260\223" +
"\316\354\256\227\376\323\235\364\367\167\372\341\057\033\264\201" +
"\355\336\136\374\224\163\271\157\245\253\367\307\020\214\253\077" +
"\076\076\175\173\376\263\167\377\371\005\143\167\015\143\354\022" +
"\330\306\351\227\063\140\103\351\252\363\256\052\001\330\073\031" +
"\324\304\200\126\320\006\235\153\073\022\100\252\311\124\073\230" +
"\345\322\326\167\343\052\144\366\371\246\362\326\273\170\032\051" +
"\077\215\057\343\171\033\317\215\167\230\345\147\157\227\205\276" +
"\361\215\362\316\241\133\116\267\163\352\303\252\043\027\146\153" +
"\025\060\256\325\245\065\116\307\312\240\362\311\156\007\271\141" +
"\161\345\315\332\234\372\016\164\160\322\366\166\246\302\107\346" +
"\207\321\041\246\103\374\105\236\165\323\054\015\246\255\005\123" +
"\256\375\143\344\164\327\233\215\032\037\372\151\267\232\340\347" +
"\110\134\272\005\375\273\065\141\231\277\006\031\152\335\277\036" +
"\316\127\143\164\161\307\263\106\052\142\166\006\046\371\152\060" +
"\276\376\364\365\032\067\041\335\342\002\075\117\255\216\143\340" +
"\200\277\364\242\363\241\002\266\153\127\365\061\072\152\247\064" +
"\066\306\352\014\115\255\257\215\222\066\066\343\116\234\167\067" +
"\343\377\100\103\337\350\040\161\243\310\304\017\324\101\116\201" +
"\275\112\266\127\300\136\226\215\370\100\162\234\204\237\044\051" +
"\336\223\160\222\202\104\220\034\220\034\222\034\221\020\245\040" +
"\112\101\024\101\024\101\024\101\024\101\024\101\024\101\024\101" +
"\024\216\015\033\145\163\234\342\111\212\007\051\036\246\170\224" +
"\042\071\161\202\162\202\162\202\162\202\162\202\362\014\315\015" +
"\104\045\007\236\142\221\242\370\007\326\161\130\364\105\003\000" +
"\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\125\220\301\156\024\061" +
"\014\206\323\102\325\135\101\113\121\317\334\070\127\144\322\226" +
"\126\234\053\016\024\011\151\045\070\040\165\024\062\141\066\220" +
"\165\206\254\147\247\053\136\011\136\206\227\100\034\170\007\234" +
"\070\263\273\134\374\307\036\373\373\075\376\361\127\034\364\121" +
"\234\176\274\375\242\127\372\314\153\150\317\146\030\035\264\257" +
"\176\376\172\377\373\317\263\357\257\367\205\270\357\204\020\157" +
"\121\354\075\107\061\321\320\274\031\232\032\121\074\325\321\314" +
"\035\132\203\175\264\245\166\242\021\265\231\057\054\340\262\224" +
"\216\076\071\150\210\070\346\207\046\370\000\351\065\065\141\221" +
"\072\323\373\230\336\135\000\312\112\333\223\115\141\034\174\154" +
"\002\000\271\225\364\270\244\041\156\047\112\141\271\123\101\007" +
"\275\255\275\003\233\052\007\115\310\166\047\304\215\353\167\301" +
"\355\354\151\357\321\106\320\176\264\163\015\065\271\317\316\306" +
"\224\116\350\057\312\256\207\156\143\260\350\075\272\172\347\077" +
"\246\140\207\321\154\332\205\070\156\173\324\305\260\042\342\306" +
"\055\332\157\275\213\233\374\021\352\330\332\261\173\262\332\256" +
"\061\244\033\057\073\155\230\071\070\234\227\117\173\167\164\005" +
"\015\353\133\362\273\361\066\255\100\313\175\265\353\041\304\006" +
"\305\251\337\326\147\344\146\301\130\272\100\252\056\311\320\207" +
"\326\031\355\323\060\335\003\002\174\230\375\007\232\204\316\106" +
"\115\327\044\046\055\337\106\275\100\261\177\107\236\017\353\116" +
"\275\144\271\312\042\257\263\124\057\130\044\113\305\242\130\316" +
"\131\056\130\056\131\230\122\061\245\142\212\142\212\142\212\142" +
"\212\142\212\142\212\142\212\142\212\244\201\007\165\167\225\343" +
"\165\216\347\071\136\344\170\231\043\073\111\206\112\206\112\206" +
"\112\206\112\206\312\002\055\003\114\145\007\231\143\225\243\372" +
"\007\224\306\076\064\073\003\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\174\031\350\014\024\210\300\016\014\014\114" +
"\100\314\110\145\214\156\046\023\211\366\300\324\062\000\000\033" +
"\227\276\205\117\001\000\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\064\024\030\030\144\200\130\026\212\345\250" +
"\204\345\221\260\002\024\053\102\261\022\016\254\014\305\052\310" +
"\030\000\056\040\326\364\273\000\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\315\330\311\152\024\141" +
"\000\304\361\217\211\161\337\267\030\215\372\105\235\111\342\276" +
"\257\061\216\273\343\202\017\060\227\074\202\012\021\117\336\075" +
"\371\024\336\074\171\364\171\074\011\236\275\132\015\065\350\020" +
"\301\052\013\232\064\374\350\311\120\344\373\247\057\201\376\374" +
"\243\114\256\274\051\023\303\341\140\352\347\267\227\323\037\077" +
"\054\166\112\171\367\252\224\262\200\357\073\303\301\213\257\313" +
"\275\267\337\277\174\032\175\335\055\153\360\132\171\135\336\027" +
"\024\226\156\277\224\347\265\224\016\114\300\072\230\204\365\260" +
"\001\066\302\046\330\014\133\140\053\154\203\355\260\003\166\302" +
"\056\330\015\173\140\057\354\203\375\060\005\007\140\032\016\066" +
"\247\342\176\150\124\200\317\063\160\030\216\300\121\250\060\013" +
"\307\306\352\256\244\232\323\234\255\365\354\316\247\130\047\157" +
"\255\272\113\051\326\311\133\253\356\172\212\165\362\326\252\173" +
"\234\142\235\274\265\352\356\245\130\047\157\255\272\371\024\353" +
"\344\255\125\167\041\305\072\171\153\325\135\116\261\116\336\132" +
"\165\147\123\254\223\267\126\335\305\024\353\344\255\125\367\040" +
"\305\072\171\153\325\075\111\261\116\336\132\165\327\122\254\223" +
"\267\126\335\102\212\165\362\326\252\273\233\142\235\274\265\352" +
"\156\245\130\047\157\255\272\033\051\326\311\133\253\356\176\212" +
"\165\362\326\252\353\267\201\177\101\337\256\073\231\342\311\362" +
"\326\252\073\225\142\235\274\265\352\116\247\130\047\157\255\272" +
"\063\051\326\311\133\253\356\134\212\165\362\326\252\273\232\142" +
"\235\274\265\352\036\245\130\047\157\255\272\147\065\174\217\322" +
"\374\306\372\373\075\312\361\372\227\367\050\160\242\362\075\012" +
"\356\135\230\371\263\010\077\367\140\166\125\335\040\255\253\377" +
"\377\226\147\216\237\347\053\337\362\254\252\273\335\206\346\324" +
"\321\375\137\327\130\335\142\212\047\313\133\253\356\141\212\165" +
"\362\126\257\133\126\346\055\137\143\317\356\116\033\232\123\107" +
"\167\253\256\227\342\311\362\326\252\273\231\142\235\274\265\352" +
"\346\122\254\223\267\126\335\323\272\226\377\127\054\265\201\317" +
"\167\111\171\166\277\000\224\256\052\172\167\031\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\203\146\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\012\223\340\376\377\377\077\126\011\376\277\243\022\303\124" +
"\142\330\307\071\116\017\122\321\347\040\172\360\205\056\330\162" +
"\000\320\003\265\117\141\004\000\000"
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
"\103\105\001\003\003\103\155\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\330" +
"\004\030\031\300\100\001\207\004\043\056\035\244\110\220\152\004" +
"\320\071\064\263\124\200\032\036\302\057\301\302\104\262\121\164" +
"\160\025\135\044\200\036\041\121\002\247\121\054\144\044\033\007" +
"\112\123\336\020\213\153\006\006\246\301\027\327\114\244\007\073" +
"\061\061\067\030\223\073\075\044\024\150\130\002\163\120\315\231" +
"\034\043\067\202\206\273\004\343\150\324\142\026\130\114\203\321" +
"\125\016\270\313\127\001\354\022\015\364\010\253\006\352\031\305" +
"\202\323\203\015\264\367\007\007\056\011\106\322\353\073\016\122" +
"\165\070\340\064\212\245\002\000\215\030\161\052\125\014\000\000" +
""
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\155\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\243" +
"\002\303\132\200\021\044\010\042\007\211\173\206\236\237\206\143" +
"\010\016\172\137\217\314\100\037\015\312\301\340\005\234\266\221" +
"\056\101\137\277\016\254\373\210\267\204\012\052\021\022\000\267" +
"\016\273\220\075\011\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\155\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\100" +
"\076\043\110\220\377\377\377\177\130\045\030\030\024\260\353\000" +
"\221\270\044\370\260\113\044\342\322\301\040\200\135\202\011\247" +
"\035\100\147\141\227\140\305\355\052\001\354\022\034\014\016\330" +
"\045\004\160\030\305\210\323\125\215\070\044\070\160\351\140\141" +
"\302\351\134\376\006\322\134\305\300\170\200\364\210\142\302\045" +
"\301\201\113\202\005\273\204\042\056\073\034\111\215\163\062\022" +
"\003\351\161\316\200\053\316\071\206\130\234\073\340\222\150\030" +
"\112\161\216\073\152\251\027\203\364\210\132\006\106\076\134\022" +
"\214\070\242\226\234\030\244\175\104\341\051\054\107\103\227\322" +
"\100\044\075\110\106\175\076\324\175\076\222\075\310\063\030\075" +
"\110\115\127\221\156\071\007\116\313\111\256\350\111\267\034\267" +
"\121\054\025\000\064\101\070\224\215\014\000\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\316\073\112\103\101" +
"\024\006\340\361\152\214\157\343\373\031\023\123\144\057\026\256" +
"\300\306\045\210\240\270\041\073\053\113\227\044\270\007\377\013" +
"\026\227\024\311\004\333\157\340\143\070\314\231\363\237\217\237" +
"\322\173\171\056\315\303\335\375\327\343\364\365\373\363\275\051" +
"\345\355\251\254\224\366\114\052\134\307\161\247\276\251\374\127" +
"\073\273\246\357\166\246\156\267\077\212\161\245\255\071\263\233" +
"\177\350\057\321\333\146\255\317\344\266\367\152\034\304\060\166" +
"\142\364\367\166\036\027\061\350\314\070\214\263\330\210\323\350" +
"\305\156\134\306\166\234\304\176\154\306\125\354\305\132\047\013" +
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
"\000\000\000\000\000\000\000\000\000\000\000\000\000\026\372\005" +
"\303\377\144\347\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\226\311\123\024\061" +
"\024\207\137\201\270\357\042\210\053\212\042\056\270\357\242\202" +
"\202\072\052\242\210\033\203\200\012\356\013\240\203\270\336\075" +
"\371\127\170\343\304\321\277\307\223\125\236\275\232\256\232\261" +
"\332\046\323\235\116\136\222\137\027\244\352\315\364\164\136\122" +
"\137\175\357\245\247\047\177\123\125\141\214\052\363\371\134\355" +
"\237\237\135\165\337\276\266\124\020\115\214\020\321\147\161\277" +
"\042\237\353\374\061\330\070\376\153\352\173\351\166\003\001\215" +
"\302\050\175\041\101\366\217\052\270\256\024\061\247\370\135\045" +
"\142\156\361\072\210\171\042\346\207\176\207\143\201\210\205\305" +
"\353\105\042\026\213\130\042\311\133\052\142\231\210\345\042\126" +
"\210\130\051\142\125\054\025\304\220\122\125\107\042\172\117\226" +
"\043\133\223\224\107\222\375\322\123\255\326\240\252\221\314\325" +
"\262\122\351\272\132\023\231\253\323\242\342\032\353\024\363\326" +
"\106\157\224\165\265\236\320\372\312\373\320\356\253\015\222\074" +
"\077\147\060\251\202\033\255\122\161\214\115\232\353\352\203\217" +
"\214\365\325\146\102\073\203\334\117\121\025\252\055\326\250\032" +
"\254\272\162\065\266\226\233\160\122\301\155\061\071\356\135\065" +
"\352\056\114\345\152\073\311\135\065\225\131\043\333\053\133\147" +
"\220\217\152\007\044\225\212\253\235\220\124\111\071\273\140\250" +
"\166\103\270\152\216\311\121\247\342\032\173\164\027\146\354\115" +
"\306\244\202\173\143\366\312\306\031\064\247\332\347\215\152\077" +
"\143\250\356\067\055\157\032\325\001\317\161\060\370\366\322\127" +
"\207\050\155\005\323\122\035\326\240\362\177\006\217\260\121\271" +
"\032\074\357\355\107\051\275\253\322\357\143\061\373\122\164\335" +
"\014\372\307\011\273\072\236\220\343\247\333\117\100\122\361\076" +
"\031\116\102\122\331\162\325\002\101\165\312\211\253\323\206\256" +
"\364\251\274\217\214\235\101\025\252\063\316\250\146\337\105\325" +
"\337\105\133\231\242\315\144\275\323\156\077\133\146\037\212\256" +
"\143\243\072\247\100\345\346\014\352\074\031\332\331\251\072\030" +
"\250\170\134\235\147\166\205\131\301\040\056\100\122\311\134\135" +
"\204\242\312\131\167\165\311\300\025\146\005\355\121\135\206\244" +
"\272\142\215\012\363\235\001\142\300\164\173\047\305\125\320\204" +
"\352\252\001\325\114\173\062\230\120\165\071\247\272\306\352\352" +
"\272\103\127\376\053\330\015\107\165\003\302\125\117\102\016\077" +
"\325\115\015\127\374\124\267\064\134\141\166\373\155\110\052\331" +
"\334\035\110\252\160\316\135\143\252\136\013\124\146\256\362\226" +
"\134\231\127\260\017\222\312\304\325\075\110\252\044\127\375\220" +
"\124\003\336\250\006\143\250\060\053\210\111\165\337\051\325\203" +
"\114\273\172\350\234\152\210\315\325\260\143\127\230\025\174\004" +
"\105\365\230\325\325\023\146\127\074\124\117\041\251\236\101\122" +
"\245\351\253\347\126\251\136\150\122\371\165\365\022\222\252\024" +
"\257\234\122\275\126\244\302\164\025\246\032\201\244\302\164\065" +
"\113\145\106\065\352\235\152\314\272\253\067\114\256\060\053\350" +
"\237\352\255\127\252\002\204\253\161\105\127\156\251\336\101\122" +
"\251\366\325\204\123\252\367\231\166\045\233\373\000\111\205\351" +
"\312\036\325\107\110\052\114\127\334\124\237\040\251\060\135\375" +
"\267\356\057\173\025\115\331\147\111\000\000"
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
        SCANNER_STATE_COUNT = 125;
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
