/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IPeak && e2 instanceof IPeak) {
			//
			IPeak peak1 = (IPeak)e1;
			IPeak peak2 = (IPeak)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Boolean.compare(peak2.isActiveForAnalysis(), peak1.isActiveForAnalysis());
					break;
				case 1:
					sortOrder = Integer.compare(peak2.getPeakModel().getRetentionTimeAtPeakMaximum(), peak1.getPeakModel().getRetentionTimeAtPeakMaximum());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
