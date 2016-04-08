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
import java.io.File;
import java.io.IOException;

/**
 * An image that has had steganography applied (or probably will had). Used to
 * carry image between modules and encapsulate images in VSL modules and
 * application.
 * 
 * @author Michal Wegrzyn
 */
public class StegoImage extends BufferedImage {

	/** Default path for this image */
	private static final String DEFAULT_PATH = "image.png";
	/** Path to original file on the disk */
	private String imagePath;
	/** Number of layers that this image has */
	private final int layers;
	/** Byte JPEG content of an image */
	private byte[] jpegBytes;

	public StegoImage(File image) throws IOException {
		this(image, image.getAbsolutePath());
	}

	public StegoImage(File image, String path) throws IOException {
		this(ImageUtility.readImage(image.getAbsolutePath()), path);
		String format = FileUtility.getFileFormat(image.getAbsolutePath());

		if ("jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format)) {
			jpegBytes = FileUtility.getBytesFromFile(image);
		} else {
			jpegBytes = ImageUtility.getBytes(this, "jpeg");
		}
	}

	public StegoImage(StegoImage source) {
		super(source.getColorModel(), source.copyData(null), source
				.isAlphaPremultiplied(), null);
		imagePath = source.getPath();
		layers = source.getLayerCount();
		jpegBytes = source.getJpegBytes();
	}

	/**
	 * Creates <code>StegoImage</code> instance of the given
	 * <code>BufferedImage</code>.
	 * 
	 * @param source
	 *            Source <code>BufferedImage</code>.
	 * @param path
	 *            Filepath to the original image on the HDD.
	 */
	public StegoImage(BufferedImage source, String path) throws IOException {

		super(source.getColorModel(), source.copyData(null), source
				.isAlphaPremultiplied(), null);
		imagePath = path == null ? DEFAULT_PATH : path;
		layers = getLayerCount();
		jpegBytes = ImageUtility.getBytes(this, "jpeg");
	}

	/**
	 * Writes an image out to disk.
	 * 
	 * @param formatname
	 *            The format type to output.
	 * @param output
	 *            The file to write to.
	 * @return True if image write successful, false if no appropriate writers
	 *         could be found.
	 * @throws IllegalArgumentException
	 *             If the arguments are null.
	 * @throws IOException
	 *             If an error occurs during writing.
	 */
	public void write(String filename) throws IllegalArgumentException,
			IOException {
		String format = FileUtility.getFileFormat(filename);

		if ("jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format)) {
			FileUtility.writeFile(jpegBytes, filename);
		}
		ImageUtility.write(this, filename);
	}

	/**
	 * Gets the number of layers the image has.
	 * <p>
	 * The number of layers is determined by the colour depth of the image. If a
	 * colour has a 24 bit colour depth, then the number of layers is 3, one for
	 * each of red, green and blue. In contrast, if the colour depth is 8, 1
	 * layer is returned.
	 * <p>
	 * To summarize, 24 bits = 3 layers, 16 bit = 3 layers, 8 bit = 1. Images
	 * which do not have a deep enough set of colours return 0.
	 * 
	 * @return The number of "layers" an image has.
	 */
	public int getLayerCount() {
		int type = getType();

		if (type == BufferedImage.TYPE_BYTE_BINARY) {
			// 1, 2 and 4 bit images
			return 0;
		} else if (type == BufferedImage.TYPE_BYTE_INDEXED
				|| type == BufferedImage.TYPE_BYTE_GRAY
				|| type == BufferedImage.TYPE_USHORT_GRAY) {
			// 8 bit images
			return 1;
		} else {
			// all other image types
			return 3;
		}
	}

	/**
	 * Returns specified bit plane of this image.
	 * 
	 * @param layer
	 *            Number of layer which has plane which should be returned
	 * @param plane
	 *            Number of plane in byte layer which should be returned
	 * @return Array with bit values of bit plane
	 */
	public boolean[][] getBitPlane(int layer, int plane) {
		boolean[][] bitPlane = new boolean[getWidth()][getHeight()];
		if (layers == 1) {
			if (layer != 0) {
				throw new IllegalArgumentException("Layer incorrect for this"
						+ "image type");
			}

			for (int i = 0; i < getWidth(); i++) {
				for (int j = 0; j < getHeight(); j++) {
					int pixel = getRaster().getSample(i, j, 0);
					byte b = (byte) pixel;
					bitPlane[i][j] = Message.getBit(b, plane);
				}
			}
		} else if (layers == 3) {
			if (layer < 0 || layer > 2) {
				throw new IllegalArgumentException("Layer incorrect for this"
						+ "image type");
			}
			for (int i = 0; i < getWidth(); i++) {
				for (int j = 0; j < getHeight(); j++) {
					int pixel = getRGB(i, j);
					byte b = 0;
					switch (layer) {
					case 0:
						b = (byte) ImageUtility.getBlue(pixel);
						break;
					case 1:
						b = (byte) ImageUtility.getGreen(pixel);
						break;
					case 2:
						b = (byte) ImageUtility.getRed(pixel);
						break;
					default:
						break;
					}
					bitPlane[i][j] = Message.getBit(b, plane);
				}
			}

		} else {
			throw new UnsupportedOperationException("Unsupported for this "
					+ "image type");
		}

		return bitPlane;
	}

