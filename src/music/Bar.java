package music;

import java.awt.Color;
import java.awt.Graphics;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Bar extends Mass {

  private static final int FAT = 2, RIGHT = 4, LEFT = 8;
  //represents bytes.
  //FAT which we AND to check for fat bar.
  //RIGHT for checking repeact on right, left for left side.

  public Sys sys;
  public int x, barType = 0;

  public Bar(Sys sys, int x) {
    super("NOTE");
    this.sys = sys;
    this.x = x;

    addReaction(new Reaction("S-S") { //Cycles between the bar types.
      public int bid(Gesture gest) {
        int x = gest.vs.xM();
        if (Math.abs(x - Bar.this.x) > UC.barToMarginSnap) {
          return UC.noBid;
        }
        int y1 = gest.vs.yL(), y2 = gest.vs.yH();
        if (y1 < Bar.this.sys.yTop() - 20) {
          return UC.noBid;
        }
        if (y2 > Bar.this.sys.yBot() + 20) {
          return UC.noBid;
        }
        return Math.abs(x - Bar.this.x);
      }

      public void act(Gesture gest) {
        Bar.this.cycleType();
      }
    });

    addReaction(new Reaction("DOT") {     //DOT reaction.
      public int bid(Gesture gest) {
        int x = gest.vs.xM(), y = gest.vs.yM();
        if(y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) {return UC.noBid;}
        int dist = Math.abs(x - Bar.this.x);
        if(dist > 3*UC.defaultStaffSpace) {return UC.noBid;}
        return dist;
      }
      public void act(Gesture gest) {
        if(gest.vs.xM() < Bar.this.x) {Bar.this.toggleLeft();} else{Bar.this.toggleLRight();}
      }
    });
  }

  public void cycleType() {
    barType++;
    if (barType > 2) {
      barType = 0;
    }
  }

  public void toggleLeft() {
    barType = barType ^ LEFT; // ^ bit wise exclusive OR.
  }

  public void toggleLRight() {
    barType = barType ^ RIGHT; // ^ bit wise exclusive OR.
  }

  public void show(Graphics g) {
    int sysTop = sys.yTop(), N = sys.fmt.size(), y1 = 0, y2 = 0;  //y1, y2 is top and bottom of connected component.
    boolean justSawBreak = true;
    for (int i = 0; i < N; i++) {
      Staff.Fmt sf = sys.fmt.get(i);
      int staffTop = sysTop + sys.fmt.staffOffSet.get(i);
      if(justSawBreak) {y1 = staffTop;}
      y2 = staffTop + sf.height();
      if (!sf.barContinues){drawLines(g, x, y1, y2);}
      justSawBreak = !sf.barContinues;
      if(barType > 3) {drawDots(g, x, staffTop);}
    }
  }

  public void drawLines(Graphics g, int x, int y1, int y2) {
    int H = UC.defaultStaffSpace;
    if(barType == 0) {thinBar(g, x, y1, y2);}
    if(barType == 1) {thinBar(g, x, y1, y2); thinBar(g, x-H, y1, y2);}
    if(barType == 2) {fatBar(g, x-H, y1, y2, H);thinBar(g, x-2*H, y1, y2);}
    if(barType >= 4) {
      fatBar(g, x-H, y1, y2, H);  //All repeats fatBars.
      if((barType & LEFT) != 0) {thinBar(g, x-2*H, y1, y2); wings(g, x-2*H, y1, y2, -H, H);}
      if((barType & RIGHT) != 0) {thinBar(g, x+H, y1, y2); wings(g, x+H, y1, y2, H, H);}
    }
  }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
    g.drawLine(x, y1, x + dx, y1 - dy);
    g.drawLine(x, y2, x + dx, y2 + dy);
  }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {
    g.fillRect(x, y1, dx, y2 - y1);
  }

  public static void thinBar(Graphics g, int x, int y1, int y2) {
    g.drawLine(x, y1, x, y2);
  }

  public void drawDots(Graphics g, int x, int top) {
    int H = UC.defaultStaffSpace;
    if (((barType & LEFT) != 0)) {
      g.fillOval(x - 3 * H, top + (11 * H) / 4, H / 2, H / 2);
      g.fillOval(x - 3 * H, top + (19 * H) / 4, H / 2, H / 2);
    }
    if (((barType & RIGHT) != 0)) {
      g.fillOval(x +  H + H/2, top + (11 * H) / 4, H / 2, H / 2);
      g.fillOval(x +  H + H/2, top + (19 * H) / 4, H / 2, H / 2);
    }
  }
}
