package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class modelTest extends JPanel {

    private static JLabel label;
    private static double initialTime;
    private static final int xsize = 1200;
    private static final int ysize = 700;
    private static CartesianCoordinates CENTER = transform(CartesianCoordinates.of(0,0));
    static double days = 0;
    private static final CartesianCoordinates SUN = transform(CartesianCoordinates.of(0,0));
    public static double refreshRate = 5;
    private static double radiusFactor = 200;
    private static double delta = -0.1;


    public static void main(String[] args){
        initialTime = System.nanoTime();
        JFrame frame = new JFrame("planetModel Test");
        JPanel canvas = new modelTest();
        canvas.setSize(xsize, ysize);
        label = new JLabel("Test");
        frame.add(canvas);
        frame.addKeyListener(new KeyListener());
        //frame.add(label);
        frame.setSize(xsize, ysize);
        //frame.pack();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        int delay = (int) refreshRate; //milliseconds

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                days = days + delta;
                canvas.repaint();
            }
        };

        javax.swing.Timer timer = new Timer((int)refreshRate, taskPerformer);
        timer.setRepeats(true);
        timer.start();
    }

    static CartesianCoordinates transform(CartesianCoordinates xy){
        return CartesianCoordinates.of(xy.x() + xsize/2, xy.y() + ysize / 2);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        EclipticToEquatorialConversion ecl = new EclipticToEquatorialConversion(Epoch.J2000.epoch.plusDays((long) days));
        Sun currentSun =  SunModel.SUN.at(days, ecl );
        double sunLon = ecl.apply (currentSun.eclipticPos()).ra();

        double sunX = PlanetModel.EARTH.getDatedPlanetInfo(days).getDistanceFromSun() *radiusFactor * Math.cos(sunLon);
        double sunY = PlanetModel.EARTH.getDatedPlanetInfo(days).getDistanceFromSun() *radiusFactor * Math.sin(sunLon);



        CENTER = transform(CartesianCoordinates.of(-sunX, -sunY));
        //Planet mars = PlanetModel.JUPITER.


            double radius = 3000;

        for(PlanetModel model : PlanetModel.values()){

            if(model.equals(PlanetModel.EARTH)) continue;

            double marsLon = model.at(days,ecl).equatorialPos().ra();
            //double marsLon = model.getDatedPlanetInfo(days).getHelioLon();
            double marsX = radius * Math.cos(marsLon);
            double marsY = radius * Math.sin(marsLon);

            g.setColor(Color.BLACK);
            g.drawLine((int) CENTER.x(), (int) CENTER.y(),(int)(CENTER.x() + marsX), (int)( CENTER.y() + marsY) );
            
        }
        double moonLon = MoonModel.MOON.at(days,ecl).equatorialPos().ra();
        double moonX = radius*0.4 * Math.cos(moonLon);
        double moonY = radius*0.4 * Math.sin(moonLon);
        g.setColor(Color.BLUE);
        g.drawLine((int) CENTER.x(), (int) CENTER.y(),(int)(CENTER.x() + moonX), (int)( CENTER.y() + moonY) );
        







        radius = 0;
        for(PlanetModel model : PlanetModel.values()){
            radius = radius + 10;
            if(model.equals(PlanetModel.EARTH)) continue;

            //double marsLon = model.at(days,ecl).equatorialPos().ra();
            double marsLon =  ecl.apply(model.getDatedPlanetInfo(days).getHeliocentricCoordinates()).ra();

            double marsX = model.getDatedPlanetInfo(days).getDistanceFromSun() *radiusFactor * Math.cos(marsLon);
            double marsY = model.getDatedPlanetInfo(days).getDistanceFromSun() *radiusFactor * Math.sin(marsLon);

            g.setColor(Color.GREEN);
            g.drawLine((int) SUN.x(), (int) SUN.y(),(int)(SUN.x() + marsX), (int)( SUN.y() + marsY) );
            g.setColor(Color.RED);
            g.fillOval((int)(SUN.x() + marsX) - 4, (int)( SUN.y() + marsY) - 4, 8,8);
            g.drawString(model.name(),(int)(SUN.x() + marsX), (int)( SUN.y() + marsY));
            radius = radius + 30;
        }
        
        
        g.setColor(Color.BLUE);
        g.fillOval((int)CENTER.x() -5, (int) CENTER.y() -5, 10,10);
        g.drawString("Earth", (int)CENTER.x() -5, (int) CENTER.y() -5);

        g.drawString("Days since J2010: " + days + "  Zoom in : I   Zoom out : O", 20,20);
        g.setColor(Color.YELLOW);
        g.fillOval((int)(SUN.x()) -10, (int)( SUN.y()) -10, 20,20);
        g.drawString("Sun " + currentSun.angularSize(), (int)SUN.x() +10, (int)SUN.y() + 10);
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
