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

import org.eclipse.chemclipse.model.core.IScan;
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

		IExtractedIonSignals extractedSignals = new ExtractedIonSignals(getNumberOfScansWithIons(chromatogram), chromatogram);
		exitloop:
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanMSD) {
				if(!extractSignals(extractedSignals, (IScanMSD)scan, startIon, stopIon)) {
					break exitloop;
				}
			}
		}
		return extractedSignals;
	}

	@Override
	public IExtractedIonSignals getExtractedIonSignals() {

		IExtractedIonSignals extractedSignals = new ExtractedIonSignals(getNumberOfScansWithIons(chromatogram), chromatogram);
		exitloop:
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanMSD) {
				if(!extractSignals(extractedSignals, (IScanMSD)scan, 0, 0)) {
					break exitloop;
				}
			}
		}
		return extractedSignals;
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
		//
		if(startScan < 1 && stopScan > getNumberOfScansWithIons(chromatogram)) {
			return new ExtractedIonSignals(0, chromatogram);
		}
		/*
		 * Get the start without empty scans.
		 */
		exitloop:
		for(int scan = startScan; scan <= stopScan; scan++) {
			if(chromatogram.getSupplierScan(scan).getNumberOfIons() > 0) {
				startScan = scan;
				break exitloop;
			}
		}
		/*
		 * Get the stop without empty scans.
		 */
		exitloop:
		for(int scan = startScan; scan <= stopScan; scan++) {
			if(chromatogram.getSupplierScan(scan).getNumberOfIons() == 0) {
				stopScan = scan;
				break exitloop;
			}
		}
		//
		IExtractedIonSignals extractedIonSignals = new ExtractedIonSignals(startScan, stopScan, chromatogram);
		for(int scan = startScan; scan <= stopScan; scan++) {
			extractSignals(extractedIonSignals, chromatogram.getSupplierScan(scan), 0, 0);
		}
		//
		return extractedIonSignals;
	}

	/**
	 * Calculates the number of scans until one scan without ions is detected.
	 * 
	 * @param chromatogram
	 * @return int
	 */
	private int getNumberOfScansWithIons(IChromatogramMSD chromatogram) {

		int counter = 0;
		exitloop:
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanMSD) {
				IScanMSD massSpectrum = (IScanMSD)scan;
				if(massSpectrum.getNumberOfIons() > 0) {
					counter++;
				} else {
					break exitloop;
				}
			}
		}
		return counter;
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
