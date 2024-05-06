import java.util.*;

import minijava.visitor.GJVoidDepthFirst;
import minijava.syntaxtree.*;

import sparrow.*;
import IR.token.FunctionName;

public class TranslateVisitor extends GJVoidDepthFirst<Vector<String>> {
  GoalSymbolTable symbol_table_;
  Program program_ = new Program();

  public TranslateVisitor(GoalSymbolTable symbol_table) {
    super();
    symbol_table_ = symbol_table;
    program_.funDecls = new ArrayList<FunctionDecl>();
  }

  public void visit(Goal n, Vector<String> depth) {
    // Main Class
    n.f0.accept(this, depth);
    // Classes
    n.f1.accept(this, depth);
  }

  public void visit(MainClass n, Vector<String> depth) {
    final String main_class = n.accept(new ToStringVisitor());

    Vector<String> s_depth = new Vector<>(depth);
    s_depth.add(main_class);
    s_depth.add("main");

    generate_method(n.f15, s_depth, null);
  }

  public void visit(ClassDeclaration n, Vector<String> depth) {
    String class_name = n.accept(new ToStringVisitor());
    generate_constructor(class_name);

    Vector<String> m_depth = new Vector<>(depth);
    m_depth.add(class_name);
    n.f4.accept(this, m_depth);
  }

  public void visit(ClassExtendsDeclaration n, Vector<String> depth) {
    String class_name = n.accept(new ToStringVisitor());
    generate_constructor(class_name);

    Vector<String> m_depth = new Vector<>(depth);
    m_depth.add(class_name);
    n.f6.accept(this, m_depth);
  }

  public void visit(MethodDeclaration n, Vector<String> depth) {
    String method_name = n.accept(new ToStringVisitor());
    Vector<String> m_depth = new Vector<>(depth);
    m_depth.add(method_name);
    generate_method(n.f8, m_depth, n.f10);
  }

  void generate_method(NodeListOptional statements, Vector<String> depth, Expression ret) {
    String class_name = depth.get(0);
    String method_name = depth.get(1);
    MethodSymbolTable method_table = symbol_table_.methods(class_name).get(method_name);

    List<IR.token.Identifier> params = new ArrayList<IR.token.Identifier>();
    IR.token.Identifier this_id = new IR.token.Identifier("this");
    List<Instruction> ins = new ArrayList<Instruction>();
    HashMap<String, IR.token.Identifier> identifiers = new HashMap<>();

    FunctionName method;
    if (class_name.equals(symbol_table_.main_class_)) {
      method = new FunctionName("Main");
    } else {
      method = new FunctionName(class_name + "__" + method_name);
      params.add(this_id);

      for (String arg : method_table.arg_order_) {
        IR.token.Identifier arg_id = IdGenerator.gen_id();
        identifiers.put(arg, arg_id);
        params.add(arg_id);
      }
    }

    for (String decl : method_table.declarations_.keySet()) {
      IR.token.Identifier decl_id = IdGenerator.gen_id();
      identifiers.put(decl, decl_id);
      ins.add(new Move_Id_Integer(decl_id, 0));
    }

    StatementTranslator s_translator = new StatementTranslator(symbol_table_, depth, identifiers, ins);
    statements.accept(s_translator);

    IR.token.Identifier ret_reg;
    if (ret != null) {
      ret_reg = ret.accept(s_translator.translator_);
    } else {
      ret_reg = IdGenerator.gen_id();
      ins.add(new Move_Id_Integer(ret_reg, 0));
    }

    ins.add(new Goto(IdGenerator.end_label(depth)));

    ins.add(new LabelInstr(IdGenerator.null_except(depth)));
    ins.add(new ErrorMessage("\"null pointer\""));

    ins.add(new LabelInstr(IdGenerator.bad_array(depth)));
    ins.add(new ErrorMessage("\"array index out of bounds\""));

    ins.add(new LabelInstr(IdGenerator.end_label(depth)));

    program_.funDecls.add(new FunctionDecl(method, params, new sparrow.Block(ins, ret_reg)));
  }

  void generate_constructor(String c) {
    IndexedMap<String, MethodSymbolTable> all_methods = symbol_table_.methods(c);

    FunctionName cname = new FunctionName(c + "__");
    List<IR.token.Identifier> params = new ArrayList<IR.token.Identifier>();
    List<Instruction> ins = new ArrayList<Instruction>();

    IR.token.Identifier field_size = IdGenerator.gen_id();
    IR.token.Identifier fields = IdGenerator.gen_id();
    ins.add(new Move_Id_Integer(field_size, 4 * symbol_table_.num_fields(c)));
    ins.add(new Alloc(fields, field_size));

    IR.token.Identifier method_size = IdGenerator.gen_id();
    IR.token.Identifier methods = IdGenerator.gen_id();
    ins.add(new Move_Id_Integer(method_size, 4 * all_methods.size()));
    ins.add(new Alloc(methods, method_size));
    for (int i = 0; i < all_methods.insertionOrder.size(); i++) {
      String method = all_methods.insertionOrder.get(i);
      IR.token.Identifier method_name = IdGenerator.gen_id();
      String implementor = symbol_table_.implementor(c, method);
      ins.add(new Move_Id_FuncName(method_name, new FunctionName(implementor + "__" + method)));
      ins.add(new Store(methods, 4 * i, method_name));
    }

    IR.token.Identifier eight = IdGenerator.gen_id();
    IR.token.Identifier ret = IdGenerator.gen_id();
    ins.add(new Move_Id_Integer(eight, 8));
    ins.add(new Alloc(ret, eight));
    ins.add(new Store(ret, 0, fields));
    ins.add(new Store(ret, 4, methods));

    program_.funDecls.add(new FunctionDecl(cname, params, new sparrow.Block(ins, ret)));
  }
}
