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

import java.io.Serializable;

/**
 * Custom class for carrying objects between instances of <code>VslModule</code>
 * and application modules.
 * 
 * @author Michal Wegrzyn
 */
public class StegoPackage implements Serializable {

	/** For serialization. */
	private static final long serialVersionUID = 385653455445839196L;

	private StegoImage image;
	private Message message;
	private String info;
	private String outputSubdirectory;

	public StegoPackage() {
	}

	public StegoPackage(StegoImage img) {
		image = img;
	}

	public StegoPackage(Message msg) {
		message = msg;
	}

	public StegoPackage(StegoImage img, Message msg) {
		image = img;
		message = msg;
	}

	public StegoPackage(StegoPackage sp) {
		if (sp.getMessage() != null) {
			message = new Message(sp.getMessage().getBytes().clone());
		}
		if (sp.getImage() != null) {
			image = new StegoImage(sp.getImage());
		}
		outputSubdirectory = sp.getOutputSubdirectory();
	}

	public StegoImage getImage() {
		return image;
	}

	public Message getMessage() {
		return message;
	}

	public String getInfo() {
		return info;
	}

	public void setImage(StegoImage img) {
		image = img;
	}

	public void setMessage(Message msg) {
		message = msg;
	}

	public void setInfo(String i) {
		info = i;
	}

	public String getOutputSubdirectory() {
		return outputSubdirectory;
	}

	public void setOutputSubdirectory(String outputSubdirectory) {
		this.outputSubdirectory = outputSubdirectory;
	}
}
