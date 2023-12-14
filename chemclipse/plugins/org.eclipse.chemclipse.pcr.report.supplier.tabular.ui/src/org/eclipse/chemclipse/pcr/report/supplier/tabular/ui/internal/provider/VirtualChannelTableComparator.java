/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannel;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class VirtualChannelTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof VirtualChannel mapping1 && e2 instanceof VirtualChannel mapping2) {
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = mapping1.getSubset().compareTo(mapping2.getSubset());
					break;
				case 1:
					sortOrder = mapping1.getSample().compareTo(mapping2.getSample());
					break;
				case 2:
					sortOrder = mapping1.getSourceChannelString().compareTo(mapping2.getSourceChannelString());
					break;
				case 3:
					sortOrder = Integer.compare(mapping1.getTargetChannel(), mapping2.getTargetChannel());
					break;
			}
		}
		if(getDirection() == DESCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
