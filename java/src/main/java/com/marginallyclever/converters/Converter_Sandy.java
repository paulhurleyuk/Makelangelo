package com.marginallyclever.converters;


import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.marginallyclever.basictypes.ImageConverter;
import com.marginallyclever.filters.Filter_BlackAndWhite;
import com.marginallyclever.makelangelo.MakelangeloRobot;
import com.marginallyclever.makelangelo.MainGUI;
import com.marginallyclever.makelangelo.MultilingualSupport;


public class Converter_Sandy extends ImageConverter {
  float blockScale=50.0f;
  int direction=0;
  
  public Converter_Sandy(MainGUI gui, MakelangeloRobot mc,
      MultilingualSupport ms) {
    super(gui, mc, ms);
  }

  @Override
  public String getName() {
	  return translator.get("Sandy Noble Style");
  }

  /**
   * Overrides MoveTo() because optimizing for zigzag is different logic than straight lines.
   */
  @Override
  protected void moveTo(Writer out,float x,float y,boolean up) throws IOException {
    if(lastup!=up) {
      if(up) liftPen(out);
      else   lowerPen(out);
      lastup=up;
    }
    tool.writeMoveTo(out, TX(x), TY(y));
  }
  
  /**
   * create horizontal lines across the image.  Raise and lower the pen to darken the appropriate areas
   * @param img the image to convert.
   */
  public boolean convert(BufferedImage img,Writer out) throws IOException {
    final JTextField field_size = new JTextField(Float.toString(blockScale));

    JPanel panel = new JPanel(new GridLayout(0,1));
    panel.add(new JLabel(translator.get("HilbertCurveSize")));
    panel.add(field_size);
    
    String [] directions = { "top right", "top left", "bottom left", "bottom right", "center" };
    final JComboBox<String> direction_choices = new JComboBox<>(directions);
    panel.add(direction_choices);
    
    int result = JOptionPane.showConfirmDialog(null, panel, getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
      blockScale = Float.parseFloat(field_size.getText());
      direction = direction_choices.getSelectedIndex();
      convertNow(img,out);
      return true;
    }
    return false;
  }
  

  /**
   * Converts images into zigzags in paper space instead of image space
   * @param img the buffered image to convert
   * @throws IOException couldn't open output file
   */
  private void convertNow(BufferedImage img,Writer out) throws IOException {
    // make black & white
    Filter_BlackAndWhite bw = new Filter_BlackAndWhite(mainGUI, machine, translator, 255);
    img = bw.filter(img);
  
        imageStart(img, out);

        // set absolute coordinates
        out.write("G00 G90;\n");
        tool.writeChangeTo(out);
        liftPen(out);

//	      convertImageSpace(img, out);
		convertPaperSpace(img,out);

        liftPen(out);
  }
  
  
  private void convertPaperSpace(BufferedImage img,Writer out) throws IOException {
    // if the image were projected on the paper, where would the top left corner of the image be in paper space?
    // image(0,0) is (-paperWidth/2,-paperHeight/2)*paperMargin
	setupPaperImageTransform();
    
    double PULSE_MINIMUM=0.5;

    // from top to bottom of the image...
    double x, y, z, scaleZ, pulseSize;
    
    
    double dx = xStart - machine.getLimitRight()*10; 
    double dy = yStart - machine.getLimitTop()*10;
    double rMax = Math.sqrt(dx*dx+dy*dy);
    double rMin = 0;
    
	double cx,cy;
	
	switch(direction) {
	case 0:
		cx = machine.getLimitRight()*10;
		cy = machine.getLimitTop()*10;
		break;
	case 1:
		cx = machine.getLimitLeft()*10;
		cy = machine.getLimitTop()*10;
		break;
	case 2:
		cx = machine.getLimitLeft()*10;
		cy = machine.getLimitBottom()*10;
		break;
	case 3:
		cx = machine.getLimitLeft()*10;
		cy = machine.getLimitBottom()*10;
		break;
	default:
		cx = 0;
		cy = 0;
		break;
	}

    double rStep = (rMax-rMin)/blockScale;
    double r;
	double t_dir=1;
	double pulseFlip=1;
	double x2,y2,t,t_step, t_step2;
	double last_x=0,last_y=0;
	boolean wasDrawing=true;
    
    for(r=rMin;r<rMax;r+=rStep) {
    	// go around in a circle
    	t=0;
    	t_step = tool.getDiameter()/r;
    	for(t=0;t<Math.PI*2;t+=t_step2) {
    		dx = Math.cos(t_dir *t);
    		dy = Math.sin(t_dir *t);
	    	x = cx + dx * r;
	    	y = cy + dy * r;
	    	t_step2=t_step;
		    if(!isInsideLimits(x,y)) {
		    	if(wasDrawing) {
		    		moveToPaper(out,last_x,last_y,true);
		    		wasDrawing=false;
		    	}
		    	continue;
		    }
		    
		    last_x=x;
		    last_y=y;
            // read a block of the image and find the average intensity in this block
            z = sampleScale( img, x-rStep/2, y-rStep/2,x+rStep/2,y + rStep/2 );
            // scale the intensity value
            assert(z>=0);
            assert(z<=255.0);
            scaleZ = (255.0 -  z) / 255.0;
            scaleZ = 1-scaleZ;
            pulseSize = rStep*0.5;//r_step * 0.6 * scale_z;
	    	t_step2=t_step*pulseSize*scaleZ;
	    	if(t_step2<t_step) t_step2=t_step;
            
	    	x2 = x + dx * pulseSize*pulseFlip;
	    	y2 = y + dy * pulseSize*pulseFlip;
	    	pulseFlip=-pulseFlip;
	    	if(wasDrawing == false) {
	    		moveToPaper(out,last_x,last_y,pulseSize<PULSE_MINIMUM);
	    		wasDrawing=true;
	    	}
    		moveToPaper(out,x2,y2,pulseSize<PULSE_MINIMUM);
	    	x2 = x + dx * pulseSize*pulseFlip;
	    	y2 = y + dy * pulseSize*pulseFlip;
    		moveToPaper(out,x2,y2,pulseSize<PULSE_MINIMUM);
    	}
    	t_dir=-t_dir;
    }
  }
}


/**
 * This file is part of DrawbotGUI.
 *
 * DrawbotGUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DrawbotGUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DrawbotGUI.  If not, see <http://www.gnu.org/licenses/>.
 */