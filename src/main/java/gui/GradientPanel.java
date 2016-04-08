package main.java.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;

/**
 * GradientPanel class.
 * 
 * @author Teodora C.
 */
class GradientPanel extends JPanel {
	private static final long serialVersionUID = -6385751027379193053L;

	/**
	 * The constructor for GradientPanel.
	 * 
	 * @param background
	 */
	public GradientPanel(Color background) {
		setBackground(background);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (isOpaque()) {
			// Color controlColor = new Color(99, 153, 255);
			Color controlColor = new Color(120, 85, 150);
			int width = getWidth();
			int height = getHeight();

			Graphics2D g2 = (Graphics2D) g;
			Paint oldPaint = g2.getPaint();
			g2.setPaint(new GradientPaint(0, 0, getBackground(), width, 0,
					controlColor));
			g2.fillRect(0, 0, width, height);
			g2.setPaint(oldPaint);
		}
	}

}