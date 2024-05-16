import java.util.*;

public class IdGenerator {
  static int id_ = 0;

  public static String gen_id() {
    return "v" + Integer.toString(id_++);
  }
}
