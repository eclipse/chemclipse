/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

public class ExtractedSingleWavelengthSignals implements IExtractedSingleWavelengthSignals {

	private static final Logger logger = Logger.getLogger(ExtractedSingleWavelengthSignals.class);
	private NavigableMap<Integer, IExtractedSingleWavelengthSignal> signals;
	private int startScan = 0;
	private int stopScan = 0;
	private IChromatogramWSD chromatogram = null;
	private double wavelength;

	public ExtractedSingleWavelengthSignals(int numberOfScans, double wavelength, IChromatogramWSD chromatogram) {

		if(numberOfScans <= 0) {
			numberOfScans = 0;
			startScan = 0;
			stopScan = 0;
		} else {
			startScan = 1;
			stopScan = numberOfScans;
		}
		signals = new TreeMap<>();
		this.wavelength = wavelength;
		this.chromatogram = chromatogram;
	}

	/**
	 * Creates a {@link IExtractedWavelengthSignals} instance.<br/>
	 * The start and stop scan needs also to be specified, if e.g. only a
	 * selection from scan 40 - 60 is chosen.
	 * 
	 * @param startScan
	 * @param stopScan
	 */
	public ExtractedSingleWavelengthSignals(int startScan, int stopScan, double wavelength, IChromatogramWSD chromatogram) {

		startScan = (startScan <= 0) ? 0 : startScan;
		stopScan = (stopScan <= 0) ? 0 : stopScan;
		int start = Math.min(startScan, stopScan);
		int stop = Math.max(startScan, stopScan);
		if(start == 0 || stop == 0) {
			start = 0;
			stop = 0;
		}
		signals = new TreeMap<>();
		this.startScan = start;
		this.stopScan = stop;
		this.wavelength = wavelength;
		this.chromatogram = chromatogram;
	}

	@Override
	public IChromatogramWSD getChromatogram() {

		return chromatogram;
	}

	@Override
	public void add(IExtractedSingleWavelengthSignal extractedWavelengthSignal) {

		int scan;
		if(signals.isEmpty()) {
			scan = startScan;
		} else {
			scan = signals.lastKey() + 1;
		}
		signals.put(scan, extractedWavelengthSignal);
	}

	@Override
	public void add(IExtractedSingleWavelengthSignal extractedWavelengthSignal, int scan) {

		signals.put(scan, extractedWavelengthSignal);
	}

	@Override
	public IExtractedSingleWavelengthSignal getTotalScanSignal(int scan) {

		if(scan < startScan || scan > stopScan) {
			return null;
		}
		IExtractedSingleWavelengthSignal signal = signals.get(scan);
		if(signal != null) {
			return signal;
		}
		if(chromatogram == null) {
			return null;
		}
		IScan s = chromatogram.getScan(scan);
		int retentionTime = s.getRetentionTime();
		float retentionIndex = s.getRetentionIndex();
		//
		IExtractedSingleWavelengthSignal floorSignal = signals.floorEntry(retentionTime).getValue();
		IExtractedSingleWavelengthSignal ceilSignal = signals.ceilingEntry(retentionTime).getValue();
		//
		Point p1 = new Point(floorSignal.getRetentionTime(), floorSignal.getTotalSignal());
		Point p2 = new Point(ceilSignal.getRetentionTime(), ceilSignal.getTotalSignal());
		LinearEquation eq = Equations.createLinearEquation(p1, p2);
		return new ExtractedSingleWavelengthSignalUnmodifiable(wavelength, (float)eq.calculateY(retentionTime), retentionTime, retentionIndex);
	}

	@Override
	public int size() {

		return signals.size();
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public IExtractedSingleWavelengthSignals makeDeepCopy() {

		IExtractedSingleWavelengthSignals extractedWavelengthSignals = new ExtractedSingleWavelengthSignals(startScan, stopScan, wavelength, chromatogram);
		//
		for(Entry<Integer, IExtractedSingleWavelengthSignal> entry : signals.entrySet()) {
			extractedWavelengthSignals.add(entry.getValue().makeDeepCopy(), entry.getKey());
		}
		return extractedWavelengthSignals;
	}

	@Override
	public double getWavelength() {

		return wavelength;
	}

	@Override
	public void add(ITotalScanSignal totalScanSignal) {

		if(totalScanSignal instanceof IExtractedSingleWavelengthSignal) {
			add((IExtractedSingleWavelengthSignal)totalScanSignal);
		} else {
			// TODO: exception
		}
	}

	@Override
	public List<ITotalScanSignal> getTotalScanSignals() {

		return new ArrayList<>(signals.values());
	}

	@Override
	public Collection<ITotalScanSignal> getTotalScanSignalCollection() {

		return Collections.unmodifiableCollection(signals.values());
	}

	@Override
	public Iterator<Integer> iterator() {

		return Collections.unmodifiableSet(signals.keySet()).iterator();
	}
}
