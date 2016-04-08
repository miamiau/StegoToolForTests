package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.java.gui.MainPanel;
import main.java.lsb.LsbEncoder;

/**
 * EncodeAction class.
 * 
 * @author Teodora C.
 */
public class EncodeAction extends AbstractAction {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(EncodeAction.class);
    
    private final MainPanel mainPanel;
    
    /**
     * The constructor for EncodeAction.
     * 
     * @param mainPanel
     */
    public EncodeAction(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        putValue(NAME, "Encode");
        putValue(SHORT_DESCRIPTION, "Encode");
        putValue(ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        executeLsbEncodingSteganography();
    }
    
    /**
     * Encodes the secret image into the cover image using LSB steganography.
     */
    public void executeLsbEncodingSteganography() {
        try {
            BufferedImage coverImage = mainPanel.getCoverPanel().getImage();
            BufferedImage secretImage = mainPanel.getSecretPanel().getImage();
            String coverImagePath = mainPanel.getCoverPanel().getImagePath();
            String secretImagePath = mainPanel.getSecretPanel().getImagePath();
            
            if (!isEncodingPossible(coverImage, coverImagePath, secretImage,
                            secretImagePath)) {
                return;
            }
            
            String[] secretImagePathArray = secretImagePath
                            .split(File.separator);
            String secretImageName = secretImagePathArray[secretImagePathArray.length - 1];
            String secretImageTitle = secretImageName.substring(0,
                            secretImageName.length() - 4);
            String stegoImagePath = coverImagePath.replace(".", "_"
                            + secretImageTitle + "_stegoimage.");
                            
            SaveImageAction saveImageAction = new SaveImageAction(mainPanel);
            saveImageAction.setSavedFilePath(stegoImagePath);
            saveImageAction.actionPerformed(null);
            if (!saveImageAction.getSavedFilePath().equals(stegoImagePath)) {
                stegoImagePath = saveImageAction.getSavedFilePath();
            }
            
            UpdateDataAction updateDataAction = new UpdateDataAction(mainPanel,
                            mainPanel.getStegoPanel().getImagePanel(), stegoImagePath);
            updateDataAction.actionPerformed(null);
            
            System.out.println("Executing LSB steganography encoding...");
            LsbEncoder lsbEncoder = new LsbEncoder(coverImage, coverImagePath,
                            secretImage, secretImagePath, stegoImagePath);
            System.out.println("Finished LSB steganography encoding.");
            
            mainPanel.getStegoPanel().getImagePanel()
                            .setImage(lsbEncoder.getImage(), stegoImagePath);
        } catch (IOException e) {
            log.error("Could not execute the LSB encoding!", e);
        }
    }
    
    /**
     * Verifies if the encoding needs are satisfied: all images are valid; all
     * paths are valid.
     * 
     * @param coverImage
     * @param coverImagePath
     * @param secretImage
     * @param secretImagePath
     * @return
     */
    private boolean isEncodingPossible(BufferedImage coverImage,
                    String coverImagePath, BufferedImage secretImage,
                    String secretImagePath) {
        if (coverImage == null) {
            JOptionPane.showMessageDialog(mainPanel,
                            "No cover image was selected!");
            return false;
        }
        if (coverImagePath == null) {
            JOptionPane.showMessageDialog(mainPanel,
                            "Cover image path is invalid!");
            return false;
        }
        if (secretImage == null) {
            JOptionPane.showMessageDialog(mainPanel,
                            "No secret image was selected!");
            return false;
        }
        if (secretImagePath == null) {
            JOptionPane.showMessageDialog(mainPanel,
                            "Secret image path is invalid!");
            return false;
        }
        return true;
    }
    
}
