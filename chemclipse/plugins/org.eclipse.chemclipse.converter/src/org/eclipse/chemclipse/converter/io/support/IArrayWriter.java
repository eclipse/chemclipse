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

public interface IArrayWriter {

	void write(byte[] bytes);

	void skipBytes(int bytes);

	byte[] getBytesStringTerminated(int writeBytes, String entry);

	byte[] getBytesStringNullTerminated(int writeBytes, String entry);

	void writeIntegerAsBigEndian(int value);

	byte[] get2BytesAsShortBigEndian(int value);

	byte[] get4BytesAsIntegerBigEndian(int value);

	void write2BytesUnsignedIntegerLittleEndian(int value);

	void write4BytesUnsignedIntegerLittleEndian(int value);

	void write8BytesUnsignedLittleEndian(long value);

	byte[] get2BytesLittleEndian(int value);

	byte[] get4BytesLittleEndian(int value);

	byte[] get8BytesLittleEndian(long value);
}
