/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring ILabel support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ClassifiedPeaksFilterSettings {

	@JsonProperty(value = "Classifications", defaultValue = "")
	@JsonPropertyDescription("Filter peaks with the given classification, separate different ones with comma.")
	private String classifications = "";
	@JsonProperty(value = "Active for Analysis", defaultValue = "")
	@JsonPropertyDescription("Activate/deactivate the matched peaks.")
	private boolean activeForAnalysis = true;

	public String getClassifications() {

		return classifications;
	}

	public void setClassifications(String classifications) {

		this.classifications = classifications;
	}

	public Set<String> getClassificationSet() {

		if(classifications == null || classifications.isEmpty()) {
			return Collections.emptySet();
		}
		//
		HashSet<String> set = new HashSet<>();
		String[] split = classifications.split(",");
		for(String string : split) {
			set.add(string.trim());
		}
		//
		return set;
	}

	public boolean isActiveForAnalysis() {

		return activeForAnalysis;
	}

	public void setActiveForAnalysis(boolean activeForAnalysis) {

		this.activeForAnalysis = activeForAnalysis;
	}
}