	/**
	 * Gets the image.
	 * 
	 * @return The image.
	 */
	public BufferedImage getImage() {
		return toBufferedImage();
	}

	/**
	 * Gets a particular bit in the image, and puts it into the LSB of an
	 * integer.
	 * 
	 * @param xpos
	 *            The x position of the pixel on the image.
	 * @param ypos
	 *            The y position of the pixel on the image.
	 * @param layer
	 *            The layer (R,G,B) containing the bit.
	 * @param bitpos
	 *            The bit position (0 - LSB -> 7 - MSB).
	 * @return The bit at the given position, as the LSB of an integer.
	 */
	public int getPixelBit(int xpos, int ypos, int layer, int bitpos) {
		if (layers == 1) {
			int pixel = getRaster().getSample(xpos, ypos, 0);
			byte b = (byte) pixel;
			return Message.getBit(b, bitpos) ? 1 : 0;
		} else {
			int pixel = getRGB(xpos, ypos);
			int layerpos = (layer * 8) + bitpos;
			return ((pixel >> layerpos) & 0x1);
		}

	}

	/**
	 * Sets the pixel bit at the given location to the new value.
	 * 
	 * @param xpos
	 *            The x position of the pixel.
	 * @param ypos
	 *            The y position of the pixel.
	 * @param layer
	 *            The layer of the bit.
	 * @param bitpos
	 *            The position of the bit (0-7 or 0-4)
	 * @param newbit
	 *            The new bit for the pixel.
	 * @throws IllegalArgumentException
	 *             If the bit position is not in the right range, or the layer
	 *             is incorrect.
	 */
	public void setPixelBit(int xpos, int ypos, int layer, int bitpos,
			boolean newbit) throws IllegalArgumentException {

		// check the bit range
		if ((getType() == BufferedImage.TYPE_USHORT_555_RGB || getType() == BufferedImage.TYPE_USHORT_565_RGB)
				&& bitpos > 4) {
			throw new IllegalArgumentException("Bit position in incorrect "
					+ "position for image (0-4)!");
		}
		if (bitpos > 7 || bitpos < 0) {
			throw new IllegalArgumentException("Bit position in incorrect "
					+ "position for image (0-7)!");
		}
		// check layer
		if (layer > layers - 1 || layer < 0) {
			throw new IllegalArgumentException("Layer is incorrect "
					+ "for image type!");
		}

		if (layers == 3) {
			// RGB image

			// get the pixel we want to work on
			int pixel = getRGB(xpos, ypos);
			int newpixel;

			// hide the bit
			if (newbit) {
				newpixel = pixel | Integer.rotateLeft(1, bitpos + (layer * 8));
			} else {
				newpixel = pixel & ~Integer.rotateLeft(1, bitpos + (layer * 8));
			}

			// now set the pixel.
			setRGB(xpos, ypos, newpixel);

		} else if (layers == 1) {
			// image with 1 layer (8 bit)
			int pixel = getRaster().getSample(xpos, ypos, 0);
			byte b = (byte) pixel;
			b = BitUtility.setBit(b, bitpos, newbit);
			getRaster().setSample(xpos, ypos, 0, b);
		} else {
			throw new UnsupportedOperationException("Unsupported for this "
					+ "image type");
		}
	}

	/**
	 * Gets path for this image.
	 * 
	 * @return Filepath to this file on the disk.
	 */
	public String getPath() {
		return imagePath;
	}

	/**
	 * Sets path for this image.
	 * 
	 * @param path
	 *            filepath that will be used for this image.
	 */
	public void setPath(String path) {
		imagePath = path;
	}

	/**
	 * Converts this <code>StegoImage</code> to a <code>BufferedImage</code>.
	 * 
	 * @return <code>BuferedImage</code> representation of this image.
	 */
	public BufferedImage toBufferedImage() {
		return new BufferedImage(getColorModel(), copyData(null),
				isAlphaPremultiplied(), null);
	}

	/**
	 * @return the jpegBytes
	 */
	public byte[] getJpegBytes() {
		return jpegBytes;
	}

}