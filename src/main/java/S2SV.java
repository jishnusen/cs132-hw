import java.util.*;

import IR.SparrowParser;
import IR.ParseException;
import IR.syntaxtree.Node;

public class S2SV {
  public static void main(String[] args) throws ParseException {
    Node root = (new SparrowParser(System.in)).Program();

    // Phase 1
    LivenessVisitor lv_visitor = new LivenessVisitor();
    root.accept(lv_visitor);

    for (String fn : lv_visitor.method_liveness.keySet()) {
      LivenessTable lv = lv_visitor.method_liveness.get(fn);
      lv.assign_LSRA();
    }

    TranslateVisitor tv = new TranslateVisitor(lv_visitor.method_liveness);
    root.accept(tv);

    System.out.println(tv.p.toString());
  }
}
