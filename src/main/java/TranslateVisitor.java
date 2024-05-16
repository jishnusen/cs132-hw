import java.util.*;

import IR.visitor.DepthFirstVisitor;
import IR.syntaxtree.*;
import IR.token.Register;

public class TranslateVisitor extends DepthFirstVisitor {
  Map<String, LivenessTable> method_liveness;

  // temp state
  LivenessTable lv = null;
  int idx = 0;
  List<sparrowv.Instruction> ins = null;
  Map<String, Interval> parameter_liveness;

  sparrowv.Program p = new sparrowv.Program();

  TranslateVisitor(Map<String, LivenessTable> method_liveness) {
    super();
    this.p.funDecls = new ArrayList<>();
    this.method_liveness = method_liveness;
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
    String method = n.f1.f0.toString();

    // reset temp state
    lv = method_liveness.get(method);
    idx = 0;
    ins = new ArrayList<>();
    parameter_liveness = new HashMap<>();

    List<IR.token.Identifier> params = new ArrayList<>();
    for (Node p : n.f3.nodes) {
      String param = ((Identifier)p).f0.toString();
      params.add(new IR.token.Identifier(param));
      if (lv.liveness.containsKey(param)) {
        parameter_liveness.put(param, lv.liveness.get(param).get(0));
      }
    }

    n.f5.accept(this);

    String ret = lv.liveness.get(n.f5.f2.f0.toString()).get(0).id;
    IR.token.Identifier ret_id;
    if (lv.all_registers.contains(ret)) {
      ret_id = new IR.token.Identifier("ret_save_" + ret);
      ins.add(new sparrowv.Move_Id_Reg(ret_id, new Register(ret)));
    } else {
      ret_id = new IR.token.Identifier(ret);
    }

    p.funDecls.add(
      new sparrowv.FunctionDecl(
        new IR.token.FunctionName(method),
        params,
        new sparrowv.Block(
          ins,
          ret_id
        )
      )
    );
  }

   /**
    * f0 -> ( Instruction() )*
    * f1 -> "return"
    * f2 -> Identifier()
    */
  public void visit(Block n) {
    List<Node> instructions = n.f0.nodes;
    for (idx = 0; idx < instructions.size(); idx++)  {
      for (String p : parameter_liveness.keySet()) {
        Interval iv = parameter_liveness.get(p);
        if (iv.start == idx) {
          if (lv.all_registers.contains(iv.id)) {
            ins.add(new sparrowv.Move_Reg_Id(new Register(iv.id), new IR.token.Identifier(p)));
          } else {
            iv.id = p;
          }
        }
      }
      instructions.get(idx).accept(this);
    }
  }

  /**
   * f0 -> Label()
   * f1 -> ":"
   */
  public void visit(LabelWithColon n) {
     ins.add(new sparrowv.LabelInstr(new IR.token.Label(n.f0.f0.toString())));
  }

  class Registers {
    Register[] reg = null;
    List<sparrowv.Instruction> head = new ArrayList<>();
    List<sparrowv.Instruction> tail = new ArrayList<>();
  }

  Registers get_registers(Identifier[] ids) {
    return get_registers(ids, false);
  }

