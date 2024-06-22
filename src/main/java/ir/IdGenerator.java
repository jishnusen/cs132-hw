package ir;

import java.util.*;

import IR.token.*;

public class IdGenerator {
  static int id_ = 0;

  public static Identifier gen_id() {
    return new Identifier("v" + Integer.toString(id_++));
  }

  public static Label gen_label() {
    return new Label("l" + Integer.toString(id_++));
  }

  public static FunctionName fname(List<String> depth) {
    return new FunctionName(depth.get(0) + "__" + depth.get(1));
  }

  public static Label null_except(List<String> depth) {
    return new Label(depth.get(0) + "__" + depth.get(1) + "__" + "null_except");
  }

  public static Label bad_array(List<String> depth) {
    return new Label(depth.get(0) + "__" + depth.get(1) + "__" + "bad_array");
  }

  public static Label end_label(List<String> depth) {
    return new Label(depth.get(0) + "__" + depth.get(1) + "__" + "end");
  }
}
