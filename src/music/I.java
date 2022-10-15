package music;

import graphics.G;

import java.awt.*;
import reactions.Gesture;


/*Interface is nothing but abstract function*/
public interface I {


    public interface Show {public void show(Graphics g);}
    public interface Draw{public void draw(Graphics g);/*this is a signature  A abstract function*/}
    public interface Hit{public boolean hit(int x, int y);}
    public interface Act{public void act(Gesture gest);}
    public interface React extends Act{public int bid(Gesture gest);}

    public interface Area extends Hit{
        public void dn(int x, int y);
        public void drag(int x, int y);
        public void up(int x,int y);

    }


}
