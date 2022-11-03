package reactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import music.I;
import music.UC;

public abstract class Reaction implements I.React {

  public Shape shape;
  private static Map byShape = new Map();
  public static List initialReactions = new List();

  public Reaction(String shapeName) {
    shape = Shape.DB.get(shapeName);
    if (shape == null) {
      System.out.println("WTF - shapeDB doesn't know " + shapeName);
    }
  }

  public static void nuke() {
    byShape = new Map();
    initialReactions.enable();
  }

  public void enable() {
    List list = byShape.getList(shape);
    if (!list.contains(this)) {
      list.add(this);
    }
  }

  public void disable() {
    List list = byShape.getList(shape);
    list.remove(this);
  }

  public static Reaction best(Gesture g) {
    return byShape.getList(g.shape).loBid(g);
  }

  //------------------------------------------ List ---------------------------------------------//
  public static class List extends ArrayList<Reaction> {

    public void addReaction(Reaction r) {
      add(r);
      r.enable();
    }

    public void removeReaction(Reaction r) {
      remove(r);
      r.disable();
    }

    public void clearAll() {
      for (Reaction r : this) {
        r.disable();
      }
      this.clear();
    }

    public Reaction loBid(Gesture g) {    //Can return null
      Reaction res = null;
      int bestSoFar = UC.noBid;
      for (Reaction r : this) {
        int b = r.bid(g);
        if (b < bestSoFar) {
          bestSoFar = b;
          res = r;
        }
      }
      return res;
    }

    public void enable() {
      for (Reaction r : this) {
        r.enable();
      }
    }
  }

  //------------------------------------------ Map ---------------------------------------------//
  public static class Map extends HashMap<Shape, List> {

    public List getList(Shape s) {    //Always succeeds.
      List res = get(s);
      if (res == null) {
        res = new List();
        put(s, res);
      }
      return res;
    }
  }
}
