/* Generated By:JavaCC: Do not edit this line. SparrowParserConstants.java */
package IR;

/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface SparrowParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 6;
  /** RegularExpression Id. */
  int FORMAL_COMMENT = 7;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 8;
  /** RegularExpression Id. */
  int LPAREN = 9;
  /** RegularExpression Id. */
  int RPAREN = 10;
  /** RegularExpression Id. */
  int LSQPAREN = 11;
  /** RegularExpression Id. */
  int RSQPAREN = 12;
  /** RegularExpression Id. */
  int LBRACE = 13;
  /** RegularExpression Id. */
  int RBRACE = 14;
  /** RegularExpression Id. */
  int SEMICOLON = 15;
  /** RegularExpression Id. */
  int DOT = 16;
  /** RegularExpression Id. */
  int ASSIGN = 17;
  /** RegularExpression Id. */
  int LT = 18;
  /** RegularExpression Id. */
  int PLUS = 19;
  /** RegularExpression Id. */
  int MINUS = 20;
  /** RegularExpression Id. */
  int ADDRESS = 21;
  /** RegularExpression Id. */
  int FUNC = 22;
  /** RegularExpression Id. */
  int IFZERO = 23;
  /** RegularExpression Id. */
  int GOTO = 24;
  /** RegularExpression Id. */
  int CALL = 25;
  /** RegularExpression Id. */
  int ALLOC = 26;
  /** RegularExpression Id. */
  int PRINT = 27;
  /** RegularExpression Id. */
  int ERROR = 28;
  /** RegularExpression Id. */
  int RETURN = 29;
  /** RegularExpression Id. */
  int INTEGER_LITERAL = 30;
  /** RegularExpression Id. */
  int IDENTIFIER = 31;
  /** RegularExpression Id. */
  int LETTER = 32;
  /** RegularExpression Id. */
  int DIGIT = 33;
  /** RegularExpression Id. */
  int STRINGCONSTANT = 34;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<SINGLE_LINE_COMMENT>",
    "<FORMAL_COMMENT>",
    "<MULTI_LINE_COMMENT>",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "\";\"",
    "\".\"",
    "\"=\"",
    "\"<\"",
    "\"+\"",
    "\"-\"",
    "\"@\"",
    "\"func\"",
    "\"if0\"",
    "\"goto\"",
    "\"call\"",
    "\"alloc\"",
    "\"print\"",
    "\"error\"",
    "\"return\"",
    "<INTEGER_LITERAL>",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "<STRINGCONSTANT>",
    "\":\"",
    "\"*\"",
  };

}
