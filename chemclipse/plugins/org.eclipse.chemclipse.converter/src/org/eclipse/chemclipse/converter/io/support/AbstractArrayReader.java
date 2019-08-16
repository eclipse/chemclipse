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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.converter.io.streams.DataInputStream;
import org.eclipse.chemclipse.logging.core.Logger;

public abstract class AbstractArrayReader implements IArrayReader {

	private static final Logger logger = Logger.getLogger(AbstractArrayReader.class);
	//
	private int position;
	private byte[] data;
	private int length;
	private Map<Byte, String> charMap = new HashMap<>();

	public AbstractArrayReader(byte[] data) {
		initialize(data);
	}

	public AbstractArrayReader(File file) throws FileNotFoundException, IOException {
		byte[] data = getByteArrayFromFile(file);
		initialize(data);
	}

	private byte[] getByteArrayFromFile(File file) throws FileNotFoundException, IOException {

		int length = (int)file.length();
		byte[] data = new byte[length];
		DataInputStream is = new DataInputStream(file);
		is.read(data);
		is.close();
		return data;
	}

	@Override
	public int getLength() {

		return length;
	}

	private void initialize(byte[] data) {

		position = 0;
		this.data = data;
		this.length = data.length;
		//
		charMap.put((byte)-60, "Ä");
		charMap.put((byte)-42, "Ö");
		charMap.put((byte)-36, "Ü");
		charMap.put((byte)-28, "ä");
		charMap.put((byte)-10, "ö");
		charMap.put((byte)-4, "ü");
	}

	@Override
	public int getPosition() {

		return position;
	}

	@Override
	public void increasePosition() {

		position++;
	}

	@Override
	public void decreasePosition() {

		position--;
	}

	@Override
	public void resetPosition() {

		position = 0;
	}

	@Override
	public void seek(int position) {

		if(position > 0) {
			resetPosition();
			skipBytes(position);
		}
	}

	@Override
	public byte readByte() {

		return data[position++];
	}

	@Override
	public byte[] readBytes(int i) {

		return getByteArray(i);
	}

	@Override
	public byte[] readBytes(byte[] prefix, int i) {

		return getByteArray(prefix, i);
	}

	@Override
	public void skipBytes(int i) {

		position += i;
	}

	/*
	 * LITTLE ENDIAN------------------------------------------------------------
	 */
	@Override
	public short read1BUShortLE() {

		return (short)readULongLE(1);
	}

	@Override
	public int read2BUIntegerLE() {

		return (int)readULongLE(2);
	}

	@Override
	public long read4BULongLE() {

		return readULongLE(4);
	}

	@Override
	public long read8BULongLE() {

		return readULongLE(8);
	}

	@Override
	public float read4BUFloatLE() {

		return Float.intBitsToFloat((int)read4BULongLE());
	}

	@Override
	public short read1BShortLE() {

		return readByte();
	}

	@Override
	public short read2BShortLE() {

		return (short)readLongLE(2);
	}

	@Override
	public int read2BIntegerLE() {

		return read2BShortLE();
	}

	@Override
	public int read4BIntegerLE() {

		return (int)readLongLE(4);
	}

	@Override
	public long read4BLongLE() {

		return read4BIntegerLE();
	}

	@Override
	public long read8BLongLE() {

		return readLongLE(8);
	}

	@Override
	public float read4BFloatLE() {

		return Float.intBitsToFloat(read4BIntegerLE());
	}

	@Override
	public long readULongLE(int numBytes) {

		long value = readLongLE(numBytes);
		return value & 0x7FFFFFFFFFFFFFFFL;
	}

	@Override
	public long readLongLE(int numBytes) {

		assert numBytes <= 8 : "The number of bytes needs to be lower than 8";
		if(numBytes <= 0 || numBytes > 8) {
			return 0;
		}
		byte[] tmp = getByteArray(numBytes);
		long result = 0;
		int shift = 0;
		for(int i = 0; i < numBytes; i++) {
			result |= (tmp[i] & 0xFFL) << shift;
			shift += 8;
		}
		return result;
	}

	@Override
	public double read8BUDoubleLE() {

		return Double.longBitsToDouble(read8BULongLE());
	}

	@Override
	public double read8BDoubleLE() {

		return Double.longBitsToDouble(read8BLongLE());
	}

	/*
	 * BIG ENDIAN------------------------------------------------------------
	 */
	@Override
	public short read1BUShortBE() {

		return (short)readULongBE(1);
	}

	@Override
	public int read2BUIntegerBE() {

		return (int)readULongBE(2);
	}

	@Override
	public long read4BULongBE() {

		return readULongBE(4);
	}

	@Override
	public long read8BULongBE() {

		return readULongBE(8);
	}

	@Override
	public short read1BShortBE() {

		return readByte();
	}

	@Override
	public short read2BShortBE() {

		return (short)readLongBE(2);
	}

	@Override
	public int read2BIntegerBE() {

		return read2BShortBE();
	}

	@Override
	public int read4BIntegerBE() {

		return (int)readLongBE(4);
	}

