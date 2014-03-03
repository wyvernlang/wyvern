/*
 * Built at Sat Mar 01 22:09:55 EST 2014
 * by Copper version 0.7.1,
 *      revision 1cd57156c790d7c88540b5f453389b9ca39fae06,
 *      build 20131117-2243
 */
package wyvern2.parsing;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




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
        arrow_t(5),
        classKwd_t(6),
        closeParen_t(7),
        colon_t(8),
        comma_t(9),
        comment_t(10),
        dash_t(11),
        decimalInteger_t(12),
        defKwd_t(13),
        divide_t(14),
        dot_t(15),
        dsl_t(16),
        equals_t(17),
        fnKwd_t(18),
        identifier_t(19),
        ignoredNewline(20),
        metadataKwd_t(21),
        mult_t(22),
        multi_comment_t(23),
        openParen_t(24),
        plus_t(25),
        shortString_t(26),
        tarrow_t(27),
        tilde_t(28),
        typeKwd_t(29),
        valKwd_t(30),
        varKwd_t(31);

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
            case 24:
                RESULT = runSemanticAction_24(lexeme);
                break;
            default:
        runDefaultTermAction();
                 break;
            }
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
              parenLevel--; 
            return RESULT;
        }
        public java.lang.Object runSemanticAction_24(final String lexeme)
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
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Dedent_t;
	
        }
        public int disambiguate_3(final String lexeme)
        throws edu.umn.cs.melt.copper.runtime.logging.CopperParserException
        {
            @SuppressWarnings("unused") final int Indent_t = 3;
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
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
            @SuppressWarnings("unused") final int ignoredNewline = 20;
            
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
"\020\165\346\227\011\220\200\304\232\053\104\364\217\020\261\102" +
"\202\240\010\004\121\022\261\141\061\062\155\317\304\221\333\335" +
"\161\173\146\202\340\110\160\031\056\201\130\160\007\354\172\325" +
"\201\215\353\377\352\275\362\367\077\142\272\366\342\361\247\167" +
"\327\162\043\017\255\164\253\303\213\340\215\133\275\370\361\363" +
"\343\257\337\117\276\276\031\011\161\333\011\041\276\005\061\176" +
"\375\341\044\210\203\127\132\151\027\316\165\047\215\137\204\040" +
"\346\110\220\173\352\006\167\357\275\336\132\343\164\362\167\245" +
"\367\355\066\171\367\152\053\373\376\355\126\245\340\101\155\333" +
"\136\237\111\257\035\165\325\255\155\007\257\151\044\241\044\217" +
"\021\147\112\366\127\311\171\244\164\155\032\151\117\135\320\053" +
"\015\016\112\057\031\165\256\314\306\050\332\073\125\155\200\355" +
"\055\225\364\315\132\332\236\126\054\335\300\302\044\312\146\151" +
"\200\164\140\126\256\365\132\061\375\040\366\033\035\244\222\101" +
"\162\377\254\131\133\102\175\230\034\263\370\217\341\375\266\323" +
"\356\116\317\254\263\153\332\265\337\137\265\076\340\256\104\043" +
"\334\335\143\067\030\013\256\173\341\113\247\007\011\033\151\377" +
"\271\236\335\351\305\345\313\363\313\040\106\062\005\164\310\040" +
"\166\124\374\227\050\076\172\221\353\150\131\107\044\343\022\163" +
"\153\022\276\351\244\227\115\154\034\245\160\076\150\211\161\023" +
"\343\261\363\161\176\332\176\276\256\243\235\104\313\241\141\233" +
"\312\363\066\121\223\175\204\336\351\222\054\106\034\167\251\072" +
"\112\317\044\020\205\111\320\276\211\203\141\335\131\235\302\070" +
"\227\064\016\343\323\344\051\316\304\117\214\030\121\051\275\076" +
"\002\223\276\263\070\267\350\262\034\246\210\305\105\207\267\244" +
"\124\216\112\136\220\051\121\172\112\101\165\214\022\242\074\103" +
"\022\375\025\367\347\324\137\321\373\214\122\005\003\042\312\217" +
"\140\236\243\206\251\002\253\213\012\065\254\051\260\246\300\232" +
"\014\055\031\220\063\172\131\003\240\262\143\312\035\321\313\051" +
"\040\144\350\006\152\011\270\022\160\045\066\226\340\126\202\133" +
"\211\351\022\003\025\153\147\265\230\253\060\127\141\256\302\134" +
"\305\232\330\360\076\126\301\267\100\147\206\050\343\133\360\341" +
"\253\277\111\307\344\366\056\004\000\000"
});

