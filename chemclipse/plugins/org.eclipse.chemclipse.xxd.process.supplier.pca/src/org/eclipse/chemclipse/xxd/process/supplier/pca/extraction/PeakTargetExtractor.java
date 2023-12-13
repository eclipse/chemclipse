/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.extraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.Target;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.ValueOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.DescriptionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;

public class PeakTargetExtractor extends AbstractClassifierDescriptionExtractor {

	public Samples extractPeakData(Map<IDataInputEntry, IPeaks<IPeak>> peaks, DescriptionOption descriptionOption, ValueOption valueOption) {

		List<Sample> samplesList = new ArrayList<>();
		peaks.keySet().forEach(d -> samplesList.add(new Sample(d.getSampleName(), d.getGroupName())));
		Samples samples = new Samples(samplesList);
		//
		Map<String, IPeaks<IPeak>> peakMap = new LinkedHashMap<>();
		peaks.forEach((dataInputEntry, peaksInput) -> {
			peakMap.put(dataInputEntry.getSampleName(), peaksInput);
		});
		//
		Set<String> targets = extractPeakTargets(peaks.values());
		Map<String, SortedMap<String, IPeak>> extractPeaks = exctractPcaPeakMap(peakMap);
		samples.getVariables().addAll(Target.create(targets));
		//
		setExtractData(extractPeaks, samples, valueOption);
		setClassifierAndDescription(samples, descriptionOption);
		//
		return samples;
	}

	private Set<String> extractPeakTargets(Collection<IPeaks<IPeak>> peaksCollection) {

		Set<String> targets = new HashSet<>();
		//
		for(IPeaks<?> peaks : peaksCollection) {
			for(IPeak peak : peaks.getPeaks()) {
				ILibraryInformation libraryInformation = IIdentificationTarget.getLibraryInformation(peak);
				if(libraryInformation != null) {
					targets.add(libraryInformation.getName());
				}
			}
		}
		//
		return targets;
	}

	private Map<String, SortedMap<String, IPeak>> exctractPcaPeakMap(Map<String, IPeaks<IPeak>> peakMap) {

		Map<String, SortedMap<String, IPeak>> pcaPeaks = new LinkedHashMap<>();
		//
		for(Map.Entry<String, IPeaks<IPeak>> peakEnry : peakMap.entrySet()) {
			String name = peakEnry.getKey();
			IPeaks<IPeak> peaks = peakEnry.getValue();
			TreeMap<String, IPeak> peakTree = new TreeMap<>();
			//
			for(IPeak peak : peaks.getPeaks()) {
				String target;
				ILibraryInformation libraryInformation = IIdentificationTarget.getLibraryInformation(peak);
				if(libraryInformation != null) {
					target = libraryInformation.getName();
				} else {
					target = "";
				}
				peakTree.put(target, peak);
			}
			pcaPeaks.put(name, peakTree);
		}
		//
		return pcaPeaks;
	}

	private void setExtractData(Map<String, SortedMap<String, IPeak>> extractData, Samples samples, ValueOption valueOption) {

		List<IVariable> extractedTargets = samples.getVariables();
		boolean useQuantitationValue = ValueOption.CONCENTRATION.equals(valueOption);
		//
		for(Sample sample : samples.getSampleList()) {
			Iterator<IVariable> iterator = extractedTargets.iterator();
			SortedMap<String, IPeak> extractPeak = extractData.get(sample.getSampleName());
			while(iterator.hasNext()) {
				String target = iterator.next().getValue();
				IPeak peak = extractPeak.get(target);
				if(peak != null) {
					double value = 0.0d;
					if(useQuantitationValue) {
						/*
						 * Concentration
						 */
						List<IQuantitationEntry> quantitationEntries = peak.getQuantitationEntries();
						if(!quantitationEntries.isEmpty()) {
							value = quantitationEntries.get(0).getConcentration();
						}
					} else {
						/*
						 * Area
						 */
						value = peak.getIntegratedArea();
					}
					//
					PeakSampleData sampleData = new PeakSampleData(value, peak);
					sampleData.setPeak(peak);
					sample.getSampleData().add(sampleData);
				} else {
					PeakSampleData sampleData = new PeakSampleData();
					sample.getSampleData().add(sampleData);
				}
			}
		}
	}
}