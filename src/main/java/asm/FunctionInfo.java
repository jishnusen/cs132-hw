package asm;

import java.util.*;

import IR.token.Identifier;
import sparrowv.*;

public class FunctionInfo {
  public Map<String, Integer> stackFrame = new HashMap<>();
  int num_params;

  public FunctionInfo(FunctionDecl fn) {
    this.num_params = fn.formalParameters.size();
  }

  public void addId(Identifier id) {
    String stack_name = id.toString();
    if (stackFrame.containsKey(stack_name)) return;

    stackFrame.put(stack_name, stackFrame.size());
  }

  public int getOffset(Identifier v) {
    // stack addr 0                      n
    //            [ra] [temp ids] [params]
    // index      n                      0
    int maxIndex = stackFrame.size();
    int index = stackFrame.get(v.toString());
    return (maxIndex - index) * 4;
  }

  public int getFrameSize() {
    int num = 1 + stackFrame.size() - num_params;
    return num * 4;
  }
}
