/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import junit.framework.TestCase;

public class TripleQuadMethod_1_Test extends TestCase {

	private TripleQuadMethod tripleQuadMethod;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		tripleQuadMethod = new TripleQuadMethod();
	}

	@Override
	protected void tearDown() throws Exception {

		tripleQuadMethod = null;
		super.tearDown();
	}

	public void test1() {

		assertEquals("QQQ", tripleQuadMethod.getInstrumentName());
	}

	public void test2() {

		assertEquals("EI", tripleQuadMethod.getIonSource());
	}

	public void test3() {

		assertEquals("ByChromatographTime", tripleQuadMethod.getStopMode());
	}

	public void test4() {

		assertEquals(60000, tripleQuadMethod.getStopTime());
	}

	public void test5() {

		assertEquals(360000, tripleQuadMethod.getSolventDelay());
	}

	public void test6() {

		assertTrue(tripleQuadMethod.isCollisionGasOn());
	}

	public void test7() {

		assertEquals(700, tripleQuadMethod.getTimeFilterPeakWidth());
	}

	public void test8() {

		assertEquals(230.0d, tripleQuadMethod.getSourceHeater());
	}

	public void test9() {

		assertEquals(5.0d, tripleQuadMethod.getSamplingRate());
	}
}