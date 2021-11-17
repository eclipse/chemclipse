/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public class RetentionIndexExtractor {

	@SuppressWarnings("rawtypes")
	public ISeparationColumnIndices extract(IChromatogram chromatogram) {

		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		separationColumnIndices.setSeparationColumn(chromatogram.getSeparationColumnIndices().getSeparationColumn());
		//
		List<? extends IPeak> peaks = getPeaks(chromatogram);
		for(IPeak peak : peaks) {
			float retentionIndexPeak = peak.getPeakModel().getPeakMaximum().getRetentionIndex();
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndexPeak);
			IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), identificationTargetComparator);
			if(identificationTarget != null) {
				ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
				String name = libraryInformation.getName().trim();
				int retentionIndex = RetentionIndexCalculator.getRetentionIndex(name);
				if(retentionIndex > RetentionIndexCalculator.INDEX_MISSING) {
					int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
					IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
					separationColumnIndices.put(retentionIndexEntry);
				}
			}
		}
		return separationColumnIndices;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<? extends IPeak> getPeaks(IChromatogram chromatogram) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.addAll(chromatogram.getPeaks());
		return peaks;
	}
}