public static final byte[] symbolDisplayNamesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\115\121\313\156\024\061" +
"\020\364\276\222\015\220\200\304\231\033\347\210\171\021\242\134" +
"\221\120\004\102\010\044\056\110\131\231\261\167\343\310\343\061" +
"\036\357\056\010\076\011\176\206\237\100\034\370\007\354\256\236" +
"\300\305\375\256\256\152\177\377\043\026\333\040\036\176\170\165" +
"\043\167\362\324\112\267\071\175\027\203\161\233\213\037\077\337" +
"\377\372\375\350\353\213\251\020\237\275\020\342\133\024\223\307" +
"\121\234\074\327\112\273\370\126\173\151\302\052\106\261\104\202" +
"\334\113\067\272\107\257\365\336\032\247\263\177\050\103\350\367" +
"\331\273\323\132\071\014\057\367\052\007\367\132\333\017\372\215" +
"\014\332\121\127\333\333\176\364\272\116\022\112\366\030\361\100" +
"\311\341\072\073\017\224\156\115\047\355\245\213\172\243\301\101" +
"\351\065\243\056\225\331\031\105\173\027\252\217\260\203\245\222" +
"\376\264\225\166\240\025\153\067\262\060\231\262\131\033\040\235" +
"\230\215\353\203\126\114\077\212\343\116\107\251\144\224\334\177" +
"\320\155\055\241\336\317\216\131\375\307\360\156\357\265\273\325" +
"\163\340\355\226\166\035\017\327\175\210\270\052\321\210\267\367" +
"\070\214\306\202\353\121\374\342\365\050\141\047\355\077\067\260" +
"\073\271\212\142\052\263\034\072\142\112\250\050\146\111\170\362" +
"\022\317\351\272\115\050\306\145\326\326\144\154\343\145\220\135" +
"\152\234\346\160\071\352\110\161\227\342\231\013\151\176\321\177" +
"\274\151\223\235\047\313\241\141\233\313\313\076\323\222\103\202" +
"\236\370\054\211\021\147\076\127\247\371\231\107\242\060\217\072" +
"\164\151\060\156\275\325\071\114\163\131\337\070\276\310\236\342" +
"\114\372\300\204\221\124\322\033\022\322\125\022\067\137\371\242" +
"\204\251\122\141\345\361\326\224\052\121\051\053\062\065\112\117" +
"\050\150\316\121\102\124\026\110\242\277\341\376\222\372\033\172" +
"\237\122\252\142\100\104\345\031\314\063\324\060\125\141\165\325" +
"\240\206\065\025\326\124\130\123\240\245\000\162\101\057\153\000" +
"\124\161\116\271\063\172\071\005\204\002\335\100\255\001\127\003" +
"\256\306\306\032\334\152\160\253\061\135\143\240\141\355\254\026" +
"\163\015\346\032\314\065\230\153\130\023\033\336\307\052\370\026" +
"\350\054\020\025\174\013\076\174\363\027\337\140\252\011\044\004" +
"\000\000"
});

public static final byte[] symbolNumbersHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\152\030\006\030\050\014\040\166\140\140\140" +
"\002\142\126\050\315\010\305\154\100\314\216\304\007\141\026\250" +
"\032\146\050\237\031\011\043\213\063\240\351\103\027\147\100\223" +
"\143\102\062\007\335\076\106\044\267\061\343\321\007\223\103\127" +
"\007\323\313\012\365\023\230\006\000\012\365\315\240\013\002\000" +
"\000"
});

