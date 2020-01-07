/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.IIonTransition;

import junit.framework.TestCase;

public class MassSpectrum_32_Test extends TestCase {

	private ScanMSD massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
		massSpectrum.addIon(new Ion(45.5d, 78500.2f));
		massSpectrum.addIon(new Ion(85.4d, 3000.5f));
		massSpectrum.addIon(new Ion(104.1d, 120000.4f));
		IIonTransition ionTransition = new IonTransition(53.2d, 32, 10.0, 1.2d, 1.2d, 1);
		IIon ion = new Ion(32.6d, 890520.4f, ionTransition);
		massSpectrum.addIon(ion);
		massSpectrum.addIon(new Ion(105.7d, 120000.4f));
		massSpectrum.addIon(new Ion(28.2d, 33000.5f));
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void test_1() {

		assertTrue(massSpectrum.isTandemMS());
	}

	public void test_2() {

		assertFalse(massSpectrum.isHighResolutionMS());
	}
}
