import java.util.*;

import IR.visitor.GJNoArguVisitor;
import IR.syntaxtree.*;

public class IdsUsed implements GJNoArguVisitor<List<String>> {

  //
  // GJ Auto class visitors with no argument
  //

  public List<String> visit(NodeList n) {
    throw new UnsupportedOperationException();
  }
  public List<String> visit(NodeListOptional n) {
    throw new UnsupportedOperationException();
  }
  public List<String> visit(NodeOptional n) {
    throw new UnsupportedOperationException();
  }
  public List<String> visit(NodeSequence n) {
    throw new UnsupportedOperationException();
  }
  public List<String> visit(NodeToken n) {
    throw new UnsupportedOperationException();
  }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> ( FunctionDeclaration() )*
   * f1 -> <EOF>
   */
  public List<String> visit(Program n) {
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
  public List<String> visit(FunctionDeclaration n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> ( Instruction() )*
   * f1 -> "return"
   * f2 -> Identifier()
   */
  public List<String> visit(Block n) {
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
  public List<String> visit(Instruction n) {
    return n.f0.choice.accept(this);
  }

  /**
   * f0 -> Label()
   * f1 -> ":"
   */
  public List<String> visit(LabelWithColon n) {
    return new ArrayList<String>();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> IntegerLiteral()
   */
  public List<String> visit(SetInteger n) {
    return new ArrayList<>(Arrays.asList(n.f0.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "@"
   * f3 -> FunctionName()
   */
  public List<String> visit(SetFuncName n) {
    return new ArrayList<>(Arrays.asList(n.f0.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "+"
   * f4 -> Identifier()
   */
  public List<String> visit(Add n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f2.f0.toString(),
          n.f4.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "-"
   * f4 -> Identifier()
   */
  public List<String> visit(Subtract n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f2.f0.toString(),
          n.f4.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "*"
   * f4 -> Identifier()
   */
  public List<String> visit(Multiply n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f2.f0.toString(),
          n.f4.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "<"
   * f4 -> Identifier()
   */
  public List<String> visit(LessThan n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f2.f0.toString(),
          n.f4.f0.toString()));
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
  public List<String> visit(Load n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f3.f0.toString()));
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
  public List<String> visit(Store n) {
    return new ArrayList<>(Arrays.asList(n.f1.f0.toString(), n.f6.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   */
  public List<String> visit(Move n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f2.f0.toString()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "alloc"
   * f3 -> "("
   * f4 -> Identifier()
   * f5 -> ")"
   */
  public List<String> visit(Alloc n) {
    return new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f4.f0.toString()));
  }

  /**
   * f0 -> "print"
   * f1 -> "("
   * f2 -> Identifier()
   * f3 -> ")"
   */
  public List<String> visit(Print n) {
    return new ArrayList<>(Arrays.asList(n.f2.f0.toString()));
  }

  /**
   * f0 -> "error"
   * f1 -> "("
   * f2 -> StringLiteral()
   * f3 -> ")"
   */
  public List<String> visit(ErrorMessage n) {
    return new ArrayList<>();
  }

  /**
   * f0 -> "goto"
   * f1 -> Label()
   */
  public List<String> visit(Goto n) {
    return new ArrayList<>();
  }

  /**
   * f0 -> "if0"
   * f1 -> Identifier()
   * f2 -> "goto"
   * f3 -> Label()
   */
  public List<String> visit(IfGoto n) {
    return new ArrayList<>(Arrays.asList(n.f1.f0.toString()));
  }

  public List<String> visit(If n) {
    return new ArrayList<>(Arrays.asList(n.f1.f0.toString()));
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
  public List<String> visit(Call n) {
    List<String> res = new ArrayList<>(Arrays.asList(
          n.f0.f0.toString(),
          n.f3.f0.toString()));
    for (Node arg : n.f5.nodes) {
      Identifier id = (Identifier)arg;
      res.add(id.f0.toString());
    }
    return res;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public List<String> visit(FunctionName n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public List<String> visit(Label n) {
    return new ArrayList<>();
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public List<String> visit(Identifier n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public List<String> visit(IntegerLiteral n) {
    throw new UnsupportedOperationException();
  }

  /**
   * f0 -> <STRINGCONSTANT>
   */
  public List<String> visit(StringLiteral n) {
    throw new UnsupportedOperationException();
  }

  public List<String> visit(LabeledInstruction n) {
    return n.f1.accept(this);
  }
}

