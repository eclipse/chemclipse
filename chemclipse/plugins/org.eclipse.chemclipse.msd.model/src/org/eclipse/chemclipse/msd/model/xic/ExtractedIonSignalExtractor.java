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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public class ExtractedIonSignalExtractor implements IExtractedIonSignalExtractor {

	private IChromatogramMSD chromatogram;

	/**
	 * All values will be extracted from IChromatogram.
	 * 
	 * @param chromatogram
	 * @throws ChromatogramIsNullException
	 */
	public ExtractedIonSignalExtractor(IChromatogramMSD chromatogram) throws ChromatogramIsNullException {
		if(chromatogram == null) {
			throw new ChromatogramIsNullException();
		}
		this.chromatogram = chromatogram;
	}

	@Override
	public IExtractedIonSignals getExtractedIonSignals(float startIon, float stopIon) {

		int startScan = 1;
		int stopScan = chromatogram.getNumberOfScans();
		return getExtractedIonSignals(startScan, stopScan, startIon, stopIon);
	}

	@Override
	public IExtractedIonSignals getExtractedIonSignals() {

		int startScan = 1;
		int stopScan = chromatogram.getNumberOfScans();
		return getExtractedIonSignals(startScan, stopScan);
	}

	@Override
	public IExtractedIonSignals getExtractedIonSignals(IChromatogramSelectionMSD chromatogramSelection) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() != chromatogram) {
			return new ExtractedIonSignals(0, chromatogram);
		}
		/*
		 * Get the start and stop scan.
		 */
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		return getExtractedIonSignals(startScan, stopScan);
	}

	@Override
	public IExtractedIonSignals getExtractedIonSignals(int startScan, int stopScan) {

		return getExtractedIonSignals(startScan, stopScan, 0, 0);
	}

	private IExtractedIonSignals getExtractedIonSignals(int startScan, int stopScan, float startIon, float stopIon) {

		if(chromatogram.getNumberOfScans() == 0) {
			return new ExtractedIonSignals(0, chromatogram);
		}
		/*
		 * Adjust the range.
		 */
		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		/*
		 * Do additional checks.
		 */
		stopScan = (stopScan > chromatogram.getNumberOfScans()) ? chromatogram.getNumberOfScans() : stopScan;
		int start = (startScan < 1) ? 1 : startScan;
		int stop = stopScan;
		/*
		 * Get the start without empty scans.
		 */
		exitloop:
		for(int scan = start; scan <= stop; scan++) {
			if(chromatogram.getSupplierScan(scan).getNumberOfIons() > 0) {
				startScan = scan;
				break exitloop;
			}
		}
		/*
		 * Get the stop without empty scans.
		 */
		for(int scan = stop; scan > startScan; scan--) {
			if(chromatogram.getSupplierScan(scan).getNumberOfIons() == 0) {
				stopScan = scan - 1;
			}
		}
		/*
		 * Extract the signals.
		 */
		IExtractedIonSignals extractedIonSignals = new ExtractedIonSignals(startScan, stopScan, chromatogram);
		for(int scan = startScan; scan <= stopScan; scan++) {
			extractSignals(extractedIonSignals, chromatogram.getSupplierScan(scan), startIon, stopIon);
		}
		//
		return extractedIonSignals;
	}

	private boolean extractSignals(IExtractedIonSignals extractedIonSignals, IScanMSD scanMSD, float startIon, float stopIon) {

		if(scanMSD.getNumberOfIons() > 0) {
			if(startIon == 0 && stopIon == 0) {
				extractedIonSignals.add(scanMSD.getExtractedIonSignal());
			} else {
				extractedIonSignals.add(scanMSD.getExtractedIonSignal(startIon, stopIon));
			}
			return true;
		}
		return false;
	}
}
