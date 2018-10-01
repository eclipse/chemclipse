/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.Comparator;

import org.eclipse.chemclipse.support.comparator.SortOrder;

public class ExtractedSingleWavelengthSignalComparator implements Comparator<IExtractedSingleWavelengthSignal> {

	private SortOrder sortOrder = SortOrder.ASC;

	public ExtractedSingleWavelengthSignalComparator(SortOrder sortOrder) {
		if(sortOrder != null) {
			this.sortOrder = sortOrder;
		}
	}

	@Override
	public int compare(IExtractedSingleWavelengthSignal signal1, IExtractedSingleWavelengthSignal signal2) {

		int result = 0;
		if(signal1 == null || signal2 == null) {
			return 0;
		}
		if(signal1.getRetentionTime() == signal2.getRetentionTime()) {
			result = 0;
		}
		switch(sortOrder) {
			case ASC:
				result = signal2.getRetentionTime() - signal1.getRetentionTime();
				break;
			case DESC:
				result = signal1.getRetentionTime() - signal2.getRetentionTime();
				break;
			default:
				result = signal2.getRetentionTime() - signal1.getRetentionTime();
		}
		return result;
	}
}
