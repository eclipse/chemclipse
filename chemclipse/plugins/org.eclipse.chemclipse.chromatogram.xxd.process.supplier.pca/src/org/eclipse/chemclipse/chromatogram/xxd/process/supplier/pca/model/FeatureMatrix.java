/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class FeatureMatrix {

	public FeatureMatrix(ISamplesPCA<? extends IVariable, ? extends ISample> samples) {
		if(samples != null) {
			List<? extends IVariable> variableList = samples.getVariables();
			for(IVariable variable : variableList) {
				// System.out.println(variable.getClassification()); // null
				// System.out.println(variable.getDescription()); // null
				// System.out.println(variable.getType()); // Retention time (min)
				// System.out.println(variable.getValue()); // 3.466
			}
			//
			List<? extends ISample> sampleList = samples.getSampleList();
			for(ISample sample : sampleList) {
				System.out.println(sample.getName());
				for(ISampleData sampleData : sample.getSampleData()) {
					// System.out.println("\t" + sampleData.getData()); // 50327.8
					// System.out.println("\t" + sampleData.getModifiedData()); // 0.524298283655198
					// System.out.println("\t" + sampleData.isEmpty()); // false
					// System.out.println("\t" + sampleData.getData2()); // e.g. PeakMSD
				}
			}
		}
	}
}
