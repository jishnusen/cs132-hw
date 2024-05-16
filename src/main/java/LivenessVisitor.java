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

    Map<String, Integer> labels = new HashMap<>();
    for (int i = 0; i < instructions.size(); i++) {
      Instruction ins = (Instruction)(instructions.get(i));
      List<String> ids_used = ins.accept(new IdsUsed());
      String ids_set = ins.accept(new IdsSet());

      Node ins_c = ins.f0.choice;
      for (String id : ids_used) {
        if (lv.liveness.containsKey(id)) {
          List<Interval> intervals = lv.liveness.get(id);
          intervals.get(intervals.size() - 1).end = i;
        } else {
          lv.liveness.put(id, new ArrayList<>(Arrays.asList(new Interval(i, i))));
        }
      }

      if (ids_set != null && !ids_used.contains(ids_set)) {
        lv.liveness.putIfAbsent(ids_set,
            new ArrayList<>(Arrays.asList(new Interval(i, i))));
        List<Interval> intervals = lv.liveness.get(ids_set);
        Interval last = intervals.get(intervals.size() - 1);
        last.end = i;
      }
    }

    String return_id = n.f5.f2.f0.toString();
    List<Interval> return_liveness = lv.liveness.get(return_id);
    return_liveness.get(0).end = return_liveness.get(return_liveness.size() - 1).end;
    return_liveness.subList(1, return_liveness.size()).clear();

    method_liveness.put(n.f1.f0.toString(), lv);
  }
}
