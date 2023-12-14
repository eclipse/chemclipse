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

import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMapping;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ChannelMappingTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ChannelMapping mapping1 && e2 instanceof ChannelMapping mapping2) {
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = mapping1.getSubset().compareTo(mapping2.getSubset());
					break;
				case 1:
					sortOrder = Integer.compare(mapping1.getChannel(), mapping2.getChannel());
					break;
				case 2:
					sortOrder = mapping1.getLabel().compareTo(mapping2.getLabel());
					break;
			}
		}
		if(getDirection() == DESCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
