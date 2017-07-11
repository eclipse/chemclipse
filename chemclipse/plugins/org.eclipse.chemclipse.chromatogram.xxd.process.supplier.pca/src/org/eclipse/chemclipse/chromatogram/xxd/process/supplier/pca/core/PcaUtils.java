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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Group;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.targets.IPeakTarget;

public class PcaUtils {

	public static List<IGroup> createGroup(List<ISample> samples) {

		List<ISample> newSamples = new ArrayList<>(samples);
		List<IGroup> groups = new ArrayList<>();
		Set<String> groupNames = getGroupNames(newSamples);
		for(Iterator<String> iterator = groupNames.iterator(); iterator.hasNext();) {
			String groupName = iterator.next();
			if(groupName != null) {
				Group group = new Group();
				group.setGroupName(groupName);
				group.setPcaResult(newSamples);
				groups.add(group);
			}
		}
		return groups;
	}

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

	public static List<IPeak> getPeaks(ISample sample, int leftRetentionTimeBound, int rightRetentionTimeBound) {

		IPeaks peaks = sample.getPcaResult().getPeaks();
		if(peaks != null) {
			return getPeaks(peaks, leftRetentionTimeBound, rightRetentionTimeBound);
		}
		return null;
	}

	public static List<TreeSet<String>> getPeaksNames(List<Integer> retentionTime, List<ISample> samples) {

		List<TreeSet<String>> map = new ArrayList<>(retentionTime.size());
		for(int i = 0; i < retentionTime.size(); i++) {
			map.add(new TreeSet<>());
		}
		int leftRetentionTimeBound = 0;
		int rightRetentionTimeBound = 0;
		for(int j = 0; j < retentionTime.size(); j++) {
			rightRetentionTimeBound = retentionTime.get(j);
			for(ISample sample : samples) {
				List<IPeak> peakList = getPeaks(sample, leftRetentionTimeBound, rightRetentionTimeBound);
				for(IPeak peak : peakList) {
					List<IPeakTarget> target = peak.getTargets();
					if(!target.isEmpty()) {
						map.get(j).add(target.get(0).getLibraryInformation().getName());
					}
				}
			}
			leftRetentionTimeBound = rightRetentionTimeBound;
		}
		return map;
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

	public static void sortSampleListByName(List<ISample> samples) {

		Comparator<ISample> comparator = (arg0, arg1) -> {
			return arg0.getName().compareTo(arg1.getName());
		};
		Collections.sort(samples, comparator);
	}
}
