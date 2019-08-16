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

public class PeaksDeconv implements IPeaksDeconv {

	private List<IPeakDeconv> peaks;
	private String whereGotPeak;

	public PeaksDeconv(String whereArePeakDetect) {
		whereGotPeak = whereArePeakDetect;
		peaks = new ArrayList<IPeakDeconv>(0);
	}

	public String getWhereGotPeak() {

		return whereGotPeak;
	}

	public void setWhereGotPeak(String text) {

		this.whereGotPeak = text;
	}

	public IPeakDeconv getPeakDeconv(int value) {

		return peaks.get(value);
	}

	public int sizePeakDeconv() {

		return peaks.size();
	}

	public void addPeak(IPeakDeconv peak) {

		peaks.add(peak);
	}
}
