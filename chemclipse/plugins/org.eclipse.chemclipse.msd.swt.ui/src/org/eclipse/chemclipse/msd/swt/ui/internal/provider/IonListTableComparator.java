/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class IonListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		int sortOrder = 0;
		if(e1 instanceof IIon && e2 instanceof IIon) {
			IIon ion1 = (IIon)e1;
			IIon ion2 = (IIon)e2;
			IIonTransition ionTransition1 = ion1.getIonTransition();
			IIonTransition ionTransition2 = ion1.getIonTransition();
			//
			switch(getPropertyIndex()) {
				case 0: // m/z
					sortOrder = Double.compare(ion1.getIon(), ion2.getIon());
					break;
				case 1: // abundance
					sortOrder = Float.compare(ion1.getAbundance(), ion2.getAbundance());
					break;
				case 2: // parent m/z
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Integer.compare(ionTransition1.getFilter1Ion(), ionTransition2.getFilter1Ion());
					break;
				case 3: // parent resolution
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition1.getFilter1Resolution(), ionTransition2.getFilter1Resolution());
					break;
				case 4: // daughter m/z
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Integer.compare(ionTransition1.getFilter3Ion(), ionTransition2.getFilter3Ion());
					break;
				case 5: // daughter resolution
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition1.getFilter3Resolution(), ionTransition2.getFilter3Resolution());
					break;
				case 6: // collision energy
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition1.getCollisionEnergy(), ionTransition2.getCollisionEnergy());
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
