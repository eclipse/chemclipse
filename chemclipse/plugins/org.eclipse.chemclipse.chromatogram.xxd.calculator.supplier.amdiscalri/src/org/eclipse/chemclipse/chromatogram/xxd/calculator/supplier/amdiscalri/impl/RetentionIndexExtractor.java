/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

public class RetentionIndexExtractor {

	public List<IRetentionIndexEntry> extract(IChromatogram chromatogram) {

		StandardsReader standardsReader = new StandardsReader();
		Map<String, Integer> nameIndexMap = standardsReader.getNameIndexMap();
		//
		List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<IRetentionIndexEntry>();
		List<? extends IPeak> peaks = getPeaks(chromatogram);
		for(IPeak peak : peaks) {
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

	private List<? extends IPeak> getPeaks(IChromatogram chromatogram) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		if(chromatogram instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
			peaks.addAll(chromatogramMSD.getPeaks());
		} else if(chromatogram instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
			peaks.addAll(chromatogramCSD.getPeaks());
		}
		return peaks;
	}
}
