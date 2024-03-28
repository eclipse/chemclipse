/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

import junit.framework.TestCase;

public class SubtractCalculator_2_Test extends TestCase {

	private SubtractCalculator subtractCalculator;
	private IScanMSD subtractMassSpectrum;
	private IScanMSD targetMassSpectrum;
	private Map<Double, Float> subtractMassSpectrumMap;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		boolean useNominalMasses = true;
		boolean useNormalize = true;
		//
		subtractCalculator = new SubtractCalculator();
		//
		subtractMassSpectrum = new CombinedMassSpectrum();
		subtractMassSpectrum.addIon(new Ion(18.2, 200));
		subtractMassSpectrum.addIon(new Ion(28.1, 1000));
		subtractMassSpectrum.addIon(new Ion(32.3, 500));
		//
		targetMassSpectrum = new CombinedMassSpectrum();
		targetMassSpectrum.addIon(new Ion(16.1, 2893.3f));
		targetMassSpectrum.addIon(new Ion(18.1, 8484.3f));
		targetMassSpectrum.addIon(new Ion(20.0, 3894.4f));
		targetMassSpectrum.addIon(new Ion(28.1, 57693.0f));
		targetMassSpectrum.addIon(new Ion(32.0, 3894.6f));
		targetMassSpectrum.addIon(new Ion(43.0, 3793.5f));
		//
		subtractMassSpectrumMap = subtractCalculator.getMassSpectrumMap(subtractMassSpectrum, useNominalMasses, useNormalize);
		subtractCalculator.adjustIntensityValues(targetMassSpectrum, subtractMassSpectrumMap, useNominalMasses, useNormalize);
	}

	@Override
	protected void tearDown() throws Exception {

		subtractCalculator = null;
		super.tearDown();
	}

	public void testMassSpectrumMap_1() {

		assertEquals(3, subtractMassSpectrumMap.size());
	}

	public void testMassSpectrumMap_2() {

		assertEquals(20.0f, subtractMassSpectrumMap.get(18.0));
	}

	public void testMassSpectrumMap_3() {

		assertEquals(100.0f, subtractMassSpectrumMap.get(28.0));
	}

	public void testMassSpectrumMap_4() {

		assertEquals(50.0f, subtractMassSpectrumMap.get(32.0));
	}

	public void testSubtractedMassSpectrum_1() {

		assertEquals(5, targetMassSpectrum.getNumberOfIons());
	}

	public void testSubtractedMassSpectrum_2() {

		assertEquals(2893.3f, targetMassSpectrum.getIon(16.1).getAbundance());
	}

	public void testSubtractedMassSpectrum_3() {

		assertEquals(6787.44f, targetMassSpectrum.getIon(18.1).getAbundance());
	}

	public void testSubtractedMassSpectrum_4() {

		assertEquals(3894.4f, targetMassSpectrum.getIon(20.0).getAbundance());
	}

	public void testSubtractedMassSpectrum_5() {

		assertEquals(1947.3f, targetMassSpectrum.getIon(32.0).getAbundance());
	}

	public void testSubtractedMassSpectrum_6() {

		assertEquals(3793.5f, targetMassSpectrum.getIon(43.0).getAbundance());
	}
}
