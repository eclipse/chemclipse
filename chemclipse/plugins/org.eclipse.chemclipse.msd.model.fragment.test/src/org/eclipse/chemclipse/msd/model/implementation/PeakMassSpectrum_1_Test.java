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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import junit.framework.TestCase;

public class PeakMassSpectrum_1_Test extends TestCase {

	private IScanMSD massSpectrum;
	private IIon ion;
	private IPeakMassSpectrum peakMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
		ion = new Ion(45.5f, 64830.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(78.5f, 4440.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(104.5f, 6444830.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(14.5f, 40.4f);
		massSpectrum.addIon(ion);
		peakMassSpectrum = new PeakMassSpectrum(massSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		massSpectrum = null;
		peakMassSpectrum = null;
		super.tearDown();
	}

	public void testGetNumberOfIons_1() {

		assertEquals("NumberOfIons", massSpectrum.getNumberOfIons(), peakMassSpectrum.getNumberOfIons());
	}

	public void testGetTotalSignal_1() {

		assertEquals("TotalSignal", massSpectrum.getTotalSignal(), peakMassSpectrum.getTotalSignal());
	}

	public void testGetBasePeak_1() {

		assertEquals("BasePeak", massSpectrum.getBasePeak(), peakMassSpectrum.getBasePeak());
	}
}
