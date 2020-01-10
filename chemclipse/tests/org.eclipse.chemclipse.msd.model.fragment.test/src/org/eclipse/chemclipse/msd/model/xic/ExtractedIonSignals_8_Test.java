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
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

public class ExtractedIonSignals_8_Test extends TestCase {

	private IVendorMassSpectrum supplierMassSpectrum;
	private IScanIon defaultIon;
	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;
	private IChromatogramMSD chromatogram;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		int scans = 100;
		int ionStart = 25;
		int ionStop = 30;
		chromatogram = new ChromatogramMSD();
		/*
		 * Add 100 scans with scans of 6 ions.
		 */
		for(int scan = 1; scan <= scans; scan++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(scan);
			supplierMassSpectrum.setRetentionIndex(scan / 60.0f);
			for(int ion = ionStart; ion <= ionStop; ion++) {
				defaultIon = new ScanIon(ion, ion * scan);
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

	public void testSize_1() {

		assertEquals("Size", 100, extractedIonSignals.size());
	}

	public void testSize_2() throws NoExtractedIonSignalStoredException {

		assertEquals("Size", 100, extractedIonSignals.size());
		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals("Abundance", 25.0f, extractedIonSignal.getAbundance(25));
		extractedIonSignals.add(25, 250.0f, 1, true);
		assertEquals("Size", 100, extractedIonSignals.size());
		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals("Abundance", 250.0f, extractedIonSignal.getAbundance(25));
	}

	public void testSize_3() throws NoExtractedIonSignalStoredException {

		assertEquals("Size", 100, extractedIonSignals.size());
		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals("Abundance", 25.0f, extractedIonSignal.getAbundance(25));
		extractedIonSignals.add(25, 250, 1, false);
		assertEquals("Size", 100, extractedIonSignals.size());
		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals("Abundance", 275.0f, extractedIonSignal.getAbundance(25));
	}

	public void testSize_4() throws NoExtractedIonSignalStoredException {

		assertEquals("Size", 100, extractedIonSignals.size());
		extractedIonSignal = extractedIonSignals.getExtractedIonSignal(1);
		assertEquals("Abundance", 25.0f, extractedIonSignal.getAbundance(25));
		extractedIonSignals.add(31, 600.0f, 101, false);
		assertEquals("Size", 100, extractedIonSignals.size());
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(101);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
	}
}
