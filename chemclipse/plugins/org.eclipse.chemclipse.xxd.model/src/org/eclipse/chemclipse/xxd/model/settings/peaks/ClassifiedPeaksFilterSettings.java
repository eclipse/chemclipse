/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ClassifiedPeaksFilterSettings {

	@JsonPropertyDescription("Disables all peaks with the given Classification, separate different ones with comma")
	@JsonProperty(value = "Classifications", defaultValue = "")
	private String classifications;

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
		HashSet<String> set = new HashSet<>();
		String[] split = classifications.split(",");
		for(String string : split) {
			set.add(string.trim());
		}
		return set;
	}
}
