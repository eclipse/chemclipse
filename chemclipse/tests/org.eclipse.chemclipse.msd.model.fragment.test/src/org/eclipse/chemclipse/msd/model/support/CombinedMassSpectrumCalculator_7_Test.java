/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

public class CombinedMassSpectrumCalculator_7_Test extends TestCase {

	private static final float NORMALIZATION_FACTOR = 1000.0f;
	private ICombinedMassSpectrum noiseMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		CombinedMassSpectrumCalculator combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		combinedMassSpectrumCalculator.addIon(18.0f, 200.0f);
		combinedMassSpectrumCalculator.addIon(28.0f, 320.0f);
		combinedMassSpectrumCalculator.addIon(43.0f, 400.0f);
		combinedMassSpectrumCalculator.addIon(103.0f, 5000.0f);
		combinedMassSpectrumCalculator.addIon(104.0f, 20500.0f);
		combinedMassSpectrumCalculator.addIon(155.0f, 18000.0f);
		//
		noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(NORMALIZATION_FACTOR);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValues_1() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 18;
		assertEquals(9.756098f, noiseMassSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 28;
		assertEquals(15.609756f, noiseMassSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_3() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 43;
		assertEquals(19.512196f, noiseMassSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_4() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 103;
		assertEquals(243.90244f, noiseMassSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_5() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 104;
		assertEquals(1000.0f, noiseMassSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_6() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 155;
		assertEquals(878.04877f, noiseMassSpectrum.getIon(ion).getAbundance());
	}
}