	@Override
	public long read4BLongBE() {

		return read4BIntegerBE();
	}

	@Override
	public long read8BLongBE() {

		return readLongBE(8);
	}

	@Override
	public long readULongBE(int numBytes) {

		long value = readLongBE(numBytes);
		return value & 0x7FFFFFFFFFFFFFFFL;
	}

	@Override
	public long readLongBE(int numBytes) {

		assert numBytes <= 8 : "The number of bytes needs to be lower than 8";
		if(numBytes <= 0 || numBytes > 8) {
			return 0;
		}
		byte[] tmp = getByteArray(numBytes);
		long result = 0;
		int shift = (numBytes - 1) * 8;
		for(int i = 0; i < numBytes; i++) {
			result |= (tmp[i] & 0xFFL) << shift;
			shift -= 8;
		}
		return result;
	}

	@Override
	public float read4BFloatBE() {

		return Float.intBitsToFloat(read4BIntegerBE());
	}

	@Override
	public double read8BUDoubleBE() {

		return Double.longBitsToDouble(read8BULongBE());
	}

	@Override
	public double read8BDoubleBE() {

		return Double.longBitsToDouble(read8BLongBE());
	}

	/*
	 * MIDDLE ENDIAN------------------------------------------------------------
	 */
	/**
	 * Reads 4 bytes from the byte array as Middle Endian.<br/>
	 * A B C D -> B A D C
	 * 
	 * @return long
	 */
	@Override
	public int read4BUIntegerME() {

		byte[] tmp = getByteArray(4);
		return (int)(((tmp[0] & 0xFF) << 16) | ((tmp[1] & 0x7F) << 24) | (tmp[2] & 0xFF) | ((tmp[3] & 0xFF) << 8));
	}

	/**
	 * Reads 4 bytes from the byte array as Middle Endian.<br/>
	 * A B C D -> B A D C
	 * 
	 * @return long
	 */
	@Override
	public long read4BULongME() {

		byte[] tmp = getByteArray(4);
		return ((tmp[0] & 0xFFL) << 16) | ((tmp[1] & 0xFFL) << 24) | (tmp[2] & 0xFFL) | ((tmp[3] & 0xFFL) << 8);
	}

	// ------------------------------------------------------------------------------
	/**
	 * This method reads a given amount of bytes (in the way agilent has stored
	 * them) and returns the specific result. For Example: If you want to read
	 * the DataName from the binary file, you would call the method like this:
	 * getBytes(61); The Data is stored in the way, that the first byte is an
	 * value of the data length in the byte array. So, the method first reads
	 * one byte to determine the length of data and afterwards reads the
	 * specified 61 bytes and returns only a byte array of the determined
	 * length.
	 * 
	 * @param readBytes
	 * @return byte[]
	 */
	@Override
	public String readBytesAsStringWithLengthIndex(int readBytes) {

		int length;
		byte[] tmp = new byte[readBytes];
		/*
		 * The first byte defines the length of the string.
		 */
		length = data[position++];
		byte[] bytes = new byte[length];
		/*
		 * Reading the bytes from the array.
		 */
		for(int i = 0; i < readBytes; i++) {
			tmp[i] = data[position++];
		}
		/*
		 * Reading the bytes and copying only the length of the string from the
		 * array.
		 */
		for(int i = 0; i < length; i++) {
			bytes[i] = tmp[i];
		}
		//
		return getCorrectedString(bytes);
	}

	private String getCorrectedString(byte[] bytes) {

		StringBuilder builder = new StringBuilder();
		for(byte b : bytes) {
			if(charMap.containsKey(b)) {
				builder.append(charMap.get(b));
			} else {
				builder.append((char)b);
			}
		}
		//
		return builder.toString().trim();
	}

	@Override
	public String readBytesAsString(int readBytes) {

		return new String(getByteArray(readBytes));
	}

	private byte[] getByteArray(int count) {

		byte[] tmp = new byte[count];
		for(int i = 0; i < count; i++) {
			tmp[i] = data[position++];
		}
		return tmp;
	}

	private byte[] getByteArray(byte[] prefix, int count) {

		byte[] tmp = new byte[prefix.length + count];
		int i = 0;
		/*
		 * Prefix
		 */
		for(int j = 0; j < prefix.length; j++) {
			tmp[i++] = prefix[j];
		}
		/*
		 * Data
		 */
		for(int j = 0; j < count; j++) {
			tmp[i++] = data[position++];
		}
		return tmp;
	}

	@Override
	public String readString(int readBytes) {

		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= readBytes; i++) {
			int value = readByte();
			if(value > 0) {
				builder.append((char)value);
			}
		}
		return builder.toString();
	}

	@Override
	public String readString(int readBytes, String charsetName) {

		byte[] data = new byte[readBytes];
		for(int i = 0; i < readBytes; i++) {
			data[i] = readByte();
		}
		String result = "";
		try {
			result = new String(data, charsetName);
		} catch(UnsupportedEncodingException e) {
			logger.warn(e);
		}
		//
		return result;
	}
}
