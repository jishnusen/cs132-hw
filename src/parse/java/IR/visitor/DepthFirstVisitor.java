//
// Generated by JTB 1.3.2
//

package IR.visitor;

import IR.syntaxtree.*;

import java.util.Enumeration;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class DepthFirstVisitor implements Visitor {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public void visit(NodeList n) {
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
   }

   public void visit(NodeListOptional n) {
      if ( n.present() )
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
            e.nextElement().accept(this);
   }

   public void visit(NodeOptional n) {
      if ( n.present() )
         n.node.accept(this);
   }

   public void visit(NodeSequence n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
   }

   public void visit(NodeToken n) { }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( FunctionDeclaration() )*
    * f1 -> <EOF>
    */
   public void visit(Program n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> "func"
    * f1 -> FunctionName()
    * f2 -> "("
    * f3 -> ( Identifier() )*
    * f4 -> ")"
    * f5 -> Block()
    */
   public void visit(FunctionDeclaration n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
   }

   /**
    * f0 -> ( Instruction() )*
    * f1 -> "return"
    * f2 -> Identifier()
    */
   public void visit(Block n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
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
   public void visit(Instruction n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> Label()
    * f1 -> ":"
    */
   public void visit(LabelWithColon n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> IntegerLiteral()
    */
   public void visit(SetInteger n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "@"
    * f3 -> FunctionName()
    */
   public void visit(SetFuncName n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "+"
    * f4 -> Identifier()
    */
   public void visit(Add n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "-"
    * f4 -> Identifier()
    */
   public void visit(Subtract n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "*"
    * f4 -> Identifier()
    */
   public void visit(Multiply n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "<"
    * f4 -> Identifier()
    */
   public void visit(LessThan n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
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
   public void visit(Load n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
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
   public void visit(Store n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    */
   public void visit(Move n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "alloc"
    * f3 -> "("
    * f4 -> Identifier()
    * f5 -> ")"
    */
   public void visit(Alloc n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
   }

   /**
    * f0 -> "print"
    * f1 -> "("
    * f2 -> Identifier()
    * f3 -> ")"
    */
   public void visit(Print n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> "error"
    * f1 -> "("
    * f2 -> StringLiteral()
    * f3 -> ")"
    */
   public void visit(ErrorMessage n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> "goto"
    * f1 -> Label()
    */
   public void visit(Goto n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> "if0"
    * f1 -> Identifier()
    * f2 -> "goto"
    * f3 -> Label()
    */
   public void visit(IfGoto n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
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
   public void visit(Call n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(FunctionName n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Label n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Identifier n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public void visit(IntegerLiteral n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <STRINGCONSTANT>
    */
   public void visit(StringLiteral n) {
      n.f0.accept(this);
   }

   @Override
   public void visit(If n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   @Override
   public void visit(LabeledInstruction n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

}
