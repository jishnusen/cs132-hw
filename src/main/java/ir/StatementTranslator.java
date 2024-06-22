package ir;

import java.util.*;

import minijava.visitor.DepthFirstVisitor;
import minijava.syntaxtree.*;

import sparrow.*;
import IR.token.Label;

public class StatementTranslator extends DepthFirstVisitor {
  lib.GoalSymbolTable symbol_table_;
  Vector<String> depth_;
  Map<String, IR.token.Identifier> identifiers_;
  List<Instruction> instructions_;
  ExpressionTranslator translator_;

  public StatementTranslator(lib.GoalSymbolTable symbol_table,
                             Vector<String> depth,
                             Map<String, IR.token.Identifier> identifiers,
                             List<Instruction> instructions) {
    super();
    symbol_table_ = symbol_table;
    depth_ = depth;
    identifiers_ = identifiers;
    instructions_ = instructions;
    translator_ = new ExpressionTranslator(symbol_table_, depth_, identifiers_, instructions_);
  }

  @Override
  public void visit(NodeListOptional n) {
    for (Node s : n.nodes) {
      s.accept(this);
    }
  }

  @Override
  public void visit(Statement n) {
    n.f0.accept(this);
  }

  @Override
  public void visit(minijava.syntaxtree.Block n) {
    n.f1.accept(this);
  }

  @Override
  public void visit(AssignmentStatement n) {
    String lhs_name = n.f0.accept(new lib.ToStringVisitor());
    IR.token.Identifier rhs_reg = n.f2.accept(translator_);

    if (identifiers_.containsKey(lhs_name)) { // param or decl
      instructions_.add(new Move_Id_Id(identifiers_.get(lhs_name), rhs_reg));
    } else { // class variable
      IR.token.Identifier this_reg = new IR.token.Identifier("this");
      IR.token.Identifier vtable = IdGenerator.gen_id();
      instructions_.add(new Load(vtable, this_reg, 0));
      int offset = 4 * symbol_table_.offset(depth_.get(0), lhs_name);
      instructions_.add(new Store(vtable, offset, rhs_reg));
    }
  }

  @Override
  public void visit(ArrayAssignmentStatement n) {
    String lhs_name = n.f0.accept(new lib.ToStringVisitor());
    IR.token.Identifier array_address;

    if (identifiers_.containsKey(lhs_name)) { // param or decl
      array_address = identifiers_.get(lhs_name);
    } else { // class variable
      IR.token.Identifier this_reg = new IR.token.Identifier("this");
      IR.token.Identifier vtable = IdGenerator.gen_id();
      instructions_.add(new Load(vtable, this_reg, 0));
      int offset = 4 * symbol_table_.offset(depth_.get(0), lhs_name);

      array_address = IdGenerator.gen_id();
      instructions_.add(new Load(array_address, vtable, offset));
    }
    instructions_.add(new IfGoto(array_address, IdGenerator.null_except(depth_)));

    IR.token.Identifier array_len = IdGenerator.gen_id();
    instructions_.add(new Load(array_len, array_address, 0));

    IR.token.Identifier index = n.f2.accept(translator_);
    IR.token.Identifier test_index = IdGenerator.gen_id();
    instructions_.add(new LessThan(test_index, index, array_len));
    instructions_.add(new IfGoto(test_index, IdGenerator.bad_array(depth_)));

    IR.token.Identifier zero = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(zero, 0));
    IR.token.Identifier one = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(one, 1));
    instructions_.add(new LessThan(test_index, index, zero));
    instructions_.add(new LessThan(test_index, test_index, one));
    instructions_.add(new IfGoto(test_index, IdGenerator.bad_array(depth_)));

    IR.token.Identifier four = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(four, 4));
    instructions_.add(new Multiply(index, four, index));
    instructions_.add(new Add(array_address, array_address, index));

    IR.token.Identifier rhs_reg = n.f5.accept(translator_);
    instructions_.add(new Store(array_address, 4, rhs_reg));
  }

  @Override
  public void visit(IfStatement n) {
    IR.token.Identifier condition = n.f2.accept(translator_);
    Label else_label = IdGenerator.gen_label();
    Label end_label = IdGenerator.gen_label();
    instructions_.add(new IfGoto(condition, else_label));
    n.f4.accept(this);
    instructions_.add(new Goto(end_label));
    instructions_.add(new LabelInstr(else_label));
    n.f6.accept(this);
    instructions_.add(new LabelInstr(end_label));
  }

  @Override
  public void visit(WhileStatement n) {
    Label while_label = IdGenerator.gen_label();
    Label end_label = IdGenerator.gen_label();

    instructions_.add(new LabelInstr(while_label));
    IR.token.Identifier condition = n.f2.accept(translator_);
    instructions_.add(new IfGoto(condition, end_label));

    n.f4.accept(this);
    instructions_.add(new Goto(while_label));
    instructions_.add(new LabelInstr(end_label));
  }

  @Override
  public void visit(PrintStatement n) {
    IR.token.Identifier arg_reg = n.f2.accept(translator_);
    instructions_.add(new Print(arg_reg));
  }
}
