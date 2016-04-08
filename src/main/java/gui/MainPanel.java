package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.java.actions.DecodeAction;
import main.java.actions.EncodeAction;
import main.java.actions.SelectImageAction;
import main.java.utils.Utils;

/**
 * MainPanel class.
 * 
 * @author Teodora C.
 */
public class MainPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(MainPanel.class);
    
    private final JFrame frame;
    
    private GenericPanel coverPanel;
    private GenericPanel secretPanel;
    private GenericPanel stegoPanel;
    private GenericPanel sourcePanel;
    private GenericPanel hiddenPanel;
    
    private BufferedImage coverImage;
    private BufferedImage secretImage;
    private BufferedImage stegoImage;
    private BufferedImage sourceImage;
    private BufferedImage hiddenImage;
    
    private SelectImageAction selectImageAction;
    private EncodeAction encodeAction;
    private DecodeAction decodeAction;
    
    private GenericImagePanel currentImagePanel;
    
    private HashMap<String, String> data = null;
    
    /**
     * The constructor for MainPanel.
     */
    public MainPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        instantiateActions();
        data = Utils.readXmlFileData();
        if (data == null) {
            defineData();
            Utils.createXmlFileElements(data);
        }
        createComponents();
    }
    
    /**
     * Instantiates the possible actions.
     */
    private void instantiateActions() {
        selectImageAction = new SelectImageAction(this);
        encodeAction = new EncodeAction(this);
        decodeAction = new DecodeAction(this);
    }
    
    /**
     * Defines the persistence data that will be written in the xml. It contains
     * the image paths (also used as the last opened directories for each
     * image).
     */
    private void defineData() {
        data = new HashMap<String, String>();
        String noPath = "";
        data.put(ImageType.COVER.getType(), noPath);
        data.put(ImageType.SECRET.getType(), noPath);
        data.put(ImageType.STEGO.getType(), noPath);
        data.put(ImageType.SOURCE.getType(), noPath);
        data.put(ImageType.HIDDEN.getType(), noPath);
    }
    
    /**
     * Creates the main panel components.
     */
    private void createComponents() {
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.PAGE_AXIS));
        JScrollPane scrollPane = new JScrollPane(modePanel,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setName("Scroll Pane");
        scrollPane.setBackground(Color.BLACK);
        modePanel.add(createEncodePanel());
        modePanel.add(createDecodePanel());
        add(scrollPane);
    }
    
    /**
     * Creates an ImageIcon, or remains null if the path was invalid.
     * 
     * @param path
     * @return
     */
    private ImageIcon createImageIcon(String path) {
        URL imgURL = MainPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            log.error("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * Creates the encode mode panel.
     * 
     * @return
     */
    private JPanel createEncodePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        setPanelTitleIconBorder(panel, "Encode mode",
                        "/main/resources/icons/closed_lock.png");
        JPanel encodePanel = new JPanel();
        encodePanel.setLayout(new BoxLayout(encodePanel, BoxLayout.LINE_AXIS));
        coverPanel = new GenericPanel(this, ImageType.COVER, "Select ",
                        "Cover Image", selectImageAction, coverImage);
        secretPanel = new GenericPanel(this, ImageType.SECRET, "Select ",
                        "Secret Image", selectImageAction, secretImage);
        stegoPanel = new GenericPanel(this, ImageType.STEGO, "Generate ",
                        "Stego Image", encodeAction, stegoImage);
        encodePanel.add(coverPanel);
        encodePanel.add(secretPanel);
        encodePanel.add(stegoPanel);
        panel.add(encodePanel);
        return panel;
    }
    
    /**
     * Creates the decode mode panel.
     * 
     * @return
     */
    private JPanel createDecodePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        setPanelTitleIconBorder(panel, "Decode mode",
                        "/main/resources/icons/open_lock.png");
        JPanel decodePanel = new JPanel();
        decodePanel.setLayout(new BoxLayout(decodePanel, BoxLayout.LINE_AXIS));
        sourcePanel = new GenericPanel(this, ImageType.SOURCE, "Select ",
                        "Source Image", selectImageAction, sourceImage);
        hiddenPanel = new GenericPanel(this, ImageType.HIDDEN, "Recover ",
                        "Hidden Image", decodeAction, hiddenImage);
        decodePanel.add(sourcePanel);
        decodePanel.add(hiddenPanel);
        panel.add(decodePanel);
        return panel;
    }
    
    /**
     * Creates a border with icon and title for a panel.
     * 
     * @param panelTitle
     * @param iconPath
     * @param encodePanel2
     */
    private void setPanelTitleIconBorder(JPanel panel, String panelTitle,
                    String iconPath) {
        ImageIcon icon = createImageIcon(iconPath);
        JLabel label = new JLabel(panelTitle, icon, JLabel.LEADING);
        label.setForeground(Color.WHITE);
        GradientPanel gradientPanel = new GradientPanel(Color.BLACK);
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.add(label, BorderLayout.WEST);
        int borderOffset = 2;
        if (icon == null) {
            borderOffset += 1;
        }
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(borderOffset,
                        4, borderOffset, 1));
        panel.add(gradientPanel, BorderLayout.NORTH);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
    
    /**
     * Getter for the current image panel.
     * 
     * @return
     */
    public GenericImagePanel getCurrentImagePanel() {
        return currentImagePanel;
    }
    
    /**
     * Setter for the current image panel.
     * 
     * @param imagePanel
     */
    public void setCurrentImagePanel(GenericImagePanel imagePanel) {
        this.currentImagePanel = imagePanel;
    }
    
    /**
     * Getter for the cover image panel.
     * 
     * @return
     */
    public GenericPanel getCoverPanel() {
        return coverPanel;
    }
    
    /**
     * Getter for the secret image panel.
     * 
     * @return
     */
    public GenericPanel getSecretPanel() {
        return secretPanel;
    }
    
    /**
     * Getter for the stego image panel.
     * 
     * @return
     */
    public GenericPanel getStegoPanel() {
        return stegoPanel;
    }
    
    /**
     * Getter for the source image panel.
     * 
     * @return
     */
    public GenericPanel getSourcePanel() {
        return sourcePanel;
    }
    
    /**
     * Getter for the hidden image panel.
     * 
     * @return
     */
    public GenericPanel getHiddenPanel() {
        return hiddenPanel;
    }
    
    /**
     * Getter for the persistence data.
     * 
     * @return
     */
    public HashMap<String, String> getData() {
        return data;
    }
    
    /**
     * Getter for the main frame of the application.
     * 
     * @return
     */
    public JFrame getFrame() {
        return frame;
    }
    
}
