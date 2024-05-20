import java.util.*;

public class IdGenerator {
  static int id_ = 0;

  public static String gen_id() {
    return "v" + Integer.toString(id_++);
  }

  public static boolean is_param(String id) {
    Set<String> res = new HashSet<>();
    for (int i = 2; i < 8; i++) {
      res.add("a" + Integer.toString(i));
    }
    return res.contains(id);
  }
}
