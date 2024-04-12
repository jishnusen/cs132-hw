import java.util.HashMap;

import minijava.visitor.GJVoidDepthFirst;
import minijava.syntaxtree.*;

public class LinksetBuilder extends GJVoidDepthFirst<HashMap<String, String>> {
  public void visit(MainClass n, HashMap<String, String> v) {
    v.put(n.f1.f0.toString(), null);
  }

  public void visit(ClassDeclaration n, HashMap<String, String> v) {
    v.put(n.f1.f0.toString(), null);
  }

  public void visit(ClassExtendsDeclaration n, HashMap<String, String> v) {
    v.put(n.f1.f0.toString(), n.f3.f0.toString());
  }
}
