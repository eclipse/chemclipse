/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

public class XmlReader {

	private static final Logger logger = Logger.getLogger(XmlReader.class);

	private XmlReader() {

	}

	public static void addIons(double[] intensities, double[] retentionTimes, IVendorChromatogram chromatogram) {

		int tic = Math.min(retentionTimes.length, intensities.length);
		try {
			for(int i = 0; i < tic; i++) {
				VendorScan scan = new VendorScan();
				int retentionTime = (int)(retentionTimes[i]);
				scan.setRetentionTime(retentionTime);
				float intensity = (float)intensities[i];
				VendorIon ion = new VendorIon(IIon.TIC_ION, intensity);
				scan.addIon(ion, false);
				chromatogram.addScan(scan);
			}
		} catch(AbundanceLimitExceededException e) {
			logger.warn(e);
		} catch(IonLimitExceededException e) {
			logger.warn(e);
		}
	}

	public static void addIons(double[] intensities, double[] mzs, IVendorMassSpectrum massSpectrum) {

		int ions = Math.min(mzs.length, intensities.length);
		for(int i = 0; i < ions; i++) {
			try {
				double intensity = intensities[i];
				double mz = AbstractIon.getIon(mzs[i]);
				if(intensity >= VendorIon.MIN_ABUNDANCE && intensity <= VendorIon.MAX_ABUNDANCE) {
					IVendorIon ion = new VendorIon(mz, (float)intensity);
					massSpectrum.addIon(ion);
				}
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}
}
