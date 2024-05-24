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
  boolean callee_save = true;

  TranslateVisitor(Map<String, LivenessTable> method_liveness) {
    super();
    this.p.funDecls = new ArrayList<>();
    this.method_liveness = method_liveness;
    float total_avg_alive_call = 0;
    float total_clobbered = 0;
    for (LivenessTable lv : method_liveness.values()) {
      total_avg_alive_call += lv.avg_alive_call;
      total_clobbered += lv.clobbered().size();
    }
    this.callee_save = total_avg_alive_call > total_clobbered;
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
    for (int i = 0; i < n.f3.nodes.size(); i++) {
      Node p = n.f3.nodes.get(i);
      String param = ((Identifier)p).f0.toString();
      if (i > 5) {
        params.add(new IR.token.Identifier("arg__" + param));
        if (lv.liveness.containsKey(param)) {
          parameter_liveness.put(param, lv.liveness.get(param));
        }
      } else {
        // ins.add(new sparrowv.Move_Reg_Reg(
        //       new Register(lv.liveness.get(param).id),
        //       new Register("a" + Integer.toString(i + 2))));
      }
    }

    if (callee_save && !method.equals("Main")) {
      for (String id : lv.clobbered()) {
        IR.token.Identifier stack_id = new IR.token.Identifier("callee_save__" + id);
        ins.add(new sparrowv.Move_Id_Reg(stack_id, new Register(id)));
      }
    }

    n.f5.accept(this);

    String ret = lv.liveness.get(n.f5.f2.f0.toString()).id;
    IR.token.Identifier ret_id;
    if (lv.all_registers.contains(ret)) {
      ret_id = new IR.token.Identifier("ret_save__" + ret);
      ins.add(new sparrowv.Move_Id_Reg(ret_id, new Register(ret)));
    } else {
      ret_id = new IR.token.Identifier(ret);
    }

    if (callee_save && !method.equals("Main")) {
      for (String id : lv.clobbered()) {
        IR.token.Identifier stack_id = new IR.token.Identifier("callee_save__" + id);
        ins.add(new sparrowv.Move_Reg_Id(new Register(id), stack_id));
      }
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
            ins.add(new sparrowv.Move_Reg_Id(new Register(iv.id),
                  new IR.token.Identifier("arg__" + p)));
          } else {
            iv.id = "arg__" + p;
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

    if (load_first && ids.length == 3) {
      throw new RuntimeException("load conflict");
    }

    String[] reg_ids;
    if (ids.length == 3) {
      reg_ids = new String[]{ "t0", "t0", "t1" };
    } else {
      reg_ids = new String[]{ "t0", "t1" };
    }
    Registers res = new Registers();
    res.reg = new Register[ids.length];
    for (int i = 0; i < ids.length; i++) {
      String id = lv.lookup(ids[i].f0.toString(), idx);
      if (lv.all_registers.contains(id)) {
        res.reg[i] = new Register(id);
      } else {
        res.reg[i] = new Register(reg_ids[i]);
        IR.token.Identifier stack = new IR.token.Identifier(id);
        if (i == 0) {
          if (load_first) res.head.add(new sparrowv.Move_Reg_Id(res.reg[i], stack));
          // may need to write back result
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
    Registers mapping = get_registers(ids);
    int offset = Integer.parseInt(n.f5.f0.toString());

    ins.addAll(mapping.head);
    ins.add(new sparrowv.Load(mapping.reg[0], mapping.reg[1], offset));
    ins.addAll(mapping.tail);
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

    // caller save
    List<String> cur_alive = lv.alive(idx);
    List<String> lookahead = lv.alive(idx + 1);
    List<String> preserve_reg = new ArrayList<>();
    for (String name : lookahead) {
      String reg = lv.liveness.get(name).id;
      if (!lv.all_registers.contains(reg)) {
        continue;
      }
      if (cur_alive.contains(name)) {
        preserve_reg.add(reg);
      }
    }

    // don't save register we are using to store return
    preserve_reg.remove(mapping.reg[0].toString());

    if (callee_save) {
      List<String> non_arg = new ArrayList<>();
      for (String reg : preserve_reg) {
        if (!IdGenerator.is_param(reg)) non_arg.add(reg);
      }
      preserve_reg.removeAll(non_arg);
    }

    Map<String, IR.token.Identifier> saved = new HashMap<>();
    for (String r : preserve_reg) {
      saved.put(r, new IR.token.Identifier("stack_save__" + r));
      ins.add(new sparrowv.Move_Id_Reg(saved.get(r), new Register(r)));
    }

    List<IR.token.Identifier> args = new ArrayList<>();
    List<String> arg_ids = new ArrayList<>();
    for (int i = 0; i < n.f5.nodes.size(); i++) {
      Node a = n.f5.nodes.get(i);
      String s_id = ((Identifier)a).f0.toString();
      arg_ids.add(lv.lookup(s_id, idx));
    }
    for (int i = 0; i < arg_ids.size(); i++) {
      String r = arg_ids.get(i);
      int r_num = (r.charAt(1) - '0') - 2;
      if (IdGenerator.is_param(r) && !saved.containsKey(r) && i != r_num) {
        saved.put(r, new IR.token.Identifier("stack_save__" + r));
        ins.add(new sparrowv.Move_Id_Reg(saved.get(r), new Register(r)));
      }
    }

    Set<String> p_saved = new HashSet<>();
    for (int i = 6; i < arg_ids.size(); i++) {
      String id = arg_ids.get(i);
      IR.token.Identifier arg;
      if (lv.all_registers.contains(id)) {
        if (saved.containsKey(id)) {
          arg = saved.get(id);
        } else {
          arg = new IR.token.Identifier("param_save__" + id);
          if (!p_saved.contains(id))
            ins.add(new sparrowv.Move_Id_Reg(arg, new Register(id)));
          p_saved.add(id);
        }
      } else {
        arg = new IR.token.Identifier(id);
      }
      args.add(arg);
    }

    for (int i = 0; i < arg_ids.size() && i < 6; i++) {
      String id = arg_ids.get(i);
      if (IdGenerator.is_param(id)) {
        int r_num = (id.charAt(1) - '0') - 2;
        if (r_num != i) {
          ins.add(new sparrowv.Move_Reg_Id(
                new Register("a" + Integer.toString(i + 2)),
                saved.get(id)));
        }
      } else {
        ins.add(new sparrowv.Move_Reg_Reg(
              new Register("a" + Integer.toString(i + 2)),
              new Register(id)));
      }
    }

    ins.add(new sparrowv.Call(mapping.reg[0], mapping.reg[1], args));
    for (String r : preserve_reg) {
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
