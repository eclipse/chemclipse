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

import org.eclipse.chemclipse.pcr.model.core.IPlateTableEntry;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PlateListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IPlateTableEntry && e2 instanceof IPlateTableEntry) {
			IPlateTableEntry entry1 = (IPlateTableEntry)e1;
			IPlateTableEntry entry2 = (IPlateTableEntry)e2;
			//
			int columnIndex = getPropertyIndex();
			if(columnIndex == 0) {
				sortOrder = entry2.getRow().compareTo(entry1.getRow());
			} else {
				IWell well1 = entry1.getWells().get(columnIndex);
				IWell well2 = entry2.getWells().get(columnIndex);
				if(well1 != null && well2 != null) {
					sortOrder = well2.getSampleId().compareTo(well1.getSampleId());
				}
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
