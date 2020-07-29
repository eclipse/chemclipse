/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
public class CasSupport_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	/*
	 * Correct CAS#: 7732-18-5
	 */
	public void test1a() {

		assertTrue(CasSupport.verifyChecksum("7732-18-5"));
	}

	public void test1b() {

		assertTrue(CasSupport.verifyChecksum(7732185));
	}

	public void test1c() {

		assertFalse(CasSupport.verifyChecksum("7732-18-4"));
	}

	public void test1d() {

		assertFalse(CasSupport.verifyChecksum(7732184));
	}

	public void test1e() {

		assertFalse(CasSupport.verifyChecksum("7732-185"));
	}

	public void test1f() {

		assertFalse(CasSupport.verifyChecksum("773218-5"));
	}

	public void test1g() {

		assertFalse(CasSupport.verifyChecksum("7732185"));
	}

	public void test1h() {

		assertFalse(CasSupport.verifyChecksum("7732-18-6"));
	}

	public void test1i() {

		assertFalse(CasSupport.verifyChecksum(7732186));
	}

	/*
	 * Correct CAS#: 7732-18-5
	 */
	public void test2a() {

		assertTrue(CasSupport.verifyChecksum("0-00-0"));
	}

	public void test2b() {

		assertTrue(CasSupport.verifyChecksum(0));
	}

	public void test2c() {

		assertFalse(CasSupport.verifyChecksum("0-00-1"));
	}

	/*
	 * Out of range test cases.
	 */
	public void test3a() {

		assertFalse(CasSupport.verifyChecksum(-1));
	}

	public void test3b() {

		assertFalse(CasSupport.verifyChecksum(1000000001));
	}

	public void test3c() {

		assertFalse(CasSupport.verifyChecksum("0-00"));
	}

	public void test3d() {

		assertFalse(CasSupport.verifyChecksum("0-00-0-"));
	}

	public void test3e() {

		assertFalse(CasSupport.verifyChecksum("-0-00-0"));
	}

	public void test3f() {

		assertFalse(CasSupport.verifyChecksum(null));
	}

	/*
	 * Existing CAS#
	 */
	public void test4() {

		assertTrue(CasSupport.verifyChecksum("100-42-5"));
	}

	public void test5() {

		assertTrue(CasSupport.verifyChecksum("71-43-2"));
	}

	public void test6() {

		assertTrue(CasSupport.verifyChecksum("108-88-3"));
	}

	public void test7() {

		assertTrue(CasSupport.verifyChecksum("5989-27-5"));
	}

	public void test8() {

		assertTrue(CasSupport.verifyChecksum("65996-98-7"));
	}
}
