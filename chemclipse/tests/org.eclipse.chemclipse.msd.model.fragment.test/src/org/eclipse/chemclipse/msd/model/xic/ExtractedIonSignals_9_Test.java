/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

public class ExtractedIonSignals_9_Test extends TestCase {

	private IVendorMassSpectrum supplierMassSpectrum;
	private IIon defaultIon;
	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;
	private IChromatogramMSD chromatogram;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		int scans = 1;
		int ionStart = 25;
		int ionStop = 50;
		chromatogram = new ChromatogramMSD();
		/*
		 * Add 100 scans with scans of 6 ions.
		 */
		for(int scan = 1; scan <= scans; scan++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(scan);
			supplierMassSpectrum.setRetentionIndex(scan / 60.0f);
			for(int ion = ionStart; ion <= ionStop; ion++) {
				defaultIon = new Ion(ion, ion * scan);
				supplierMassSpectrum.addIon(defaultIon);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals();
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignals = null;
		extractedIonSignal = null;
		chromatogram = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals("Size", 1, extractedIonSignals.size());
	}

	public void test_2() throws NoExtractedIonSignalStoredException {

		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals(50.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(975.0f, extractedIonSignal.getTotalSignal());
		extractedIonSignal.normalize(100.0f);
		assertEquals(100.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(1950.0f, extractedIonSignal.getTotalSignal());
	}

	public void test_3() throws NoExtractedIonSignalStoredException {

		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals(50.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(975.0f, extractedIonSignal.getTotalSignal());
		extractedIonSignal.normalize(0.0f);
		assertEquals(50.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(975.0f, extractedIonSignal.getTotalSignal());
	}

	public void test_4() throws NoExtractedIonSignalStoredException {

		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals(50.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(975.0f, extractedIonSignal.getTotalSignal());
		extractedIonSignal.normalize(1.0f);
		assertEquals(1.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(19.5f, extractedIonSignal.getTotalSignal(), 0.1f);
	}

	public void test_5() throws NoExtractedIonSignalStoredException {

		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals(50.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(975.0f, extractedIonSignal.getTotalSignal());
		extractedIonSignal.normalize();
		assertEquals(1000.0f, extractedIonSignal.getMaxIntensity());
		assertEquals(19500.0f, extractedIonSignal.getTotalSignal());
	}
}
