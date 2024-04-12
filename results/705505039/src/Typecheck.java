import java.util.*;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.Node;

// import hw2.*;

public class Typecheck {
  public static void main(String[] args) {
    try {
      Node root = (new MiniJavaParser(System.in)).Goal();

      HashMap<String, String> linkset = new HashMap<>();
      root.accept(new LinksetBuilder(), linkset);

      GoalSymbolTable symbol_table = new GoalSymbolTable(linkset);
      root.accept(new SymbolTableBuilder(symbol_table), new Vector<>());

      TypecheckVisitor typechecker = new TypecheckVisitor(symbol_table);
      root.accept(typechecker, new Vector<>());

      System.out.println("Program type checked successfully");
    } catch (ParseException e) {
      System.out.println(e.toString());
    } catch (TypecheckError e) {
      System.out.println("Type error");
    }
  }
}
