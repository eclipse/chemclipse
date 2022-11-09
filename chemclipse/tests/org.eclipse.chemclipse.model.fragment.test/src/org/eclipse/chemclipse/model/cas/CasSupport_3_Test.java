/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.cas;

import junit.framework.TestCase;

/*
 * https://en.wikipedia.org/wiki/CAS_Registry_Number
 */
public class CasSupport_3_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("", CasSupport.calculateChecksum(null));
	}

	public void test2() {

		assertEquals("", CasSupport.calculateChecksum(""));
	}

	public void test3() {

		assertEquals("", CasSupport.calculateChecksum("a"));
	}

	public void test4() {

		assertEquals("", CasSupport.calculateChecksum("a-"));
	}

	public void test5() {

		assertEquals("0", CasSupport.calculateChecksum("0-00-"));
	}

	public void test6() {

		assertEquals("5", CasSupport.calculateChecksum("7732-18-"));
	}

	public void test7() {

		assertEquals("5", CasSupport.calculateChecksum("100-42-"));
	}

	public void test8() {

		assertEquals("2", CasSupport.calculateChecksum("71-43-"));
	}

	public void test9() {

		assertEquals("3", CasSupport.calculateChecksum("108-88-"));
	}

	public void test10() {

		assertEquals("5", CasSupport.calculateChecksum("5989-27-"));
	}

	public void test11() {

		assertEquals("7", CasSupport.calculateChecksum("65996-98-"));
	}
}