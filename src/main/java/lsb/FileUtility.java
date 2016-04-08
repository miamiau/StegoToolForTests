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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility for performing all file related operations.
 * 
 * @author Michal Wegrzyn
 */
public class FileUtility {

	/** Out of memory error message. */
	public static final String OUT_OF_MEMORY_MSG = "Out of memory - there is no more heap space that"
			+ " can currently\nbe reclaimed, and either the operating"
			+ " system cannot provide\nany more memory to the JVM or"
			+ " you have reached the JVM upper\nmemory bound.\n"
			+ " In the second case, you may want to specify the Java"
			+ " heap space.\nFor example, if you want to specify"
			+ " an initial Java heap \nsize of 50 MB and a maximum "
			+ "Java heap size of 512 MB, \nyou would start VSL as:\n"
			+ "java -Xms50M -Xmx512M -jar <jar name>.jar";

	// Hide contructor
	private FileUtility() {
	};

	/**
	 * Returns directory of the given file.
	 * 
	 * @param filename
	 *            Name of file which directory should be returned
	 * @return String representation of path to directory which contains given
	 *         file.
	 */
	public static String getDirectory(String filename) {
		if (filename == null) {
			return null;
		}
		if (new File(filename).isDirectory()) {
			return filename;
		}

		int endIndex = filename.lastIndexOf(File.separator);

		if (endIndex > 0) {
			return filename.substring(0, endIndex);
		} else {
			return "";
		}
	}

	/**
	 * Returns unique filename for a file based on given filename.
	 * 
	 * @param filepath
	 *            Absolute filepath to a file that needs to be saved.
	 * @return filepath with filename that does not exist in destination
	 *         directory
	 */
	public static String getUniqueFilename(String filepath) {
		String outputDirectory = getDirectory(filepath);
		String name = getFileName(filepath);
		String outputName = getFileNameWithoutFormat(name);
		String format = getFileFormat(name);

		File tmp = outputDirectory.equals("") ? new File(name) : new File(
				outputDirectory + File.separator + name);
		// make sure that file with the same name does not exists
		int i = 1;
		while (tmp.exists()) {
			tmp = outputDirectory.equals("") ? new File(outputName + "_(" + i
					+ ")" + (format == null ? "" : "." + format)) : new File(
					outputDirectory + File.separator + outputName + "_(" + i
							+ ")" + (format == null ? "" : "." + format));
			i++;
		}

		return tmp.getAbsolutePath();
	}

	/**
	 * Return format of given file
	 * 
	 * @param filename
	 * @return format of given file. <br/>
	 *         null if file does not have specified format
	 */
	public static String getFileFormat(String filename) {
		if (filename == null) {
			return null;
		}
		String format = null;
		int beginIndex = filename.lastIndexOf(".");
		if (beginIndex > 0) {
			format = filename.substring(beginIndex + 1, filename.length());
		}
		return format;
	}

	/**
	 * Gives name of file without its format.
	 * 
	 * @param name
	 *            - Name of the file
	 * @return name without file format
	 */
	public static String getFileNameWithoutFormat(String name) {
		int idx = name.lastIndexOf(".");
		if (idx < 0) {
			return name;
		} else {
			return name.substring(0, idx);
		}
	}

	/**
	 * Returns file name from given path.
	 * 
	 * @param path
	 *            - path to the filename
	 * @return - filename
	 */
	public static String getFileName(String path) {
		int slInd = path.lastIndexOf(File.separator);
		if (slInd != -1) {
			return path.substring(slInd + 1);
		} else {
			return path;
		}
	}

	/**
	 * Writes byte array to file. If files with gives filename exists, appends
	 * number to filename, so existing files can not be overwritten with this
	 * method.
	 * 
	 * @param bytes
	 *            bytes which should be saved to a file
	 * @param path
	 *            path to a result file
	 * @throws IOException
	 */
	public static File writeFile(byte[] bytes, String path) throws IOException {
		path = FileUtility.getUniqueFilename(path);
		FileOutputStream output = new FileOutputStream(path);

		output.write(bytes);
		output.close();

		return new File(path);
	}

	/**
	 * Returns the contents of the file in a byte array.
	 * 
	 * @return content of the file in byte array.
	 */
	public static byte[] getBytesFromFile(String path) throws IOException {
		File file = new File(path);
		return FileUtility.getBytesFromFile(file);
	}

	/**
	 * Returns the contents of the file in a byte array.
	 * 
	 * @return content of the file in byte array.
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {

		InputStream is = new FileInputStream(file);

		/* Get the size of the file */
		long length = file.length();

		/*
		 * Cannot create an array using a long type. It needs to be an int type.
		 * Before converting to an int type, check to ensure that file is not
		 * larger than Integer.MAX_VALUE.
		 */
		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("Cannot create message - file is to large");
		}

		byte[] bytes;
		try {
			// Create the byte array to hold the data
			bytes = new byte[(int) length];
		} catch (OutOfMemoryError e) {
			throw new IOException(OUT_OF_MEMORY_MSG);
		}

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
}
