import java.util.*;
import java.io.StringReader;

import minijava.MiniJavaParser;
import IR.SparrowParser;

public class Compile {
  public static void main(String[] args) throws minijava.ParseException,
                                                lib.TypecheckError,
                                                IR.ParseException {
    sparrow.Program ir_program;
    try {
      minijava.syntaxtree.Node root = (new MiniJavaParser(System.in)).Goal();

      // Phase 1
      lib.GoalSymbolTable symbol_table = new lib.GoalSymbolTable();
      root.accept(new lib.SymbolTableBuilder(symbol_table), new Vector<>());

      // Phase 2
      tc.TypecheckVisitor typechecker = new tc.TypecheckVisitor(symbol_table);
      root.accept(typechecker, new Vector<>());

      ir.TranslateVisitor tv = new ir.TranslateVisitor(symbol_table);
      root.accept(tv, new Vector<>());

      ir_program = tv.program_;
    } catch (minijava.ParseException e) {
      System.out.println("Parse Error");
      throw e;
    } catch (lib.TypecheckError e) {
      System.out.println("Type error");
      throw e;
    }

    sparrowv.Program sv_program;
    {
      StringReader reader = new StringReader(ir_program.toString());
      IR.syntaxtree.Node root = (new IR.SparrowParser(reader)).Program();
      sv.LivenessVisitor lv_visitor = new sv.LivenessVisitor();
      root.accept(lv_visitor);

      sv.TranslateVisitor tv = new sv.TranslateVisitor(lv_visitor.method_liveness);
      root.accept(tv);

      sv_program = tv.program_;
    }

    {
      IR.registers.Registers.SetRiscVregs();
      StringReader reader = new StringReader(sv_program.toString());
      IR.SparrowParser.ReInit(reader);
      IR.syntaxtree.Node root =
        IR.SparrowParser.Program();
      IR.visitor.SparrowVConstructor svc =
        new IR.visitor.SparrowVConstructor();
      root.accept(svc);
      sparrowv.Program program = svc.getProgram();

      asm.StackPassVisitor stackPass = new asm.StackPassVisitor();
      program.accept(stackPass);

      asm.TranslateVisitor translator = new asm.TranslateVisitor(stackPass.functions);
      program.accept(translator);
    }
  }
}