public static final byte[] productionLHSsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\154\025\030\030\024\200\130\011\212\225\241" +
"\130\005\212\125\201\130\015\210\325\201\130\003\212\065\241\130" +
"\013\210\165\240\130\027\210\365\220\260\076\022\066\100\302\206" +
"\120\154\004\305\306\120\154\002\305\246\110\330\014\210\315\161" +
"\140\013\050\266\104\303\126\100\154\215\204\155\240\330\026\212" +
"\355\100\030\000\277\277\317\261\017\001\000\000"
});

public static final byte[] parseTableHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\234\133\167\325\104" +
"\024\307\367\152\213\212\042\126\121\261\050\270\105\004\021\257" +
"\134\024\241\110\324\242\026\213\105\251\327\203\132\173\203\026" +
"\054\345\322\122\140\055\336\171\362\123\360\346\023\217\176\036" +
"\237\134\313\147\136\235\071\114\026\223\311\236\234\311\234\114" +
"\046\231\361\267\326\254\177\317\116\262\063\154\346\144\046\073" +
"\073\347\317\177\141\303\352\025\030\354\164\306\267\336\377\173" +
"\162\344\217\073\243\003\000\353\053\000\260\306\354\003\235\361" +
"\123\177\115\357\136\373\347\336\335\324\174\034\042\146\365\062" +
"\334\006\026\011\175\024\020\272\333\051\373\040\153\103\322\347" +
"\015\102\037\141\355\121\223\263\263\375\036\143\155\043\153\217" +
"\113\266\047\130\333\304\332\223\254\155\146\355\051\326\206\211" +
"\143\237\326\370\174\106\350\026\326\236\025\177\077\107\354\367" +
"\074\153\133\131\173\241\167\024\154\140\236\107\252\364\347\232" +
"\207\121\110\000\056\160\213\252\072\133\110\070\033\013\333\252" +
"\364\347\232\314\130\370\202\133\124\325\331\102\302\331\130\170" +
"\261\112\177\256\311\214\205\005\156\121\125\147\223\101\200\227" +
"\172\235\211\355\263\275\277\276\366\007\073\377\016\335\066\147" +
"\143\341\345\052\375\271\306\131\024\260\112\177\256\161\026\205" +
"\127\252\364\347\032\175\024\022\200\271\372\373\343\207\314\325" +
"\361\147\156\111\065\046\012\307\302\154\375\375\361\103\146\054" +
"\234\345\226\124\143\342\141\024\246\175\167\305\043\231\261\260" +
"\304\055\252\352\154\041\221\211\302\247\334\222\152\114\144\242" +
"\360\271\357\336\370\042\023\205\117\270\045\325\230\320\317\224" +
"\010\260\263\376\376\370\041\063\026\076\343\026\125\165\266\220" +
"\050\134\065\315\324\337\037\077\024\176\043\136\255\277\077\176" +
"\050\214\302\256\372\373\343\007\072\012\010\360\132\002\360\261" +
"\227\056\171\300\115\176\201\002\001\166\273\076\207\164\256\075" +
"\145\366\067\217\002\363\374\272\145\247\032\005\002\354\125\155" +
"\316\162\115\157\124\351\317\065\146\121\100\200\175\204\155\120" +
"\371\134\372\071\145\057\230\237\067\205\016\013\175\313\322\117" +
"\356\071\245\114\146\325\164\236\133\124\325\331\102\102\073\107" +
"\274\235\000\134\367\322\045\017\320\121\110\000\072\352\236\150" +
"\160\165\244\216\253\023\004\170\307\140\237\167\125\133\224\163" +
"\304\173\252\255\371\125\034\302\126\246\212\143\277\320\003\130" +
"\131\025\007\266\057\012\007\205\126\130\313\202\355\213\302\041" +
"\241\175\104\041\001\070\243\354\375\176\301\277\142\110\267\315" +
"\024\124\242\040\331\077\060\070\366\060\153\037\262\166\204\265" +
"\243\026\347\036\145\355\230\361\252\351\243\222\336\053\277\057" +
"\301\356\177\220\033\242\234\043\162\367\312\316\356\043\162\031" +
"\134\154\100\206\237\365\141\214\262\027\107\201\035\165\302\141" +
"\247\032\103\346\076\142\221\133\124\325\331\102\042\023\205\337" +
"\271\105\125\235\055\044\314\256\013\011\300\004\127\214\056\023" +
"\057\203\201\077\275\353\157\216\300\036\125\240\154\373\270\215" +
"\337\036\076\117\226\334\377\113\302\066\041\177\326\346\027\116" +
"\225\356\336\203\343\276\262\071\316\067\332\374\302\117\066\336" +
"\154\217\363\215\166\054\114\332\170\143\307\235\256\240\123\265" +
"\223\231\051\177\343\226\124\143\342\377\372\005\116\046\012\335" +
"\053\133\322\322\053\134\077\144\242\320\275\026\044\226\327\204" +
"\066\323\367\172\241\357\054\113\201\357\257\135\371\126\041\163" +
"\115\337\311\032\003\205\365\013\337\324\337\037\077\344\243\200" +
"\112\336\061\006\310\157\304\017\134\021\140\312\123\247\152\207" +
"\214\102\064\337\204\024\062\012\123\262\306\000\031\205\157\145" +
"\215\001\363\365\102\002\360\253\373\376\370\041\312\347\021\271" +
"\061\156\234\161\013\172\005\245\315\057\174\157\343\015\305\054" +
"\333\066\310\253\343\145\271\245\066\131\165\120\333\115\155\076" +
"\211\356\011\335\217\224\235\034\013\127\344\226\332\144\325\101" +
"\155\107\042\027\211\236\353\237\124\354\307\002\326\123\357\170" +
"\126\150\256\212\243\244\037\303\172\107\013\317\076\252\076\255" +
"\336\373\305\062\125\237\027\271\105\125\235\055\044\234\135\035" +
"\177\251\322\237\153\252\215\002\266\164\225\115\316\021\253\376" +
"\372\343\007\062\012\327\270\042\300\264\237\076\325\017\231\161" +
"\213\370\331\224\156\017\154\137\325\347\214\320\032\176\307\015" +
"\353\131\057\314\012\365\274\152\302\366\215\205\071\241\341\214" +
"\205\171\241\236\306\102\322\322\132\004\033\202\250\006\137\100" +
"\147\325\340\211\303\352\353\246\101\216\005\062\023\101\201\236" +
"\307\202\264\057\037\013\347\054\316\135\342\315\200\104\124\327" +
"\141\124\157\022\122\044\055\273\117\054\103\220\353\205\105\241" +
"\341\254\027\226\204\066\040\343\206\175\374\366\057\006\235\175" +
"\055\002\233\233\211\047\063\206\321\105\201\174\277\043\223\175" +
"\355\316\267\252\352\154\041\141\274\136\310\125\327\207\104\260" +
"\277\313\262\134\146\377\306\317\224\227\204\066\140\246\054\360" +
"\336\326\167\145\126\344\317\205\367\224\153\245\272\327\142\310" +
"\034\164\243\152\013\352\200\274\263\136\227\065\006\310\261\120" +
"\130\243\020\042\344\130\270\051\153\014\220\121\250\355\275\204" +
"\246\100\106\301\252\276\255\315\070\273\233\162\361\133\034\127" +
"\253\366\231\102\136\035\257\271\072\133\123\041\277\021\143\262" +
"\306\000\031\205\025\271\245\066\131\165\120\333\115\155\076\041" +
"\243\160\111\156\251\115\126\035\324\166\123\233\117\310\050\054" +
"\313\055\265\311\252\203\332\216\101\345\035\061\234\067\003\162" +
"\165\133\344\130\260\372\355\205\066\103\106\301\331\274\334\124" +
"\310\050\334\220\065\006\310\050\334\222\065\006\214\163\320\225" +
"\147\316\232\004\071\026\116\310\032\003\144\024\346\145\215\201" +
"\377\000\006\140\215\340\355\170\000\000"
});

