import java.util.*;

import IR.visitor.DepthFirstVisitor;
import IR.syntaxtree.*;

public class LivenessVisitor extends DepthFirstVisitor {
  public Map<String, LivenessTable> method_liveness = new HashMap<>();

   /**
    * f0 -> "func"
    * f1 -> FunctionName()
    * f2 -> "("
    * f3 -> ( Identifier() )*
    * f4 -> ")"
    * f5 -> Block()
    */
  public void visit(FunctionDeclaration n) {
    LivenessTable lv = new LivenessTable();
    List<Node> instructions = n.f5.f0.nodes;
    String method = n.f1.f0.toString();
    List<Integer> calls = new ArrayList<>();

    Map<String, Interval> rg_params = new HashMap<>();
    for (int i = 0; i < n.f3.nodes.size() && i < 6; i++) {
      Node p = n.f3.nodes.get(i);
      String param = ((Identifier)p).f0.toString();
      rg_params.put(param, new Interval(0, 0));
      rg_params.get(param).id = "a" + Integer.toString(i + 2);
    }
    for (int i = rg_params.size(); i < 6; i++) {
      lv.all_registers.add("a" + Integer.toString(i + 2));
    }

    Map<String, Integer> labels = new HashMap<>();
    for (int i = 0; i < instructions.size(); i++) {
      Instruction ins = (Instruction)(instructions.get(i));

      if (ins.f0.choice instanceof Call) {
        calls.add(i);
      }

      if (ins.f0.choice instanceof LabelWithColon) {
        LabelWithColon lwc = (LabelWithColon)(ins.f0.choice);
        labels.put(lwc.f0.f0.toString(), i);
      }

      String label = null;
      if (ins.f0.choice instanceof Goto) {
        Goto gt = (Goto)(ins.f0.choice);
        label = gt.f1.f0.toString();
      } else if (ins.f0.choice instanceof IfGoto) {
        IfGoto gt = (IfGoto)(ins.f0.choice);
        label = gt.f3.f0.toString();
      }

      // jump backward
      if (label != null && labels.containsKey(label)) {
        Interval loop = new Interval(labels.get(label), i);
        List<Interval> ivs = new ArrayList<>();
        ivs.addAll(lv.liveness.values());
        ivs.addAll(rg_params.values());
        for (Interval iv : ivs) {
          if (iv.contains(loop.start)) {
            // iv.start = Math.min(iv.start, loop.start);
            iv.end = i;
          }
        }
      }

      List<String> ids_used = ins.accept(new IdsUsed());

      for (String id : ids_used) {
        if (rg_params.containsKey(id)) {
          rg_params.get(id).end = i;
        } else if (lv.liveness.containsKey(id)) {
          lv.liveness.get(id).end = i;
        } else {
          lv.liveness.put(id, new Interval(i, i));
        }
      }
    }

    String return_id = n.f5.f2.f0.toString();
    if (lv.liveness.containsKey(return_id))
      lv.liveness.get(return_id).end = instructions.size();

    lv.assign_LSRA();
    if (!calls.isEmpty()) {
      float avg_alive_call = 0;
      for (int i : calls) {
        avg_alive_call += lv.alive_reg(i).size();
      }
      avg_alive_call /= calls.size();
      lv.avg_alive_call = avg_alive_call;
    }

    for (int i = 2; i < 8; i++) {
      lv.all_registers.add("a" + Integer.toString(i));
    }
    lv.liveness.putAll(rg_params);
    lv.liveness.get(return_id).end = instructions.size();

    method_liveness.put(method, lv);
  }
}
