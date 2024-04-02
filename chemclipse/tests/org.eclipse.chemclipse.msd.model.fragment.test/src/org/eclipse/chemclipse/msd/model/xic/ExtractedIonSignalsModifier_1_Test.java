/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class ExtractedIonSignalsModifier_1_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum supplierMassSpectrum;
	private IIon supplierIon;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Build a chromatogram and add scans with ions and no
		 * abundance.
		 */
		chromatogram = new ChromatogramMSD();
		for(int scan = 1; scan <= 100; scan++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			for(int ion = 32; ion <= 104; ion++) {
				supplierIon = new Ion(ion, ion * 2);
				supplierMassSpectrum.addIon(supplierIon);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		supplierIon = null;
		supplierMassSpectrum = null;
		super.tearDown();
	}

	public void testAdjustThresholdTransitions_1() {

		try {
			ExtractedIonSignalsModifier.adjustThresholdTransitions(null);
		} catch(AnalysisSupportException e) {
			assertTrue("AnalysisSupportException", true);
		}
	}
}
