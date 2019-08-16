/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IAllIonSignals;

public class PeakRanges implements IPeakRanges {

	private int startScan;
	private int stopScan;
	private List<IPeakRange> peakRanges;

	public PeakRanges(IAllIonSignals signals) {
		startScan = signals.getStartScan();
		stopScan = signals.getStopScan();
		peakRanges = new ArrayList<IPeakRange>(0);
	}

	public int getStartScan() {

		return startScan;
	}

	public int getStopScan() {

		return stopScan;
	}

	public IPeakRange getPeakRange(int value) {

		return peakRanges.get(value);
	}

	public List<IPeakRange> getAllPeakRanges() {

		return peakRanges;
	}

	public void addPeakRange(IPeakRange peakRange) {

		peakRanges.add(peakRange);
	}

	public void deletePeakRange(int value) {

		peakRanges.remove(value);
	}

	public int size() {

		return peakRanges.size();
	}
}
