/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
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
		if(e1 instanceof IQuantitationEntryMSD && e2 instanceof IQuantitationEntryMSD) {
			IQuantitationEntryMSD quantitationEntryMSD1 = (IQuantitationEntryMSD)e1;
			IQuantitationEntryMSD quantitationEntryMSD2 = (IQuantitationEntryMSD)e2;
			switch(getPropertyIndex()) {
				case 0: // Name
					sortOrder = quantitationEntryMSD2.getName().compareTo(quantitationEntryMSD1.getName());
					break;
				case 1: // Chemical Class
					sortOrder = quantitationEntryMSD2.getChemicalClass().compareTo(quantitationEntryMSD1.getChemicalClass());
					break;
				case 2: // Concentration
					sortOrder = Double.compare(quantitationEntryMSD2.getConcentration(), quantitationEntryMSD1.getConcentration());
					break;
				case 3: // Concentration Unit
					sortOrder = quantitationEntryMSD2.getConcentrationUnit().compareTo(quantitationEntryMSD1.getConcentrationUnit());
					break;
				case 4: // Area
					sortOrder = Double.compare(quantitationEntryMSD2.getArea(), quantitationEntryMSD1.getArea());
					break;
				case 5: // Ion
					sortOrder = Double.compare(quantitationEntryMSD2.getIon(), quantitationEntryMSD1.getIon());
					break;
				case 6: // Calibration Method
					sortOrder = quantitationEntryMSD2.getCalibrationMethod().compareTo(quantitationEntryMSD1.getCalibrationMethod());
					break;
				case 7: // Used Cross Zero
					sortOrder = Boolean.valueOf(quantitationEntryMSD2.getUsedCrossZero()).compareTo(quantitationEntryMSD1.getUsedCrossZero());
					break;
				case 8: // Description
					sortOrder = quantitationEntryMSD2.getDescription().compareTo(quantitationEntryMSD1.getDescription());
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
