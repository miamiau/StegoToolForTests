package main.java.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * GenericPanel class.
 * 
 * @author Teodora C.
 */
public class GenericPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static int PANEL_WIDTH = 325;
	private final static int PANEL_HEIGHT = 260;

	private final MainPanel mainPanel;

	private GenericImagePanel imagePanel;

	/**
	 * The constructor for GenericPanel.
	 * 
	 * @param mainPanel
	 * @param imageType
	 * @param buttonText
	 * @param imageTitle
	 * @param action
	 */
	public GenericPanel(MainPanel mainPanel, ImageType imageType,
			String buttonText, String imageTitle, AbstractAction action,
			BufferedImage image) {
		this.mainPanel = mainPanel;
		setPanelSettings();
		createComponents(buttonText, imageType, imageTitle, action);
	}

	/**
	 * Defines the panel settings.
	 */
	public void setPanelSettings() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		add(Box.createRigidArea(new Dimension(0, 5)));
	}

	/**
	 * Creates the panel components.
	 * 
	 * @param buttonText
	 * @param imageType
	 * @param imageTitle
	 * @param action
	 */
	private void createComponents(String buttonText, ImageType imageType,
			String imageTitle, AbstractAction action) {
		createImagePanel(imageTitle, imageType, action);
		createButton(buttonText, imageTitle, action);
	}

	/**
	 * Creates the image panel.
	 * 
	 * @param imageTitle
	 * @param imageType
	 * @param action
	 */
	private void createImagePanel(String imageTitle, ImageType imageType,
			AbstractAction action) {
		imagePanel = new GenericImagePanel(mainPanel, imageType, imageTitle);
		imagePanel.setAlignmentX(CENTER_ALIGNMENT);
		add(imagePanel);
	}

	/**
	 * Creates the button.
	 * 
	 * @param buttonText
	 * @param imageTitle
	 * @param action
	 */
	private void createButton(String buttonText, String imageTitle,
			AbstractAction action) {
		JButton selectImageButton = new JButton(buttonText + imageTitle);
		selectImageButton.setAlignmentX(CENTER_ALIGNMENT);
		selectImageButton.addActionListener(action);
		selectImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainPanel.setCurrentImagePanel(imagePanel);
			}
		});
		add(selectImageButton);
	}

	/**
	 * Gets the selected image. Reads the image from file system (if found).
	 * 
	 * @param selectedFile
	 * @return selectedImage
	 * @throws IOException
	 */
	public BufferedImage getSelectedImage(File selectedFile) throws IOException {
		BufferedImage selectedImage = null;
		try {
			selectedImage = ImageIO.read(selectedFile);
		} catch (IOException e) {
			throw new IOException("The selected image was not found!", e);
		}
		return selectedImage;
	}

	/**
	 * Getter for the image panel.
	 * 
	 * @return
	 */
	public GenericImagePanel getImagePanel() {
		return imagePanel;
	}

	/**
	 * Getter for the image.
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return imagePanel.getImage();
	}

	/**
	 * Getter for the image path.
	 * 
	 * @return
	 */
	public String getImagePath() {
		return imagePanel.getImagePath();
	}

}
