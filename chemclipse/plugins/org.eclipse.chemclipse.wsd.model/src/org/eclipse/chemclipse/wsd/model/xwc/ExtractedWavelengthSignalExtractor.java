/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public class ExtractedWavelengthSignalExtractor implements IExtractedWavelengthSignalExtractor {

	private IChromatogramWSD chromatogram;

	/**
	 * All values will be extracted from IChromatogram.
	 * 
	 * @param chromatogram
	 * @throws ChromatogramIsNullException
	 */
	public ExtractedWavelengthSignalExtractor(IChromatogramWSD chromatogram) throws ChromatogramIsNullException {

		if(chromatogram == null) {
			throw new ChromatogramIsNullException();
		}
		this.chromatogram = chromatogram;
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals(float startWavelength, float stopWavelength) {

		IExtractedWavelengthSignals signals = new ExtractedWavelengthSignals(getNumberOfScansWithWavelengths(chromatogram), chromatogram);
		IExtractedWavelengthSignal extractedWavelengthSignal;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD scanWSD) {
				if(!scanWSD.getScanSignals().isEmpty()) {
					extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal(startWavelength, stopWavelength);
					signals.add(extractedWavelengthSignal);
				}
			}
		}
		return signals;
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals() {

		IExtractedWavelengthSignals signals = new ExtractedWavelengthSignals(getNumberOfScansWithWavelengths(chromatogram), chromatogram);
		IExtractedWavelengthSignal extractedWavelengthSignal;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD scanWSD) {
				if(!scanWSD.getScanSignals().isEmpty()) {
					extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
					signals.add(extractedWavelengthSignal);
				}
			}
		}
		return signals;
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() != chromatogram) {
			return new ExtractedWavelengthSignals(0, chromatogram);
		}
		/*
		 * Get the start and stop scan.
		 */
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		return getExtractedWavelengthSignals(startScan, stopScan);
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals(int startScan, int stopScan) {

		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		if(startScan < 1 && stopScan > getNumberOfScansWithWavelengths(chromatogram)) {
			return new ExtractedWavelengthSignals(0, chromatogram);
		}
		IScanWSD scanWSD;
		IExtractedWavelengthSignals extractedIonSignals = new ExtractedWavelengthSignals(startScan, stopScan, chromatogram);
		for(int scan = startScan; scan <= stopScan; scan++) {
			scanWSD = chromatogram.getSupplierScan(scan);
			if(!scanWSD.getScanSignals().isEmpty()) {
				IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
				extractedWavelengthSignal.setRetentionTime(scanWSD.getRetentionTime());
				extractedIonSignals.add(extractedWavelengthSignal);
			}
		}
		return extractedIonSignals;
	}

	/**
	 * 
	 * @param chromatogram
	 * @return int
	 */
	private int getNumberOfScansWithWavelengths(IChromatogramWSD chromatogram) {

		int counter = 0;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD scanWSD) {
				if(!scanWSD.getScanSignals().isEmpty()) {
					counter++;
				}
			}
		}
		return counter;
	}
}
