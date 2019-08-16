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

/**
 * This is the comparator to sort an list of ion values by its ion
 * value.
 */
public class WavelengthValueComparator implements Comparator<IScanSignalWSD>, Serializable {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 8463067630896954737L;
	private SortOrder sortOrder;

	public WavelengthValueComparator() {
		sortOrder = SortOrder.ASC;
	}

	public WavelengthValueComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IScanSignalWSD scanSignal1, IScanSignalWSD scanSignal2) {

		int returnValue;
		switch(sortOrder) {
			case ASC:
				returnValue = Double.compare(scanSignal1.getWavelength(), scanSignal2.getWavelength());
				break;
			case DESC:
				returnValue = Double.compare(scanSignal2.getWavelength(), scanSignal1.getWavelength());
				break;
			default:
				returnValue = Double.compare(scanSignal1.getWavelength(), scanSignal2.getWavelength());
				break;
		}
		return returnValue;
	}
}