public static final byte[] shiftableSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\305\225\275\112\303\140" +
"\024\206\117\342\227\222\261\150\007\335\052\164\160\352\324\105" +
"\134\104\072\211\233\143\246\012\016\021\225\330\174\051\231\212" +
"\223\070\111\210\010\056\275\002\035\164\160\163\360\016\024\257" +
"\300\033\360\022\004\133\260\213\344\371\340\224\210\031\222\341" +
"\315\371\171\317\317\173\356\077\045\310\206\262\022\355\035\015" +
"\106\203\156\146\343\343\356\116\154\367\017\355\326\311\332\206" +
"\171\275\352\047\276\110\236\210\310\050\035\112\363\367\137\247" +
"\137\227\343\315\336\335\372\222\170\221\230\203\330\246\126\374" +
"\150\067\117\246\116\147\337\266\171\132\176\314\256\347\076\074" +
"\371\171\362\364\114\306\342\147\263\167\060\007\076\046\275\355" +
"\112\100\156\115\265\205\024\215\233\277\266\360\212\013\155\014" +
"\075\120\230\046\001\101\175\026\236\262\044\013\270\322\247\313" +
"\301\221\240\036\130\205\326\142\077\002\155\007\047\255\163\065" +
"\301\352\254\244\014\102\135\126\270\070\172\340\245\350\364\211" +
"\071\021\204\254\074\307\264\267\265\265\142\200\262\242\164\037" +
"\072\220\125\151\240\354\172\300\261\006\365\001\304\243\060\055" +
"\365\176\030\265\053\275\005\001\264\152\014\120\153\251\037\341" +
"\173\343\131\067\242\150\061\335\163\024\200\332\156\124\311\213" +
"\123\127\243\370\326\022\017\275\005\016\034\213\214\303\202\230" +
"\243\000\150\305\022\171\204\157\060\160\070\076\016\351\343\153" +
"\000\061\360\106\141\331\121\341\376\131\000\320\002\000\307\375" +
"\120\006\307\165\326\003\056\131\322\022\134\100\054\361\010\273" +
"\134\175\003\106\074\056\333\346\013\000\000"
});

