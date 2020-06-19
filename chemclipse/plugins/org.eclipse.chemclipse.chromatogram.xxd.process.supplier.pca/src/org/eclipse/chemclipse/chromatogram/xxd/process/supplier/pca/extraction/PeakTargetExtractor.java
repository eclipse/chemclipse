/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.Target;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakTargetExtractor {

	private static final String DELIMITER = ",";
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public Samples extractPeakData(Map<IDataInputEntry, IPeaks<?>> peaks, IProgressMonitor monitor) {

		Samples samples = new Samples(peaks.keySet());
		//
		Map<String, IPeaks<?>> peakMap = new LinkedHashMap<>();
		peaks.forEach((dataInputEntry, peaksInput) -> {
			peakMap.put(dataInputEntry.getName(), peaksInput);
		});
		//
		Set<String> targets = extractPeakTargets(peaks.values());
		Map<String, SortedMap<String, IPeak>> extractPeaks = exctractPcaPeakMap(peakMap, targets);
		samples.getVariables().addAll(Target.create(targets));
		//
		setExtractData(extractPeaks, samples);
		setClassifierAndDescription(samples);
		//
		return samples;
	}

	private Set<String> extractPeakTargets(Collection<IPeaks<?>> peaksCollection) {

		Set<String> targets = new HashSet<>();
		//
		for(IPeaks<?> peaks : peaksCollection) {
			for(IPeak peak : peaks.getPeaks()) {
				IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets());
				if(identificationTarget != null) {
					ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
					targets.add(libraryInformation.getName());
				}
			}
		}
		//
		return targets;
	}

	private Map<String, SortedMap<String, IPeak>> exctractPcaPeakMap(Map<String, IPeaks<?>> peakMap, Set<String> targets) {

		Map<String, SortedMap<String, IPeak>> pcaPeaks = new LinkedHashMap<>();
		//
		for(Map.Entry<String, IPeaks<?>> peakEnry : peakMap.entrySet()) {
			String name = peakEnry.getKey();
			IPeaks<?> peaks = peakEnry.getValue();
			TreeMap<String, IPeak> peakTree = new TreeMap<>();
			//
			for(IPeak peak : peaks.getPeaks()) {
				IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets());
				String target;
				if(identificationTarget != null) {
					target = identificationTarget.getLibraryInformation().getName();
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

	private void setExtractData(Map<String, SortedMap<String, IPeak>> extractData, Samples samples) {

		List<IVariable> extractedTargets = samples.getVariables();
		//
		for(Sample sample : samples.getSampleList()) {
			Iterator<IVariable> iterator = extractedTargets.iterator();
			SortedMap<String, IPeak> extractPeak = extractData.get(sample.getName());
			while(iterator.hasNext()) {
				String target = iterator.next().getValue();
				IPeak peak = extractPeak.get(target);
				if(peak != null) {
					PeakSampleData sampleData = new PeakSampleData(peak.getIntegratedArea(), peak);
					sampleData.setPeak(peak);
					sample.getSampleData().add(sampleData);
				} else {
					PeakSampleData sampleData = new PeakSampleData();
					sample.getSampleData().add(sampleData);
				}
			}
		}
	}

	private void setClassifierAndDescription(Samples samples) {

		for(int i = 0; i < samples.getVariables().size(); i++) {
			final int j = i;
			final Set<String> descriptions = new HashSet<>();
			final Set<String> classifier = new HashSet<>();
			/*
			 * Fetch classifications and descriptions.
			 */
			samples.getSampleList().stream().map(s -> s.getSampleData()).map(d -> d.get(j).getPeak()).forEach(peak -> {
				if(peak.isPresent()) {
					/*
					 * Classifier / Descriptions
					 */
					IPeak peakX = peak.get();
					IPeakModel peakModel = peakX.getPeakModel();
					classifier.addAll(peakX.getClassifier());
					descriptions.add(decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogram.MINUTE_CORRELATION_FACTOR));
				}
			});
			/*
			 * Classifier
			 */
			if(!classifier.isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				List<String> list = new ArrayList<>(classifier);
				Collections.sort(list);
				Iterator<String> iterator = list.iterator();
				while(iterator.hasNext()) {
					stringBuilder.append(iterator.next());
					if(iterator.hasNext()) {
						stringBuilder.append(DELIMITER);
						stringBuilder.append(" ");
					}
				}
				//
				samples.getVariables().get(i).setClassification(stringBuilder.toString());
			}
			/*
			 * Description -> Best Target
			 */
			if(!descriptions.isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				List<String> list = new ArrayList<>(descriptions);
				Collections.sort(list);
				Iterator<String> iterator = list.iterator();
				while(iterator.hasNext()) {
					stringBuilder.append(iterator.next());
					if(iterator.hasNext()) {
						stringBuilder.append(DELIMITER);
						stringBuilder.append(" ");
					}
				}
				//
				samples.getVariables().get(i).setDescription(stringBuilder.toString());
			}
		}
	}
}
