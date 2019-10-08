/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_19_Test extends PeakBuilderTestCase {

	private IMarkedIons excludedIons;
	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignalExtractor totalIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		totalIonSignals = totalIonSignalExtractor.getTotalScanSignals(2, 16);
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignals = null;
		excludedIons = null;
		super.tearDown();
	}

	public void testValidateTotalIonSignals_1() {

		try {
			PeakBuilderMSD.validateTotalIonSignals(totalIonSignals);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testValidateTotalIonSignals_2() {

		try {
			PeakBuilderMSD.validateTotalIonSignals(null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testValidateExcludedIons_1() {

		try {
			PeakBuilderMSD.validateExcludedIons(excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testValidateExcludedIons_2() {

		try {
			PeakBuilderMSD.validateExcludedIons(null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testValidateChromatogram_1() {

		try {
			PeakBuilderMSD.validateChromatogram(chromatogram);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testValidateChromatogram_2() {

		try {
			PeakBuilderMSD.validateChromatogram(null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testValidateExtractedIonSignals_1() {

		IExtractedIonSignals extractedIonSignals = new ExtractedIonSignals(20);
		try {
			PeakBuilderMSD.validateExtractedIonSignals(extractedIonSignals);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testValidateExtractedIonSignals_2() {

		try {
			PeakBuilderMSD.validateExtractedIonSignals(null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
