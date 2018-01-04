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
package org.eclipse.chemclipse.support.text;

import java.text.NumberFormat;
import java.text.ParseException;

import junit.framework.TestCase;

public class ValueFormat_4_Test extends TestCase {

	private NumberFormat numberFormat;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		numberFormat = ValueFormat.getNumberFormatEnglish();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		try {
			assertEquals(0.0d, numberFormat.parse("0.0").doubleValue());
		} catch(ParseException e) {
			assertTrue(false);
		}
	}

	public void test2() {

		try {
			assertEquals(0.1d, numberFormat.parse("0.1").doubleValue());
		} catch(ParseException e) {
			assertTrue(false);
		}
	}

	public void test3() {

		try {
			assertEquals(1.0d, numberFormat.parse("1.0").doubleValue());
		} catch(ParseException e) {
			assertTrue(false);
		}
	}

	public void test4() {

		try {
			assertEquals(1.1d, numberFormat.parse("1.1").doubleValue());
		} catch(ParseException e) {
			assertTrue(false);
		}
	}
}
