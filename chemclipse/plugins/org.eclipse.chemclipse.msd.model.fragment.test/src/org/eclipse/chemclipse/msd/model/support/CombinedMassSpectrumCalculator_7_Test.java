/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.support.CombinedMassSpectrumCalculator;
import org.eclipse.chemclipse.msd.model.support.ICombinedMassSpectrumCalculator;

import junit.framework.TestCase;

public class CombinedMassSpectrumCalculator_7_Test extends TestCase {

	private ICombinedMassSpectrumCalculator combinedMassSpectrumCalculator;
	private static final float NORMALIZATION_FACTOR = 1000.0f;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		combinedMassSpectrumCalculator.addIon(18.0f, 200.0f);
		combinedMassSpectrumCalculator.addIon(28.0f, 320.0f);
		combinedMassSpectrumCalculator.addIon(43.0f, 400.0f);
		combinedMassSpectrumCalculator.addIon(103.0f, 5000.0f);
		combinedMassSpectrumCalculator.addIon(104.0f, 20500.0f);
		combinedMassSpectrumCalculator.addIon(155.0f, 18000.0f);
		combinedMassSpectrumCalculator.normalize(NORMALIZATION_FACTOR);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValues_1() {

		int ion = 18;
		assertEquals(9.75609756097561, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_2() {

		int ion = 28;
		assertEquals(15.609756097560975, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_3() {

		int ion = 43;
		assertEquals(19.51219512195122, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_4() {

		int ion = 103;
		assertEquals(243.90243902439025, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_5() {

		int ion = 104;
		assertEquals(1000.0, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testValues_6() {

		int ion = 155;
		assertEquals(878.048780487805, combinedMassSpectrumCalculator.getAbundance(ion));
	}
}
