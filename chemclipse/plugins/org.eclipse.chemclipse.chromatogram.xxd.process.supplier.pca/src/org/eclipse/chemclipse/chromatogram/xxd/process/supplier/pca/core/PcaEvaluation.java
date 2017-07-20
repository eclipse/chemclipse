/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ejml.example.PrincipalComponentAnalysis;

public class PcaEvaluation {

	private Map<String, double[]> extractData(IPcaResults pcaResults) {

		Map<String, double[]> selectedSamples = new HashMap<>();
		List<Boolean> isSelected = pcaResults.isSelectedRetentionTimes();
		int numSelected = (int)isSelected.stream().filter(b -> b).count();
		for(ISample sample : pcaResults.getSampleList()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<ISampleData> data = sample.getSampleData();
				selectedSampleData = new double[numSelected];
				int j = 0;
				for(int i = 0; i < data.size(); i++) {
					if(isSelected.get(i)) {
						selectedSampleData[j] = data.get(i).getNormalizedData();
						j++;
					}
				}
				selectedSamples.put(sample.getName(), selectedSampleData);
			}
			sample.getPcaResult().setSampleData(selectedSampleData);
		}
		return selectedSamples;
	}

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

	private int getSampleSize(Map<String, double[]> extractData) {

		Iterator<Map.Entry<String, double[]>> it = extractData.entrySet().iterator();
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
	private PrincipalComponentAnalysis initializePCA(Map<String, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipleComponents) {

		/*
		 * Initialize the PCA analysis.
		 */
		int numSamples = pcaPeakMap.size();
		PrincipalComponentAnalysis principleComponentAnalysis = new PrincipalComponentAnalysis();
		principleComponentAnalysis.setup(numSamples, sampleSize);
		/*
		 * Add the samples.
		 */
		for(Map.Entry<String, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			principleComponentAnalysis.addSample(sampleData);
		}
		/*
		 * Compute the basis for the number of principle components.
		 */
		principleComponentAnalysis.computeBasis(numberOfPrincipleComponents);
		return principleComponentAnalysis;
	}

	public IPcaResults process(IPcaResults pcaResults, int numberOfPrincipleComponents, IProgressMonitor monitor) {

		monitor.subTask("Run PCA");
		Map<String, double[]> extractData = extractData(pcaResults);
		int sampleSize = getSampleSize(extractData);
		PrincipalComponentAnalysis principleComponentAnalysis = initializePCA(extractData, sampleSize, numberOfPrincipleComponents);
		List<double[]> basisVectors = getBasisVectors(principleComponentAnalysis, numberOfPrincipleComponents);
		pcaResults.setBasisVectors(basisVectors);
		setEigenSpaceAndErrorValues(principleComponentAnalysis, extractData, pcaResults);
		pcaResults.setNumberOfPrincipleComponents(numberOfPrincipleComponents);
		setGroups(pcaResults);
		return pcaResults;
	}

	private void setEigenSpaceAndErrorValues(PrincipalComponentAnalysis principleComponentAnalysis, Map<String, double[]> pcaPeakMap, IPcaResults pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<ISample> samples = pcaResults.getSampleList();
		for(ISample sample : samples) {
			IPcaResult result = sample.getPcaResult();
			double[] sampleData = null;
			double[] eigenSpace = null;
			double errorMemberShip = Double.NaN;
			if(sample.isSelected()) {
				String sampleName = sample.getName();
				sampleData = pcaPeakMap.get(sampleName);
				eigenSpace = principleComponentAnalysis.sampleToEigenSpace(sampleData);
				errorMemberShip = principleComponentAnalysis.errorMembership(sampleData);
			}
			result.setSampleData(sampleData);
			result.setEigenSpace(eigenSpace);
			result.setErrorMemberShip(errorMemberShip);
		}
	}

	private void setGroups(IPcaResults pcaResults) {

		for(IGroup group : pcaResults.getGroupList()) {
			String groupName = group.getGroupName();
			List<ISample> samples = pcaResults.getSampleList().stream().filter(s -> s.isSelected() && groupName.equals(s.getGroupName())).collect(Collectors.toList());
			if(!samples.isEmpty()) {
				int lentsampleData = samples.get(0).getPcaResult().getSampleData().length;
				double[] sampleData = new double[lentsampleData];
				samples.stream().map(s -> s.getPcaResult().getSampleData()) //
						.forEach(d -> {
							for(int i = 0; i < lentsampleData; i++) {
								sampleData[i] += d[i];
							}
						});
				Arrays.stream(sampleData).forEach(d -> d = d / samples.size());
				group.getPcaResult().setSampleData(sampleData);
				int lentEigenSpance = samples.get(0).getPcaResult().getEigenSpace().length;
				double[] eigenSpace = new double[lentEigenSpance];
				samples.stream().map(s -> s.getPcaResult().getEigenSpace())//
						.forEach(d -> {
							for(int i = 0; i < lentEigenSpance; i++) {
								eigenSpace[i] += d[i];
							}
						});
				Arrays.stream(eigenSpace).forEach(d -> d = d / samples.size());
				group.getPcaResult().setEigenSpace(eigenSpace);
				double errorMemberShip = samples.stream().mapToDouble(s -> s.getPcaResult().getErrorMemberShip()).sum() / samples.size();
				group.getPcaResult().setErrorMemberShip(errorMemberShip);
			} else {
				group.getPcaResult().setSampleData(null);
				group.getPcaResult().setEigenSpace(null);
				group.getPcaResult().setErrorMemberShip(Double.NaN);
			}
		}
	}
}
