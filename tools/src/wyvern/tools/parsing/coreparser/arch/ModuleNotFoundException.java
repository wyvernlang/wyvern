package wyvern.tools.parsing.coreparser.arch;

import wyvern.tools.parsing.coreparser.Token;

/**
 * This exception is thrown when component or connector types defined in the
 * architecture don't have a corresponding module or module def in the
 * programmer provided code.
 */

public class ModuleNotFoundException extends Exception {
  /**
   * This is the last token that has been consumed successfully. If this object
   * has been created due to a parse error, the token followng this token will
   * (therefore) be the first error token.
   */
  public Token currentToken;

  /**
   * Each entry in this array is an array of integers. Each array of integers
   * represents a sequence of tokens (by their ordinal values) that is expected
   * at this point of the parse.
   */
  public int[][] expectedTokenSequences;

  /**
   * This is a reference to the "tokenImage" array of the generated parser
   * within which the parse error occurred. This array is defined in the
   * generated ...Constants interface.
   */
  public String[] tokenImage;

  public ModuleNotFoundException(Token currentTokenVal,
      int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
    super(
        initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
    currentToken = currentTokenVal;
    expectedTokenSequences = expectedTokenSequencesVal;
    tokenImage = tokenImageVal;
  }

  public ModuleNotFoundException() {
    super();
  }

  /** Constructor with message. */
  public ModuleNotFoundException(String message) {
    super(message);
  }

  /**
   * It uses "currentToken" and "expectedTokenSequences" to generate a parse
   * error message and returns it. If this object has been created due to a
   * parse error, and you do not catch it (it gets thrown from the parser) the
   * correct error message gets displayed.
   */
  private static String initialise(Token currentToken,
      int[][] expectedTokenSequences, String[] tokenImage) {
    String eol = System.getProperty("line.separator", "\n");
    StringBuffer expected = new StringBuffer();
    int maxSize = 0;
    for (int i = 0; i < expectedTokenSequences.length; i++) {
      if (maxSize < expectedTokenSequences[i].length) {
        maxSize = expectedTokenSequences[i].length;
      }
      for (int j = 0; j < expectedTokenSequences[i].length; j++) {
        expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
      }
      if (expectedTokenSequences[i][expectedTokenSequences[i].length
          - 1] != 0) {
        expected.append("...");
      }
      expected.append(eol).append("    ");
    }
    String retval = "Encountered \"";
    Token tok = currentToken.next;
    for (int i = 0; i < maxSize; i++) {
      if (i != 0)
        retval += " ";
      if (tok.kind == 0) {
        retval += tokenImage[0];
        break;
      }
      retval += " " + tokenImage[tok.kind];
      retval += " \"";
      retval += add_escapes(tok.image);
      retval += " \"";
      tok = tok.next;
    }
    retval += "\" at line " + currentToken.next.beginLine + ", column "
        + currentToken.next.beginColumn;
    retval += "." + eol;
    if (expectedTokenSequences.length == 1) {
      retval += "Was expecting:" + eol + "    ";
    } else {
      retval += "Was expecting one of:" + eol + "    ";
    }
    retval += expected.toString();
    return retval;
  }

  /**
   * The end of line string for this machine.
   */
  protected String eol = System.getProperty("line.separator", "\n");

  /**
   * Used to convert raw characters to their escaped version when these raw
   * version cannot be used as part of an ASCII string literal.
   */
  static String add_escapes(String str) {
    StringBuffer retval = new StringBuffer();
    char ch;
    for (int i = 0; i < str.length(); i++) {
      switch (str.charAt(i)) {
      case 0:
        continue;
      case '\b':
        retval.append("\\b");
        continue;
      case '\t':
        retval.append("\\t");
        continue;
      case '\n':
        retval.append("\\n");
        continue;
      case '\f':
        retval.append("\\f");
        continue;
      case '\r':
        retval.append("\\r");
        continue;
      case '\"':
        retval.append("\\\"");
        continue;
      case '\'':
        retval.append("\\\'");
        continue;
      case '\\':
        retval.append("\\\\");
        continue;
      default:
        if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
          String s = "0000" + Integer.toString(ch, 16);
          retval.append("\\u" + s.substring(s.length() - 4, s.length()));
        } else {
          retval.append(ch);
        }
        continue;
      }
    }
    return retval.toString();
  }

}
