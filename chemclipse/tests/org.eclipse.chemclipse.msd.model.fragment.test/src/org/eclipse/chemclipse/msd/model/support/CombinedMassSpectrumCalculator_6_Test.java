/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

public class CombinedMassSpectrumCalculator_6_Test extends TestCase {

	private CombinedMassSpectrumCalculator combinedMassSpectrumCalculator;
	private IMarkedIons excludedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		combinedMassSpectrumCalculator.addIon(56.0f, 5100.0f);
		combinedMassSpectrumCalculator.addIon(60.0f, 52900.0f);
		combinedMassSpectrumCalculator.addIon(104.0f, 5300.0f);
		combinedMassSpectrumCalculator.addIon(28.0f, 5400.0f);
		combinedMassSpectrumCalculator.addIon(103.0f, 5500.0f);
		excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValues_1() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 103;
		ICombinedMassSpectrum massSpectrum1 = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals(5500.0f, massSpectrum1.getIon(ion).getAbundance());
		excludedIons.add(new MarkedIon(ion));
		combinedMassSpectrumCalculator.removeIons(excludedIons);
		ICombinedMassSpectrum massSpectrum2 = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertNull(massSpectrum2.getIon(ion));
	}

	public void testValues_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 104;
		ICombinedMassSpectrum massSpectrum1 = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals(5300.0f, massSpectrum1.getIon(ion).getAbundance());
		excludedIons.add(new MarkedIon(ion));
		combinedMassSpectrumCalculator.removeIons(excludedIons);
		ICombinedMassSpectrum massSpectrum2 = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertNull(massSpectrum2.getIon(ion));
	}

	public void testValues_3() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum massSpectrum1 = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals(5100.0f, massSpectrum1.getIon(56).getAbundance());
		assertEquals(5500.0f, massSpectrum1.getIon(103).getAbundance());
		excludedIons.add(new MarkedIon(56));
		excludedIons.add(new MarkedIon(103));
		combinedMassSpectrumCalculator.removeIons(excludedIons);
		ICombinedMassSpectrum massSpectrum2 = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertNull(massSpectrum2.getIon(56));
		assertNull(massSpectrum2.getIon(103));
	}
}
