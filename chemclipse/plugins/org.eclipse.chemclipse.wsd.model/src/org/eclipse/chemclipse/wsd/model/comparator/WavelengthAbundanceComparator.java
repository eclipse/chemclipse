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

public class WavelengthAbundanceComparator implements Comparator<IScanSignalWSD>, Serializable {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 2053994137218915007L;
	private SortOrder sortOrder;

	/**
	 * The sort order is per default value ascending.
	 */
	public WavelengthAbundanceComparator() {
		sortOrder = SortOrder.ASC;
	}

	/**
	 * You can choose whether to sort the abundance values ascending (asc) or
	 * descending (desc).
	 * 
	 * @param sortOrder
	 */
	public WavelengthAbundanceComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IScanSignalWSD scanSignal1, IScanSignalWSD scanSignal2) {

		int returnValue;
		switch(sortOrder) {
			case ASC:
				returnValue = Float.compare(scanSignal1.getAbundance(), scanSignal2.getAbundance());
				break;
			case DESC:
				returnValue = Float.compare(scanSignal2.getAbundance(), scanSignal1.getAbundance());
				break;
			default:
				returnValue = Float.compare(scanSignal1.getAbundance(), scanSignal2.getAbundance());
				break;
		}
		return returnValue;
	}
}
