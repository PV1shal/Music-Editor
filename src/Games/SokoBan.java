package Games;

import graphics.G;
import graphics.Window;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SokoBan extends Window {

    public static Board board = new Board();
    public static G.V LEFT = new G.V(-1, 0), RIGHT = new G.V(1, 0);
    public static G.V UP = new G.V(0, -1), DOWN = new G.V(0, 1);

    public SokoBan() {
        super("SokoBan", 1000, 700);
    }

    public static void main(String[] args) {
        (PANEL = new SokoBan()).launch();
//        board.loadStringArray(puz1);
        board.loadStringArray(puz2);
    }

    public void paintComponent(Graphics g) {
        G.clear(g);
        board.show(g);
        if(board.done()) {
            g.setColor(Color.black);
            g.drawString("You Win!!", 20, 30);
        }
    }

    public void keyPressed(KeyEvent ke) {
        int vk = ke.getKeyCode();
        if (vk == KeyEvent.VK_LEFT) {
            board.go(LEFT);
        }
        if (vk == KeyEvent.VK_RIGHT) {
            board.go(RIGHT);
        }
        if (vk == KeyEvent.VK_UP) {
            board.go(UP);
        }
        if (vk == KeyEvent.VK_DOWN) {
            board.go(DOWN);
        }
        if (vk == KeyEvent.VK_SPACE) {
            board.clear();
//            board.loadStringArray(puz1);
            board.loadStringArray(puz2);
        }
        repaint();
    }

    // --------------------- Board -----------------------//

    public static class Board {
        public static final int N = 25;
        public static String boardStates = " WPCGgE";    // All the states in the game
        public static Color[] colors = {Color.white, Color.darkGray, Color.green, Color.orange,
                Color.cyan, Color.blue, Color.red};
        public static final int xM = 50, yM = 50, W = 40;

        public char[][] b = new char[N][N];
        public G.V player = new G.V(0, 0);
        public boolean onGoal = false;
        public G.V dest = new G.V(0, 0);

        public Board() {
            clear();
        }

        public void clear() {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    b[r][c] = ' ';
                }
            }
            player.set(0, 0);
            onGoal = false;
        }

        public void show(Graphics g) {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    int ndx = boardStates.indexOf(b[r][c]);
                    g.setColor(colors[ndx]);
                    g.fillRect(xM + c * W, yM + r * W, W, W);
                }
            }
        }

        public char ch(G.V v) {
            return b[v.y][v.x];
        }

        public void set(G.V v, char c) {
            b[v.y][v.x] = c;
        }

        public void movePerson() {
            boolean res = ch(dest) == 'G';       // Goal State
            set(player, onGoal ? 'G' : ' ');    // Set value for space player
            set(dest, 'P');                     // Set Player
            player.set(dest);
            onGoal = res;
        }

        public void go(G.V v) {
            dest.set(player);
            dest.add(v);    //actual destination
            if (ch(dest) == 'W' || ch(dest) == 'E') {       //Blocked from walking into this direction.
                return;
            }
            if (ch(dest) == ' ' || ch(dest) == 'G') {       //Blocked from walking into this direction.
                movePerson();
                return;
            }
            if (ch(dest) == 'C' || ch(dest) == 'g') {       //Blocked from walking into this direction.
                dest.add(v);
                if (ch(dest) != ' ' && ch(dest) != 'G') {
                    return;
                }
                set(dest, ch(dest) == 'G' ? 'g' : 'C');     // Put box in final spot.
                dest.set(player);
                dest.add(v);
                set(dest, ch(dest) == 'g' ? 'G' : ' ');     // Sets the space where the box left/
                movePerson();
            }
        }

        public void loadStringArray(String[] a) {
            for (int r = 0; r < a.length; r++) {
                String s = a[r];
                for (int c = 0; c < s.length(); c++) {
                    char ch = s.charAt(c);
                    b[r][c] = (boardStates.indexOf(ch) > -1) ? ch : 'E';
                    if (ch == 'P') {
                        player.x = c;
                        player.y = r;
                    }
                }
            }
        }

        public boolean done() {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    if(b[r][c] == 'C') {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static String[] puz1 = {
            "  WWWWW",
            "WWW   W",
            "WGPC  W",
            "WWW CGW",
            "WGWWC W",
            "W W G ww",
            "WC gCCGW",
            "W   G  W",
            "WWWWWWWW"
    };

    public static String[] puz2 = {
            "WWWWWWWWWWWW",
            "WGG  W     WWW",
            "WGG  W C  C  W",
            "WGG  WCWWWW  W",
            "WGG    P WW  W",
            "WGG  W W  C WW",
            "WWWWWW WWC C W",
            "  W C  C C C W",
            "  W    W     W",
            "  WWWWWWWWWWWW"
    };
}
