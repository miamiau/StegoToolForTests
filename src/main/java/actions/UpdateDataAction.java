package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import main.java.gui.GenericImagePanel;
import main.java.gui.MainPanel;
import main.java.utils.Utils;

/**
 * UpdateDataAction class.
 * 
 * @author Teodora C.
 */
public class UpdateDataAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private final MainPanel mainPanel;
	private final GenericImagePanel imagePanel;
	private final String imagePath;

	/**
	 * The constructor for UpdateDataAction.
	 * 
	 * @param mainPanel
	 */
	public UpdateDataAction(MainPanel mainPanel, GenericImagePanel imagePanel,
			String imagePath) {
		this.mainPanel = mainPanel;
		this.imagePanel = imagePanel;
		this.imagePath = imagePath;
		putValue(NAME, "Decode");
		putValue(SHORT_DESCRIPTION, "Decode");
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		updatePersistanceData(imagePanel, imagePath);
	}

	/**
	 * Updates the persistence data and the xml.
	 * 
	 * @param currentImagePanel
	 * @param imagePath
	 */
	public void updatePersistanceData(GenericImagePanel currentImagePanel,
			String imagePath) {
		HashMap<String, String> data = mainPanel.getData();
		data.remove(currentImagePanel.getImageType().getType());
		data.put(currentImagePanel.getImageType().getType(), imagePath);
		Utils.writeXmlFileData(data);
	}

}
