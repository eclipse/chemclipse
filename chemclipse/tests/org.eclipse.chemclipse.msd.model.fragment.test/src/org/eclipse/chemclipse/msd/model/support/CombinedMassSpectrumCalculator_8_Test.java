/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

public class CombinedMassSpectrumCalculator_8_Test extends TestCase {

	private CombinedMassSpectrumCalculator combinedMassSpectrumCalculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		combinedMassSpectrumCalculator.addIon(56.0f, 5100.0f);
		combinedMassSpectrumCalculator.addIon(60.0f, 0.0f);
		combinedMassSpectrumCalculator.addIon(104.0f, 5300.0f);
		combinedMassSpectrumCalculator.addIon(28.0f, 5400.0f);
		combinedMassSpectrumCalculator.addIon(103.0f, 0.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValues_1() {

		assertEquals(3, combinedMassSpectrumCalculator.size());
	}

	public void testValues_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 60;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertNull(massSpectrum.getIon(ion));
	}

	public void testValues_3() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 103;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertNull(massSpectrum.getIon(ion));
	}

	public void testValues_4() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 56;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals(5100.0f, massSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_5() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 104;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals(5300.0f, massSpectrum.getIon(ion).getAbundance());
	}

	public void testValues_6() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 28;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals(5400.0f, massSpectrum.getIon(ion).getAbundance());
	}
}