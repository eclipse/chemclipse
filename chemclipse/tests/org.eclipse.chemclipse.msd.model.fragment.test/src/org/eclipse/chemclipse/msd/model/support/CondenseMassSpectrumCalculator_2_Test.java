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
package org.eclipse.chemclipse.msd.model.support;

import java.util.Map;

import junit.framework.TestCase;

public class CondenseMassSpectrumCalculator_2_Test extends TestCase {

	private CondenseMassSpectrumCalculator calculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new CondenseMassSpectrumCalculator(false);
		calculator.add(18.1d, 1000.0d);
		calculator.add(18.2d, 1100.0d);
		calculator.add(18.4d, 1200.0d);
		calculator.add(18.5d, 1300.0d);
		calculator.add(18.9d, 1400.0d);
	}

	@Override
	protected void tearDown() throws Exception {

		calculator = null;
		super.tearDown();
	}

	public void test1() {

		Map<Double, Double> mappedTraces = calculator.getMappedTraces();
		assertEquals(5, mappedTraces.size());
		assertTrue(mappedTraces.containsKey(18.1d));
		assertEquals(1000.0d, mappedTraces.get(18.1d));
		assertTrue(mappedTraces.containsKey(18.2d));
		assertEquals(1100.0d, mappedTraces.get(18.2d));
		assertTrue(mappedTraces.containsKey(18.4d));
		assertEquals(1200.0d, mappedTraces.get(18.4d));
		assertTrue(mappedTraces.containsKey(18.5d));
		assertEquals(1300.0d, mappedTraces.get(18.5d));
		assertTrue(mappedTraces.containsKey(18.9d));
		assertEquals(1400.0d, mappedTraces.get(18.9d));
	}
}
