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
package org.eclipse.chemclipse.vsd.model.support;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.SignalType;
import org.eclipse.chemclipse.vsd.model.core.selection.IChromatogramSelectionVSD;

public class FilterSupportVSD {

	public static IScanVSD getCombinedSpectrum(IChromatogramSelectionVSD chromatogramSelection, boolean nominalizeWavenumber, CalculationType calculationType) {

		IScanVSD scanCombinedISD = null;
		if(chromatogramSelection != null && calculationType != null) {
			/*
			 * Settings
			 */
			IChromatogramVSD chromatogram = chromatogramSelection.getChromatogram();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int startScan = chromatogram.getScanNumber(startRetentionTime);
			int stopScan = chromatogram.getScanNumber(stopRetentionTime);
			//
			SignalType signalType = SignalType.RAMAN;
			CombinedScanCalculator combinedScanCalculator = new CombinedScanCalculator();
			for(int i = startScan; i <= stopScan; i++) {
				IScan scan = chromatogram.getScan(i);
				if(scan instanceof IScanVSD scanISD) {
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