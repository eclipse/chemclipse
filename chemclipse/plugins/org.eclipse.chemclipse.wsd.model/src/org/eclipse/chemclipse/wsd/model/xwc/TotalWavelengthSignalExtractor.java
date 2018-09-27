/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

/**
 * @deprecated Use {@link ExtractedSingleWavelengthSignalExtractor} instead
 *
 * @see {@link IExtractedSingleWavelengthSignals}
 * @see {@link IExtractedSingleWavelengthSignal}
 * 
 */
@Deprecated
public class TotalWavelengthSignalExtractor extends TotalScanSignalExtractor implements ITotalWavelengthSignalExtractor {

	private IChromatogramWSD chromatogram;

	public TotalWavelengthSignalExtractor(IChromatogramWSD chromatogram) throws ChromatogramIsNullException {
		super(chromatogram);
		this.chromatogram = chromatogram;
	}

	@Override
	public ITotalScanSignals getTotalWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() != chromatogram) {
			return new TotalScanSignals(0, chromatogram);
		}
		/*
		 * Get the start and stop scan.
		 */
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		ITotalScanSignals signals = initializeTotalWavelenghtSignals(startScan, stopScan);
		return signals;
	}

	private ITotalScanSignals initializeTotalWavelenghtSignals(int startScan, int stopScan) {

		assert (startScan <= stopScan) : "The startScan must be lower or equal the stop scan.";
		/*
		 * Validate the scan borders.
		 */
		if(startScan < 1 || startScan > chromatogram.getNumberOfScans() || stopScan < 1 || stopScan > chromatogram.getNumberOfScans()) {
			return new TotalScanSignals(0, chromatogram);
		}
		/*
		 * Add the selected scans.
		 */
		ITotalScanSignals signals = new TotalScanSignals(startScan, stopScan, chromatogram);
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScanWSD scanWSD = chromatogram.getSupplierScan(scan);
			ITotalScanSignal totalWavelengthSignal = new TotalScanSignal(scanWSD.getRetentionTime(), scanWSD.getRetentionIndex(), scanWSD.getTotalSignal());
			signals.add(totalWavelengthSignal);
		}
		return signals;
	}
}
