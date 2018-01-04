/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPeaks implements IPeaks {

	private List<IPeak> peaks;

	/**
	 * Initialize mass spectra and create a new internal mass spectra list.
	 */
	public AbstractPeaks() {
		peaks = new ArrayList<IPeak>();
	}

	@Override
	public void addPeak(IPeak peak) {

		if(peak != null) {
			peaks.add(peak);
		}
	}

	@Override
	public void removePeak(IPeak peak) {

		if(peak != null) {
			peaks.remove(peak);
		}
	}

	@Override
	public IPeak getPeak(int i) {

		IPeak peak = null;
		if(i > 0 && i <= peaks.size()) {
			peak = peaks.get(--i);
		}
		return peak;
	}

	@Override
	public List<IPeak> getPeaks() {

		return peaks;
	}

	@Override
	public int size() {

		return peaks.size();
	}
}
