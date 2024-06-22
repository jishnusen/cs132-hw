import java.util.*;

import IR.SparrowParser;
import IR.ParseException;

public class SV2V {
  public static void main(String[] args) {
    IR.registers.Registers.SetRiscVregs();
    try {
      IR.syntaxtree.Node root =
        new IR.SparrowParser(System.in).Program();
      IR.visitor.SparrowVConstructor svc =
        new IR.visitor.SparrowVConstructor();
      root.accept(svc);
      sparrowv.Program program = svc.getProgram();

      StackPassVisitor stackPass = new StackPassVisitor();
      program.accept(stackPass);

      TranslateVisitor translator = new TranslateVisitor(stackPass.functions);
      program.accept(translator);
    } catch (IR.ParseException e) {
      System.out.println(e.toString());
    }
  }
}
