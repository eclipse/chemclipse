/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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
			IIonTransition ionTransition2 = ion2.getIonTransition();
			//
			switch(getPropertyIndex()) {
				case 0: // m/z
					sortOrder = Double.compare(ion2.getIon(), ion1.getIon());
					break;
				case 1: // abundance
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				case 2: // parent m/z
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Integer.compare(ionTransition2.getQ1Ion(), ionTransition1.getQ1Ion());
					break;
				case 3: // parent resolution
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getQ1Resolution(), ionTransition1.getQ1Resolution());
					break;
				case 4: // daughter m/z
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getQ3Ion(), ionTransition1.getQ3Ion());
					break;
				case 5: // daughter resolution
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getQ3Resolution(), ionTransition1.getQ3Resolution());
					break;
				case 6: // collision energy
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getCollisionEnergy(), ionTransition1.getCollisionEnergy());
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
