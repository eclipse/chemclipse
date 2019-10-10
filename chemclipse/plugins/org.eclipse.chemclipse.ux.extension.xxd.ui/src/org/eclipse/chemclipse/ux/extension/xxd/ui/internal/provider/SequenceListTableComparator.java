/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class SequenceListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ISequenceRecord && e2 instanceof ISequenceRecord) {
			ISequenceRecord sequenceRecord1 = (ISequenceRecord)e1;
			ISequenceRecord sequenceRecord2 = (ISequenceRecord)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = sequenceRecord2.getSampleName().compareTo(sequenceRecord1.getSampleName());
					break;
				case 1:
					sortOrder = sequenceRecord2.getDataPath().compareTo(sequenceRecord1.getDataPath());
					break;
				case 2:
					sortOrder = sequenceRecord2.getDataFile().compareTo(sequenceRecord1.getDataFile());
					break;
				case 3:
					sortOrder = sequenceRecord2.getAdvice().compareTo(sequenceRecord1.getAdvice());
					break;
				case 4:
					sortOrder = Integer.compare(sequenceRecord2.getVial(), sequenceRecord1.getVial());
					break;
				case 5:
					sortOrder = sequenceRecord2.getSubstance().compareTo(sequenceRecord1.getSubstance());
					break;
				case 6:
					sortOrder = sequenceRecord2.getDescription().compareTo(sequenceRecord1.getDescription());
					break;
				case 7:
					sortOrder = sequenceRecord2.getProcessMethod().compareTo(sequenceRecord1.getProcessMethod());
					break;
				case 8:
					sortOrder = sequenceRecord2.getReportMethod().compareTo(sequenceRecord1.getReportMethod());
					break;
				case 9:
					sortOrder = Double.compare(sequenceRecord2.getMultiplier(), sequenceRecord1.getMultiplier());
					break;
				case 10:
					sortOrder = Double.compare(sequenceRecord2.getInjectionVolume(), sequenceRecord1.getInjectionVolume());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
