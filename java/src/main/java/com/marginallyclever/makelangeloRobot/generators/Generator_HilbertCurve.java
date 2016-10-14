package com.marginallyclever.makelangeloRobot.generators;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.marginallyclever.makelangelo.Translator;

public class Generator_HilbertCurve extends ImageGenerator {
	private Turtle turtle;
	private float turtleStep = 10.0f;
	private float xMax = 7;
	private float xMin = -7;
	private float yMax = 7;
	private float yMin = -7;
	private int order = 4; // controls complexity of curve


	@Override
	public String getName() {
		return Translator.get("HilbertCurveName");
	}

	@Override
	public String getPreviewImage() {
		return "/images/generators/hilbert-curve.JPG";
	}



	@Override
	public boolean generate(Writer out) throws IOException {
		final JTextField field_order = new JTextField(Integer.toString(order));

		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel(Translator.get("HilbertCurveOrder")));
		panel.add(field_order);

		int result = JOptionPane.showConfirmDialog(null, panel, getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			order = Integer.parseInt(field_order.getText());
			createCurveNow(out);
			return true;
		}
		return false;
	}


	private void createCurveNow(Writer out) throws IOException {
		imageStart(out);
		liftPen(out);
		machine.writeChangeTo(out);

		float v = Math.min((float)(machine.getPaperWidth()  * machine.getPaperMargin()),
				           (float)(machine.getPaperHeight() * machine.getPaperMargin())) * 10.0f/2.0f;
		xMax = v;
		yMax = v;
		xMin = -v;
		yMin = -v;

		turtle = new Turtle();
		turtleStep = (float) ((xMax - xMin) / (Math.pow(2, order)));

		boolean drawBoundingBox=false;
		if(drawBoundingBox) {
			liftPen(out);
			moveTo(out, xMax, yMax, false);
			moveTo(out, xMax, yMin, false);
			moveTo(out, xMin, yMin, false);
			moveTo(out, xMin, yMax, false);
			moveTo(out, xMax, yMax, false);
			liftPen(out);
		}

		// move to starting position
		turtle.setX(xMax - turtleStep / 2);
		turtle.setY(yMax - turtleStep / 2);
		moveTo(out, turtle.getX(), turtle.getY(), true);
		lowerPen(out);
		// do the curve
		hilbert(out, order);
		liftPen(out);
	    moveTo(out, (float)machine.getHomeX(), (float)machine.getHomeY(),true);
	}


	// Hilbert curve
	private void hilbert(Writer output, int n) throws IOException {
		if (n == 0) return;
		turtle.turn(90);
		treblih(output, n - 1);
		turtle_goForward(output);
		turtle.turn(-90);
		hilbert(output, n - 1);
		turtle_goForward(output);
		hilbert(output, n - 1);
		turtle.turn(-90);
		turtle_goForward(output);
		treblih(output, n - 1);
		turtle.turn(90);
	}


	// evruc trebliH
	public void treblih(Writer output, int n) throws IOException {
		if (n == 0) return;
		turtle.turn(-90);
		hilbert(output, n - 1);
		turtle_goForward(output);
		turtle.turn(90);
		treblih(output, n - 1);
		turtle_goForward(output);
		treblih(output, n - 1);
		turtle.turn(90);
		turtle_goForward(output);
		hilbert(output, n - 1);
		turtle.turn(-90);
	}


	public void turtle_goForward(Writer output) throws IOException {
		//turtle_x += turtle_dx * distance;
		//turtle_y += turtle_dy * distance;
		//output.write(new String("G0 X"+(turtle_x)+" Y"+(turtle_y)+"\n").getBytes());
		turtle.move(turtleStep);
		moveTo(output, turtle.getX(), turtle.getY(), false);
	}
}
