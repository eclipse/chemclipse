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

import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakQuantitationEntriesTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: PeakQuantitationEntriesLabelProvider PeakQuantitationEntriesLabelComparator PeakQuantitationEntriesView
		 */
		int sortOrder = 0;
		if(e1 instanceof IQuantitationEntry && e2 instanceof IQuantitationEntry) {
			IQuantitationEntry quantitationEntry1 = (IQuantitationEntry)e1;
			IQuantitationEntry quantitationEntry2 = (IQuantitationEntry)e2;
			switch(getPropertyIndex()) {
				case 0: // Name
					sortOrder = quantitationEntry2.getName().compareTo(quantitationEntry1.getName());
					break;
				case 1: // Chemical Class
					sortOrder = quantitationEntry2.getChemicalClass().compareTo(quantitationEntry1.getChemicalClass());
					break;
				case 2: // Concentration
					sortOrder = Double.compare(quantitationEntry2.getConcentration(), quantitationEntry1.getConcentration());
					break;
				case 3: // Concentration Unit
					sortOrder = quantitationEntry2.getConcentrationUnit().compareTo(quantitationEntry1.getConcentrationUnit());
					break;
				case 4: // Area
					sortOrder = Double.compare(quantitationEntry2.getArea(), quantitationEntry1.getArea());
					break;
				case 5: // Ion
					sortOrder = Double.compare(quantitationEntry2.getSignal(), quantitationEntry1.getSignal());
					break;
				case 6: // Calibration Method
					sortOrder = quantitationEntry2.getCalibrationMethod().compareTo(quantitationEntry1.getCalibrationMethod());
					break;
				case 7: // Used Cross Zero
					sortOrder = Boolean.valueOf(quantitationEntry2.getUsedCrossZero()).compareTo(quantitationEntry1.getUsedCrossZero());
					break;
				case 8: // Description
					sortOrder = quantitationEntry2.getDescription().compareTo(quantitationEntry1.getDescription());
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
