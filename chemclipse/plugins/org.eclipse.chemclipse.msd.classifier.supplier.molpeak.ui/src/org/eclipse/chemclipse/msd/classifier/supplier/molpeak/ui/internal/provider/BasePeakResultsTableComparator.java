/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

public class BasePeakResultsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof Map.Entry<?, ?> ligninEntry1 && e2 instanceof Map.Entry<?, ?> ligninEntry2) {
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = ((String)ligninEntry2.getKey()).compareTo((String)ligninEntry1.getKey());
					break;
				case 1:
					sortOrder = Double.compare((double)ligninEntry2.getValue(), (double)ligninEntry1.getValue());
					break;
			}
			if(getDirection() == ASCENDING) {
				sortOrder = -sortOrder;
			}
		}
		return sortOrder;
	}
}
