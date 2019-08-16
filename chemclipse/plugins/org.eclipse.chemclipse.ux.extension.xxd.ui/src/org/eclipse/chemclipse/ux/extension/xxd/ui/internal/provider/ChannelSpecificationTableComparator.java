/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ChannelSpecificationTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof Map.Entry && e2 instanceof Map.Entry) {
			//
			Map.Entry<String, String> entry1 = (Map.Entry<String, String>)e1;
			Map.Entry<String, String> entry2 = (Map.Entry<String, String>)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = entry1.getKey().compareTo(entry2.getKey());
					break;
				case 1:
					sortOrder = entry1.getValue().compareTo(entry2.getValue());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
