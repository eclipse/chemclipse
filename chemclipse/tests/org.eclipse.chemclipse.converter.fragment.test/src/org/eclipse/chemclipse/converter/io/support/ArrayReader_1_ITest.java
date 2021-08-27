/*******************************************************************************
 * Copyright (c) 2012, 2021 Lablicate GmbH.
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

import java.io.File;

import org.eclipse.chemclipse.converter.TestPathHelper;

import junit.framework.TestCase;

public class ArrayReader_1_ITest extends TestCase {

	/*
	 * Big Endian
	 */
	private IArrayReader arrayReader;

	@Override
	protected void setUp() throws Exception {

		/*
		 * IMPORT_BIN_TEST:
		 * F0 A7 C1 0B 04 9F 01 3B
		 * 11110000 10100111 11000001 00001011 00000100 10011111 00000001 00111011
		 */
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_BIN_TEST));
		arrayReader = new ArrayReaderTestImplementation(file);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testRead1BUShortBE_1() {

		assertEquals(240, arrayReader.read1BUShortBE());
	}

	public void testRead1BShortBE_1() {

		assertEquals(-16, arrayReader.read1BShortBE());
	}

	public void testRead2BShortBE_1() {

		assertEquals(-3929, arrayReader.read2BShortBE());
	}

	public void testRead2BUIntegerBE_1() {

		assertEquals(61607, arrayReader.read2BUIntegerBE());
	}

	public void testRead2BIntegerBE_1() {

		assertEquals(-3929, arrayReader.read2BIntegerBE());
	}

	public void testRead4BIntegerBE_1() {

		assertEquals(-257441525, arrayReader.read4BIntegerBE());
	}

	public void testRead4BULongBE_1() {

		assertEquals(4037525771L, arrayReader.read4BULongBE());
	}

	public void testRead4BLongBE_1() {

		assertEquals(-257441525, arrayReader.read4BLongBE());
	}

	public void testRead8BULongBE_1() {

		assertEquals(8117669106424938811L, arrayReader.read8BULongBE());
	}

	public void testRead8BLongBE_1() {

		assertEquals(-1105702930429836997L, arrayReader.read8BLongBE());
	}

	// ---------------------------------------------------------------
	public void testRead8BUDoubleBE_1() {

		assertEquals(4.720464669257224E234, arrayReader.read8BUDoubleBE());
	}

	public void testRead8BDoubleBE_1() {

		assertEquals(-4.720464669257224E234, arrayReader.read8BDoubleBE());
	}

	// ---------------------------------------------------------------
	public void testReadULongBE_1() {

		assertEquals(240, arrayReader.readULongBE(1));
	}

	public void testReadLongBE_1() {

		assertEquals(240, arrayReader.readLongBE(1));
	}

	public void testReadULongBE_2() {

		assertEquals(61607, arrayReader.readULongBE(2));
	}

	public void testReadLongBE_2() {

		assertEquals(61607, arrayReader.readLongBE(2));
	}

	public void testReadULongBE_3() {

		assertEquals(4037525771L, arrayReader.readULongBE(4));
	}

	public void testReadLongBE_3() {

		assertEquals(4037525771L, arrayReader.readLongBE(4));
	}

	public void testReadULongBE_4() {

		assertEquals(8117669106424938811L, arrayReader.readULongBE(8));
	}

	public void testReadLongBE_4() {

		assertEquals(-1105702930429836997L, arrayReader.readLongBE(8));
	}

	public void testReadULongBE_5() {

		assertEquals(0, arrayReader.readULongBE(-1));
	}

	public void testReadLongBE_5() {

		assertEquals(0, arrayReader.readLongBE(-1));
	}

	public void testReadULongBE_6() {

		assertEquals(0, arrayReader.readULongBE(0));
	}

	public void testReadLongBE_6() {

		assertEquals(0, arrayReader.readLongBE(0));
	}

	public void testReadULongBE_7() {

		assertEquals(0, arrayReader.readULongBE(9));
	}

	public void testReadLongBE_7() {

		assertEquals(0, arrayReader.readLongBE(9));
	}
	// ---------------------------------------------------------------
}
