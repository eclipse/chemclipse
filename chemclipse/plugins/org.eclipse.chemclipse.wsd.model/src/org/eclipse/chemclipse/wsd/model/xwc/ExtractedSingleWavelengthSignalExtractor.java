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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;

public class ExtractedSingleWavelengthSignalExtractor implements IExtractedSingleWavelengthSignalExtractor {

	private IChromatogramWSD chromatogram;
	private boolean joinSignal;

	/**
	 * All values will be extracted from IChromatogram.
	 * 
	 * @param chromatogram
	 * @throws ChromatogramIsNullException
	 */
	public ExtractedSingleWavelengthSignalExtractor(IChromatogramWSD chromatogram, boolean joinSignal) throws ChromatogramIsNullException {

		if(chromatogram == null) {
			throw new ChromatogramIsNullException();
		}
		this.chromatogram = chromatogram;
		this.joinSignal = joinSignal;
	}

	@Override
	public List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection) {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() != chromatogram) {
			List<IExtractedSingleWavelengthSignals> extractedWavelengthSignals = new ArrayList<>();
			extractedWavelengthSignals.add(new ExtractedSingleWavelengthSignals(0, Double.NaN, chromatogram));
			return extractedWavelengthSignals;
		}
		/*
		 * Get the start and stop scan.
		 */
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IMarkedWavelengths markedWavelengths = chromatogramSelection.getSelectedWavelengths();
		return getExtractedWavelengthSignals(startScan, stopScan, markedWavelengths, joinSignal);
	}

	@Override
	public List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(int startScan, int stopScan, IMarkedWavelengths markedWavelengths) {

		return getExtractedWavelengthSignals(startScan, stopScan, markedWavelengths, joinSignal);
	}

	private List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(int startScan, int stopScan, IMarkedWavelengths markedWavelengths, boolean join) {

		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		/*
		 * check
		 */
		List<IExtractedSingleWavelengthSignals> extractedWavelengthSignals = new ArrayList<>();
		if(startScan < 1 && stopScan > getNumberOfScansWithWavelengths(chromatogram)) {
			extractedWavelengthSignals.add(new ExtractedSingleWavelengthSignals(0, Double.NaN, chromatogram));
			return extractedWavelengthSignals;
		}
		Iterator<Double> it = markedWavelengths.getWavelengths().stream().sorted().iterator();
		while(it.hasNext()) {
			double wavelength = it.next();
			SortedMap<Integer, IExtractedSingleWavelengthSignal> extractedSignalsMap = null;
			int startScanSignal = 0;
			int stopScanSignal = 0;
			for(int scan = startScan; scan <= stopScan; scan++) {
				IScanWSD scanWSD = chromatogram.getSupplierScan(scan);
				if(scanWSD.getScanSignals().size() > 0) {
					/*
					 * 
					 */
					Optional<IExtractedSingleWavelengthSignal> extractedWavelengthSignal = scanWSD.getExtractedSingleWavelengthSignal(wavelength);
					if(extractedWavelengthSignal.isPresent()) {
						/*
						 * if signal on wavelength exist
						 */
						if(extractedSignalsMap == null) {
							extractedSignalsMap = new TreeMap<>();
							extractedSignalsMap.put(scan, extractedWavelengthSignal.get());
							startScanSignal = scan;
							stopScanSignal = scan;
						} else {
							extractedSignalsMap.put(scan, extractedWavelengthSignal.get());
							stopScanSignal = scan;
						}
					} else if(extractedSignalsMap != null && !join) {
						/*
						 * if signal stop earlier and interval should not be join
						 */
						IExtractedSingleWavelengthSignals extractedSingleWavelengthSignals = new ExtractedSingleWavelengthSignals(startScanSignal, stopScanSignal, wavelength, chromatogram);
						for(Entry<Integer, IExtractedSingleWavelengthSignal> entry : extractedSignalsMap.entrySet()) {
							extractedSingleWavelengthSignals.add(entry.getValue(), entry.getKey());
						}
						extractedWavelengthSignals.add(extractedSingleWavelengthSignals);
						extractedSignalsMap = null;
					}
				}
			}
			if(extractedSignalsMap != null) {
				IExtractedSingleWavelengthSignals extractedIonSignals = new ExtractedSingleWavelengthSignals(startScanSignal, stopScanSignal, wavelength, chromatogram);
				for(Entry<Integer, IExtractedSingleWavelengthSignal> entry : extractedSignalsMap.entrySet()) {
					extractedIonSignals.add(entry.getValue(), entry.getKey());
				}
				extractedWavelengthSignals.add(extractedIonSignals);
			}
		}
		return extractedWavelengthSignals;
	}

	/**
	 * 
	 * @param chromatogram
	 * @return int
	 */
	private int getNumberOfScansWithWavelengths(IChromatogramWSD chromatogram) {

		int counter = 0;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)scan;
				if(scanWSD.getScanSignals().size() > 0) {
					counter++;
				}
			}
		}
		return counter;
	}

	@Override
	public List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals() {

		IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
		chromatogram.getScans().forEach(s -> {
			IScanWSD scanWSD = (IScanWSD)s;
			scanWSD.getScanSignals().forEach(signal -> markedWavelengths.add(signal.getWavelength()));
		});
		return getExtractedWavelengthSignals(1, chromatogram.getNumberOfScans(), markedWavelengths, joinSignal);
	}

	@Override
	public List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(int startScan, int stopScan) {

		IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
		for(int i = startScan; i <= stopScan; i++) {
			chromatogram.getSupplierScan(i).getScanSignals().forEach(signal -> markedWavelengths.add(signal.getWavelength()));
		}
		return getExtractedWavelengthSignals(startScan, stopScan, markedWavelengths, joinSignal);
	}

	@Override
	public List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(IMarkedWavelengths markedWavelengths) {

		return getExtractedWavelengthSignals(1, chromatogram.getNumberOfScans(), markedWavelengths, joinSignal);
	}

	@Override
	public boolean isJoinSignal() {

		return joinSignal;
	}

	@Override
	public Optional<IExtractedSingleWavelengthSignals> getExtractWavelengthContinuousSignal(int startScan, int stopScan, IMarkedWavelength markedWavelength) {

		IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
		markedWavelengths.add(markedWavelength);
		List<IExtractedSingleWavelengthSignals> extracetedSignals = getExtractedWavelengthSignals(startScan, stopScan, markedWavelengths, false);
		if(extracetedSignals.size() == 1) {
			IExtractedSingleWavelengthSignals extracetedSignal = extracetedSignals.get(0);
			if(extracetedSignal.getStartScan() == startScan && extracetedSignal.getStopScan() == stopScan) {
				return Optional.of(extracetedSignal);
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<IExtractedSingleWavelengthSignals> getExtractWavelengthContinuousSignal(IMarkedWavelength markedWavelength) {

		return getExtractWavelengthContinuousSignal(1, chromatogram.getNumberOfScans(), markedWavelength);
	}

	@Override
	public void setJoinSignal(boolean joinSignal) {

		this.joinSignal = joinSignal;
	}
}
