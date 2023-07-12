/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class InternalStandardsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IInternalStandard internalStandard1 && e2 instanceof IInternalStandard internalStandard2) {
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = internalStandard2.getName().compareTo(internalStandard1.getName());
					break;
				case 1:
					sortOrder = Double.compare(internalStandard2.getConcentration(), internalStandard1.getConcentration());
					break;
				case 2:
					sortOrder = internalStandard2.getConcentrationUnit().compareTo(internalStandard1.getConcentrationUnit());
					break;
				case 3:
					sortOrder = Double.compare(internalStandard2.getCompensationFactor(), internalStandard1.getCompensationFactor());
					break;
				case 4:
					sortOrder = Double.compare(internalStandard2.getResponseFactor(), internalStandard1.getResponseFactor());
					break;
				case 5:
					sortOrder = internalStandard2.getChemicalClass().compareTo(internalStandard1.getChemicalClass());
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