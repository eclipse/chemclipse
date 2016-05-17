/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.support.ui.swt.EnhancedViewerSorter;
import org.eclipse.jface.viewers.Viewer;

public class RetentionIndexTableSorter extends EnhancedViewerSorter {

	private int propertyIndex;
	private static final int ASCENDING = 0;
	private int direction = ASCENDING;

	public RetentionIndexTableSorter() {
		propertyIndex = 0;
		direction = ASCENDING;
	}

	public void setColumn(int column) {

		if(column == this.propertyIndex) {
			// Toggle the direction
			direction = 1 - direction;
		} else {
			this.propertyIndex = column;
			direction = ASCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IRetentionIndexEntry && e2 instanceof IRetentionIndexEntry) {
			IRetentionIndexEntry modelStandard1 = (IRetentionIndexEntry)e1;
			IRetentionIndexEntry modelStandard2 = (IRetentionIndexEntry)e2;
			switch(propertyIndex) {
				case 0:
					sortOrder = Integer.compare(modelStandard2.getRetentionTime(), modelStandard1.getRetentionTime());
					break;
				case 1:
					sortOrder = Float.compare(modelStandard2.getRetentionIndex(), modelStandard1.getRetentionIndex());
					break;
				case 2:
					sortOrder = modelStandard2.getName().compareTo(modelStandard1.getName());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(direction == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
