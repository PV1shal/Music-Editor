package music;

import static sandbox.Music.PAGE;

import java.awt.Graphics;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

public class Staff extends Mass {

  public Sys sys;
  public int iStaff;
  public Staff.Fmt fmt;

  public Staff(Sys sys, int iStaff, Staff.Fmt fmt) {
    super("BACK");
    this.sys = sys;
    this.iStaff = iStaff;
    this.fmt = fmt;

    addReaction(new Reaction("S-S") {   //Bar Line.
      public int bid(Gesture gesture) {
        int x = gesture.vs.xM(), y1 = gesture.vs.yM(), y2 = gesture.vs.yH();
//        y1 is where we start the stroke and
//        y2 is where we end the stroke.
        if (x < PAGE.margins.left && x > PAGE.margins.right + UC.barToMarginSnap) {
          return UC.noBid;
        }
//        System.out.println("Top" + y1 + " " + Staff.this.yTop());
        int d = Math.abs((y1 - Staff.this.yTop())) + Math.abs(y2 - Staff.this.yBot());
        System.out.println("D: " + d);
        return (d < 50) ? d + UC.barToMarginSnap : UC.noBid;
      }

      public void act(Gesture gesture) {
        int x = gesture.vs.xM();
        if (Math.abs(x - PAGE.margins.right) < UC.barToMarginSnap) {
          x = PAGE.margins.right;
        }
        new Bar(Staff.this.sys, x);
      }
    });

    addReaction(new Reaction("S-S") {     //This is a set Bar continues.22

      public int bid(Gesture gest) {
        if (Staff.this.sys.iSys != 0) {return UC.noBid;}  //Only change bar continues in first sys.
        int y1 = gest.vs.yL(), y2 = gest.vs.yH(), iStaff = Staff.this.iStaff;
        if (iStaff == PAGE.sysFmt.size() - 1) {return UC.noBid;}  //Last staff can't continue.
        if (Math.abs(y1-Staff.this.yBot()) > 20) {return UC.noBid;}
        Staff nextStaff = Staff.this.sys.staffs.get(iStaff+1);
        if(Math.abs(y2-nextStaff.yTop()) > 20) {return UC.noBid;}
        return 10;
      }

      public void act(Gesture gest) {
        Staff.this.fmt.toggleBarContinues();
      }
    });

    addReaction(new Reaction("SW-SW") {         //Adding Quarter note
      public int bid(Gesture gest) {
        int x = gest.vs.xM(), y = gest.vs.yM();
        if(x < PAGE.margins.left || x > PAGE.margins.right) {return UC.noBid;}
        int H = Staff.this.fmt.H, top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
        if(y < top || y > bot) {return UC.noBid;}
        return 10;
      }
      public void act(Gesture gest) {
        new Head(Staff.this, gest.vs.xM(), gest.vs.yM());
      }
    });
  }

  public int yTop() {
    return sys.yTop() + sysOff();
  }

  public int yBot() {
    return yTop() + fmt.height();
  }

  private int sysOff() {
    return sys.fmt.staffOffSet.get(iStaff);
  }


  //--------------------------------------- FMT -------------------------------------------------//
  public static class Fmt {

    public int nLines = 5;
    public int H = UC.defaultStaffSpace;                     //Height i.e. halfway distance between two lines.
    public boolean barContinues = false;

    public int height() {
      return 2 * H * (nLines - 1);
    }

    public void toggleBarContinues() {
      barContinues = !barContinues;
    }

    public void showAt(Graphics g, int y) {
      int LEFT = PAGE.margins.left, RIGHT = PAGE.margins.right;

      for (int i = 0; i < nLines; i++) {
        g.drawLine(LEFT, y + 2 * H * i, RIGHT, y + 2 * H * i);
      }
    }
  }
}
