package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import main.java.gui.GenericImagePanel;
import main.java.gui.MainPanel;

/**
 * SaveImageAction class.
 * 
 * @author Teodora C.
 */
public class SaveImageAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private final MainPanel mainPanel;

	private String savedFilePath = "";

	/**
	 * The constructor for SaveImageAction.
	 * 
	 * @param frame
	 */
	public SaveImageAction(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		putValue(NAME, "Save image");
		putValue(SHORT_DESCRIPTION, "Save image");
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		saveImage();
	}

	/**
	 * Saves image from file chooser.
	 */
	public void saveImage() {
		GenericImagePanel currentImagePanel = mainPanel.getCurrentImagePanel();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save image:");
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(new File(mainPanel.getData().get(
				currentImagePanel.getImageType().getType())));
		fileChooser.setSelectedFile(new File(savedFilePath));
		int returnValue = fileChooser.showSaveDialog(mainPanel.getFrame());
		File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			savedFilePath = selectedFile.getAbsolutePath();
		}
	}

	/**
	 * Getter for the saved image path.
	 * 
	 * @return
	 */
	public String getSavedFilePath() {
		return savedFilePath;
	}

	/**
	 * Setter for the saved image path.
	 * 
	 * @param savedFilePath
	 */
	public void setSavedFilePath(String savedFilePath) {
		this.savedFilePath = savedFilePath;
	}

}
