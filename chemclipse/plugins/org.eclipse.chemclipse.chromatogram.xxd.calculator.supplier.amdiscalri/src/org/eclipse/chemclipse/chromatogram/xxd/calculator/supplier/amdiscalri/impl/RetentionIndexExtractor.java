/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

	public ISeparationColumnIndices extract(IChromatogram<?> chromatogram, boolean useCuratedNames) {

		String[] standards = RetentionIndexCalculator.getStandards();
		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		separationColumnIndices.setSeparationColumn(chromatogram.getSeparationColumnIndices().getSeparationColumn());
		//
		List<? extends IPeak> peaks = getPeaks(chromatogram);
		for(IPeak peak : peaks) {
			float retentionIndexPeak = peak.getPeakModel().getPeakMaximum().getRetentionIndex();
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndexPeak);
			IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), identificationTargetComparator);
			if(identificationTarget != null) {
				/*
				 * Validate
				 */
				ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
				int retentionIndex = extractRetentionIndex(libraryInformation);
				if(retentionIndex > RetentionIndexCalculator.INDEX_MISSING) {
					/*
					 * Determine the name
					 */
					String name = libraryInformation.getName().trim();
					if(useCuratedNames) {
						int index = retentionIndex / 100 - 1;
						if(index >= 0 && index < standards.length) {
							name = standards[index];
						}
					}
					/*
					 * Create a new entry.
					 */
					int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
					IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
					separationColumnIndices.put(retentionIndexEntry);
				}
			}
		}
		return separationColumnIndices;
	}

	private int extractRetentionIndex(ILibraryInformation libraryInformation) {

		int retentionIndex = RetentionIndexCalculator.getRetentionIndex(libraryInformation.getName().trim());
		if(retentionIndex == RetentionIndexCalculator.INDEX_MISSING) {
			retentionIndex = RetentionIndexCalculator.getRetentionIndex(libraryInformation);
		}
		//
		return retentionIndex;
	}

	private List<? extends IPeak> getPeaks(IChromatogram<?> chromatogram) {

		List<IPeak> peaks = new ArrayList<>();
		peaks.addAll(chromatogram.getPeaks());
		return peaks;
	}
}