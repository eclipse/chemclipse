/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.io;

import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.TestPathHelper;

public class PeakReader_1_MSD_0903_ITest extends PeakReaderMSDTestCase {

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0903);
		super.setUp();
	}

	public void testReader_1() {

		assertEquals(4, peaks.getPeaks().size());
	}

	@SuppressWarnings("deprecation")
	public void testReader_2() {

		IPeak peak = peaks.getPeak(1);
		IPeakMSD peakMSD = (IPeakMSD)peak;
		assertEquals("Peak Detector First Derivative", peakMSD.getDetectorDescription());
		assertEquals(1219140.1874049378d, peakMSD.getIntegratedArea());
		assertEquals("Integrator Trapezoid: TIC", peakMSD.getIntegratorDescription());
		assertEquals("", peakMSD.getQuantifierDescription());
		Set<IIdentificationTarget> peakTargets = peakMSD.getTargets();
		assertEquals(1, peakTargets.size());
		IPeakMassSpectrum massSpectrum = peakMSD.getPeakModel().getPeakMassSpectrum();
		assertEquals(47, massSpectrum.getNumberOfIons());
		assertEquals(16.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(143.81145f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(207.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(60.937057f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(57.1d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(6787.7783f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(853916, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
	}
}
