package asm;

import java.util.*;

import IR.token.Identifier;

import IR.token.Identifier;
import sparrowv.*;

public class TranslateVisitor extends sparrowv.visitor.DepthFirst {
  StringLiteralStore strings = new StringLiteralStore();
  Map<String, FunctionInfo> functions;

  String functionName = null;
  FunctionInfo function = null;

  int unique = 0;

  public TranslateVisitor(Map<String, FunctionInfo> functions) {
    this.functions = functions;
  }

  String formatLabel(IR.token.Label l) {
    return l.toString() + "__" + this.functionName;
  }

  /*   List<FunctionDecl> funDecls; */
  public void visit(Program n) {
    System.out.println(".equiv @sbrk, 9");
    System.out.println(".equiv @print_string, 4");
    System.out.println(".equiv @print_char, 11");
    System.out.println(".equiv @print_int, 1");
    System.out.println(".equiv @exit 10");
    System.out.println(".equiv @exit2, 17");

    System.out.println("\n\n.text");
    System.out.println("\tjal " + n.funDecls.get(0).functionName.toString());
    System.out.println("\tli a0, @exit");
    System.out.println("\tecall");
    System.out.println();
    System.out.println();

    System.out.println("alloc:");
    System.out.println("\tmv a1, a0");
    System.out.println("\tli a0, @sbrk");
    System.out.println("\tecall");
    System.out.println("\tjr ra");
    System.out.println();
    System.out.println();

    System.out.println("error:");
    System.out.println("\tmv a1, a0");
    System.out.println("\tli a0, @print_string");
    System.out.println("\tecall");
    System.out.println("\tli a1, 10");
    System.out.println("\tli a0, @print_char");
    System.out.println("\tecall");
    System.out.println("\tli a0, @exit");
    System.out.println("\tecall");
    System.out.println("abort_17:");
    System.out.println("\tj abort_17");
    System.out.println();
    System.out.println();

    for (FunctionDecl fd: n.funDecls) {
        fd.accept(this);
    }
    this.strings.print();
  }

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public void visit(FunctionDecl n) {
    this.functionName = n.functionName.toString();
    this.function = this.functions.get(this.functionName);
    System.out.println(this.functionName + ":");

    System.out.println("\taddi sp, sp, -" + this.function.getFrameSize());
    System.out.println("\tsw ra, 0(sp)");
    n.block.accept(this);
    System.out.println("\tlw ra, 0(sp)");
    System.out.println("\taddi sp, sp, " + this.function.getFrameSize());
    System.out.println("\tjr ra");
    System.out.print("\n\n");
  }

  /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  public void visit(Block n) {
    for (Instruction i: n.instructions) {
        i.accept(this);
    }
    System.out.println("\tlw a0, " + this.function.getOffset(n.return_id) + "(sp)");
  }

  /*   Label label; */
  public void visit(LabelInstr n) {
    System.out.println(formatLabel(n.label) + ":");
  }

  /*   Register lhs;
   *   int rhs; */
  public void visit(Move_Reg_Integer n) {
    System.out.println("\tli " + n.lhs.toString() + ", " + n.rhs);
  }

  /*   Register lhs;
   *   FunctionName rhs; */
  public void visit(Move_Reg_FuncName n) {
    System.out.println("\tla " + n.lhs.toString() + ", " + n.rhs.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(Add n) {
    System.out.println("\tadd " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(Subtract n) {
    System.out.println("\tsub " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(Multiply n) {
    System.out.println("\tmul " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(LessThan n) {
    System.out.println("\tslt " + n.lhs.toString() + ", " + n.arg1.toString() + ", " + n.arg2.toString());
  }

  /*   Register lhs;
   *   Register base;
   *   int offset; */
  public void visit(Load n) {
    System.out.println("\tlw " + n.lhs.toString() + ", " + n.offset + "(" + n.base.toString() + ")");
  }

  /*   Register base;
   *   int offset;
   *   Register rhs; */
  public void visit(Store n) {
    System.out.println("\tsw " + n.rhs.toString() + ", " + n.offset + "(" + n.base.toString() + ")");
  }

  /*   Register lhs;
   *   Register rhs; */
  public void visit(Move_Reg_Reg n) {
    System.out.println("\tmv " + n.lhs.toString() + ", " + n.rhs.toString());
  }

  /*   Identifier lhs;
   *   Register rhs; */
  public void visit(Move_Id_Reg n) {
    System.out.println("\tsw " + n.rhs.toString() + ", " + this.function.getOffset(n.lhs) + "(sp)");
  }

  /*   Register lhs;
   *   Identifier rhs; */
  public void visit(Move_Reg_Id n) {
    System.out.println("\tlw " + n.lhs.toString() + ", " + this.function.getOffset(n.rhs) + "(sp)");
  }

  /*   Register lhs;
   *   Register size; */
  public void visit(Alloc n) {
    System.out.println("\tmv a0, " + n.size.toString());
    System.out.println("\tjal alloc");
    System.out.println("\tmv " + n.lhs.toString() + ", a0");
  }

  /*   Register content; */
  public void visit(Print n) {
    System.out.println("\tmv a1, " + n.content.toString());
    System.out.println("\tli a0, @print_int");
    System.out.println("\tecall");
    System.out.println("\tli a1, 10");
    System.out.println("\tli a0, @print_char");
    System.out.println("\tecall");
  }

  /*   String msg; */
  public void visit(ErrorMessage n) {
    int msg = this.strings.addStringLiteral(n.msg);
    System.out.println("\tla a0, msg_" + msg);
    System.out.println("\tjal zero error");
  }

  /*   Label label; */
  public void visit(Goto n) {
    System.out.println("\tjal zero " + formatLabel(n.label));
  }

  /*   Register condition;
   *   Label label; */
  public void visit(IfGoto n) {
    String end = "end__" + (this.unique++);
    System.out.println("\tbne " + n.condition.toString() + ", zero, " + end);
    System.out.println("\tjal zero " + formatLabel(n.label));
    System.out.println(end + ":");
  }

  /*   Register lhs;
   *   Register callee;
   *   List<Identifier> args; */
  public void visit(Call n) {
    int param_stack = n.args.size() * 4;
    System.out.println("\taddi sp, sp, -" + param_stack);
    for (int i = 0; i < n.args.size(); i++) {
      Identifier id = n.args.get(i);
      System.out.println("\tlw a0, " + (this.function.getOffset(id) + param_stack) + "(sp)");
      System.out.println("\tsw a0, " + ((param_stack - 4) - (i * 4)) + "(sp)");
    }
    System.out.println("\tjalr " + n.callee.toString());
    System.out.println("\taddi sp, sp, " + param_stack);
    System.out.println("\tmv " + n.lhs.toString() + ", a0");
  }
}
