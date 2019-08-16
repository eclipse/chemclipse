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

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ChromtogramScanInfoTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IScanMSD && e2 instanceof IScanMSD) {
			IScanMSD ion1 = (IScanMSD)e1;
			IScanMSD ion2 = (IScanMSD)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(ion2.getScanNumber(), ion1.getScanNumber());
					break;
				case 1:
					sortOrder = Integer.compare(ion2.getRetentionTime(), ion1.getRetentionTime());
					break;
				case 2:
					sortOrder = Integer.compare(ion2.getNumberOfIons(), ion1.getNumberOfIons());
					break;
				case 3:
					sortOrder = Integer.compare(ion2.getNumberOfIons(), ion1.getNumberOfIons());
					break;
				case 4:
					sortOrder = Integer.compare(ion2.getNumberOfIons(), ion1.getNumberOfIons());
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
