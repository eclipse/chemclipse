/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Lorenz Gerber - PCA adapter, algorithm
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IMultivariateCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.OplsCalculatorNipals;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaCalculatorNipals;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaCalculatorSvd;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Variable;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaEvaluation {

	private <V extends IVariable, S extends ISample<? extends ISampleData>> Map<ISample<?>, double[]> extractData(ISamples<V, S> samples, String algorithm) {

		Map<ISample<? extends ISampleData>, double[]> selectedSamples = new HashMap<>();
		List<? extends IVariable> retentionTimes = samples.getVariables();
		int numSelected = (int)retentionTimes.stream().filter(r -> r.isSelected()).count();
		final Set<String> groups = samples.getSampleList().stream().map(s -> s.getGroupName()).distinct().collect(Collectors.toList()).stream().limit(2).collect(Collectors.toSet());
		for(ISample<? extends ISampleData> sample : samples.getSampleList()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<? extends ISampleData> data = sample.getSampleData();
				selectedSampleData = new double[numSelected];
				int j = 0;
				for(int i = 0; i < data.size(); i++) {
					if(retentionTimes.get(i).isSelected()) {
						selectedSampleData[j] = data.get(i).getModifiedData();
						j++;
					}
				}
				selectedSamples.put(sample, selectedSampleData);
			}
		}
		if(algorithm.equals(IPcaSettings.OPLS_ALGO_NIPALS)) {
			Map<ISample<? extends ISampleData>, double[]> groupSelected = selectedSamples.entrySet().stream().filter(e -> groups.contains(e.getKey().getGroupName())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
			setNaNValues(groupSelected);
			return groupSelected;
		}
		setNaNValues(selectedSamples);
		return selectedSamples;
	}

	private List<double[]> getLoadingVectors(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		/*
		 * Print the basis vectors.
		 */
		List<double[]> loadingVectors = new ArrayList<double[]>();
		for(int principalComponent = 0; principalComponent < numberOfPrincipalComponents; principalComponent++) {
			double[] loadingVector = principalComponentAnalysis.getLoadingVector(principalComponent);
			loadingVectors.add(loadingVector);
		}
		return loadingVectors;
	}

	private List<double[]> getExplainedVariance(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		principalComponentAnalysis.getSummedVariance();
		List<double[]> explainedVariance = new ArrayList<double[]>();
		for(int principalComponent = 0; principalComponent < numberOfPrincipalComponents; principalComponent++) {
			// calculate explained variance in principalComponentAnalysis
		}
		return explainedVariance;
	}

	private int getSampleSize(Map<ISample<?>, double[]> extractData) {

		Iterator<Map.Entry<ISample<?>, double[]>> it = extractData.entrySet().iterator();
		if(it.hasNext()) {
			return it.next().getValue().length;
		}
		return -1;
	}

	/**
	 * Initializes the PCA analysis.
	 *
	 * @param pcaPeakMap
	 * @param numSamples
	 * @param sampleSize
	 * @param numberOfPrincipalComponents
	 * @return PrincipalComponentAnalysis
	 */
	private IMultivariateCalculator setupPCA(Map<ISample<?>, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipalComponents, String pcaAlgorithm) {

		/*
		 * Initialize the PCA analysis.
		 */
		int numSamples = pcaPeakMap.size();
		IMultivariateCalculator principalComponentAnalysis = null;
		if(pcaAlgorithm.equals(IPcaSettings.PCA_ALGO_NIPALS)) {
			principalComponentAnalysis = new PcaCalculatorNipals();
		} else if(pcaAlgorithm.equals(IPcaSettings.PCA_ALGO_SVD)) {
			principalComponentAnalysis = new PcaCalculatorSvd();
		} else if(pcaAlgorithm.equals(IPcaSettings.OPLS_ALGO_NIPALS)) {
			principalComponentAnalysis = new OplsCalculatorNipals();
		}
		principalComponentAnalysis.initialize(numSamples, sampleSize);
		/*
		 * Add the samples.
		 */
		for(Map.Entry<ISample<?>, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			ISample<?> sampleKey = entry.getKey();
			principalComponentAnalysis.addObservation(sampleData);
			principalComponentAnalysis.addObservationKey(sampleKey);
			principalComponentAnalysis.addGroupName(entry.getKey().getGroupName());
		}
		return principalComponentAnalysis;
	}

	public <V extends IVariable, S extends ISample<? extends ISampleData>> PcaResults process(ISamples<V, S> samples, IPcaSettings settings, IProgressMonitor monitor) {

		monitor.subTask("Run PCA");
		int numberOfPrincipalComponents = settings.getNumberOfPrincipalComponents();
		String pcaAlgorithm = settings.getPcaAlgorithm();
		PcaResults pcaResults = new PcaResults(settings);
		Map<ISample<?>, double[]> extractData = extractData(samples, pcaAlgorithm);
		setRetentionTime(pcaResults, samples);
		int sampleSize = getSampleSize(extractData);
		/*
		 * Prepare PCA Calculation
		 */
		IMultivariateCalculator principalComponentAnalysis = setupPCA(extractData, sampleSize, numberOfPrincipalComponents, pcaAlgorithm);
		/*
		 * Compute PCA
		 */
		principalComponentAnalysis.compute(numberOfPrincipalComponents);
		/*
		 * Collect PCA results
		 */
		List<double[]> loadingVectors = getLoadingVectors(principalComponentAnalysis, numberOfPrincipalComponents);
		double summedVariance = principalComponentAnalysis.getSummedVariance();
		List<Double> extractedVariances = new ArrayList<>();
		for(int i = 0; i < numberOfPrincipalComponents; i++) {
			extractedVariances.add(principalComponentAnalysis.getExtractedVariance(i));
		}
		pcaResults.setLoadingVectors(loadingVectors);
		setEigenSpaceAndErrorValues(principalComponentAnalysis, extractData, pcaResults);
		// setGroups(pcaResults, samples);
		return pcaResults;
	}

	private void setEigenSpaceAndErrorValues(IMultivariateCalculator principalComponentAnalysis, Map<ISample<?>, double[]> pcaPeakMap, IPcaResults pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<IPcaResult> resultsList = new ArrayList<>();
		for(Entry<ISample<?>, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			double[] scoreVector = null;
			ISample<?> sample = entry.getKey();
			double errorMemberShip = Double.NaN;
			IPcaResult pcaResult = new PcaResult(sample);
			pcaResult.setName(sample.getName());
			pcaResult.setGroupName(sample.getGroupName());
			scoreVector = principalComponentAnalysis.getScoreVector(sample);
			errorMemberShip = principalComponentAnalysis.getErrorMetric(sampleData);
			pcaResult.setSampleData(sampleData);
			pcaResult.setScoreVector(scoreVector);
			pcaResult.setErrorMemberShip(errorMemberShip);
			pcaResult.setSampleData(sampleData);
			resultsList.add(pcaResult);
		}
		pcaResults.getPcaResultList().setAll(resultsList);
	}

	private void setNaNValues(Map<ISample<?>, double[]> selectedSamples) {

		Collection<double[]> values = selectedSamples.values();
		Optional<double[]> v = values.stream().findFirst();
		if(v.isPresent()) {
			for(int i = 0; i < v.get().length; i++) {
				double sum = 0;
				int count = 0;
				for(double[] value : values) {
					if(!Double.isNaN(value[i])) {
						sum += value[i];
						count++;
					}
				}
				double mean = count != 0 ? sum / count : 0;
				for(double[] value : values) {
					if(Double.isNaN(value[i])) {
						value[i] = mean;
					}
				}
			}
		}
	}

	private void setRetentionTime(IPcaResults pcaResults, ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples) {

		List<IVaribleExtracted> variables = new ArrayList<>();
		for(int i = 0; i < samples.getVariables().size(); i++) {
			if(samples.getVariables().get(i).isSelected()) {
				IVariable variable = samples.getVariables().get(i);
				variables.add(new Variable(variable));
			}
		}
		pcaResults.getExtractedVariables().setAll(variables);
	}
}
