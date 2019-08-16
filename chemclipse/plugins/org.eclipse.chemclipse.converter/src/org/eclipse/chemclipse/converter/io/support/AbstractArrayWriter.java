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

public abstract class AbstractArrayWriter implements IArrayWriter {

	private int position;
	private byte[] data;

	public AbstractArrayWriter(byte[] data) {
		this.position = 0;
		this.data = data;
	}

	/**
	 * Writes the byte array to this.data at the actual position.
	 * 
	 * @param bytes
	 */
	@Override
	public void write(byte[] bytes) {

		for(int i = 0; i < bytes.length; i++) {
			data[position++] = bytes[i];
		}
	}

	@Override
	public void skipBytes(int bytes) {

		position += bytes;
	}

	/**
	 * Getting a byte array for a specific entry.<br/>
	 * The example has e.g. 1 + 19 chars. The first char defines the
	 * length of the string.<br/>
	 * So you can call the method:<br/>
	 * ... getBytes(20, chrom.getFileString);<br/>
	 * and get a byte array of the length 20.<br/>
	 * 
	 * @param writeBytes
	 * @param entry
	 * @return byte[]
	 */
	@Override
	public byte[] getBytesStringTerminated(int writeBytes, String entry) {

		/*
		 * 1 byte is used to store the length of the string.
		 * That's why the length must not exceed a length of 255.
		 */
		int maxLength = 255;
		if(writeBytes > maxLength) {
			writeBytes = maxLength;
		}
		//
		byte[] bytes = new byte[writeBytes];
		int endIndex = writeBytes - 1;
		if(endIndex > 0) {
			/*
			 * The string may not exceed a certain length.
			 */
			int length;
			if(endIndex > entry.length()) {
				length = entry.length();
			} else {
				length = entry.substring(0, endIndex).length();
			}
			//
			byte[] bytesLength;
			byte[] bytesEntry;
			/*
			 * Getting the byte arrays for the length an the entry
			 */
			bytesLength = get4BytesAsIntegerBigEndian(length);
			bytesEntry = entry.getBytes();
			bytes[0] = bytesLength[3];
			for(int i = 1; i <= length; i++) {
				bytes[i] = bytesEntry[i - 1];
			}
		}
		return bytes;
	}

	@Override
	public byte[] getBytesStringNullTerminated(int writeBytes, String entry) {

		writeBytes *= 2;
		byte[] bytes = new byte[writeBytes];
		byte[] bytesEntry = entry.getBytes();
		//
		int index = 0;
		for(int i = 0; i < bytesEntry.length; i++) {
			if(index < writeBytes) {
				bytes[index++] = bytesEntry[i];
				bytes[index++] = 0;
			}
		}
		return bytes;
	}

	@Override
	public void writeIntegerAsBigEndian(int value) {

		write(get4BytesAsIntegerBigEndian(value));
	}

	@Override
	public byte[] get2BytesAsShortBigEndian(int value) {

		byte[] bytes = new byte[2];
		for(int i = 0; i < 2; i++) {
			/*
			 * i = 0 - 0000|0000 >> 0000|0000 = 0 i = 1 - 0000|0001 >> 0000|1000
			 * = 8
			 */
			int shift = i << 3;
			bytes[1 - i] = (byte)((value & (0xff << shift)) >>> shift);
		}
		return bytes;
	}

	@Override
	public byte[] get4BytesAsIntegerBigEndian(int value) {

		byte[] bytes = new byte[4];
		for(int i = 0; i < 4; i++) {
			/*
			 * i = 0 - 0000|0000 >> 0000|0000 = 0 i = 1 - 0000|0001 >> 0000|1000
			 * = 8 i = 2 - 0000|0010 >> 0001|0000 = 16 i = 3 - 0000|0011 >>
			 * 0001|1000 = 24
			 */
			int shift = i << 3;
			bytes[3 - i] = (byte)((value & (0xff << shift)) >>> shift);
		}
		return bytes;
	}

	@Override
	public void write2BytesUnsignedIntegerLittleEndian(int value) {

		write(get2BytesLittleEndian(value));
	}

	@Override
	public void write4BytesUnsignedIntegerLittleEndian(int value) {

		write(get4BytesLittleEndian(value));
	}

	@Override
	public void write8BytesUnsignedLittleEndian(long value) {

		write(get8BytesLittleEndian(value));
	}

	@Override
	public byte[] get2BytesLittleEndian(int value) {

		byte[] bytes = new byte[2];
		for(int i = 0; i < 2; i++) {
			int shift = i * 8;
			bytes[i] = (byte)(value >>> shift);
		}
		return bytes;
	}

	@Override
	public byte[] get4BytesLittleEndian(int value) {

		byte[] bytes = new byte[4];
		for(int i = 0; i < 4; i++) {
			int shift = i * 8;
			bytes[i] = (byte)(value >>> shift);
		}
		return bytes;
	}

	@Override
	public byte[] get8BytesLittleEndian(long value) {

		byte[] bytes = new byte[8];
		for(int i = 0; i < 8; i++) {
			int shift = i * 8;
			bytes[i] = (byte)(value >>> shift);
		}
		return bytes;
	}
}
