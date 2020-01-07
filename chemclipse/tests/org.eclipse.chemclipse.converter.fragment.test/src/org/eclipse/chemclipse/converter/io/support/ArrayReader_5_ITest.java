/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.io.support.IArrayReader;
import org.eclipse.chemclipse.converter.TestPathHelper;

import junit.framework.TestCase;

public class ArrayReader_5_ITest extends TestCase {

	/*
	 * Little Endian
	 */
	private IArrayReader arrayReader;
	private byte[] prefix = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 4, 0};
	private byte[] tmp;

	@Override
	protected void setUp() throws Exception {

		/*
		 * IMPORT_BIN_TEST:
		 * F0 A7 C1 0B 04 9F 01 3B
		 * 11110000 10100111 11000001 00001011 00000100 10011111 00000001 00111011
		 */
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_BIN_TEST));
		super.setUp();
		arrayReader = new ArrayReaderTestImplementation(file);
		tmp = arrayReader.readBytes(prefix, 4);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test_1() {

		assertEquals(14, tmp.length);
	}

	public void test_2() {

		assertEquals(31, tmp[0]);
	}

	public void test_3() {

		assertEquals(-117, tmp[1]);
	}

	public void test_4() {

		assertEquals(4, tmp[8]);
	}

	public void test_5() {

		assertEquals(0, tmp[9]);
	}

	public void test_6() {

		assertEquals(-16, tmp[10]);
	}

	public void test_7() {

		assertEquals(11, tmp[13]);
	}
}