public static final byte[] layoutSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\131\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\012\227\230\300\062\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\061\052\061\052\101\251\004\000\321" +
"\377\305\313\346\013\000\000"
});

public static final byte[] prefixSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\131\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\144\200\202\212\342\102\206\072\006\246\122\020" +
"\311\072\052\061\052\061\052\061\052\061\052\061\052\061\052\061" +
"\052\061\052\061\052\061\052\101\105\011\000\231\351\240\311\346" +
"\013\000\000"
});

public static final byte[] prefixMapsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\044\072\332\047\053\261\054\121\257\264\044\063\107\317" +
"\051\263\044\070\265\304\132\362\322\273\215\346\317\356\030\061" +
"\061\060\124\024\060\060\060\224\001\025\012\143\121\227\053\251" +
"\301\162\276\317\245\000\246\116\241\200\000\050\055\144\250\143" +
"\140\032\125\071\252\162\124\345\250\312\121\225\243\052\107\125" +
"\216\252\034\125\071\252\162\124\345\250\312\121\225\243\052\107" +
"\125\216\252\034\125\071\252\162\124\045\004\000\000\302\127\077" +
"\301\247\023\000\000"
});

public static final byte[] terminalUsesHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\201\051\332\323\167\127\202\132\331\253\115\113\231\030\030" +
"\052\012\030\030\030\024\030\006\030\000\000\162\271\041\343\233" +
"\000\000\000"
});

public static final byte[] shiftableUnionHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\270" +
"\210\101\040\053\261\054\121\257\264\044\063\107\317\051\263\044" +
"\070\265\044\357\157\107\235\245\311\152\105\146\006\306\150\006" +
"\226\244\314\222\342\022\006\246\150\257\212\202\322\042\060\255" +
"\300\262\125\150\143\351\144\046\006\206\212\002\006\006\006\106" +
"\040\146\170\371\177\373\337\012\000\215\054\362\034\121\000\000" +
"\000"
});

public static final byte[] acceptSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\150\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\330" +
"\004\030\101\202\014\034\044\113\010\060\310\221\246\203\021\227" +
"\345\114\244\273\212\011\227\004\203\003\251\106\001\075\102\252" +
"\016\106\354\022\002\304\007\067\026\113\161\251\154\300\045\301" +
"\201\113\202\211\150\263\031\032\260\113\070\320\041\231\220\234" +
"\176\360\104\010\203\002\016\011\026\212\323\077\316\344\241\102" +
"\104\144\023\151\073\013\131\132\211\067\213\021\352\103\162\315" +
"\044\043\166\031\024\260\353\160\300\145\107\003\356\124\112\104" +
"\044\122\323\147\324\214\243\301\256\025\022\010\214\024\207\216" +
"\302\340\115\374\070\313\014\007\354\022\300\204\130\001\000\221" +
"\362\050\055\245\007\000\000"
});

