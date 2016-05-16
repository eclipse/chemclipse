/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.StandardsReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexEntry;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

public class RetentionIndexExtractor {

	public List<IRetentionIndexEntry> extract(IChromatogramMSD chromatogram) {

		StandardsReader standardsReader = new StandardsReader();
		Map<String, Integer> nameIndexMap = standardsReader.getNameIndexMap();
		//
		List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<IRetentionIndexEntry>();
		for(IChromatogramPeakMSD peak : chromatogram.getPeaks()) {
			List<IPeakTarget> peakTargets = peak.getTargets();
			if(peakTargets.size() > 0) {
				String name = peakTargets.get(0).getLibraryInformation().getName().trim();
				if(nameIndexMap.containsKey(name)) {
					int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
					float retentionIndex = nameIndexMap.get(name);
					IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
					retentionIndexEntries.add(retentionIndexEntry);
				}
			}
		}
		return retentionIndexEntries;
	}
}
