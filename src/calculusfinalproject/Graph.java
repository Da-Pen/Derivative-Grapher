/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculusfinalproject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Daniel Peng
 */
public class Graph {
    
    private static final int POINT_DIAMETER = 10;
    private static final int DIVISIONS = 10000;    
    private static final DecimalFormat twoDecimalFormat = new DecimalFormat("0.##");
    private static final DecimalFormat fiveDecimalFormat = new DecimalFormat("0.#####");
    
    private double[] domain;
    private double[] range;
    private double stretchX;
    private double stretchY;
    
    private ArrayList<Double> xValues;
    private ArrayList<Double> yValues;
    private ArrayList<Double> xDerivatives;
    private ArrayList<Double> yDerivatives;
    
    private double xAxis;
    private double yAxis;
    
    private double[] mousePos;
    
    private double startX;
    private double endX;
    private double interval;
    
    public Graph(String equation){
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        xDerivatives = new ArrayList<>();
        yDerivatives = new ArrayList<>();
        
        mousePos = new double[2];
        initValues(equation);
    }
    
    
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) {
                        x += parseTerm(); // addition
                    } else if (eat('-')) {
                        x -= parseTerm(); // subtraction
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) {
                        x *= parseFactor(); // multiplication
                    } else if (eat('/')) {
                        x /= parseFactor(); // division
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) {
                    return parseFactor(); // unary plus
                }
                if (eat('-')) {
                    return -parseFactor(); // unary minus
                }
                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') {
                        nextChar();
                    }
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) {
                        x = Math.sqrt(x);
                    } else if (func.equals("sin")) {
                        x = Math.sin(x);
                    } else if (func.equals("cos")) {
                        x = Math.cos(x);
                    } else if (func.equals("tan")) {
                        x = Math.tan(x);
                    } else if(func.equals("log")){
                        x = Math.log(x);
                    }else {
                        throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) {
                    x = Math.pow(x, parseFactor()); // exponentiation
                }
                return x;
            }
        }.parse();
    }
    
    public void mouseMoved(double[] pos){
        mousePos = pos;
    }
    
    private void initValues(String equation){
        startX =  Double.parseDouble(JOptionPane.showInputDialog("Enter the starting (left) x value you want displayed"));
        endX =  Double.parseDouble(JOptionPane.showInputDialog("Enter the ending (right) x value you want displayed"));
        interval = (endX - startX)/DIVISIONS;
        
        //delete whitepsace
        equation = equation.replace(" ", "");
        //remove "y=" if the user put this
        if(equation.charAt(0) == 'y')equation = equation.substring(1);
        if(equation.charAt(0) == '=')equation = equation.substring(1);
        
        domain = new double[2];
        domain[0] = startX;
        domain[1] = endX;
        
        for (double i = domain[0]; i <= domain[1]; i+= interval) {
           
            String s = fiveDecimalFormat.format(i);
            String currExpression = equation.replace("x", "(" + s + ")");
            currExpression = currExpression.replace("X", "(" + s + ")");            
            xValues.add(i);
            yValues.add(-(double)eval(currExpression));
            
        }
        
        int numValues = xValues.size();
        
        //calculate derivative values
        for (int i = 0; i < numValues - 1; i++) {
            xDerivatives.add((xValues.get(i) + xValues.get(i+1))/2);
            yDerivatives.add((yValues.get(i+1) - yValues.get(i))/interval);
        }
        
        //strech function to fit frame
        range = new double[2];
        range[0] = Math.min(Math.min(getMin(yValues), getMin(yDerivatives)), 0);
        range[1] = Math.max(Math.max(getMax(yValues), getMax(yDerivatives)), 0);
        range[0] = range[0] - Math.abs(range[0]*0.2);
        range[1] = range[1] + Math.abs(range[1]*0.2);        
                
       // System.out.println("range: " + range[0] + ", " + range[1]);
        stretchX = CalculusFinalProject.FRAME_SIZE/(domain[1] - domain[0]);
        stretchY = CalculusFinalProject.FRAME_SIZE/(range[1] - range[0]);
        
        xAxis = getStretchedY(0);
        yAxis = getStretchedX(0);
        
        
    }
    
    public void draw(Graphics2D g){
        
        int numValues = xValues.size();
        
        //draw axes
        g.drawLine(0, (int)xAxis, CalculusFinalProject.FRAME_SIZE, (int)xAxis);
        g.drawLine((int)yAxis, 0, (int)yAxis, CalculusFinalProject.FRAME_SIZE);
        
        //draw as graph
        g.setColor(Color.BLUE);
        for (int i = 0; i < numValues - 1; i++) {
        //    System.out.println(yValuesStretched.get(i));
            g.drawLine((int)getStretchedX(xValues.get(i).doubleValue()), (int)getStretchedY(yValues.get(i).doubleValue()),
                    (int)getStretchedX(xValues.get(i+1).doubleValue()), (int)getStretchedY(yValues.get(i+1).doubleValue()));
        }
        
        //point on original graph
        int index = (int)((getUnstretchedX(mousePos[0])-startX)/interval);
        int pointX = (int)getStretchedX(xValues.get(index));
        int pointY = (int)getStretchedY(yValues.get(index));
        
        g.fillOval(pointX - POINT_DIAMETER/2, pointY - POINT_DIAMETER/2, POINT_DIAMETER, POINT_DIAMETER);
        //draw point coordinates
        g.drawString("(" + twoDecimalFormat.format(xValues.get(index)) + ", " + twoDecimalFormat.format(yValues.get(index)) + ")", pointX, pointY - 10);
        
        //draw derivative as graph
        g.setColor(Color.RED);
        for (int i = 0; i < numValues - 2; i++) {
            g.drawLine((int)getStretchedX(xDerivatives.get(i).doubleValue()), (int)getStretchedY(yDerivatives.get(i).doubleValue()),
                    (int)getStretchedX(xDerivatives.get(i+1).doubleValue()), (int)getStretchedY(yDerivatives.get(i+1).doubleValue()));
        }
        
        
        //point on derivative graph
        pointX = (int)getStretchedX(xDerivatives.get(index));
        pointY = (int)getStretchedY(yDerivatives.get(index));
        g.fillOval(pointX - POINT_DIAMETER/2, pointY - POINT_DIAMETER/2, POINT_DIAMETER, POINT_DIAMETER);
        //draw point coordinates
        g.drawString("(" + twoDecimalFormat.format(xDerivatives.get(index)) + ", " + twoDecimalFormat.format(yDerivatives.get(index)) + ")", pointX, pointY + 15);
        
        //draw tangent line
        g.setColor(Color.GREEN);
        double slope = yDerivatives.get(index);
        double b = yValues.get(index) - slope*xValues.get(index);//y-intercept
        
        double leftY = slope*startX + b;//y location of tangent line on left side of frame
        double rightY = slope*endX + b;//y location of tangent line on right side of frame
        //tangent line
        g.drawLine(0, (int)getStretchedY(leftY), CalculusFinalProject.FRAME_SIZE, (int)getStretchedY(rightY));
        
        //draw legend
        g.setColor(Color.WHITE);
        g.fillRect(10, 10, 150, 80);
        g.setColor(Color.BLACK);
        g.drawRect(10, 10, 150, 80);
        //line for original function
        g.setColor(Color.BLUE);
        g.drawLine(30, 30, 40, 30);
        g.drawString("Original Graph", 50, 35);
        //line for derivative function
        g.setColor(Color.RED);
        g.drawLine(30, 50, 40, 50);
        g.drawString("Derivative Graph", 50, 55);
        //line for tangent line
        g.setColor(Color.GREEN);
        g.drawLine(30, 70, 40, 70);
        g.drawString("Tangent Line", 50, 75);
    }
    
    private Double getMax(ArrayList<Double> a){
        Double max = a.get(0);
        for (Double i : a) {
            if(i > max){
                max = i;
            }
        }
        return max;
    }
    private Double getMin(ArrayList<Double> a){
        Double min = a.get(0);
        for (Double i : a) {
            if(i < min){
                min = i;
            }
        }
        return min;
    }
    
    private double getStretchedX(double x){
        return (x - domain[0])*stretchX;
    }
    
    private double getStretchedY(double y){
        return (y - range[0])*stretchY;
    }
    
    private double getUnstretchedX(double x){
        return x/stretchX + domain[0];
    }
    
    private double getUnstretchedY(double y){
        return y/stretchY + range[0];
    }
}
