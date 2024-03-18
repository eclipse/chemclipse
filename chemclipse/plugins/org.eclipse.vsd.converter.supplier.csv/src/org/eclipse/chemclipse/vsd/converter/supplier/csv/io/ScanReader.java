/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.converter.supplier.csv.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.vsd.converter.supplier.csv.model.IVendorSpectrumVSD;
import org.eclipse.chemclipse.vsd.converter.supplier.csv.model.VendorSpectrumVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanReader {

	public IVendorSpectrumVSD read(File file, IProgressMonitor monitor) throws IOException {

		/*
		 * 5.226228e+002,0.000000e+000
		 * 5.245513e+002,3.940448e-001
		 * 5.264798e+002,3.945516e-001
		 * 5.284083e+002,3.944114e-001
		 * ...
		 */
		IVendorSpectrumVSD vendorScan = new VendorSpectrumVSD();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;
		while((line = bufferedReader.readLine()) != null) {
			String[] values = line.split(",");
			if(values.length == 2) {
				try {
					double wavelength = Double.parseDouble(values[0].trim());
					double absorbance = Double.parseDouble(values[1].trim());
					vendorScan.getScanVSD().getProcessedSignals().add(new SignalInfrared(wavelength, absorbance, 0));
				} catch(NumberFormatException e) {
					//
				}
			}
		}
		bufferedReader.close();
		return vendorScan;
	}
}