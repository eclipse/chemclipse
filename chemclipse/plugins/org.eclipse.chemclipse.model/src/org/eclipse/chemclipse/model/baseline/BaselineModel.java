/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.baseline;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.BaselineIsNotDefinedException;

/**
 * This class represents the baseline model of the current chromatogram.
 * 
 * @author eselmeister
 */
public class BaselineModel implements IBaselineModel {

	@SuppressWarnings("rawtypes")
	private transient IChromatogram chromatogram;
	/*
	 * The start retention time is the key.
	 */
	private NavigableMap<Integer, IBaselineSegment> baselineSegments;
	private float defaultBackgroundAbundance;

	@SuppressWarnings("rawtypes")
	@Deprecated
	public BaselineModel(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
		this.baselineSegments = new TreeMap<Integer, IBaselineSegment>();
		this.defaultBackgroundAbundance = 0f;
	}

	@SuppressWarnings("rawtypes")
	public BaselineModel(IChromatogram chromatogram, float defaultBackgroundAbundance) {

		this.chromatogram = chromatogram;
		this.baselineSegments = new TreeMap<Integer, IBaselineSegment>();
		this.defaultBackgroundAbundance = 0f;
		this.defaultBackgroundAbundance = defaultBackgroundAbundance;
	}

	// --------------------------------------------IBaselineModel
	@Override
	public void addBaseline(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance, boolean validate) {

		if(validate) {
			addBaselineChecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		} else {
			addBaselineUnchecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		}
	}

