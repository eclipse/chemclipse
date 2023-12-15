/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ColumnIndicesComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IColumnIndexMarker marker1 && e2 instanceof IColumnIndexMarker marker2) {
			ISeparationColumn column1 = marker1.getSeparationColumn();
			ISeparationColumn column2 = marker2.getSeparationColumn();
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Float.compare(marker1.getRetentionIndex(), marker2.getRetentionIndex());
					break;
				case 1:
					sortOrder = column1.getName().compareTo(column2.getName());
					break;
				case 2:
					sortOrder = column1.getSeparationColumnType().compareTo(column2.getSeparationColumnType());
					break;
				case 3:
					sortOrder = column1.getSeparationColumnPackaging().compareTo(column2.getSeparationColumnPackaging());
					break;
				case 4:
					sortOrder = column1.getCalculationType().compareTo(column2.getCalculationType());
					break;
				case 5:
					sortOrder = column1.getLength().compareTo(column2.getLength());
					break;
				case 6:
					sortOrder = column1.getDiameter().compareTo(column2.getDiameter());
					break;
				case 7:
					sortOrder = column1.getPhase().compareTo(column2.getPhase());
					break;
				case 8:
					sortOrder = column1.getThickness().compareTo(column2.getThickness());
					break;
				default:
					sortOrder = 0;
			}
		}
		//
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		//
		return sortOrder;
	}
}
