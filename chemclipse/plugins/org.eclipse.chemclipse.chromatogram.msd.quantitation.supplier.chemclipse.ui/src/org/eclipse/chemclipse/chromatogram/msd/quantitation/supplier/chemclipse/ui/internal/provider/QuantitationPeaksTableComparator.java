/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationPeakDocument;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class QuantitationPeaksTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IQuantitationPeakDocument && e2 instanceof IQuantitationPeakDocument) {
			IQuantitationPeakDocument entry1 = (IQuantitationPeakDocument)e1;
			IQuantitationPeakDocument entry2 = (IQuantitationPeakDocument)e2;
			//
			switch(getPropertyIndex()) {
				case 0: // Concentration
					sortOrder = Double.compare(entry2.getConcentration(), entry1.getConcentration());
					break;
				case 1: // Concentration Unit
					sortOrder = (entry2.getConcentrationUnit().compareTo(entry1.getConcentrationUnit()));
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
