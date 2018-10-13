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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

public class ExtractedSingleWavelengthSignals implements IExtractedSingleWavelengthSignals {

	private static final Logger logger = Logger.getLogger(ExtractedSingleWavelengthSignals.class);
	private List<IExtractedSingleWavelengthSignal> signals;
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
		signals = new ArrayList<>(numberOfScans);
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
		int numberOfScans;
		if(start == 0 || stop == 0) {
			numberOfScans = 0;
			start = 0;
			stop = 0;
		} else {
			numberOfScans = stop - start + 1;
		}
		signals = new ArrayList<>(numberOfScans);
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

		signals.add(extractedWavelengthSignal);
	}

	@Override
	public IExtractedSingleWavelengthSignal getTotalScanSignal(int scan) {

		if(scan <= 0) {
			return null;
		}
		if(scan < startScan || scan > stopScan) {
			return null;
		}
		int correction = startScan - 1;
		scan -= correction;
		return signals.get(--scan);
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
		for(ITotalScanSignal signal : signals) {
			extractedWavelengthSignals.add(signal.makeDeepCopy());
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

		return new ArrayList<>(signals);
	}

	@Override
	public List<ITotalScanSignal> getTotalScanSignalList() {

		return Collections.unmodifiableList(signals);
	}

	@Override
	public Iterator<Integer> iterator() {

		return new Iterator<Integer>() {

			private int startScan = getStartScan();
			private int stopScan = getStopScan();
			private int actualScan = getStartScan();

			@Override
			public Integer next() {

				if(!this.hasNext()) {
					throw new NoSuchElementException();
				}
				return actualScan++;
			}

			@Override
			public boolean hasNext() {

				if(startScan == 0 || stopScan == 0) {
					return false;
				}
				return actualScan <= stopScan;
			}
		};
	}
}
