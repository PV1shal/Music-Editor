package music;

import static sandbox.Music.PAGE;

import java.awt.Graphics;
import java.util.ArrayList;
import reactions.Mass;

public class Sys extends Mass {

  public ArrayList<Staff> staffs = new ArrayList<>();
  public Page page;
  public int iSys;
  public Sys.Fmt fmt;

  public Sys(Page page, int iSys, Sys.Fmt fmt) {
    super("BACK");
    this.page = page;
    this.iSys = iSys;
    this.fmt = fmt;

    for (int i = 0; i < fmt.size(); i++) {
      addStaff(new Staff(this, i, fmt.get(i)));
    }
  }

  private void addStaff(Staff staff) {
    staffs.add(staff);
  }

  public int yTop() {
    return page.sysTop(iSys);
  }

  public int yBot() {
    return staffs.get(staffs.size()-1).yBot();
  }

  public void show(Graphics g) {
    int y = yTop(), x = PAGE.margins.left;
    g.drawLine(x, y, x, y + fmt.height());
  }

  //--------------------------------------- Fmt ------------------------------------------------//
  public static class Fmt extends ArrayList<Staff.Fmt> {

    public ArrayList<Integer> staffOffSet = new ArrayList<>();

    public int height() {
      int last = size() - 1;
      return staffOffSet.get(last) + get(last).height();
    }

    public void showAt(Graphics g, int y) {
      for (int i = 0; i < size(); i++) {
        get(i).showAt(g, y + staffOffSet.get(i));
      }
    }
  }
}
