import java.util.*;

import IR.visitor.GJNoArguVisitor;
import IR.syntaxtree.*;

public class IdsSet implements GJNoArguVisitor<String> {

  //
  // GJ Auto class visitors with no argument
  //

  public String visit(NodeList n) {
    throw new UnsupportedOperationException();
  }
  public String visit(NodeListOptional n) {
    throw new UnsupportedOperationException();
  }
  public String visit(NodeOptional n) {
    throw new UnsupportedOperationException();
  }
  public String visit(NodeSequence n) {
    throw new UnsupportedOperationException();
  }
  public String visit(NodeToken n) {
    throw new UnsupportedOperationException();
  }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> ( FunctionDeclaration() )*
   * f1 -> <EOF>
   */
  public String visit(Program n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> "func"
   * f1 -> FunctionName()
   * f2 -> "("
   * f3 -> ( Identifier() )*
   * f4 -> ")"
   * f5 -> Block()
   */
  public String visit(FunctionDeclaration n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> ( Instruction() )*
   * f1 -> "return"
   * f2 -> Identifier()
   */
  public String visit(Block n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> LabelWithColon()
   *       | SetInteger()
   *       | SetFuncName()
   *       | Add()
   *       | Subtract()
   *       | Multiply()
   *       | LessThan()
   *       | Load()
   *       | Store()
   *       | Move()
   *       | Alloc()
   *       | Print()
   *       | ErrorMessage()
   *       | Goto()
   *       | IfGoto()
   *       | Call()
   */
  public String visit(Instruction n) {
    return n.f0.choice.accept(this);
  }

  /**
   * f0 -> Label()
   * f1 -> ":"
   */
  public String visit(LabelWithColon n) {
    return null;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> IntegerLiteral()
   */
  public String visit(SetInteger n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "@"
   * f3 -> FunctionName()
   */
  public String visit(SetFuncName n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "+"
   * f4 -> Identifier()
   */
  public String visit(Add n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "-"
   * f4 -> Identifier()
   */
  public String visit(Subtract n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "*"
   * f4 -> Identifier()
   */
  public String visit(Multiply n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "<"
   * f4 -> Identifier()
   */
  public String visit(LessThan n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "["
   * f3 -> Identifier()
   * f4 -> "+"
   * f5 -> IntegerLiteral()
   * f6 -> "]"
   */
  public String visit(Load n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> "["
   * f1 -> Identifier()
   * f2 -> "+"
   * f3 -> IntegerLiteral()
   * f4 -> "]"
   * f5 -> "="
   * f6 -> Identifier()
   */
  public String visit(Store n) {
    return null;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   */
  public String visit(Move n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "alloc"
   * f3 -> "("
   * f4 -> Identifier()
   * f5 -> ")"
   */
  public String visit(Alloc n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> "print"
   * f1 -> "("
   * f2 -> Identifier()
   * f3 -> ")"
   */
  public String visit(Print n) {
    return null;
  }

  /**
   * f0 -> "error"
   * f1 -> "("
   * f2 -> StringLiteral()
   * f3 -> ")"
   */
  public String visit(ErrorMessage n) {
    return null;
  }

  /**
   * f0 -> "goto"
   * f1 -> Label()
   */
  public String visit(Goto n) {
    return null;
  }

  /**
   * f0 -> "if0"
   * f1 -> Identifier()
   * f2 -> "goto"
   * f3 -> Label()
   */
  public String visit(IfGoto n) {
    return null;
  }
  public String visit(If n) {
    return null;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "call"
   * f3 -> Identifier()
   * f4 -> "("
   * f5 -> ( Identifier() )*
   * f6 -> ")"
   */
  public String visit(Call n) {
    return n.f0.f0.toString();
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public String visit(FunctionName n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public String visit(Label n) {
    return null;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public String visit(Identifier n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public String visit(IntegerLiteral n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> <STRINGCONSTANT>
   */
  public String visit(StringLiteral n) {
    throw new UnsupportedOperationException();
  }

  public String visit(LabeledInstruction n) {
    return n.f1.accept(this);
  }
}

