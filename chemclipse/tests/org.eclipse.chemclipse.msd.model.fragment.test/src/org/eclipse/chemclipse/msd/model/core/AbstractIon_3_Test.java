/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.math.IonRoundMethod;

import junit.framework.TestCase;

public class AbstractIon_3_Test extends TestCase {

	private IonRoundMethod defaultMethod;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		defaultMethod = IonRoundMethod.getActive();
	}

	@Override
	protected void tearDown() throws Exception {

		IonRoundMethod.setActive(defaultMethod);
		super.tearDown();
	}

	public void test1() {

		IonRoundMethod.setActive(IonRoundMethod.DEFAULT);
		assertEquals(18, AbstractIon.getIon(18.49d));
	}

	public void test2() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_00);
		assertEquals(18, AbstractIon.getIon(18.0d));
	}

	public void test3() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_01);
		assertEquals(18, AbstractIon.getIon(17.9d));
	}

	public void test4() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_02);
		assertEquals(18, AbstractIon.getIon(17.8d));
	}

	public void test5() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_03);
		assertEquals(18, AbstractIon.getIon(17.7d));
	}

	public void test6() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_04);
		assertEquals(18, AbstractIon.getIon(17.6d));
	}

	public void test7() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_05);
		assertEquals(18, AbstractIon.getIon(17.5d));
	}

	public void test8() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_06);
		assertEquals(18, AbstractIon.getIon(17.4d));
	}

	public void test9() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_07);
		assertEquals(18, AbstractIon.getIon(17.3d));
	}

	public void test10() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_08);
		assertEquals(18, AbstractIon.getIon(17.2d));
	}

	public void test11() {

		IonRoundMethod.setActive(IonRoundMethod.MINUS_09);
		assertEquals(18, AbstractIon.getIon(17.1d));
	}
}
