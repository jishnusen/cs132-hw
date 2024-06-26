package sparrowv;

import IR.token.Identifier;
import IR.token.Register;
import sparrowv.visitor.ArgRetVisitor;
import sparrowv.visitor.ArgVisitor;
import sparrowv.visitor.RetVisitor;
import sparrowv.visitor.Visitor;

import java.util.List;

public class Call extends Instruction {
  public Register lhs;
  public Register callee;
  public List<Identifier> args;

  public Call(Register lhs, Register callee, List<Identifier> args) {
    this.lhs = lhs ;
    this.callee = callee;
    this.args = args;
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
    String res = lhs + " = call " + callee + "(";
    String s = "";
    for (Identifier param: args) {
      s = s + param + " ";
    }
    if (!(s.equals(""))) {
       res = res + s.subSequence(0,s.length()-1);
    }
    res = res + ")";
    return res;
  }
}
