package ch.epfl.rigel;

import ch.epfl.rigel.coordinates.CartesianCoordinates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class modelTest extends Canvas {

    private static JLabel label;
    private static double initialTime;
    private static final int xsize = 600;
    private static final int ysize = 600;
    private static final CartesianCoordinates CENTER = transform(CartesianCoordinates.of(0,0));

    public static void main(String[] args){
        initialTime = System.nanoTime();
        JFrame frame = new JFrame("planetModel Test");
        Canvas canvas = new modelTest();
        canvas.setSize(xsize, ysize);
        label = new JLabel("Test");
        frame.add(canvas);
        //frame.add(label);
        frame.pack();
        frame.setVisible(true);

        int delay = 1000; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                canvas.repaint();
            }
        };
        new Timer(delay, taskPerformer).start();
    }

    static CartesianCoordinates transform(CartesianCoordinates xy){
        return CartesianCoordinates.of(xy.x() + xsize/2, xy.y() + ysize / 2);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int)CENTER.x(), (int) CENTER.y(), 5,5);




    }


}
