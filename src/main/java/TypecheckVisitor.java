import java.util.*;

import minijava.visitor.GJVoidDepthFirst;
import minijava.syntaxtree.*;

public class TypecheckVisitor extends GJVoidDepthFirst<Vector<String>> {
  public GoalSymbolTable symbol_table_;

  public TypecheckVisitor(GoalSymbolTable symbol_table) {
    super();
    symbol_table_ = symbol_table;
  }

  public static boolean acyclic(HashMap<String, String> m) {
    for (String k : m.keySet()) {
      if (!acyclic(k, m, new HashSet<>())) {
        return false;
      }
    }
    return true;
  }

  public static boolean acyclic(String k, HashMap<String, String> m, HashSet<String> seen) {
    if (k == null) {
      return true;
    }

    if (seen.contains(k)) {
      return false;
    }

    seen.add(k);
    return acyclic(m.get(k), m, seen);
  }


  public void visit(Goal n, Vector<String> depth) {
    // distinct (mc, d1, ..., dn)
    HashSet<String> ids = new HashSet<>();
    ids.add(n.f0.accept(new ToStringVisitor()));
    for (Node c : n.f1.nodes) {
      final String name = c.accept(new ToStringVisitor());
      if (ids.contains(name)) {
        throw new TypecheckError();
      }
      ids.add(name);
    }

    if (!acyclic(symbol_table_.link_set_)) {
      throw new TypecheckError();
    }

    // |- mc
    n.f0.accept(this, depth);
    // |- d_i
    n.f1.accept(this, depth);
  }

  public void visit(MainClass n, Vector<String> depth) {
    final String main_class = n.accept(new ToStringVisitor());
    final MethodSymbolTable main_method = symbol_table_
      .classes_.get(main_class)
      .methods_.get("main");

    // distinct (id_1, ..., id_r)
    n.f14.accept(new DistinctVisitor());
    // check types of ids are valid
    n.f14.accept(this, depth);

    for (final String id : main_method.declarations_.keySet()) {
      if (main_method.arguments_.containsKey(id)) {
        throw new TypecheckError();
      }
    }

    // |- s_i
    Vector<String> s_depth = new Vector<>(depth);
    s_depth.add(main_class);
    s_depth.add("main");
    n.f15.accept(this, s_depth);
  }

  public void visit(ClassDeclaration n, Vector<String> depth) {
    // distinct(id_1, ..., id_f)
    n.f3.accept(new DistinctVisitor());
    // check types of ids
    n.f3.accept(this, depth);

    // distinct(methodname(m_1), ..., methodname(m_k))
    n.f4.accept(new DistinctVisitor());

    // id |- m_k
    Vector<String> m_depth = new Vector<>(depth);
    m_depth.add(n.accept(new ToStringVisitor()));
    n.f4.accept(this, m_depth);
  }

  public void visit(ClassExtendsDeclaration n, Vector<String> depth) {
    // distinct(id_1, ..., id_f)
    n.f5.accept(new DistinctVisitor());
    // check types of ids
    n.f5.accept(this, depth);

    // distinct(methodname(m_1), ..., methodname(m_k))
    n.f6.accept(new DistinctVisitor());

    // noOverloading(id, id^P, methodname(m_i))
    String id = n.f1.f0.toString();
    String id_P = n.f3.f0.toString();
    for (final Node m : n.f6.nodes) {
      final String method_name = m.accept(new ToStringVisitor());
      // methodtype (id_P, m) != 0
      if (symbol_table_.methods(id_P).containsKey(method_name)) {
        // methodtype(id, m) == methodtype(id_P, m)
        if (!symbol_table_.methods(id).get(method_name)
              .equals(symbol_table_.methods(id_P).get(method_name))) {
          throw new TypecheckError();
        }
      }
    }

    // id |- m_k
    Vector<String> m_depth = new Vector<>(depth);
    m_depth.add(n.accept(new ToStringVisitor()));
    n.f6.accept(this, m_depth);
  }

  public void visit(MethodDeclaration n, Vector<String> depth) {
    // distinct(args)
    n.f4.accept(new DistinctVisitor());
    n.f4.accept(this, depth);

    // distinct(decls)
    n.f7.accept(new DistinctVisitor());
    n.f7.accept(this, depth);

    // distinct(args, decls)
    final String method_name = n.accept(new ToStringVisitor());
    MethodSymbolTable m_symbols = symbol_table_.classes_.get(depth.get(0))
                                               .methods_.get(method_name);
    for (String id : m_symbols.arguments_.keySet()) {
      if (m_symbols.declarations_.keySet().contains(id)) {
        throw new TypecheckError();
      }
    }

    // A, C |- s_i
    Vector<String> n_depth = new Vector<>(depth);
    n_depth.add(method_name);
    n.f8.accept(this, n_depth);

    // A, C |- e : t
    if (!symbol_table_.inherits(n.f10.accept(new ExpressionType(symbol_table_), n_depth),
                                m_symbols.return_type_)) {
      throw new TypecheckError();
    }
  }

  public void visit(Type n, Vector<String> depth) {
    HashSet<String> primitive = new HashSet<>();
    primitive.add("int");
    primitive.add("int[]");
    primitive.add("boolean");

    final String t = n.accept(new ToStringVisitor());

    if (!primitive.contains(t) && !symbol_table_.classes_.containsKey(t)) {
      throw new TypecheckError();
    }
  }

  public void visit(Statement n, Vector<String> depth) {
    n.f0.choice.accept(this, depth);
  }

  public void visit(AssignmentStatement n, Vector<String> depth) {
    String lhs_type = symbol_table_.lookup(n.f0.accept(new ToStringVisitor()), depth);
    String rhs_type = n.f2.accept(new ExpressionType(symbol_table_), depth);

    if (!symbol_table_.inherits(rhs_type, lhs_type)) {
      throw new TypecheckError();
    }
  }

  public void visit(ArrayAssignmentStatement n, Vector<String> depth) {
    String idx_type = n.f2.accept(new ExpressionType(symbol_table_), depth);
    String rhs_type = n.f5.accept(new ExpressionType(symbol_table_), depth);

    if (!idx_type.equals("int") || !rhs_type.equals("int")) {
      throw new TypecheckError();
    }
  }

  public void visit(IfStatement n, Vector<String> depth) {
    String cond_type = n.f2.accept(new ExpressionType(symbol_table_), depth);
    if (!cond_type.equals("boolean")) {
      throw new TypecheckError();
    }

    n.f4.accept(this, depth);
    n.f6.accept(this, depth);
  }

  public void visit(WhileStatement n, Vector<String> depth) {
    String cond_type = n.f2.accept(new ExpressionType(symbol_table_), depth);
    if (!cond_type.equals("boolean")) {
      throw new TypecheckError();
    }

    n.f4.accept(this, depth);
  }

  public void visit(PrintStatement n, Vector<String> depth) {
    String arg_type = n.f2.accept(new ExpressionType(symbol_table_), depth);
    if (!arg_type.equals("int")) {
      throw new TypecheckError();
    }
  }
}
