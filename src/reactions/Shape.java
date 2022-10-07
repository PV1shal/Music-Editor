package reactions;

import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import music.UC;

public class Shape implements Serializable {

  public String name;
  public Prototype.List prototypes = new Prototype.List();
  public static DataBase DB = DataBase.load();
  public static Shape DOT = DB.get("DOT");
  public static Collection<Shape> LIST = DB.values();

  public static void saveShapeDB() {
    DataBase.save();
  }

  public static Shape recognize(Ink ink) {    //Can return Null
    if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold) {
      return DOT;
    }
    Shape bestMatch = null;
    int bestSoFar = UC.noMatchDist;
    for (Shape s : LIST) {
      int d = s.prototypes.bestDist(ink.norm);
      if (d < bestSoFar) {
        bestMatch = s;
        bestSoFar = d;
      }
    }
    return bestMatch;
  }

  public Shape(String name) {
    this.name = name;
  }

  //----------------------------------- Prototype ---------------------------------------------//

  public static class Prototype extends Ink.Norm implements Serializable {

    public int nBlend = 1;

    public void blend(Ink.Norm norm) {
      blend(norm, nBlend);
      nBlend++;
    }

    //---------------------------------- List ---------------------------------------------------//
    public static class List extends ArrayList<Prototype> implements Serializable {

      public static Prototype bestMatch;  //Set by Side effect of bestDist

      private static int m = 10, w = 60;
      private static G.VS showBox = new G.VS(m, m, w, w);

      public int bestDist(Ink.Norm norm) {
        bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for (Prototype p : this) {
          int d = p.dist(norm);
          if (d < bestSoFar) {
            bestMatch = p;
            bestSoFar = d;
          }
        }
        return bestSoFar;
      }


      public void train(Ink.Norm norm) {
        if (bestDist(norm) < UC.noMatchDist) {
          bestMatch.blend(norm);
        } else {
          add(new Shape.Prototype());
        }
      }

      public void show(Graphics g) {
        g.setColor(Color.blue);
        for (int i = 0; i < size(); i++) {
          Prototype p = get(i);
          int x = m + i * (m + w);
          showBox.loc.set(x, m);
          p.drawAt(g, showBox);
          g.drawString("" + p.nBlend, x, 20);
        }
      }
    }
  }

  public static class DataBase extends HashMap<String, Shape> {

    private DataBase() {
      super();
      addNewShape("DOT");
    }

    public void addNewShape(String name) {
      put(name, new Shape(name));
    }

    public Shape forceGet(String name) {
      if (!DB.containsKey(name)) {
        addNewShape(name);
      }
      return DB.get(name);
    }

    public void train(String name, Ink.Norm norm) {
      if (isLegal(name)) {
        forceGet(name).prototypes.train(norm);
      }
    }

    public static boolean isLegal(String name) {
      return !name.equals("") && !name.equals("DOT");
    }

    public static DataBase load() {
      String fileName = UC.shapeDBFileName;
      DataBase res;
      try {
        System.out.println("Attempting DB load....");
        ObjectInputStream OIS = new ObjectInputStream(new FileInputStream(fileName));
        res = (DataBase) OIS.readObject();
        System.out.println("Successful load - Found" + res.keySet());
        OIS.close();
      } catch (Exception e) {
        System.out.println("Load Failed");
        System.out.println(e);
        res = new DataBase();
      }
      return res;
    }

    public static void save() {
      String fileName = UC.shapeDBFileName;
      try {
        System.out.println("Saving DB...");
        ObjectOutputStream OOS = new ObjectOutputStream(new FileOutputStream(fileName));
        OOS.writeObject(DB);
        System.out.println("Saved " + fileName);
        OOS.close();
      } catch (Exception e) {
        System.out.println("Failed DB Save");
        System.out.println(e);
        throw new RuntimeException(e);
      }
    }

  }
}
