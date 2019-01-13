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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IMultivariateCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.OplsCalculatorNipals;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaCalculatorNipals;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaCalculatorSvd;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Variable;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaEvaluation {

	private <V extends IVariable, S extends ISample> Map<ISample, double[]> extractData(ISamples<V, S> samples, String algorithm, IPcaSettings settings, boolean[] isSelectedVariable) {

		Map<ISample, double[]> selectedSamples = new HashMap<>();
		List<? extends IVariable> retentionTimes = samples.getVariables();
		/*
		 * get variables
		 */
		for(int i = 0; i < isSelectedVariable.length; i++) {
			isSelectedVariable[i] = isSelectedVariable[i] & retentionTimes.get(i).isSelected();
			if(settings.IsRemoveUselessVariables()) {
				int numEmptyValues = 0;
				for(ISample sample : samples.getSampleList()) {
					if(!sample.getSampleData().get(i).isEmpty()) {
						numEmptyValues++;
					}
				}
				if(numEmptyValues <= 1) {
					isSelectedVariable[i] = false;
				}
			}
		}
		/*
		 * collect variables
		 */
		int numSelected = 0;
		for(boolean b : isSelectedVariable) {
			if(b) {
				numSelected++;
			}
		}
		final Set<String> groups = samples.getSampleList().stream().map(s -> s.getGroupName()).distinct().collect(Collectors.toList()).stream().limit(2).collect(Collectors.toSet());
		for(ISample sample : samples.getSampleList()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<? extends ISampleData> data = sample.getSampleData();
				selectedSampleData = new double[numSelected];
				int j = 0;
				for(int i = 0; i < data.size(); i++) {
					if(isSelectedVariable[i]) {
						selectedSampleData[j] = data.get(i).getModifiedData();
						j++;
					}
				}
				selectedSamples.put(sample, selectedSampleData);
			}
		}
		if(algorithm.equals(IPcaSettings.OPLS_ALGO_NIPALS)) {
			Map<ISample, double[]> groupSelected = selectedSamples.entrySet().stream().filter(e -> groups.contains(e.getKey().getGroupName())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
			return groupSelected;
		}
		return selectedSamples;
	}

	private <V extends IVariable, S extends ISample> boolean[] selectedVariables(ISamples<V, S> samples, IPcaSettings settings) {

		List<? extends IVariable> retentionTimes = samples.getVariables();
		boolean[] isSelectedVariable = new boolean[retentionTimes.size()];
		Arrays.fill(isSelectedVariable, true);
		for(int i = 0; i < isSelectedVariable.length; i++) {
			isSelectedVariable[i] = isSelectedVariable[i] & retentionTimes.get(i).isSelected();
			if(settings.IsRemoveUselessVariables()) {
				int numEmptyValues = 0;
				for(ISample sample : samples.getSampleList()) {
					if(sample.isSelected()) {
						if(!sample.getSampleData().get(i).isEmpty()) {
							numEmptyValues++;
						}
					}
				}
				if(numEmptyValues <= 1) {
					isSelectedVariable[i] = false;
				}
			}
		}
		return isSelectedVariable;
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

	private double[] getExplainedVariances(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		double summedVariance = principalComponentAnalysis.getSummedVariance();
		double[] explainedVariances = new double[numberOfPrincipalComponents];
		for(int i = 0; i < numberOfPrincipalComponents; i++) {
			explainedVariances[i] = 100.0 / summedVariance * principalComponentAnalysis.getExplainedVariance(i);
		}
		return explainedVariances;
	}

	private double[] getCumulativeExplainedVariances(double[] explainedVariances) {

		double[] cumulativeExplainedVariances = new double[explainedVariances.length];
		double cumVarTemp = 0.0;
		for(int i = 0; i < explainedVariances.length; i++) {
			cumulativeExplainedVariances[i] = cumVarTemp + explainedVariances[i];
			cumVarTemp = cumulativeExplainedVariances[i];
		}
		return cumulativeExplainedVariances;
	}

	private int getNumSampleVars(Map<ISample, double[]> extractData) {

		Iterator<Map.Entry<ISample, double[]>> it = extractData.entrySet().iterator();
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
	private IMultivariateCalculator setupPCA(Map<ISample, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipalComponents, String pcaAlgorithm) {

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
		principalComponentAnalysis.initialize(numSamples, sampleSize, numberOfPrincipalComponents);
		/*
		 * Add the samples.
		 */
		for(Map.Entry<ISample, double[]> entry : pcaPeakMap.entrySet()) {
			principalComponentAnalysis.addObservation(entry.getValue(), entry.getKey(), entry.getKey().getGroupName());
		}
		return principalComponentAnalysis;
	}

	public <V extends IVariable, S extends ISample> PcaResults process(ISamples<V, S> samples, IPcaSettings settings, IProgressMonitor monitor) {

		monitor.subTask("Run PCA");
		int numberOfPrincipalComponents = settings.getNumberOfPrincipalComponents();
		String pcaAlgorithm = settings.getPcaAlgorithm();
		PcaResults pcaResults = new PcaResults(settings);
		boolean[] isSelectedVariables = selectedVariables(samples, settings);
		Map<ISample, double[]> extractData = extractData(samples, pcaAlgorithm, settings, isSelectedVariables);
		setRetentionTime(pcaResults, samples, isSelectedVariables);
		int numVars = getNumSampleVars(extractData);
		/*
		 * Prepare PCA Calculation
		 */
		IMultivariateCalculator principalComponentAnalysis = setupPCA(extractData, numVars, numberOfPrincipalComponents, pcaAlgorithm);
		/*
		 * Compute PCA
		 */
		principalComponentAnalysis.compute();
		/*
		 * Collect PCA results
		 */
		if(!principalComponentAnalysis.getComputeStatus() || !principalComponentAnalysis.isPcaValid()) {
			return null;
		}
		List<double[]> loadingVectors = getLoadingVectors(principalComponentAnalysis, numberOfPrincipalComponents);
		double[] explainedVariances = this.getExplainedVariances(principalComponentAnalysis, numberOfPrincipalComponents);
		double[] cumulativeExplainedVariances = this.getCumulativeExplainedVariances(explainedVariances);
		pcaResults.setLoadingVectors(loadingVectors);
		pcaResults.setExplainedVariances(explainedVariances);
		pcaResults.setCumulativeExplainedVariances(cumulativeExplainedVariances);
		setEigenSpaceAndErrorValues(principalComponentAnalysis, extractData, pcaResults);
		return pcaResults;
	}

	private void setEigenSpaceAndErrorValues(IMultivariateCalculator principalComponentAnalysis, Map<ISample, double[]> pcaPeakMap, IPcaResults pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<IPcaResult> resultsList = new ArrayList<>();
		for(Entry<ISample, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			double[] scoreVector = null;
			ISample sample = entry.getKey();
			double errorMemberShip = Double.NaN;
			IPcaResult pcaResult = new PcaResult(sample);
			pcaResult.setName(sample.getName());
			pcaResult.setGroupName(sample.getGroupName());
			pcaResult.setScoreVector(principalComponentAnalysis.getScoreVector(sample));
			pcaResult.setErrorMemberShip(principalComponentAnalysis.getErrorMetric(sampleData));
			pcaResult.setSampleData(sampleData);
			resultsList.add(pcaResult);
		}
		pcaResults.getPcaResultList().clear();
		pcaResults.getPcaResultList().addAll(resultsList);
	}

	private void setRetentionTime(IPcaResults pcaResults, ISamples<? extends IVariable, ? extends ISample> samples, boolean[] isSelectedVariables) {

		List<IVaribleExtracted> variables = new ArrayList<>();
		for(int i = 0; i < samples.getVariables().size(); i++) {
			if(isSelectedVariables[i]) {
				IVariable variable = samples.getVariables().get(i);
				variables.add(new Variable(variable));
			}
		}
		pcaResults.getExtractedVariables().clear();
		pcaResults.getExtractedVariables().addAll(variables);
	}
}
