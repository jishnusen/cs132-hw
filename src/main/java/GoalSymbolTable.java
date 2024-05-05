import java.util.*;

public class GoalSymbolTable {
  public HashMap<String, String> link_set_ = new HashMap<>();
  public HashMap<String, ClassSymbolTable> classes_ = new HashMap<>();
  public String main_class_ = null;

  public void add_main(String main_name) {
    main_class_ = main_name;

    ClassSymbolTable main_table = new ClassSymbolTable();
    main_table.methods_.put("main", new MethodSymbolTable());
    classes_.put(main_name, main_table);
  }

  // t <= tp
  public boolean child_of(String t, String tp) {
    if (t.equals(tp)) {
      return true;
    }

    if (!link_set_.containsKey(t)) {
      return false;
    }

    String cur = t;
    while (!cur.equals(tp) && link_set_.containsKey(cur)) {
      cur = link_set_.get(cur);
      if (cur.equals(tp)) {
        return true;
      }
    }

    return false;
  }

  public IndexedMap<String, String> fields(String class_name) {
    if (!classes_.containsKey(class_name)) {
      throw new TypecheckError();
    }

    IndexedMap<String, String> res = new IndexedMap<>();
    Stack<IndexedMap<String, String>> inherit = new Stack<>();
    inherit.add(classes_.get(class_name).declarations_);

    String cur = class_name;
    while (link_set_.containsKey(cur)) {
      cur = link_set_.get(cur);
      inherit.add(classes_.get(cur).declarations_);
    }

    while (!inherit.empty()) {
      IndexedMap<String, String> fields = inherit.pop();
      for (String k : fields.insertionOrder) {
        res.put(k, fields.get(k));
      }
    }
    return res;
  }

  public IndexedMap<String, MethodSymbolTable> methods(String class_name) {
    if (!classes_.containsKey(class_name)) {
      throw new TypecheckError();
    }

    IndexedMap<String, MethodSymbolTable> res = new IndexedMap<>();
    Stack<IndexedMap<String, MethodSymbolTable>> inherit = new Stack<>();
    inherit.add(classes_.get(class_name).methods_);

    String cur = class_name;
    while (link_set_.containsKey(cur)) {
      cur = link_set_.get(cur);
      inherit.add(classes_.get(cur).methods_);
    }

    while (!inherit.empty()) {
      IndexedMap<String, MethodSymbolTable> fields = inherit.pop();
      for (String k : fields.insertionOrder) {
        res.put(k, fields.get(k));
      }
    }
    return res;
  }

  public void put_decl(String k, String v, Vector<String> depth) {
    classes_.get(depth.get(0)).put_decl(k, v, depth);
  }

  public String toString() {
    String res = "";
    for (String k : classes_.keySet()) {
      res += k;
      res += "\n";
      res += classes_.get(k).toString();
      res += "\n";
    }
    return res;
  }
}
