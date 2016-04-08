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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

/**
 * Class containing mostly static methods to do some image manipulations.
 * 
 * @author Michal Wegrzyn
 */
public class ImageUtility {

	public static final int HUE = 0;
	public static final int SATURATION = 1;
	public static final int VALUE = 2;
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int Y = 0;
	public static final int CB = 1;
	public static final int CR = 2;

	/**
	 * Writes given <code>BufferedImage</code> instance to a file.
	 * 
	 * @param image
	 *            Image which should be saved.
	 * @param filename
	 *            Path to the file to which image should be saved.
	 * @throws java.io.IOException
	 *             If could not save image
	 */
	public static void write(BufferedImage image, String filename)
			throws IOException {
		String format;
		int idx = filename.lastIndexOf('.');
		if (idx >= 0) {
			format = filename.substring(idx + 1).toLowerCase();
		} else {
			// if cannot determine fileformat, use png
			format = "png";
		}

		File file = new File(filename);
		if (format.equals("png")) {
			// write PNG
			ImageIO.write(image, format, file);

		} else {
			if (hasAlpha(image)) {
				/*
				 * cannot write file that is not a PNG with alpha -> convert it
				 * to the image without alpha
				 */
				BufferedImage img = new BufferedImage(image.getWidth(null),
						image.getHeight(null), BufferedImage.TYPE_INT_RGB);
				// Copy image to buffered image
				Graphics g = img.createGraphics();
				// Paint the image onto the buffered image
				g.drawImage(image, 0, 0, null);
				g.dispose();
				if (format.equals("jpg") || format.equals("jpeg")) {
					writeJPEG(img, filename, 1.0f);
				} else {
					ImageIO.write(img, format, file);
				}
			} else {
				if (format.equals("jpg") || format.equals("jpeg")) {
					writeJPEG(image, filename, 1.0f);
				} else {
					ImageIO.write(image, format, file);
				}
			}

		}
	}

