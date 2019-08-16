/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

public interface IArrayReader extends IBigEndianArrayReader, ILittleEndianArrayReader, IMiddleEndianArrayReader {

	/**
	 * Total number of bytes of the file.
	 * 
	 * @return long
	 */
	int getLength();

	int getPosition();

	void increasePosition();

	void decreasePosition();

	void resetPosition();

	/**
	 * Moves to the given position.
	 * The position must be > 0.
	 * 
	 * @param position
	 */
	void seek(int position);

	byte readByte();

	byte[] readBytes(int i);

	byte[] readBytes(byte[] prefix, int i);

	void skipBytes(int i);

	/**
	 * The first byte defines the length of the string.
	 * 
	 * @param readBytes
	 * @return String
	 */
	String readBytesAsStringWithLengthIndex(int readBytes);

	/**
	 * Reads the number of bytes as a String.
	 * 
	 * @param readBytes
	 * @return String
	 */
	String readBytesAsString(int readBytes);

	/**
	 * Reads the number of bytes as string.
	 * Each char follows a zero byte in the input stream.
	 * 
	 * 46 00 69 00 6E 00 -> FIN
	 * 
	 * @param readBytes
	 * @return String
	 */
	String readString(int readBytes);

	/**
	 * Returns the String converted with the given charsetName.
	 * If a failure occurs, "" will be returned.
	 * 
	 * US-ASCII
	 * ISO-8859-1
	 * UTF-8
	 * UTF-16BE
	 * UTF-16LE
	 * UTF-16
	 * 
	 * @param readBytes
	 * @param charsetName
	 * @return
	 */
	String readString(int readBytes, String charsetName);
}
