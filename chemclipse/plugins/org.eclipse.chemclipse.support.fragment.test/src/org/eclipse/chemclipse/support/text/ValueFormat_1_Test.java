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

import java.text.DecimalFormat;

import junit.framework.TestCase;

public class ValueFormat_1_Test extends TestCase {

	private DecimalFormat decimalFormat;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("0", decimalFormat.format(0.0d));
	}

	public void test2() {

		assertEquals("0.1", decimalFormat.format(0.1d));
	}

	public void test3() {

		assertEquals("1", decimalFormat.format(1.0d));
	}

	public void test4() {

		assertEquals("1.1", decimalFormat.format(1.1d));
	}
}
