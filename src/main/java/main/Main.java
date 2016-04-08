package main.java.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import main.java.gui.MainPanel;


public class Main {

	/**
	 * Main method. Instantiates the GUI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager
							.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (ClassNotFoundException cnfe) {
					System.out.println("Could not change the look&feel");
				} catch (IllegalAccessException iae) {
					System.out.println("Could not change the look&feel");
				} catch (InstantiationException ie) {
					System.out.println("Could not change the look&feel");
				} catch (UnsupportedLookAndFeelException ulafe) {
					System.out.println("Could not change the look&feel");
				} finally {
					// If Nimbus is not available, you can set the GUI to
					// another look and feel.
					// Turn off metal's use of bold fonts
					UIManager.put("swing.boldMetal", Boolean.FALSE);
				}
				Main.createAndShowGUI();
			}
		});
	}

	/**
	 * Creates and shows the GUI.
	 */
	private static void createAndShowGUI() {

		// Create and set up the window.
		JFrame frame = new JFrame("LSB Steganography Tool");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		MainPanel mainPanel = new MainPanel(frame);
		mainPanel.setOpaque(true); // content panes must be opaque
		frame.getContentPane().add(mainPanel);
		frame.pack();

		// Center the window.
		frame.setLocationRelativeTo(null);
		centerWindow(frame);

		// Display the window.
		frame.setVisible(true);
	}

	// Center the window.
	private static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

}
