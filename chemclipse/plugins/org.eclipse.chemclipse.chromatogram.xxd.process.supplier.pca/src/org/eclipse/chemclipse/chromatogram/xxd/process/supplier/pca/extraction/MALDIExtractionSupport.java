/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements
 * Matthias Mailänder - adapted for MALDI-TOF MS
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.statistics.MassToChargeRatio;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.core.runtime.IProgressMonitor;

public class MALDIExtractionSupport {

	public enum ExtractionType {
		CLOSEST_MASS, LINEAR_INTERPOLATION_MASS;
	}

	private double beginMassMax;
	private double endMassMin;
	private ExtractionType extractionType;
	private int maximalNumberPeaks;
	private double massWindow;
	private boolean useDefaultProperties;

	public MALDIExtractionSupport(int massWindow, int maximalNumberPeaks, ExtractionType extractionType, boolean useDefaultProperties) {

		this.massWindow = massWindow;
		this.extractionType = extractionType;
		this.useDefaultProperties = useDefaultProperties;
		this.maximalNumberPeaks = maximalNumberPeaks;
		this.extractionType = extractionType;
	}

	public Samples process(Map<IDataInputEntry, Collection<IIon>> dataInput, IProgressMonitor monitor) {

		/*
		 * Initialize PCA Results
		 */
		List<Sample> samplesList = new ArrayList<>();
		dataInput.keySet().forEach(d -> samplesList.add(new Sample(d.getName(), d.getGroupName())));
		Samples samples = new Samples(samplesList);
		/*
		 * Extract data
		 */
		Map<String, NavigableMap<Double, Float>> extractIons = extractIons(dataInput, monitor);
		//
		boolean similarMassSpectrum = true;
		if(useDefaultProperties) {
			Collection<NavigableMap<Double, Float>> dataSet = extractIons.values();
			boolean isFirst = true;
			Set<Double> massRange = new HashSet<>();
			int massRangeSize = massRange.size();
			for(NavigableMap<Double, Float> data : dataSet) {
				if(isFirst) {
					massRange.addAll(data.keySet());
				} else {
					if(data.size() != massRange.size()) {
						similarMassSpectrum = false;
						break;
					}
					long dataSize = data.keySet().stream().filter(mz -> massRange.contains(mz)).count();
					if(dataSize != massRangeSize) {
						similarMassSpectrum = false;
						break;
					}
				}
			}
		}
		double size = ((endMassMin - beginMassMax) / massWindow);
		if(size > maximalNumberPeaks) {
			massWindow = (endMassMin - beginMassMax) / maximalNumberPeaks;
			similarMassSpectrum = false;
		}
		if(similarMassSpectrum && useDefaultProperties) {
			useDefaultProperties(samples, extractIons);
		} else {
			switch(extractionType) {
				case CLOSEST_MASS:
					setClosestMass(samples, extractIons);
					break;
				case LINEAR_INTERPOLATION_MASS:
					interpolation(samples, extractIons, new LinearInterpolator());
					break;
			}
		}
		return samples;
	}

	private Map<String, NavigableMap<Double, Float>> extractIons(Map<IDataInputEntry, Collection<IIon>> inputs, IProgressMonitor monitor) {

		beginMassMax = 0;
		endMassMin = Integer.MAX_VALUE;
		Map<String, NavigableMap<Double, Float>> scanMap = new HashMap<>();
		for(Entry<IDataInputEntry, Collection<IIon>> input : inputs.entrySet()) {
			TreeMap<Double, Float> extractPeaks = new TreeMap<>();
			Collection<IIon> ions = input.getValue();
			/*
			 * extract ions
			 */
			for(IIon ion : ions) {
				float abundance = ion.getAbundance();
				double mass = ion.getIon();
				extractPeaks.put(mass, abundance);
			}
			double firstMass = extractPeaks.firstKey();
			double lastMass = extractPeaks.lastKey();
			if(firstMass > beginMassMax) {
				beginMassMax = firstMass;
			}
			if(lastMass < endMassMin) {
				endMassMin = lastMass;
			}
			scanMap.put(input.getKey().getName(), extractPeaks);
		}
		return scanMap;
	}

