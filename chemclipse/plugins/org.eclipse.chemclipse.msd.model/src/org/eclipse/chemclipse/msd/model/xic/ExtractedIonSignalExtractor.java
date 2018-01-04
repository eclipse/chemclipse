/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
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

		IExtractedIonSignals signals = new ExtractedIonSignals(getNumberOfScansWithIons(chromatogram), chromatogram);
		IExtractedIonSignal extractedIonSignal;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scan;
				if(massSpectrum.getNumberOfIons() > 0) {
					extractedIonSignal = massSpectrum.getExtractedIonSignal(startIon, stopIon);
					signals.add(extractedIonSignal);
				}
			}
		}
		return signals;
	}

	@Override
	public IExtractedIonSignals getExtractedIonSignals() {

		IExtractedIonSignals signals = new ExtractedIonSignals(getNumberOfScansWithIons(chromatogram), chromatogram);
		IExtractedIonSignal extractedIonSignal;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scan;
				if(massSpectrum.getNumberOfIons() > 0) {
					extractedIonSignal = massSpectrum.getExtractedIonSignal();
					signals.add(extractedIonSignal);
				}
			}
		}
		return signals;
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

		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		if(startScan < 1 && stopScan > getNumberOfScansWithIons(chromatogram)) {
			return new ExtractedIonSignals(0, chromatogram);
		}
		IVendorMassSpectrum massSpectrum;
		IExtractedIonSignals extractedIonSignals = new ExtractedIonSignals(startScan, stopScan, chromatogram);
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = chromatogram.getSupplierScan(scan);
			if(massSpectrum.getNumberOfIons() > 0) {
				extractedIonSignals.add(massSpectrum.getExtractedIonSignal());
			}
		}
		return extractedIonSignals;
	}

	/**
	 * 
	 * @param chromatogram
	 * @return int
	 */
	private int getNumberOfScansWithIons(IChromatogramMSD chromatogram) {

		int counter = 0;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scan;
				if(massSpectrum.getNumberOfIons() > 0) {
					counter++;
				}
			}
		}
		return counter;
	}
}
