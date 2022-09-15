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
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;

public class PcaUtils {

	/**
	 * 
	 * This method extract data from sample, it extracts only selected samples {@link ISample#isSelected()}
	 * 
	 * @param samples
	 * @return map where is key name of sample and value are sample data
	 */
	@SuppressWarnings("rawtypes")
	public static <V extends IVariable, S extends ISample> Map<String, double[]> extractData(ISamples<V, S> samples) {

		Map<String, double[]> selectedSamples = new HashMap<>();
		List<V> variables = samples.getVariables();
		int numSelected = (int)variables.stream().filter(r -> r.isSelected()).count();
		for(S sample : samples.getSampleList()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<? extends ISampleData> data = sample.getSampleData();
				selectedSampleData = new double[numSelected];
				int j = 0;
				for(int i = 0; i < data.size(); i++) {
					if(variables.get(i).isSelected()) {
						selectedSampleData[j] = data.get(i).getModifiedData();
						j++;
					}
				}
				selectedSamples.put(sample.getSampleName(), selectedSampleData);
			}
		}
		return selectedSamples;
	}

	/**
	 * This method create covariance matrix from sample, it is created only from selected samples {@link ISample#isSelected()}
	 * 
	 * @param samples
	 * @return covariance matrix
	 */
	public static <V extends IVariable, S extends ISample> RealMatrix getCovarianceMatrix(ISamples<V, S> samples) {

		Map<String, double[]> data = extractData(samples);
		double[][] array = new double[data.size()][];
		Iterator<double[]> it = data.values().iterator();
		int i = 0;
		while(it.hasNext()) {
			double[] ds = it.next();
			array[i] = ds;
			i++;
		}
		Covariance covariance = new Covariance(array);
		return covariance.getCovarianceMatrix();
	}

	/**
	 * 
	 * @param samples
	 * @return real part of covariance matrix return by {@link #getCovarianceMatrix(ISamples)}
	 */
	public static <V extends IVariable, S extends ISample> double[] getEigenValuesCovarianceMatrix(ISamples<V, S> samples) {

		RealMatrix covarianceMatrix = getCovarianceMatrix(samples);
		EigenDecomposition_F64<DMatrixRMaj> eigenDecomposition = DecompositionFactory_DDRM.eig(covarianceMatrix.getColumnDimension(), false, true);
		eigenDecomposition.decompose(new DMatrixRMaj(covarianceMatrix.getData()));
		double[] eigenvalues = new double[eigenDecomposition.getNumberOfEigenvalues()];
		for(int i = 0; i < eigenvalues.length; i++) {
			eigenvalues[i] = eigenDecomposition.getEigenvalue(i).real;
		}
		return eigenvalues;
	}

	/**
	 * 
	 * @param pcaResults
	 * @return all groupName {@link IResultPCA#getGroupName()}
	 */
	public static Set<String> getGroupNames(List<IResultPCA> pcaResults) {

		Set<String> groupNames = new HashSet<>();
		for(IResultPCA pcaResult : pcaResults) {
			String groupName = pcaResult.getSample().getGroupName();
			groupNames.add(groupName);
		}
		return groupNames;
	}

	/**
	 *
	 * @param sample
	 * @param onlySelected
	 * @return all group which list of samples contains, if some group name is null Set contains also null value
	 */
	public static <S extends ISample> Set<String> getGroupNames(List<S> samples, boolean onlySelected) {

		Set<String> groupNames = new HashSet<>();
		for(ISample sample : samples) {
			String groupName = sample.getGroupName();
			if(!onlySelected || sample.isSelected()) {
				groupNames.add(groupName);
			}
		}
		return groupNames;
	}

	/**
	 * 
	 * @param inputEntries
	 * @return
	 */
	public static Set<String> getGroupNamesFromEntry(List<IDataInputEntry> inputEntries) {

		Set<String> groupNames = new HashSet<>();
		for(IDataInputEntry inputEntry : inputEntries) {
			String groupName = inputEntry.getGroupName();
			groupNames.add(groupName);
		}
		return groupNames;
	}

	/**
	 *
	 * @param samples
	 * @return return sorted map, key is first occurrence group name in List, Value contains group name, Value can be null
	 */
	public static <S extends ISample> SortedMap<Integer, String> getIndexsFirstOccurrence(List<S> samples) {

		SortedMap<Integer, String> names = new TreeMap<>();
		for(int i = 0; i < samples.size(); i++) {
			String groupName = samples.get(i).getGroupName();
			if(!names.containsValue(groupName)) {
				names.putIfAbsent(i, groupName);
			}
		}
		return names;
	}

	/**
	 * 
	 * @param peaks
	 * @param leftRetentionTimeBound
	 * @param rightRetentionTimeBound
	 * @return all peaks which has peak maximum {@link IPeakModel#getPeakMaximum()} between {@code leftRetentionTimeBound} and {@code rightRetentionTimeBound}
	 */
	public static List<IPeak> getPeaks(IPeaks<?> peaks, int leftRetentionTimeBound, int rightRetentionTimeBound) {

		List<IPeak> peakInInterval = new ArrayList<>();
		for(IPeak peak : peaks.getPeaks()) {
			int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
			if(retentionTime >= leftRetentionTimeBound && retentionTime <= rightRetentionTimeBound) {
				peakInInterval.add(peak);
			}
		}
		return peakInInterval;
	}

	/**
	 * 
	 * @param samples
	 * @param onlySelected
	 * @return
	 */
	public static List<TreeSet<String>> getPeaksNames(List<Sample> samples, boolean onlySelected) {

		List<TreeSet<String>> map = new ArrayList<>();
		if(!samples.isEmpty()) {
			int lenght = samples.get(0).getSampleData().size();
			for(int i = 0; i < lenght; i++) {
				map.add(new TreeSet<>());
			}
			for(int j = 0; j < lenght; j++) {
				for(Sample sample : samples) {
					if(sample.isSelected() || !onlySelected) {
						Optional<IPeak> peak = sample.getSampleData().get(j).getPeak();
						if(peak.isPresent()) {
							List<IIdentificationTarget> target = new ArrayList<>(peak.get().getTargets());
							if(target.size() > 0) {
								map.get(j).add(target.get(0).getLibraryInformation().getName());
							}
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 
	 * @param samples
	 * @param containsNullGroupName
	 * @param onlySelected
	 * @return
	 */
	public static <S extends ISample> Map<String, Set<S>> getSamplesByGroupName(List<S> samples, boolean containsNullGroupName, boolean onlySelected) {

		Map<String, Set<S>> samplesByGroupName = new HashMap<>();
		Set<String> groupNames = getGroupNames(samples, onlySelected);
		for(String groupName : groupNames) {
			if(groupName != null || containsNullGroupName) {
				Set<S> samplesIdenticalGroupName = samples.stream().filter(s -> (Comparator.nullsFirst(String::compareTo).compare(groupName, s.getGroupName()) == 0) && s.isSelected()).collect(Collectors.toSet());
				if(!samplesIdenticalGroupName.isEmpty()) {
					samplesByGroupName.put(groupName, samplesIdenticalGroupName);
				}
			}
		}
		return samplesByGroupName;
	}

	/**
	 * 
	 * @param pcaResults
	 */
	public static void sortPcaResultsByGroup(List<IResultPCA> pcaResults) {

		Comparator<IResultPCA> comparator = (arg0, arg1) -> {
			String name0 = arg0.getSample().getGroupName();
			String name1 = arg1.getSample().getGroupName();
			if(name0 == null && name1 == null) {
				return 0;
			}
			if(name0 != null && name1 == null) {
				return 1;
			}
			if(name0 == null && name1 != null) {
				return -1;
			}
			return name0.compareTo(name1);
		};
		Collections.sort(pcaResults, comparator);
	}

	/**
	 * 
	 * @param samples
	 */
	public static void sortPcaResultsByName(List<IResultPCA> samples) {

		Comparator<IResultPCA> comparator = (arg0, arg1) -> {
			return arg0.getSample().getSampleName().compareTo(arg1.getSample().getSampleName());
		};
		Collections.sort(samples, comparator);
	}

	/**
	 * 
	 * @param pcaResults
	 * @param inverse
	 */
	public static void sortPcaResultsListByErrorMemberShip(List<IResultPCA> pcaResults, boolean inverse) {

		int i = 1;
		if(inverse) {
			i = -1;
		}
		final int inv = i;
		Comparator<IResultPCA> comparator = (arg0, arg1) -> {
			return inv * Double.compare(arg0.getErrorMemberShip(), arg1.getErrorMemberShip());
		};
		Collections.sort(pcaResults, comparator);
	}

	/**
	 * sort list by group name
	 *
	 * @param samples
	 */
	public static <S extends ISample> void sortSampleListByGroup(List<S> samples) {

		Comparator<ISample> comparator = (arg0, arg1) -> {
			String name0 = arg0.getGroupName();
			String name1 = arg1.getGroupName();
			if(name0 == null && name1 == null) {
				return 0;
			}
			if(name0 != null && name1 == null) {
				return 1;
			}
			if(name0 == null && name1 != null) {
				return -1;
			}
			return name0.compareTo(name1);
		};
		Collections.sort(samples, comparator);
	}

	/**
	 * sort list by name {@link ISample#getName()}
	 * 
	 * @param samples
	 */
	public static <S extends ISample> void sortSampleListByName(List<S> samples) {

		Comparator<ISample> comparator = (arg0, arg1) -> {
			return arg0.getSampleName().compareTo(arg1.getSampleName());
		};
		Collections.sort(samples, comparator);
	}
}
