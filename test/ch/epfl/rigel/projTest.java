package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.astronomy.PlanetModel;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.math.Angle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Function;

public class projTest extends Canvas {

    private static JLabel label;
    private static double initialTime;
    private static final int xsize = 1200;
    private static final int ysize = 700;
    private static CartesianCoordinates CENTER = transform(CartesianCoordinates.of(0,0));
    static double days = 0;
    private static final CartesianCoordinates SUN = transform(CartesianCoordinates.of(0,0));
    public static double refreshRate = 10;
    private static double radiusFactor = 1;
    private static double delta = 0.5;


    public static void main(String[] args){
        initialTime = System.nanoTime();
        JFrame frame = new JFrame("planetModel Test");
        Canvas canvas = new projTest();
        canvas.setSize(xsize, ysize);
        label = new JLabel("Test");
        frame.add(canvas);
        frame.addKeyListener(new KeyListener());
        //frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        int delay = (int) refreshRate; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                days = days + delta;
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


        EclipticToEquatorialConversion ecl = new EclipticToEquatorialConversion(Epoch.J2000.epoch.plusDays((long) days));
        EquatorialToHorizontalConversion conv = new EquatorialToHorizontalConversion(Epoch.J2000.epoch.plusDays((long) days),GeographicCoordinates.ofDeg(0,0));
        StereographicProjection sp = new StereographicProjection(HorizontalCoordinates.ofDeg(0,0));

        Sun currentSun = SunModel.SUN.at(days, ecl);

        Function<EquatorialCoordinates, CartesianCoordinates> proj = conv.andThen(sp);

        g.drawLine(0, (int)SUN.y(),xsize, (int) SUN.y());

        CartesianCoordinates sunValues = proj.apply(currentSun.equatorialPos());
        CartesianCoordinates sun = CartesianCoordinates.of(sunValues.x() * radiusFactor, sunValues.y() * radiusFactor);
        g.setColor(Color.ORANGE);
        g.drawOval((int) (SUN.x() + sun.x()), (int)(SUN.y() + sun.y()), 5,5);
        g.drawString(sun.x() + "| " + sun.y() + "  Days: " + days, 10 , 10);

        CartesianCoordinates zeroValues = proj.apply(EquatorialCoordinates.of(0,0));
        CartesianCoordinates zero = CartesianCoordinates.of(zeroValues.x() * radiusFactor, zeroValues.y() * radiusFactor);
        g.setColor(Color.RED);
        g.drawOval((int) (SUN.x() + zero.x()), (int)(SUN.y() + zero.y()), 5,5);



        for(PlanetModel model : PlanetModel.values()){

            if(model.equals(PlanetModel.EARTH)) continue;

            CartesianCoordinates modelValues = proj.apply(model.at(days,ecl).equatorialPos());
            CartesianCoordinates mod = CartesianCoordinates.of(modelValues.x() * radiusFactor, modelValues.y() * radiusFactor);
            g.setColor(Color.BLUE);
            g.drawOval((int) (SUN.x() + mod.x()), (int)(SUN.y() + mod.y()), 5,5);
            g.drawString(model.name(), (int) (SUN.x() + mod.x()), (int)(SUN.y() + mod.y()));

        }



    }


    static class KeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {

            char ch = event.getKeyChar();

            if(ch == 'o') radiusFactor = radiusFactor / 1.3;
            if(ch == 'i') radiusFactor = radiusFactor * 1.3;

        }
    }

}
