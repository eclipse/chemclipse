/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author eselmeister
 */
public class TrapezoidPeakIntegrator_4_Test extends DefaultPeakTestCase {

	private PeakIntegrator integrator;
	private IPeakIntegrationResult result;
	private IPeakIntegrationResults results;
	private PeakIntegrationSettings peakIntegrationSettings;
	private List<IPeakMSD> peaks;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrator = new PeakIntegrator();
		peakIntegrationSettings = new PeakIntegrationSettings();
		peaks = new ArrayList<IPeakMSD>();
		for(int i = 1; i <= 100; i++) {
			peaks.add(super.getPeak());
		}
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testPeak_1() {

		IPeakMassSpectrum ms = super.getPeak().getPeakModel().getPeakMassSpectrum();
		assertEquals("TotalSignal", 5231.0f, ms.getTotalSignal());
	}

	public void testPeak_2() {

		float abundance = super.getPeak().getPeakModel().getBackgroundAbundance(1500);
		assertEquals("Background", 1760.0f, abundance);
	}

	public void testPeak_3() {

		float abundance = super.getPeak().getPeakModel().getBackgroundAbundance(15500);
		assertEquals("Background", 1760.0f, abundance);
	}

	public void testPeakList_1() {

		assertEquals("Size", 100, peaks.size());
	}

	public void testIntegrate_1() {

		// No chromatogram baseline.
		try {
			results = integrator.integrate(peaks, peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Total Integrated Peak Area", 2.85326990546264E7d, results.getTotalPeakArea());
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_2() {

		// No chromatogram baseline.
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			results = integrator.integrate(peaks, peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Total Integrated Peak Area", 5.314805905462652E7d, results.getTotalPeakArea());
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_3() {

		// No chromatogram baseline.
		try {
			results = integrator.integrate(peaks, peakIntegrationSettings, new NullProgressMonitor());
			result = results.getPeakIntegrationResult(0);
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertTrue(result.getIntegratedTraces().containsAll(peakIntegrationSettings.getMarkedTraces().getTraces()));
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_4() {

		// No chromatogram baseline.
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			results = integrator.integrate(peaks, peakIntegrationSettings, new NullProgressMonitor());
			result = results.getPeakIntegrationResult(0);
			assertEquals("Integrated Peak Area", 531480.5905462648d, result.getIntegratedArea());
			assertTrue(result.getIntegratedTraces().containsAll(peakIntegrationSettings.getMarkedTraces().getTraces()));
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}
}
