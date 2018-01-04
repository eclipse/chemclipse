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

public class CombinedMassSpectrumCalculator_5_Test extends TestCase {

	private ICombinedMassSpectrumCalculator combinedMassSpectrumCalculator;

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
		assertEquals(12000.0, combinedMassSpectrumCalculator.getValues().get(56));
	}
}
