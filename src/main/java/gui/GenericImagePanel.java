package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GenericImagePanel class.
 * 
 * @author Teodora C.
 */
public class GenericImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static int SUB_PANEL_WIDTH = 320;
	private final static int SUB_PANEL_HEIGHT = 240;

	private final ImageType imageType;
	private JLabel imageLabel;
	private BufferedImage image;
	private String imagePath;

	/**
	 * The constructor for GenericImagePanel.
	 * 
	 * @param mainPanel
	 * @param imageType
	 * @param imageTitle
	 */
	public GenericImagePanel(MainPanel mainPanel, ImageType imageType,
			String imageTitle) {
		this.imageType = imageType;
		setImagePanelSettings();
		createComponents(imageTitle);
	}

	/**
	 * Creates the panel components.
	 * 
	 * @param imageTitle
	 */
	private void createComponents(String imageTitle) {
		imageLabel = new JLabel(imageTitle + " Preview");
		setLabelSettings(imageLabel);
		add(imageLabel);
	}

	/**
	 * Defines the panel settings.
	 */
	public void setImagePanelSettings() {
		setLayout(new BorderLayout());
		setAlignmentX(CENTER_ALIGNMENT);
		setPreferredSize(new Dimension(SUB_PANEL_WIDTH, SUB_PANEL_HEIGHT));
		add(Box.createRigidArea(new Dimension(0, 5)));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setBackground(new Color(177, 177, 200));
	}

	/**
	 * Defines the label settings.
	 * 
	 * @param label
	 */
	public void setLabelSettings(JLabel label) {
		label.setFont(new Font("Calibri", Font.BOLD, 13));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		repaint();
	}

	/**
	 * Sets the chosen image as label.
	 * 
	 * @param image
	 * @param imagePath
	 */
	public void setImage(BufferedImage image, String imagePath) {
		this.image = image;
		this.imagePath = imagePath;
		if (image != null) {
			imageLabel.setText("");
			imageLabel.setIcon(convertImageToIcon(image));
			imageLabel.setToolTipText(imagePath);
		}
	}

	/**
	 * Converts the chosen image to an icon.
	 * 
	 * @param image
	 * @return
	 */
	public ImageIcon convertImageToIcon(BufferedImage image) {
		double ratio = 4 / 3;
		double initialWidth = image.getWidth();
		double initialHeight = image.getHeight();
		double finalWidth = SUB_PANEL_WIDTH;
		double finalHeight = SUB_PANEL_HEIGHT;
		ratio = initialWidth / initialHeight;
		if (initialWidth > initialHeight) {
			finalWidth = SUB_PANEL_WIDTH;
			finalHeight = finalWidth / ratio;
		} else {
			finalHeight = SUB_PANEL_HEIGHT;
			finalWidth = finalHeight / ratio;
		}
		Image newImage = image.getScaledInstance((int) finalWidth,
				(int) finalHeight, java.awt.Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(newImage);
		return imageIcon;
	}

	/**
	 * Getter for the image type.
	 * 
	 * @return
	 */
	public ImageType getImageType() {
		return imageType;
	}

	/**
	 * Getter for the image.
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Getter for the image path.
	 * 
	 * @return
	 */
	public String getImagePath() {
		return imagePath;
	}

}
