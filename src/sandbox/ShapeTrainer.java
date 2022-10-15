package sandbox;

import graphics.G;
import graphics.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import music.UC;
import reactions.Ink;
import reactions.Shape;
import reactions.Shape.Prototype;

public class ShapeTrainer extends Window {

  public static void main(String[] args) {
    (Window.PANEL = new ShapeTrainer()).launch();
  }
  public ShapeTrainer() {
    super("ShaeTrainer", UC.initialWindowWidth, UC.initialWindowWidth);
  }

  public static String UNKNOWN = " <- this Name is unknown";
  public static String ILLEGAL = " <- this Name is not legal Shape Name";
  public static String KNOWN = " <- this is Known Shape";

  public static String currName = "";
  public static String currState = ILLEGAL;
  public static Shape.Prototype.List pList = new Shape.Prototype.List();

  public void setState() {
    currState = (currName.equals("") || currName.equals("DOT")) ? ILLEGAL : UNKNOWN;
    if (currState == UNKNOWN) {
      if (Shape.DB.containsKey(currName)) {
        currState = KNOWN;
        pList = Shape.DB.get(currName).prototypes;
      } else {
        pList = null;
      }
    }
  }

  public void paintComponent(Graphics g) {
    G.clear(g);
    g.setColor(Color.black);
    g.drawString(currName, 600, 30);
    g.drawString(currState, 700, 30);
    g.setColor(Color.red);
    Ink.BUFFER.show(g);
    if (pList != null) {
      pList.show(g);
    }
  }

  public void mousePressed(MouseEvent me) {
    Ink.BUFFER.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    Ink.BUFFER.drag(me.getX(), me.getY());
    repaint();
  }

//  public void mouseReleased(MouseEvent me) {
//    if (currState != ILLEGAL) {
//      Ink ink = new Ink();
//      Shape.Prototype proto;
//
//      if (pList == null) {
//        Shape s = new Shape(currName);
//        Shape.DB.put(currName, s);
//        pList = s.prototypes;
//      }
//
//      if (pList.bestDist(ink.norm) < UC.noMatchDist) {
//        proto = Prototype.List.bestMatch;
//        proto.blend(ink.norm);
//      } else {
//        proto = new Shape.Prototype();
//        pList.add(proto);
//      }
//    }
//    repaint();
//  }

  public void mouseReleased(MouseEvent me) {
    Ink ink = new Ink();
    Shape.DB.train(currName, ink);
    setState();
    repaint();
  }

  public void keyTyped(KeyEvent key) {
    char c = key.getKeyChar();
    System.out.println("Type: " + c);
    currName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : currName + c;
    if (c == ' ' || c == 0x0D || c == 0x0A) {
      Shape.saveShapeDB();
    }
    setState();
    repaint();
  }

}
