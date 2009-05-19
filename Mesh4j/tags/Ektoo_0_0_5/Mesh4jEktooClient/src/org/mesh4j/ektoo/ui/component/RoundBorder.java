package org.mesh4j.ektoo.ui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.LineBorder;

public class RoundBorder extends LineBorder
{
  private static final long serialVersionUID = -7231786606013473646L;
  private static final int ARC_WIDTH = 15;
  private static final int ARC_HEIGHT= 15;
    
  Color fillColor = Color.LIGHT_GRAY;

  public RoundBorder(Color color) 
  {
    super(Color.red);
    fillColor = color;
  }
  
  public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
  {  
    w = w - 3;  
    h = h - 3;  
    x ++;  
    y ++;  

    g.setColor(fillColor);  
    
    g.drawLine(x, y + 2, x, y + h - 2);  
    g.drawLine(x + 2, y, x + w - 2, y);  
    g.drawLine(x, y + 2, x + 2, y); // Top left diagonal  
    g.drawLine(x, y + h - 2, x + 2, y + h); // Bottom left diagonal  
    
    g.drawLine(x + w, y + 2, x + w, y + h - 2);  
    g.drawLine(x + 2, y + h, x + w -2, y + h);  
    g.drawLine(x + w - 2, y, x + w, y + 2); // Top right diagonal  
    g.drawLine(x + w, y + h - 2, x + w -2, y + h); // Bottom right diagonal  
   }    
}