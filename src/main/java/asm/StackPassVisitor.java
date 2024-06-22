package asm;

import java.util.*;

import IR.token.Identifier;
import sparrowv.*;

public class StackPassVisitor extends sparrowv.visitor.DepthFirst {
  public Map<String, FunctionInfo> functions = new HashMap<>();
  FunctionInfo cur_fn = null;

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public void visit(FunctionDecl n) {
    this.cur_fn = new FunctionInfo(n);

    for (Identifier param: n.formalParameters) {
      this.cur_fn.addId(param);
    }
    n.block.accept(this);
    this.functions.put(n.functionName.toString(), this.cur_fn);
  }

  /*   Identifier lhs;
   *   Register rhs; */
  public void visit(Move_Id_Reg n) {
    this.cur_fn.addId(n.lhs);
  }

  /*   Register lhs;
   *   Identifier rhs; */
  public void visit(Move_Reg_Id n) {
    this.cur_fn.addId(n.rhs);
  }
}
