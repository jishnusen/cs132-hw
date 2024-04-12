import java.util.*;

public class GoalSymbolTable {
  public HashMap<String, String> link_set_;
  public HashMap<String, ClassSymbolTable> classes_ = new HashMap<>();

  public GoalSymbolTable(HashMap<String, String> link_set) {
    link_set_ = link_set;
  }

  public void add_main(String main_name) {
    ClassSymbolTable main_table = new ClassSymbolTable();
    main_table.methods_.put("main{}", new MethodSymbolTable());
    classes_.put(main_name, main_table);
  }

  // t <= tp
  public boolean inherits(String t, String tp) {
    if (t.equals(tp)) {
      return true;
    }

    if (!link_set_.containsKey(t)) {
      return false;
    }

    if (link_set_.get(t) == null) {
      return t.equals(tp);
    }

    return inherits(link_set_.get(t), tp);
  }

  public HashMap<String, String> fields(String class_name) {
    if (!classes_.containsKey(class_name)) {
      throw new TypecheckError();
    }

    HashMap<String, String> res = new HashMap<>();
    res.putAll(classes_.get(class_name).declarations_);

    if (link_set_.get(class_name) == null) {
      return res;
    } else {
      fields(link_set_.get(class_name)).forEach(res::putIfAbsent);
    }
    return res;
  }

  public HashMap<String, MethodSymbolTable> methods(String class_name) {
    if (!classes_.containsKey(class_name)) {
      throw new TypecheckError();
    }

    HashMap<String, MethodSymbolTable> res = new HashMap<>();
    res.putAll(classes_.get(class_name).methods_);

    if (link_set_.get(class_name) == null) {
      return res;
    } else {
      methods(link_set_.get(class_name)).forEach(res::putIfAbsent);
    }
    return res;
  }

  public String lookup(String k, Vector<String> depth) {
    HashMap<String, String> avail_vars = fields(depth.get(0));
    MethodSymbolTable method = methods(depth.get(0)).get(depth.get(1));
    avail_vars.putAll(method.arguments_);
    avail_vars.putAll(method.declarations_);

    if (avail_vars.containsKey(k)) {
      return avail_vars.get(k);
    } else {
      throw new TypecheckError();
    }
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
