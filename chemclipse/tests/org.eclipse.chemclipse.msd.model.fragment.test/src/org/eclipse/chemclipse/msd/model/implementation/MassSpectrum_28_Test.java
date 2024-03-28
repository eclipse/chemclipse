/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

import junit.framework.TestCase;

public class MassSpectrum_28_Test extends TestCase {

	private IScanMSD massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void test1() {

		assertFalse(massSpectrum.isMeasurementSIM());
	}

	public void test2()  {

		addIons(massSpectrum, 1);
		assertTrue(massSpectrum.isMeasurementSIM());
	}

	public void test3()  {

		addIons(massSpectrum, 10);
		assertTrue(massSpectrum.isMeasurementSIM());
	}

	public void test4()  {

		addIons(massSpectrum, 11);
		assertFalse(massSpectrum.isMeasurementSIM());
	}

	private void addIons(IScanMSD massSpectrum, int amount)  {

		for(int i = 1; i <= amount; i++) {
			IIon nominalIon = new Ion(i, 1000);
			massSpectrum.addIon(nominalIon, false);
		}
	}
}