  Registers get_registers(Identifier[] ids, boolean load_first) {
    if (ids.length > 3) {
      throw new RuntimeException("got too many ids");
    }

    Registers res = new Registers();
    res.reg = new Register[ids.length];
    for (int i = 0; i < ids.length; i++) {
      String id = lv.lookup(ids[i].f0.toString(), idx);
      if (lv.all_registers.contains(id)) {
        res.reg[i] = new Register(id);
      } else {
        res.reg[i] = new Register("t" + Integer.toString(i));
        IR.token.Identifier stack = new IR.token.Identifier(id);
        if (i == 0) {
          // may need to write back result
          if (load_first) res.head.add(new sparrowv.Move_Reg_Id(res.reg[i], stack));
          res.tail.add(new sparrowv.Move_Id_Reg(stack, res.reg[i]));
        } else {
          res.head.add(new sparrowv.Move_Reg_Id(res.reg[i], stack));
        }
      }
    }
    return res;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> IntegerLiteral()
   */
  public void visit(SetInteger n) {
    Identifier[] ids = {n.f0};
    Registers mapping = get_registers(ids);
    int value = Integer.parseInt(n.f2.f0.toString());
    ins.addAll(mapping.head);
    ins.add(new sparrowv.Move_Reg_Integer(mapping.reg[0], value));
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "@"
   * f3 -> FunctionName()
   */
  public void visit(SetFuncName n) {
    Identifier[] ids = {n.f0};
    Registers mapping = get_registers(ids);
    IR.token.FunctionName func_name = new IR.token.FunctionName(n.f3.f0.toString());
    ins.addAll(mapping.head);
    ins.add(new sparrowv.Move_Reg_FuncName(mapping.reg[0], func_name));
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "+"
   * f4 -> Identifier()
   */
  public void visit(Add n) {
    Identifier[] ids = {n.f0, n.f2, n.f4};
    Registers mapping = get_registers(ids);
    ins.addAll(mapping.head);
    ins.add(new sparrowv.Add(mapping.reg[0], mapping.reg[1], mapping.reg[2]));
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "-"
   * f4 -> Identifier()
   */
  public void visit(Subtract n) {
    Identifier[] ids = {n.f0, n.f2, n.f4};
    Registers mapping = get_registers(ids);
    ins.addAll(mapping.head);
    ins.add(new sparrowv.Subtract(mapping.reg[0], mapping.reg[1], mapping.reg[2]));
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "*"
   * f4 -> Identifier()
   */
  public void visit(Multiply n) {
    Identifier[] ids = {n.f0, n.f2, n.f4};
    Registers mapping = get_registers(ids);
    ins.addAll(mapping.head);
    ins.add(new sparrowv.Multiply(mapping.reg[0], mapping.reg[1], mapping.reg[2]));
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "<"
   * f4 -> Identifier()
   */
  public void visit(LessThan n) {
    Identifier[] ids = {n.f0, n.f2, n.f4};
    Registers mapping = get_registers(ids);
    ins.addAll(mapping.head);
    ins.add(new sparrowv.LessThan(mapping.reg[0], mapping.reg[1], mapping.reg[2]));
    ins.addAll(mapping.tail);
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
    Identifier[] ids = {n.f0, n.f3};
    Registers mapping = get_registers(ids, true);
    int offset = Integer.parseInt(n.f5.f0.toString());

    ins.addAll(mapping.head);
    ins.add(new sparrowv.Load(mapping.reg[0], mapping.reg[1], offset));
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
    Identifier[] ids = {n.f1, n.f6};
    Registers mapping = get_registers(ids, true);
    int offset = Integer.parseInt(n.f3.f0.toString());

    ins.addAll(mapping.head);
    ins.add(new sparrowv.Store(mapping.reg[0], offset, mapping.reg[1]));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   */
  public void visit(Move n) {
    String lhs = lv.lookup(n.f0.f0.toString(), idx);
    String rhs = lv.lookup(n.f2.f0.toString(), idx);
    if (!lv.all_registers.contains(lhs) && !lv.all_registers.contains(rhs)) {
      ins.add(new sparrowv.Move_Reg_Id(new Register("t0"), new IR.token.Identifier(rhs)));
      rhs = "t0";
    }
    ins.add(new sparrowv.Move_Reg_Reg(new Register(lhs), new Register(rhs)));
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
    Identifier[] ids = {n.f0, n.f4};
    Registers mapping = get_registers(ids);

    ins.addAll(mapping.head);
    ins.add(new sparrowv.Alloc(mapping.reg[0], mapping.reg[1]));
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> "print"
   * f1 -> "("
   * f2 -> Identifier()
   * f3 -> ")"
   */
  public void visit(Print n) {
    Identifier[] ids = {n.f2};
    Registers mapping = get_registers(ids, true);

    ins.addAll(mapping.head);
    ins.add(new sparrowv.Print(mapping.reg[0]));
  }

  /**
   * f0 -> "error"
   * f1 -> "("
   * f2 -> StringLiteral()
   * f3 -> ")"
   */
  public void visit(ErrorMessage n) {
    ins.add(new sparrowv.ErrorMessage(n.f2.f0.toString()));
  }

  /**
   * f0 -> "goto"
   * f1 -> Label()
   */
  public void visit(Goto n) {
    ins.add(new sparrowv.Goto(new IR.token.Label(n.f1.f0.toString())));
  }

  /**
   * f0 -> "if0"
   * f1 -> Identifier()
   * f2 -> "goto"
   * f3 -> Label()
   */
  public void visit(IfGoto n) {
    Identifier[] ids = {n.f1};
    Registers mapping = get_registers(ids, true);

    ins.addAll(mapping.head);
    ins.add(new sparrowv.IfGoto(mapping.reg[0], new IR.token.Label(n.f3.f0.toString())));
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
    Identifier[] ids = {n.f0, n.f3};
    Registers mapping = get_registers(ids);

    ins.addAll(mapping.head);
    List<IR.token.Identifier> args = new ArrayList<>();;
    for (Node a : n.f5.nodes) {
      String s_id = ((Identifier)a).f0.toString();
      String id = lv.lookup(s_id, idx);
      IR.token.Identifier arg;
      if (lv.all_registers.contains(id)) {
        arg = new IR.token.Identifier("param_save_" + id);
        ins.add(new sparrowv.Move_Id_Reg(arg, new Register(id)));
      } else {
        arg = new IR.token.Identifier(id);
      }
      args.add(arg);
    }
    // caller save
    List<String> cur_alive = lv.alive_reg(idx + 1);
    // don't save register we are using to store return
    cur_alive.remove(mapping.reg[0].toString());

    Map<String, IR.token.Identifier> saved = new HashMap<>();
    for (String r : cur_alive) {
      saved.put(r, new IR.token.Identifier("stack_save_" + r));
      ins.add(new sparrowv.Move_Id_Reg(saved.get(r), new Register(r)));
    }
    ins.add(new sparrowv.Call(mapping.reg[0], mapping.reg[1], args));
    for (String r : cur_alive) {
      ins.add(new sparrowv.Move_Reg_Id(new Register(r), saved.get(r)));
    }

    // callee writeback
    ins.addAll(mapping.tail);
  }

  /**
   * f0 -> "if0"
   * f1 -> Identifier()
   * f2 -> "goto"
   * f3 -> Label()
   */
  public void visit(If n) {
    Identifier[] ids = {n.f1};
    Registers mapping = get_registers(ids, true);

    ins.addAll(mapping.head);
    ins.add(new sparrowv.IfGoto(mapping.reg[0], new IR.token.Label(n.f3.f0.toString())));
  }
}
