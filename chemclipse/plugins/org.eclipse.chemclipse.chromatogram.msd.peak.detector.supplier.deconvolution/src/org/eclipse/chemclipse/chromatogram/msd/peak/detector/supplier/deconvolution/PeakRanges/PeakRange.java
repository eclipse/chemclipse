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

public class PeakRange implements IPeakRange {

	private int startPoint;
	private int endPoint;
	private int widthPeakRange;
	private double signalToNoise;
	private List<IPeaksDeconv> peakList;

	public PeakRange(int peakStart, int peakEnd, double snr) {
		signalToNoise = snr;
		startPoint = peakStart;
		endPoint = peakEnd;
		widthPeakRange = peakEnd - peakStart;
		peakList = new ArrayList<IPeaksDeconv>(0);
	}

	public int getPeakStartPoint() {

		return startPoint;
	}

	public int getPeakEndPoint() {

		return endPoint;
	}

	public int getwidthPeakRange() {

		return widthPeakRange;
	}

	public double getSignalToNoise() {

		return signalToNoise;
	}

	public IPeaksDeconv getPeakList(int value) {

		return peakList.get(value);
	}

	public void setPeakStartPoint(int value) {

		this.startPoint = value;
	}

	public void setPeakEndPoint(int value) {

		this.endPoint = value;
	}

	public void setwidthPeakRange(int value) {

		this.widthPeakRange = value;
	}

	public void setSignalToNoise(double snr) {

		this.signalToNoise = snr;
	}

	public int sizePeakList() {

		return peakList.size();
	}

	public void addPeaks(IPeaksDeconv peaks) {

		peakList.add(peaks);
	}
}
