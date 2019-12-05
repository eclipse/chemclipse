/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - fix m/z filtering
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public class TotalIonSignalExtractor extends TotalScanSignalExtractor implements ITotalIonSignalExtractor {

	private IChromatogramMSD chromatogram;

	public TotalIonSignalExtractor(IChromatogramMSD chromatogram) throws ChromatogramIsNullException {
		super(chromatogram);
		this.chromatogram = chromatogram;
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(int startScan, int stopScan, IMarkedIons excludedIons) {

		/*
		 * Change the order of start and stop scan if necessary.
		 */
		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		/*
		 * If excludedMassFragements is null the the total ion list will be
		 * returned.
		 */
		return initializeTotalIonSignals(startScan, stopScan, excludedIons);
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons excludedIons) {

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
		/*
		 * If excludedMassFragements is null the the total ion list will be
		 * returned.
		 */
		ITotalScanSignals signals;
		signals = initializeTotalIonSignals(startScan, stopScan, excludedIons);
		return signals;
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(IChromatogramSelectionMSD chromatogramSelection) {

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
		ITotalScanSignals signals = initializeTotalIonSignals(startScan, stopScan, null);
		return signals;
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(IMarkedIons excludedIons) {

		return initializeTotalIonSignals(excludedIons);
	}

	/**
	 * Returns a {@link ITotalScanSignals} object.<br/>
	 * The method assumes that startScan and stopScan are in right order.<br/>
	 * If excludedIons is != null, the given ions will be
	 * excluded.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param excludedIons
	 * @return {@link ITotalScanSignals}
	 */
	private ITotalScanSignals initializeTotalIonSignals(int startScan, int stopScan, IMarkedIons excludedIons) {

		assert (startScan <= stopScan) : "The startScan must be lower or equal the stop scan.";
		/*
		 * Validate the scan borders.
		 */
		if(startScan < 1 || startScan > chromatogram.getNumberOfScans() || stopScan < 1 || stopScan > chromatogram.getNumberOfScans()) {
			return new TotalScanSignals(0, chromatogram);
		}
		/*
		 * Create the total ion signals object.
		 */
		ITotalScanSignal totalIonSignal;
		ITotalScanSignals signals = new TotalScanSignals(startScan, stopScan, chromatogram);
		
		/*
		 * Add the selected scans.
		 */
		IVendorMassSpectrum ms;
		for(int scan = startScan; scan <= stopScan; scan++) {
			ms = chromatogram.getSupplierScan(scan);
			totalIonSignal = new TotalScanSignal(ms.getRetentionTime(), ms.getRetentionIndex(), ms.getTotalSignal(excludedIons));
			signals.add(totalIonSignal);
		}
		return signals;
	}

	/**
	 * Returns an {@link ITotalScanSignals} object.<br/>
	 * If {@link ExcludedIons} is not null, the stored ions
	 * will be excluded.<br/>
	 * 
	 * @param excludedIons
	 * @return {@link ITotalScanSignals}
	 */
	private ITotalScanSignals initializeTotalIonSignals(IMarkedIons excludedIons) {

		ITotalScanSignal totalIonSignal;
		ITotalScanSignals signals = new TotalScanSignals(chromatogram.getNumberOfScans(), chromatogram);
		/*
		 * Get each scan signal.
		 */
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				/*
				 * Get the excluded version if necessary.
				 */
				totalIonSignal = new TotalScanSignal(ms.getRetentionTime(), ms.getRetentionIndex(), ms.getTotalSignal(excludedIons));
				signals.add(totalIonSignal);
			}
		}
		return signals;
	}
}
