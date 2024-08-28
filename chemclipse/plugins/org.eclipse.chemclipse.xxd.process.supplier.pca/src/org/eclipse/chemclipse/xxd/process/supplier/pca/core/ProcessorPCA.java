/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Lorenz Gerber - PCA adapter, algorithm
 * Philip Wenig - get rid of JavaFX, feature selection
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.algorithms.CalculatorNIPALS;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.algorithms.CalculatorOPLS;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.algorithms.CalculatorSVD;
import org.eclipse.chemclipse.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Feature;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.FeatureDataMatrix;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IMultivariateCalculator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ResultPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ResultsPCA;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class ProcessorPCA {

	public <V extends IVariable, S extends ISample> void cleanUnusedVariables(EvaluationPCA evaluationPCA, IProgressMonitor monitor) {

		if(evaluationPCA != null) {
			ISamplesPCA<? extends IVariable, ? extends ISample> samples = evaluationPCA.getSamples();
			if(samples != null) {
				/*
				 * Collect
				 */
				List<IVariable> removeVariables = new ArrayList<>();
				List<? extends IVariable> variables = samples.getVariables();
				List<? extends ISample> sampleList = samples.getSamples();
				List<Integer> indices = new ArrayList<>();
				//
				for(int i = 0; i < variables.size(); i++) {
					IVariable variable = variables.get(i);
					if(!variable.isSelected() || isInvalidVariable(samples, i)) {
						removeVariables.add(variable);
						indices.add(i);
					}
				}
				/*
				 * Remove
				 */
				samples.getVariables().removeAll(removeVariables);
				for(ISample sample : sampleList) {
					List<? extends ISampleData<?>> sampleData = sample.getSampleData();
					List<ISampleData<?>> sampleDataRemove = new ArrayList<>();
					for(int index : indices) {
						sampleDataRemove.add(sampleData.get(index));
					}
					sampleData.removeAll(sampleDataRemove);
				}
				/*
				 * Data Matrix
				 */
				calculateFeatureDataMatrix(evaluationPCA);
			}
		}
	}

	public <V extends IVariable, S extends ISample> EvaluationPCA process(ISamplesPCA<V, S> samples, EvaluationPCA masterEvaluationPCA, IProgressMonitor monitor) throws MathIllegalArgumentException {

		EvaluationPCA evaluationPCA = null;
		if(samples != null) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, "Run PCA", 160);
			try {
				/*
				 * Settings
				 */
				IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
				ResultsPCA results = new ResultsPCA(analysisSettings);
				/*
				 * Template Map
				 */
				Map<String, Boolean> variablesSelectionMap = getVariablesSelectionMap(masterEvaluationPCA != null ? masterEvaluationPCA.getSamples().getVariables() : Collections.emptyList());
				subMonitor.worked(20);
				/*
				 * Preprocessing
				 */
				IPreprocessingSettings preprocessingSettings = analysisSettings.getPreprocessingSettings();
				preprocessingSettings.process(samples, monitor);
				subMonitor.worked(20);
				/*
				 * Variable Extraction
				 */
				int numberOfPrincipalComponents = analysisSettings.getNumberOfPrincipalComponents();
				Algorithm algorithm = analysisSettings.getAlgorithm();
				boolean[] selectedVariables = getSelectedVariables(samples, analysisSettings, variablesSelectionMap);
				Map<ISample, double[]> extractData = extractData(samples, algorithm, analysisSettings, selectedVariables);
				assignVariables(results, samples, selectedVariables, variablesSelectionMap);
				int numberVariables = getNumSampleVars(extractData);
				subMonitor.worked(20);
				/*
				 * Prepare PCA Calculation
				 */
				IMultivariateCalculator principalComponentAnalysis = setupPCA(extractData, numberVariables, numberOfPrincipalComponents, algorithm);
				subMonitor.worked(20);
				/*
				 * Compute PCA
				 */
				principalComponentAnalysis.compute();
				subMonitor.worked(20);
				/*
				 * Collect PCA results
				 */
				if(!principalComponentAnalysis.getComputeStatus()) {
					return null;
				}
				subMonitor.worked(20);
				//
				List<double[]> loadingVectors = getLoadingVectors(principalComponentAnalysis, numberOfPrincipalComponents);
				double[] explainedVariances = this.getExplainedVariances(principalComponentAnalysis, numberOfPrincipalComponents);
				double[] cumulativeExplainedVariances = this.getCumulativeExplainedVariances(explainedVariances);
				results.setLoadingVectors(loadingVectors);
				results.setExplainedVariances(explainedVariances);
				results.setCumulativeExplainedVariances(cumulativeExplainedVariances);
				setEigenSpaceAndErrorValues(principalComponentAnalysis, extractData, results);
				subMonitor.worked(20);
				/*
				 * Feature Data Matrix
				 */
				evaluationPCA = new EvaluationPCA(samples, results);
				calculateFeatureDataMatrix(evaluationPCA);
				subMonitor.worked(20);
			} finally {
				SubMonitor.done(subMonitor);
			}
		}
		//
		return evaluationPCA;
	}

	private void calculateFeatureDataMatrix(EvaluationPCA evaluationPCA) {

		ISamplesPCA<? extends IVariable, ? extends ISample> samplesPCA = evaluationPCA.getSamples();
		/*
		 * Feature Data Matrix
		 */
		List<? extends IVariable> variables = samplesPCA.getVariables();
		List<? extends ISample> samples = samplesPCA.getSamples();
		/*
		 * Samples
		 */
		List<String> sampleNames = new ArrayList<>();
		for(ISample sample : samples) {
			sampleNames.add(sample.getSampleName());
		}
		/*
		 * variable.getClassification() //
		 * variable.getDescription() //
		 * variable.getType() // Retention time (min)
		 * variable.getValue() // 3.466
		 */
		List<Feature> features = new ArrayList<>();
		for(IVariable variable : variables) {
			features.add(new Feature(variable));
		}
		//
		for(int i = 0; i < samples.size(); i++) {
			/*
			 * sampleData.getData() // 50327.8
			 * sampleData.getModifiedData() // 0.524298283655198
			 * sampleData.isEmpty() // false
			 * sampleData.getDataObject() // e.g. PeakMSD
			 */
			ISample sample = samples.get(i);
			List<? extends ISampleData<?>> sampleDataList = sample.getSampleData();
			for(int j = 0; j < sampleDataList.size(); j++) {
				ISampleData<?> sampleData = sampleDataList.get(j);
				features.get(j).getSampleData().add(sampleData);
			}
		}
		//
		evaluationPCA.setFeatureDataMatrix(new FeatureDataMatrix(sampleNames, features));
	}

	private <V extends IVariable, S extends ISample> Map<ISample, double[]> extractData(ISamples<V, S> samples, Algorithm algorithm, IAnalysisSettings settings, boolean[] isSelectedVariable) {

		Map<ISample, double[]> selectedSamples = new HashMap<>();
		List<? extends IVariable> variables = samples.getVariables();
		/*
		 * Variables
		 */
		for(int i = 0; i < isSelectedVariable.length; i++) {
			IVariable variable = variables.get(i);
			isSelectedVariable[i] = isSelectedVariable[i] & variable.isSelected();
			if(settings.isRemoveUselessVariables()) {
				if(isInvalidVariable(samples, i)) {
					isSelectedVariable[i] = false;
					variable.setSelected(false);
				}
			}
		}
		/*
		 * Collect
		 */
		int numSelected = 0;
		for(boolean b : isSelectedVariable) {
			if(b) {
				numSelected++;
			}
		}
		//
		final Set<String> classifications = samples.getSamples().stream().map(s -> s.getClassification()).distinct().collect(Collectors.toList()).stream().limit(2).collect(Collectors.toSet());
		for(ISample sample : samples.getSamples()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<? extends ISampleData<?>> data = sample.getSampleData();
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
		//
		if(algorithm.equals(Algorithm.OPLS)) {
			Map<ISample, double[]> classificationSelected = selectedSamples.entrySet().stream().filter(e -> classifications.contains(e.getKey().getClassification())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
			return classificationSelected;
		}
		//
		return selectedSamples;
	}

	private <V extends IVariable, S extends ISample> boolean[] getSelectedVariables(ISamples<V, S> samples, IAnalysisSettings settings, Map<String, Boolean> variablesSelectionMap) {

		List<? extends IVariable> variables = samples.getVariables();
		boolean[] selectedVariables = new boolean[variables.size()];
		Arrays.fill(selectedVariables, true);
		//
		for(int i = 0; i < selectedVariables.length; i++) {
			/*
			 * Variable
			 */
			IVariable variable = variables.get(i);
			variable.setSelected(true);
			//
			if(isVariableSelected(variable, variablesSelectionMap)) {
				if(settings.isRemoveUselessVariables()) {
					if(isInvalidVariable(samples, i)) {
						selectedVariables[i] = false;
						variable.setSelected(false);
					}
				}
			} else {
				selectedVariables[i] = false;
				variable.setSelected(false);
			}
		}
		//
		return selectedVariables;
	}

	private <V extends IVariable, S extends ISample> boolean isInvalidVariable(ISamples<V, S> samples, int row) {

		return !samples.containsValidData(row);
	}

	private List<double[]> getLoadingVectors(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		/*
		 * Print the basis vectors.
		 */
		List<double[]> loadingVectors = new ArrayList<double[]>();
		for(int principalComponent = 1; principalComponent <= numberOfPrincipalComponents; principalComponent++) {
			double[] loadingVector = principalComponentAnalysis.getLoadingVector(principalComponent);
			loadingVectors.add(loadingVector);
		}
		//
		return loadingVectors;
	}

	private double[] getExplainedVariances(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		double summedVariance = principalComponentAnalysis.getSummedVariance();
		double[] explainedVariances = new double[numberOfPrincipalComponents];
		for(int i = 0; i < numberOfPrincipalComponents; i++) {
			explainedVariances[i] = 100.0 / summedVariance * principalComponentAnalysis.getExplainedVariance(i);
		}
		//
		return explainedVariances;
	}

	private double[] getCumulativeExplainedVariances(double[] explainedVariances) {

		double[] cumulativeExplainedVariances = new double[explainedVariances.length];
		double cumVarTemp = 0.0;
		for(int i = 0; i < explainedVariances.length; i++) {
			cumulativeExplainedVariances[i] = cumVarTemp + explainedVariances[i];
			cumVarTemp = cumulativeExplainedVariances[i];
		}
		//
		return cumulativeExplainedVariances;
	}

	private int getNumSampleVars(Map<ISample, double[]> extractData) {

		Iterator<Map.Entry<ISample, double[]>> it = extractData.entrySet().iterator();
		if(it.hasNext()) {
			return it.next().getValue().length;
		}
		//
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
	 * @throws Exception
	 */
	private IMultivariateCalculator setupPCA(Map<ISample, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipalComponents, Algorithm algorithm) throws MathIllegalArgumentException {

		/*
		 * Initialize the PCA analysis.
		 */
		int numSamples = pcaPeakMap.size();
		IMultivariateCalculator principalComponentAnalysis = null;
		if(algorithm.equals(Algorithm.NIPALS)) {
			principalComponentAnalysis = new CalculatorNIPALS(numSamples, sampleSize, numberOfPrincipalComponents);
		} else if(algorithm.equals(Algorithm.SVD)) {
			principalComponentAnalysis = new CalculatorSVD(numSamples, sampleSize, numberOfPrincipalComponents);
		} else if(algorithm.equals(Algorithm.OPLS)) {
			principalComponentAnalysis = new CalculatorOPLS(numSamples, sampleSize, numberOfPrincipalComponents);
		}
		/*
		 * Add the samples.
		 */
		for(Map.Entry<ISample, double[]> entry : pcaPeakMap.entrySet()) {
			principalComponentAnalysis.addObservation(entry.getValue(), entry.getKey(), entry.getKey().getGroupName(), entry.getKey().getClassification());
		}
		//
		return principalComponentAnalysis;
	}

	private void setEigenSpaceAndErrorValues(IMultivariateCalculator principalComponentAnalysis, Map<ISample, double[]> pcaPeakMap, IResultsPCA<IResultPCA, IVariable> pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<IResultPCA> resultsList = new ArrayList<>();
		for(Entry<ISample, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			ISample sample = entry.getKey();
			IResultPCA pcaResult = new ResultPCA(sample);
			pcaResult.setScoreVector(principalComponentAnalysis.getScoreVector(sample));
			pcaResult.setErrorMemberShip(principalComponentAnalysis.getErrorMetric(sampleData));
			pcaResult.setSampleData(sampleData);
			resultsList.add(pcaResult);
		}
		//
		pcaResults.getPcaResultList().clear();
		pcaResults.getPcaResultList().addAll(resultsList);
	}

	private void assignVariables(IResultsPCA<IResultPCA, IVariable> pcaResults, ISamples<? extends IVariable, ? extends ISample> samples, boolean[] isSelectedVariables, Map<String, Boolean> variablesSelectionMap) {

		/*
		 * Clear the variables.
		 */
		pcaResults.getExtractedVariables().clear();
		/*
		 * Assign and validate the variables again.
		 */
		for(int i = 0; i < samples.getVariables().size(); i++) {
			if(isSelectedVariables[i]) {
				IVariable variable = samples.getVariables().get(i);
				variable.setSelected(isVariableSelected(variable, variablesSelectionMap));
				pcaResults.getExtractedVariables().add(variable);
			}
		}
	}

	private Map<String, Boolean> getVariablesSelectionMap(List<? extends IVariable> templateVariables) {

		/*
		 * Map existing variables. They have been probably deactivated.
		 */
		Map<String, Boolean> variablesSelectionMap = new HashMap<>();
		for(IVariable variable : templateVariables) {
			variablesSelectionMap.put(variable.getValue(), variable.isSelected());
		}
		//
		return variablesSelectionMap;
	}

	private boolean isVariableSelected(IVariable variable, Map<String, Boolean> variablesSelectionMap) {

		if(variable.isSelected()) {
			return variablesSelectionMap.getOrDefault(variable.getValue(), true);
		}
		//
		return false;
	}
}