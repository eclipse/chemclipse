/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

public interface IBigEndianArrayReader {

	/*
	 * Unsigned Big Endian
	 */
	short read1BUShortBE();

	int read2BUIntegerBE();

	long read4BULongBE();

	/**
	 * The sign bit will be masked.
	 * 
	 * @return long
	 */
	long read8BULongBE();

	/*
	 * Signed Big Endian
	 */
	short read1BShortBE();

	short read2BShortBE();

	int read2BIntegerBE();

	int read4BIntegerBE();

	long read4BLongBE();

	long read8BLongBE();

	/*
	 * Generic method
	 */
	/**
	 * The sign bit will be masked if 8 bytes are used.
	 * 
	 * @param numBytes
	 * @return long
	 */
	long readULongBE(int numBytes);

	/**
	 * The sign bit will be not masked if 8 bytes are used.
	 * But note, if less than 8 bytes are read, the sign bit will be not considered
	 * and the value will be read as unsigned.
	 * In such a case, use rather read2BShortBE() ...
	 * 
	 * @param numBytes
	 * @return long
	 */
	long readLongBE(int numBytes);

	/*
	 * Floating Point Big Endian
	 */
	float read4BFloatBE();

	double read8BUDoubleBE();

	double read8BDoubleBE();
}
