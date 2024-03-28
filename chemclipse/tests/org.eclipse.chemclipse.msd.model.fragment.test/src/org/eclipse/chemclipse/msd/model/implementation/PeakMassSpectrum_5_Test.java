/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import static org.junit.Assert.assertThrows;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

import junit.framework.TestCase;

public class PeakMassSpectrum_5_Test extends TestCase {

	private IIon ion;
	private IScanMSD massSpectrum;
	private IPeakMassSpectrum peakMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * The total signal of the mass spectrum is 6514141.6f.
		 */
		massSpectrum = new ScanMSD();
		ion = new Ion(45.5f, 64830.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(78.5f, 4440.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(104.5f, 6444830.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(14.5f, 40.4f);
		massSpectrum.addIon(ion);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		peakMassSpectrum = null;
		super.tearDown();
	}

	public void testGetTotalSignal_1() {

		/*
		 * 70% actual: 6514141.6f -> 100% : 9305916.571f
		 */
		peakMassSpectrum = new PeakMassSpectrum(massSpectrum, 70.0f);
		assertEquals("TotalSignal", 9305916.571f, peakMassSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_2() {

		/*
		 * 120% actual: 6514141.6f -> 100% : 5428451.333f
		 */
		peakMassSpectrum = new PeakMassSpectrum(massSpectrum, 120.0f);
		assertEquals("TotalSignal", 5428451.0f, peakMassSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_3() {

		/*
		 * 100% actual: 6514141.6f -> 100% : 6514141.6f
		 */
		peakMassSpectrum = new PeakMassSpectrum(massSpectrum, 100.0f);
		assertEquals("TotalSignal", 6514142.0f, peakMassSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_4() {

		assertThrows(IllegalArgumentException.class, () -> {
			peakMassSpectrum = new PeakMassSpectrum(massSpectrum, 0.0f);
		});
	}

	public void testGetTotalSignal_5() {

		assertThrows(IllegalArgumentException.class, () -> {
			peakMassSpectrum = new PeakMassSpectrum(massSpectrum, -1.0f);
		});
	}

	public void testGetTotalSignal_6() {

		/*
		 * 97.2% actual: 6514141.6f -> 100% : 6701791.77f
		 */
		peakMassSpectrum = new PeakMassSpectrum(massSpectrum, 97.2f);
		assertEquals("TotalSignal", 6701792.5f, peakMassSpectrum.getTotalSignal());
	}
}