	private Float getClosestMass(NavigableMap<Double, Float> ions, double i) {

		Entry<Double, Float> massAbundanceCeil = ions.ceilingEntry(i);
		Entry<Double, Float> massAbundanceFloor = ions.floorEntry(i);
		if(massAbundanceCeil != null && massAbundanceFloor != null) {
			if((massAbundanceCeil.getKey() - i) < (i - massAbundanceFloor.getKey())) {
				return massAbundanceCeil.getValue();
			} else {
				return massAbundanceFloor.getValue();
			}
		} else if(massAbundanceCeil != null) {
			return massAbundanceCeil.getValue();
		} else if(massAbundanceFloor != null) {
			return massAbundanceFloor.getValue();
		}
		return null;
	}

	private void interpolation(Samples samples, Map<String, NavigableMap<Double, Float>> extractIons, UnivariateInterpolator interpolator) {

		for(Sample sample : samples.getSampleList()) {
			List<PeakSampleData> data = sample.getSampleData();
			NavigableMap<Double, Float> ions = extractIons.get(sample.getSampleName());
			double[] mz = new double[ions.size()];
			double[] abundance = new double[ions.size()];
			int j = 0;
			Iterator<Entry<Double, Float>> it = ions.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Double, Float> entry = it.next();
				mz[j] = entry.getKey();
				abundance[j] = entry.getValue();
				j++;
			}
			UnivariateFunction fun = interpolator.interpolate(mz, abundance);
			for(double i = beginMassMax; i <= endMassMin; i += massWindow) {
				double value = fun.value(i);
				PeakSampleData d = new PeakSampleData(value, null);
				data.add(d);
			}
		}
		setMassToChargeRatio(samples);
	}

	private void setClosestMass(Samples samples, Map<String, NavigableMap<Double, Float>> extractIons) {

		for(Sample sample : samples.getSampleList()) {
			List<PeakSampleData> data = sample.getSampleData();
			NavigableMap<Double, Float> scans = extractIons.get(sample.getSampleName());
			for(double i = beginMassMax; i <= endMassMin; i += massWindow) {
				Float value = getClosestMass(scans, i);
				PeakSampleData d = new PeakSampleData(value, null);
				data.add(d);
			}
		}
		setMassToChargeRatio(samples);
	}

	private void setMassToChargeRatio(Samples samples) {

		List<Double> mz = new ArrayList<>();
		for(double i = beginMassMax; i <= endMassMin; i += massWindow) {
			mz.add(i);
		}
		samples.getVariables().addAll(MassToChargeRatio.create(mz));
	}

	private void useDefaultProperties(Samples samples, Map<String, NavigableMap<Double, Float>> extractIons) {

		Set<Double> massToChargeRatioSet = extractIons.entrySet().iterator().next().getValue().keySet();
		List<Double> massToChargeRatio = new ArrayList<>(massToChargeRatioSet);
		Collections.sort(massToChargeRatio);
		for(Sample sample : samples.getSampleList()) {
			Iterator<Double> it = massToChargeRatio.iterator();
			List<PeakSampleData> data = sample.getSampleData();
			NavigableMap<Double, Float> scans = extractIons.get(sample.getSampleName());
			while(it.hasNext()) {
				Double mz = it.next();
				Float abundance = scans.get(mz);
				PeakSampleData d;
				if(abundance != null) {
					d = new PeakSampleData(abundance, null);
				} else {
					abundance = getClosestMass(scans, mz);
					d = new PeakSampleData(abundance, null);
				}
				data.add(d);
			}
		}
		samples.getVariables().addAll(MassToChargeRatio.create(massToChargeRatio));
	}
}