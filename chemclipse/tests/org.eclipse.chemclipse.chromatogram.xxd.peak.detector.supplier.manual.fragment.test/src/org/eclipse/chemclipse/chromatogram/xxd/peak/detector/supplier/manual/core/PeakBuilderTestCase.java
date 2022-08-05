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
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class PeakBuilderTestCase extends TestCase {

	protected IChromatogramMSD chromatogram;
	private IIon ion;
	private TreeMap<Float, Float> fragmentValues;
	private IVendorMassSpectrum supplierMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		// ----------------------Chromatogram
		chromatogram = new ChromatogramMSD();
		/*
		 * Add some more ion values, for example as a background.<br/>
		 * 580 + 420 + 760 > 1760
		 */
		fragmentValues = new TreeMap<Float, Float>();
		fragmentValues.put(104.0f, 2300.0f);
		fragmentValues.put(103.0f, 580.0f);
		fragmentValues.put(51.0f, 260.0f);
		fragmentValues.put(50.0f, 480.0f);
		fragmentValues.put(78.0f, 236.0f);
		fragmentValues.put(77.0f, 25.0f);
		fragmentValues.put(74.0f, 380.0f);
		fragmentValues.put(105.0f, 970.0f);
		fragmentValues.put(43.0f, 580.0f);
		fragmentValues.put(18.0f, 420.0f);
		fragmentValues.put(28.0f, 760.0f);
		/*
		 * The chromatogram should represent a real peak.
		 */
		List<Float> correctionFactor = new ArrayList<Float>();
		correctionFactor.add(0.0278125434570991f);
		correctionFactor.add(0.0278125434570991f);
		correctionFactor.add(0.0764219162842442f);
		correctionFactor.add(0.125031289111389f);
		correctionFactor.add(0.173640661938534f);
		correctionFactor.add(0.222250034765679f);
		correctionFactor.add(0.319468780419969f);
		correctionFactor.add(0.475018773466834f);
		correctionFactor.add(0.825006257822278f);
		correctionFactor.add(1f);
		correctionFactor.add(0.863893756083994f);
		correctionFactor.add(0.650012515644556f);
		correctionFactor.add(0.445853149770547f);
		correctionFactor.add(0.319468780419969f);
		correctionFactor.add(0.173640661938534f);
		correctionFactor.add(0.0278125434570991f);
		correctionFactor.add(0.0278125434570991f);
		/*
		 * Add Scan 1 (500) to 17 (16500) Correct the abundance values!
		 */
		for(int i = 1; i <= 17; i++) {
			// Scan
			supplierMassSpectrum = new VendorMassSpectrum();
			for(Entry<Float, Float> entry : fragmentValues.entrySet()) {
				ion = new Ion(entry.getKey(), entry.getValue() * correctionFactor.get(i - 1));
				supplierMassSpectrum.addIon(ion);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		/*
		 * ------------------ Total Signals 194.43748 194.43748 534.2656
		 * 874.0938 1213.9219 1553.75 2233.4062 3320.8562 5767.6187 6991.0
		 * 6039.481 4544.2373 3116.9595 2233.4062 1213.9219 194.43748 194.43748
		 * ------------------
		 */
		chromatogram.setScanDelay(500);
		chromatogram.setScanInterval(1000);
		chromatogram.recalculateRetentionTimes();
		// ----------------------Chromatogram
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		ion = null;
		fragmentValues = null;
		supplierMassSpectrum = null;
		super.tearDown();
	}
}
