import java.util.*;

import minijava.visitor.GJNoArguDepthFirst;
import minijava.syntaxtree.*;

import sparrow.*;
import IR.token.Label;
import IR.token.FunctionName;

public class ExpressionTranslator extends GJNoArguDepthFirst<IR.token.Identifier> {
  GoalSymbolTable symbol_table_;
  Vector<String> depth_;
  Map<String, IR.token.Identifier> identifiers_;
  List<Instruction> instructions_;

  public ExpressionTranslator(GoalSymbolTable symbol_table,
                              Vector<String> depth,
                              Map<String, IR.token.Identifier> identifiers,
                              List<Instruction> instructions) {
    super();
    symbol_table_ = symbol_table;
    depth_ = depth;
    identifiers_ = identifiers;
    instructions_ = instructions;
  }

  @Override
  public IR.token.Identifier visit(Expression n) {
    return n.f0.choice.accept(this);
  }

  @Override
  public IR.token.Identifier visit(AndExpression n) {
    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(result, 0));

    IR.token.Identifier lhs = n.f0.accept(this);
    Label else_label = IdGenerator.gen_label();
    instructions_.add(new IfGoto(lhs, else_label));

    IR.token.Identifier rhs = n.f2.accept(this);
    instructions_.add(new Move_Id_Id(result, rhs));
    instructions_.add(new LabelInstr(else_label));

    return result;
  }

  @Override
  public IR.token.Identifier visit(CompareExpression n) {
    IR.token.Identifier lhs = n.f0.accept(this);
    IR.token.Identifier rhs = n.f2.accept(this);

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new LessThan(result, lhs, rhs));

    return result;
  }

  @Override
  public IR.token.Identifier visit(PlusExpression n) {
    IR.token.Identifier lhs = n.f0.accept(this);
    IR.token.Identifier rhs = n.f2.accept(this);

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Add(result, lhs, rhs));

    return result;
  }

  @Override
  public IR.token.Identifier visit(MinusExpression n) {
    IR.token.Identifier lhs = n.f0.accept(this);
    IR.token.Identifier rhs = n.f2.accept(this);

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Subtract(result, lhs, rhs));

    return result;
  }

  @Override
  public IR.token.Identifier visit(TimesExpression n) {
    IR.token.Identifier lhs = n.f0.accept(this);
    IR.token.Identifier rhs = n.f2.accept(this);

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Multiply(result, lhs, rhs));

    return result;
  }

  @Override
  public IR.token.Identifier visit(ArrayLookup n) {
    IR.token.Identifier array_address = n.f0.accept(this);
    instructions_.add(new IfGoto(array_address, IdGenerator.null_except(depth_)));

    IR.token.Identifier array_len = IdGenerator.gen_id();
    instructions_.add(new Load(array_len, array_address, 0));

    IR.token.Identifier index = n.f2.accept(this);
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

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Load(result, array_address, 4));
    return result;
  }

  @Override
  public IR.token.Identifier visit(ArrayLength n) {
    IR.token.Identifier array_address = n.f0.accept(this);
    instructions_.add(new IfGoto(array_address, IdGenerator.null_except(depth_)));

    IR.token.Identifier array_len = IdGenerator.gen_id();
    instructions_.add(new Load(array_len, array_address, 0));

    return array_len;
  }

  @Override
  public IR.token.Identifier visit(MessageSend n) {
    IR.token.Identifier obj_address = n.f0.accept(this);
    instructions_.add(new IfGoto(obj_address, IdGenerator.null_except(depth_)));

    String object_type = n.f0.accept(new ExpressionType(symbol_table_), depth_);
    String method_name = n.f2.accept(new ToStringVisitor());

    IndexedMap<String, MethodSymbolTable> avail_methods = symbol_table_.methods(object_type);
    int method_index = avail_methods.insertionOrder.indexOf(method_name);
    MethodSymbolTable method_table = avail_methods.get(method_name);

    IR.token.Identifier mtable = IdGenerator.gen_id();
    instructions_.add(new Load(mtable, obj_address, 4));
    IR.token.Identifier fnptr = IdGenerator.gen_id();
    instructions_.add(new Load(fnptr, mtable, 4 * method_index));

    ArrayList<IR.token.Identifier> args = new ArrayList<>();
    args.add(obj_address);
    if (n.f4.node != null) {
      args.addAll(get_args((ExpressionList)n.f4.node));
    }

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Call(result, fnptr, args));

    return result;
  }

  ArrayList<IR.token.Identifier> get_args(ExpressionList exprs) {
    ArrayList<IR.token.Identifier> args = new ArrayList<>();
    args.add(exprs.f0.accept(this));

    for (Node e : exprs.f1.nodes) {
      ExpressionRest n = (ExpressionRest)e;
      args.add(n.f1.accept(this));
    }
    return args;
  }

  @Override
  public IR.token.Identifier visit(PrimaryExpression n) {
    return n.f0.choice.accept(this);
  }

  @Override
  public IR.token.Identifier visit(IntegerLiteral n) {
    IR.token.Identifier result = IdGenerator.gen_id();
    int value = Integer.parseInt(n.f0.toString());
    instructions_.add(new Move_Id_Integer(result, value));

    return result;
  }

  @Override
  public IR.token.Identifier visit(TrueLiteral n) {
    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(result, 1));
    return result;
  }

  @Override
  public IR.token.Identifier visit(FalseLiteral n) {
    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(result, 0));
    return result;
  }

  @Override
  public IR.token.Identifier visit(Identifier n) {
    String var_name = n.accept(new ToStringVisitor());

    IR.token.Identifier result = IdGenerator.gen_id();
    if (identifiers_.containsKey(var_name)) { // param or decl
      instructions_.add(new Move_Id_Id(result, identifiers_.get(var_name)));
      return result;
    }
    // class variable
    IR.token.Identifier this_reg = new IR.token.Identifier("this");
    IR.token.Identifier vtable = IdGenerator.gen_id();
    instructions_.add(new Load(vtable, this_reg, 0));
    int offset = 4 * symbol_table_.offset(depth_.get(0), var_name);
    instructions_.add(new Load(result, vtable, offset));
    return result;
  }

  @Override
  public IR.token.Identifier visit(ThisExpression n) {
    return new IR.token.Identifier("this");
  }

  @Override
  public IR.token.Identifier visit(ArrayAllocationExpression n) {
    IR.token.Identifier four = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(four, 4));

    IR.token.Identifier size = n.f3.accept(this);

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Multiply(result, four, size));
    instructions_.add(new Add(result, result, four));
    instructions_.add(new Alloc(result, result));

    instructions_.add(new Store(result, 0, size));

    return result;
  }

  @Override
  public IR.token.Identifier visit(AllocationExpression n) {
    String object_type = n.f1.accept(new ToStringVisitor());
    FunctionName constructor = new FunctionName(object_type + "__");

    IR.token.Identifier fptr = IdGenerator.gen_id();
    instructions_.add(new Move_Id_FuncName(fptr, constructor));

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Call(result, fptr, new ArrayList<IR.token.Identifier>()));

    return result;
  }

  @Override
  public IR.token.Identifier visit(NotExpression n) {
    IR.token.Identifier arg = n.f1.accept(this);

    IR.token.Identifier result = IdGenerator.gen_id();
    instructions_.add(new Move_Id_Integer(result, 1));
    instructions_.add(new LessThan(result, arg, result));

    return result;
  }

  public IR.token.Identifier visit(BracketExpression n) {
    return n.f1.accept(this);
  }
}
