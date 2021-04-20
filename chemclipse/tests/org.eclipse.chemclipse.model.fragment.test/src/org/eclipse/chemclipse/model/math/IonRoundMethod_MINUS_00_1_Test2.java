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
package org.eclipse.chemclipse.model.math;

import junit.framework.TestCase;

public class IonRoundMethod_MINUS_00_1_Test2 extends TestCase {

	private IonRoundMethod ionRoundMethod = IonRoundMethod.MINUS_00;

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

		assertEquals(17, ionRoundMethod.round(17.9d));
	}

	public void test6() {

		assertEquals(18, ionRoundMethod.round(18.0d));
	}

	public void test7() {

		assertEquals(18, ionRoundMethod.round(18.1d));
	}

	public void test8() {

		assertEquals(18, ionRoundMethod.round(18.9d));
	}

	public void test9() {

		assertEquals(19, ionRoundMethod.round(19.0d));
	}
}
