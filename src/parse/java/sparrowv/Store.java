package sparrowv;

import IR.token.Register;
import sparrowv.visitor.ArgRetVisitor;
import sparrowv.visitor.ArgVisitor;
import sparrowv.visitor.RetVisitor;
import sparrowv.visitor.Visitor;

public class Store extends Instruction {
  public Register base;
  public int offset;
  public Register rhs;

  public Store(Register base, int offset, Register rhs) {
    this.base = base;
    this.offset = offset;
    this.rhs = rhs;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public <A,R> R accept(ArgRetVisitor<A,R> v, A arg) {
    return v.visit(this, arg);
  }

  public <R> R accept(RetVisitor<R> v){
    return v.visit(this);
  }

  public <A> void accept(ArgVisitor<A> v, A arg) {
    v.visit(this, arg);
  }

  public String toString() {
    return "[" + base + " + " + offset + "] = " + rhs;
  }
}
