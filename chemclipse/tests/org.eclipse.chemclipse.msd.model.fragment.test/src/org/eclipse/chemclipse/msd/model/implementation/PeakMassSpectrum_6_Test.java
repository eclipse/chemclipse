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

import junit.framework.TestCase;

public class PeakMassSpectrum_6_Test extends TestCase {

	private IIon ion;
	private IPeakMassSpectrum peakMassSpectrum;
	private IPeakMassSpectrum peakMassSpectrumShifted;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * The total signal of the mass spectrum is 6514141.6f.
		 */
		peakMassSpectrum = new PeakMassSpectrum();
		ion = new Ion(45.5f, 64830.4f);
		peakMassSpectrum.addIon(ion);
		ion = new Ion(78.5f, 4440.4f);
		peakMassSpectrum.addIon(ion);
		ion = new Ion(104.5f, 6444830.4f);
		peakMassSpectrum.addIon(ion);
		ion = new Ion(14.5f, 40.4f);
		peakMassSpectrum.addIon(ion);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		peakMassSpectrumShifted = null;
		peakMassSpectrum = null;
		super.tearDown();
	}

	public void testGetShiftedMassSpectrum_1() {

		/*
		 * 50% of 6514141.6f = 3257071.0f
		 */
		peakMassSpectrumShifted = new PeakMassSpectrum(peakMassSpectrum, 50);
		assertEquals("TotalSignal", 3257071.0f, peakMassSpectrumShifted.getTotalSignal());
	}

	public void testGetShiftedMassSpectrum_2() {

		/*
		 * 100% of 6514141.6f = 6514141.6f
		 */
		peakMassSpectrumShifted = new PeakMassSpectrum(peakMassSpectrum, 100);
		assertEquals("TotalSignal", 6514142.0f, peakMassSpectrumShifted.getTotalSignal());
	}

	public void testGetShiftedMassSpectrum_3() {

		/*
		 * 101% of 6514141.6f = 3257071.0f
		 */
		assertThrows(IllegalArgumentException.class, () -> peakMassSpectrumShifted = new PeakMassSpectrum(peakMassSpectrum, 101));
	}

	public void testGetShiftedMassSpectrum_4() {

		/*
		 * 0% of 6514141.6f = 3257071.0f
		 */
		peakMassSpectrumShifted = new PeakMassSpectrum(peakMassSpectrum, 0);
		assertEquals("TotalSignal", 0.0f, peakMassSpectrumShifted.getTotalSignal());
	}

	public void testGetShiftedMassSpectrum_5() {

		/*
		 * 30% of 6514141.6f = 1954242.48f
		 */
		peakMassSpectrumShifted = new PeakMassSpectrum(peakMassSpectrum, 30);
		assertEquals("TotalSignal", 1954242.48f, peakMassSpectrumShifted.getTotalSignal());
	}
}
