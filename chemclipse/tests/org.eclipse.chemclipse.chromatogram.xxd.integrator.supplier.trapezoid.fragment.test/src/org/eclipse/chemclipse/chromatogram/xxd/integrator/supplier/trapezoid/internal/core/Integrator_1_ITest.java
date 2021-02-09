/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.BackgroundIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

public class Integrator_1_ITest extends ChromatogramImportOCBTestCase {

	private ChromatogramIntegrator chromatogramIntegrator;
	private BackgroundIntegrator backgroundIntegrator;
	private PeakIntegrator peakIntegrator;
	private PeakIntegrationSettings peakIntegrationSettings;

	@Override
	protected void setUp() throws Exception {

		chromatogramRelativePath = TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_2;
		super.setUp();
		chromatogramIntegrator = new ChromatogramIntegrator();
		backgroundIntegrator = new BackgroundIntegrator();
		peakIntegrator = new PeakIntegrator();
		peakIntegrationSettings = new PeakIntegrationSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testChromatogramIntegrate_1() {

		double area = chromatogramIntegrator.integrate(chromatogramSelection);
		assertEquals("", 2.5891749759277344E8, area);
	}

	public void testBackgroundIntegrate_1() {

		double area = backgroundIntegrator.integrate(chromatogramSelection);
		assertEquals("", 2.391494172607422E8, area);
	}

	public void testPeakIntegrate_1() {

		List<IChromatogramPeakMSD> peaksChromatogram = chromatogramSelection.getChromatogram().getPeaks();
		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD peak : peaksChromatogram) {
			peaks.add(peak);
		}
		IPeakIntegrationResults results;
		try {
			results = peakIntegrator.integrate(peaks, peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("", 1.6943660149129305E7, results.getTotalPeakArea());
		} catch(ValueMustNotBeNullException e) {
			assertTrue(false);
		}
	}
}
