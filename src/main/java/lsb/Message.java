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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Class representing object to hide.
 * 
 * @author Michal Wegrzyn
 */
public class Message implements Serializable {

	/** For serialization. */
	private static final long serialVersionUID = -5847927342425612984L;
	/** The path to the message (on disk). */
	private String path;
	/** A count of the number of bits left in this byte. */
	private byte count;
	/** A buffer for the byte currently being read. */
	private final byte[] buffer;
	/** A buffer in int variable. */
	private int ibuffer;
	/** Tells had we get whole message already. */
	private boolean finished;
	/** Message in byte[]. */
	private byte[] msg;
	/** Position in byte[] of the message. */
	private int pos;
	/** Size of the message. */
	private long size;

	/**
	 * Creates a new message to embedd (that will be inserted into the image).
	 * 
	 * @param path
	 *            The path to the message on disk.
	 */
	public Message(String filePath) throws FileNotFoundException, IOException {

		this();
		count = 8;
		path = filePath;
		msg = FileUtility.getBytesFromFile(path);
		size = msg.length;

		FileInputStream msgFile = new FileInputStream(filePath);
		// get the first byte
		int status = msgFile.read(buffer);
		msgFile.close();
		if (status == -1) {
			throw new IOException("File is empty!");
		}
	}

	/**
	 * Creates a new message to retrieve (that will be retrieved from the
	 * image).
	 * 
	 * @param bufferSize
	 *            the size of message that will be retrieved in bytes.
	 */
	public Message(int bufferSize) throws IOException {
		this();
		count = 0;
		size = bufferSize;
		try {
			msg = new byte[bufferSize];
		} catch (OutOfMemoryError e) {
			throw new IOException(FileUtility.OUT_OF_MEMORY_MSG);
		}
	}

	/**
	 * Creates new Message from byte array.
	 * 
	 * @param content
	 *            content of a message in byte[].
	 */
	public Message(byte[] content) {
		this();
		size = content.length;
		msg = content;
	}

	/**
	 * Default contructor.
	 */
	public Message() {
		finished = false;
		buffer = new byte[1];
		ibuffer = 0;
		pos = 0;
	}

	/**
	 * Retrieves bit from byte at specified position.
	 * 
	 * @param data
	 *            Byte with bit to retrieve
	 * @param pos
	 *            position of wanted bit in data byte
	 * @return Bit from specified position
	 */
	public static boolean getBit(byte data, int pos) {
		int posBit = pos % 8;
		int valInt = (data >> posBit) & 0x0001;
		return valInt == 0 ? false : true;
	}

	/**
	 * Gets the next bit of the content.
	 * 
	 * @return next bit of the message.
	 */
	public boolean nextBit() throws IOException {
		// first check if we need to get another byte.
		if (finished) {
			throw new IOException("File reading has finished!");
		}
		// have byte, must manipulate to get bits
		boolean bit = (((buffer[0] >> (--count)) & 0x1) == 0x1);

		if (count == 0) {
			// get another byte
			if (++pos < msg.length) {
				buffer[0] = msg[pos];
				count = 8;
			} else {
				finished = true;
			}
		}

		return bit;
	}

	/**
	 * Sets the next bit of the output stream.
	 * <p>
	 * The bit will be set to 0 if bit is false, 1 if it is true.
	 * 
	 * @param bit
	 *            The value to set.
	 * @throws IOException
	 *             If the file is finished writing already, or there was an I/O
	 *             Error.
	 */
	public void setNext(boolean bit) throws IOException {

		// check file hasn't been closed
		if (finished) {
			throw new IOException("File has finished writing!");
			// set the new bit
		}

		int newbit = 0x0;
		if (bit) {
			newbit = 0x1;
		}

		/*
		 * change the buffer and increment count if the buffer is full, write it
		 * out
		 */
		ibuffer = ibuffer << 1 | newbit;
		if (++count == 8) {
			// get another byte
			if (pos < msg.length) {
				msg[pos++] = (byte) ibuffer;
				count = 0;
				ibuffer = 0;
			} else {
				finished = true;
			}
		}
	}

	/**
	 * Gives the size of the message.
	 * 
	 * @return The size of the message (in bytes).
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Return content of the message.
	 * 
	 * @return byte[] of the message.
	 */
	public byte[] getBytes() {
		return msg;
	}

	/**
	 * Sets content of the message.
	 * 
	 * @param byte[] with content to set.
	 */
	public void setBytes(byte[] content) {
		msg = content;
	}

	/**
	 * Tells had we get (read) whole message already. If there are any bytes to
	 * read from message, it will return false.
	 * 
	 * @return Whether we had finished reading whole message.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Writes content of the message to a file.
	 */
	public void writeBytesToFile(String path) throws IOException {
		FileOutputStream output = new FileOutputStream(path);
		output.write(msg);
		output.close();
	}

	/**
	 * Gives filepath to the stego object.
	 * 
	 * @return path - path to the message
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Prepares Message for next encoding.
	 */
	public void prepareMessage() {
		finished = false;
		buffer[0] = msg[0];
		ibuffer = 0;
		pos = 0;
		count = 8;
	}

}
