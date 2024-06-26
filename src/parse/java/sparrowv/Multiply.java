package sparrowv;

import IR.token.Register;
import sparrowv.visitor.ArgRetVisitor;
import sparrowv.visitor.ArgVisitor;
import sparrowv.visitor.RetVisitor;
import sparrowv.visitor.Visitor;

public class Multiply extends Instruction {
  public Register lhs;
  public Register arg1;
  public Register arg2;

  public Multiply(Register lhs, Register arg1, Register arg2) {
    this.lhs = lhs;
    this.arg1 = arg1;
    this.arg2 = arg2;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public <A> void accept(ArgVisitor<A> v, A arg) {
    v.visit(this, arg);
  }

  public <A,R> R accept(ArgRetVisitor<A,R> v, A arg) {
    return v.visit(this, arg);
  }

  public <R> R accept(RetVisitor<R> v){
    return v.visit(this);
  }

  public String toString() {
    return lhs + " = " + arg1 + " * " + arg2;
  }
}
