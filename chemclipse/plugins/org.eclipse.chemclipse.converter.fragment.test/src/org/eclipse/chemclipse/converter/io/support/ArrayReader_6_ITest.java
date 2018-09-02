/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

public class ArrayReader_6_ITest extends TestCase {

	/*
	 * Little Endian
	 */
	private IArrayReader arrayReader;

	@Override
	protected void setUp() throws Exception {

		/*
		 * -60, "Ä"
		 * -42, "Ö"
		 * -36, "Ü"
		 * -28, "ä"
		 * -10, "ö"
		 * -4, "ü"
		 */
		super.setUp();
		byte[] data = new byte[]{6, -60, -42, -36, -28, -10, -4};
		arrayReader = new ArrayReaderTestImplementation(data);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test_1() {

		assertEquals("ÄÖÜäöü", arrayReader.readBytesAsStringWithLengthIndex(6));
	}
}
