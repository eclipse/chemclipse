/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.math;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class GeometricDistanceCalculator_2_Test extends MassSpectrumSetTestCase {

	private GeometricDistanceCalculator calculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new GeometricDistanceCalculator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		IScanMSD unknown = sinapylAclohol.getMassSpectrum();
		IScanMSD reference = benzenepropanoicAcid.getMassSpectrum();
		assertEquals(0.60377383f, calculator.calculate(unknown, reference, unknown.getExtractedIonSignal().getIonRange()));
	}

	public void test2() {

		IScanMSD unknown = benzenepropanoicAcid.getMassSpectrum();
		IScanMSD reference = sinapylAclohol.getMassSpectrum();
		assertEquals(0.5981944f, calculator.calculate(unknown, reference, unknown.getExtractedIonSignal().getIonRange()));
	}

	public void test3() {

		IScanMSD unknown = benzenepropanoicAcid.getMassSpectrum();
		IScanMSD reference = benzenepropanoicAcid.getMassSpectrum();
		assertEquals(1.0f, calculator.calculate(unknown, reference, unknown.getExtractedIonSignal().getIonRange()));
	}
}
