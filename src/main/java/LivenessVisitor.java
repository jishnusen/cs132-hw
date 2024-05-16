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
      Node ins = instructions.get(i);
      List<String> ids_used = ins.accept(new IdsUsed());
      String ids_set = ins.accept(new IdsSet());
      if (ids_set != null) {
        ids_used.add(ids_set);
      }

      for (String id : ids_used) {
        if (lv.liveness.containsKey(id)) {
          lv.liveness.get(id).end = i;
        } else {
          lv.liveness.put(id, new Interval(i, i));
        }
      }
    }

    for (int i = 0; i < n.f3.nodes.size() && i < 3; i++) {
      Node p = n.f3.nodes.get(i);
      String param = ((Identifier)p).f0.toString();
      lv.liveness.putIfAbsent(param, new Interval(0, 0));
      lv.liveness.get(param).start = 0;
    }

    String return_id = n.f5.f2.f0.toString();
    lv.liveness.get(return_id).end = instructions.size();

    method_liveness.put(n.f1.f0.toString(), lv);
  }
}