	private void removeBaseline(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		removeMiddleSegments(startRetentionTime, stopRetentionTime);
		/*
		 * part which should be remove lie in interval one segment
		 */
		cutSegmentInTwoParts(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		/*
		 * 
		 */
		cutSegmentBeginningPart(startRetentionTime, stopRetentionTime, stopBackgroundAbundance);
		/*
		 * 
		 */
		cutSegmentEndingPart(startRetentionTime, stopRetentionTime, startBackgroundAbundance);
	}

	/**
	 * remove middle segments
	 * 
	 */
	private void removeMiddleSegments(int startRetentionTime, int stopRetentionTime) {

		SortedMap<Integer, IBaselineSegment> sortedMap = baselineSegments.subMap(startRetentionTime, stopRetentionTime);
		Set<Integer> keyToRemove = new HashSet<>();
		//
		for(Entry<Integer, IBaselineSegment> entry : sortedMap.entrySet()) {
			int stopRetentionTimeRemoveSegment = entry.getValue().getStopRetentionTime();
			if(stopRetentionTimeRemoveSegment <= stopRetentionTime) {
				keyToRemove.add(entry.getKey());
			}
		}
		/**
		 * remove key
		 */
		keyToRemove.forEach(k -> baselineSegments.remove(k));
	}

	/**
	 * Cut the beginning part of an existing segment.
	 * 
	 */
	private void cutSegmentBeginningPart(int startRetentionTime, int stopRetentionTime, float stopBackgroundAbundance) {

		Map.Entry<Integer, IBaselineSegment> cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		cuttingSegmentEntry = baselineSegments.floorEntry(stopRetentionTime);
		if(cuttingSegmentEntry != null) {
			IBaselineSegment cuttingSegment = cuttingSegmentEntry.getValue();
			int x0 = cuttingSegment.getStartRetentionTime();
			int x1 = cuttingSegment.getStopRetentionTime();
			if(startRetentionTime <= x0 && x0 <= stopRetentionTime && stopRetentionTime < x1) {
				baselineSegments.remove(cuttingSegmentEntry.getKey());
				int partSegmentStartRetentionTime = startRetentionTime + 1;
				int partSegmentStopRetentionTime = x1;
				float partSegmentStartAbundance = cuttingSegment.getBackgroundAbundance(partSegmentStartRetentionTime);
				float partSegmentStopAbundance = cuttingSegment.getStopBackgroundAbundance();
				addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, partSegmentStartAbundance, partSegmentStopAbundance);
				/*
				 * 
				 */
				if(partSegmentStartRetentionTime != partSegmentStopRetentionTime) {
					addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, partSegmentStartAbundance, partSegmentStopAbundance);
				} else if(!Float.isNaN(stopBackgroundAbundance)) {
					partSegmentStartRetentionTime -= 1;
					addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, stopBackgroundAbundance, partSegmentStopAbundance);
				}
			}
		}
	}

	/**
	 * Cut the ending part of an existing segment.
	 * 
	 */
	private void cutSegmentEndingPart(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance) {

		Map.Entry<Integer, IBaselineSegment> cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		if(cuttingSegmentEntry != null) {
			IBaselineSegment cuttingSegment = cuttingSegmentEntry.getValue();
			int x0 = cuttingSegment.getStartRetentionTime();
			int x1 = cuttingSegment.getStopRetentionTime();
			if(x0 < startRetentionTime && startRetentionTime <= x1 && x1 <= stopRetentionTime) {
				baselineSegments.remove(cuttingSegmentEntry.getKey());
				int partSegmentStartRetentionTime = x0;
				int partSegmentStopRetentionTime = startRetentionTime - 1;
				float partSegmentStartAbundance = cuttingSegment.getStartRetentionTime();
				float partSegmentStopAbundance = cuttingSegment.getBackgroundAbundance(partSegmentStopRetentionTime);
				/*
				 * 
				 */
				if(partSegmentStartRetentionTime != partSegmentStopRetentionTime) {
					addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, partSegmentStartAbundance, partSegmentStopAbundance);
				} else if(!Float.isNaN(startBackgroundAbundance)) {
					partSegmentStopRetentionTime += 1;
					addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, partSegmentStartAbundance, startBackgroundAbundance);
				}
			}
		}
	}

	/**
	 * Cut an existing segment into two peaces.
	 */
	private void cutSegmentInTwoParts(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		Map.Entry<Integer, IBaselineSegment> cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		if(cuttingSegmentEntry != null) {
			IBaselineSegment cuttingSegment = cuttingSegmentEntry.getValue();
			int x0 = cuttingSegment.getStartRetentionTime();
			int x1 = cuttingSegment.getStopRetentionTime();
			if(x0 < startRetentionTime && stopRetentionTime < x1) {
				baselineSegments.remove(cuttingSegmentEntry.getKey());
				/*
				 * add first segment
				 */
				int firstPartSegmentStartRetentionTime = x0;
				int firstPartSegmentStopRetentionTime = startRetentionTime - 1;
				float firstPartSegmentStartAbundance = cuttingSegment.getStartRetentionTime();
				float firstPartSegmentStopAbundance = cuttingSegment.getBackgroundAbundance(firstPartSegmentStopRetentionTime);
				/*
				 * 
				 */
				if(firstPartSegmentStartRetentionTime != firstPartSegmentStopRetentionTime) {
					addBaselineUnchecked(firstPartSegmentStartRetentionTime, firstPartSegmentStopRetentionTime, firstPartSegmentStartAbundance, firstPartSegmentStopAbundance);
				} else if(!Float.isNaN(startBackgroundAbundance)) {
					firstPartSegmentStopRetentionTime += 1;
					addBaselineUnchecked(firstPartSegmentStartRetentionTime, firstPartSegmentStopRetentionTime, firstPartSegmentStartAbundance, startBackgroundAbundance);
				}
				/*
				 * add second segment
				 */
				int secondPartSegmentStartRetentionTime = stopRetentionTime + 1;
				int secondPartSegmentStopRetentionTime = x1;
				float secondPartSegmentStartAbundance = cuttingSegment.getBackgroundAbundance(secondPartSegmentStartRetentionTime);
				float secondPartSegmentStopAbundance = cuttingSegment.getStopBackgroundAbundance();
				/*
				 * 
				 */
				if(firstPartSegmentStartRetentionTime != firstPartSegmentStopRetentionTime) {
					addBaselineUnchecked(secondPartSegmentStartRetentionTime, secondPartSegmentStopRetentionTime, secondPartSegmentStartAbundance, secondPartSegmentStopAbundance);
				} else if(!Float.isNaN(stopBackgroundAbundance)) {
					secondPartSegmentStartRetentionTime = stopRetentionTime - 1;
					addBaselineUnchecked(secondPartSegmentStartRetentionTime, secondPartSegmentStopRetentionTime, stopBackgroundAbundance, secondPartSegmentStopAbundance);
				}
			}
		}
	}

	@Override
	public void removeBaseline() {

		clearBaseline();
	}

	@Override
	@Deprecated
	public float getBackgroundAbundance(int retentionTime) {

		if(retentionTime < chromatogram.getStartRetentionTime() || retentionTime > chromatogram.getStopRetentionTime()) {
			return 0.0f;
		}
		/*
		 * Get the correct baseline segment and calculate the abundance.
		 */
		Map.Entry<Integer, IBaselineSegment> entry = baselineSegments.floorEntry(retentionTime);
		if(entry == null) {
			return 0.0f;
		} else {
			IBaselineSegment segment = entry.getValue();
			return segment.getBackgroundAbundance(retentionTime);
		}
	}

	@Override
	public float getBackground(int retentionTime) {

		return getBackground(retentionTime, defaultBackgroundAbundance);
	}

	private float getBackground(int retentionTime, float defaultAbudance) {

		if(baselineSegments.isEmpty() || retentionTime < baselineSegments.firstKey() || retentionTime > baselineSegments.lastEntry().getValue().getStopRetentionTime()) {
			return defaultAbudance;
		}
		/*
		 * Get the correct baseline segment and calculate the abundance.
		 */
		IBaselineSegment floorSegment = baselineSegments.floorEntry(retentionTime).getValue();
		int stopRetentionTime = floorSegment.getStopRetentionTime();
		if(retentionTime <= stopRetentionTime) {
			return floorSegment.getBackgroundAbundance(retentionTime);
		} else {
			return defaultAbudance;
		}
	}

	@Override
	public float getBackgroundNotNaN(int retentionTime) throws BaselineIsNotDefinedException {

		float background = getBackground(retentionTime);
		if(background != Float.NaN) {
			return background;
		} else {
			throw new BaselineIsNotDefinedException();
		}
	}

	@Override
	public IBaselineModel makeDeepCopy() {

		IBaselineModel baselineModelCopy = new BaselineModel(chromatogram);
		int startRT;
		int stopRT;
		float startAB;
		float stopAB;
		for(IBaselineSegment segment : baselineSegments.values()) {
			startRT = segment.getStartRetentionTime();
			stopRT = segment.getStopRetentionTime();
			startAB = segment.getStartBackgroundAbundance();
			stopAB = segment.getStopBackgroundAbundance();
			baselineModelCopy.addBaseline(startRT, stopRT, startAB, stopAB, true);
		}
		return baselineModelCopy;
	}

	// --------------------------------------------IBaselineModel
	// ------------------------------------------private methods
	private void addBaselineUnchecked(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		IBaselineSegment segment = new BaselineSegment(startRetentionTime, stopRetentionTime);
		segment.setStartBackgroundAbundance(startBackgroundAbundance);
		segment.setStopBackgroundAbundance(stopBackgroundAbundance);
		baselineSegments.put(segment.getStartRetentionTime(), segment);
	}

	private void addBaselineChecked(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		if(startRetentionTime >= stopRetentionTime) {
			return;
		}
		removeBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		addBaselineUnchecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
	}

	/**
	 * Clear the baseline segments and create a new tree map to store them.
	 */
	private void clearBaseline() {

		if(baselineSegments != null) {
			baselineSegments.clear();
		}
	}
	// ------------------------------------------private methods

	@Override
	public void removeBaseline(int startRetentionTime, int stopRetentionTime) {

		removeBaseline(startRetentionTime, stopRetentionTime, Float.NaN, Float.NaN);
	}
}
