/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.math;

import junit.framework.TestCase;

public class IonRoundMethod_MINUS_03_1_Test extends TestCase {

	private IonRoundMethod ionRoundMethod = IonRoundMethod.MINUS_03;

	public void test1() {

		assertEquals(0, ionRoundMethod.round(Double.NaN));
	}

	public void test2() {

		assertEquals(0, ionRoundMethod.round(Double.NEGATIVE_INFINITY));
	}

	public void test3() {

		assertEquals(0, ionRoundMethod.round(Double.POSITIVE_INFINITY));
	}

	public void test4() {

		assertEquals(0, ionRoundMethod.round(-Double.MIN_NORMAL));
	}

	public void test5() {

		assertEquals(17, ionRoundMethod.round(17.6d));
	}

	public void test6() {

		assertEquals(18, ionRoundMethod.round(17.7d));
	}

	public void test7() {

		assertEquals(18, ionRoundMethod.round(17.8d));
	}

	public void test8() {

		assertEquals(18, ionRoundMethod.round(18.6d));
	}

	public void test9() {

		assertEquals(19, ionRoundMethod.round(18.7d));
	}

	public void test10() {

		assertEquals("Round m/z from -0.3 (incl.) to +0.7 (excl.)", ionRoundMethod.label());
	}
}