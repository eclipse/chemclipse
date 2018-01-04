/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakIntegrationEntriesTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: PeakIntegrationEntriesLabelProvider PeakIntegrationEntriesLabelComparator PeakIntegrationEntriesView
		 */
		int sortOrder = 0;
		if(e1 instanceof IIntegrationEntryMSD && e2 instanceof IIntegrationEntryMSD) {
			IIntegrationEntryMSD integrationEntryMSD1 = (IIntegrationEntryMSD)e1;
			IIntegrationEntryMSD integrationEntryMSD2 = (IIntegrationEntryMSD)e2;
			switch(getPropertyIndex()) {
				case 0: // Ion
					sortOrder = Double.compare(integrationEntryMSD2.getIon(), integrationEntryMSD1.getIon());
					break;
				case 1: // Area
					sortOrder = Double.compare(integrationEntryMSD2.getIntegratedArea(), integrationEntryMSD1.getIntegratedArea());
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
