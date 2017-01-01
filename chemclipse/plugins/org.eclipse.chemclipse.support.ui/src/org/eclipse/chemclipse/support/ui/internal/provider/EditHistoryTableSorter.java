/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class EditHistoryTableSorter extends ViewerSorter {

	private int propertyIndex = -1;
	private static final int ASCENDING = 0;
	private int direction = ASCENDING;

	public EditHistoryTableSorter() {
		propertyIndex = 0;
		direction = ASCENDING;
	}

	public void setColumn(int column) {

		/*
		 * Toggle the direction
		 */
		if(column == this.propertyIndex) {
			direction = 1 - direction;
		} else {
			direction = ASCENDING;
		}
		propertyIndex = column;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IEditInformation && e2 instanceof IEditInformation) {
			IEditInformation info1 = (IEditInformation)e1;
			IEditInformation info2 = (IEditInformation)e2;
			switch(propertyIndex) {
				case 0: // Date
					sortOrder = info2.getDate().compareTo(info1.getDate());
					break;
				case 1: // Description
					sortOrder = info2.getDescription().compareTo(info1.getDescription());
					break;
				case 2: // Editor
					sortOrder = info2.getEditor().compareTo(info1.getEditor());
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
