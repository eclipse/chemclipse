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
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import junit.framework.TestCase;

public class MassSpectra_1_Test extends TestCase {

	private IMassSpectra massSpectra;
	private ScanMSD massSpectrum1;
	private ScanMSD massSpectrum2;
	private IScanMSD massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectra = new MassSpectra();
		massSpectrum1 = new ScanMSD();
		massSpectrum1.addIon(new Ion(45.4f, 730.4f));
		massSpectrum1.addIon(new Ion(76.4f, 7830.4f));
		massSpectrum1.addIon(new Ion(48.7f, 57330.4f));
		massSpectrum2 = new ScanMSD();
		massSpectrum2.addIon(new Ion(43.4f, 723830.4f));
		massSpectrum2.addIon(new Ion(78.4f, 3430.4f));
		massSpectrum2.addIon(new Ion(23.3f, 530.4f));
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum1 = null;
		massSpectrum2 = null;
		massSpectra = null;
		super.tearDown();
	}

	public void testMassSpectra_1() {

		assertEquals("Size", 0, massSpectra.size());
	}

	public void testMassSpectra_2() {

		massSpectra.addMassSpectrum(massSpectrum1);
		massSpectra.addMassSpectrum(massSpectrum2);
		assertEquals("Size", 2, massSpectra.size());
	}

	public void testMassSpectra_3() {

		massSpectra.addMassSpectrum(massSpectrum1);
		massSpectra.addMassSpectrum(massSpectrum2);
		massSpectra.removeMassSpectrum(massSpectrum1);
		assertEquals("Size", 1, massSpectra.size());
	}

	public void testMassSpectra_4() {

		massSpectra.addMassSpectrum(massSpectrum1);
		massSpectra.addMassSpectrum(massSpectrum2);
		massSpectra.removeMassSpectrum(massSpectrum1);
		massSpectrum = massSpectra.getMassSpectrum(1);
		assertEquals("TotalSignal", 727791.1f, massSpectrum.getTotalSignal());
	}

	public void testMassSpectra_5() {

		massSpectra.addMassSpectrum(massSpectrum1);
		massSpectra.addMassSpectrum(massSpectrum2);
		massSpectrum = massSpectra.getMassSpectrum(1);
		assertEquals("TotalSignal", 65891.195f, massSpectrum.getTotalSignal());
		massSpectrum = massSpectra.getMassSpectrum(2);
		assertEquals("TotalSignal", 727791.1f, massSpectrum.getTotalSignal());
	}

	public void testMassSpectra_6() {

		massSpectra.addMassSpectrum(massSpectrum1);
		massSpectra.addMassSpectrum(massSpectrum2);
		massSpectra.removeMassSpectrum(massSpectrum2);
		massSpectra.removeMassSpectrum(massSpectrum1);
		assertEquals("Size", 0, massSpectra.size());
	}

	public void testMassSpectra_7() {

		massSpectra.addMassSpectrum(null);
		assertEquals("Size", 0, massSpectra.size());
	}
}
