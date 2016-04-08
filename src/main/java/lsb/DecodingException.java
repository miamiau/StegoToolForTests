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
 * This class represents exception that may occur during decoding process. It
 * encapsulates message and <code>Throwable</code> cause of an error.
 * 
 * @author Michal Wegrzyn
 */
public class DecodingException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * This field holds the exception ex if the DecodingException(String s,
	 * Throwable ex) constructor was used to instantiate the object
	 */
	private Throwable ex;

	/**
	 * Constructs an {@code DecodingException} with no detail message.
	 */
	public DecodingException() {
		super();
	}

	/**
	 * Constructs an {@code DecodingException} with the specified detail
	 * message.
	 * 
	 * @param s
	 *            the detail message.
	 */
	public DecodingException(String s) {
		super(s);
	}

	/**
	 * Constructs a <code>ClassNotFoundException</code> with the specified
	 * detail message and optional exception that was raised while loading the
	 * class.
	 * 
	 * @param s
	 *            the detail message
	 * @param ex
	 *            the exception that was raised while loading the class
	 * @since 1.2
	 */
	public DecodingException(String s, Throwable ex) {
		super(s, null); // Disallow initCause
		this.ex = ex;
	}

	/**
	 * Returns the exception that was raised if an error occurred while
	 * attempting to decode the message. Otherwise, returns <tt>null</tt>.
	 * 
	 * @return the <code>Exception</code> that was raised while loading a class
	 */
	public Throwable getException() {
		return ex;
	}

	/**
	 * Returns the cause of this exception (the exception that was raised if an
	 * error occurred while attempting to decode message; otherwise
	 * <tt>null</tt>).
	 * 
	 * @return the cause of this exception.
	 */
	@Override
	public Throwable getCause() {
		return ex;
	}
}
