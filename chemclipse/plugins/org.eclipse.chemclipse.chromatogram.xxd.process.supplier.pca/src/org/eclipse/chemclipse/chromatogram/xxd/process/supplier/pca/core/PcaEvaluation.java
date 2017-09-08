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
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ejml.example.PrincipalComponentAnalysis;

public class PcaEvaluation {

	private Map<String, double[]> extractData(ISamples samples) {

		Map<String, double[]> selectedSamples = new HashMap<>();
		List<IRetentionTime> retentionTimes = samples.getExtractedRetentionTimes();
		int numSelected = (int)retentionTimes.stream().filter(r -> r.isSelected()).count();
		for(ISample sample : samples.getSampleList()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<ISampleData> data = sample.getSampleData();
				selectedSampleData = new double[numSelected];
				int j = 0;
				for(int i = 0; i < data.size(); i++) {
					if(retentionTimes.get(i).isSelected()) {
						selectedSampleData[j] = data.get(i).getModifiedData();
						j++;
					}
				}
				selectedSamples.put(sample.getName(), selectedSampleData);
			}
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

	public IPcaResults process(ISamples samples, int numberOfPrincipleComponents, IProgressMonitor monitor) {

		monitor.subTask("Run PCA");
		IPcaResults pcaResults = new PcaResults();
		Map<String, double[]> extractData = extractData(samples);
		setRetentionTime(pcaResults, samples);
		int sampleSize = getSampleSize(extractData);
		PrincipalComponentAnalysis principleComponentAnalysis = initializePCA(extractData, sampleSize, numberOfPrincipleComponents);
		List<double[]> basisVectors = getBasisVectors(principleComponentAnalysis, numberOfPrincipleComponents);
		pcaResults.setBasisVectors(basisVectors);
		setEigenSpaceAndErrorValues(principleComponentAnalysis, extractData, pcaResults);
		pcaResults.setNumberOfPrincipleComponents(numberOfPrincipleComponents);
		setGroups(pcaResults, samples);
		return pcaResults;
	}

	private void setEigenSpaceAndErrorValues(PrincipalComponentAnalysis principleComponentAnalysis, Map<String, double[]> pcaPeakMap, IPcaResults pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<IPcaResult> resultsList = new ArrayList<>();
		for(Entry<String, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			double[] eigenSpace = null;
			String sampleName = entry.getKey();
			double errorMemberShip = Double.NaN;
			IPcaResult pcaResult = new PcaResult();
			pcaResult.setName(sampleName);
			sampleData = pcaPeakMap.get(sampleName);
			eigenSpace = principleComponentAnalysis.sampleToEigenSpace(sampleData);
			errorMemberShip = principleComponentAnalysis.errorMembership(sampleData);
			pcaResult.setSampleData(sampleData);
			pcaResult.setEigenSpace(eigenSpace);
			pcaResult.setErrorMemberShip(errorMemberShip);
			pcaResult.setSampleData(sampleData);
			resultsList.add(pcaResult);
		}
		pcaResults.setPcaResultList(resultsList);
	}

	private void setGroups(IPcaResults pcaResults, ISamples samples) {

		for(IGroup group : samples.getGroupList()) {
			String groupName = group.getGroupName();
			IPcaResult pcaResultGroup = new PcaResult();
			List<IPcaResult> pcaResultsList = pcaResults.getPcaResultList().stream().filter(r -> groupName.equals(r.getGroupName())).collect(Collectors.toList());
			if(!pcaResultsList.isEmpty()) {
				int lentsampleData = pcaResultsList.get(0).getSampleData().length;
				double[] sampleData = new double[lentsampleData];
				pcaResultsList.stream().map(s -> s.getSampleData()) //
						.forEach(d -> {
							for(int i = 0; i < lentsampleData; i++) {
								sampleData[i] += d[i];
							}
						});
				Arrays.stream(sampleData).forEach(d -> d = d / pcaResultsList.size());
				pcaResultGroup.setSampleData(sampleData);
				int lentEigenSpance = pcaResultsList.get(0).getEigenSpace().length;
				double[] eigenSpace = new double[lentEigenSpance];
				pcaResultsList.stream().map(r -> r.getEigenSpace())//
						.forEach(d -> {
							for(int i = 0; i < lentEigenSpance; i++) {
								eigenSpace[i] += d[i];
							}
						});
				Arrays.stream(eigenSpace).forEach(d -> d = d / pcaResultsList.size());
				pcaResultGroup.setEigenSpace(eigenSpace);
				double errorMemberShip = pcaResultsList.stream().mapToDouble(s -> s.getErrorMemberShip()).sum() / pcaResultsList.size();
				pcaResultGroup.setErrorMemberShip(errorMemberShip);
			} else {
				pcaResultGroup.setSampleData(null);
				pcaResultGroup.setEigenSpace(null);
				pcaResultGroup.setErrorMemberShip(Double.NaN);
			}
			pcaResults.getPcaResultGroupsList().add(pcaResultGroup);
		}
	}

	private void setRetentionTime(IPcaResults pcaResults, ISamples samples) {

		List<Integer> retTime = new ArrayList<>();
		for(int i = 0; i < samples.getExtractedRetentionTimes().size(); i++) {
			if(samples.getExtractedRetentionTimes().get(i).isSelected()) {
				retTime.add(new Integer(samples.getExtractedRetentionTimes().get(i).getRetentionTime()));
			}
		}
		pcaResults.setExtractedRetentionTimes(retTime);
	}
}
