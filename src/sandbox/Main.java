package sandbox;

import graphics.Window;

import java.awt.*;

public class Main {
    public static void main(String [] args){

        System.out.println("hello, do");

        Window.PANEL = new ShapeTrainer();
//        Window.PANEL = new PaintInk();
        Window.launch();
        
    }
}
