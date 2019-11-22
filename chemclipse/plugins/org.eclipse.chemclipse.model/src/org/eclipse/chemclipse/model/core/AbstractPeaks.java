/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPeaks<T extends IPeak> implements IPeaks<T> {

	private final List<T> peaks;

	/**
	 * Initialize mass spectra and create a new internal mass spectra list.
	 */
	public AbstractPeaks(Class<T> type) {
		peaks = Collections.checkedList(new ArrayList<>(), type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addPeak(IPeak peak) {

		if(peak != null) {
			try {
				return peaks.add((T)peak);
			} catch(ClassCastException e) {
				// can't be added then
			}
		}
		return false;
	}

	@Override
	public boolean removePeak(IPeak peak) {

		if(peak != null) {
			return peaks.remove(peak);
		}
		return false;
	}

	@Override
	public List<T> getPeaks() {

		return peaks;
	}
}
