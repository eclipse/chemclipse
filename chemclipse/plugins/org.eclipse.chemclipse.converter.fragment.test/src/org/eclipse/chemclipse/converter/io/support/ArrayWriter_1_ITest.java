/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import junit.framework.TestCase;

public class ArrayWriter_1_ITest extends TestCase {

	private IArrayWriter arrayWriter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testRead1BUShortBE_1() {

		byte[] data = new byte[8];
		arrayWriter = new ArrayWriterTestImplementation(data);
		long value = Double.doubleToRawLongBits(0.0168d);
		arrayWriter.write8BytesUnsignedLittleEndian(value);
		assertEquals(21, data[0]); // 0x15
		assertEquals(-116, data[1]); // 0x8C
		assertEquals(74, data[2]); // 0x4A
		assertEquals(-22, data[3]); // 0xEA
		assertEquals(4, data[4]); // 0x04
		assertEquals(52, data[5]); // 0x34
		assertEquals(-111, data[6]); // 0x91
		assertEquals(63, data[7]); // 0x3F
	}
}
