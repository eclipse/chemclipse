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

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

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
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValues_1() {

		int ion = 103;
		assertEquals(5500.0, combinedMassSpectrumCalculator.getAbundance(ion));
		excludedIons.add(new MarkedIon(ion));
		combinedMassSpectrumCalculator.removeIons(excludedIons);
		assertEquals(0.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_2() {

		int ion = 104;
		assertEquals(5300.0, combinedMassSpectrumCalculator.getAbundance(ion));
		excludedIons.add(new MarkedIon(ion));
		combinedMassSpectrumCalculator.removeIons(excludedIons);
		assertEquals(0.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_3() {

		assertEquals(5100.0, combinedMassSpectrumCalculator.getAbundance(56));
		assertEquals(5500.0, combinedMassSpectrumCalculator.getAbundance(103));
		excludedIons.add(new MarkedIon(56));
		excludedIons.add(new MarkedIon(103));
		combinedMassSpectrumCalculator.removeIons(excludedIons);
		assertEquals(0.0, combinedMassSpectrumCalculator.getAbundance(56));
		assertEquals(0.0, combinedMassSpectrumCalculator.getAbundance(103));
	}
}
