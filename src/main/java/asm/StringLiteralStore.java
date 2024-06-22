import java.util.*;

public class StringLiteralStore {
  List<String> string_literals = new ArrayList<>();

  public int addStringLiteral(String s) {
    string_literals.add(s);
    return string_literals.size() - 1;
  }

  public void print() {
    System.out.println(".data");
    for (int i = 0; i < string_literals.size(); i++) {
      // System.out.println(".globl msg_" + i);
      System.out.println("msg_" + i + ":");
      System.out.println("\t.asciiz " + string_literals.get(i));
      System.out.println("\t.align 2");
      System.out.println();
      System.out.println();
    }
  }
}
