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

/**
 * Useful bit utilities.
 * 
 * @author Michal Wegrzyn
 */
public class BitUtility {

	private BitUtility() {
	}

	/**
	 * Sets bit in byte at specified position.
	 * 
	 * @param data
	 *            Byte with bit to set.
	 * @param pos
	 *            Position of bit to set
	 * @param val
	 *            value which should be set.
	 */
	public static byte setBit(byte data, int pos, boolean b) {
		int val = b ? 1 : 0;
		int posBit = pos % 8;
		byte oldByte = data;
		oldByte = (byte) (((0xFF7F >> (8 - (posBit + 1))) & oldByte) & 0x00FF);
		byte newByte = (byte) ((val << posBit) | oldByte);
		return newByte;
	}

	public static byte[] intToByteArray(int value) {
		return new byte[] { (byte) ((value >>> 24) & 0xFF),
				(byte) ((value >>> 16) & 0xFF), (byte) ((value >>> 8) & 0xFF),
				(byte) (value & 0xFF) };
	}

	public static int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

}
