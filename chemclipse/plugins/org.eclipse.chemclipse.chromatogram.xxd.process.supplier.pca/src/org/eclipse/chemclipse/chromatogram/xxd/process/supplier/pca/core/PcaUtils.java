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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Group;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;

public class PcaUtils {

	/**
	 *
	 * @param samples
	 * @return all group which list of samples contains, if some group name is null Set contains also null value
	 */
	public static Set<String> getGroupNames(List<ISample> samples) {

		return getGroupNames(samples, false);
	}

	/**
	 *
	 * @param sample
	 * @param onlySelected
	 * @return all group which list of samples contains, if some group name is null Set contains also null value
	 */
	public static Set<String> getGroupNames(List<ISample> samples, boolean onlySelected) {

		Set<String> groupNames = new HashSet<>();
		for(ISample sample : samples) {
			String groupName = sample.getGroupName();
			if(!onlySelected || sample.isSelected()) {
				groupNames.add(groupName);
			}
		}
		return groupNames;
	}

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
	public static SortedMap<Integer, String> getIndexsFirstOccurrence(List<ISample> samples) {

		SortedMap<Integer, String> names = new TreeMap<>();
		for(int i = 0; i < samples.size(); i++) {
			String groupName = samples.get(i).getGroupName();
			if(!names.containsValue(groupName)) {
				names.putIfAbsent(i, groupName);
			}
		}
		return names;
	}

	public static int getNumberOfGroupNames(List<ISample> samples) {

		return getGroupNames(samples).size();
	}

	public static List<IPeak> getPeaks(IPeaks peaks, int leftRetentionTimeBound, int rightRetentionTimeBound) {

		List<IPeak> peakInInterval = new ArrayList<>();
		for(IPeak peak : peaks.getPeaks()) {
			int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
			if(retentionTime >= leftRetentionTimeBound && retentionTime <= rightRetentionTimeBound) {
				peakInInterval.add(peak);
			}
		}
		return peakInInterval;
	}

	public static List<TreeSet<String>> getPeaksNames(List<ISample> samples) {

		List<TreeSet<String>> map = new ArrayList<>();
		if(!samples.isEmpty()) {
			int lenght = samples.get(0).getSampleData().size();
			for(int i = 0; i < lenght; i++) {
				map.add(new TreeSet<>());
			}
			for(int j = 0; j < lenght; j++) {
				for(ISample sample : samples) {
					Set<IPeak> peakList = sample.getSampleData().get(j).getPeaks();
					if(peakList != null) {
						for(IPeak peak : peakList) {
							List<IPeakTarget> target = peak.getTargets();
							if(!target.isEmpty()) {
								map.get(j).add(target.get(0).getLibraryInformation().getName());
							}
						}
					}
				}
			}
		}
		return map;
	}

	public static Map<String, Set<ISample>> getSamplesByGroupName(List<ISample> samples, boolean containsNullGroupName, boolean onlySelected) {

		Map<String, Set<ISample>> samplesByGroupName = new HashMap<>();
		Set<String> groupNames = getGroupNames(samples, onlySelected);
		for(String groupName : groupNames) {
			if(groupName != null || containsNullGroupName) {
				Set<ISample> samplesIdenticalGroupName = samples.stream().filter(s -> (Comparator.nullsFirst(String::compareTo).compare(groupName, s.getGroupName()) == 0) && s.isSelected()).collect(Collectors.toSet());
				if(!samplesIdenticalGroupName.isEmpty()) {
					samplesByGroupName.put(groupName, samplesIdenticalGroupName);
				}
			}
		}
		return samplesByGroupName;
	}

	public static void setGroups(IPcaResults pcaResults, boolean onlySelected) {

		Set<String> groupNames = PcaUtils.getGroupNames(pcaResults.getSampleList(), onlySelected);
		pcaResults.getGroupList().clear();
		groupNames.forEach(groupName -> {
			if(groupName != null) {
				List<ISample> samplesSomeGroupName = pcaResults.getSampleList().stream().filter(s -> groupName.equals(s.getGroupName())).collect(Collectors.toList());
				IGroup group = new Group(samplesSomeGroupName);
				group.setGroupName(groupName);
				pcaResults.getGroupList().add(group);
			}
		});
	}

	public static void sortSampleListByErrorMemberShip(List<ISample> samples, boolean inverse) {

		int i = 1;
		if(inverse) {
			i = -1;
		}
		final int inv = i;
		Comparator<ISample> comparator = (arg0, arg1) -> {
			return inv * Double.compare(arg0.getPcaResult().getErrorMemberShip(), arg1.getPcaResult().getErrorMemberShip());
		};
		Collections.sort(samples, comparator);
	}

	/**
	 * sort list by group name, instance of SampleGroupMean will be sorted before other object in case of identical group name
	 *
	 * @param samples
	 */
	public static void sortSampleListByGroup(List<ISample> samples) {

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
			if(name0.equals(name1)) {
				if(arg0 instanceof Group) {
					return -1;
				}
				if(arg1 instanceof Group) {
					return 1;
				}
			}
			return name0.compareTo(name1);
		};
		Collections.sort(samples, comparator);
	}

	public static RealMatrix getCovarianceMatrix(IPcaResults pcaResults) {

		Map<String, double[]> data = extractData(pcaResults);
		double[][] array = new double[data.size()][];
		Iterator<double[]> it = data.values().iterator();
		int i = 0;
		while(it.hasNext()) {
			double[] ds = (double[])it.next();
			array[i] = ds;
			i++;
		}
		Covariance covariance = new Covariance(array);
		return covariance.getCovarianceMatrix();
	}

	public static double[] getEigenValuesCovarianceMatrix(IPcaResults pcaResults) {

		RealMatrix covarianceMatrix = getCovarianceMatrix(pcaResults);
		EigenDecomposition<DenseMatrix64F> eigenDecomposition = DecompositionFactory.eig(covarianceMatrix.getColumnDimension(), false, true);
		eigenDecomposition.decompose(new DenseMatrix64F(covarianceMatrix.getData()));
		double[] eigenvalues = new double[eigenDecomposition.getNumberOfEigenvalues()];
		for(int i = 0; i < eigenvalues.length; i++) {
			eigenvalues[i] = eigenDecomposition.getEigenvalue(i).real;
		}
		return eigenvalues;
	}

	public static Map<String, double[]> extractData(IPcaResults pcaResults) {

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

	public static void sortSampleListByName(List<ISample> samples) {

		Comparator<ISample> comparator = (arg0, arg1) -> {
			return arg0.getName().compareTo(arg1.getName());
		};
		Collections.sort(samples, comparator);
	}
}
