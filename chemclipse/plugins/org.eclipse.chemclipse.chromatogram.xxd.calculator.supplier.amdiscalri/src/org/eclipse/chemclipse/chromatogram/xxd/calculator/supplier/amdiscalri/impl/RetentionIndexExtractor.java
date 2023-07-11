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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.support.RetentionIndexMath;

public class RetentionIndexExtractor {

	public ISeparationColumnIndices extract(IChromatogram<?> chromatogram, boolean deriveMissingIndices, boolean useCuratedNames) {

		String[] standards = RetentionIndexCalculator.getStandards();
		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		separationColumnIndices.setSeparationColumn(chromatogram.getSeparationColumnIndices().getSeparationColumn());
		Set<Integer> availableIndices = new HashSet<>();
		TreeMap<Integer, Integer> retentionIndexPeakMap = new TreeMap<>();
		//
		for(IPeak peak : getPeaks(chromatogram)) {
			IPeakModel peakModel = peak.getPeakModel();
			IScan peakMaximum = peakModel.getPeakMaximum();
			float retentionIndexPeak = peakMaximum.getRetentionIndex();
			/*
			 * Map the RI entries
			 */
			if(retentionIndexPeak > 0) {
				retentionIndexPeakMap.put(Math.round(retentionIndexPeak), peakMaximum.getRetentionTime());
			}
			/*
			 * Match the name
			 */
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
					availableIndices.add(retentionIndex);
				}
			}
		}
		/*
		 * Auto-Fill Retention Indices
		 */
		if(deriveMissingIndices) {
			if(retentionIndexPeakMap.size() >= 2) {
				/*
				 * Calculate the alkanes in between.
				 */
				List<IRetentionIndexEntry> derivedRetentionIndexEntries = new ArrayList<>();
				int minAlkane = (int)Math.round(retentionIndexPeakMap.firstKey() / 100.0d);
				int maxAlkane = (int)Math.round(retentionIndexPeakMap.lastKey() / 100.0d);
				//
				for(int alkane = minAlkane; alkane <= maxAlkane; alkane++) {
					int retentionIndex = alkane * 100;
					if(!availableIndices.contains(retentionIndex)) {
						Map.Entry<Integer, Integer> floorEntry = retentionIndexPeakMap.floorEntry(retentionIndex);
						if(floorEntry != null) {
							Map.Entry<Integer, Integer> ceilingEntry = retentionIndexPeakMap.ceilingEntry(retentionIndex);
							if(ceilingEntry != null) {
								/*
								 * Derived Entry
								 */
								int retentionTime = 0;
								if(floorEntry.getKey() == retentionIndex) {
									retentionTime = floorEntry.getValue();
								} else if(ceilingEntry.getKey() == retentionIndex) {
									retentionTime = ceilingEntry.getValue();
								} else {
									int retentionIndexLow = floorEntry.getKey();
									int retentionTimeLow = floorEntry.getValue();
									int retentionIndexHigh = ceilingEntry.getKey();
									int retentionTimeHigh = ceilingEntry.getValue();
									retentionTime = RetentionIndexMath.calculateRetentionTime(retentionIndex, retentionIndexLow, retentionIndexHigh, retentionTimeLow, retentionTimeHigh);
								}
								/*
								 * Add
								 */
								if(retentionTime > 0) {
									String name = getRetentionIndexName(standards, alkane);
									derivedRetentionIndexEntries.add(new RetentionIndexEntry(retentionTime, retentionIndex, name));
								}
							}
						}
					}
				}
				/*
				 * Transfer
				 */
				for(IRetentionIndexEntry derivedRetentionIndexEntry : derivedRetentionIndexEntries) {
					separationColumnIndices.put(derivedRetentionIndexEntry);
				}
			}
		}
		//
		return separationColumnIndices;
	}

	private String getRetentionIndexName(String[] standards, int index) {

		return RetentionIndexCalculator.getAlkaneLabel(standards, index) + " -> Derived";
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