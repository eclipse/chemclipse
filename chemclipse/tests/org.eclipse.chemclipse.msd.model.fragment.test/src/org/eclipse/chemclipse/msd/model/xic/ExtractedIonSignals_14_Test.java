/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

public class ExtractedIonSignals_14_Test extends TestCase {

	private IVendorMassSpectrum supplierMassSpectrum;
	private IIon defaultIon;
	private IExtractedIonSignals extractedIonSignals;
	private IChromatogramMSD chromatogram;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		int scans = 120;
		int ionStart = 25;
		int ionStop = 30;
		/*
		 * No empty scans.
		 */
		chromatogram = new ChromatogramMSD();
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
		//
		((IScanMSD)chromatogram.getScan(1)).removeAllIons();
		((IScanMSD)chromatogram.getScan(2)).removeAllIons();
		((IScanMSD)chromatogram.getScan(3)).removeAllIons();
		((IScanMSD)chromatogram.getScan(4)).removeAllIons();
		((IScanMSD)chromatogram.getScan(69)).removeAllIons();
		/*
		 * Use a chromatogram selection.
		 */
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
		IChromatogramSelectionMSD chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignals = null;
		chromatogram = null;
		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("Size", 64, extractedIonSignals.size());
	}

	public void testSize_2() {

		assertEquals(5, extractedIonSignals.getStartScan());
		assertEquals(68, extractedIonSignals.getStopScan());
	}

	public void testSize_3() {

		assertEquals(25, extractedIonSignals.getStartIon());
		assertEquals(30, extractedIonSignals.getStopIon());
	}
}
