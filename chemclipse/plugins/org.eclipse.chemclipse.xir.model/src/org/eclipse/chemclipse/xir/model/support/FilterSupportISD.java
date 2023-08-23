/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.support;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.IScanISD;
import org.eclipse.chemclipse.xir.model.core.SignalType;
import org.eclipse.chemclipse.xir.model.core.selection.IChromatogramSelectionISD;

public class FilterSupportISD {

	public static IScanISD getCombinedSpectrum(IChromatogramSelectionISD chromatogramSelection, boolean nominalizeWavenumber, CalculationType calculationType) {

		IScanISD scanCombinedISD = null;
		if(chromatogramSelection != null && calculationType != null) {
			/*
			 * Settings
			 */
			IChromatogramISD chromatogram = chromatogramSelection.getChromatogram();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int startScan = chromatogram.getScanNumber(startRetentionTime);
			int stopScan = chromatogram.getScanNumber(stopRetentionTime);
			//
			SignalType signalType = SignalType.RAMAN;
			CombinedScanCalculator combinedScanCalculator = new CombinedScanCalculator();
			for(int i = startScan; i <= stopScan; i++) {
				IScan scan = chromatogram.getScan(i);
				if(scan instanceof IScanISD scanISD) {
					combinedScanCalculator.addSignals(scanISD, nominalizeWavenumber);
					signalType = scanISD.getSignalType();
				}
			}
			scanCombinedISD = combinedScanCalculator.createScan(calculationType, signalType);
		}
		//
		return scanCombinedISD;
	}
}