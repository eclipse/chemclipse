/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
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

import java.util.Map;

import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ColumMappingComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@SuppressWarnings("rawtypes")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof Map.Entry && e2 instanceof Map.Entry) {
			//
			Map.Entry setting1 = (Map.Entry)e1;
			Map.Entry setting2 = (Map.Entry)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = setting2.getKey().toString().compareTo(setting1.getKey().toString());
					break;
				case 1:
					sortOrder = setting2.getValue().toString().compareTo(setting1.getValue().toString());
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
