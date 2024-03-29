/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class FlavorMarkerComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IFlavorMarker marker1 && e2 instanceof IFlavorMarker marker2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					/*
					 * Checked is ranked higher than unchecked, hence reverse order here.
					 */
					sortOrder = Boolean.compare(marker2.isManuallyVerified(), marker1.isManuallyVerified());
					break;
				case 1:
					sortOrder = marker1.getOdor().compareTo(marker2.getOdor());
					break;
				case 2:
					sortOrder = marker1.getMatrix().compareTo(marker2.getMatrix());
					break;
				case 3:
					sortOrder = marker1.getSolvent().compareTo(marker2.getSolvent());
					break;
				default:
					sortOrder = 0;
			}
		}
		//
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		//
		return sortOrder;
	}
}
