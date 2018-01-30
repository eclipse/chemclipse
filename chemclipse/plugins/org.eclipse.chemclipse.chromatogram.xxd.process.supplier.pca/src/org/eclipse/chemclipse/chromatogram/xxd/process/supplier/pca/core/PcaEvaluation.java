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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Variable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ejml.example.PrincipalComponentAnalysis;

public class PcaEvaluation {

	private <V extends IVariable, S extends ISample<? extends ISampleData>> Map<ISample<?>, double[]> extractData(ISamples<V, S> samples) {

		Map<ISample<? extends ISampleData>, double[]> selectedSamples = new HashMap<>();
		List<? extends IVariable> retentionTimes = samples.getVariables();
		int numSelected = (int)retentionTimes.stream().filter(r -> r.isSelected()).count();
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
		setNaNValues(selectedSamples);
		return selectedSamples;
	}

	// will implememt more generic version
	private List<double[]> getBasisVectors(PrincipalComponentAnalysis principleComponentAnalysis, int numberOfPrincipleComponents) {

		/*
		 * Print the basis vectors.
		 */
		List<double[]> basisVectors = new ArrayList<double[]>();
		for(int principleComponent = 0; principleComponent < numberOfPrincipleComponents; principleComponent++) {
			double[] basisVector = principleComponentAnalysis.getBasisVector(principleComponent);
			basisVectors.add(basisVector);
		}
		return basisVectors;
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
	 * @param numberOfPrincipleComponents
	 * @return PrincipleComponentAnalysis
	 */
	private PrincipalComponentAnalysis initializePCA(Map<ISample<?>, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipleComponents) {

		/*
		 * Initialize the PCA analysis.
		 */
		int numSamples = pcaPeakMap.size();
		PrincipalComponentAnalysis principleComponentAnalysis = new PrincipalComponentAnalysis();
		principleComponentAnalysis.setup(numSamples, sampleSize);
		/*
		 * Add the samples.
		 */
		for(Map.Entry<ISample<?>, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			principleComponentAnalysis.addSample(sampleData);
		}
		/*
		 * Compute the basis for the number of principle components.
		 */
		principleComponentAnalysis.computeBasis(numberOfPrincipleComponents);
		return principleComponentAnalysis;
	}

	public <V extends IVariable, S extends ISample<? extends ISampleData>> IPcaResults process(ISamples<V, S> samples, IPcaSettings settings, IProgressMonitor monitor) {

		monitor.subTask("Run PCA");
		int numberOfPrincipleComponents = settings.getNumberOfPrincipalComponents();
		IPcaResults pcaResults = new PcaResults(settings);
		Map<ISample<?>, double[]> extractData = extractData(samples);
		setRetentionTime(pcaResults, samples);
		int sampleSize = getSampleSize(extractData);
		PrincipalComponentAnalysis principleComponentAnalysis = initializePCA(extractData, sampleSize, numberOfPrincipleComponents);
		List<double[]> basisVectors = getBasisVectors(principleComponentAnalysis, numberOfPrincipleComponents);
		pcaResults.setBasisVectors(basisVectors);
		setEigenSpaceAndErrorValues(principleComponentAnalysis, extractData, pcaResults);
		// setGroups(pcaResults, samples);
		return pcaResults;
	}

	private void setEigenSpaceAndErrorValues(PrincipalComponentAnalysis principleComponentAnalysis, Map<ISample<?>, double[]> pcaPeakMap, IPcaResults pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<IPcaResult> resultsList = new ArrayList<>();
		for(Entry<ISample<?>, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			double[] eigenSpace = null;
			ISample<?> sample = entry.getKey();
			double errorMemberShip = Double.NaN;
			IPcaResult pcaResult = new PcaResult(sample);
			pcaResult.setName(sample.getName());
			pcaResult.setGroupName(sample.getGroupName());
			eigenSpace = principleComponentAnalysis.sampleToEigenSpace(sampleData);
			errorMemberShip = principleComponentAnalysis.errorMembership(sampleData);
			pcaResult.setSampleData(sampleData);
			pcaResult.setEigenSpace(eigenSpace);
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
