/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.model.core;

import java.util.Comparator;

import org.eclipse.chemclipse.support.comparator.SortOrder;

public class RetentionIndexComparator implements Comparator<IRetentionIndex> {

	private SortOrder sortOrder;

	public RetentionIndexComparator() {
		sortOrder = SortOrder.ASC;
	}

	public RetentionIndexComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IRetentionIndex retentionIndex1, IRetentionIndex retentionIndex2) {

		int returnValue;
		switch(sortOrder) {
			case ASC:
				returnValue = Float.compare(retentionIndex1.getIndex(), retentionIndex2.getIndex());
				break;
			case DESC:
				returnValue = Float.compare(retentionIndex2.getIndex(), retentionIndex1.getIndex());
				break;
			default:
				returnValue = Float.compare(retentionIndex1.getIndex(), retentionIndex2.getIndex());
				break;
		}
		return returnValue;
	}
}
