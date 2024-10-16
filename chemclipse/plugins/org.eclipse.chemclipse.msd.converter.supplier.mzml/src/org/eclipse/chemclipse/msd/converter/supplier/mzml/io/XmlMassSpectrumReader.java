/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.io;

import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class XmlMassSpectrumReader {

	private XmlMassSpectrumReader() {

	}

	public static void addTotalSignals(double[] intensities, double[] retentionTimes, IVendorChromatogram chromatogram) {

		int tic = Math.min(retentionTimes.length, intensities.length);
		for(int i = 0; i < tic; i++) {
			VendorScan scan = new VendorScan();
			int retentionTime = (int)(retentionTimes[i]);
			scan.setRetentionTime(retentionTime);
			float intensity = (float)intensities[i];
			VendorIon ion = new VendorIon(IIon.TIC_ION, intensity);
			scan.addIon(ion, false);
			chromatogram.addScan(scan);
		}
	}

	public static void addIons(double[] intensities, double[] mzs, IScanMSD massSpectrum) {

		int ions = Math.min(mzs.length, intensities.length);
		for(int i = 0; i < ions; i++) {
			massSpectrum.addIon(new VendorIon(mzs[i], (float)intensities[i]), false);
		}
	}
}
