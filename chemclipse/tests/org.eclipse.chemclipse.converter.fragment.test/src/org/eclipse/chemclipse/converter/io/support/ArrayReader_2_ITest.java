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

public class ArrayReader_2_ITest extends TestCase {

	/*
	 * Little Endian
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

	public void testRead1BUShortLE_1() {

		assertEquals(240, arrayReader.read1BUShortLE());
	}

	public void testRead1BShortLE_1() {

		assertEquals(-16, arrayReader.read1BShortLE());
	}

	public void testRead2BShortLE_1() {

		assertEquals(-22544, arrayReader.read2BShortLE());
	}

	public void testRead2BUIntegerLE_1() {

		assertEquals(42992, arrayReader.read2BUIntegerLE());
	}

	public void testRead2BIntegerLE_1() {

		assertEquals(-22544, arrayReader.read2BIntegerLE());
	}

	public void testRead4BIntegerLE_1() {

		assertEquals(197240816, arrayReader.read4BIntegerLE());
	}

	public void testRead4BULongLE_1() {

		assertEquals(197240816, arrayReader.read4BULongLE());
	}

	public void testRead4BLongLE_1() {

		assertEquals(197240816, arrayReader.read4BLongLE());
	}

	public void testRead8BULongLE_1() {

		assertEquals(4251854362940385264L, arrayReader.read8BULongLE());
	}

	public void testRead8BLongLE_1() {

		assertEquals(4251854362940385264L, arrayReader.read8BLongLE());
	}

	// ---------------------------------------------------------------
	public void testRead8BUDoubleLE_1() {

		assertEquals(1.8219847735894905E-24, arrayReader.read8BUDoubleLE());
	}

	public void testRead8BDoubleLE_1() {

		assertEquals(1.8219847735894905E-24, arrayReader.read8BDoubleLE());
	}

	// ---------------------------------------------------------------
	public void testReadULongLE_1() {

		assertEquals(240, arrayReader.readULongLE(1));
	}

	public void testReadLongLE_1() {

		assertEquals(240, arrayReader.readLongLE(1));
	}

	public void testReadULongLE_2() {

		assertEquals(42992, arrayReader.readULongLE(2));
	}

	public void testReadLongLE_2() {

		assertEquals(42992, arrayReader.readLongLE(2));
	}

	public void testReadULongLE_3() {

		assertEquals(197240816, arrayReader.readULongLE(4));
	}

	public void testReadLongLE_3() {

		assertEquals(197240816, arrayReader.readLongLE(4));
	}

	public void testReadULongLE_4() {

		assertEquals(4251854362940385264L, arrayReader.readULongLE(8));
	}

	public void testReadLongLE_4() {

		assertEquals(4251854362940385264L, arrayReader.readLongLE(8));
	}

	public void testReadULongLE_5() {

		assertEquals(0, arrayReader.readULongLE(-1));
	}

	public void testReadLongLE_5() {

		assertEquals(0, arrayReader.readLongLE(-1));
	}

	public void testReadULongLE_6() {

		assertEquals(0, arrayReader.readULongLE(0));
	}

	public void testReadLongLE_6() {

		assertEquals(0, arrayReader.readLongLE(0));
	}

	public void testReadULongLE_7() {

		assertEquals(0, arrayReader.readULongLE(9));
	}

	public void testReadLongLE_7() {

		assertEquals(0, arrayReader.readLongLE(9));
	}
	// ---------------------------------------------------------------
}
