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

		assertEquals(3, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testValues_2() {

		int ion = 60;
		assertEquals(0.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_3() {

		int ion = 103;
		assertEquals(0.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_4() {

		int ion = 56;
		assertEquals(5100.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_5() {

		int ion = 104;
		assertEquals(5300.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_6() {

		int ion = 28;
		assertEquals(5400.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}
}
