/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add support for excluded wavelengths
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
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;

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
		return initializeTotalWavelengthSignals(startScan, stopScan, null);
	}

	private ITotalScanSignals initializeTotalWavelengthSignals(int startScan, int stopScan, IMarkedWavelengths excludedWavelengths) {

		if(startScan > stopScan) {
			throw new IllegalArgumentException("The start scan " + startScan + " must be lower or equal the stop scan " + stopScan + ".");
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
		ITotalScanSignals signals = new TotalScanSignals(startScan, stopScan, chromatogram);
		/*
		 * Add the selected scans.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScanWSD scanWSD = chromatogram.getSupplierScan(scan);
			ITotalScanSignal totalWavelengthSignal = new TotalScanSignal(scanWSD.getRetentionTime(), scanWSD.getRetentionIndex(), scanWSD.getTotalSignal(excludedWavelengths));
			signals.add(totalWavelengthSignal);
		}
		return signals;
	}

	@Override
	public ITotalScanSignals getTotalWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection, IMarkedWavelengths excludedWavelengths) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() != chromatogram) {
			return new TotalScanSignals(0, chromatogram);
		}
		/*
		 * Get the start and stop scan.
		 */
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * If excludedMassFragements is null the the total ion list will be
		 * returned.
		 */
		return initializeTotalWavelengthSignals(startScan, stopScan, excludedWavelengths);
	}

	@Override
	public ITotalScanSignals getTotalScanSignals(int startScan, int stopScan, MarkedWavelengths excludedWavelengths) {

		if(startScan > stopScan) {
			throw new IllegalArgumentException("The start scan " + startScan + " must be lower or equal the stop scan " + stopScan + ".");
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
		ITotalScanSignals signals = new TotalScanSignals(startScan, stopScan, chromatogram);
		/*
		 * Add the selected scans.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScanWSD scanWSD = chromatogram.getSupplierScan(scan);
			ITotalScanSignal totalWavelengthSignal = new TotalScanSignal(scanWSD.getRetentionTime(), scanWSD.getRetentionIndex(), scanWSD.getTotalSignal(excludedWavelengths));
			signals.add(totalWavelengthSignal);
		}
		return signals;
	}
}
