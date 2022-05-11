/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class RetentionIndexMap extends TreeMap<Integer, Integer> {

	public static final int VALUE_NOT_AVAILABLE = -1;
	//
	private static final long serialVersionUID = -4827650901052762574L;

	public RetentionIndexMap() {

	}

	public RetentionIndexMap(IChromatogram<?> chromatogram) {

		update(chromatogram);
	}

	public void update(IChromatogram<?> chromatogram) {

		clear();
		if(chromatogram != null) {
			for(IScan scan : chromatogram.getScans()) {
				float retentionIndex = scan.getRetentionIndex();
				if(retentionIndex > 0) {
					put(Math.round(retentionIndex), scan.getRetentionTime());
				}
			}
		}
	}

	public int getRetentionTime(int retentionIndex) {

		Entry<Integer, Integer> floorEntry = floorEntry(retentionIndex);
		return floorEntry != null ? floorEntry.getValue().intValue() : VALUE_NOT_AVAILABLE;
	}
}