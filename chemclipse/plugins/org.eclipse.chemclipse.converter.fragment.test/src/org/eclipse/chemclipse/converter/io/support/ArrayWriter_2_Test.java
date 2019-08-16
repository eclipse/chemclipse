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

public class ArrayWriter_2_Test extends TestCase {

	private ArrayWriterTestImplementation arrayWriter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		arrayWriter = new ArrayWriterTestImplementation(new byte[1000]);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		byte[] bytes = arrayWriter.getBytesStringNullTerminated(11, "Hello World");
		assertEquals(22, bytes.length);
	}

	public void test2() {

		byte[] bytes = arrayWriter.getBytesStringNullTerminated(5, "Hello World");
		assertEquals(10, bytes.length);
	}

	public void test3() {

		byte[] bytes = arrayWriter.getBytesStringNullTerminated(5, "Hello World");
		assertEquals(10, bytes.length);
		assertEquals(72, bytes[0]);
		assertEquals(0, bytes[1]);
		assertEquals(101, bytes[2]);
		assertEquals(0, bytes[3]);
		assertEquals(108, bytes[4]);
		assertEquals(0, bytes[5]);
		assertEquals(108, bytes[6]);
		assertEquals(0, bytes[7]);
		assertEquals(111, bytes[8]);
		assertEquals(0, bytes[9]);
	}
}
