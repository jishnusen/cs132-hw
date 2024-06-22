package lib;

import java.util.*;

import minijava.visitor.GJDepthFirst;
import minijava.syntaxtree.*;

public class ExpressionType extends GJDepthFirst<String, Vector<String>> {
  public GoalSymbolTable symbol_table_;

  public ExpressionType(GoalSymbolTable symbol_table) {
    super();
    symbol_table_ = symbol_table;
  }

  public String visit(Expression n, Vector<String> depth) {
    return n.f0.choice.accept(this, depth);
  }

  public String visit(AndExpression n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("boolean") &&
        n.f2.accept(this, depth).equals("boolean")) {
      return "boolean";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(CompareExpression n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("int") && n.f2.accept(this, depth).equals("int")) {
      return "boolean";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(PlusExpression n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("int") && n.f2.accept(this, depth).equals("int")) {
      return "int";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(MinusExpression n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("int") && n.f2.accept(this, depth).equals("int")) {
      return "int";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(TimesExpression n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("int") && n.f2.accept(this, depth).equals("int")) {
      return "int";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(ArrayLookup n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("int[]") && n.f2.accept(this, depth).equals("int")) {
      return "int";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(ArrayLength n, Vector<String> depth) {
    if (n.f0.accept(this, depth).equals("int[]")) {
      return "int";
    } else {
      throw new TypecheckError();
    }
  }

  public String visit(MessageSend n, Vector<String> depth) {
    String obj_type = n.f0.accept(this, depth);
    String obj_method = n.f2.accept(new ToStringVisitor());
    String[] arg_types;
    String args = n.f4.accept(this, depth);
    if (args == null) {
      arg_types = new String[0];
    } else {
      arg_types = args.split(";");
    }

    HashMap<String, MethodSymbolTable> avail_methods = symbol_table_.methods(obj_type);
    if (!avail_methods.containsKey(obj_method)) {
      throw new TypecheckError();
    }
    MethodSymbolTable call = avail_methods.get(obj_method);

    if (arg_types.length != call.arg_order_.size()) {
      throw new TypecheckError();
    }
    for (int i = 0; i < arg_types.length; i++) {
      final String true_type = call.arguments_.get(call.arg_order_.get(i));
      if (!symbol_table_.child_of(arg_types[i], true_type)) {
        throw new TypecheckError();
      }
    }
    return call.return_type_;
  }

  public String visit(ExpressionList n, Vector<String> depth) {
    return n.f0.accept(this, depth) + n.f1.accept(this, depth);
  }

  public String visit(ExpressionRest n, Vector<String> depth) {
    return ";" + n.f1.accept(this, depth);
  }

  public String visit(NodeListOptional n, Vector<String> depth) {
    String res = "";
    for (Node e : n.nodes) {
      res += e.accept(this, depth);
    }
    return res;
  }

  /* Primary Expression */
  public String visit(PrimaryExpression n, Vector<String> depth) {
    return n.f0.choice.accept(this, depth);
  }

  // c
  public String visit(IntegerLiteral n, Vector<String> depth) {
    return "int";
  }

  // true
  public String visit(TrueLiteral n, Vector<String> depth) {
    return "boolean";
  }

  // false
  public String visit(FalseLiteral n, Vector<String> depth) {
    return "boolean";
  }

  // id
  public String visit(Identifier n, Vector<String> depth) {
    return symbol_table_.lookup(n.accept(new ToStringVisitor()), depth);
  }

  // this
  public String visit(ThisExpression n, Vector<String> depth) {
    if (depth.size() > 0 && !depth.get(0).equals(symbol_table_.main_class_)) {
      return depth.get(0);
    } else {
      throw new TypecheckError();
    }
  }

  // new int[e]
  public String visit(ArrayAllocationExpression n, Vector<String> depth) {
    if (n.f3.accept(this, depth).equals("int")) {
      return "int[]";
    } else {
      throw new TypecheckError();
    }
  }

  // new id()
  public String visit(AllocationExpression n, Vector<String> depth) {
    final String obj_type = n.f1.accept(new ToStringVisitor());
    if (symbol_table_.classes_.containsKey(obj_type)) {
      return obj_type;
    } else {
      throw new TypecheckError();
    }
  }

  // !e
  public String visit(NotExpression n, Vector<String> depth) {
    if (n.f1.accept(this, depth).equals("boolean")) {
      return "boolean";
    } else {
      throw new TypecheckError();
    }
  }

  // (e)
  public String visit(BracketExpression n, Vector<String> depth) {
    return n.f1.accept(this, depth);
  }
}
