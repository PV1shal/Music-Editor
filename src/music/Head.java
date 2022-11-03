package music;

import java.awt.Graphics;
import reactions.Mass;

public class Head extends Mass {

  public Staff staff;
  public int x, line;

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    this.x = x;
    int H = staff.fmt.H;
    int top = staff.yTop() - H;
    line = ((y - top + H/2) / H) - 1;
    System.out.println("line: "+line);
  }
  public void show(Graphics g) {
    int H = staff.fmt.H;
    Glyph.HEAD_Q.showAt(g, H, x, staff.yTop() + line*H);
  }
}
