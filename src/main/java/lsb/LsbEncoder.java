package main.java.lsb;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LsbEncoder class.
 * 
 * @author Teodora C.
 */
public class LsbEncoder {


    private static final Log log = LogFactory.getLog(LsbEncoder.class);

	private final BufferedImage stegoImage;

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
	 * The constructor for LsbEncoder.
	 * 
	 * @param coverImage
	 * @param coverImagePath
	 * @param secretImage
	 * @param secretImagePath
	 * @param stegoImagePath
	 * @throws IOException
	 */
	public LsbEncoder(BufferedImage coverImage, String coverImagePath,
			BufferedImage secretImage, String secretImagePath,
			String stegoImagePath) throws IOException {

		StegoImage stegoCoverImage = new StegoImage(coverImage, coverImagePath);
		Message stegoMessageImage = new Message(secretImagePath);

		StegoPackage stegoPackage = new StegoPackage(stegoCoverImage,
				stegoMessageImage);

		LinkedHashMap<String, String> o = new LinkedHashMap<String, String>();
		o.put("startbits", "0");
		o.put("endbits", "0");

		StegoImage stegoImage = null;
		try {
			stegoImage = encode(stegoPackage, o);
		} catch (EncodingException e) {
			log.error("Could not encode message.", e);
			System.exit(1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Could not hide the secret message in the stego image!");
		}

		try {
			stegoImage.write(stegoImagePath);
		} catch (IllegalArgumentException e) {
			log.error("Could not write stego image.", e);
			System.exit(1);
		} catch (IOException e) {
			log.error("Could not write stego image.", e);
			System.exit(1);
		}

		this.stegoImage = stegoImage;
	}

	/**
	 * Getter for the stego image.
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return stegoImage;
	}

	/**
	 * Encodes an image with the given message. The start and end positions for
	 * possible hits should be zero based - i.e. the first bit is 0. This is
	 * counting from LSB to MSB (0 is least significant, 7 most). 555 and 565
	 * image formats should be in the range 0-4, other images should have 0-7.
	 * 
	 * @param p
	 *            StegoPackage containing Message to hide and cover image
	 * @param options
	 *            Options for this method containing startbits and endbits
	 * @return Image with hidden message
	 * @throws pl.edu.zut.wi.vsl.commons.steganography.EncodingException
	 */
	public StegoImage encode(StegoPackage p,
			LinkedHashMap<String, String> options) throws EncodingException {

		int startbits = Integer.parseInt(options.get("startbits"));
		int endbits = Integer.parseInt(options.get("endbits"));

		// check the bit ranges.
		if (startbits > 7 || startbits < 0) {
			throw new EncodingException("Start bit range not in range 0-7!");
		}
		if (endbits > 7 || endbits < 0) {
			throw new EncodingException("End bit range not in range 0-7!");
		}
		if (startbits > endbits) {
			throw new EncodingException("End bit range must be "
					+ "higher than start range!");
		}

		// assign in the start and end bits.
		startBits = startbits;
		endBits = endbits;
		bitCounter = 0;
		Message message = p.getMessage();
		StegoImage cimage = p.getImage();
		Shot sh;
		bitCounter = 0;
		layers = cimage.getLayerCount();
		bitsPerLayer = (endBits - startBits) + 1;
		maxBits = cimage.getHeight() * cimage.getWidth() * bitsPerLayer
				* layers;
		int messagesize = (int) message.getSize();

		// check the message fill actually fit
		if ((messagesize * 8) + 32 > maxBits) {
			JOptionPane.showMessageDialog(null,
					"Could not hide the secret message in the stego image: \n"
							+ "the message is to big for the cover image!");
			throw new EncodingException("Message is too big "
					+ "for this image. Maximum size for "
					+ "this configuration [B]: " + (maxBits - 32) / 8);
		}

		// put the size in the first 32 bits
		for (int i = 0; i < 32; i++) {
			// generate a valid shot
			sh = getNextShot(cimage.getWidth());
			// put in the next size bit...
			boolean bit = ((messagesize >> i) & 0x1) == 0x1;

			cimage.setPixelBit(sh.getX(), sh.getY(), sh.getLayer(),
					sh.getBitPosition(), bit);
		}

		// now we can start embedding the message into the cover
		while (!message.isFinished()) {
			sh = getNextShot(cimage.getWidth());

			boolean bit;
			try {
				bit = message.nextBit();
			} catch (IOException e) {
				JOptionPane
						.showMessageDialog(null,
								"Cound not hide the secert message in the stego image!");
				throw new EncodingException("IOException occured while "
						+ "fetching next bit of the message", e);
			}
			cimage.setPixelBit(sh.getX(), sh.getY(), sh.getLayer(),
					sh.getBitPosition(), bit);
		}
		log.info("Finished LSB encoding. Number of encoded bytes: "
				+ messagesize);
		// now the message is hidden inside the image.
		try {
			return new StegoImage(cimage.getImage(), cimage.getPath());
		} catch (IOException e) {
			throw new EncodingException("Could not create final image", e);
		}

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