public static final byte[] rejectSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\150\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\243" +
"\002\243\002\324\027\140\004\011\062\160\014\032\367\014\260\257" +
"\111\227\030\025\030\115\245\170\035\010\000\164\320\262\176\065" +
"\006\000\000"
});

public static final byte[] possibleSetsHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\133\363\226\201\265\264" +
"\210\101\070\332\047\053\261\054\121\257\264\044\063\107\317\051" +
"\263\044\070\265\304\072\127\122\203\345\174\237\113\001\023\003" +
"\103\105\001\003\003\103\150\161\021\203\000\272\252\274\277\035" +
"\165\226\046\253\025\231\031\030\243\031\130\222\062\113\212\113" +
"\030\230\242\275\052\012\200\206\202\150\005\226\255\102\033\113" +
"\047\303\314\140\250\050\056\144\250\143\140\052\005\221\254\100" +
"\076\043\110\360\377\377\377\377\260\112\060\160\340\320\001\224" +
"\160\300\056\041\300\040\207\103\207\002\166\243\030\161\271\212" +
"\011\227\304\001\234\256\142\142\120\300\056\321\340\202\135\207" +
"\002\116\243\200\036\301\056\241\203\123\007\043\166\011\001\134" +
"\376\140\301\045\201\323\162\074\072\032\260\113\160\000\041\016" +
"\035\114\070\044\030\161\332\301\320\200\135\302\001\167\052\241" +
"\132\362\041\071\135\341\116\045\014\270\122\011\060\174\161\044" +
"\037\034\106\121\061\371\340\214\332\041\046\301\201\063\211\342" +
"\114\127\070\045\250\127\310\070\340\212\250\006\334\061\110\152" +
"\142\240\242\004\316\164\065\070\343\174\160\112\220\221\256\106" +
"\154\174\340\017\022\000\123\227\210\036\245\010\000\000"
});

public static final byte[] cMapHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\321\113\112\003\101" +
"\020\006\340\166\064\032\337\232\030\337\032\237\061\127\161\341" +
"\011\334\170\204\040\050\136\310\235\053\227\036\111\360\016\326" +
"\100\011\263\120\031\305\225\174\015\037\377\124\367\164\165\103" +
"\077\275\225\316\335\155\251\256\057\257\136\156\106\367\257\317" +
"\217\125\051\017\223\062\125\352\161\326\302\111\230\151\324\027" +
"\055\367\265\355\135\147\377\223\265\365\060\027\016\303\161\350" +
"\206\275\260\035\066\302\142\350\205\335\157\054\247\325\106\337" +
"\205\160\032\106\131\357\377\322\326\017\377\257\317\072\157\334" +
"\343\143\256\223\337\323\141\066\154\206\225\057\172\124\231\303" +
"\314\101\346\116\250\137\164\051\353\371\306\236\243\314\203\074" +
"\157\034\326\376\360\015\001\000\000\000\000\000\000\000\000\000" +
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
"\000\000\000\370\147\336\001\272\106\173\122\033\000\004\000"
});

