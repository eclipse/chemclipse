/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.core;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class ChromatogramSubtractor {

	public void perform(IChromatogram<?> chromatogramMaster, IChromatogram<?> chromatogramSubtract) {

		if(chromatogramMaster != null && chromatogramSubtract != null) {
			int startRetentionTime = chromatogramMaster.getStartRetentionTime();
			int stopRetentionTime = chromatogramMaster.getStopRetentionTime();
			perform(chromatogramMaster, chromatogramSubtract, startRetentionTime, stopRetentionTime);
		}
	}

	public void perform(IChromatogram<?> chromatogramMaster, IChromatogram<?> chromatogramSubtract, int startRetentionTime, int stopRetentionTime) {

		if(chromatogramMaster != null && chromatogramSubtract != null) {
			int startScan = chromatogramMaster.getScanNumber(startRetentionTime);
			int stopScan = chromatogramMaster.getScanNumber(stopRetentionTime);
			//
			for(int i = startScan; i <= stopScan; i++) {
				IScan scanMaster = chromatogramMaster.getScan(i);
				if(scanMaster != null) {
					int scanNumberSubtract = chromatogramSubtract.getScanNumber(scanMaster.getRetentionTime());
					IScan scanSubtract = chromatogramSubtract.getScan(scanNumberSubtract);
					if(scanSubtract != null) {
						/*
						 * Subtract the signal.
						 */
						float totalSignalMaster = scanMaster.getTotalSignal();
						float totalSignalSubstract = scanSubtract.getTotalSignal();
						float totalSignal = totalSignalMaster - totalSignalSubstract;
						//
						if(scanMaster instanceof IScanMSD) {
							if(totalSignal > 0) {
								scanMaster.adjustTotalSignal(totalSignal);
							} else {
								IScanMSD scanMSD = (IScanMSD)scanMaster;
								scanMSD.removeAllIons();
							}
						} else {
							scanMaster.adjustTotalSignal(totalSignal);
						}
					}
				}
			}
		}
	}
}
