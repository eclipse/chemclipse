/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIonTransition;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class IonTransitionTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IMarkedIonTransition && e2 instanceof IMarkedIonTransition) {
			IMarkedIonTransition markedIonTransition1 = (IMarkedIonTransition)e1;
			IIonTransition ionTransition1 = markedIonTransition1.getIonTransition();
			IMarkedIonTransition markedIonTransition2 = (IMarkedIonTransition)e2;
			IIonTransition ionTransition2 = markedIonTransition2.getIonTransition();
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = ionTransition2.getCompoundName().compareTo(ionTransition1.getCompoundName());
					break;
				case 1:
					sortOrder = Integer.compare(ionTransition2.getQ1Ion(), ionTransition1.getQ1Ion());
					break;
				case 2:
					sortOrder = Double.compare(ionTransition2.getQ1Resolution(), ionTransition1.getQ1Resolution());
					break;
				case 3:
					sortOrder = Double.compare(ionTransition2.getQ3Ion(), ionTransition1.getQ3Ion());
					break;
				case 4:
					sortOrder = Double.compare(ionTransition2.getQ3Resolution(), ionTransition1.getQ3Resolution());
					break;
				case 5:
					sortOrder = Double.compare(ionTransition2.getCollisionEnergy(), ionTransition1.getCollisionEnergy());
					break;
				case 6:
					sortOrder = Boolean.compare(markedIonTransition2.isSelected(), markedIonTransition1.isSelected());
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
