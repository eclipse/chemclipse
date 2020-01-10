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

import java.io.File;

import org.eclipse.chemclipse.converter.io.support.IArrayReader;
import org.eclipse.chemclipse.converter.TestPathHelper;

import junit.framework.TestCase;

public class ArrayReader_3_ITest extends TestCase {

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

	public void testRead4BUIntegerME_1() {

		assertEquals(670043073, arrayReader.read4BUIntegerME());
	}

	public void testRead4BULongME_1() {

		assertEquals(2817526721L, arrayReader.read4BULongME());
	}
}
