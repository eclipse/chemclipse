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
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeatureDataMatrix {

	private List<String> sampleNames = new ArrayList<>();
	private List<Feature> features = new ArrayList<>();

	public FeatureDataMatrix(List<String> sampleNames, List<Feature> features) {

		this.sampleNames = sampleNames;
		this.features = features;
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