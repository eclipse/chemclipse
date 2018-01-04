/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.internal.massspectrum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class ComparatorCache {

	private static final int NUMBER_TOP_IONS = 12;
	private static Map<Integer, Set<Integer>> unknownTopIons = null;
	private static Map<Integer, Set<Integer>> referenceTopIons = null;
	//
	private IonAbundanceComparator ionAbundanceComparator;

	public ComparatorCache() {
		/*
		 * Initialize the static maps once.
		 */
		ionAbundanceComparator = new IonAbundanceComparator(SortOrder.DESC);
		initializeDatabaseMaps();
	}

	/**
	 * If sorted is false, the ion list will be sorted descending - m/z values with highest abundance first.
	 * 
	 * @param ions
	 * @param referenceIons
	 * @return boolean
	 */
	public boolean useReferenceForComparison(IScanMSD unknown, IScanMSD reference, double thresholdPreOptimization) {

		if(unknown == null || reference == null) {
			return false;
		}
		/*
		 * Warm up the cache.
		 */
		int keyUnknown = unknown.getIons().hashCode();
		if(!unknownTopIons.containsKey(keyUnknown)) {
			unknownTopIons.put(keyUnknown, extractTopIons(unknown));
		}
		/*
		 * It is assumed that the reference is not modified.
		 */
		int keyReference = unknown.getIons().hashCode();
		if(!referenceTopIons.containsKey(reference)) {
			referenceTopIons.put(keyReference, extractTopIons(reference));
		}
		/*
		 * Make the comparison.
		 */
		Set<Integer> unknownIons = unknownTopIons.get(keyUnknown);
		Set<Integer> referenceIons = referenceTopIons.get(keyReference);
		return useReferenceForComparison(unknownIons, referenceIons, thresholdPreOptimization);
	}

	/**
	 * The ion list must be sorted descending - m/z values with highest abundance first.
	 * 
	 * @param ions
	 * @param referenceIons
	 * @return
	 */
	private boolean useReferenceForComparison(Set<Integer> unknownIons, Set<Integer> referenceIons, double thresholdPreOptimization) {

		int hits = 0;
		int size = unknownIons.size();
		if(size > 0) {
			Iterator<Integer> iterator = unknownIons.iterator();
			while(iterator.hasNext()) {
				int mz = iterator.next();
				if(referenceIons.contains(mz)) {
					hits++;
				}
			}
			//
			double percentageHits = hits / (double)referenceIons.size();
			if(percentageHits >= thresholdPreOptimization) {
				return true;
			}
		}
		//
		return false;
	}

	private Set<Integer> extractTopIons(IScanMSD massSpectrum) {

		/*
		 * Extract the list of n top ions.
		 */
		List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
		Collections.sort(ions, ionAbundanceComparator);
		Set<Integer> topIons = new HashSet<Integer>();
		int size = (ions.size() < NUMBER_TOP_IONS) ? ions.size() : NUMBER_TOP_IONS;
		for(int i = 0; i < size; i++) {
			topIons.add(AbstractIon.getIon(ions.get(i).getIon()));
		}
		//
		return topIons;
	}

	private void initializeDatabaseMaps() {

		if(unknownTopIons == null) {
			unknownTopIons = new HashMap<Integer, Set<Integer>>();
		}
		if(referenceTopIons == null) {
			referenceTopIons = new HashMap<Integer, Set<Integer>>();
		}
	}
}
