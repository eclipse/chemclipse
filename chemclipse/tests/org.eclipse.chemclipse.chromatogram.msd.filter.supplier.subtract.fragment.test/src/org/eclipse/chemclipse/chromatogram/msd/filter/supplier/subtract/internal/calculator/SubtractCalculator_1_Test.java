/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.internal.calculator;

import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.calculator.SubtractCalculator;

import junit.framework.TestCase;

public class SubtractCalculator_1_Test extends TestCase {

	private SubtractCalculator subtractCalculator;
	private Map<Double, Float> subtractMassSpectrumMap;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		subtractCalculator = new SubtractCalculator();
	}

	@Override
	protected void tearDown() throws Exception {

		subtractCalculator = null;
		super.tearDown();
	}

	public void testMassSpectrumMap_1() {

		subtractMassSpectrumMap = subtractCalculator.getMassSpectrumMap(null, true, true);
		assertEquals(0, subtractMassSpectrumMap.size());
	}

	public void testMassSpectrumMap_2() {

		subtractMassSpectrumMap = subtractCalculator.getMassSpectrumMap(null, true, false);
		assertEquals(0, subtractMassSpectrumMap.size());
	}
}
