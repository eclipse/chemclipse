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

public class CondenseMassSpectrumCalculator_1_Test extends TestCase {

	private CondenseMassSpectrumCalculator calculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new CondenseMassSpectrumCalculator(true);
		calculator.add(18.1d, 1000.0d);
		calculator.add(18.2d, 1000.0d);
		calculator.add(18.4d, 1000.0d);
		calculator.add(18.5d, 1000.0d);
		calculator.add(18.9d, 1000.0d);
	}

	@Override
	protected void tearDown() throws Exception {

		calculator = null;
		super.tearDown();
	}

	public void test1() {

		Map<Double, Double> mappedTraces = calculator.getMappedTraces();
		assertEquals(2, mappedTraces.size());
		assertTrue(mappedTraces.containsKey(18.0d));
		assertEquals(3000.0d, mappedTraces.get(18.0d));
		assertTrue(mappedTraces.containsKey(19.0d));
		assertEquals(2000.0d, mappedTraces.get(19.0d));
	}
}
