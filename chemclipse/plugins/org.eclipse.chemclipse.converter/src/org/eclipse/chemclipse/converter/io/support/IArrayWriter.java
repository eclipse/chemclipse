/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

public interface IArrayWriter {

	void writeIntegerAsBigEndian(int b);

	void write(byte[] bytes);

	void skipBytes(int bytes);

	byte[] getBytesWithStringLengthIndex(int writeBytes, String entry);

	byte[] get2BytesAsShortBigEndian(int value);

	byte[] get4BytesAsIntegerBigEndian(int value);
}
