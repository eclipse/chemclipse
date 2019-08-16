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

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import junit.framework.TestCase;

public class PeakMassSpectrum_3_Test extends TestCase {

	private IIon ion;
	private IPeakMassSpectrum peakMassSpectrum;
	private IPeakMassSpectrum peakMassSpectrumShifted;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peakMassSpectrum = new PeakMassSpectrum();
		ion = new Ion(45.5f, 64830.4f);
		peakMassSpectrum.addIon(ion);
		ion = new Ion(78.5f, 4440.4f);
		peakMassSpectrum.addIon(ion);
		ion = new Ion(104.5f, 6444830.4f);
		peakMassSpectrum.addIon(ion);
		ion = new Ion(14.5f, 40.4f);
		peakMassSpectrum.addIon(ion);
		peakMassSpectrumShifted = new PeakMassSpectrum(peakMassSpectrum, 50);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		peakMassSpectrumShifted = null;
		peakMassSpectrum = null;
		super.tearDown();
	}

	public void testGetTotalSignal_1() {

		assertEquals("TotalSignal", 3257071.0f, peakMassSpectrumShifted.getTotalSignal());
	}
}
