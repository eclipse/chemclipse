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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Group;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;

public class PcaUtils {

	/**
	 *
	 * @param samples
	 * @return all group which list of samples contains, if some group name is null Set can contains also null value
	 */
	public static Set<String> getGroupNames(List<ISample> samples) {

		Set<String> groupNames = new HashSet<>();
		for(ISample sample : samples) {
			String groupName = sample.getGroupName();
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

	public static Map<Integer, List<IPeak>> getPeaksAtInterval(IPcaResults pcaResults) {

		List<ISample> samples = pcaResults.getSampleList();
		List<Integer> retentionTimes = pcaResults.getExtractedRetentionTimes();
		final Map<Integer, List<IPeak>> result = new HashMap<>();
		for(Integer retentionTime : retentionTimes) {
			result.put(retentionTime, new ArrayList<>());
		}
		for(ISample sample : samples) {
			Map<Integer, List<IPeak>> peaks = getPeaksAtIntervals(retentionTimes, sample);
			Iterator<Integer> iter = peaks.keySet().iterator();
			while(iter.hasNext()) {
				Integer key = iter.next();
				result.get(key).addAll(peaks.get(key));
			}
		}
		return result;
	}

	public static List<IPeak> getPeaksAtInterval(IPeaks peaks, int leftRetentionTimeBound, int rightRetentionTimeBound) {

		List<IPeak> peakInInterval = new ArrayList<>();
		for(IPeak peak : peaks.getPeaks()) {
			int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
			if(retentionTime >= leftRetentionTimeBound && retentionTime <= rightRetentionTimeBound) {
				peakInInterval.add(peak);
			}
		}
		return peakInInterval;
	}

	public static Map<Integer, List<IPeak>> getPeaksAtIntervals(List<Integer> retentionTime, ISample sample) {

		Map<Integer, List<IPeak>> peaksAtInterval = new HashMap<>();
		IPeaks peaks = sample.getPcaResult().getPeaks();
		if(peaks != null) {
			int leftRetentionTimeBound = 0;
			for(int i = 0; i < retentionTime.size(); i++) {
				List<IPeak> peakAtInterval = getPeaksAtInterval(peaks, leftRetentionTimeBound, retentionTime.get(i));
				peaksAtInterval.put(retentionTime.get(i), peakAtInterval);
				leftRetentionTimeBound = retentionTime.get(i);
			}
		}
		return peaksAtInterval;
	}

	public static List<ISample> insertGroup(List<ISample> samples) {

		List<ISample> newSamples = new ArrayList<>(samples);
		Set<String> groupNames = getGroupNames(newSamples);
		for(Iterator<String> iterator = groupNames.iterator(); iterator.hasNext();) {
			String groupName = iterator.next();
			if(groupName != null) {
				Group group = new Group("mean");
				group.setGroupName(groupName);
				group.setPcaResult(newSamples);
				newSamples.add(0, group);
			}
		}
		return newSamples;
	}

	public static void sortSampleListByErrorMemberShip(List<ISample> samples) {

		Comparator<ISample> comparator = (arg0, arg1) -> {
			return Double.compare(arg0.getPcaResult().getErrorMemberShip(), arg1.getPcaResult().getErrorMemberShip());
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

	public static void sortSampleListByName(List<ISample> samples) {

		Comparator<ISample> comparator = (arg0, arg1) -> {
			return arg0.getName().compareTo(arg1.getName());
		};
		Collections.sort(samples, comparator);
	}
}
