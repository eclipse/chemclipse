/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.core;

import java.util.Comparator;

import org.eclipse.chemclipse.numeric.miscellaneous.SortOrder;

/**
 * This comparator compares the y values of an {@link IPoint} instance. The
 * values will be sorted ascending or descending.<br/>
 * Ascending is the default sort order.
 * 
 * @author eselmeister
 */
public class PointYComparator implements Comparator<IPoint> {

	private SortOrder sortOrder = SortOrder.ASCENDING;

	public PointYComparator() {
		sortOrder = SortOrder.ASCENDING;
	}

	/**
	 * Sorts the given IPoints by the given sort order.
	 * 
	 * @param sortOrder
	 */
	public PointYComparator(SortOrder sortOrder) {
		if(sortOrder != null) {
			this.sortOrder = sortOrder;
		}
	}

	@Override
	public int compare(IPoint point1, IPoint point2) {

		if(point1 == null || point2 == null) {
			return 0;
		}
		//
		int result;
		switch(sortOrder) {
			case ASCENDING:
				result = Double.compare(point1.getY(), point2.getY());
				break;
			case DESCENDING:
				result = Double.compare(point2.getY(), point1.getY());
				break;
			default:
				result = Double.compare(point1.getY(), point2.getY());
				break;
		}
		return result;
	}
}
