/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class MolPeakResultsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		Map.Entry<String, Double> ligninEntry1 = (Map.Entry<String, Double>)e1;
		Map.Entry<String, Double> ligninEntry2 = (Map.Entry<String, Double>)e2;
		int sortOrder;
		switch(getPropertyIndex()) {
			case 0:
				sortOrder = ligninEntry2.getKey().compareTo(ligninEntry1.getKey());
				break;
			case 1:
				sortOrder = Double.compare(ligninEntry2.getValue(), ligninEntry1.getValue());
				break;
			default:
				sortOrder = 0;
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
