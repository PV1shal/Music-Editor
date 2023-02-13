package Games;

import graphics.G;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import graphics.Window;

import javax.swing.*;

public class Tetris extends Window implements ActionListener {
    public static Timer timer;
    public static final int H = 20, W = 10, C = 25;
    public static final int xM = 50, yM = 50;
    public static Color[] color = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.BLACK, Color.PINK};
    public static Shape[] shapes = {Shape.Z, Shape.S, Shape.J, Shape.L, Shape.I, Shape.O, Shape.T};
    public static int time = 1, iShape = 0;
    public static int[][] well = new int[W][H];
    public static Shape shape = null;
    public static int iBack = 7;
//    public static Shape nextShape;
    public static int zap = 8;

    public Tetris() {
        super("Tetris", 1000, 700);

        shape = shapes[G.rnd(7)];
//        nextShape = shapes[G.rnd(7)];
        clearWell();
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void paintComponent(Graphics g) {
        G.clear(g);
        time++;
        if(time == 10) {
            time = 0;
            shape.drop();
        }
        unZapWell();
        showWell(g);
        shape.show(g);
    }

    public void keyPressed(KeyEvent key) {
        int vK = key.getKeyCode();
        if (vK == KeyEvent.VK_LEFT) {
            shape.slide(G.LEFT);
        }
        if (vK == KeyEvent.VK_RIGHT) {
            shape.slide(G.RIGHT);
        }
        if (vK == KeyEvent.VK_UP) {
            shape.safeRot();
        }
        if (vK == KeyEvent.VK_DOWN) {
            shape.drop();
        }
    }

    public static void clearWell() {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                well[x][y] = iBack;
            }
        }
    }

    public static void showWell(Graphics g) {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                g.setColor(color[well[x][y]]);
                int xx = xM + C * x, yy = yM + C * y;
                g.fillRect(xx, yy, C, C);
                g.setColor(Color.BLACK);
                g.drawRect(xx, yy, C, C);
            }
        }
    }

    public static void zapWell() {
        for (int y = 0; y < H; y++) {
            zapRow(y);
        }
    }

    public static void zapRow(int y) {
        for (int x = 0; x < W; x++) {
            if (well[x][y] == iBack) {
                return;
            }
        }
        for (int x = 0; x < W; x++) {
            well[x][y] = zap;
        }
    }

    public static void unZapWell() {
        boolean done = false;
        for (int y = 1; y < H; y++) {
            for (int x = 0; x < W; x++) {
                if (well[x][y - 1] != zap && well[x][y] == zap) {
                    done = true;
                    well[x][y] = well[x][y - 1];
                    well[x][y - 1] = (y == 1) ? iBack : zap;
                }
            }
            if (done) {
                return;
            }
        }
    }

    public static void dropNewShape() {
//        if(nextShape == null)

        shape = shapes[G.rnd(7)];
//        nextShape = shapes[G.rnd(7)];
        shape.loc.set(4, 0);    // new shape centered at the top
    }

    public static void main(String[] args) {
        (PANEL = new Tetris()).launch();
    }


    /// ------------------------------------------------ Shape ------------------------------------------------------//

    public static class Shape {
        public static Shape Z, S, J, L, I, O, T;
        public static G.V temp = new G.V(0, 0);
        public static Shape cds = new Shape((new int[]{0, 0, 0, 0, 0, 0, 0, 0}), 0);
        public G.V[] a = new G.V[4];
        public G.V loc = new G.V();
        public int iColor; //Index of Color

        public Shape(int[] xy, int iC) {
            for (int i = 0; i < 4; i++) {
                a[i] = new G.V(xy[i * 2], xy[i * 2 + 1]);
//                a[i].set(xy[i * 2], xy[i * 2 + 1]);
            }
            iColor = iC;
        }

        public void show(Graphics g) {
            g.setColor(color[iColor]);
            for (int i = 0; i < 4; i++) {
                g.fillRect(x(i), y(i), C, C);
            }
            g.setColor(Color.BLACK);
            for (int i = 0; i < 4; i++) {
                g.drawRect(x(i), y(i), C, C);
            }
        }

        public int x(int i) {
            return xM + C * (a[i].x + loc.x);
        }

        public int y(int i) {
            return yM + C * (a[i].y + loc.y);
        }

        public void rot() {
            temp.set(0, 0);
            for (int i = 0; i < 4; i++) {
                a[i].set(-a[i].y, a[i].x);      // Rotates 90 (x,y) -> (-y, x).
                if (temp.x > a[i].x) {
                    temp.x = a[i].x;
                }
                if (temp.y > a[i].y) {
                    temp.y = a[i].y;
                }
            }
            temp.set(-temp.x, -temp.y);
            for (int i = 0; i < 4; i++) {
                a[i].add(temp);
            }
        }

        public void safeRot() {
            rot();
            cdsSet();
            if (collisionDetected()) {
                rot();
                rot();
                rot();
            }
        }

        public static boolean collisionDetected() {
            for (int i = 0; i < 4; i++) {
                G.V v = cds.a[i]; // local way to reffer to collision
                if (v.x < 0 || v.x >= W || v.y < 0 || v.y >= H) {
                    return true;
                }
                if (well[v.x][v.y] != iBack) {
                    return true;
                }
            }
            return false;
        }

        public void cdsSet() {
            for (int i = 0; i < 4; i++) {
                cds.a[i].set(a[i]);
                cds.a[i].add(loc);
            }
        }

        public void cdsGet() {
            for (int i = 0; i < 4; i++) {
                a[i].set(cds.a[i]);
            }
        }

        public void cdsAdd(G.V v) {
            for (int i = 0; i < 4; i++) {
                cds.a[i].add(v);
            }
        }

        public void slide(G.V dx) {
            cdsSet();
            cdsAdd(dx);
            if (collisionDetected()) {
                return;
            }
//            cdsGet();
            loc.add(dx);    // slide updates loc rather than changing it.
        }

        public void drop() {
            cdsSet();
            cdsAdd(G.DOWN);
            if (collisionDetected()) {
                copyToWell();
                zapWell();
                dropNewShape();
                return;
            }
            loc.add(G.DOWN);    // slide updates loc rather than changing it.
        }

        public void copyToWell() {
            for (int i = 0; i < 4; i++) {
                well[a[i].x + loc.x][a[i].y + loc.y] = iColor;
            }
        }

        static {
            Z = new Shape(new int[]{0, 0, 1, 0, 1, 1, 2, 1}, 0);
            S = new Shape(new int[]{0, 1, 1, 0, 1, 1, 2, 0}, 1);
            J = new Shape(new int[]{0, 0, 0, 1, 1, 1, 2, 1}, 2);
            L = new Shape(new int[]{0, 1, 1, 1, 2, 1, 2, 0}, 3);
            I = new Shape(new int[]{0, 0, 1, 0, 2, 0, 3, 0}, 4);
            O = new Shape(new int[]{0, 0, 1, 0, 0, 1, 1, 1}, 5);
            T = new Shape(new int[]{0, 1, 1, 0, 1, 1, 2, 1}, 6);
        }
    }
}