/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculusfinalproject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JOptionPane;
/**
 *
 * @author Daniel Peng
 */
public class DrawingSurface extends JPanel implements ActionListener, MouseMotionListener, MouseListener{
    private Graph graph;
    private Timer timer;
    
    
    public DrawingSurface(){
        String equation = JOptionPane.showInputDialog("Enter your equation");
        graph = new Graph(equation);
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);
        
        addMouseMotionListener(this);
        addMouseListener(this);
        
        timer = new Timer(10, this);
        timer.start();
    }
    
    //does the actual drawing
     private void doDrawing(Graphics g) {
        //the Graphics2D class is the class that handles all the drawing
        //must be casted from older Graphics class in order to have access to some newer methods
        Graphics2D g2d = (Graphics2D) g;
        graph.draw(g2d);
    }
     private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
    }
    
    //overrides paintComponent in JPanel class
    //performs custom painting
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);//does the necessary work to prepare the panel for drawing        
        doDrawing(g);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        //update components
        
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double[] newPos = {e.getX(), e.getY()};
        graph.mouseMoved(newPos);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    
    
}
