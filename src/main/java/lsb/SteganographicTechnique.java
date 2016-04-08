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

import java.util.LinkedHashMap;

/**
 * Interface representing steganalytic technique. A steganographic technique is
 * an algorithm that can be used to both encode and decode a message to/from a
 * graphical image.
 * 
 * @author Michal Wegrzyn
 */
public interface SteganographicTechnique {

	/**
	 * Method which hides message in the image.
	 * 
	 * @param pack
	 *            StegoPackage with message to hide and cover image
	 * @param options
	 *            parameters for method
	 * @return StegoImage image with embedded message
	 * @throws pl.ps.wi.vsl.commons.steganography.EncodingException
	 */
	public StegoImage encode(StegoPackage pack,
			LinkedHashMap<String, String> options) throws EncodingException;

	/**
	 * Method which decodes message from given cover image
	 * 
	 * @param img
	 *            StegoImage with message
	 * @param options
	 *            parameters for method
	 * @return Message decoded message from image
	 * @throws pl.ps.wi.vsl.commons.steganography.DecodingException
	 */
	public Message decode(StegoImage img, LinkedHashMap<String, String> options)
			throws DecodingException;

}
