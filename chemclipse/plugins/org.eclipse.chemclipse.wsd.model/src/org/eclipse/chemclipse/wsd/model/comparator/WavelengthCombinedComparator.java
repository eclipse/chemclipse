/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;

public class WavelengthCombinedComparator implements Comparator<IScanSignalWSD>, Serializable {

	private static final long serialVersionUID = -109573471700765379L;
	private final Comparator<IScanSignalWSD> firstComparator;
	private final Comparator<IScanSignalWSD> secondComparator;

	/**
	 * The sort order is per default value ascending.
	 */
	public WavelengthCombinedComparator(WavelengthComparatorMode wavelengthComparatorMode) {
		this(wavelengthComparatorMode, SortOrder.ASC);
	}

	public WavelengthCombinedComparator(WavelengthComparatorMode ionComparatorMode, SortOrder sortOrder) {
		switch(ionComparatorMode) {
			case ABUNDANCE_FIRST:
				firstComparator = new WavelengthAbundanceComparator(sortOrder);
				secondComparator = new WavelengthValueComparator(sortOrder);
				break;
			case WAVELENGTH_FIRST:
				firstComparator = new WavelengthValueComparator(sortOrder);
				secondComparator = new WavelengthAbundanceComparator(sortOrder);
				break;
			default:
				firstComparator = new WavelengthAbundanceComparator(sortOrder);
				secondComparator = new WavelengthValueComparator(sortOrder);
				break;
		}
	}

	@Override
	public int compare(IScanSignalWSD scanSignal1, IScanSignalWSD scanSignal2) {

		int result = firstComparator.compare(scanSignal1, scanSignal2);
		if(result != 0) {
			return result;
		}
		result = secondComparator.compare(scanSignal1, scanSignal2);
		return result;
	}
}
