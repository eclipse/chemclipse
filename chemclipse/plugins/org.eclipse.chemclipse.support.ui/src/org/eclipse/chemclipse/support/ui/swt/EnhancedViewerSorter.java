/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.jface.viewers.ViewerSorter;

public class EnhancedViewerSorter extends ViewerSorter {

	public static final int ASCENDING = 0;
	private int propertyIndex = -1;
	private int direction = ASCENDING;

	public EnhancedViewerSorter() {
		propertyIndex = 0;
		direction = ASCENDING;
	}

	public void setColumn(int column) {

		/*
		 * Toggle the direction
		 */
		if(column == propertyIndex) {
			direction = 1 - direction;
		} else {
			direction = ASCENDING;
		}
		propertyIndex = column;
	}

	public int getPropertyIndex() {

		return propertyIndex;
	}

	public int getDirection() {

		return direction;
	}
}
