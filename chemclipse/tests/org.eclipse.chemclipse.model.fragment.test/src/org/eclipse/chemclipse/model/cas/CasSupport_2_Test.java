/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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
public class CasSupport_2_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("", CasSupport.format(""));
	}

	public void test2() {

		assertEquals("a", CasSupport.format("a"));
	}

	public void test3() {

		assertEquals("a1", CasSupport.format("a1"));
	}

	public void test4() {

		assertEquals("123", CasSupport.format("123"));
	}

	public void test5() {

		assertEquals("1a234", CasSupport.format("1a234"));
	}

	public void test6() {

		assertEquals("0-00-0", CasSupport.format(null));
	}

	public void test7() {

		assertEquals("0-00-0", CasSupport.format("0"));
	}

	public void test8() {

		assertEquals("0-00-0", CasSupport.format("0-00-0"));
	}

	public void test9() {

		assertEquals("0-00-0", CasSupport.format("0000"));
	}

	public void test10() {

		assertEquals("7732-18-5", CasSupport.format("7732185"));
	}

	public void test11() {

		assertEquals("100-42-5", CasSupport.format("100425"));
	}

	public void test12() {

		assertEquals("71-43-2", CasSupport.format("71432"));
	}

	public void test13() {

		assertEquals("108-88-3", CasSupport.format("108883"));
	}

	public void test14() {

		assertEquals("5989-27-5", CasSupport.format("5989275"));
	}

	public void test15() {

		assertEquals("65996-98-7", CasSupport.format("65996987"));
	}
}
