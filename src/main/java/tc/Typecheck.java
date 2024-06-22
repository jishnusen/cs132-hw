import java.util.*;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.Node;

// import hw2.*;

public class Typecheck {
  public static void main(String[] args) {
    try {
      Node root = (new MiniJavaParser(System.in)).Goal();

      // Phase 1
      GoalSymbolTable symbol_table = new GoalSymbolTable();
      root.accept(new SymbolTableBuilder(symbol_table), new Vector<>());

      // Phase 2
      TypecheckVisitor typechecker = new TypecheckVisitor(symbol_table);
      root.accept(typechecker, new Vector<>());

      System.out.println("Program type checked successfully");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Type error");
    }
  }
}
