/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.matrix;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

public class ExtractedMatrix_2_Test extends TestCase {

	private IVendorMassSpectrum supplierMassSpectrum;
	private IIon defaultIon;
	private IChromatogramMSD chromatogram;
	private float[][] signalMatrix;

	@Override
	protected void setUp() throws Exception {

		int scans = 10;
		int ionStart = 25;
		int ionStop = 30;
		/*
		 * missing ion 28
		 */
		chromatogram = new ChromatogramMSD();
		for(int scan = 1; scan <= scans; scan++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(scan);
			supplierMassSpectrum.setRetentionIndex(scan / 60.0f);
			for(int ion = ionStart; ion <= ionStop; ion++) {
				defaultIon = new Ion(ion, ion * scan);
				if(ion != 28) {
					supplierMassSpectrum.addIon(defaultIon);
				}
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		signalMatrix = new float[10][6];
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 6; j++) {
				signalMatrix[i][j] = 10.1f;
			}
		}
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			ChromatogramSelectionMSD selection = new ChromatogramSelectionMSD(chromatogram);
			ExtractedMatrix extracted = new ExtractedMatrix(selection);
			assertNotNull(extracted);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void testUpdate_1() {

		try {
			ChromatogramSelectionMSD selection = new ChromatogramSelectionMSD(chromatogram);
			ExtractedMatrix extracted = new ExtractedMatrix(selection);
			extracted.updateSignal();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
