/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.model.support.IntegrationConstraint;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.core.runtime.NullProgressMonitor;

public class TrapezoidPeakIntegrator_3_Test extends DefaultPeakTestCase {

	private PeakIntegrator integrator;
	private IPeakIntegrationResult result;
	private PeakIntegrationSettings peakIntegrationSettings;
	private String INTEGRATOR = "Trapezoid";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrator = new PeakIntegrator();
		peakIntegrationSettings = new PeakIntegrationSettings();
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

	public void testIntegrate_1a() {

		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_1b() {

		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 531480.5905462648d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_2a() {

		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_2b() {

		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		peakIntegrationSettings.setIncludeBackground(true);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_3a() {

		/*
		 * Chromatogram baseline at an abundance level of 4000.0f;
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 4000.0f, 4000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(false);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_3b() {

		/*
		 * Chromatogram baseline at an abundance level of 4000.0f;
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 4000.0f, 4000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(true);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 85739.27804626466d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_4a() {

		/*
		 * Chromatogram baseline at an abundance level of 6991.0f. That's
		 * exactly the total peak height (peak background and peak intensity).
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 6991.0f, 6991.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(false);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_4b() {

		/*
		 * Chromatogram baseline at an abundance level of 6991.0f. That's
		 * exactly the total peak height (peak background and peak intensity).
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 6991.0f, 6991.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(true);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 0.0d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_5a() {

		/*
		 * Chromatogram baseline at an abundance level of 8000.0f. That's above
		 * the total peak height (peak background and peak intensity).
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 8000.0f, 8000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_5b() {

		/*
		 * Chromatogram baseline at an abundance level of 8000.0f. That's above
		 * the total peak height (peak background and peak intensity).
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 8000.0f, 8000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(true);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 0.0d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_6a() {

		/*
		 * Chromatogram baseline at an abundance level of 8000.0f. That's above
		 * the total peak height (peak background and peak intensity).
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 0.0f, 8000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(false);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_6b() {

		/*
		 * Chromatogram baseline at an abundance level of 8000.0f. That's above
		 * the total peak height (peak background and peak intensity).
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 0.0f, 8000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(true);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 83859.96051452635d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_7a() {

		/*
		 * Chromatogram baseline at an abundance level of 4000.0f but leave peak
		 * as it is.
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 4000.0f, 4000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		super.getPeak().getIntegrationConstraints().add(IntegrationConstraint.LEAVE_PEAK_AS_IT_IS);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_7b() {

		/*
		 * Chromatogram baseline at an abundance level of 4000.0f but leave peak
		 * as it is.
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 4000.0f, 4000.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		peakIntegrationSettings.setIncludeBackground(true);
		super.getPeak().getIntegrationConstraints().add(IntegrationConstraint.LEAVE_PEAK_AS_IT_IS);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_8a() {

		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(false);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 59181.76195989684d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_8b() {

		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 110238.28392748673d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_9a() {

		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		peakIntegrationSettings.setIncludeBackground(false);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 59181.76195989684d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_9b() {

		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		peakIntegrationSettings.setIncludeBackground(true);
		try {
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 59181.76195989684d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_10a() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(false);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285327.0001125974d, result.getIntegratedArea(), 1);
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_10b() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 531480.6083655402d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_11a() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		try {
			peakIntegrationSettings.setIncludeBackground(false);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285327.0001125974d, result.getIntegratedArea(), 1);
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_11b() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285327.0001125974d, result.getIntegratedArea(), 1);
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_12a() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(IIon.TIC_ION));
		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(false);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_12b() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(IIon.TIC_ION));
		/*
		 * No chromatogram baseline.
		 * > peakIntegrationSettings.setIncludeBackground(true);
		 */
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 531480.5905462648d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_13a() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(IIon.TIC_ION));
		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * > peakIntegrationSettings.setIncludeBackground(false);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		try {
			peakIntegrationSettings.setIncludeBackground(false);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}

	public void testIntegrate_13() {

		/*
		 * Add all peak ions
		 */
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(104));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(103));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(51));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(50));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(78));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(77));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(74));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(105));
		peakIntegrationSettings.getSelectedIons().add(new MarkedIon(AbstractIon.getIon(IIon.TIC_ION)));
		/*
		 * Chromatogram baseline at an abundance level of 1760.0f;
		 * peakIntegrationSettings.setIncludeBackground(true);
		 */
		super.getChromatogram().getBaselineModel().addBaseline(500, 16500, 1760.0f, 1760.0f, true);
		peakIntegrationSettings.getBaselineSupport().setBaselineModel(super.getChromatogram().getBaselineModel());
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(500));
		assertEquals(1760.0f, peakIntegrationSettings.getBaselineSupport().getBackgroundAbundance(16500));
		try {
			peakIntegrationSettings.setIncludeBackground(true);
			result = integrator.integrate(super.getPeak(), peakIntegrationSettings, new NullProgressMonitor());
			assertEquals("Integrated Peak Area", 285326.9905462646d, result.getIntegratedArea());
			assertEquals(INTEGRATOR, result.getIntegratorType());
			assertTrue(result.getIntegratedIons().containsAll(peakIntegrationSettings.getSelectedIons().getIonsNominal()));
			String description = super.getPeak().getIntegratorDescription();
			assertEquals(INTEGRATOR, description);
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", false);
		}
	}
}
