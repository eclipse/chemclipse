/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeakQuantitations {

	private List<String> titles = new ArrayList<String>();
	private List<PeakQuantitation> quantitationEntries = new ArrayList<PeakQuantitation>();

	public List<String> getTitles() {

		return titles;
	}

	public List<PeakQuantitation> getQuantitationEntries() {

		return quantitationEntries;
	}

	public Map<String, Double> getSummedQuantitations() {

		Map<String, Double> quantitationMap = new HashMap<>();
		//
		for(int i = PeakQuantitation.INDEX_QUANTITATIONS; i < titles.size(); i++) {
			String name = titles.get(i);
			double concentration = 0.0d;
			for(PeakQuantitation peakQuantitation : quantitationEntries) {
				double quantitation = peakQuantitation.getConcentrations().get(i - PeakQuantitation.INDEX_QUANTITATIONS);
				if(!Double.isNaN(quantitation)) {
					concentration += quantitation;
				}
			}
			quantitationMap.put(name, concentration);
		}
		//
		return quantitationMap;
	}
}