public static final byte[] deltaHash = edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder.literalToByteArray
(new String[]{ "\037\213\010\000\000\000\000\000\000\000\355\231\311\156\023\101" +
"\020\206\133\066\016\011\020\310\006\306\204\075\311\044\036\366" +
"\055\011\140\366\000\161\066\023\047\071\371\302\043\000\222\021" +
"\047\356\234\170\012\156\234\070\362\074\234\220\070\163\245\046" +
"\032\107\245\236\236\245\147\172\074\375\013\227\364\051\135\335" +
"\323\245\137\345\216\247\252\375\375\217\250\164\077\210\162\247" +
"\323\254\376\375\325\252\175\375\322\050\011\361\351\235\020\142" +
"\217\346\113\235\346\346\317\267\316\307\337\077\276\365\246\353" +
"\002\300\272\357\305\147\101\212\017\324\226\174\312\304\041\177" +
"\134\041\206\210\303\304\060\061\102\034\041\216\022\307\210\121" +
"\342\070\161\202\030\043\306\211\011\142\222\305\363\230\362\377" +
"\172\173\116\372\343\123\104\225\075\163\132\113\155\215\041\244" +
"\261\120\214\345\365\270\147\344\261\374\134\244\051\325\236\201" +
"\121\313\155\072\156\167\116\166\066\154\241\057\047\341\134\110" +
"\074\263\271\265\316\300\325\312\237\356\171\221\375\044\364\347" +
"\277\254\110\273\240\232\114\255\366\142\146\075\152\273\024\265" +
"\150\325\333\341\162\224\122\245\132\135\233\111\070\247\263\036" +
"\152\326\344\166\126\021\047\136\255\325\006\256\166\216\341\110" +
"\176\022\026\122\354\211\142\236\373\340\271\255\063\134\311\327" +
"\341\152\206\275\234\053\334\007\317\255\055\166\115\065\151\255" +
"\132\245\005\324\136\147\310\176\121\334\350\215\301\163\153\265" +
"\131\123\047\244\357\320\371\316\233\126\253\345\126\124\207\176" +
"\053\154\301\112\265\371\336\047\334\226\202\366\367\334\336\321" +
"\124\253\163\156\357\032\125\153\265\005\324\336\143\310\176\221" +
"\354\153\011\250\135\144\054\111\176\121\054\023\367\275\161\356" +
"\157\207\007\021\361\372\177\156\377\317\016\075\135\156\261\272" +
"\110\047\043\015\003\061\070\017\271\037\120\373\210\341\110\176" +
"\022\236\204\314\077\115\021\313\343\061\367\301\337\016\130\035" +
"\272\153\210\347\206\342\074\343\176\100\355\013\206\053\371\072" +
"\274\224\374\127\051\343\254\160\037\374\334\132\155\340\367\011" +
"\066\253\175\035\120\273\052\220\352\004\156\366\367\274\130\065" +
"\330\100\155\176\152\007\135\144\176\135\344\100\255\016\115\050" +
"\265\172\271\315\343\073\141\055\044\136\366\357\204\165\206\043" +
"\371\111\330\114\261\047\212\015\356\203\127\214\130\367\011\130" +
"\035\072\126\156\115\250\335\212\171\046\156\075\271\332\026\303" +
"\225\174\035\266\063\354\345\274\341\076\370\167\002\326\135\015" +
"\326\355\007\126\156\115\250\155\307\250\155\033\123\153\363\175" +
"\102\360\366\143\107\040\365\145\111\353\333\335\210\065\354\372" +
"\066\277\334\142\375\112\202\125\215\143\345\026\253\142\304\372" +
"\225\004\253\142\304\312\055\126\125\203\365\166\260\372\146\351" +
"\037\175\220\173\020\357\071\000\000"
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
        TERMINAL_COUNT = 32;
        GRAMMAR_SYMBOL_COUNT = 63;
        SYMBOL_COUNT = 124;
        PARSER_STATE_COUNT = 118;
        SCANNER_STATE_COUNT = 85;
        DISAMBIG_GROUP_COUNT = 7;
        SCANNER_START_STATENUM = 1;
        PARSER_START_STATENUM = 1;
        EOF_SYMNUM = 0;
        EPS_SYMNUM = -1;
        try { initArrays(); }
        catch(java.io.IOException ex) { ex.printStackTrace(); System.exit(1); }
        catch(java.lang.ClassNotFoundException ex) { ex.printStackTrace(); System.exit(1); }
        disambiguationGroups = new java.util.BitSet[7];
        disambiguationGroups[0] = newBitVec(32,4,20);
        disambiguationGroups[1] = newBitVec(32,2,4,20);
        disambiguationGroups[2] = newBitVec(32,2,20);
        disambiguationGroups[3] = newBitVec(32,3,20);
        disambiguationGroups[4] = newBitVec(32,1,4,20);
        disambiguationGroups[5] = newBitVec(32,3,4,20);
        disambiguationGroups[6] = newBitVec(32,2,3,4,20);
    }

}
