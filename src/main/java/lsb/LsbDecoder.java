package main.java.lsb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LsbDecoder class.
 * 
 * @author Teodora C.
 */
public class LsbDecoder {


    private static final Log log = LogFactory.getLog(LsbDecoder.class);

	private final BufferedImage hiddenImage;

	/** The start range for writable bits. */
	private int startBits;
	/** The end range for writable bits. */
	private int endBits;
	/** Number of image layers. */
	private int layers;
	/** Counts the number of bits that have been written. */
	private long bitCounter;
	/** Number of bits hidden in one layer of the image */
	private int bitsPerLayer;
	/**
	 * Number of maximum bits that can be hidden within this image and with this
	 * configuration
	 */
	private int maxBits;

	/**
	 * The constructor for LsbDecoder.
	 * 
	 * @param sourceImage
	 * @param sourceImagePath
	 * @param hiddenImagePath
	 * @throws IOException
	 */
	public LsbDecoder(BufferedImage sourceImage, String sourceImagePath,
			String hiddenImagePath) throws IOException {

		StegoImage stegoSourceImage = new StegoImage(sourceImage,
				sourceImagePath);

		LinkedHashMap<String, String> o = new LinkedHashMap<String, String>();
		o.put("startbits", "0");
		o.put("endbits", "0");

		Message hiddenMessageImage = null;
		try {
			hiddenMessageImage = decode(stegoSourceImage, o);
		} catch (DecodingException e) {
			log.error("Could not decode message.", e);
			System.exit(1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"There was no message hidden in the image!");
		}

		try {
			hiddenMessageImage.writeBytesToFile(hiddenImagePath);
		} catch (IllegalArgumentException e) {
			log.error("Could not write stego image.", e);
			System.exit(1);
		} catch (IOException e) {
			log.error("Could not write stego image.", e);
			System.exit(1);
		}

		try {
			File hiddenImageFile = new File(hiddenImagePath);
			hiddenImage = ImageIO.read(hiddenImageFile);
		} catch (IOException e) {
			throw new IOException("The selected image was not found!", e);
		}
	}

	/**
	 * Getter for the hidden image.
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return hiddenImage;
	}

	/**
	 * Decodes message from given stego image.
	 * 
	 * @param simage
	 *            Image with hidden content.
	 * @param options
	 *            Options for this LSB technique, containing startbits and
	 *            endbits
	 * @return Message which was hidden in given image
	 * @throws pl.edu.zut.wi.vsl.commons.steganography.DecodingException
	 */
	public Message decode(StegoImage simage,
			LinkedHashMap<String, String> options) throws DecodingException {

		int startbits = Integer.parseInt(options.get("startbits").toString());
		int endbits = Integer.parseInt(options.get("endbits").toString());

		// check the bit ranges.
		if (startbits > 7 || startbits < 0) {
			throw new DecodingException("Start bit range not in range 0-7!");
		}
		if (endbits > 7 || endbits < 0) {
			throw new DecodingException("End bit range not in range 0-7!");
		}
		if (startbits > endbits) {
			throw new DecodingException("End bit range must be "
					+ "higher than start range!");
		}

		// assign in the start and end bits.
		startBits = startbits;
		endBits = endbits;
		layers = simage.getLayerCount();
		bitsPerLayer = (endBits - startBits) + 1;
		maxBits = simage.getHeight() * simage.getWidth() * bitsPerLayer
				* layers;
		Shot sh;
		int size = 0;
		bitCounter = 0;

		// get the size - in the first 32 hidden bits
		for (int i = 0; i < 32; i++) {

			sh = getNextShot(simage.getWidth());
			int bit = simage.getPixelBit(sh.getX(), sh.getY(), sh.getLayer(),
					sh.getBitPosition());
			size = size << 1 | bit;
		}

		// reverse it as it was retrieved backwards
		int size2 = 0;
		for (int j = 0; j < 32; j++) {
			size2 = size2 << 1 | ((size >> j) & 0x1);
		}

		if (size2 * 8 + 32 > maxBits) {
			throw new DecodingException("Recovered message size is wrong: "
					+ size2 + ". Parameters are wrong or image was distorted.");
		}

		Message rmess = null;
		log.info("Decoding, size of message [B]:" + size2);
		try {
			rmess = new Message(size2);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"There was no message hidden in the image!");
			throw new DecodingException("Could not create message", e);
		}

		// multiply by 8 to get then number of bits
		size2 = size2 * 8;

		// make sure that the message isn't bigger than it's supposed to be
		long imagespace = (((simage.getWidth() * simage.getHeight()) * simage
				.getLayerCount()) * ((endBits - startBits) + 1));

		if (size2 >= imagespace || size2 < 0) {
			throw new DecodingException("Message is bigger than "
					+ "supposed to be");
		}
		// start retrieving and writing out the message
		for (int k = 0; k < size2; k++) {

			sh = getNextShot(simage.getWidth());
			try {
				rmess.setNext((simage.getPixelBit(sh.getX(), sh.getY(),
						sh.getLayer(), sh.getBitPosition())) == 0x1);
			} catch (IOException e) {
				throw new DecodingException("IOException occured during "
						+ "retrieving and writing out the message", e);
			}

		}

		return rmess;
	}

	/**
	 * Gets the next shot on the image.
	 * 
	 * @param width
	 *            The width of the image.
	 * @return The next shot to make.
	 */
	private Shot getNextShot(int width) {
		if (bitCounter > maxBits) {
			return null;
		}
		int plane = startBits + ((int) bitCounter / maxBits);
		int rangeupto = (int) (bitCounter % (bitsPerLayer * layers));
		int tmp = (int) (bitCounter - rangeupto) / (bitsPerLayer * layers);
		int xrow = tmp % width;
		int yrow = (tmp - xrow) / width;

		bitCounter++;
		return new Shot(xrow, yrow, plane + rangeupto % bitsPerLayer, rangeupto
				/ bitsPerLayer);

	}

}
