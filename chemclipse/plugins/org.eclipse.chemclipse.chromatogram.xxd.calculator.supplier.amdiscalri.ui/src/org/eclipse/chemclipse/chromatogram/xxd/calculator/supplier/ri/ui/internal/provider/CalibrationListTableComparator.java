/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.impl.CalibrationFile;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class CalibrationListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof CalibrationFile && e2 instanceof CalibrationFile) {
			//
			CalibrationFile calibrationFile1 = (CalibrationFile)e1;
			CalibrationFile calibrationFile2 = (CalibrationFile)e2;
			String column1 = calibrationFile1.getSeparationColumnIndices().getSeparationColumn().getName();
			String column2 = calibrationFile2.getSeparationColumnIndices().getSeparationColumn().getName();
			String type1 = calibrationFile1.getSeparationColumnIndices().getSeparationColumn().getSeparationColumnType().label();
			String type2 = calibrationFile2.getSeparationColumnIndices().getSeparationColumn().getSeparationColumnType().label();
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = column2.compareTo(column1);
					break;
				case 1:
					sortOrder = type2.compareTo(type1);
					break;
				case 2:
					sortOrder = calibrationFile2.getFile().getName().compareTo(calibrationFile1.getFile().getName());
					break;
				case 3:
					sortOrder = calibrationFile2.getFile().getAbsolutePath().compareTo(calibrationFile1.getFile().getAbsolutePath());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}