	public static byte[] getBytes(BufferedImage image, String format)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);

		ImageIO.write(image, format, baos);
		baos.flush();

		return baos.toByteArray();
	}

	/**
	 * Writes out given image to a jpeg file.
	 * 
	 * @param input
	 *            Image that should be saved.
	 * @param name
	 *            Filepath to the file in which image should be saved.
	 * @param quality
	 *            Quality factor of JPEG compression. It is a value between
	 *            <code>0</code> and <code>1</code>.
	 *            <p>
	 *            A compression quality setting of 0.0 is most generically
	 *            interpreted as "high compression is important," while a
	 *            setting of 1.0 is most generically interpreted as "high image
	 *            quality is important."
	 * @throws java.io.IOException
	 *             If could not save file
	 */
	@SuppressWarnings("rawtypes")
	public static void writeJPEG(BufferedImage input, String name, float quality)
			throws IOException {
		Iterator iter = ImageIO.getImageWritersByFormatName("JPG");
		if (iter.hasNext()) {
			ImageWriter writer = (ImageWriter) iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(quality);
			File outFile = new File(name);
			FileImageOutputStream output = new FileImageOutputStream(outFile);
			writer.setOutput(output);
			IIOImage image = new IIOImage(input, null, null);
			writer.write(null, image, iwp);
			output.close();
		}
	}

	/**
	 * Returns instance of <code>BufferedImage</code> read from disk.
	 * 
	 * @param filename
	 *            Filepath to image which should be loaded.
	 * @return Loaded image.
	 * @throws java.io.IOException
	 *             If could not load image.
	 */
	public static BufferedImage readImage(String filename) throws IOException {

		File f = new File(filename);
		return ImageIO.read(f);
	}

	/**
	 * Returns instance of <code>BufferedImage</code> read from byte array.
	 * 
	 * @param bytes
	 *            byte array containing image which should be loaded
	 * @return Loaded image
	 * @throws IOException
	 */
	public static BufferedImage readImage(byte[] bytes) throws IOException {

		InputStream in = new ByteArrayInputStream(bytes);
		return ImageIO.read(in);
	}

	/**
	 * Makes a copy of the given <code>BufferedImage</code>.
	 * 
	 * @param source
	 *            Image which should be copied.
	 * @return copy of the given image
	 */
	public static BufferedImage copy(BufferedImage source) {
		WritableRaster raster = source.copyData(null);
		BufferedImage copy = new BufferedImage(source.getColorModel(), raster,
				source.isAlphaPremultiplied(), null);
		return copy;
	}

	/**
	 * Gets the red content of a pixel.
	 * 
	 * @param pixel
	 *            The pixel to get the red content of.
	 * @return The red content of the pixel.
	 */
	public static int getRed(int pixel) {
		return ((pixel >> 16) & 0xff);
	}

	/**
	 * Gets the green content of a pixel.
	 * 
	 * @param pixel
	 *            The pixel to get the green content of.
	 * @return The green content of the pixel.
	 */
	public static int getGreen(int pixel) {
		return ((pixel >> 8) & 0xff);
	}

	/**
	 * Gets the blue content of a pixel.
	 * 
	 * @param pixel
	 *            The pixel to get the blue content of.
	 * @return The blue content of the pixel.
	 */
	public static int getBlue(int pixel) {
		return (pixel & 0xff);
	}

	/**
	 * Clamps given value to 0-255 value.
	 * 
	 * @param value
	 *            Value to clamp
	 * @return Number from 0 to 255
	 */
	public static int clamp(int value) {
		return value > 255 ? 255 : value < 0 ? 0 : value;
	}

	/**
	 * Converts one RGB pixel to HSV color space.
	 * 
	 * @param r
	 *            Red component value from pixel to convert
	 * @param g
	 *            Green component value from pixel to convert
	 * @param b
	 *            Blue component value from pixel to convert
	 * @param hsv
	 *            Hue, Saturation and Value values for given pixel.
	 */
	public static double[] rgb2hsv(int red, int green, int blue) {

		double r = red;
		double g = green;
		double b = blue;

		double h;
		double s;
		double v;

		double[] hsv = new double[] { 0, 0, 0 };
		double min = Math.min(r, g);
		min = Math.min(min, b);
		double max = Math.max(r, g);
		max = Math.max(max, b);

		v = max;

		double delta = max - min;

		if (max != 0) {
			s = delta / max;
		} else {
			h = s = v = 0;
			hsv[0] = h;
			hsv[1] = s;
			hsv[2] = v;
			return hsv;
		}

		if (r == max) {
			// between yellow & magenta
			h = delta == 0 ? 0 : (g - b) / delta;
		} else if (g == max) {
			// between cyan & yellow
			h = delta == 0 ? 0 : 2 + (b - r) / delta;
		} else {
			// between magenta & cyan
			h = delta == 0 ? 0 : 4 + (r - g) / delta;
		}
		h = h * 60;
		if (h < 0) {
			h = h + 360;
		}

		hsv[ImageUtility.HUE] = h;
		hsv[ImageUtility.SATURATION] = s;
		hsv[ImageUtility.VALUE] = v;

		return hsv;
	}

	/**
	 * Converts hue, saturation and value values to a RGB pixel.
	 * 
	 * @param h
	 *            hue
	 * @param s
	 *            saturation
	 * @param v
	 *            value
	 * @return RGB pixel representation of the given HSV values.
	 */
	public static int[] hsv2rgb(double h, double s, double v) {

		int[] rgb = new int[] { 0, 0, 0 };
		double r;
		double g;
		double b;

		// black
		if (s == 0) {
			r = v;
			g = v;
			b = v;
		} else {
			double var_h = h / 60;
			double var_i = Math.floor(var_h);
			double var_1 = v * (1 - s);
			double var_2 = v * (1 - s * (var_h - var_i));
			double var_3 = v * (1 - s * (1 - (var_h - var_i)));

			if (var_i == 0) {
				r = v;
				g = var_3;
				b = var_1;
			} else if (var_i == 1) {
				r = var_2;
				g = v;
				b = var_1;
			} else if (var_i == 2) {
				r = var_1;
				g = v;
				b = var_3;
			} else if (var_i == 3) {
				r = var_1;
				g = var_2;
				b = v;
			} else if (var_i == 4) {
				r = var_3;
				g = var_1;
				b = v;
			} else {
				r = v;
				g = var_1;
				b = var_2;
			}
		}

		rgb[ImageUtility.RED] = (int) r;// Math.floor(r * 255);
		rgb[ImageUtility.GREEN] = (int) g;// Math.floor(g * 255);
		rgb[ImageUtility.BLUE] = (int) b;// Math.floor(b * 255);

		return rgb;
	}

	/**
	 * Converts RGB values into YCbCr values.
	 * 
	 * @param red
	 *            Red component value
	 * @param green
	 *            Green component value
	 * @param blue
	 *            Blue component value
	 * @return YCbCr representation of the given RGB values.
	 */
	public static double[] rgb2ycbcr(int red, int green, int blue) {
		double y, cb, cr;
		double[] ycbcr = new double[3];
		double r = red;
		double g = green;
		double b = blue;

		y = 16 + 1.0d / 256 * (65.738 * r + 129.057 * g + 25.064 * b);
		cb = 128 + 1.0d / 256 * (-37.945 * r - 74.494 * g + 112.439 * b);
		cr = 128 + 1.0d / 256 * (112.439 * r - 94.154 * g - 18.285 * b);
		ycbcr[ImageUtility.Y] = y;
		ycbcr[ImageUtility.CB] = cb;
		ycbcr[ImageUtility.CR] = cr;

		return ycbcr;
	}

	/**
	 * Converts YCbCr values into RGB values.
	 * 
	 * @param Y
	 *            Luma
	 * @param Cb
	 *            Blue difference
	 * @param Cr
	 *            Red difference
	 * @return RGB representation of the given YCbCr values.
	 */
	public static int[] ycbcr2rgb(double Y, double Cb, double Cr) {
		int[] rgb = new int[3];
		int r, g, b;

		r = (int) ((298.082 * Y + 408.583 * Cr) / 256 - 222.921);
		g = (int) ((298.082 * Y - 100.291 * Cb - 208.120 * Cr) / 256 + 135.576);
		b = (int) ((298.082 * Y + 516.412 * Cb) / 256 - 276.836);

		rgb[ImageUtility.RED] = r;
		rgb[ImageUtility.GREEN] = g;
		rgb[ImageUtility.BLUE] = b;

		return rgb;
	}

	/**
	 * Determines whether given image has alpha layer.
	 * 
	 * @param image
	 *            Image to examine if it has alpha
	 * @return <code>true</code> if image has alpha, otherwise
	 *         <code>false</code>.
	 */
	public static boolean hasAlpha(Image image) {
		/* If buffered image, the color model is readily available */
		if (image instanceof BufferedImage) {
			return ((BufferedImage) image).getColorModel().hasAlpha();
		}

		/*
		 * Use a pixel grabber to retrieve the image's color model; grabbing a
		 * single pixel is usually sufficient
		 */
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		/* Get the image's color model */
		return pg.getColorModel().hasAlpha();
	}

	/**
	 * Gets raw pixels rectangle data from image as int[].
	 * 
	 * @param img
	 *            Source image
	 * @param x
	 *            The X coordinate of the upper-left pixel location
	 * @param y
	 *            The Y coordinate of the upper-left pixel location
	 * @param w
	 *            Width of the pixel rectangle
	 * @param h
	 *            Height of the pixel rectangle
	 * @param pixels
	 *            reference to object that will hold returned data
	 * @return An object reference to an array with the requested pixel data.
	 */
	public static int[] getPixels(BufferedImage img, int x, int y, int w,
			int h, int[] pixels) {
		if (w == 0 || h == 0) {
			return new int[0];
		}
		if (pixels == null) {
			pixels = new int[w * h];
		} else if (pixels.length < w * h) {
			throw new IllegalArgumentException(
					"pixels array must have a length >= w*h");
		}

		int imageType = img.getType();
		if (imageType == BufferedImage.TYPE_INT_ARGB
				|| imageType == BufferedImage.TYPE_INT_RGB) {
			/* we know that data are integers - use faster method */
			Raster raster = img.getRaster();
			return (int[]) raster.getDataElements(x, y, w, h, pixels);
		} else if (imageType == BufferedImage.TYPE_BYTE_INDEXED
				|| imageType == BufferedImage.TYPE_BYTE_GRAY
				|| imageType == BufferedImage.TYPE_USHORT_GRAY) {
			/* gray image */
			return img.getRaster().getSamples(x, y, w, h, 0, pixels);

		} else {
			/*
			 * do not know if pixels are integers use slower method with
			 * conversion
			 */
			return img.getRGB(x, y, w, h, pixels, 0, w);
		}
	}

	/**
	 * Sets the data for a rectangle of pixels from an int array.
	 * 
	 * @param img
	 *            Image to modify
	 * @param x
	 *            The X coordinate of the upper-left pixel location
	 * @param y
	 *            The Y coordinate of the upper-left pixel location
	 * @param w
	 *            Width of the pixel rectangle
	 * @param h
	 *            Height of the pixel rectangle
	 * @param pixels
	 *            reference to object that holds data which has to be set
	 */
	public static void setPixels(BufferedImage img, int x, int y, int w, int h,
			int[] pixels) {
		if (pixels == null || w == 0 || h == 0) {
			return;
		} else if (pixels.length < w * h) {
			throw new IllegalArgumentException(
					"pixels array must have a length >= w*h");
		}

		int imageType = img.getType();
		if (imageType == BufferedImage.TYPE_INT_ARGB
				|| imageType == BufferedImage.TYPE_INT_RGB) {
			/* we know that data are integers - use faster method */
			WritableRaster raster = img.getRaster();
			raster.setDataElements(x, y, w, h, pixels);
		} else if (imageType == BufferedImage.TYPE_BYTE_INDEXED
				|| imageType == BufferedImage.TYPE_BYTE_GRAY
				|| imageType == BufferedImage.TYPE_USHORT_GRAY) {
			/* gray image */
			img.getRaster().setSamples(x, y, w, h, 0, pixels);

		} else {
			/*
			 * do not know if pixels are integers use slower method with
			 * conversion
			 */
			img.setRGB(x, y, w, h, pixels, 0, w);
		}
	}
}