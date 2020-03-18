/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class SamplesComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ISample && e2 instanceof ISample) {
			Sample sample1 = (Sample)e1;
			Sample sample2 = (Sample)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = sample2.getName().compareTo(sample1.getName());
					break;
				case 1:
					sortOrder = Boolean.compare(sample2.isSelected(), sample1.isSelected());
					break;
				case 2:
					if(sample1.getGroupName() != null && sample2.getGroupName() != null) {
						sortOrder = sample2.getGroupName().compareTo(sample1.getGroupName());
					} else {
						sortOrder = 0;
					}
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
