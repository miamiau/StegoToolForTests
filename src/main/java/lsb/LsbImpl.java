/*
 *    Virtual Steganographic Laboratory (VSL)
 *    Copyright (C) 2008-2011  M. Wegrzyn <bend-up@users.sourceforge.net>

 *    This file is part of Virtual Steganographic Laboratory (VSL).

 *    VSL is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.

 *    VSL is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with VSL.  If not, see <http://www.gnu.org/licenses/>.
 */
package main.java.lsb;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A blindhide way of hiding data.
 * <p>
 * Blindly hiding the data is the simplest way to hide data in a cover image.
 * The algorithm simply starts at point (0,0) and continues to write data to the
 * image until it runs out of data to write. This algorithm allows for a range
 * of bits to be selected, instead of just writing the least significant bits.
 * <p>
 * The original code of this technique was produced by Kathryn Hempstalk. Visit
 * her webpage for more information - {@link http
 * ://www.cs.waikato.ac.nz/~kah18/}
 * 
 * @author Michal Wegrzyn
 */
public class LsbImpl implements SteganographicTechnique {


    private static final Log log = LogFactory.getLog(LsbImpl.class);

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

	public LsbImpl(String[] args) {

		switch (args.length) {
		// encoding
		case 5:
			LinkedHashMap<String, String> o = new LinkedHashMap<String, String>();
			o.put("startbits", args[3]);
			o.put("endbits", args[4]);
			StegoImage si = null;
			Message msg = null;
			try {
				msg = new Message(args[1]);
			} catch (FileNotFoundException e) {
				log.error("Could not find message file.", e);
				// System.exit(1);
			} catch (IOException e) {
				log.error("Could not create message object.", e);
				// System.exit(1);

			}
			try {
				BufferedImage bi = ImageUtility.readImage(args[0]);
				si = new StegoImage(bi, args[0]);
			} catch (IllegalArgumentException e) {
				log.error("Could not create stegoimage.", e);
				// System.exit(1);
			} catch (NullPointerException e) {
				log.error("Could not create stegoimage.", e);
				// System.exit(1);
			} catch (IOException e) {
				log.error("Could not create stegoimage.", e);
				// System.exit(1);
			}

			StegoPackage p = new StegoPackage(si, msg);
			StegoImage result = null;

			try {
				result = encode(p, o);
			} catch (EncodingException e) {
				log.error("Could not encode message.", e);
				// System.exit(1);
			}
			try {
				result.write(args[2]);
			} catch (IllegalArgumentException e) {
				log.error("Could not write result image.", e);
				// System.exit(1);
			} catch (IOException e) {
				log.error("Could not write result image.", e);
				// System.exit(1);
			}

			break;
		// decoding
		case 4:
			LinkedHashMap<String, String> o2 = new LinkedHashMap<String, String>();
			o2.put("startbits", args[2]);
			o2.put("endbits", args[3]);
			StegoImage si2 = null;
			Message msg2 = null;

			try {
				BufferedImage bi = ImageUtility.readImage(args[0]);
				si2 = new StegoImage(bi, args[0]);
			} catch (IllegalArgumentException e) {
				log.error("Could not create stegoimage.", e);
				// System.exit(1);
			} catch (NullPointerException e) {
				log.error("Could not create stegoimage.", e);
				// System.exit(1);
			} catch (IOException e) {
				log.error("Could not create stegoimage.", e);
				// System.exit(1);
			}

			try {
				msg2 = decode(si2, o2);
			} catch (DecodingException e) {
				log.error("Could not decode message.", e);
				// System.exit(1);
			}
			try {
				msg2.writeBytesToFile(args[1]);
			} catch (IllegalArgumentException e) {
				log.error("Could not write result image.", e);
				// System.exit(1);
			} catch (IOException e) {
				log.error("Could not write result image.", e);
				// System.exit(1);
			}
			break;
		case 1:
			if (args[0].equals("--help") || args[0].equals("-help")
					|| args[0].equals("?") || args[0].equals("/?")) {
				printUsage();
			}
			break;
		default:
			System.out.println("Unsupported option");
			LsbImpl.printUsage();
			break;
		}
	}

	/**
	 * Prints usage to console.
	 */
	private static void printUsage() {
		System.out
				.println("Usage: \n"
						+ "Encoding: vsl-module-lsb <path to image> <path to message> \n"
						+ "                         <path to result image> <startbits> <endbits>\n"
						+ "Decoding: vsl-module-lsb <path to image> <path to result message> \n"
						+ "                         <startbits> <endbits> \n"
						+ "startbits - the start bit position for possible bits (0-7 integer) \n"
						+ "endbits - the end bit position for possible bits (0-7 integer)");
	}

	public LsbImpl() {
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
	@Override
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
	@Override
	@SuppressWarnings("rawtypes")
	public Message decode(StegoImage simage, LinkedHashMap options)
			throws DecodingException {

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