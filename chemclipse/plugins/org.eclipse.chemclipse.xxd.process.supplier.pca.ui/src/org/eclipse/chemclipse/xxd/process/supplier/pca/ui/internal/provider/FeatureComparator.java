/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Feature;
import org.eclipse.jface.viewers.Viewer;

public class FeatureComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof Feature && e2 instanceof Feature) {
			Feature feature1 = (Feature)e1;
			IVariable variable1 = feature1.getVariable();
			Feature feature2 = (Feature)e2;
			IVariable variable2 = feature2.getVariable();
			//
			int columnIndex = getPropertyIndex();
			switch(columnIndex) {
				case 0:
					try {
						double value1 = Double.parseDouble(variable1.getValue());
						double value2 = Double.parseDouble(variable2.getValue());
						sortOrder = Double.compare(value2, value1);
					} catch(Exception e) {
						sortOrder = variable2.getValue().compareTo(variable1.getValue());
					}
					break;
				case 1:
					sortOrder = Boolean.compare(variable2.isSelected(), variable1.isSelected());
					break;
				case 2:
					sortOrder = variable2.getClassification().compareTo(variable1.getClassification());
					break;
				case 3:
					sortOrder = variable2.getDescription().compareTo(variable1.getDescription());
					break;
				default:
					int index = columnIndex - 4;
					List<ISampleData<?>> sampleData1 = feature1.getSampleData();
					List<ISampleData<?>> sampleData2 = feature2.getSampleData();
					//
					if(sampleData1.size() > index && sampleData2.size() > index) {
						sortOrder = Double.compare(sampleData2.get(index).getData(), sampleData1.get(index).getData());
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