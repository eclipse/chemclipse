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

public class CombinedMassSpectrumCalculator_1_Test extends TestCase {

	private CombinedMassSpectrumCalculator combinedMassSpectrumCalculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("Size", 0, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testSize_2() {

		combinedMassSpectrumCalculator.addIon(56.5f, 500.0f);
		assertEquals("Size", 1, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testSize_3() {

		combinedMassSpectrumCalculator.addIon(56.5f, 500.0f);
		combinedMassSpectrumCalculator.addIon(80.2f, 700.0f);
		assertEquals("Size", 2, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testSize_4() {

		combinedMassSpectrumCalculator.addIon(56.5f, 500.0f);
		combinedMassSpectrumCalculator.addIon(80.2f, 700.0f);
		combinedMassSpectrumCalculator.addIon(90.3f, 800.0f);
		assertEquals("Size", 3, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testSize_5() {

		/*
		 * Math round is used to determine the integer value of the mass
		 * fragment.
		 */
		combinedMassSpectrumCalculator.addIon(56.4f, 500.0f);
		combinedMassSpectrumCalculator.addIon(56.2f, 700.0f);
		combinedMassSpectrumCalculator.addIon(55.9f, 800.0f);
		assertEquals("Size", 1, combinedMassSpectrumCalculator.getValues().size());
	}
}
