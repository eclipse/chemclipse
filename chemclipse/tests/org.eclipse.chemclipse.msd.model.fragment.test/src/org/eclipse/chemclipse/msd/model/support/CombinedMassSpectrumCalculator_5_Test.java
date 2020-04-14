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

public class CombinedMassSpectrumCalculator_5_Test extends TestCase {

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

	public void testValues_1() {

		combinedMassSpectrumCalculator.addIon(56.0f, 5200.0f);
		combinedMassSpectrumCalculator.addIon(56.0f, 6800.0f);
		assertEquals(5200.0, combinedMassSpectrumCalculator.getValues().get(56).get(0));
		assertEquals(6800.0, combinedMassSpectrumCalculator.getValues().get(56).get(1));
	}
}
