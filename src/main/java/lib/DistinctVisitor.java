package lib;

import java.util.*;

import minijava.visitor.DepthFirstVisitor;
import minijava.syntaxtree.*;

public class DistinctVisitor extends DepthFirstVisitor {
  public void visit(NodeListOptional n) {
    HashSet<String> ids = new HashSet<>();

    for (Node i : n.nodes) {
      final String id = i.accept(new ToStringVisitor());
      if (ids.contains(id)) {
        throw new TypecheckError();
      }
      ids.add(id);
    }
  }

  public void visit(FormalParameterList n) {
    HashSet<String> ids = new HashSet<>();
    ids.add(n.f0.accept(new ToStringVisitor()));

    for (Node p : n.f1.nodes) {
      final String param = p.accept(new ToStringVisitor());
      if (ids.contains(param)) {
        throw new TypecheckError();
      }
      ids.add(param);
    }
  }
}
