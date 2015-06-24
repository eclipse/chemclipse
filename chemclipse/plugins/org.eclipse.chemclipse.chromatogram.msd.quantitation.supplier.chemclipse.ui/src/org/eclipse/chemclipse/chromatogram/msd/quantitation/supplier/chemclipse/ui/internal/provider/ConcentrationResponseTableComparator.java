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

import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntryMSD;
import org.eclipse.chemclipse.swt.ui.viewers.AbstractRecordTableComparator;
import org.eclipse.chemclipse.swt.ui.viewers.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ConcentrationResponseTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IConcentrationResponseEntryMSD && e2 instanceof IConcentrationResponseEntryMSD) {
			IConcentrationResponseEntryMSD entry1 = (IConcentrationResponseEntryMSD)e1;
			IConcentrationResponseEntryMSD entry2 = (IConcentrationResponseEntryMSD)e2;
			//
			switch(getPropertyIndex()) {
				case 0: // Ion
					sortOrder = Double.compare(entry2.getIon(), entry1.getIon());
					break;
				case 1: // Concentration
					sortOrder = Double.compare(entry2.getConcentration(), entry1.getConcentration());
					break;
				case 2: // Response
					sortOrder = Double.compare(entry2.getResponse(), entry1.getResponse());
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
