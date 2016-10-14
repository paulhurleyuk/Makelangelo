package com.marginallyclever.makelangeloRobot.converters;

import java.io.IOException;
import java.io.Writer;

import com.jogamp.opengl.GL2;
import com.marginallyclever.makelangeloRobot.TransformedImage;
import com.marginallyclever.makelangeloRobot.ImageManipulator;
import com.marginallyclever.makelangeloRobot.MakelangeloRobotDecorator;
import com.marginallyclever.makelangeloRobot.settings.MakelangeloRobotSettings;

/**
 * Converts a BufferedImage to gcode
 * Don't forget http://www.reverb-marketing.com/wiki/index.php/When_a_new_style_has_been_added_to_the_Makelangelo_software
 * @author Dan Royer
 *
 */
public abstract class ImageConverter extends ImageManipulator implements MakelangeloRobotDecorator {
	/**
	 * @param img the <code>java.awt.image.BufferedImage</code> this filter is using as source material.
	 * @return true if conversion succeeded.
	 */
	public boolean convert(TransformedImage img,Writer out) throws IOException {
		return false;
	}

	/**
	 * live preview as the system is converting pictures.
	 * draw the results as the calculation is being performed.
	 */
	public void render(GL2 gl2,MakelangeloRobotSettings settings) {}
}
