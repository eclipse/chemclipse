/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class SampleQuantTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ISampleQuantSubstance && e2 instanceof ISampleQuantSubstance) {
			ISampleQuantSubstance sampleQuantSubstance1 = (ISampleQuantSubstance)e1;
			ISampleQuantSubstance sampleQuantSubstance2 = (ISampleQuantSubstance)e2;
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(sampleQuantSubstance2.getId(), sampleQuantSubstance1.getId());
					break;
				case 1:
					sortOrder = sampleQuantSubstance2.getCasNumber().compareTo(sampleQuantSubstance1.getCasNumber());
					break;
				case 2:
					sortOrder = sampleQuantSubstance2.getName().compareTo(sampleQuantSubstance1.getName());
					break;
				case 3:
					sortOrder = Integer.compare(sampleQuantSubstance2.getMaxScan(), sampleQuantSubstance1.getMaxScan());
					break;
				case 4:
					sortOrder = Double.compare(sampleQuantSubstance2.getConcentration(), sampleQuantSubstance1.getConcentration());
					break;
				case 5:
					sortOrder = sampleQuantSubstance2.getUnit().compareTo(sampleQuantSubstance1.getUnit());
					break;
				case 6:
					sortOrder = sampleQuantSubstance2.getMisc().compareTo(sampleQuantSubstance1.getMisc());
					break;
				case 7:
					sortOrder = sampleQuantSubstance2.getType().compareTo(sampleQuantSubstance1.getType());
					break;
				case 8:
					sortOrder = Double.compare(sampleQuantSubstance2.getMinMatchQuality(), sampleQuantSubstance1.getMinMatchQuality());
					break;
				case 9:
					sortOrder = Double.compare(sampleQuantSubstance2.getMatchQuality(), sampleQuantSubstance1.getMatchQuality());
					break;
				case 10:
					sortOrder = Boolean.compare(sampleQuantSubstance2.isValidated(), sampleQuantSubstance1.isValidated());
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
