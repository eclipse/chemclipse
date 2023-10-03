/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.core.SignalSupport;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class QuantitationListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IQuantitationEntry quantitationEntry1 && e2 instanceof IQuantitationEntry quantitationEntry2) {
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = quantitationEntry2.getName().compareTo(quantitationEntry1.getName());
					break;
				case 1:
					sortOrder = quantitationEntry2.getChemicalClass().compareTo(quantitationEntry1.getChemicalClass());
					break;
				case 2:
					sortOrder = Double.compare(quantitationEntry2.getConcentration(), quantitationEntry1.getConcentration());
					break;
				case 3:
					sortOrder = quantitationEntry2.getConcentrationUnit().compareTo(quantitationEntry1.getConcentrationUnit());
					break;
				case 4:
					sortOrder = Double.compare(quantitationEntry2.getArea(), quantitationEntry1.getArea());
					break;
				case 5:
					sortOrder = SignalSupport.compare(quantitationEntry2.getSignals(), quantitationEntry1.getSignals());
					break;
				case 6:
					sortOrder = quantitationEntry2.getCalibrationMethod().compareTo(quantitationEntry1.getCalibrationMethod());
					break;
				case 7:
					sortOrder = Boolean.compare(quantitationEntry2.getUsedCrossZero(), quantitationEntry1.getUsedCrossZero());
					break;
				case 8:
					sortOrder = quantitationEntry2.getQuantitationFlag().compareTo(quantitationEntry1.getQuantitationFlag());
					break;
				case 9:
					sortOrder = quantitationEntry2.getDescription().compareTo(quantitationEntry1.getDescription());
					break;
				case 10:
					sortOrder = quantitationEntry2.getGroup().compareTo(quantitationEntry1.getGroup());
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