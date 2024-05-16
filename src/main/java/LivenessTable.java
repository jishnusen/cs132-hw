import java.util.*;

class Interval {
  public int start;
  public int end;
  public String id = null;

  Interval(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public boolean contains(int i) {
    return start <= i && i <= end;
  }

  public void print() {
    System.out.println(
        "(" + Integer.toString(start) +
        ", " + Integer.toString(end) +
        "," + id +
        ")");
  }
}

class ByStart implements Comparator<Interval> {
  @Override
  public int compare(Interval a, Interval b) {
    return Integer.compare(a.start, b.start);
  }
}

class ByEnd implements Comparator<Interval> {
  @Override
  public int compare(Interval a, Interval b) {
    return Integer.compare(a.end, b.end);
  }
}

public class LivenessTable {
  Map<String, List<Interval>> liveness = new HashMap<>();
  SortedSet<String> all_registers = all_registers();

  static SortedSet<String> all_registers() {
    SortedSet<String> res = new TreeSet<>();

    for (int i = 2; i < 8; i++) {
      res.add("a" + Integer.toString(i));
    }
    for (int i = 1; i < 12; i++) {
      res.add("s" + Integer.toString(i));
    }
    for (int i = 3; i < 5; i++) {
      res.add("t" + Integer.toString(i));
    }

    return res;
  }

  public void assign_LSRA() {
    SortedSet<String> free = all_registers();

    List<Interval> intervals = new ArrayList<>();
    liveness.values().forEach(intervals::addAll);
    Collections.sort(intervals, new ByStart());

    List<Interval> active = new ArrayList<>();

    for (Interval i : intervals) {
      Collections.sort(active, new ByEnd());

      // ExpireOldIntervals(i)
      List<Interval> to_remove = new ArrayList<>();
      for (Interval j : active) {
        if (j.end >= i.start) {
          break;
        }
        to_remove.add(j);
        free.add(j.id);
      }
      active.removeAll(to_remove);

      if (free.isEmpty()) {
        // SpillAtInterval(i)
        Interval spill = active.get(active.size() - 1);
        if (spill.end > i.end) {
          i.id = spill.id;
          spill.id = IdGenerator.gen_id();
          active.remove(spill);
          active.add(i);
        } else {
          i.id = IdGenerator.gen_id();
        }
      } else {
        i.id = free.iterator().next();
        free.remove(i.id);
        active.add(i);
      }
    }
  }

  public String lookup(String var, int idx) {
    for (Interval i : liveness.get(var)) {
      if (i.contains(idx)) {
        return i.id;
      }
    }
    throw new RuntimeException("no register assigned for " +
        var + " @ " + Integer.toString(idx));
  }

  public List<String> alive_reg(int idx) {
    List<String> res = new ArrayList<>();

    List<Interval> intervals = new ArrayList<>();
    liveness.values().forEach(intervals::addAll);
    for (Interval i : intervals) {
      if (i.contains(idx) && all_registers.contains(i.id)) {
        res.add(i.id);
      }
    }
    return res;
  }

  public List<String> clobbered() {
    List<String> res = new ArrayList<>();

    List<Interval> intervals = new ArrayList<>();
    liveness.values().forEach(intervals::addAll);
    for (Interval i : intervals) {
      if (all_registers.contains(i.id)) {
        res.add(i.id);
      }
    }
    return res;
  }

  public void print() {
    for (String k : liveness.keySet()) {
      System.out.println(k + ":");
      for (Interval iv : liveness.get(k)) {
        iv.print();
      }
    }
  }
}
