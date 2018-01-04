/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core.internal.support;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

/*
 * Chromatogram
 * Retention times:
 * Scan 1 1500
 * Scan 2 2500
 * Scan 3 3500
 * Scan 4 4500
 * Scan 5 5500
 * Scan 6 6500
 * Scan 7 7500
 * Scan 8 8500
 * Scan 9 9500
 * Scan 10 10500
 */
public class RTShifterChromatogramTestCase extends TestCase {

	private IChromatogramMSD chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = createChromatogram();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public IChromatogramMSD getChromatogram() {

		return chromatogram;
	}

	private IChromatogramMSD createChromatogram() {

		IChromatogramMSD chromatogram = new ChromatogramMSD();
		IVendorMassSpectrum scan;
		for(int i = 1; i <= 10; i++) {
			scan = new VendorMassSpectrum();
			chromatogram.addScan(scan);
		}
		chromatogram.setScanDelay(1500);
		chromatogram.setScanInterval(1000);
		chromatogram.recalculateRetentionTimes();
		return chromatogram;
	}
}
