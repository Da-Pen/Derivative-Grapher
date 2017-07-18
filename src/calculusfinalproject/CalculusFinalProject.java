/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculusfinalproject;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Daniel Peng
 */
public class CalculusFinalProject extends JFrame{
    
    public final static int FRAME_SIZE = 700;
    
    //constructor
    public CalculusFinalProject() {
        //create the User interface
        initUI();
        
    }
    
    //create the custom JFrame
    private void initUI() {
        //set title of the JFrame
        setTitle("CalculusFinalProject");
        //add a custom JPanel to draw on
        add(new DrawingSurface());
        //set the size of the window
        //30x10+16, 30x20+38
        setSize(FRAME_SIZE + 16, FRAME_SIZE + 38); //inner frame is 500 x 500
        //tell the JFrame what to do when closed
        //this is important if our application has multiple windows
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        //make sure that all UI updates are concurrency safe (related to multi threading)
        //much more detailed http://www.javamex.com/tutorials/threads/invokelater.shtml
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //instantiate the main window
                CalculusFinalProject windowFrame = new CalculusFinalProject();
                //make sure it can be seen
                windowFrame.setVisible(true);
            }
        });
    }
}
