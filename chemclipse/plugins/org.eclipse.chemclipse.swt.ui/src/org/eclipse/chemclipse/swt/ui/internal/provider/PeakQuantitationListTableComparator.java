/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.util.List;

import org.eclipse.chemclipse.model.support.PeakQuantitation;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakQuantitationListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof PeakQuantitation && e2 instanceof PeakQuantitation) {
			//
			PeakQuantitation peakQuantitation1 = (PeakQuantitation)e1;
			PeakQuantitation peakQuantitation2 = (PeakQuantitation)e2;
			//
			int indexColumn = getPropertyIndex();
			switch(indexColumn) {
				case 0:
					sortOrder = Integer.compare(peakQuantitation2.getRetentionTime(), peakQuantitation1.getRetentionTime());
					break;
				case 1:
					sortOrder = peakQuantitation2.getName().compareTo(peakQuantitation1.getName());
					break;
				case 2:
					sortOrder = peakQuantitation2.getCasNumber().compareTo(peakQuantitation1.getCasNumber());
					break;
				case 3:
					sortOrder = peakQuantitation2.getReferenceIdentifier().compareTo(peakQuantitation1.getReferenceIdentifier());
					break;
				case 4:
					sortOrder = Double.compare(peakQuantitation2.getIntegratedArea(), peakQuantitation1.getIntegratedArea());
					break;
				case 5:
					sortOrder = peakQuantitation2.getClassifier().compareTo(peakQuantitation1.getClassifier());
					break;
				case 6:
					sortOrder = peakQuantitation2.getQuantifier().compareTo(peakQuantitation1.getQuantifier());
					break;
				default:
					/*
					 * The concentrations are added dynamically.
					 */
					sortOrder = 0;
					if(indexColumn > 6) {
						List<Double> concentrations1 = peakQuantitation1.getConcentrations();
						List<Double> concentrations2 = peakQuantitation2.getConcentrations();
						if(!concentrations1.isEmpty() && !concentrations2.isEmpty()) {
							if(concentrations1.size() == concentrations2.size()) {
								int index = indexColumn - 7;
								sortOrder = Double.compare(concentrations2.get(index), concentrations1.get(index));
							}
						}
					}
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}