/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class TotalScanSignalExtractor implements ITotalScanSignalExtractor {

	private IChromatogram chromatogram;

	/**
	 * All values will be extracted from IChromatogram.
	 * 
	 * @param chromatogram
	 * @throws ChromatogramIsNullException
	 */
	public TotalScanSignalExtractor(IChromatogram chromatogram) throws ChromatogramIsNullException {

		if(chromatogram == null) {
			throw new ChromatogramIsNullException();
		}
		this.chromatogram = chromatogram;
	}

	/**
	 * The overview will be casted to IChromatogram.
	 * 
	 * @param chromatogramOverview
	 * @throws ChromatogramIsNullException
	 */
	public TotalScanSignalExtractor(IChromatogramOverview chromatogramOverview) throws ChromatogramIsNullException {

		if(chromatogramOverview == null) {
			throw new ChromatogramIsNullException();
		}
		/*
		 * Cast to chromatogram.
		 */
		if(chromatogramOverview instanceof IChromatogram) {
			this.chromatogram = (IChromatogram)chromatogramOverview;
		} else {
			throw new ChromatogramIsNullException("Chromatogram Overview can't be casted to IChromatogram.");
		}
	}

	@Override
	public ITotalScanSignals getTotalScanSignals() {

		return getTotalScanSignals(true);
	}

	@Override
	public ITotalScanSignals getTotalScanSignals(boolean validatePositive) {

		ITotalScanSignal totalScanSignal;
		ITotalScanSignals signals = new TotalScanSignals(chromatogram.getNumberOfScans(), chromatogram);
		/*
		 * Get each scan signal.
		 */
		for(IScan scan : chromatogram.getScans()) {
			totalScanSignal = new TotalScanSignal(scan.getRetentionTime(), scan.getRetentionIndex(), scan.getTotalSignal(), validatePositive);
			signals.add(totalScanSignal);
		}
		return signals;
	}

	@Override
	public ITotalScanSignals getTotalScanSignals(int startScan, int stopScan) {

		return getTotalScanSignals(startScan, stopScan, true);
	}

	@Override
	public ITotalScanSignals getTotalScanSignals(int startScan, int stopScan, boolean validatePositive) {

		/*
		 * Change the order of start and stop scan if necessary.
		 */
		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		/*
		 * Validate the scan borders.
		 */
		if(startScan < 1 || startScan > chromatogram.getNumberOfScans() || stopScan < 1 || stopScan > chromatogram.getNumberOfScans()) {
			return new TotalScanSignals(0, chromatogram);
		}
		/*
		 * Create the total ion signals object.
		 */
		ITotalScanSignal totalScanSignal;
		ITotalScanSignals signals = new TotalScanSignals(startScan, stopScan, chromatogram);
		/*
		 * Get the signals
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan selectedScan = chromatogram.getScan(scan);
			totalScanSignal = new TotalScanSignal(selectedScan.getRetentionTime(), selectedScan.getRetentionIndex(), selectedScan.getTotalSignal(), validatePositive);
			signals.add(totalScanSignal);
		}
		return signals;
	}

	@Override
	public ITotalScanSignals getTotalScanSignals(IChromatogramSelection chromatogramSelection) {

		return getTotalScanSignals(chromatogramSelection, true);
	}

	@Override
	public ITotalScanSignals getTotalScanSignals(IChromatogramSelection chromatogramSelection, boolean validatePositive) {

		/*
		 * If the chromatogram selection is null, return an empty
		 * ITotalIonSignals object.
		 */
		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() != chromatogram) {
			return new TotalScanSignals(0, chromatogram);
		}
		/*
		 * Get the start and stop scan.
		 */
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		return getTotalScanSignals(startScan, stopScan, validatePositive);
	}
}
