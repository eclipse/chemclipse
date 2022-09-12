/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class FeatureDataMatrix {

	private List<String> sampleNames = new ArrayList<>();
	private List<Feature> features = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	public FeatureDataMatrix(ISamplesPCA<? extends IVariable, ? extends ISample> samples) {

		if(samples != null) {
			/*
			 * Data
			 */
			List<? extends IVariable> variableList = samples.getVariables();
			List<? extends ISample> sampleList = samples.getSampleList();
			/*
			 * Samples
			 */
			for(ISample sample : sampleList) {
				sampleNames.add(sample.getSampleName());
			}
			/*
			 * variable.getClassification() // null
			 * variable.getDescription() // null
			 * variable.getType() // Retention time (min)
			 * variable.getValue() // 3.466
			 */
			for(IVariable variable : variableList) {
				features.add(new Feature(variable));
			}
			//
			for(int i = 0; i < sampleList.size(); i++) {
				ISample sample = sampleList.get(i);
				/*
				 * sampleData.getData() // 50327.8
				 * sampleData.getModifiedData() // 0.524298283655198
				 * sampleData.isEmpty() // false
				 * sampleData.getData2() // e.g. PeakMSD
				 */
				List<? extends ISampleData> sampleDataList = sample.getSampleData();
				for(int j = 0; j < sampleDataList.size(); j++) {
					ISampleData<?> sampleData = sampleDataList.get(j);
					features.get(j).getSampleData().add(sampleData);
				}
			}
		}
	}

	public String getVariableName() {

		for(Feature feature : features) {
			String name = feature.getVariable().getType();
			if(!"".equals(name)) {
				return name;
			}
		}
		return "--";
	}

	public List<String> getSampleNames() {

		return Collections.unmodifiableList(sampleNames);
	}

	public List<Feature> getFeatures() {

		return Collections.unmodifiableList(features);
	}
}
