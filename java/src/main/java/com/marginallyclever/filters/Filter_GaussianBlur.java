package com.marginallyclever.filters;

import com.marginallyclever.basictypes.C3;
import com.marginallyclever.makelangelo.MachineConfiguration;
import com.marginallyclever.makelangelo.MainGUI;
import com.marginallyclever.makelangelo.MultilingualSupport;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

	
/**
 * Converts an image to N shades of grey.
 * @author Dan
 *
 */
public class Filter_GaussianBlur extends Filter {
	int radius=1;


	public Filter_GaussianBlur(MainGUI gui,
			MachineConfiguration mc, MultilingualSupport ms,int _radius) {
		super(gui, mc, ms);
		assert(radius>0);
		radius = _radius;
	}
	
	@Override
	public BufferedImage process(BufferedImage img) {
		int h = img.getHeight();
		int w = img.getWidth();
		int x,y;

		BufferedImage dest = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
		
		//float [] scales = new float[radius];
		// TODO fill scales[] with a gaussian curve
		float [] scales = new float[3];
		scales[0] = 1.0f/4.0f;
		scales[1] = 1.0f/2.0f;
		scales[2] = 1.0f/4.0f;
		
		C3 pixel = new C3(0,0,0);
		C3 p;
		double sum;
		
		// horizontal blur
		for(y=0;y<h;++y) {
			for(x=0;x<w;++x) {
				pixel.set(0,0,0);
				sum=0;
				if(x-1>=0) {
					p = new C3(img.getRGB(x-1, y));
					p.mul(scales[0]);
					pixel.add(p);
					sum+= scales[0];
				}

				p = new C3(img.getRGB(x, y));
				p.mul(scales[1]);
				pixel.add(p);
				sum+= scales[1];
				
				if(x+1< w) {
					p = new C3(img.getRGB(x+1, y));
					p.mul(scales[2]);
					pixel.add(p);
					sum+= scales[2];
				}
				
				//pixel.mul(1.0/sum);
				//if(b==255) System.out.println(x+"\t"+y+"\t"+i+"\t"+b);
				dest.setRGB(x, y, pixel.toInt());
			}
		}

		// vertical blur
		for(x=0;x<w;++x) {
			for(y=0;y<h;++y) {
				pixel.set(0,0,0);
				sum=0;
				if(y-1>=0) {
					p = new C3(dest.getRGB(x, y-1));
					p.mul(scales[0]);
					pixel.add(p);
					sum+= scales[0];
				}

				p = new C3(dest.getRGB(x, y));
				p.mul(scales[1]);
				pixel.add(p);
				sum+= scales[1];
				
				if(y+1< h) {
					p = new C3(dest.getRGB(x, y+1));
					p.mul(scales[2]);
					pixel.add(p);
					sum+= scales[2];
				}
				
				pixel.mul(1.0/sum);
				//if(b==255) System.out.println(x+"\t"+y+"\t"+i+"\t"+b);
				img.setRGB(x, y, pixel.toInt());
			}
		}

		try {
		    // save image
		    File outputfile = new File("saved.png");
		    ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return img;
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