import java.util.*;

public class ClassSymbolTable {
  public HashMap<String, String> declarations_ = new HashMap<>();
  public HashMap<String, MethodSymbolTable> methods_ = new HashMap<>();

  public void put_decl(String k, String v, Vector<String> depth) {
    if (depth.size() > 1) {
      methods_.get(depth.get(1)).put_decl(k, v, depth);
    } else {
      declarations_.put(k, v);
    }
  }

  public String toString() {
    String res = "";

    for (String decl : declarations_.keySet()) {
      res += "\t" + decl + " : " + declarations_.get(decl);
      res += "\n";
    }

    for (String method : methods_.keySet()) {
      res += "\t" + method + "\n";
      res += methods_.get(method).toString();
      res += "\n";
    }

    return res;
  }
}
