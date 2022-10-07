package sandbox;

import graphics.G;
import graphics.Window;
import music.UC;
import reactions.Ink;

import java.awt.*;
import java.awt.event.MouseEvent;
import reactions.Shape;
import reactions.Shape.Prototype;

public class PaintInk extends Window {

  public static String recognised = "";

  public PaintInk() {
    super("PaintInk", UC.initialWindowWidth, UC.initialWindowHeight);
  }

  public static Shape.Prototype.List pList = new Prototype.List();
  public static Ink.List inkList = new Ink.List();

  @Override
  public void paintComponent(Graphics g) {
    G.clear(g);

    inkList.show(g);

    g.setColor(Color.red);

    Ink.BUFFER.show(g);

    if (inkList.size() > 1) {
      int last = inkList.size() - 1;
      int dist = inkList.get(last).norm.dist(inkList.get(last - 1).norm);
      g.setColor((dist < UC.noMatchDist ? Color.green : Color.red));
      g.drawString("dist: " + dist, 600, 60);
    }
    g.drawString("points: " + Ink.BUFFER.n, 600, 30);
    pList.show(g);
    g.drawString(recognised, 700, 40);
  }

  public void mousePressed(MouseEvent me) {
    Ink.BUFFER.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    Ink.BUFFER.drag(me.getX(), me.getY());
    repaint();
  }

  public void mouseReleased(MouseEvent me) {
    Ink ink = new Ink();
    Shape s = Shape.recognize(ink);
    recognised = "recognised: " + ((s != null) ? s.name : "unrecognised");
    Shape.Prototype proto;
    inkList.add(ink);
    if (pList.bestDist(ink.norm) < UC.noMatchDist) {
      proto = Prototype.List.bestMatch;
      proto.blend(ink.norm);
    } else {
      proto = new Shape.Prototype();
      pList.add(proto);
    }
    ink.norm = proto;
    repaint();
  }

}
