import java.util.Vector;

import minijava.visitor.GJVoidDepthFirst;
import minijava.syntaxtree.*;

public class SymbolTableBuilder extends GJVoidDepthFirst<Vector<String>> {
  public GoalSymbolTable table_;

  public SymbolTableBuilder(GoalSymbolTable table) {
    super();
    table_ = table;
  }

  // f1 -> Identifier
  public void visit(MainClass n, Vector<String> depth) {
    final String main_class = n.accept(new ToStringVisitor());

    table_.add_main(main_class);
    MethodSymbolTable main_method = table_.classes_.get(main_class)
      .methods_.get("main");
    main_method.return_type_ = "void";
    main_method.arguments_
      .put(n.f11.accept(new ToStringVisitor()), "String[]");
    main_method.arg_order_.addAll(main_method.arguments_.keySet());

    Vector<String> n_depth = new Vector<>(depth);
    n_depth.add(main_class);
    n_depth.add("main");
    n.f14.accept(this, n_depth);
  }

  public void visit(VarDeclaration n, Vector<String> depth) {
    table_.put_decl(n.accept(new ToStringVisitor()),
                    n.f0.accept(new ToStringVisitor()),
                    depth);
  }

  public void visit(ClassDeclaration n, Vector<String> depth) {
    final String class_name = n.accept(new ToStringVisitor());
    table_.classes_.put(class_name, new ClassSymbolTable());

    Vector<String> n_depth = new Vector<>(depth);
    n_depth.add(class_name);
    n.f3.accept(this, n_depth);
    n.f4.accept(this, n_depth);
  }

  public void visit(ClassExtendsDeclaration n, Vector<String> depth) {
    final String class_name = n.accept(new ToStringVisitor());
    final String parent_name = n.f3.accept(new ToStringVisitor());
    table_.link_set_.put(class_name, parent_name);
    table_.classes_.put(class_name, new ClassSymbolTable());

    Vector<String> n_depth = new Vector<>(depth);
    n_depth.add(class_name);
    n.f5.accept(this, n_depth);
    n.f6.accept(this, n_depth);
  }

  public void visit(MethodDeclaration n, Vector<String> depth) {
    final String method_name = n.accept(new ToStringVisitor());

    MethodSymbolTable method = new MethodSymbolTable();
    method.return_type_ = n.f1.accept(new ToStringVisitor());

    table_.classes_.get(depth.get(0)).methods_.put(method_name,
                                                   method);

    Vector<String> n_depth = new Vector<>(depth);
    n_depth.add(method_name);
    n.f4.accept(this, n_depth);
    n.f7.accept(this, n_depth);
  }

  public void visit(FormalParameter n, Vector<String> depth) {
    MethodSymbolTable method_table = table_.classes_.get(depth.get(0))
                                           .methods_.get(depth.get(1));
    final String arg_name = n.f1.f0.toString();
    method_table.arg_order_.add(arg_name);
    method_table.arguments_.put(arg_name,
                                n.f0.accept(new ToStringVisitor()));
  }
}
