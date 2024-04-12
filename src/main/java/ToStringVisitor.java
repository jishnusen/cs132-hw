import minijava.visitor.GJNoArguDepthFirst;
import minijava.syntaxtree.*;

public class ToStringVisitor extends GJNoArguDepthFirst<String> {
  public String visit(MainClass n) {
    return n.f1.f0.toString();
  }

  public String visit(TypeDeclaration n) {
    return n.f0.choice.accept(this);
  }

  public String visit(ClassDeclaration n) {
    return n.f1.accept(this);
  }

  public String visit(ClassExtendsDeclaration n) {
    return n.f1.accept(this);
  }

  public String visit(VarDeclaration n) {
    return n.f1.accept(this);
  }

  public String visit(MethodDeclaration n) {
    return n.f2.accept(this);
  }

  public String visit(Type n) {
    return n.f0.choice.accept(this);
  }

  public String visit(ArrayType n) {
    return n.f0.toString() + n.f1.toString() + n.f2.toString();
  }

  public String visit(BooleanType n) {
    return n.f0.toString();
  }

  public String visit(IntegerType n) {
    return n.f0.toString();
  }

  public String visit(Identifier n) {
    return n.f0.toString();
  }

  public String visit(FormalParameter n) {
    return n.f1.accept(this);
  }

  public String visit(FormalParameterRest n) {
    return n.f1.accept(this);
  }
}
