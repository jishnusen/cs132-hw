import java.util.*;

public class MethodSymbolTable {
  public String return_type_ = null;
  public Vector<String> arg_order_ = new Vector<>();
  public HashMap<String, String> arguments_ = new HashMap<>();
  public HashMap<String, String> declarations_ = new HashMap<>();

  public void put_decl(String k, String v, Vector<String> depth) {
    declarations_.put(k, v);
  }

  public String toString() {
    String res = "";
    res += "\t\tReturn: " + return_type_ + "\n";
    res += "\t\tParams:\n";
    for (HashMap.Entry<String, String> arg : arguments_.entrySet()) {
      res += "\t\t\t" + arg.getKey() + " : " + arg.getValue();
      res += "\n";
    }

    res += "\t\tDecl:\n";
    for (HashMap.Entry<String, String> arg : declarations_.entrySet()) {
      res += "\t\t\t" + arg.getKey() + " : " + arg.getValue();
      res += "\n";
    }

    return res;
  }

  public boolean equals(MethodSymbolTable b) {
    if (arg_order_.size() != b.arg_order_.size()) {
      return false;
    }

    for (int i = 0; i < arg_order_.size(); i++) {
      final String a_arg_type = arguments_.get(arg_order_.get(i));
      final String b_arg_type = b.arguments_.get(b.arg_order_.get(i));
      if (!a_arg_type.equals(b_arg_type)) {
        return false;
      }
    }

    return return_type_.equals(b.return_type_);
  }
}
