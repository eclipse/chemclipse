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

import org.eclipse.chemclipse.model.identifier.ITargetTemplate;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class TargetTemplateComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ITargetTemplate && e2 instanceof ITargetTemplate) {
			//
			ITargetTemplate targetTemplate1 = (ITargetTemplate)e1;
			ITargetTemplate targetTemplate2 = (ITargetTemplate)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = targetTemplate2.getName().compareTo(targetTemplate1.getName());
					break;
				case 1:
					sortOrder = targetTemplate2.getCasNumber().compareTo(targetTemplate1.getCasNumber());
					break;
				case 2:
					sortOrder = targetTemplate2.getComment().compareTo(targetTemplate1.getComment());
					break;
				case 3:
					sortOrder = targetTemplate2.getContributor().compareTo(targetTemplate1.getContributor());
					break;
				case 4:
					sortOrder = targetTemplate2.getReferenceId().compareTo(targetTemplate1.getReferenceId());
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
