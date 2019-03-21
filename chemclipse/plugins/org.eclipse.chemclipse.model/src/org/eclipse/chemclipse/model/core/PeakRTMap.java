/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

/**
 * A specialized Map-Like-Datastructure that retains peaks indexed by there maximum signal retention time,
 * 
 * @author Christoph Läubrich
 *
 * @param <T>
 */
public final class PeakRTMap<T extends IPeak> implements Serializable, IChromatogramPeaks<T> {

	private static final long serialVersionUID = 6339698016420166069L;
	private int peakcount;
	private TreeMap<Integer, Collection<T>> internalMap = new TreeMap<>();

	@Override
	public void addPeak(T peak) {

		Integer rt = getKey(peak);
		Collection<T> list = internalMap.get(rt);
		if(list == null) {
			// this will ensure that peaks, that are equal not added twice
			// but different peaks remain in the order of insertion
			list = new LinkedHashSet<>();
			internalMap.put(rt, list);
		}
		if(list.add(peak)) {
			peakcount++;
		}
	}

	@Override
	public void removePeak(T peakToRemove) {

		Integer rt = getKey(peakToRemove);
		Collection<T> list = internalMap.get(rt);
		if(list != null) {
			for(Iterator<T> iterator = list.iterator(); iterator.hasNext();) {
				T other = iterator.next();
				if(other.equals(peakToRemove)) {
					iterator.remove();
					peakcount--;
					break;
				}
			}
			if(list.isEmpty()) {
				// allow for garbage collection....
				internalMap.remove(rt);
			}
		}
	}

	/**
	 * 
	 * @return a copy of all peaks in this map
	 */
	@Override
	public List<T> getPeaks() {

		return collectPeaks(internalMap.values());
	}

	/**
	 * returns all peaks that are inside the given retention time, that means the retention time is within the start/stop retention time of the peak
	 * 
	 * @param retentionTime
	 * @return a list of peaks at the given retention time, ordered by the start retention time of the peak
	 */
	@Override
	public List<T> getPeaks(int startRetentionTime, int stopRetentionTime) {

		return collectPeaks(internalMap.subMap(startRetentionTime, true, stopRetentionTime, true).values());
	}

	/**
	 * Collect all peaks from a collection of lists into one list and sort them according to the {@link IPeak#COMPARATOR_RT_MAX}
	 * 
	 * @param values
	 *            the values to merge
	 * @return the collected peaks
	 */
	private static <T extends IPeak> List<T> collectPeaks(Collection<Collection<T>> values) {

		ArrayList<T> list = new ArrayList<>();
		for(Collection<T> value : values) {
			list.addAll(value);
		}
		Collections.sort(list, IPeak.COMPARATOR_RT_MAX);
		return list;
	}

	private static Integer getKey(IPeak peak) {

		return peak.getPeakModel().getRetentionTimeAtPeakMaximum();
	}

	@Override
	public void removeAllPeaks() {

		internalMap.clear();
	}

	@Override
	public int getNumberOfPeaks() {

		return peakcount;
	}

	@Override
	public void removePeaks(List<T> peaksToDelete) {

		// because of the datastructure we can't use removeAll, but removing one peak at a time is quite efficient anyways
		for(T peak : peaksToDelete) {
			removePeak(peak);
		}
	}

	@Override
	public List<T> getPeaks(@SuppressWarnings("rawtypes") IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			return getPeaks(chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
		} else {
			return getPeaks();
		}
	}
}