import java.util.*;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.Node;

public class J2S {
  public static void main(String[] args) throws ParseException {
    Node root = (new MiniJavaParser(System.in)).Goal();

    // Phase 1
    GoalSymbolTable symbol_table = new GoalSymbolTable();
    root.accept(new SymbolTableBuilder(symbol_table), new Vector<>());

    TranslateVisitor tv = new TranslateVisitor(symbol_table);
    root.accept(tv, new Vector<>());

    System.out.println(tv.program_.toString());
  }
}
