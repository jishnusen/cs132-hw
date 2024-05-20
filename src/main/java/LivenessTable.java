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

  public boolean overlaps(Interval other) {
    return start <= other.end && other.start <= end;
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
  Map<String, Interval> liveness = new HashMap<>();
  SortedSet<String> all_registers = all_registers();

  static SortedSet<String> all_registers() {
    SortedSet<String> res = new TreeSet<>();

    for (int i = 1; i < 12; i++) {
      res.add("s" + Integer.toString(i));
    }
    for (int i = 2; i < 5; i++) {
      res.add("t" + Integer.toString(i));
    }

    return res;
  }

  public void assign_LSRA() {
    SortedSet<String> free = all_registers();

    List<Interval> intervals = new ArrayList<>(liveness.values());
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
    Interval iv = liveness.get(var);
    if (iv.contains(idx)) {
      return iv.id;
    }
    throw new RuntimeException("no register assigned for " +
        var + " @ " + Integer.toString(idx));
  }

  public List<String> alive(int idx) {
    List<String> res = new ArrayList<>();

    for (String id : liveness.keySet()) {
      if (liveness.get(id).contains(idx)) {
        res.add(id);
      }
    }
    return res;
  }

  public List<String> alive_reg(int idx) {
    List<String> res = new ArrayList<>();

    for (String v : alive(idx)) {
      String id = liveness.get(v).id;
      if (all_registers.contains(id)) {
        res.add(id);
      }
    }
    return res;
  }

  public void print() {
    for (String k : liveness.keySet()) {
      System.out.println(k + ":");
      liveness.get(k).print();
    }
  }
}
