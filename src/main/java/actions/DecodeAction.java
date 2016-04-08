package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.java.gui.MainPanel;
import main.java.lsb.LsbDecoder;

/**
 * DecodeAction class.
 * 
 * @author Teodora C.
 */
public class DecodeAction extends AbstractAction {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(DecodeAction.class);
    
    private final MainPanel mainPanel;
    
    /**
     * The constructor for DecodeAction.
     * 
     * @param mainPanel
     */
    public DecodeAction(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        putValue(NAME, "Decode");
        putValue(SHORT_DESCRIPTION, "Decode");
        putValue(ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        executeLsbDecodingSteganography();
    }
    
    /**
     * Decodes the secret image from the source image using LSB steganography.
     * 
     * @throws IOException
     */
    public void executeLsbDecodingSteganography() {
        try {
            BufferedImage sourceImage = mainPanel.getSourcePanel().getImage();
            String sourceImagePath = mainPanel.getSourcePanel().getImagePath();
            
            if (!isDecodingPossible(sourceImage, sourceImagePath)) {
                return;
            }
            
            String messageImagePath = sourceImagePath.replace("stegoimage",
                            "hiddenimage");
                            
            SaveImageAction saveImageAction = new SaveImageAction(mainPanel);
            saveImageAction.setSavedFilePath(messageImagePath);
            saveImageAction.actionPerformed(null);
            if (!saveImageAction.getSavedFilePath().equals(messageImagePath)) {
                messageImagePath = saveImageAction.getSavedFilePath();
            }
            
            UpdateDataAction updateDataAction = new UpdateDataAction(mainPanel,
                            mainPanel.getHiddenPanel().getImagePanel(),
                            messageImagePath);
            updateDataAction.actionPerformed(null);
            
            System.out.println("Executing LSB steganography decoding...");
            LsbDecoder lsbDecoder = new LsbDecoder(sourceImage,
                            sourceImagePath, messageImagePath);
            System.out.println("Finished LSB steganography decoding.");
            
            mainPanel.getHiddenPanel().getImagePanel()
                            .setImage(lsbDecoder.getImage(), messageImagePath);
        } catch (IOException e) {
            log.error("Could not execute the LSB decoding!", e);
        }
    }
    
    /**
     * Verifies if the decoding needs are satisfied: all images are valid; all
     * paths are valid.
     * 
     * @param sourceImage
     * @param sourceImagePath
     * @return
     */
    private boolean isDecodingPossible(BufferedImage sourceImage,
                    String sourceImagePath) {
        if (sourceImage == null) {
            JOptionPane.showMessageDialog(mainPanel,
                            "No source image was selected!");
            return false;
        }
        if (sourceImagePath == null) {
            JOptionPane.showMessageDialog(mainPanel,
                            "Source image path is invalid!");
            return false;
        }
        return true;
    }
    
}
