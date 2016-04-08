package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.java.gui.GenericImagePanel;
import main.java.gui.MainPanel;

/**
 * SelectImageAction class.
 * 
 * @author Teodora C.
 */
public class SelectImageAction extends AbstractAction {

	private static final long serialVersionUID = 1L;


    private static final Log log = LogFactory.getLog(SelectImageAction.class);

	private final MainPanel mainPanel;

	/**
	 * The constructor for SelectImageAction.
	 * 
	 * @param frame
	 */
	public SelectImageAction(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		putValue(NAME, "Select image");
		putValue(SHORT_DESCRIPTION, "Select image");
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		selectImage();
	}

	/**
	 * Selects image from file chooser.
	 */
	public void selectImage() {
		GenericImagePanel currentImagePanel = mainPanel.getCurrentImagePanel();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select image:");
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(new File(mainPanel.getData().get(
				currentImagePanel.getImageType().getType())));
		int returnValue = fileChooser.showOpenDialog(mainPanel.getFrame());
		File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				try {
					String imagePath = selectedFile.getPath();
					BufferedImage image = getImageFromFile(selectedFile);
					if (mainPanel.getCurrentImagePanel() != null) {
						mainPanel.getCurrentImagePanel().setImage(image,
								imagePath);
						UpdateDataAction updateDataAction = new UpdateDataAction(
								mainPanel, mainPanel.getCurrentImagePanel(),
								imagePath);
						updateDataAction.actionPerformed(null);
					}
				} catch (IOException e) {
					log.error("No image found!", e);
				}
			}
		}
	}

	/**
	 * Gets the image from the chosen file.
	 * 
	 * @param selectedFile
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage getImageFromFile(File selectedFile)
			throws IOException {
		BufferedImage selectedImage = null;
		try {
			selectedImage = ImageIO.read(selectedFile);
		} catch (IOException e) {
			throw new IOException("The image selected was not found!", e);
		}
		return selectedImage;
	}

}
