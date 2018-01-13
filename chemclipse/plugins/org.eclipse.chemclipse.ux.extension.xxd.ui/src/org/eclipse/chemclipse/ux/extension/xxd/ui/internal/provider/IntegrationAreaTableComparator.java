/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class IntegrationAreaTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IIntegrationEntry && e2 instanceof IIntegrationEntry) {
			IIntegrationEntry integrationEntry1 = (IIntegrationEntry)e1;
			IIntegrationEntry integrationEntry2 = (IIntegrationEntry)e2;
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Double.compare(integrationEntry2.getIntegratedArea(), integrationEntry1.getIntegratedArea());
					break;
				case 1:
					if(integrationEntry1 instanceof IIntegrationEntryMSD && integrationEntry2 instanceof IIntegrationEntryMSD) {
						IIntegrationEntryMSD quantitationEntryMSD1 = (IIntegrationEntryMSD)integrationEntry1;
						IIntegrationEntryMSD quantitationEntryMSD2 = (IIntegrationEntryMSD)integrationEntry2;
						sortOrder = Double.compare(quantitationEntryMSD2.getIon(), quantitationEntryMSD1.getIon());
					}
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
