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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.swt.EnhancedViewerSorter;
import org.eclipse.jface.viewers.Viewer;

public class OverlaySettingsTableSorter extends EnhancedViewerSorter {

	private int propertyIndex;
	private static final int ASCENDING = 0;
	private int direction = ASCENDING;

	public OverlaySettingsTableSorter() {
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

	@SuppressWarnings("rawtypes")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IChromatogramSelection && e2 instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection1 = (IChromatogramSelection)e1;
			IChromatogramSelection chromatogramSelection2 = (IChromatogramSelection)e2;
			switch(propertyIndex) {
				case 0:
					sortOrder = chromatogramSelection2.getChromatogram().getName().compareTo(chromatogramSelection1.getChromatogram().getName());
					break;
				case 1:
					sortOrder = Boolean.compare(chromatogramSelection2.isOverlaySelected(), chromatogramSelection1.isOverlaySelected());
					break;
				case 2:
					sortOrder = Boolean.compare(chromatogramSelection2.isLockOffset(), chromatogramSelection1.isLockOffset());
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
